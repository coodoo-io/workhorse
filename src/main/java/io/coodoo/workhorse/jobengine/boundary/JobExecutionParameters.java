package io.coodoo.workhorse.jobengine.boundary;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Marker Interface.
 * <p>
 * <strong> The implementation class needs to have a default constructor without parameters, so it is able to get deserialized! </strong>
 * </p>
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = JobExecutionParameters.PARAMETERS_CLASS_JSON_KEY)
public interface JobExecutionParameters {

    public static final String PARAMETERS_CLASS_JSON_KEY = "ParametersClass";

}
