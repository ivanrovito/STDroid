package com.anhuioss.crowdroid.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.MyDbAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author this activity is a list view for default setting
 */
public class DefaultSettingActivity extends Activity
{

    private List<Map<String, Object>> mData;

    private boolean[]                 flags = new boolean[7];

    MyDbAdapter                       myDb;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_default);
        this.myDb = new MyDbAdapter(this);
        this.myDb.open();
        this.mData = getData();
        MyAdapter adapter = new MyAdapter(this, this.mData);
        ListView lv = (ListView) findViewById(R.id.default_setting_listview);
        lv.setAdapter(adapter);
        Button confirm = (Button) findViewById(R.id.defualt_confirm_button);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                restoreSetting();
                DefaultSettingActivity.this.myDb.close();
                finish();
            }
        });

        //-----------------------
        // Init Flags
        //-----------------------
        for (int i = 0; i < this.flags.length; i++)
        {
            this.flags[i] = false;
        }
    }

    private List<Map<String, Object>> getData()
    {
        String[] DefaultSettingMenuNames =
        { getString(R.string.activity_defaultsetting_autorefresh),
                getString(R.string.activity_defaultsetting_multiupdate),
                getString(R.string.activity_defaultsetting_translate),
                getString(R.string.activity_defaultsetting_setwallpaper),
                getString(R.string.activity_defaultsetting_notification),
                getString(R.string.activity_defaultsetting_fontcolor),
                getString(R.string.activity_mainsetting_hashtag) };

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (String info : DefaultSettingMenuNames)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("info", info);
            list.add(map);
        }
        return list;
    }

    public final class ViewHolder
    {
        public TextView infoText;

        public CheckBox viewCheckBox;
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

    public class MyAdapter extends BaseAdapter
    {

        private LayoutInflater    mInflater;

        Context                   context;

        List<Map<String, Object>> data;

        ArrayList<ViewHolder>     viewHolderList = new ArrayList<ViewHolder>();

        public MyAdapter(Context context, List<Map<String, Object>> data)
        {
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return this.data.size();
        }

        @Override
        public Object getItem(int arg0)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                final ViewGroup parent)
        {

            ViewHolder holder = null;

            if (convertView == null)
            {

                holder = new ViewHolder();

                convertView = this.mInflater.inflate(
                        R.layout.activity_default_setting_item, null);
                holder.infoText = (TextView) convertView.findViewById(R.id.default_item_text);
                holder.viewCheckBox = (CheckBox) convertView.findViewById(R.id.default_item_checkbox);
                convertView.setTag(holder);

                this.viewHolderList.add(holder);

            }
            else
            {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.infoText.setText((String) this.data.get(position).get("info"));

            holder.viewCheckBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {

                    boolean check = MyAdapter.this.viewHolderList.get(position).viewCheckBox.isChecked();

                    //Change Flags
                    DefaultSettingActivity.this.flags[position] = check;

                }
            });
            return convertView;
        }

    }

    //-------------------------------------------------------------------------
    /**
     * Restore setting with default value
     */
    //-------------------------------------------------------------------------
    private void restoreSetting()
    {

        for (int i = 0; i < this.flags.length; i++)
        {

            boolean check = this.flags[i];
            switch (i)
            {

            case 0:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_REFRESH_TIME, "10");

                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_REFRESH_FLAG,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }
                break;

            case 1:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE_UID, "");

                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FOLLOW5_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FOLLOW5_UPDATE_UID, "");

                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_BUSINESS_UPDATE_UID,
                            "");

                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }
                break;

            case 2:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_AUTO_TRANSLATION,
                            MyDbAdapter.PARAM_VALUE_OFF);
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE,
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE);
                    this.myDb.deleteTranslation("-1");
                }
                break;

            case 3:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_IMAGE_PATH, "");
                }
                break;

            case 4:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_NOTIFICATION,
                            MyDbAdapter.PARAM_VALUE_OFF);
                }
                break;

            case 5:
                if (check)
                {
                    this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_FONT_COLOR, "-1");
                }
                break;

            case 6:
                if (check)
                {
                    this.myDb.deleteKeyword(-1); //delete all account
                    this.myDb.deleteKeyword_u(-1);
                }
                break;
            //			case 7:
            //				if (check) {
            //					myDb.deleteAccount(-1); //delete all account
            //				}

            default:
                break;
            }

        }

    }

}
