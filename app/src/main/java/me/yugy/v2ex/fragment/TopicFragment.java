package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.LoadingAdapter;
import me.yugy.v2ex.model.ReplyModel;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.utils.DebugUtils;
import me.yugy.v2ex.utils.MessageUtils;
import me.yugy.v2ex.utils.ScreenUtils;
import me.yugy.v2ex.widget.AppMsg;
import me.yugy.v2ex.widget.ReplyView;
import me.yugy.v2ex.widget.TopicView;

/**
 * Created by yugy on 14-2-24.
 */
public class TopicFragment extends Fragment{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TopicView mHeaderView;
    private ListView mListView;

    private TopicModel mTopicModel;
    private ArrayList<ReplyModel> mReplyModels;
    private int mTopicId;

    private boolean mLogined = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogined = PreferenceManager.getDefaultSharedPreferences(getActivity()).contains("username");
        if(mLogined){
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) rootView.findViewById(R.id.list_fragment_topic);
        mListView.setEmptyView(rootView.findViewById(R.id.progress_fragment_topic));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopicData();
            }
        });

        if(getArguments().containsKey("model")){
            mTopicModel = getArguments().getParcelable("model");
            mTopicId = mTopicModel.id;
            mHeaderView = new TopicView(getActivity());
            mHeaderView.setViewDetail();
            mHeaderView.parse(mTopicModel);
            mListView.addHeaderView(mHeaderView, mTopicModel, false);
            mListView.setAdapter(new LoadingAdapter(getActivity()));
            getReplyData();
        }else if(getArguments().containsKey("topic_id")){
            mTopicId = getArguments().getInt("topic_id");
            getTopicData();
        }
    }

    private void getTopicData(){
        V2EX.showTopicByTopicId(getActivity(), mTopicId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                DebugUtils.log(response);
                try {
                    if(mTopicModel == null){
                        mTopicModel = new TopicModel();
                    }
                    mTopicModel.parse(response.getJSONObject(0));
                    if(mHeaderView == null){
                        mHeaderView = new TopicView(getActivity());
                        mHeaderView.setViewDetail();
                        mListView.addHeaderView(mHeaderView, mTopicModel, false);
                    }
                    mHeaderView.parse(mTopicModel);
                    getReplyData();
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                if(getActivity() != null){
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                }
                e.printStackTrace();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public void onCommentFinish(JSONObject result){
        try {
            if(result.getString("result").equals("ok")){
                MessageUtils.toast(getActivity(), "Comment success");
                getReplyData();
            }else if(result.getString("result").equals("fail")){
                String errorContent = result.getJSONObject("content").getString("error_msg");
                MessageUtils.toast(getActivity(), errorContent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReplyData(){
        mSwipeRefreshLayout.setRefreshing(true);
        V2EX.getReplies(getActivity(), mTopicModel.id, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                DebugUtils.log(response);
                if(response.length() == 0){
                    mListView.setAdapter(new NoReplyAdapter());
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                try {
                    mReplyModels = getModels(response);
                    mListView.setAdapter(new TopicReplyAdapter(mReplyModels));
                    mSwipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                if(getActivity() != null){
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                }
                e.printStackTrace();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    private ArrayList<ReplyModel> getModels(JSONArray jsonArray) throws JSONException {
        ArrayList<ReplyModel> models = new ArrayList<ReplyModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            ReplyModel model = new ReplyModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_topic_comment:
                CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
                Bundle argument = new Bundle();
                argument.putInt("topic_id", mTopicId);
                commentDialogFragment.setArguments(argument);
                commentDialogFragment.show(getFragmentManager(), "comment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TopicReplyAdapter extends BaseAdapter{

        private ArrayList<ReplyModel> mModels;

        private TopicReplyAdapter(ArrayList<ReplyModel> models) {
            mModels = models;
        }

        @Override
        public int getCount() {
            return mModels.size();
        }

        @Override
        public ReplyModel getItem(int position) {
            return mModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ReplyView item = (ReplyView) convertView;
            if(item == null){
                item = new ReplyView(getActivity());
            }
            item.parse(mLogined, mTopicId, getItem(position));
            return item;
        }
    }

    private class NoReplyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getActivity());
            int padding = ScreenUtils.dp(getActivity(), 8);
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);
            textView.setText("No replies");
            return textView;
        }
    }
}
