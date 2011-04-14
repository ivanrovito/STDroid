package com.anhuioss.crowdroid.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemLongClickListener;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.info.UserInfo;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;
import com.anhuioss.crowdroid.util.ImageBuilder;
import com.anhuioss.crowdroid.util.MyDbAdapter;

public class SearchUserDialog extends Dialog
{

    private EditText            searchScreenName = null;

    private ImageButton         find             = null;

    private ProgressDialog      progress         = null;

    private ArrayList<UserInfo> mUserInfoList    = null;

    private ListView            list_strangles   = null;

    private Button              closeButton      = null;

    private Button              nextButton       = null;

    private Button              preButton        = null;

    private int                 currentPage      = 1;   //default is 1

    private MyDbAdapter         db;

    Handler                     mHandler         = new Handler() {
                                                     @Override
                                                     public void handleMessage(
                                                             Message msg)
                                                     {
                                                         //Close Progress Dialog
                                                         SearchUserDialog.this.progress.dismiss();
                                                         if (SearchUserDialog.this.mUserInfoList != null)
                                                         {
                                                             setprofile();
                                                         }

                                                         //Change Status
                                                         changeStatus();
                                                     }
                                                 };

    public SearchUserDialog(Context context, MyDbAdapter dbAdapter)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.setContentView(R.layout.dialog_find_and_search);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                R.drawable.mainsetting_ic_menu_search);

        this.db = dbAdapter;

        this.searchScreenName = (EditText) findViewById(R.id.keyword_edit);
        this.list_strangles = (ListView) findViewById(R.id.user_list);

        //------------------
        // Search
        //------------------
        this.find = (ImageButton) findViewById(R.id.search_button);
        this.find.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                String query = SearchUserDialog.this.searchScreenName.getText().toString();
                sendSearch(query, 1);
            }
        });

        //------------------
        // Close
        //------------------
        this.closeButton = (Button) findViewById(R.id.close_button);
        this.closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        //------------------
        // Next
        //------------------
        this.nextButton = (Button) findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                String query = SearchUserDialog.this.searchScreenName.getText().toString();
                sendSearch(query, SearchUserDialog.this.currentPage + 1);

            }
        });
        this.nextButton.setEnabled(false);

        //------------------
        // Pre
        //------------------
        this.preButton = (Button) findViewById(R.id.prev_button);
        this.preButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                String query = SearchUserDialog.this.searchScreenName.getText().toString();
                sendSearch(query, SearchUserDialog.this.currentPage - 1);
            }
        });
        this.preButton.setEnabled(false);
    }

    protected void setprofile()
    {
        // TODO Auto-generated method stub
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < this.mUserInfoList.size(); i++)
        {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(UserInfo.SCREENNAME,
                    this.mUserInfoList.get(i).getScreenName());
            data.add(item);
        }

        //Prepare Adapter
        SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                android.R.layout.simple_list_item_1, new String[]
                { UserInfo.SCREENNAME }, new int[]
                { android.R.id.text1 });

        this.list_strangles.setAdapter(adapter);

        this.list_strangles.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                // TODO Auto-generated method stub
                ProfileDialog pd = new ProfileDialog(getContext(),
                        SearchUserDialog.this.db);
                Bitmap bm = null;
                try
                {
                    bm = ImageBuilder.returnBitMap(SearchUserDialog.this.mUserInfoList.get(
                            position).getUserImageURL());
                }
                catch (IOException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                SearchUserDialog.this.mUserInfoList.get(position).setUserImage(
                        bm);
                pd.setInfo(SearchUserDialog.this.mUserInfoList.get(position));
                pd.show();
                return false;
            }
        });
    }

    protected void sendSearch(final String msg, final int page)
    {
        // TODO Auto-generated method stub
        if (msg != null)
        {
            this.progress = new ProgressDialog(getContext());
            this.progress.setIndeterminate(false);
            this.progress.show();

            this.db.getCurrentLoginAccountInfo();

            new Thread(new Runnable() {

                @SuppressWarnings("unchecked")
                @Override
                public void run()
                {
                    CommunicationHandlerResult result = null;

                    result = TwitterHandler.getFindPeopleInfo(page, msg);
                    SearchUserDialog.this.mUserInfoList = (ArrayList<UserInfo>) result.getData();

                    if (result != null && result.getResultCode() == 200)
                    {
                        SearchUserDialog.this.currentPage = page;
                    }

                    SearchUserDialog.this.mHandler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Change Diaplay According to Current Status
     */
    //-----------------------------------------------------------------------------
    public void changeStatus()
    {

        //		setTitle(userInfo.getScreenName() 
        //				+ getContext().getString(R.string.dialog_profile_status)
        //				+ " [" + getContext().getString(R.string.activity_timeline_currentpage)
        //				+ currentPage + "]");

        //Pre Button
        if (this.currentPage > 1)
        {
            this.preButton.setEnabled(true);
        }
        else
        {
            this.preButton.setEnabled(false);
        }

        //Next Button
        if (this.mUserInfoList != null && this.mUserInfoList.size() >= 15)
        {
            this.nextButton.setEnabled(true);
        }
        else
        {
            this.nextButton.setEnabled(false);
        }

    }

}
