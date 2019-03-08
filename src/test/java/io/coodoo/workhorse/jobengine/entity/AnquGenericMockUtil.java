package io.coodoo.workhorse.jobengine.entity;


import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.*;

import javax.persistence.*;

/**
 * This class mocks calls needed to execute a given named query. It is based on
 * a Mockito mock of the EntityManager. Just call a static method of this class
 * providing your EntityManager mock and the mock will be programmed to return
 * the given results in that order.
 * 
 * This class is multi-parted:
 * <ul>
 * <li>A half-generic untyped part for mocking calls for queries with the name
 * provided as parameter.</li>
 * <li>A generic untyped part which will return the results in the given order
 * not looking at query names at all. This should not be mixed with the other
 * parts.</li>
 * <li>Utility methods to create lists from a given number of objects.</li>
 * </ul>
 * 
 * The class does not distinguish mocking getResultList() from mocking
 * getSingleResult() or from executeUpdate(). The given return object is
 * converted depending on the format needed to answer the call. See the method
 * documentation for further information.
 * <p/>
 * Queries and <strong>TypedQueries</strong> are automatically treated in the
 * same way. Only in the specialized part the result type is relevant. In the
 * (half-)generic part it is ignored by a corresponding matcher.
 * 
 * <p/>
 * <strong>Note:</strong> This class is not suitable to verify calls the the
 * EntityManager mock.
 * <p/>
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class AnquGenericMockUtil {

	/**
	 * Mocks exactly one call to the query with the given name. The given result
	 * list will be interpreted depending on the kind of call:
	 * 
	 * <ul>
	 * <li><strong>getResultList():</strong> If the object is a list, it will be
	 * returned. Otherwise the object will be packed into a list and returned.
	 * <li><strong>getSingleResult():</strong> If the object is a list, the
	 * first element of that list will be returned. Otherwise the object will be
	 * returned.</li>
	 * <li><strong>executeUpdate():</strong> The object is cast to an int and
	 * returned. If the object is a list, the first element is taken for the
	 * cast.</li>
	 * <li><strong>Any kind:</strong> If the object is a RuntimeException or a
	 * list with a RuntimeException as first element, this exception is thrown.</li>
	 * </ul>
	 * 
	 * @param entityManagerMock the mock of the EntityManager to program
	 * @param queryName name of the query
	 * @param first first result returned
	 * @param more other results returned for subsequent calls, one at a time
	 * @param result result object evaluated/returned
	 */
	public static void mockAnyActionForQuery(EntityManager entityManagerMock, String queryName, Object first,
			Object... more)
	{
		AnquDummyQuery firstDummy = new AnquDummyQuery(first instanceof List ? (List<?>)first : genList(first));
		AnquDummyQuery[] moreDummies = new AnquDummyQuery[more.length];
		for (int i = 0; i < more.length; i++)
		{
			moreDummies[i] = new AnquDummyQuery(more[i] instanceof List ? (List<?>)more[i] : genList(more[i]));
		}
		given(entityManagerMock.createNamedQuery(queryName)).willReturn(firstDummy, moreDummies);
		given(entityManagerMock.createNamedQuery(eq(queryName), (Class<?>)any(Class.class))).willReturn(firstDummy, moreDummies);
	}

	/**
	 * Convenience method to mock a series of query calls ignoring the query
	 * name. The behaviour depends on the kind of query execution and the object
	 * defined to answer the query:
	 * 
	 * <ul>
	 * <li><strong>getResultList():</strong> If the object is a list, it will be
	 * returned. Otherwise the object will be packed into a list and returned.
	 * <li><strong>getSingleResult():</strong> If the object is a list, the
	 * first element of that list will be returned. Otherwise the object will be
	 * returned.</li>
	 * <li><strong>executeUpdate():</strong> The object is cast to an int and
	 * returned. If the object is a list, the first element is taken for the
	 * cast.</li>
	 * <li><strong>Any kind:</strong> If the object is a RuntimeException or a
	 * list with a RuntimeException as first element, this exception is thrown.</li>
	 * </ul>
	 * 
	 * <p/>
	 * <strong>Warning:</strong> The name of the query is irrelevant to this
	 * method, so do not mix this generic mocking with a specialized one. It
	 * could become confusing.
	 * 
	 * @param entityManagerMock the mock of the EntityManager to program
	 * @param first first result returned
	 * @param more other results returned for subsequent calls, one at a time
	 */
	public static void mockAnyActionAnyQuery(EntityManager entityManagerMock, Object first, Object... more)
	{
		AnquDummyQuery firstDummy = new AnquDummyQuery(first instanceof List ? (List<?>)first : genList(first));
		AnquDummyQuery[] moreDummies = new AnquDummyQuery[more.length];
		for (int i = 0; i < more.length; i++)
		{
			moreDummies[i] = new AnquDummyQuery(more[i] instanceof List ? (List<?>)more[i] : genList(more[i]));
		}
		given(entityManagerMock.createNamedQuery(anyString())).willReturn(firstDummy, moreDummies);
		given(entityManagerMock.createNamedQuery(anyString(), (Class<?>)any(Class.class))).willReturn(firstDummy, moreDummies);
	}

	/**
	 * Creates a List from the values - generic version. Name differs from
	 * asList() to handle the case where the special type is also Object.
	 * 
	 * @param values the values
	 * @return the list
	 */
	public static List<?> genList(Object... values)
	{
		return Arrays.asList(values);
	}

	/**
	 * This helper class represents a Query and TypedQuery implementation which
	 * will return the values defined during construction as results of
	 * getResultList(), getSingleResult() or executeUpdate(). It could also
	 * throw the given exception.
	 * 
	 * The dummy query will return itself on any operation configuring it, e.g.
	 * setParameter(...). Once set up, the query will always return the same
	 * result(s).
	 * 
	 */
	private static final class AnquDummyQuery implements Query, TypedQuery {

		// Everything is packed into this generic list, modeling is overrated.
		private List resultList;

		private AnquDummyQuery(int changeCount)
		{
			resultList = new ArrayList<>(1);
			resultList.add((Integer)changeCount);
		}

		private AnquDummyQuery(List resultList)
		{
			this.resultList = resultList;
		}

		public Object getSingleResult()
		{
			if (resultList == null || resultList.isEmpty())
			{
				return null;
			}
			Object result = resultList.get(0);
			if (result instanceof RuntimeException)
			{
				throw (RuntimeException)result;
			}
			return result;

		}

		public List<?> getResultList()
		{
			if (resultList == null || resultList.isEmpty())
			{
				return new ArrayList<>();
			}
			Object result = resultList.get(0);
			if (result instanceof RuntimeException)
			{
				throw (RuntimeException)result;
			}
			return resultList;
		}

		public int executeUpdate()
		{
			if (resultList == null || resultList.isEmpty())
			{
				return 0;
			}
			Object result = resultList.get(0);
			if (result instanceof RuntimeException)
			{
				throw (RuntimeException)result;
			}
			return (int)result;
		}

		public int getFirstResult()
		{
			return 0;
		}

		public FlushModeType getFlushMode()
		{
			return FlushModeType.AUTO;
		}

		public Map<String, Object> getHints()
		{
			return new HashMap<String, Object>();
		}

		public LockModeType getLockMode()
		{
			return LockModeType.READ;
		}

		public int getMaxResults()
		{
			return 0;
		}

		public Parameter<?> getParameter(String arg0)
		{
			return null;
		}

		public Parameter<?> getParameter(int arg0)
		{
			return null;
		}

		public <T> Parameter<T> getParameter(String arg0, Class<T> arg1)
		{
			return null;
		}

		public <T> Parameter<T> getParameter(int arg0, Class<T> arg1)
		{
			return null;
		}

		public <T> T getParameterValue(Parameter<T> arg0)
		{
			return null;
		}

		public Object getParameterValue(String arg0)
		{
			return null;
		}

		public Object getParameterValue(int arg0)
		{
			return null;
		}

		public Set<Parameter<?>> getParameters()
		{
			return null;
		}

		public boolean isBound(Parameter<?> arg0)
		{
			return false;
		}

		public TypedQuery<?> setFirstResult(int arg0)
		{
			return this;
		}

		public TypedQuery<?> setFlushMode(FlushModeType arg0)
		{
			return this;
		}

		public TypedQuery<?> setHint(String arg0, Object arg1)
		{
			return this;
		}

		public TypedQuery<?> setLockMode(LockModeType arg0)
		{
			return this;
		}

		public TypedQuery<?> setMaxResults(int arg0)
		{
			return this;
		}

		public TypedQuery<?> setParameter(Parameter arg0, Object arg1)
		{
			return this;
		}

		public TypedQuery<?> setParameter(String arg0, Object arg1)
		{
			return this;
		}

		public TypedQuery<?> setParameter(int arg0, Object arg1)
		{
			return this;
		}

		public TypedQuery<?> setParameter(Parameter arg0, Calendar arg1, TemporalType arg2)
		{
			return this;
		}

		public TypedQuery<?> setParameter(Parameter arg0, Date arg1, TemporalType arg2)
		{
			return this;
		}

		public TypedQuery<?> setParameter(String arg0, Calendar arg1, TemporalType arg2)
		{
			return this;
		}

		public TypedQuery<?> setParameter(String arg0, Date arg1, TemporalType arg2)
		{
			return this;
		}

		public TypedQuery<?> setParameter(int arg0, Calendar arg1, TemporalType arg2)
		{
			return this;
		}

		public TypedQuery<?> setParameter(int arg0, Date arg1, TemporalType arg2)
		{
			return this;
		}

		public <T> T unwrap(Class<T> arg0)
		{
			return null;
		}

	}
}
