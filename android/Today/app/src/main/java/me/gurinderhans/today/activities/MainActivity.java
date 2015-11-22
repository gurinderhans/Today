package me.gurinderhans.today.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import me.gurinderhans.today.R;
import me.gurinderhans.today.app.TodayApplication;
import me.gurinderhans.today.fragments.todofragment.controller.TodoFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.today_pager);
        pager.setAdapter(new TodoFragmentPagerAdapter(getSupportFragmentManager()));

        TodayApplication application = (TodayApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Image~MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
