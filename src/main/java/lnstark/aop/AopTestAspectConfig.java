package lnstark.aop;


import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lnstark.annotations.Component;
import lnstark.aop.anno.AfterReturning;
import lnstark.aop.anno.Aspect;
import lnstark.aop.anno.Pointcut;

/**
 * 
 * aop config
 */
@Aspect
@Component
public class AopTestAspectConfig {
	
	@Pointcut("@annotation(lnstark.aop.AopTestAnno)")
    public void poinCut1() {
    }
	
	@AfterReturning("poinCut1()")
    public void doAfterMethod(JointPoint point) {
		Method m = point.getM();
//		System.out.println("after excute method " + m.getName());
		System.out.println("do something after method " + m.getName());
		
	}
	
	public static void main(String[] args) {
		String str = "@annotation(lnstark.aop.AopTestAnno)";
		Matcher m = Pattern.compile("(?<=\\()(\\S+)(?=\\))").matcher(str);
		if(m.find()) {
			System.out.println(m.group());
		}
	}
}
