package lnstark.schedule;

import lnstark.entity.Configuration;
import lnstark.exception.ScheduleException;
import lnstark.schedule.annos.EnableScheduling;
import lnstark.schedule.annos.Scheduled;
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
        for(Class<?> c : cl) {
            EnableScheduling a = c.getAnnotation(EnableScheduling.class);
            if(a != null) {
                enableSchedule = true;
                config.setEnableSchedule(true);
                break;
            }
        }
        if(enableSchedule) {
            List<Object> ol = context.getAll();
            for(Object o : ol) {
                analyzeSchedule(o);
            }
        }
    }

    private void analyzeSchedule(Object o) {
        Method[] ms = o.getClass().getDeclaredMethods();
        int sec = 0, min = 0, hour = 0, day = 0, month = 0, weekday = 0;

        for (Method m : ms) {
            Scheduled sa = m.getAnnotation(Scheduled.class);
            if (sa == null)
                continue;
            String cron = sa.cron();
            if (cron == null)
                throw new NullPointerException("cron expression should not be null");
            String[] times = cron.split(" ");
            if (times.length != 6)
                throw new ScheduleException(ERROR_EXPRESSION);

            // second
            String secStr = times[0];
            if (StringUtil.isNumberOnly(secStr)) {
                sec = Integer.parseInt(secStr);
                if (sec > 60)
                    throw new ScheduleException("second should not more than 60");
            }

            // minute
            String minuteStr = times[1];
            if (StringUtil.isNumberOnly(minuteStr)) {
                min = Integer.parseInt(minuteStr);
                if (min > 60)
                    throw new ScheduleException("minute should not more than 60");
            }

            // hour
            String hourStr = times[2];
            if (StringUtil.isNumberOnly(hourStr)) {
                hour = Integer.parseInt(hourStr);
                if (hour > 23)
                    throw new ScheduleException("minute should not more than 23");
            }

            // day
            String dayStr = times[3];
            if (StringUtil.isNumberOnly(dayStr)) {
                day = Integer.parseInt(dayStr);
                if (day > 31)
                    throw new ScheduleException("day should not more than 31");
            }

            // month
            String monthStr = times[4];
            if (StringUtil.isNumberOnly(monthStr)) {
                month = Integer.parseInt(monthStr);
                if (month > 12)
                    throw new ScheduleException("month should not more than 12");
            }

            // week
            String weekdayStr = times[5];
            if (StringUtil.isNumberOnly(weekdayStr)) {
                weekday = Integer.parseInt(weekdayStr);
                if (weekday > 7)
                    throw new ScheduleException("weekday should not more than 7");
            }

            int timeArr[] = {sec, min, hour, day, month, weekday};
            executeSchedule(timeArr, m, o);
        }
    }

    private void executeSchedule(int[] timeArr, Method m, Object o ) {
        if(timeArr == null || timeArr.length != 6)
            return;
        Timer t = new Timer();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, timeArr[0]);
        c.set(Calendar.MINUTE, timeArr[1]);
        c.set(Calendar.HOUR_OF_DAY, timeArr[2]);
        c.set(Calendar.DATE, timeArr[3]);
        c.set(Calendar.MONTH, timeArr[4] - 1);
        c.set(Calendar.DAY_OF_WEEK, (timeArr[5] + 1) % 7);
        System.out.println(c.getTime());
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    m.invoke(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                t.cancel();
            }
        }, c.getTime());
    }

    public static void main(String[] args) {
        String str = "123";
        System.out.println(str.matches("^\\d+$"));
    }
}
