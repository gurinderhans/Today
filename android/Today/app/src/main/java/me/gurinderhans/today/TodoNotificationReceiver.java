package me.gurinderhans.today;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.WearableExtender;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import me.gurinderhans.today.Keys.NotificationAlarmTimes;

import static me.gurinderhans.today.Keys.NotificationAlarmTimes.AFTERNOON;
import static me.gurinderhans.today.Keys.NotificationAlarmTimes.MORNING;
import static me.gurinderhans.today.Keys.NotificationAlarmTimes.SNOOZE_NOTIFY_KEY;
import static me.gurinderhans.today.Keys.NotificationAlarmTimes.TODO_NOTIFICATION_ACTION_KEY;

/**
 * Created by ghans on 11/21/15.
 */
public class TodoNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = TodoNotificationReceiver.class.getSimpleName();
    private static final int TODOS_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getExtras().getBoolean(SNOOZE_NOTIFY_KEY)) { // snooze feature
            mNotificationManager.cancel(TODOS_NOTIFICATION_ID);
            createAlarm(context, DateTime.now().plusHours(1));
            return;
        }

        // ready up the next alarm
        createAlarm(context, NotificationAlarmTimes.nextTime());

        List<TodoItem> items = fetchTodayItems(context);
        if (items.size() > 0)
            generateNotification(context, items);
    }

    private void generateNotification(Context context, List<TodoItem> items) {

        Intent snoozeIntent = new Intent(TODO_NOTIFICATION_ACTION_KEY);
        snoozeIntent.putExtra(SNOOZE_NOTIFY_KEY, true);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

        String notificationTitle = createNotificationTitle(items.size());

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(notificationTitle)
                .setContentText(items.get(0).getText())
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                .setSmallIcon(R.drawable.ic_check_white_48dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{250, 250, 250, 250})
                .setLights(0x900606, 1000, 1000) // colorPrimary
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(0xed4c4c) // app accent color
                .setAutoCancel(true)
                .addAction(R.drawable.ic_snooze_black_24dp, "Snooze", pIntent)
                .extend(new WearableExtender()
                        .addAction(new Action(R.drawable.ic_snooze_white_48dp, "Snooze", pIntent)));

        NotificationCompat.InboxStyle multilineNotifyStyle = new NotificationCompat.InboxStyle();
        multilineNotifyStyle.setBigContentTitle(notificationTitle);
        for (TodoItem todo : items) multilineNotifyStyle.addLine(todo.getText());

        mNotifyBuilder.setStyle(multilineNotifyStyle);
        mNotificationManager.notify(TODOS_NOTIFICATION_ID, mNotifyBuilder.build());
    }

    private String createNotificationTitle(int numTodos) {
        if (NotificationAlarmTimes.nextTime() == MORNING) {
            // notification during evening
            return numTodos + " todos left for today";
        } else if (NotificationAlarmTimes.nextTime() == AFTERNOON) {
            // notification during morning
            return numTodos + " todos scheduled for today";
        } else {
            // notification during afternoon
            return numTodos + " todos still to go";
        }
    }

    private List<TodoItem> fetchTodayItems(Context context) {
        Realm realm = Realm.getInstance(context);

        DateTime now = DateTime.now();
        DateTime start = now.withTimeAtStartOfDay();
        DateTime end = start.plusDays(1);

        return realm.where(TodoItem.class)
                .between("setForDate", start.toDate(), end.toDate())
                .equalTo("done", false)
                .findAllSorted("createdAt", RealmResults.SORT_ORDER_DESCENDING);
    }

    public static void createAlarm(Context context, DateTime when) {

        Intent intent = new Intent(context, TodoNotificationReceiver.class);
        intent.setAction(TODO_NOTIFICATION_ACTION_KEY);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getMillis(), pendingIntent);
    }

}