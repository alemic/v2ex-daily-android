package me.yugy.v2ex.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;

import me.yugy.v2ex.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by yugy on 14-1-29.
 */
public class BaseActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("");
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
}
