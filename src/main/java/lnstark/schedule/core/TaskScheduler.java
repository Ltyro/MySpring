package lnstark.schedule.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lnstark.exception.ScheduleException;
import lnstark.schedule.annos.Scheduled;
import lnstark.schedule.entity.Time;
import lnstark.utils.StringUtil;

/**
 * A CRON expression parser and task executor
 * Reference from #{CronSequenceGenerator}
 */
public class TaskScheduler implements Runnable {

    private BitSet seconds      = new BitSet(60);

    private BitSet minutes      = new BitSet(60);

    private BitSet hours        = new BitSet(24);

    private BitSet daysOfMonth  = new BitSet(31);

    private BitSet daysOfWeek   = new BitSet(7);

    private BitSet months       = new BitSet(12);

    private BitSet[] timeFields;

    private Method method;
    
    private Object target;
    
//    private Scheduled schedule;
    
//    private boolean start = false, end = false;

//    private Calendar lastExecutionTime = null;
    
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);

    private static final String ERROR_EXPRESSION = "error cron expression";

    private static final String STAR = "*", QUESMARK = "?", COMMA = ",";

    public TaskScheduler(Method m, Object o, Scheduled sa) {
        timeFields = new BitSet[]{seconds, minutes, hours, daysOfMonth, months, daysOfWeek};
        int timeTypes[] = new int[]{
            Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR,
            Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.DAY_OF_WEEK
        };
        int timeLengths[] = new int[] {
        	60, 60, 60, 31, 12, 7
        };
        this.method = m;
        this.target = o;
//        this.schedule = sa;
        
        String cron = sa.cron();
        if (StringUtil.isEmpty(cron))
            throw new NullPointerException("cron expression should not be null");
        
        String[] times = cron.split(" ");
        if (times.length != 6)
            throw new ScheduleException(ERROR_EXPRESSION);

        for (int i = 0; i < times.length; i++) {
            setTimeField(times[i], timeFields[i], timeTypes[i], timeLengths[i]);
        }
    }

    public void execute() {
        
        long delay = 0;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 1);
        // while next execution time is earlier than current time, do getNextTime
        while((delay = getNextTime(c) - System.currentTimeMillis()) <= 0);
        System.out.println("next execution time is: " + c.getTime());
        executor.schedule(this, delay, TimeUnit.MILLISECONDS);
    }

    private void setTimeField(String time, BitSet timeField, int type, int timeLength) {

        if (STAR.equals(time) || QUESMARK.equals(time)) {
            timeField.set(0, timeLength);
            return;
        }

        if (StringUtil.isNumberOnly(time)) {
            int value = getTimeValue(time, type);
            timeField.set(value);
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
            int start = 0, end = timeLength - 1;
            if (strs[0].contains("-")) {
                String be[] = strs[0].split("-");
                start = getTimeValue(be[0], type);
                end = getTimeValue(be[1], type);
            }

            int period = Integer.parseInt(strs[1]);
            setPeriodTimeField(timeField, start, end, period, timeLength);
        } else if (time.contains("-")) {
            String[] strs = time.split("-");
            int start = getTimeValue(strs[0], type),
                    end = getTimeValue(strs[1], type);
            setPeriodTimeField(timeField, start, end, 1, timeLength);
        }

    }

    private void setPeriodTimeField(BitSet timeField, int start, int end, int period, int timeLength) {
        if(start == end) {
            timeField.set(start);
            return;
        }
        if(start < end) {
            for (int i = start; i <= end; i += period) {
                timeField.set(i);
            }
        } else {
            for (int i = start; i <= end + timeLength; i += period) {
                timeField.set(i % timeLength);
            }
        }
    }

    private int getTimeValue(String time, int type) {
        int value = Integer.parseInt(time);
        if (type == Calendar.DAY_OF_MONTH || type == Calendar.MONTH)
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
    	boolean resetSec = false, resetMin = false, resetHour = false;
        int second = c.get(Calendar.SECOND);
        while (!seconds.get(second)) {
        	c.add(Calendar.SECOND, 1);
        	second = c.get(Calendar.SECOND);
        }
        
        int min = c.get(Calendar.MINUTE);
        while (!minutes.get(min)) {
        	c.add(Calendar.MINUTE, 1);
        	min = c.get(Calendar.MINUTE);
        	if (!resetSec) {
	        	c.set(Calendar.SECOND, seconds.nextSetBit(0));
	        	resetSec = true;
        	}
        }
        
        int h = c.get(Calendar.HOUR_OF_DAY);
        while (!hours.get(h)) {
        	c.add(Calendar.HOUR_OF_DAY, 1);
        	h = c.get(Calendar.HOUR_OF_DAY);
        	if (!resetMin) {
        		c.set(Calendar.MINUTE, minutes.nextSetBit(0));
        		resetMin = true;
        	}
        }

        nextDay(c);

        return c.getTimeInMillis();
    }

    // day
    private void nextDay(Calendar c) {
    	boolean resetHour = false;
        
        while (!daysOfMonth.get(c.get(Calendar.DAY_OF_MONTH) - 1) ||
        		!months.get(c.get(Calendar.MONTH)) ||
        		!daysOfWeek.get(c.get(Calendar.DAY_OF_WEEK) - 1)) {
        	c.add(Calendar.DAY_OF_MONTH, 1);
        	if (!resetHour) {
        		c.set(Calendar.HOUR_OF_DAY, hours.nextSetBit(0));
        		resetHour = true;
        	}
//            nextMonth(c);
        }
        
//    	if (!fitDayOfWeek(c))
//        	nextDay(c);
    }

    // month
    private void nextMonth(Calendar c) {

        int mon = c.get(Calendar.MONTH);
        int nextMon = months.nextSetBit(mon + 1);
        if (nextMon == -1) {
            c.add(Calendar.YEAR, 1);// next year
        	nextMon = months.nextSetBit(0);
        }
        c.set(Calendar.MONTH, nextMon);

    }

    private boolean fitMonth(Calendar c) {
        return months.get(c.get(Calendar.MONTH) - 1);
    }
    
    private boolean fitDayOfWeek(Calendar c) {
        int dow = c.get(Calendar.DAY_OF_WEEK);
        return daysOfWeek.get(dow - 1);
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
