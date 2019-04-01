package io.coodoo.workhorse.jobengine.boundary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Initial / default job schedule settings
 * 
 * @author coodoo GmbH (coodoo.io)
 * 
 * @deprecated use {@link InitialJobConfig}
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobScheduleConfig {

    String second() default "0";

    String minute();

    String hour();

    String dayOfWeek() default "*";

    String dayOfMonth() default "*";

    String month() default "*";

    String year() default "*";

}
