package lnstark.aop;

import java.util.List;

import lnstark.aop.anno.Aspect;
import lnstark.dataStructure.MapList;
import lnstark.utils.Analyzer;

public class AopAnalyzer extends Analyzer{

	public AopAnalyzer() {
		super();
	}
	
	public void analyze() {
		List<Class<?>> l = context.getAllClass();
//		Class c = l.get(0);
		for(Class<?> c : l) {
			Aspect a = c.getAnnotation(Aspect.class);
			if(a == null)
				continue;
			System.out.println(c.getName() + " " + a.getClass().getName());
		}
	}

}
