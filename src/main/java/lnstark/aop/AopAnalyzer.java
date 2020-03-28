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
		List<Object> ol = context.getAll();
		for(Object o : ol) {
			Aspect a = o.getClass().getAnnotation(Aspect.class);
			if(a != null)
				configAspect(o);
		}
		Set<String> nameSet = context.getNameSet();
		for(String name : nameSet) {
			Object proxy = analyzeMethod(context.getBeanByName(name));
			if(proxy != null) {
//				context.put(context.getBeanByName(name), proxy);
				context.addBean(name, proxy);
			}
		}
		
	}

	/**
	 * 给存在配置了AOP注解方法的类配置代理
	 * @return proxy 代理对象
	 */
	private Object analyzeMethod(Object o) {
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
				m2p.add(m, a2p.get(ca));
			}
		}
		Object proxy = null;
		if(needProxy) {
			proxy = proxyObject(o, m2p);
		}
		return proxy;
	}

	/**
	 * 采用CGLIB动态代理
	 * @param o
	 * @param m2p
	 * @return
	 */
	private Object proxyObject(Object o, MapList<Method, PointcutImpl> m2p) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(o.getClass());
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				// execute method
				Object object = proxy.invokeSuper(obj, args);
				// execute after returning
				if(m2p.containsKey(method)) {
					List<PointcutImpl> pcis = m2p.get(method);
					Method afterReturningMethod;
					for(PointcutImpl pci : pcis) {
						if((afterReturningMethod = pci.getAfterReturning()) != null) {
							afterReturningMethod.invoke(pci.getAspectConfig(), new JointPoint(method));
						}
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
	private void configAspect(Object o) {
		Method[] ms = o.getClass().getDeclaredMethods();
		for(Method m : ms) {
			constructPointcuts(o, m);
		}
		for(Method m : ms) {
			AfterReturning ar = m.getAnnotation(AfterReturning.class);
			if(ar != null) {
				String arv = ar.value();
				if(arv.length() < 2)
					throw new AopException("value of AfterReturning should be like \"PointcutName()\"");
				String pointcutName = arv.substring(0, arv.length() - 2);
				PointcutImpl pc = pointcuts.get(pointcutName);

				if (pc != null)
					pc.setAfterReturning(m);
			}

		}
	}

	/**
	 * construct pointcuts
	 */
	private void constructPointcuts(Object configObj, Method m) {
		Pointcut pc = m.getAnnotation(Pointcut.class);
		if(pc == null)
			return;
		String pcv = pc.value();
//		String str = "@annotation(lnstark.aop.AopTestAnno)";
		Matcher mat = Pattern.compile("(?<=\\()(\\S+)(?=\\))").matcher(pcv);
		if(!mat.find())
			return;
		String annoName = mat.group();
		String annoPointName = m.getName();
		if(pointcuts.containsKey(annoPointName))
			throw new AopException("duplicated aspect annotation found: " + annoPointName);
		Class<?> annoClz = null;
		try {
			annoClz = ClassUtil.getClassLoader().loadClass(annoName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(annoClz == null)
			return;
		PointcutImpl pci = new PointcutImpl(configObj, annoClz);
		pointcuts.put(annoPointName, pci);
		a2p.put(annoClz, pci);
		// TODO
//		Class<?> ClassUtil.getClassLoader().loadClass(annoPointName);

	}
}
