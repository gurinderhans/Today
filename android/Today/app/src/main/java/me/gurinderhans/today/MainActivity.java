package me.gurinderhans.today;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.today_pager);
        pager.setPageTransformer(true, new PagerDepthTransformer());
        pager.setAdapter(new TodayPagerAdapter(getSupportFragmentManager()));
    }
}
