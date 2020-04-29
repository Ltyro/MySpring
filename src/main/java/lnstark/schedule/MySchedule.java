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

    @Scheduled(cron="20-40/5 29 * * 4 2,4")
    public void testSchedule() {
        System.out.println("dida");
    }


}
