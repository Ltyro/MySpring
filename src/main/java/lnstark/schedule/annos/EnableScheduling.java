package lnstark.schedule.annos;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(SchedulingConfiguration.class)
@Documented
public @interface EnableScheduling {

}
