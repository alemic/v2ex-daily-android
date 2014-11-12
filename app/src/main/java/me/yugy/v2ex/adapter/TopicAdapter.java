package me.yugy.v2ex.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.yugy.v2ex.R;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.widget.TopicViewContainer;

/**
 * Created by yugy on 14-2-25.
 */
public class TopicAdapter extends BaseAdapter {

    private ArrayList<TopicModel> mModels;

    /**
     *
     * @param models
     */
    public TopicAdapter(ArrayList<TopicModel> models) {
        mModels = models;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public TopicModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TopicViewContainer container;
        if(view == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_topic, parent, false);
            container = new TopicViewContainer();
            ButterKnife.inject(container, view);
            view.setTag(container);
        }else{
            container = (TopicViewContainer) view.getTag();
        }
        container.parse(getItem(position));
        return view;
    }
}
