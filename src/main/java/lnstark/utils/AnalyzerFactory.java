package lnstark.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerFactory {

	// class to single instance map
	private static Map<Class, Analyzer> m = new HashMap<>();
		
	public static Analyzer getAnalyzer(Class<?> c) {
		Analyzer a = m.get(c);
		if(a == null) {
			try {
				a = (Analyzer) c.getConstructor().newInstance();
				m.put(c, a);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return a;
	}
	
}
