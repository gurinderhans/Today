package me.gurinderhans.today;

import org.joda.time.DateTime;

/**
 * Created by ghans on 11/18/15.
 */
public final class Keys {
    public static final class PageFragmentKeys {
        public static final String TITLE = "title";
    }

    public static final class NotificationAlarmTimes {

        public static final String SNOOZE_NOTIFY_KEY = "snoozeAlarm";
        public static final String TODO_NOTIFICATION_ACTION_KEY = "me.gurinderhans.TODOS";

        // Constants
        public static final DateTime MORNING = DateTime.now().withTimeAtStartOfDay().plusHours(7); // 07:00
        public static final DateTime AFTERNOON = MORNING.plusHours(5); // 12:00
        public static final DateTime EVENING = AFTERNOON.plusHours(6); // 18:00


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
