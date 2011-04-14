package com.anhuioss.crowdroid.dialog;

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
import com.anhuioss.crowdroid.info.SearchInfo;
import com.anhuioss.crowdroid.info.UserInfo;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.AccountInfo;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;
import com.anhuioss.crowdroid.util.ImageBuilder;
import com.anhuioss.crowdroid.util.MyDbAdapter;

//-----------------------------------------------------------------------------------
/**
 * This Dialog is for searching TimeLine which is specified with keyword.
 */
//-----------------------------------------------------------------------------------
public class SearchInfoDialog extends Dialog
{
    private ProgressDialog        progress;

    private EditText              keywordText;

    private ImageButton           searchButton;

    private Button                preButton;

    private Button                nextButton;

    private AccountInfo           accountInfo;

    private int                   currentPage = 1; //default is 0

    private ArrayList<SearchInfo> searchInfoList;

    private MyDbAdapter           db;

    Handler                       mHandler    = new Handler() {
                                                  @Override
                                                  public void handleMessage(
                                                          Message msg)
                                                  {
                                                      //Close Progress Dialog
                                                      SearchInfoDialog.this.progress.dismiss();

                                                      if (SearchInfoDialog.this.searchInfoList != null)
                                                      {
                                                          setListView();
                                                      }

                                                      changeStatus();
                                                  }
                                              };

    //-----------------------------------------------------------------------------------
    /**
     * Constructor
     */
    //-----------------------------------------------------------------------------------
    public SearchInfoDialog(Context context, String keyword,
            MyDbAdapter dbAdapter)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.setContentView(R.layout.dialog_searchinfo);

        //Set Title
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                R.drawable.mainsetting_ic_menu_search);
        setTitle(context.getString(R.string.dialog_searchinfo_searchsometing));

        this.db = dbAdapter;

        //---------------------------------
        // Init Views
        //---------------------------------
        this.keywordText = (EditText) findViewById(R.id.EditText_searchinfo);
        this.keywordText.setText(keyword);
        this.searchButton = (ImageButton) findViewById(R.id.ImageButton_searchinfo);
        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String keyword = SearchInfoDialog.this.keywordText.getText().toString();
                keyword = keyword.replace("#", "%23");
                search(keyword, 1);
            }
        });

        this.preButton = (Button) findViewById(R.id.prev_button);
        this.preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String keyword = SearchInfoDialog.this.keywordText.getText().toString();
                keyword = keyword.replace("#", "%23");
                search(keyword, SearchInfoDialog.this.currentPage - 1);

            }
        });
        this.preButton.setEnabled(false);

        this.nextButton = (Button) findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String keyword = SearchInfoDialog.this.keywordText.getText().toString();
                keyword = keyword.replace("#", "%23");
                search(keyword, SearchInfoDialog.this.currentPage + 1);

            }
        });
        this.nextButton.setEnabled(false);

        Button closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        //---------------------------------
        // Get Account Info
        //---------------------------------

        this.accountInfo = this.db.getCurrentLoginAccountInfo();

    }

    //-----------------------------------------------------------------------------------
    /**
     * Refresh List View
     */
    //-----------------------------------------------------------------------------------
    private void setListView()
    {

        //-------------------------------------
        // Prepare Adapter
        //-------------------------------------
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (SearchInfo searchInfo : this.searchInfoList)
        {
            Map<String, Object> item = new HashMap<String, Object>();
            UserInfo userInfo = searchInfo.getUserInfo();
            item.put(SearchInfo.FROM_USER, userInfo.getScreenName());
            item.put(SearchInfo.PROFILE_IMAGE_URL, userInfo.getUserImageURL());

            item.put(SearchInfo.TEXT, searchInfo.getStatus());
            String currenttime = searchInfo.getTime_search();
            item.put(SearchInfo.CREATED_AT,
                    SearchInfo.getFormatTimeCrowdroid(currenttime));
            item.put(SearchInfo.MESSAGE_ID, searchInfo.getMessageId());
            data.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                data,
                R.layout.list_item_search_info,
                new String[]
                { SearchInfo.FROM_USER, SearchInfo.TEXT, SearchInfo.CREATED_AT },
                new int[]
                { R.id.from_user, R.id.text, R.id.create_at });

        //-------------------------------------
        // Prepare List View
        //-------------------------------------
        ListView listView = (ListView) findViewById(R.id.ListView_searchinfo);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            //			@SuppressWarnings({ "finally", "null" })
            @SuppressWarnings("finally")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id)
            {

                MultiSelectDialog msDialog = new MultiSelectDialog(
                        getContext(), SearchInfoDialog.this.db);
                //				BasicInfo basicinfo=null;
                //				UserInfo info = null;
                try
                {
                    //Set Bitmap
                    UserInfo userInfo = SearchInfoDialog.this.searchInfoList.get(
                            position).getUserInfo();
                    Bitmap bm = ImageBuilder.returnBitMap(userInfo.getUserImageURL());
                    userInfo.setUserImage(bm);
                    //					info = (UserInfo)TwitterHandler.getUserInfo(userInfo.getScreenName()).getData();
                    //					basicinfo.setUserInfo(info);
                    //					
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                finally
                {
                    msDialog.setInfo(SearchInfoDialog.this.searchInfoList.get(position));
                    msDialog.show();
                    return false;
                }

            }

        });

    }

    //-----------------------------------------------------------------------------------
    /**
     * Send Request to Search
     */
    //-----------------------------------------------------------------------------------
    private void search(final String query, final int page)
    {

        if (query != null && query.length() > 0)
        {

            //------------------------------------
            // Prepare Progress bar
            //------------------------------------
            this.progress = new ProgressDialog(getContext());
            this.progress.setIndeterminate(false);
            this.progress.show();

            //------------------------------------
            // Request
            //------------------------------------
            new Thread(new Runnable() {

                @SuppressWarnings("unchecked")
                @Override
                public void run()
                {

                    CommunicationHandlerResult result;
                    result = TwitterHandler.searchinfo(query, page);
                    if (result.getResultCode() == 200)
                    {
                        SearchInfoDialog.this.currentPage = page;
                        SearchInfoDialog.this.searchInfoList = (ArrayList<SearchInfo>) result.getData();
                    }

                    //Notify  to Handler
                    SearchInfoDialog.this.mHandler.sendEmptyMessage(0);

                }
            }, "Search").start();

        }
    }

    //-----------------------------------------------------------------------------------
    /**
     * Show
     */
    //-----------------------------------------------------------------------------------
    @Override
    public void show()
    {
        super.show();
        String keyword = this.keywordText.getText().toString();
        keyword = keyword.replace("#", "%23");
        search(keyword, 1);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Change Diaplay According to Current Status
     */
    //-----------------------------------------------------------------------------
    public void changeStatus()
    {

        //		setTitle(getContext().getString(R.string.dialog_searchinfo_searchsometing)
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
        if (this.searchInfoList != null && this.searchInfoList.size() >= 15)
        {
            this.nextButton.setEnabled(true);
        }
        else
        {
            this.nextButton.setEnabled(false);
        }
    }

}
