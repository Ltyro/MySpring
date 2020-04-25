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
//        int sec = 0, min = 0, hour = 0, day = 0, month = 0, weekday = 0;
        Time sec = new Time(0, false, TimeType.Second),
                min = new Time(0, false, TimeType.Minute),
                hour = new Time(0, false, TimeType.Hour),
                day = new Time(0, false, TimeType.Day),
                month = new Time(0, false, TimeType.Month),
                weekday = new Time(0, false, TimeType.Week);
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

            Time timeArr[] = {sec, min, hour, day, month, weekday};
            executeSchedule(timeArr, m, o);
        }
    }

    private void executeSchedule(Time[] timeArr, Method m, Object o) {
        if(timeArr == null || timeArr.length != 6)
            return;
        boolean always = true;
        Timer t = new Timer();

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(Calendar.SECOND, timeArr[0].getValue());
        c.set(Calendar.MINUTE, timeArr[1].getValue());
        c.set(Calendar.HOUR_OF_DAY, timeArr[2].getValue());
        c.set(Calendar.DATE, timeArr[3].getValue());
        c.set(Calendar.MONTH, timeArr[4].getValue() - 1);
        c.set(Calendar.DAY_OF_WEEK, (timeArr[5].getValue() + 1) % 7);
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
