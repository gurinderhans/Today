package me.gurinderhans.today.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.joda.time.DateTime;

import me.gurinderhans.today.fragments.todofragment.controller.TodoFragment;
import me.gurinderhans.today.recievers.TodoNotificationReceiver;

import static me.gurinderhans.today.app.Keys.NotificationAlarmKeys.TODO_NOTIFICATION_ACTION_KEY;

/**
 * Created by ghans on 11/22/15.
 */
public class Utils {

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            Log.e(TodoFragment.TAG, e.toString(), e);
        }
    }

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

        public static void createAlarm(Context context, DateTime when) {

            Intent intent = new Intent(context, TodoNotificationReceiver.class);
            intent.setAction(TODO_NOTIFICATION_ACTION_KEY);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, when.getMillis(), pendingIntent);
        }
    }
}
