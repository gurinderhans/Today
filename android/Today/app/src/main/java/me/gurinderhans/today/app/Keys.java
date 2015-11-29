package me.gurinderhans.today.app;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import me.gurinderhans.today.fragments.todofragment.model.TodoItem;

import static me.gurinderhans.today.app.Keys.PagerTab.TODAY;
import static me.gurinderhans.today.app.Keys.PagerTab.TOMORROW;

/**
 * Created by ghans on 11/18/15.
 */
public final class Keys {

    public static final class TodoFragmentKeys {
        public static final String TITLE = "title";
    }

    public static final class NotificationAlarmKeys {
        public static final String SNOOZE_NOTIFY_KEY = "snoozeAlarm";
        public static final String TODO_NOTIFICATION_ACTION_KEY = "me.gurinderhans.TODOS";
    }

    public enum PagerTab {
        TODAY(0, "today"),
        TOMORROW(1, "tomorrow"),
        SOMEDAY(2, "someday");

        public final String title;
        public final int index;

        PagerTab(int tabIndex, String title) {
            this.title = title;
            this.index = tabIndex;
        }

        @NonNull
        public static PagerTab getTabWithIndex(int index) {
            for (PagerTab tab : PagerTab.values()) {
                if (tab.index == index)
                    return tab;
            }
            return TODAY;
        }
    }

}
