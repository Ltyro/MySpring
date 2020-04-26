package lnstark.schedule;


import lnstark.annotations.Component;
import lnstark.schedule.annos.EnableScheduling;
import lnstark.schedule.annos.Scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务
 */
@EnableScheduling
@Component
public class MySchedule {

    @Scheduled(cron="* 5 15 26 4 *")
    public void testSchedule() {
        System.out.println("dida");
    }

    public static void main(String[] args) {
        Timer t = new Timer();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 35);
        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.DATE, 21);
        c.set(Calendar.DAY_OF_WEEK, 3);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
                t.cancel();
            }
        }, c.getTime());

        System.out.println("after setting schedule");
    }

}
