package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.TopicActivity;
import me.yugy.v2ex.adapter.NotificationAdapter;
import me.yugy.v2ex.model.NotificationModel;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.utils.DebugUtils;
import me.yugy.v2ex.utils.MessageUtils;

/**
 * Created by yugy on 14-3-13.
 */
public class NotificationFragment extends Fragment{

    @InjectView(R.id.refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list_fragment_notification) ListView mListView;

    private ArrayList<NotificationModel> mModels;
    private String mToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.inject(this, rootView);
        mListView.setEmptyView(rootView.findViewById(R.id.txt_fragment_notification_empty));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mToken != null){
                    getNotificationData();
                }
            }
        });

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences.contains("username")){
            if(sharedPreferences.contains("token")){
                mToken = sharedPreferences.getString("token", null);
                getNotificationData();
            }else{
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Getting notification token...", true, false);
                V2EX.getNotificationToken(getActivity(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        DebugUtils.log(response);
                        if (getActivity() != null) {
                            try {
                                if (response.getString("result").equals("ok")) {
                                    progressDialog.setMessage("Save notification token...");
                                    mToken = response.getString("token");
                                    sharedPreferences.edit()
                                            .putString("token", mToken)
                                            .apply();
                                    progressDialog.dismiss();
                                    getNotificationData();
                                } else if (response.getString("result").equals("fail")) {
                                    progressDialog.setMessage("Get token fail");
                                    MessageUtils.toast(getActivity(), "Get token fail");
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    private void getNotificationData(){
        mSwipeRefreshLayout.setRefreshing(true);
        V2EX.getNotification(getActivity(), mToken, new TextHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                MessageUtils.toast(getActivity(), "Network error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Pattern entryPattern = Pattern.compile("<entry>[\\d\\D]+?</entry>");
                Matcher entryMatcher = entryPattern.matcher(responseBody);
                ArrayList<String> entries = new ArrayList<String>();
                while(entryMatcher.find()){
                    entries.add(entryMatcher.group());
                }
                mModels = new ArrayList<NotificationModel>();
                for(String entry : entries){
                    NotificationModel model = new NotificationModel();
                    try {
                        model.parse(entry);
                        mModels.add(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mListView.setAdapter(new NotificationAdapter(getActivity(), mModels));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @OnItemClick(R.id.list_fragment_notification)
    void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        Bundle argument = new Bundle();
        argument.putInt("topic_id", mModels.get(position).topicId);
        intent.putExtra("argument", argument);
        startActivity(intent);
    }
}
