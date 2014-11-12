package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.TopicActivity;
import me.yugy.v2ex.adapter.NewestNodeAdapter;
import me.yugy.v2ex.dao.datahelper.AllNodesDataHelper;
import me.yugy.v2ex.dao.datahelper.NewestNodeDataHelper;
import me.yugy.v2ex.model.NodeModel;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.tasker.AllNodesParseTask;
import me.yugy.v2ex.utils.DebugUtils;
import me.yugy.v2ex.utils.MessageUtils;

import static me.yugy.v2ex.adapter.NewestNodeAdapter.OnScrollToBottomListener;

/**
 * Created by yugy on 14-2-23.
 */
public class NewestNodeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnScrollToBottomListener{

    @InjectView(R.id.refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list_fragment_node) ListView mListView;
    private AllNodesDataHelper mAllNodesDataHelper;
    private NewestNodeDataHelper mNewestNodeDataHelper;
    private NewestNodeAdapter mNewestNodeAdapter;

    private int mPage;
    private boolean mLoadFromCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
        mNewestNodeDataHelper = new NewestNodeDataHelper(getActivity());
        mNewestNodeAdapter = new NewestNodeAdapter(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_node, container, false);
        ButterKnife.inject(this, rootView);
        mListView.setEmptyView(rootView.findViewById(R.id.progress_fragment_node));
        mListView.setAdapter(mNewestNodeAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getData();
            }
        });
        if(mAllNodesDataHelper.query().length == 0){
            mNewestNodeDataHelper.clear();
            getAllNodesData();
        }else{
            getNewestNodeData();
        }
    }

    private void getAllNodesData(){
        V2EX.getAllNode(getActivity(), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                DebugUtils.log(response);
                new AllNodesParseTask(getActivity()){
                    @Override
                    protected void onPostExecute(ArrayList<NodeModel> nodeModels) {
                        mAllNodesDataHelper.bulkInsert(nodeModels);
                        getNewestNodeData();
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                if(getActivity() != null) {
                    MessageUtils.toast(getActivity(), "Network error");
                }
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    private void getNewestNodeData(){
        if(mNewestNodeDataHelper.query().length == 0){
            mPage = 1;
            getData();
            mLoadFromCache = false;
        }else{
            mLoadFromCache = true;
        }
    }

    private void getData(){
        if(mPage == 1){
            mLoadFromCache = false;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        V2EX.getLatestTopics(getActivity(), mPage, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                DebugUtils.log(response);
                try {
                    if(mPage == 1){
                        mNewestNodeDataHelper.clear();
                    }
                    mNewestNodeDataHelper.bulkInsert(getModels(response));
                    mPage++;
                } catch (JSONException e) {
                    MessageUtils.toast(getActivity(), "Json decode error");
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                if(getActivity() != null) {
                    MessageUtils.toast(getActivity(), "Network error");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    @OnItemClick(R.id.list_fragment_node)
    void onItemClick(int position) {
        TopicModel topicModel = mNewestNodeAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        Bundle argument = new Bundle();
        argument.putParcelable("model", topicModel);
        intent.putExtra("argument", argument);
        startActivity(intent);
    }

    private ArrayList<TopicModel> getModels(JSONArray response) throws JSONException {
        ArrayList<TopicModel> models = new ArrayList<TopicModel>();
        for(int i = 0; i < response.length(); i++){
            TopicModel model = new TopicModel();
            model.parse(response.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mNewestNodeDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewestNodeAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewestNodeAdapter.changeCursor(null);
    }

    @Override
    public void onScrollToBottom() {
        if(!mLoadFromCache) {
            getData();
        }
    }
}
