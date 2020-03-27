package lnstark.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lnstark.aop.anno.AfterReturning;
import lnstark.aop.anno.Aspect;
import lnstark.aop.anno.Pointcut;
import lnstark.dataStructure.MapList;
import lnstark.utils.Analyzer;
import lnstark.utils.ClassUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class AopAnalyzer extends Analyzer{

	private Map<String, PointcutImpl> pointcuts;

	private Map<Class<?>, PointcutImpl> a2p;// pointcut configured annotations

	public AopAnalyzer() {
		super();
		pointcuts = new HashMap<>();
		a2p = new HashMap<>();
	}
	
	public void analyze() {
		List<Class<?>> cl = context.getAllClass();
		List<Object> ol = context.getAll();
		for(Class<?> c : cl) {
			Aspect a = c.getAnnotation(Aspect.class);
			if(a != null)
				configAspect(c);
		}
		Set<String> nameSet = context.getNameSet();
		for(String name : nameSet) {
			analyzeMethod(context.getBeanByName(name));
		}
		
	}

	/**
	 * 给加了注解的类配置代理
	 */
	private void analyzeMethod(Object o) {
		Method[] ms = o.getClass().getDeclaredMethods();
		boolean needProxy = false;
		List<Method> aopMethods = new ArrayList<>();
		MapList<Method, PointcutImpl> m2p = new MapList<>();
		for(Method m : ms) {
			for (Class ca : a2p.keySet()) {
				Annotation a = m.getAnnotation(ca);
				if(a == null)
					continue;
				needProxy = true;
				aopMethods.add(m);
				m2p.add(m, a2p.get(a.getClass()));
			}
		}
		if(needProxy) {
			Object proxy = proxyObject(o, m2p);
		}
	}

	private Object proxyObject(Object o, MapList<Method, PointcutImpl> m2p) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(o.getClass());
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Object object = proxy.invokeSuper(obj, args);
				if(m2p.containsKey(method)) {
					List<PointcutImpl> pcis = m2p.get(method);
					for(PointcutImpl pci : pcis) {

					}
				}
				return object;
			}
		});
		Object proxy = enhancer.create();
		return proxy;
	}

	/**
	 * 解析配置类
	 * analyze aspect config classes
	 */
	private void configAspect(Class<?> c) {
		Method[] ms = c.getDeclaredMethods();
		for(Method m : ms) {
			constructPointcuts(m);
		}
		for(Method m : ms) {
			AfterReturning ar = m.getAnnotation(AfterReturning.class);
			if(ar != null) {
				String arv = ar.value();
				if(arv.length() < 2)
					throw new AopException("value of AfterReturning should be like \"PointcutName()\"");
				String pointcutName = arv.substring(0, arv.length() - 2);
				PointcutImpl pc = pointcuts.get(pointcutName);
//				if (pc == null)

			}

		}
	}

	/**
	 * construct pointcuts
	 */
	private void constructPointcuts(Method m) {
		Pointcut pc = m.getAnnotation(Pointcut.class);
		if(pc == null)
			return;
		String pcv = pc.value();
//		String str = "@annotation(lnstark.aop.AopTestAnno)";
		Matcher mat = Pattern.compile("(?<=\\()(\\S+)(?=\\))").matcher(pcv);
		if(!mat.find())
			return;
		String annoPointName = mat.group();
		if(pointcuts.containsKey(annoPointName))
			throw new AopException("duplicated aspect annotation found: " + annoPointName);
		Class<?> annoClz = null;
		try {
			annoClz = ClassUtil.getClassLoader().loadClass(annoPointName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(annoClz == null)
			return;
		PointcutImpl pci = new PointcutImpl(annoClz);
		pointcuts.put(annoPointName, pci);
		a2p.put(annoClz, pci);
		// TODO
//		Class<?> ClassUtil.getClassLoader().loadClass(annoPointName);

	}
}
