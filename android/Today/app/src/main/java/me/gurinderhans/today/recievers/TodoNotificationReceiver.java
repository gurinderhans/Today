package me.gurinderhans.today.recievers;

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
import me.gurinderhans.today.R;
import me.gurinderhans.today.activities.MainActivity;
import me.gurinderhans.today.app.Utils;
import me.gurinderhans.today.fragments.todofragment.model.TodoItem;

import static me.gurinderhans.today.app.Keys.NotificationAlarmKeys.SNOOZE_NOTIFY_KEY;
import static me.gurinderhans.today.app.Keys.NotificationAlarmKeys.TODO_NOTIFICATION_ACTION_KEY;
import static me.gurinderhans.today.app.Keys.PagerTab.TODAY;
import static me.gurinderhans.today.app.Utils.NotificationAlarmTimes.AFTERNOON;
import static me.gurinderhans.today.app.Utils.NotificationAlarmTimes.MORNING;

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
            Utils.NotificationAlarmTimes.createAlarm(context, DateTime.now().plusHours(1));
            return;
        }

        // ready up the next alarm
        Utils.NotificationAlarmTimes.createAlarm(context, Utils.NotificationAlarmTimes.nextTime());

        Realm realm = Realm.getInstance(context);
        List<TodoItem> items = Utils.fetchAdapterData(realm, TODAY.title);
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
                .addAction(R.drawable.ic_snooze_white_24dp,
                        "Snooze", pIntent)
                .extend(new WearableExtender()
                        .addAction(new Action(R.drawable.ic_snooze_white_48dp, "Snooze", pIntent)));

        NotificationCompat.InboxStyle multilineNotifyStyle = new NotificationCompat.InboxStyle();
        multilineNotifyStyle.setBigContentTitle(notificationTitle);
        for (TodoItem todo : items) multilineNotifyStyle.addLine(todo.getText());

        mNotifyBuilder.setStyle(multilineNotifyStyle);
        mNotificationManager.notify(TODOS_NOTIFICATION_ID, mNotifyBuilder.build());
    }

    private String createNotificationTitle(int numTodos) {
        if (Utils.NotificationAlarmTimes.nextTime() == MORNING) {
            // notification during evening
            return numTodos + " todos left for today";
        } else if (Utils.NotificationAlarmTimes.nextTime() == AFTERNOON) {
            // notification during morning
            return numTodos + " todos scheduled for today";
        } else {
            // notification during afternoon
            return numTodos + " todos still to go";
        }
    }
}
