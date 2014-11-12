package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.CollectionPagerAdapter;
import me.yugy.v2ex.dao.datahelper.AllNodesDataHelper;
import me.yugy.v2ex.model.NodeModel;

/**
 * Created by yugy on 14-2-25.
 */
public class CollectionFragment extends Fragment{

    @InjectView(R.id.tab_fragment_collection) PagerSlidingTabStrip mPagerSlidingTabStrip;
    @InjectView(R.id.viewpager_fragment_collection) ViewPager mViewPager;
    @InjectView(R.id.txt_fragment_collection_empty) TextView mEmptyText;

    private AllNodesDataHelper mAllNodesDataHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NodeModel[] collections = mAllNodesDataHelper.getCollections();
        if(collections.length != 0){
            mViewPager.setAdapter(new CollectionPagerAdapter(getFragmentManager(), collections));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        }else{
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

}

