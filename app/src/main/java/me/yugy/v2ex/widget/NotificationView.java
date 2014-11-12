package me.yugy.v2ex.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.yugy.v2ex.R;
import me.yugy.v2ex.model.NotificationModel;
import me.yugy.v2ex.network.AsyncImageGetter;

/**
 * Created by yugy on 14-3-14.
 */
public class NotificationView extends RelativeLayout{
    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private RelativeTimeTextView mTime;
    private TextView mContent;

    private void init(){
        inflate(getContext(), R.layout.view_notification, this);
        mTitle = (TextView) findViewById(R.id.txt_view_notification_title);
        mTime = (RelativeTimeTextView) findViewById(R.id.txt_view_notification_time);
        mContent = (TextView) findViewById(R.id.txt_view_notification_content);
    }

    public void parse(NotificationModel model){
        mTitle.setText(model.title);
        mTime.setReferenceTime(model.time);
        mContent.setText(Html.fromHtml(model.content, new AsyncImageGetter(mContent), null));
    }
}
