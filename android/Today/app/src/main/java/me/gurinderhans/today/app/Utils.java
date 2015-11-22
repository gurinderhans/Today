package me.gurinderhans.today.app;

import org.joda.time.DateTime;

/**
 * Created by ghans on 11/22/15.
 */
public class Utils {

    public static final class NotificationAlarmTimes {

        /**
         * Constants
         */
        public static final DateTime MORNING = DateTime.now().withTimeAtStartOfDay().plusHours(7); // 07:00
        public static final DateTime AFTERNOON = MORNING.plusHours(5); // 12:00
        public static final DateTime EVENING = AFTERNOON.plusHours(6); // 18:00


        /**
         * Computes the next time for the notification alaram to be scheduled at
         *
         * @return - time for the alarm to be scheduled at
         */
        public static DateTime nextTime() {
            DateTime now = DateTime.now();

            long diffM = MORNING.getMillis() - now.getMillis();
            long diffA = AFTERNOON.getMillis() - now.getMillis();
            long diffE = EVENING.getMillis() - now.getMillis();

            if (diffM < 0 && diffA > 0 && diffE > 0)
                return AFTERNOON;

            if (diffA < 0 && diffM < 0 && diffE > 0)
                return EVENING;

            return MORNING.plusDays(1);
        }
    }
}
