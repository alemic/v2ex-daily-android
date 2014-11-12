package me.yugy.v2ex.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import butterknife.ButterKnife;
import me.yugy.v2ex.R;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.widget.TopicViewContainer;

/**
 * Created by yugy on 14-3-14.
 */
public class NewestNodeAdapter extends CursorAdapter{

    private Context mContext;
    private OnScrollToBottomListener mListener;

    public NewestNodeAdapter(Context context, OnScrollToBottomListener listener) {
        super(context, null, false);
        mContext = context;
        mListener = listener;
    }

    @Override
    public TopicModel getItem(int position) {
        getCursor().moveToPosition(position);
        return TopicModel.fromCursor(getCursor(), mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_topic, parent, false);
        TopicViewContainer container = new TopicViewContainer();
        ButterKnife.inject(container, view);
        view.setTag(container);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TopicViewContainer container = (TopicViewContainer) view.getTag();
        TopicModel topicModel = TopicModel.fromCursor(cursor, context);
        container.parse(topicModel);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == getCount() - 1){
            if(mListener != null){
                mListener.onScrollToBottom();
            }
        }
        return super.getView(position, convertView, parent);
    }

    public static interface OnScrollToBottomListener{
        public void onScrollToBottom();
    }
}
