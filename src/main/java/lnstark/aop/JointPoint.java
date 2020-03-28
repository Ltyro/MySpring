package lnstark.aop;

import java.lang.reflect.Method;

public class JointPoint {
	
	private Method m;

	public JointPoint(Method m) {
		this.m = m;
	}

	public Method getM() {
		return m;
	}

	public void setM(Method m) {
		this.m = m;
	}

}
