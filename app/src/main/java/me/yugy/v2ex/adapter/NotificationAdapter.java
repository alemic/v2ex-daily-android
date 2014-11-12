package me.yugy.v2ex.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import me.yugy.v2ex.model.NotificationModel;
import me.yugy.v2ex.widget.NotificationView;

import java.util.ArrayList;

/**
 * Created by yugy on 14-3-14.
 */
public class NotificationAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<NotificationModel> mModels;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> models){
        mContext = context;
        mModels = models;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationView item = (NotificationView) convertView;
        if(item == null){
            item = new NotificationView(mContext);
        }
        item.parse(getItem(position));
        return item;
    }
}
