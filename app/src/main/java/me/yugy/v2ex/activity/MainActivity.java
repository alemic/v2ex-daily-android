package me.yugy.v2ex.activity;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import me.yugy.v2ex.R;
import me.yugy.v2ex.fragment.AllNodeFragment;
import me.yugy.v2ex.fragment.CollectionFragment;
import me.yugy.v2ex.fragment.NavigationDrawerFragment;
import me.yugy.v2ex.fragment.NewestNodeFragment;
import me.yugy.v2ex.fragment.NotificationFragment;
import me.yugy.v2ex.fragment.SettingFragment;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NewestNodeFragment mNewestNodeFragment;
    private AllNodeFragment mAllNodeFragment;
    private NotificationFragment mNotificationFragment;
    private SettingFragment mSettingFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
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
        if(mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
