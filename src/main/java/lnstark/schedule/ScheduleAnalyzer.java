package lnstark.schedule;

import lnstark.entity.Configuration;
import lnstark.schedule.annos.EnableScheduling;
import lnstark.schedule.annos.Scheduled;
import lnstark.schedule.core.TaskScheduler;
import lnstark.utils.Analyzer;
import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;

import java.lang.reflect.Method;
import java.util.List;


public class ScheduleAnalyzer extends Analyzer {


	@Override
	public void analyze() {

		Context context = ContextAware.getContext();
		Configuration config = context.getConfig();
		boolean enableSchedule = false;
		List<Class<?>> cl = context.getAllClass();
		for (Class<?> c : cl) {
			EnableScheduling a = c.getAnnotation(EnableScheduling.class);
			if (a != null) {
				enableSchedule = true;
				config.setEnableSchedule(true);
				break;
			}
		}
		if (enableSchedule) {
			List<Object> ol = context.getAll();
			for (Object o : ol) {
				analyzeSchedule(o);
			}
		}
	}

	private void analyzeSchedule(Object o) {
		Method[] ms = o.getClass().getDeclaredMethods();

		for (Method m : ms) {
			Scheduled sa = m.getAnnotation(Scheduled.class);


			new TaskScheduler().execute(sa, m, o);

		}
	}

	public static void main(String[] args) {
		System.out.println(true ^ true);
		System.out.println(!false ^ true);
		System.out.println(false ^ false);
	}
}
