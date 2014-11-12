package me.yugy.v2ex.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.LoginActivity;
import me.yugy.v2ex.dao.datahelper.AllNodesDataHelper;
import me.yugy.v2ex.sdk.V2EX;
import me.yugy.v2ex.utils.DebugUtils;
import me.yugy.v2ex.utils.TextUtils;
import me.yugy.v2ex.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.preference.Preference.OnPreferenceClickListener;

/**
 * Created by yugy on 14-2-26.
 */
public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static final String PREF_LOGIN = "pref_login";
    public static final String PREF_CONTACT = "pref_contact";
    public static final String PREF_UPDATE = "pref_check_update";
    public static final String PREF_SYNC = "pref_sync";

    private static final int REQUEST_CODE_LOGIN = 10086;

    private boolean mLogined = false;

    private AllNodesDataHelper mAllNodesDataHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());

        if(mLogined = getPreferenceManager().getSharedPreferences().contains("username")){
            String username = getPreferenceManager().getSharedPreferences().getString("username", null);
            if(username != null){
                findPreference(PREF_LOGIN).setTitle(username);
                findPreference(PREF_SYNC).setEnabled(true);
                long syncTime = getPreferenceManager().getSharedPreferences().getLong("sync_time", 0);
                if(syncTime != 0){
                    findPreference(PREF_SYNC).setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), syncTime));
                }
            }
        }

        findPreference(PREF_LOGIN).setOnPreferenceClickListener(this);
        findPreference(PREF_CONTACT).setOnPreferenceClickListener(this);
        findPreference(PREF_UPDATE).setOnPreferenceClickListener(this);
        findPreference(PREF_SYNC).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if(preference.getKey().equals(PREF_CONTACT)){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:me@yanghui.name"));
            if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
            else{
                AppMsg.makeText(getActivity(), "没有找到邮件程序", AppMsg.STYLE_CONFIRM).show();
            }
            return true;
        }else if(preference.getKey().equals(PREF_LOGIN)){
            if(mLogined){
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setMessage("你确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }else{
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_CODE_LOGIN);
            }
            return true;
        }else if(preference.getKey().equals(PREF_SYNC)){
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Syncing...", true, true);
            V2EX.getUserInfo(getActivity(), new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    DebugUtils.log(response);
                    try{
                        progressDialog.setMessage("Import Node Collections...");
                        JSONArray collectionsJson = response.getJSONObject("content").getJSONArray("collections");
                        String[] collections = new String[collectionsJson.length()];
                        for(int i = 0; i < collections.length; i++){
                            collections[i] = collectionsJson.getString(i);
                        }
                        mAllNodesDataHelper.removeCollections();
                        mAllNodesDataHelper.importCollections(collections);
                        long currentTimeMillis = System.currentTimeMillis();
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putLong("sync_time", currentTimeMillis)
                                .commit();
                        progressDialog.setMessage("Finished");
                        progressDialog.dismiss();
                        preference.setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), currentTimeMillis));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }else {
            return false;
        }
    }

    private void logout(){
        getPreferenceManager().getSharedPreferences().edit()
                .remove("username")
                .remove("reg_time")
                .remove("sync_time")
                .remove("token")
                .commit();
        V2EX.logout(getActivity());
        mLogined = false;
        findPreference(PREF_LOGIN).setTitle(getString(R.string.title_login));
        findPreference(PREF_SYNC).setEnabled(false);
        findPreference(PREF_SYNC).setSummary(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK){
            String username = data.getStringExtra("username");
            long syncTime = data.getLongExtra("sync_time", 0);
            mLogined = username != null;
            if(mLogined){
                findPreference(PREF_LOGIN).setTitle(username);
                findPreference(PREF_SYNC).setEnabled(true);
                if(syncTime != 0){
                    findPreference(PREF_SYNC).setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), syncTime));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
