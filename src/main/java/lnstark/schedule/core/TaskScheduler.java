package lnstark.schedule.core;

import lnstark.exception.ScheduleException;
import lnstark.schedule.annos.Scheduled;
import lnstark.schedule.entity.Time;
import lnstark.schedule.entity.TimeType;
import lnstark.utils.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A cron expression parser and task executor
 */
public class TaskScheduler implements Runnable{

    private BitSet seconds      = new BitSet(60);

    private BitSet minutes      = new BitSet(60);

    private BitSet hours        = new BitSet(24);

    private BitSet daysOfMonth  = new BitSet(31);

    private BitSet daysOfWeek   = new BitSet(7);

    private BitSet months       = new BitSet(12);

    private BitSet[] timeFields;

    private Method method;
    
    private Object target;
    
    private Scheduled schedule;
    
    private boolean start = false, end = false;

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);

    private static final String ERROR_EXPRESSION = "error cron expression";

    private static final String STAR = "*", QUESMARK = "?", COMMA = ",";

    public TaskScheduler(Method m, Object o, Scheduled sa) {
        timeFields = new BitSet[]{seconds, minutes, hours, daysOfMonth, months, daysOfWeek};
        int timeTypes[] = new int[]{
            Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR,
            Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.DAY_OF_WEEK
        };
        this.method = m;
        this.target = o;
        this.schedule = sa;
        
        String cron = sa.cron();
        if (StringUtil.isEmpty(cron))
            throw new NullPointerException("cron expression should not be null");
     // int sec = 0, min = 0, hour = 0, day = 0, month = 0, weekday = 0;
//      Time sec = new Time(0, false, TimeType.Second), min = new Time(0, false, TimeType.Minute),
//              hour = new Time(0, false, TimeType.Hour), day = new Time(1, false, TimeType.Day),
//              month = new Time(0, false, TimeType.Month), weekday = new Time(0, false, TimeType.Week);
        String[] times = cron.split(" ");
        if (times.length != 6)
            throw new ScheduleException(ERROR_EXPRESSION);

        for (int i = 0; i < times.length; i++) {
            setTimeField(times[i], timeFields[i], timeTypes[i]);
        }
    }

    public void execute() {
        
        // execute first time
        long delay = 0;
        Calendar c = Calendar.getInstance();
        if (!start) {
        	long firstTime = getFirstTimeFromBitSets();
        	delay = firstTime - System.currentTimeMillis();
        	if(delay < 0)
        		delay = getNextTime(c) - System.currentTimeMillis();
        	start = true;
        } else {
        	delay = getNextTime(c) - System.currentTimeMillis();
        }
        if (end)
        	return;
        executor.schedule(this, delay, TimeUnit.MILLISECONDS);
    }

    private void setTimeField(String time, BitSet timeField, int type) {

        if (STAR.equals(time) || QUESMARK.equals(time)) {
            timeField.set(0, timeField.size());
            return;
        }

        if (StringUtil.isNumberOnly(time)) {
            int value = getTimeValue(time, type);
            timeField.set(value);
            return;
        }

        if (time.contains("-")) {
            String[] strs = time.split("-");
            int start = getTimeValue(strs[0], type),
                    end = getTimeValue(strs[1], type);
            setPeriodTimeField(timeField, start, end, 1);
            return;
        }

        if (time.contains(COMMA)) {
            String[] strs = time.split(COMMA);
            for (String s : strs) {
                int value = getTimeValue(s, type);
                timeField.set(value);
            }
            return;
        }

        if (time.contains("/")) {
            String[] strs = time.split("/");
            int start = 0, end = time.length() - 1;
            if (strs[0].contains("-")) {
                String be[] = strs[0].split("-");
                start = getTimeValue(be[0], type);
                end = getTimeValue(be[1], type);
            }

            int period = Integer.parseInt(strs[1]);
            setPeriodTimeField(timeField, start, end, period);
            return;
        }

    }

    private void setPeriodTimeField(BitSet timeField, int start, int end, int period) {
        if(start == end) {
            timeField.set(start);
            return;
        }
        if(start < end) {
            for (int i = start; i <= end; i += period) {
                timeField.set(i);
            }
        } else {
            for (int i = start; i <= end + timeField.length(); i += period) {
                timeField.set(i % timeField.length());
            }
        }
    }

    private int getTimeValue(String time, int type) {
        int value = Integer.parseInt(time);
        if (type == Calendar.DAY_OF_MONTH)
            value--;
        if (type == Calendar.DAY_OF_WEEK && value == 7)
            value = 0;
        return value;
    }

    /**
     * get next execution time
     * @param c
     */
    private long getNextTime(Calendar c) {
    	c.set(Calendar.MILLISECOND, 0);
    	
    	// second
    	int sec = c.get(Calendar.SECOND);
        int nextSec = seconds.nextSetBit(sec + 1);
        if (nextSec == -1) {
        	nextSec = seconds.nextSetBit(0);
            c.set(Calendar.SECOND, nextSec);
        } else {
        	c.set(Calendar.SECOND, nextSec);
        	return c.getTimeInMillis();
        }
        
        // minute
        int min = c.get(Calendar.MINUTE);
        int nextMin = minutes.nextSetBit(min + 1);
        if (nextMin == -1) {
        	nextMin = minutes.nextSetBit(0);
            c.set(Calendar.MINUTE, nextMin);
        } else {
        	c.set(Calendar.MINUTE, nextMin);
        	return c.getTimeInMillis();
        }
        
        // hour
        int h = c.get(Calendar.HOUR);
        int nextH = minutes.nextSetBit(h + 1);
        if (nextH == -1) {
        	nextH = minutes.nextSetBit(0);
            c.set(Calendar.HOUR, nextH);
        } else {
        	c.set(Calendar.HOUR, nextH);
        	return c.getTimeInMillis();
        }

        nextDay(c);
//        // day of month
//        int d = c.get(Calendar.DAY_OF_MONTH);
//        int nextDay = daysOfMonth.nextSetBit(d);
//
//        if (nextDay == -1) {
//        	nextDay = daysOfMonth.nextSetBit(0);
//        } else {
//        	c.set(Calendar.DAY_OF_MONTH, nextDay);
//        	return c.getTimeInMillis();
//        }
//
//        // month
//        int mon = c.get(Calendar.MONTH);
//        int nextMon = daysOfMonth.nextSetBit(mon + 1);
//        if (nextMon == -1) {
//            c.add(Calendar.YEAR, 1);// next year
//        	nextMon = daysOfMonth.nextSetBit(0);
//        } else {
//        	c.set(Calendar.MONTH, nextMon);
//        	return c.getTimeInMillis();
//        }
//
//        // day of week
//        int dow = c.get(Calendar.DAY_OF_WEEK);
//        int nextDow = daysOfMonth.nextSetBit(dow + 1);
//        if (nextDow == -1) {
//        	nextDow = daysOfMonth.nextSetBit(0);
//        } else {
//        	c.set(Calendar.DAY_OF_WEEK, nextDow);
//        	return c.getTimeInMillis();
//        }

        return 0;
    }

    // day
    private void nextDay(Calendar c) {
        int d = c.get(Calendar.DAY_OF_MONTH);
        int nextDay = daysOfMonth.nextSetBit(d);
        // TODO
        if (nextDay == -1) {
            nextDay = daysOfMonth.nextSetBit(0);
            c.set(Calendar.DAY_OF_MONTH, nextDay);
            nextMonth(c);
        } else {

        }
    }

    // month
    private void nextMonth(Calendar c) {

        int mon = c.get(Calendar.MONTH);
        int nextMon = daysOfMonth.nextSetBit(mon + 1);
        if (nextMon == -1) {
            c.add(Calendar.YEAR, 1);// next year
        	nextMon = daysOfMonth.nextSetBit(0);
        }
        c.set(Calendar.MONTH, nextMon);

    }

    private boolean fitDayOfWeek(Calendar c) {
        int dow = c.get(Calendar.DAY_OF_WEEK);
        return daysOfWeek.get(dow - 1);
    }

    private long getFirstTimeFromBitSets() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, seconds.nextSetBit(0));
        c.set(Calendar.MINUTE, minutes.nextSetBit(0));
        c.set(Calendar.HOUR_OF_DAY, hours.nextSetBit(0));
        c.set(Calendar.DAY_OF_MONTH, daysOfMonth.nextSetBit(0) + 1);
        c.set(Calendar.MONTH, months.nextSetBit(0));
        c.set(Calendar.DAY_OF_WEEK, daysOfWeek.nextSetBit(0));
        return c.getTime().getTime();
    }

    // 参考CronSequenceGenerator
    private void executeSchedule(Time[] timeArr) {
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

//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//            invokeMethod(m, o);
//            t.cancel();
//            }
//        }, c.getTime());

    }

    private void invokeMethod() {
        try {
            method.invoke(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	invokeMethod();
    	execute();
    }
}
