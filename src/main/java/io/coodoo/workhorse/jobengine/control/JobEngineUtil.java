package io.coodoo.workhorse.jobengine.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;
import io.coodoo.workhorse.jobengine.control.annotation.SystemJob;
import io.coodoo.workhorse.jobengine.entity.JobType;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public final class JobEngineUtil {

    private static Logger logger = LoggerFactory.getLogger(JobEngineUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private JobEngineUtil() {}

    @SuppressWarnings("serial")
    public static Map<Class<?>, JobType> getAvailableWorkers() {

        Map<Class<?>, JobType> workers = new HashMap<>();

        for (Bean<?> workerBean : CDI.current().getBeanManager().getBeans(BaseJobWorker.class, new AnnotationLiteral<Any>() {})) {

            Class<?> workerClass = workerBean.getBeanClass();

            if (workerClass.isAnnotationPresent(SystemJob.class)) {
                workers.put(workerClass, JobType.SYSTEM);
            } else if (workerClass.isAnnotationPresent(JobScheduleConfig.class)) {
                workers.put(workerClass, JobType.SCHEDULED);
            } else {
                workers.put(workerClass, JobType.ON_DEMAND);
            }
        }
        return workers;
    }

    public static LocalDateTime timestamp() {
        return LocalDateTime.now(JobEngineConfig.TIME_ZONE);
    }

    public static LocalDateTime delayToMaturity(Long delayValue, ChronoUnit delayUnit) {

        LocalDateTime maturity = null;
        if (delayValue != null && delayUnit != null) {
            maturity = timestamp().plus(delayValue, delayUnit);
        }
        return maturity;
    }

    public static <T> T jsonToParameters(String parametersJson, Class<T> parametersClass) {
        if (parametersJson == null || parametersJson.isEmpty()) {
            return null;
        }
        try {
            return (T) objectMapper.readValue(parametersJson, parametersClass);
        } catch (IOException e) {
            throw new RuntimeException("JSON Parameter could not be mapped to an object", e);
        }
    }

    public static String parametersToJson(Object parametersObject) {
        if (parametersObject == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(parametersObject);
        } catch (IOException e) {
            throw new RuntimeException("Parameter object could not be mapped to json", e);
        }
    }

    public static String stacktraceToString(Exception e) {
        String stacktraceString = null;
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter, true)) {
            e.printStackTrace(printWriter);
            printWriter.flush();
            stringWriter.flush();
            stacktraceString = stringWriter.toString();
        } catch (IOException e1) {
            stacktraceString = "Couldn't write exception!";
            logger.error(stacktraceString, e);
        }
        return stacktraceString;
    }

}
