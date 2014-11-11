package me.yugy.v2ex.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import me.yugy.v2ex.dao.datahelper.AllNodesDataHelper;
import me.yugy.v2ex.model.NodeModel;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.utils.MessageUtils;
import me.yugy.v2ex.widget.NodeView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-3-7.
 */
public class AllNodesAdapter extends CursorAdapter implements NodeView.OnAddButtonClickListener{

    private AllNodesDataHelper mAllNodesDataHelper;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private boolean mIsLogined;
    private String mRegTime;

    public AllNodesAdapter(Context context){
        super(context, null, false);
        mContext = context;
        mAllNodesDataHelper = new AllNodesDataHelper(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mIsLogined = mSharedPreferences.contains("username");
        mRegTime = mSharedPreferences.getString("reg_time", null);

    }

    @Override
    public NodeModel getItem(int position) {
        getCursor().moveToPosition(position);
        return NodeModel.fromCursor(getCursor());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new NodeView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NodeView nodeView = (NodeView) view;
        NodeModel nodeModel = NodeModel.fromCursor(cursor);
        nodeView.parse(nodeModel);
        nodeView.setOnAddButtonClickListener(this);
    }

    @Override
    public void onClick(final int nodeId, final boolean added) {
        if(mIsLogined){
            if(added){
                mProgressDialog = ProgressDialog.show(mContext, null, "Add to collections...", true, false);
            }else{
                mProgressDialog = ProgressDialog.show(mContext, null, "Remove from collections...", true, false);
            }
            if(mRegTime == null){
                V2EX.getRegTime(mContext, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if(response.getString("result").equals("ok")){
                                mRegTime = response.getString("reg_time");
                                mSharedPreferences.edit().putString("reg_time", mRegTime).commit();
                                syncCollection(nodeId, added, mRegTime);
                            }else if(response.getString("result").equals("fail")){
                                MessageUtils.toast(mContext, "Get reg time fail");
                                mProgressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                syncCollection(nodeId, added, mRegTime);
            }
        }else{
            mAllNodesDataHelper.setCollected(added, nodeId);
        }
    }

    private void syncCollection(final int nodeId, final boolean added, String regTime){
        V2EX.syncCollection(mContext, nodeId, regTime, added, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(response.getString("result").equals("ok")){
                        mProgressDialog.setMessage("OK");
                        mAllNodesDataHelper.setCollected(added, nodeId);
                    }else if(response.getString("result").equals("fail")){
                        mProgressDialog.setMessage("Fail");
                        MessageUtils.toast(mContext, "Sync collections failed.");
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
