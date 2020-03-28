package lnstark.aop;

import lnstark.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class PointcutImpl {
	private String name;

	private Object AspectConfig;

	Class<?> annoClz;

	private Method afterReturning;



	public PointcutImpl(Object configObj, Class<?> annoClz) {
		AspectConfig = configObj;
		this.annoClz = annoClz;
	}

//	public static void main(String[] args) throws ClassNotFoundException {
//		Class<?> a = ClassUtil.getClassLoader().loadClass("lnstark.aop.AopTestAnno");
//		System.out.println(a == AopTestAnno.class);
//	}

	public Method getAfterReturning() {
		return afterReturning;
	}

	public void setAfterReturning(Method afterReturning) {
		this.afterReturning = afterReturning;
	}

	public String getName() {
		return name;
	}

	public Object getAspectConfig() {
		return AspectConfig;
	}

	public void setAspectConfig(Object aspectConfig) {
		AspectConfig = aspectConfig;
	}

	public void setName(String name) {
		this.name = name;
	}
}
