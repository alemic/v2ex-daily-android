package me.yugy.v2ex.activity;

import android.os.Bundle;

import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.swipeback.SwipeBackActivity;
import me.yugy.v2ex.fragment.NodeFragment;

/**
 * Created by yugy on 14-2-25.
 */
public class NodeActivity extends SwipeBackActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        if(savedInstanceState == null){
            NodeFragment nodeFragment = new NodeFragment();
            if(getIntent().hasExtra("argument")){
                nodeFragment.setArguments(getIntent().getBundleExtra("argument"));
            }
            getFragmentManager().beginTransaction().add(R.id.container_activity_topic, nodeFragment).commit();
        }
    }

}
