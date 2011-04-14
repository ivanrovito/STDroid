package com.anhuioss.crowdroid.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.info.UserInfo;
import com.anhuioss.crowdroid.twitter.RegisterTwitterAccountActivity;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.MyDbAdapter;

public class TwitterSettingActivity extends Activity
{
    private static final int ADD      = 1;

    private static final int DELETE   = 3;

    private long             selectid;

    private Button           add_button;

    private RadioButton      oath_radio;

    private RadioButton      basic_radio;

    private EditText         edit_text;

    private MyDbAdapter      myDbAdapter;

    private Cursor           myCursor;

    private EditText         userid;

    private EditText         passwd;

    private String           service  = "twitter";

    private ListView         listView = null;

    private Button           twitter_OK;

    private static String    proxy_url;

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Created.
     */
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_twitter);

        this.add_button = (Button) findViewById(R.id.add_twitter);
        this.oath_radio = (RadioButton) findViewById(R.id.oath);
        this.basic_radio = (RadioButton) findViewById(R.id.basic);
        this.edit_text = (EditText) findViewById(R.id.basic_url);
        this.listView = (ListView) findViewById(R.id.show_twitter_account);
        this.twitter_OK = (Button) findViewById(R.id.add_twitter_ok);

        this.myDbAdapter = new MyDbAdapter(this);
        this.myDbAdapter.open();
        this.myCursor = this.myDbAdapter.getAccountCursor(this.service, null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, this.myCursor,
                new String[]
                { "name" }, new int[]
                { android.R.id.text1 });
        this.listView.setAdapter(adapter);

        final String status_proxy = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY);

        if (status_proxy.equals(MyDbAdapter.PARAM_VALUE_OFF))
        {
            this.oath_radio.setChecked(true);
            this.basic_radio.setChecked(false);
            this.edit_text.setVisibility(View.GONE);
        }
        else
        {
            this.oath_radio.setChecked(false);
            this.basic_radio.setChecked(true);
            this.edit_text.setVisibility(View.VISIBLE);
            String apiserver = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER);
            if (apiserver != null)
            {
                this.edit_text.setText(apiserver);
            }
            //		 proxy_url = edit_text.getText().toString();
        }
        this.twitter_OK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (TwitterSettingActivity.this.oath_radio.isChecked())
                {
                    TwitterSettingActivity.this.myDbAdapter.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }
                else
                {
                    TwitterSettingActivity.this.myDbAdapter.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY,
                            MyDbAdapter.PARAM_VALUE_ON);

                }
                finish();
            }
        });
        this.oath_radio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                TwitterSettingActivity.this.edit_text.setVisibility(View.GONE);
            }
        });
        this.basic_radio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                TwitterSettingActivity.this.edit_text.setVisibility(View.VISIBLE);
            }
        });
        this.add_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                //OAuth
                if (TwitterSettingActivity.this.oath_radio.isChecked())
                {
                    Intent intent = new Intent();
                    intent.putExtra(
                            RegisterTwitterAccountActivity.KEY_API_SERVER,
                            TwitterHandler.TWITTER_ORIGINAL_API_SERVER);
                    intent.setClass(TwitterSettingActivity.this,
                            RegisterTwitterAccountActivity.class);
                    startActivity(intent);

                }
                //Basic
                else if (TwitterSettingActivity.this.basic_radio.isChecked())
                {

                    proxy_url = TwitterSettingActivity.this.edit_text.getText().toString();
                    if (proxy_url == null)
                    {
                        Toast textToast = Toast.makeText(
                                TwitterSettingActivity.this, "",
                                Toast.LENGTH_LONG);
                        textToast.show();
                    }
                    showDialog(ADD);
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                if (position >= 0)
                {
                    TwitterSettingActivity.this.selectid = id;
                }
                showDialog(DELETE);
                return false;
            }

        });

    }

    //
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case 1:
            return showAdd(this);
        case 3:
            return showDelete(this);
        }
        return super.onCreateDialog(id);
    }

    private Dialog showAdd(Context context)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.actvity_twittersetting_Addaccount);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_add_follow5, null);
        builder.setView(textEntryView);
        this.userid = (EditText) textEntryView.findViewById(R.id.add_follow5_username);
        this.passwd = (EditText) textEntryView.findViewById(R.id.add_follow5_passwd);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                registerToken(
                        TwitterSettingActivity.this.userid.getText().toString(),
                        TwitterSettingActivity.this.passwd.getText().toString());
                TwitterSettingActivity.this.myDbAdapter.updateSetting(
                        MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER,
                        proxy_url);
            }
        });
        return builder.create();
    }

    private Dialog showDelete(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.activity_ccountsetting_deleteaccount);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TwitterSettingActivity.this.myDbAdapter.deleteAccount(TwitterSettingActivity.this.selectid);
                        TwitterSettingActivity.this.myCursor.requery();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        this.myCursor.requery();
        return builder.create();
    }

    /**
     *Register user's message into DB
     * 
     * @param accessToken
     *            the access token to register
     *@param tokenSecret
     *            the token secret to register
     *@return true if success, false otherwise
     */
    private void registerToken(String username, String password)
    {
        TwitterHandler.setAccount(username, password,
                TwitterHandler.AUTH_TYPE_BASIC, proxy_url);
        UserInfo userInfo = (UserInfo) TwitterHandler.verifyUser(username,
                password, TwitterHandler.AUTH_TYPE_BASIC, proxy_url).getData();
        if (userInfo != null)
        {

            this.myDbAdapter.updateSetting(
                    MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY, "on");
            this.myDbAdapter.updateSetting(
                    MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER,
                    proxy_url);
            String uid = userInfo.getUid();
            String screenName = userInfo.getScreenName();
            if (this.myDbAdapter.updateAccount1(uid,
                    IGeneral.SERVICE_NAME_TWITTER, screenName, screenName,
                    this.passwd.getText().toString(), screenName,
                    this.passwd.getText().toString()))
            {
                //Updated
                Toast.makeText(getApplicationContext(),
                        getString(R.string.activity_register_overwrited),
                        Toast.LENGTH_SHORT).show();

            }
            else
            {
                this.myDbAdapter.insertAccount(uid,
                        IGeneral.SERVICE_NAME_TWITTER, screenName, screenName,
                        this.passwd.getText().toString(), screenName,
                        this.passwd.getText().toString());
            }

            setResult(RESULT_OK);
            this.myCursor.requery();

        }
        else
        {

            //Show Toast
            Toast.makeText(getApplicationContext(),
                    getString(R.string.activity_register_failed),
                    Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.myDbAdapter.open();
        this.myCursor.requery();
    }

    //-------------------------------------------------------------------
    /**
     * Called when Activity was Stopped
     */
    //-------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        this.myDbAdapter.close();
    }

}
