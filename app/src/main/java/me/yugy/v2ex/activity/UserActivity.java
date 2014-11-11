package me.yugy.v2ex.activity;

import android.os.Bundle;

import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.swipeback.SwipeBackActivity;
import me.yugy.v2ex.fragment.UserFragment;

/**
 * Created by yugy on 14-2-25.
 */
public class UserActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        if(savedInstanceState == null){
            UserFragment userFragment = new UserFragment();
            if(getIntent().hasExtra("argument")){
                userFragment.setArguments(getIntent().getBundleExtra("argument"));
            }else{
                Bundle argument = new Bundle();
                argument.putString("username", getIntent().getData().getPath());
                userFragment.setArguments(argument);
            }
            getFragmentManager().beginTransaction().add(R.id.container_activity_topic, userFragment).commit();
        }
    }

}