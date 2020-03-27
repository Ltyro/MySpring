package lnstark.aop;


import java.lang.reflect.Method;


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
	
}
