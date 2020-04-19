package lnstark.schedule;

import lnstark.aop.anno.Aspect;
import lnstark.utils.Analyzer;

import java.util.List;

public class ScheduleAnalyzer extends Analyzer {

    @Override
    public void analyze() {
        List<Object> ol = context.getAll();
        for(Object o : ol) {
            Aspect a = o.getClass().getAnnotation(Aspect.class);
//            if(a != null)
//                configAspect(o);
        }
    }

}
