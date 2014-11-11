package me.yugy.v2ex.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import me.yugy.v2ex.fragment.AllNodeFragment;
import me.yugy.v2ex.fragment.CollectionFragment;
import me.yugy.v2ex.fragment.NavigationDrawerFragment;
import me.yugy.v2ex.fragment.NewestNodeFragment;
import me.yugy.v2ex.fragment.NotificationFragment;
import me.yugy.v2ex.fragment.SettingFragment;

import me.yugy.v2ex.R;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NewestNodeFragment mNewestNodeFragment;
    private AllNodeFragment mAllNodeFragment;
    private NotificationFragment mNotificationFragment;
    private SettingFragment mSettingFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setIcon(R.drawable.ic_logo);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // update the main content by replacing fragments
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        switch (position){
            case 0:
                if(mNewestNodeFragment == null){
                    mNewestNodeFragment = new NewestNodeFragment();
                }
                fragmentTransaction.replace(R.id.container, mNewestNodeFragment);
                break;
            case 1:
                if(mAllNodeFragment == null){
                    mAllNodeFragment = new AllNodeFragment();
                }
                fragmentTransaction.replace(R.id.container, mAllNodeFragment);
                break;
            case 2:
                fragmentTransaction.replace(R.id.container, new CollectionFragment());
                break;
            case 3:
                if(mNotificationFragment == null){
                    mNotificationFragment = new NotificationFragment();
                }
                fragmentTransaction.replace(R.id.container, mNotificationFragment);
                break;
            case 4:
                if(mSettingFragment == null){
                    mSettingFragment = new SettingFragment();
                }
                fragmentTransaction.replace(R.id.container, mSettingFragment);
                break;
        }
        fragmentTransaction.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

}
