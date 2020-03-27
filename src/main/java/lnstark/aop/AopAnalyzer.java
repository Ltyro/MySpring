package lnstark.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lnstark.aop.anno.AfterReturning;
import lnstark.aop.anno.Aspect;
import lnstark.aop.anno.Pointcut;
import lnstark.dataStructure.MapList;
import lnstark.utils.Analyzer;
import lnstark.utils.ClassUtil;

public class AopAnalyzer extends Analyzer{

	private Map<String, PointcutImpl> pointcuts;
	
	public AopAnalyzer() {
		super();
		pointcuts = new HashMap<>();
	}
	
	public void analyze() {
		List<Class<?>> l = context.getAllClass();
		for(Class<?> c : l) {
			Aspect a = c.getAnnotation(Aspect.class);
			if(a != null)
				configAspect(c);
		}
		
		for(Class<?> c : l) {
			analyzeMethod(c);
		}
		
	}

	/**
	 * 给加了注解的类配置代理
	 */
	private void analyzeMethod(Class<?> c) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 解析配置类
	 */
	private void configAspect(Class<?> c) {
		Method[] ms = c.getDeclaredMethods();
		for(Method m : ms) {
			Annotation as[] = m.getAnnotations();
			for(Annotation a : as) {
				if(a instanceof Pointcut) {
					Pointcut pc = (Pointcut) a;
					String pcv = pc.value();
//					String str = "@annotation(lnstark.aop.AopTestAnno)";
					Matcher mat = Pattern.compile("(?<=\\()(\\S+)(?=\\))").matcher(pcv);
					if(mat.find()) {
						String annoPointName = mat.group();
						if(pointcuts.containsKey(annoPointName))
							throw new AopException("duplicated aspect annotation found: " + annoPointName);
						pointcuts.put(annoPointName, new PointcutImpl());
						// TODO
//						Class<?> ClassUtil.getClassLoader().loadClass(annoPointName);
					}
				} else if(a instanceof AfterReturning) {
					
				}
			}
			
		}
	}

}
