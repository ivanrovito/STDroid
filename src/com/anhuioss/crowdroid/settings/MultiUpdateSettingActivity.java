package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.AccountInfo;
import com.anhuioss.crowdroid.util.MyDbAdapter;

public class MultiUpdateSettingActivity extends Activity
{

    private CheckBox          twitter;

    private CheckBox          follow5;

    private CheckBox          crowdroidbusiness;

    private Spinner           sp_twitter;

    private Spinner           sp_follow5;

    private Spinner           sp_crowdroidbusiness;

    private Button            confirm;

    private Button            cancel;

    private String            check_twitter           = null;

    private String            check_crowdroidbusiness = null;

    private String            twitter_uid             = null;

    private String            follow5_uid             = null;

    private String            crowdroidbusiness_uid   = null;

    private MyDbAdapter       myDb;

    private ArrayList<String> account_list_follow5;

    private ArrayList<String> account_list_twitter;

    private ArrayList<String> account_list_crowdroidbusiness;

    ArrayList<AccountInfo>    accountList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_multi_update);

        this.twitter = (CheckBox) findViewById(R.id.twitter_checkbox);
        this.follow5 = (CheckBox) findViewById(R.id.follow5_checkbox);
        this.crowdroidbusiness = (CheckBox) findViewById(R.id.crowdroidbusiness_checkbox);
        this.confirm = (Button) findViewById(R.id.Button_confirm);
        this.cancel = (Button) findViewById(R.id.Button_cancel);
        this.sp_follow5 = (Spinner) findViewById(R.id.Spinner01_follow5_multi);
        this.sp_twitter = (Spinner) findViewById(R.id.Spinner01_twitter_multi);
        this.sp_crowdroidbusiness = (Spinner) findViewById(R.id.Spinner01_crowdroidbusiness_multi);

        this.myDb = new MyDbAdapter(this);

        this.myDb.open();
        this.check_twitter = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE);
        this.check_crowdroidbusiness = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE);

        if (this.check_twitter.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            this.twitter.setChecked(true);
        }
        else
        {
            this.twitter.setChecked(false);
        }
        if (this.check_crowdroidbusiness.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            this.crowdroidbusiness.setChecked(true);
        }
        else
        {
            this.crowdroidbusiness.setChecked(false);
        }

        this.account_list_twitter = getScreenNameList(IGeneral.SERVICE_NAME_TWITTER);

        ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, this.account_list_twitter);

        account_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.sp_twitter.setAdapter(account_adapter);

        ArrayAdapter<String> account_adapter_follow5 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                this.account_list_follow5);

        account_adapter_follow5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.sp_follow5.setAdapter(account_adapter_follow5);

        ArrayAdapter<String> account_adapter_crowdroidbusiness = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                this.account_list_crowdroidbusiness);

        account_adapter_follow5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.sp_crowdroidbusiness.setAdapter(account_adapter_crowdroidbusiness);

        this.sp_twitter.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                // TODO Auto-generated method stub
                MultiUpdateSettingActivity.this.twitter_uid = MultiUpdateSettingActivity.this.account_list_twitter.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });

        this.sp_follow5.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                MultiUpdateSettingActivity.this.follow5_uid = MultiUpdateSettingActivity.this.account_list_follow5.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });

        this.sp_crowdroidbusiness.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                MultiUpdateSettingActivity.this.crowdroidbusiness_uid = MultiUpdateSettingActivity.this.account_list_crowdroidbusiness.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });

        this.confirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (MultiUpdateSettingActivity.this.twitter.isChecked())
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE,
                            MyDbAdapter.PARAM_VALUE_ON);
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE_UID,
                            MultiUpdateSettingActivity.this.twitter_uid);
                }

                else
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }

                if (MultiUpdateSettingActivity.this.follow5.isChecked())
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FOLLOW5_UPDATE,
                            MyDbAdapter.PARAM_VALUE_ON);
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FOLLOW5_UPDATE_UID,
                            MultiUpdateSettingActivity.this.follow5_uid);
                }
                else
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FOLLOW5_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }
                if (MultiUpdateSettingActivity.this.crowdroidbusiness.isChecked())
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE,
                            MyDbAdapter.PARAM_VALUE_ON);
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE_UID,
                            MultiUpdateSettingActivity.this.crowdroidbusiness_uid);
                }
                else
                {
                    MultiUpdateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }

                MultiUpdateSettingActivity.this.myDb.close();
                finish();
            }
        });

        this.cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                MultiUpdateSettingActivity.this.myDb.close();
                finish();
            }
        });
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
        this.myDb.close();
    }

    private ArrayList<String> getScreenNameList(String service)
    {

        ArrayList<String> screenNameList = new ArrayList<String>();

        //Change SelectUserSpineer(uid,name,screen_name,access_token,token_secret);

        this.accountList = this.myDb.getAccountList(service, null);

        //Extract screen Name 
        for (AccountInfo account : this.accountList)
        {
            String screenName = account.getScreenName();
            screenNameList.add(screenName);
        }

        return screenNameList;
    }
}
