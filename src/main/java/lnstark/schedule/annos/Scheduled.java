package lnstark.schedule.annos;

import org.springframework.scheduling.annotation.Schedules;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Repeatable(Schedules.class)
public @interface Scheduled {

//    String CRON_DISABLED = "-";

    String cron() default "";

//    String zone() default "";
//
//    long fixedDelay() default -1;
//
//    String fixedDelayString() default "";
//
//    long fixedRate() default -1;
//
//    String fixedRateString() default "";
//
//    long initialDelay() default -1;
//
//    String initialDelayString() default "";

}
