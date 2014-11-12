package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.TopicActivity;
import me.yugy.v2ex.adapter.LoadingAdapter;
import me.yugy.v2ex.adapter.PersonTopicAdapter;
import me.yugy.v2ex.model.MemberModel;
import me.yugy.v2ex.model.TopicModel;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.utils.DebugUtils;
import me.yugy.v2ex.utils.ScreenUtils;
import me.yugy.v2ex.widget.AlphaForegroundColorSpan;
import me.yugy.v2ex.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by yugy on 14-2-25.
 */
public class UserFragment extends Fragment {

    private int mActionBarTitleColor;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private View mPlaceHolderView;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    @InjectView(R.id.refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list_fragment_user) ListView mListView;
    @InjectView(R.id.header_fragment_user) View mHeader;
    @InjectView(R.id.header_logo_fragment_user) ImageView mHeaderLogo;
    @InjectView(R.id.txt_fragment_user_name) TextView mName;
    @InjectView(R.id.txt_fragment_user_description) TextView mDescription;

    private MemberModel mMemberModel;
    private ArrayList<TopicModel> mModels;

    private boolean mDataLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = ScreenUtils.dp(getActivity(), 250);
        mMinHeaderTranslation = -mHeaderHeight + ScreenUtils.getActionBarHeight(getActivity());

        mActionBarTitleColor = getResources().getColor(android.R.color.white);

        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.inject(this, rootView);

        mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(mPlaceHolderView, null, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }
        });

        setupActionBar();

        if(getArguments().containsKey("model")){
            mMemberModel = getArguments().getParcelable("model");
            showData();
        }else if(getArguments().containsKey("username")){
            String username = getArguments().getString("username").substring(1);
            mName.setText(username);
            mDescription.setText("Loading...");
            V2EX.showUser(getActivity(), username, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    mMemberModel = new MemberModel();
                    try {
                        mMemberModel.parse(response);
                        showData();
                    } catch (JSONException e) {
                        AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                    super.onFailure(statusCode, headers, responseBody, e);
                }
            });
        }
    }

    private void showData(){
        mSpannableString = new SpannableString(mMemberModel.username);
        mName.setText(mMemberModel.username);
        mDescription.setText("V2EX 第 " + mMemberModel.id + " 号会员");
        ImageLoader.getInstance().displayImage(mMemberModel.avatar, mHeaderLogo);

        setupListView();
    }

    private void setupListView() {
        mListView.setAdapter(new LoadingAdapter(getActivity()));
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mHeaderLogo, getActionBarIconView(), mSmoothInterpolator.getInterpolation(ratio));
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
        getData(false);
    }

    private void getData(boolean forceRefresh){
        V2EX.showTopicByUsername(getActivity(), forceRefresh, mMemberModel.username,
            new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    DebugUtils.log(response);
                    try {
                        mModels = getModels(response);
                        mListView.setAdapter(new PersonTopicAdapter(getActivity(), mModels));
                        mDataLoaded = true;
                    } catch (JSONException e) {
                        AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                        e.printStackTrace();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    super.onFailure(statusCode, headers, responseBody, e);
                }
        });
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

    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(mSpannableString);
        mName.setAlpha(1.0f - alpha);
        mDescription.setAlpha(1.0f - alpha);
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void setupActionBar() {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_transparent);

        //getActionBarTitleView().setAlpha(0f);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) getActivity().findViewById(android.R.id.home);
    }

    @OnItemClick(R.id.list_fragment_user)
    public void onItemClick(int position) {
        if(mDataLoaded){
            Intent intent = new Intent(getActivity(), TopicActivity.class);
            Bundle argument = new Bundle();
            argument.putParcelable("model", mModels.get(position - 1));
            intent.putExtra("argument", argument);
            startActivity(intent);
        }
    }
}
