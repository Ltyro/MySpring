package lnstark.aop;

import lnstark.utils.ClassUtil;

import java.lang.annotation.Annotation;

public class PointcutImpl {
	private String name;

	Class<?> annoClz;

	public PointcutImpl(Class<?> annoClz) {
		this.annoClz = annoClz;
	}

//	public static void main(String[] args) throws ClassNotFoundException {
//		Class<?> a = ClassUtil.getClassLoader().loadClass("lnstark.aop.AopTestAnno");
//		System.out.println(a == AopTestAnno.class);
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
