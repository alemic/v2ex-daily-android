package me.yugy.v2ex.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.PhotoViewActivity;
import me.yugy.v2ex.activity.UserActivity;
import me.yugy.v2ex.fragment.CommentDialogFragment;
import me.yugy.v2ex.model.MemberModel;
import me.yugy.v2ex.model.ReplyModel;
import me.yugy.v2ex.network.AsyncImageGetter;

/**
 * Created by yugy on 14/11/12.
 */
public class ReplyViewContainer {

    @InjectView(R.id.img_view_reply_head) SelectorImageView mHead;
    @InjectView(R.id.btn_view_reply_reply) TextView mName;
    @InjectView(R.id.txt_view_reply_name) ImageButton mReply;
    @InjectView(R.id.txt_view_reply_time) RelativeTimeTextView mTime;
    @InjectView(R.id.txt_view_reply_content) TextView mContent;

    private MemberModel mMember;
    private int mTopicId;

    public void parse(boolean logined, int topicId, ReplyModel replyModel){
        if(logined){
            mReply.setVisibility(View.VISIBLE);
        }else{
            mReply.setVisibility(View.INVISIBLE);
        }
        mTopicId = topicId;
        mName.setText(replyModel.member.username);
        mTime.setReferenceTime(replyModel.created * 1000);

        Spanned spanned = Html.fromHtml(replyModel.contentRendered, new AsyncImageGetter(mContent), null);
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
//            final String imageUrl = currentSpan.getSource();
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
        mContent.setMovementMethod(LinkMovementMethod.getInstance());

        mMember = replyModel.member;

        ImageLoader.getInstance().displayImage(replyModel.member.avatar, mHead);
    }

    @OnClick(R.id.img_view_reply_head)
    void onHeadIconClick(){
        Intent intent = new Intent(mContent.getContext(), UserActivity.class);
        Bundle argument = new Bundle();
        argument.putParcelable("model", mMember);
        intent.putExtra("argument", argument);
        mContent.getContext().startActivity(intent);
    }

    @OnClick(R.id.btn_view_reply_reply)
    void onReplyClick(){
        CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
        Bundle argument = new Bundle();
        argument.putInt("topic_id", mTopicId);
        argument.putString("comment_content", "@" + mMember.username + " ");
        commentDialogFragment.setArguments(argument);
        commentDialogFragment.show(((Activity)mContent.getContext()).getFragmentManager(), "comment");
    }
}
