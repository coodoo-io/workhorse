package io.coodoo.workhorse.jobengine.boundary;

import java.lang.reflect.ParameterizedType;

import io.coodoo.workhorse.jobengine.control.BaseJobWorker;
import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

/**
 * Job worker class to define the creation and execution of jobs.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class JobWorkerWith<T> extends BaseJobWorker {

    public abstract void doWork(T parameters) throws Exception;

    public void doWork(JobExecution jobExecution) throws Exception {

        this.jobExecution = jobExecution;
        this.jobLogger.setLog(jobExecution.getLog());

        Class<?> parametersClass = getParamtersClass();
        @SuppressWarnings("unchecked")
        T parameters = (T) JobEngineUtil.jsonToParameters(jobExecution.getParameters(), parametersClass);

        doWork(parameters);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getParamtersClass() {

        // TODO als attribut in die klasse und im default konstruktor befüllen

        try {
            String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
            Class<?> clazz = Class.forName(className);
            return (Class<T>) clazz;
        } catch (Exception e) {
            throw new IllegalStateException("Class is not parametrized with generic type! Please use extends <> ");
        }
    }
}
