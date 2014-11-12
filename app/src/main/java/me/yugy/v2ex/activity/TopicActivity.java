package me.yugy.v2ex.activity;

import android.os.Bundle;

import org.json.JSONObject;

import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.swipeback.SwipeBackActivity;
import me.yugy.v2ex.fragment.TopicFragment;

import static me.yugy.v2ex.fragment.PostCommentDialogFragment.OnCommentFinishListener;

/**
 * Created by yugy on 14-2-24.
 */
public class TopicActivity extends SwipeBackActivity implements OnCommentFinishListener {

    private TopicFragment mTopicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        if(savedInstanceState == null){
            mTopicFragment = new TopicFragment();
            if(getIntent().hasExtra("argument")){
                mTopicFragment.setArguments(getIntent().getBundleExtra("argument"));
            }
            getFragmentManager().beginTransaction().add(R.id.container_activity_topic, mTopicFragment).commit();
        }
    }

    @Override
    public void onCommentFinished(JSONObject result) {
        if(mTopicFragment != null){
            mTopicFragment.onCommentFinish(result);
        }
    }
}
