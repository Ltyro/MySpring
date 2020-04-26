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

public class TaskScheduler implements Runnable{

    private BitSet seconds      = new BitSet(60);

    private BitSet minutes      = new BitSet(60);

    private BitSet hours        = new BitSet(24);

    private BitSet daysOfMonth  = new BitSet(31);

    private BitSet daysOfWeek   = new BitSet(7);

    private BitSet months       = new BitSet(12);

    private BitSet[] timeFields;

    private boolean end = false;

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);

    private final String ERROR_EXPRESSION = "error cron expression";

    private final String STAR = "*";

    public TaskScheduler() {
        timeFields = new BitSet[]{seconds, minutes, hours, daysOfMonth, months, daysOfWeek};
    }

    public void execute(Scheduled sa, Method m, Object o) {
        // int sec = 0, min = 0, hour = 0, day = 0, month = 0, weekday = 0;
        Time sec = new Time(0, false, TimeType.Second), min = new Time(0, false, TimeType.Minute),
                hour = new Time(0, false, TimeType.Hour), day = new Time(1, false, TimeType.Day),
                month = new Time(0, false, TimeType.Month), weekday = new Time(0, false, TimeType.Week);
        if (sa == null)
            return;
        String cron = sa.cron();
        if (StringUtil.isEmpty(cron))
            throw new NullPointerException("cron expression should not be null");
        String[] times = cron.split(" ");
        if (times.length != 6)
            throw new ScheduleException(ERROR_EXPRESSION);

        for (int i = 0; i < times.length; i++) {
            setTimeField(times[i], timeFields[i]);
        }
        // execute first time
        long firstTime = getFirstTimeFromBitSets();
        long delay = firstTime - System.currentTimeMillis();
        if (delay < 0)
            delay = getNextTime() - System.currentTimeMillis();
        executor.schedule(this, delay, TimeUnit.MILLISECONDS);
//        seconds.nextSetBit()
        Time timeArr[] = { sec, min, hour, day, month, weekday };
        executeSchedule(timeArr, m, o);
    }

    private void setTimeField(String time, BitSet timeField) {
        if (STAR.equals(time)) {
            timeField.set(0, timeField.size());
            return;
        }
        if (StringUtil.isNumberOnly(time)) {
            timeField.set(Integer.parseInt(time));
            return;
        }
        if (time.contains("-")) {
            String[] strs = time.split("-");
            int start = Integer.parseInt(strs[0]),
                    end = Integer.parseInt(strs[1]);
            timeField.set(start, end + 1);
            return;
        }
        if (time.contains("/")) {
            String[] strs = time.split("/");
            for (String s : strs) {
                timeField.set(Integer.parseInt(s));
            }
        }
    }

    private long getNextTime() {
        // TODO
        return 0;
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

//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//            invokeMethod(m, o);
//            t.cancel();
//            }
//        }, c.getTime());

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

    @Override
    public void run() {

    }
}
