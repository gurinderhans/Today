package me.gurinderhans.today;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import me.gurinderhans.today.Keys.NotificationAlarmTimes;

import static me.gurinderhans.today.Keys.NotificationAlarmTimes.NOTIFICATION_ACTION_KEY;
import static me.gurinderhans.today.Keys.NotificationAlarmTimes.SNOOZE_NOTIF_KEY;

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

        if (intent.getExtras().getBoolean(SNOOZE_NOTIF_KEY)) { // snooze feature
            mNotificationManager.cancel(TODOS_NOTIFICATION_ID);
            createAlarm(context, DateTime.now().plusHours(1));
            return;
        }

        // ready up the next alarm
        createAlarm(context, NotificationAlarmTimes.NEXT());

        List<TodoItem> items = fetchTodayItems(context);
        if (items.size() > 0)
            generateNotification(context, items);
    }

    private void generateNotification(Context context, List<TodoItem> items) {

        Intent snoozeIntent = new Intent(NOTIFICATION_ACTION_KEY);
        snoozeIntent.putExtra(SNOOZE_NOTIF_KEY, true);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(items.size() + " todos")
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
                .extend(new NotificationCompat.WearableExtender()
                                .addAction(new NotificationCompat.Action(R.drawable.ic_snooze_white_48dp, "Snooze", pIntent))
                );

        NotificationCompat.InboxStyle multilineNotifStyle = new NotificationCompat.InboxStyle();
        multilineNotifStyle.setBigContentTitle(items.size() + " todos");
        for (TodoItem todo : items) multilineNotifStyle.addLine(todo.getText());

        mNotifyBuilder.setStyle(multilineNotifStyle);
        mNotificationManager.notify(TODOS_NOTIFICATION_ID, mNotifyBuilder.build());
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
        intent.setAction(NOTIFICATION_ACTION_KEY);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getMillis(), pendingIntent);
    }

}
