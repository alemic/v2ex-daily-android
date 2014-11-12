package me.yugy.v2ex.widget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.NodeActivity;
import me.yugy.v2ex.activity.PhotoViewActivity;
import me.yugy.v2ex.activity.UserActivity;
import me.yugy.v2ex.model.MemberModel;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.network.AsyncImageGetter;

/**
 * Created by yugy on 14/11/12.
 */
public class TopicViewContainer {
    @InjectView(R.id.txt_view_topic_title) TextView mTitle;
    @InjectView(R.id.txt_view_topic_content) TextView mContent;
    @InjectView(R.id.img_view_topic_head) SelectorImageView mHead;
    @InjectView(R.id.txt_view_topic_name) TextView mName;
    @InjectView(R.id.txt_view_topic_time) RelativeTimeTextView mTime;
    @InjectView(R.id.txt_view_topic_replies) TextView mReplies;
    @InjectView(R.id.txt_view_topic_node) TextView mNode;

    private int mNodeId;
    private MemberModel mMember;

    public void setViewDetail(){
        mContent.setMaxLines(Integer.MAX_VALUE);
        mContent.setTextSize(16);
        mContent.setLineSpacing(3f, 1.2f);
        mContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void parse(TopicModel model){
        mTitle.setText(model.title);
        Spanned spanned = Html.fromHtml(model.contentRendered, new AsyncImageGetter(mContent), null);
        SpannableStringBuilder htmlSpannable;
        if(spanned instanceof SpannableStringBuilder){
            htmlSpannable = (SpannableStringBuilder) spanned;
        } else {
            htmlSpannable = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<String> imagePositions = new ArrayList<String>();
        for(ImageSpan currentSpan : spans){
            final String imageUrl = currentSpan.getSource();
            final int start = htmlSpannable.getSpanStart(currentSpan);
            final int end   = htmlSpannable.getSpanEnd(currentSpan);
            imagePositions.add(start + "," + end);
            imageUrls.add(imageUrl);
        }

        for(ImageSpan currentSpan : spans){
            final int start = htmlSpannable.getSpanStart(currentSpan);
            final int end   = htmlSpannable.getSpanEnd(currentSpan);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PhotoViewActivity.launch(mContent.getContext(), imagePositions.indexOf(start + "," + end), imageUrls);
                }
            };

            ClickableSpan[] clickSpans = htmlSpannable.getSpans(start, end, ClickableSpan.class);
            if(clickSpans != null && clickSpans.length != 0) {

                for(ClickableSpan c_span : clickSpans) {
                    htmlSpannable.removeSpan(c_span);
                }
            }

            htmlSpannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mContent.setText(spanned);

        mName.setText(model.member.username);
        mTime.setReferenceTime(model.created * 1000);
        mReplies.setText(model.replies + " 个回复");
        mNode.setText(model.node.title);

        mMember = model.member;
        mNodeId = model.node.id;

        ImageLoader.getInstance().displayImage(model.member.avatar, mHead);
    }

    @OnClick(R.id.img_view_topic_head)
    void onHeadIconClick(){
        Intent intent = new Intent(mContent.getContext(), UserActivity.class);
        Bundle argument = new Bundle();
        argument.putParcelable("model", mMember);
        intent.putExtra("argument", argument);
        mContent.getContext().startActivity(intent);
    }

    @OnClick(R.id.txt_view_topic_node)
    void onNodeClick(){
        Intent intent = new Intent(mContent.getContext(), NodeActivity.class);
        Bundle argument = new Bundle();
        argument.putInt("node_id", mNodeId);
        intent.putExtra("argument", argument);
        mContent.getContext().startActivity(intent);
    }

}
