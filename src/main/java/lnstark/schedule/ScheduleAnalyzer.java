package lnstark.schedule;

import lnstark.entity.Configuration;
import lnstark.exception.ScheduleException;
import lnstark.schedule.annos.EnableScheduling;
import lnstark.schedule.annos.Scheduled;
import lnstark.schedule.entity.Time;
import lnstark.schedule.entity.TimeType;
import lnstark.utils.Analyzer;
import lnstark.utils.StringUtil;
import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleAnalyzer extends Analyzer {

	final String ERROR_EXPRESSION = "error cron expression";

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
		// int sec = 0, min = 0, hour = 0, day = 0, month = 0, weekday = 0;
		Time sec = new Time(0, false, TimeType.Second), min = new Time(0, false, TimeType.Minute),
				hour = new Time(0, false, TimeType.Hour), day = new Time(1, false, TimeType.Day),
				month = new Time(0, false, TimeType.Month), weekday = new Time(0, false, TimeType.Week);
		for (Method m : ms) {
			Scheduled sa = m.getAnnotation(Scheduled.class);
			if (sa == null)
				continue;
			String cron = sa.cron();
			if (StringUtil.isEmpty(cron))
				throw new NullPointerException("cron expression should not be null");
			String[] times = cron.split(" ");
			if (times.length != 6)
				throw new ScheduleException(ERROR_EXPRESSION);

			// second
			String secStr = times[0];
			sec.setExpression(secStr);

			// minute
			String minuteStr = times[1];
			min.setExpression(minuteStr);

			// hour
			String hourStr = times[2];
			hour.setExpression(hourStr);

			// day
			String dayStr = times[3];
			day.setExpression(dayStr);

			// month
			String monthStr = times[4];
			month.setExpression(monthStr);

			// week
			String weekdayStr = times[5];
			weekday.setExpression(weekdayStr);

			Time timeArr[] = { sec, min, hour, day, month, weekday };
			executeSchedule(timeArr, m, o);
		}
	}

	private void executeSchedule(Time[] timeArr, Method m, Object o) {
		if (timeArr == null || timeArr.length != 6)
			return;
		boolean once = true, // 只执行一次
				always = true;

		for (int i = 0; i < timeArr.length; i++) {
			if (timeArr[i].isEvery()) {
				always = false;
				once = false;
				break;
			}
		}

		Timer t = new Timer();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);

		if (!timeArr[0].isEvery())
			c.set(Calendar.SECOND, timeArr[0].getValue());
		if (!timeArr[1].isEvery())
			c.set(Calendar.MINUTE, timeArr[1].getValue());
		if (!timeArr[2].isEvery())
			c.set(Calendar.HOUR_OF_DAY, timeArr[2].getValue());
		if (!timeArr[3].isEvery())
			c.set(Calendar.DATE, timeArr[3].getValue());
		if (!timeArr[4].isEvery())
			c.set(Calendar.MONTH, timeArr[4].getValue() - 1);
		if (!timeArr[5].isEvery())
			c.set(Calendar.DAY_OF_WEEK, (timeArr[5].getValue() + 1) % 7);

		System.out.println(c.getTime());

		if (once) {
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					invokeMethod(m, o);
					t.cancel();
				}
			}, c.getTime());
		} else if (judgeRegular(timeArr)) {
			int period = getRegularPeriod(timeArr);
		}

	}

	/**
	 * 判断是否固定周期执行 周和月都是"*"或都不是"*"，并且 秒~天 中有一个是"*"则返回true, 否则返回false
	 */
	private boolean judgeRegular(Time[] times) {
		boolean weekAndMonth = !(times[5].isEvery() ^ times[4].isEvery());
		boolean sec2day = times[0].isEvery() || times[1].isEvery() || times[2].isEvery() || times[3].isEvery();
		if (weekAndMonth && sec2day)
			return true;
		return false;
	}

	/**
	 * 获取固定周期
	 */
	private int getRegularPeriod(Time[] timeArr) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void invokeMethod(Method m, Object o) {
		try {
			m.invoke(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(true ^ true);
		System.out.println(!false ^ true);
		System.out.println(false ^ false);
	}
}
