package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.MyDbAdapter;

/**
 * @author this activity is a list view for default setting
 */
public class KeywordSettingActivity extends Activity
{

    private List<Map<String, Object>> mData;

    private static final int          ADD      = 1;

    MyDbAdapter                       myDb;

    Cursor                            mCursor;

    EditText                          tagEditText;

    Handler                           mHandler = new Handler() {
                                                   @Override
                                                   public void handleMessage(
                                                           Message msg)
                                                   {
                                                       KeywordSettingActivity.this.mCursor.requery();
                                                       refreshList();
                                                   }
                                               };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hanshtag_setting);
        this.myDb = new MyDbAdapter(this);
        this.myDb.open();
        this.mData = getData();
        MyAdapter adapter = new MyAdapter(this, this.mData);
        ListView lv = (ListView) findViewById(R.id.show_hashtag_list);
        lv.setAdapter(adapter);
        Button confirm = (Button) findViewById(R.id.activity_hashtagsetting_add);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                showDialog(ADD);
            }
        });
    }

    protected void refreshList()
    {
        // TODO Auto-generated method stub
        //		myDb = new MyDbAdapter(this);
        //		myDb.open();
        this.mData = getData();
        MyAdapter adapter = new MyAdapter(this, this.mData);
        ListView lv = (ListView) findViewById(R.id.show_hashtag_list);
        lv.setAdapter(adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case 1:
            return showAdd(this);
        }
        return super.onCreateDialog(id);
    }

    private Dialog showAdd(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.activity_mainsetting_add_hashtag);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_hashtag_setting, null);
        builder.setView(textEntryView);
        this.tagEditText = (EditText) textEntryView.findViewById(R.id.tag_edit);
        //		TextView exampleText = (TextView) textEntryView.findViewById(R.id.example_text);
        //		exampleText.setText(R.string.activity_mainsetting_hash_tag_example1);

        builder.setPositiveButton(R.string.actvity_twittersetting_add,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (KeywordSettingActivity.this.tagEditText.getText().length() > 0)
                        {
                            String tag = KeywordSettingActivity.this.tagEditText.getText().toString();
                            KeywordSettingActivity.this.myDb.insertKeyword(tag);
                        }
                        Message message = new Message();
                        KeywordSettingActivity.this.mHandler.sendMessage(message);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }

    private List<Map<String, Object>> getData()
    {
        ArrayList<String[]> keywordsList = new ArrayList<String[]>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        this.mCursor = this.myDb.getKeywordsCursor();

        if (this.mCursor != null)
        {
            while (this.mCursor.moveToNext())
            {
                String[] value = new String[2];
                value[0] = this.mCursor.getString(this.mCursor.getColumnIndex(MyDbAdapter.FIELD_HASHTAG_ID));
                value[1] = this.mCursor.getString(this.mCursor.getColumnIndex(MyDbAdapter.FIELD_HASHTAG_TAG));
                keywordsList.add(value);
            }

            for (String info[] : keywordsList)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", info[0]);
                map.put("info", info[1]);
                list.add(map);
            }
            
            this.mCursor.close();
        }
        
        return list;
    }

    public final class ViewHolder
    {
        public TextView infoText;

        public Button   viewButton;
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

            if (this.data != null)
            {

                ViewHolder holder = null;

                if (convertView == null)
                {

                    holder = new ViewHolder();

                    convertView = this.mInflater.inflate(
                            R.layout.activity_hanshtag_setting_item, null);
                    holder.infoText = (TextView) convertView.findViewById(R.id.setting_hashtag_item);
                    holder.viewButton = (Button) convertView.findViewById(R.id.activity_hashtag_setting_delete);
                    convertView.setTag(holder);

                    this.viewHolderList.add(holder);

                }
                else
                {

                    holder = (ViewHolder) convertView.getTag();
                }

                holder.infoText.setText((String) this.data.get(position).get(
                        "info"));

                holder.viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        KeywordSettingActivity.this.myDb.deleteKeyword(Long.valueOf((String) MyAdapter.this.data.get(
                                position).get("id")));
                        //refresh list
                        Message message = new Message();
                        KeywordSettingActivity.this.mHandler.sendMessage(message);
                    }
                });
            }
            return convertView;
        }

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

}
