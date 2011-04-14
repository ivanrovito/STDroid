package com.anhuioss.crowdroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.anhuioss.crowdroid.dialog.FavoriteDialog;
import com.anhuioss.crowdroid.dialog.MultiSelectDialog;
import com.anhuioss.crowdroid.dialog.SearchInfoDialog;
import com.anhuioss.crowdroid.dialog.SearchUserDialog;
import com.anhuioss.crowdroid.dialog.UpdateDialog;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;
import com.anhuioss.crowdroid.info.AtMessageInfo;
import com.anhuioss.crowdroid.info.BasicInfo;
import com.anhuioss.crowdroid.info.DirectMessageInfo;
import com.anhuioss.crowdroid.info.TimeLineInfo;
import com.anhuioss.crowdroid.info.UserInfo;
import com.anhuioss.crowdroid.settings.MainSettingActivity;
import com.anhuioss.crowdroid.translate.BingTranslate;
import com.anhuioss.crowdroid.translate.GoogleTranslate;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.AccountInfo;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;
import com.anhuioss.crowdroid.util.IErrorMessage;
import com.anhuioss.crowdroid.util.ImageBuilder;
import com.anhuioss.crowdroid.util.MyDbAdapter;

public class TimeLineActivity extends TabActivity implements
        OnTabChangeListener, OnItemClickListener, OnItemLongClickListener
{
    //Menu
    private static final int             MENU_SETTING                  = 1;

    private static final int             MENU_TWEET                    = 2;

    private static final int             MENU_REFRESH                  = 3;

    private static final int             MENU_NEW_DIRECT_MESSAGE       = 4;

    private static final int             MENU_SET_BACK_GROUND          = 5;

    private static final int             MENU_MORE_PAGE                = 6;

    private static final int             MENU_LESS_PAGE                = 7;

    private static final int             MENU_ABOUT_SOFTWARE_LICENCE   = 8;

    private static final int             MENU_SHOW_HELP                = 9;

    private static final int             MENU_FIND                     = 10;

    private static final int             MENU_SEARCH                   = 11;

    private static final int             MENU_MY_FAVORITE              = 12;

    private static final int             MENU_LOGOUT                   = 13;

    //Tab
    private TabHost                      myTabhost                     = null;

    public static final int              TAB_FRIEND_TIMELINE           = 1;

    public static final int              TAB_AT_MESSAGE                = 2;

    public static final int              TAB_DIRECT_MESSAGE            = 3;

    public static final int              TAB_MY_TIMELINE               = 4;

    private int                          currentTab                    = TAB_FRIEND_TIMELINE;

    Button                               imageReceive;

    Button                               imageSend;

    //CurrentPage
    private int                          currentPage_homeTimeLine      = 1;

    private int                          currentPage_atMessage         = 1;

    private int                          currentPage_directMessage     = 1;

    private int                          currentPage_myTimeLine        = 1;

    //Data
    private ArrayList<TimeLineInfo>      publicTimeLineInfoList        = null;

    private ArrayList<AtMessageInfo>     atMessageInfoList             = null;

    private ArrayList<DirectMessageInfo> directMessageInfoList         = null;

    private ArrayList<DirectMessageInfo> directMessageInfoListSend     = null;

    private ArrayList<DirectMessageInfo> directMessageInfoListRecieve  = null;

    private ArrayList<TimeLineInfo>      myTimeLineInfoList            = null;

    /** Image Map for user profile */
    private HashMap<String, Bitmap>      userImageMap;                                               //<uid, userImage>

    //Progress Dialog
    private ProgressDialog               progress;

    //DataBase Adapter
    private MyDbAdapter                  myDbAdapter;

    private static final int             DIRECT_MESSAGE_MODE_RECEIVE   = 0;

    private static final int             DIRECT_MESSAGE_MODE_SENT      = 1;

    private int                          directMessageMode             = DIRECT_MESSAGE_MODE_RECEIVE;

    private static final String          HANDLER_TYPE_REFRESH_FINISHED = "refresh-finished";

    private static final String          HANDLER_TYPE_REFRESH_REQUEST  = "refresh-request";

    //last refresh time
    private long                         lastRefreshTime;

    //is refreshing
    private boolean                      isRefreshing                  = false;

    /** Auto Refresh Timer*/
    AutoRefreshHandler                   autoRefresh;

    /** Current Service */
    private String                       currentService                = null;

    /** Current ScreenName */
    private String                       currentScreenName             = null;

    /** API ACCESS*/
    private String                       apiAccessAccount              = null;

    /** API SECRET*/
    private String                       apiAccessSecret               = null;

    /** Thread refreshTimeLine*/
    private Thread                       refreshTimeLine               = null;

    /** font color*/
    private String                       fontColor                     = "-1";

    /** define auto refresh while change tab*/
    boolean                              autoRefreshChageTab           = false;

    /**
     * This Handler is called when Refresh is finished.
     */
    Handler                              mHandler                      = new Handler() {
                                                                           @Override
                                                                           public void handleMessage(
                                                                                   Message msg)
                                                                           {

                                                                               //Get Type
                                                                               String type = msg.getData().getString(
                                                                                       "type");
                                                                               if (type.equals(TimeLineActivity.HANDLER_TYPE_REFRESH_REQUEST))
                                                                               {
                                                                                   refreshList(
                                                                                           TimeLineActivity.this.currentTab,
                                                                                           1);
                                                                               }

                                                                               else if (type.equals(TimeLineActivity.HANDLER_TYPE_REFRESH_FINISHED))
                                                                               {

                                                                                   //change the flag isRefreshing
                                                                                   TimeLineActivity.this.isRefreshing = false;

                                                                                   //Close Progress Dialog
                                                                                   if (TimeLineActivity.this.progress != null)
                                                                                   {
                                                                                       TimeLineActivity.this.progress.dismiss();
                                                                                   }
                                                                                   TimeLineActivity.this.progress = null;

                                                                                   //Show Toast
                                                                                   String toastMessage = msg.getData().getString(
                                                                                           "message");
                                                                                   Toast.makeText(
                                                                                           TimeLineActivity.this,
                                                                                           toastMessage,
                                                                                           Toast.LENGTH_SHORT).show();

                                                                                   //Refresh List View
                                                                                   createListView(TimeLineActivity.this.currentTab);

                                                                                   //Set application title
                                                                                   setApplicationTitle();

                                                                               }
                                                                           }
                                                                       };

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Created.
     */
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Init User Image Map
        this.userImageMap = new HashMap<String, Bitmap>();

        //Get Current Login Info
        this.myDbAdapter = new MyDbAdapter(this);
        this.myDbAdapter.open();
        this.fontColor = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_FONT_COLOR);

        AccountInfo currentLoginAccountInfo = this.myDbAdapter.getCurrentLoginAccountInfo();
        this.currentService = currentLoginAccountInfo.getService();

        this.apiAccessAccount = currentLoginAccountInfo.getAccessToken();
        this.apiAccessSecret = currentLoginAccountInfo.getTokenSecret();

        this.currentScreenName = currentLoginAccountInfo.getScreenName();

        //---------------------------
        //Create Tab Layout
        //---------------------------
        this.myTabhost = getTabHost();
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.activity_time_line,
                this.myTabhost.getTabContentView(), true);

        //Add Tab
        TabSpec tabspec1 = this.myTabhost.newTabSpec(
                String.valueOf(TAB_FRIEND_TIMELINE)).setIndicator(
                getString(R.string.activity_timeline_tab_home),
                getResources().getDrawable(
                        R.drawable.timeline_friends_timeline_tab)).setContent(
                R.id.friends);
        this.myTabhost.addTab(tabspec1);

        //set   follow5 tab  visible
        TabSpec tabspec2 = this.myTabhost.newTabSpec(
                String.valueOf(TAB_AT_MESSAGE)).setIndicator(
                getString(R.string.activity_timeline_tab_at_message),
                getResources().getDrawable(R.drawable.timeline_at_message_tab)).setContent(
                R.id.update);
        this.myTabhost.addTab(tabspec2);

        TabSpec tabspec3 = this.myTabhost.newTabSpec(
                String.valueOf(TAB_DIRECT_MESSAGE)).setIndicator(
                getString(R.string.activity_timeline_tab_directmessage),
                getResources().getDrawable(
                        R.drawable.timeline_direct_message_tab)).setContent(
                R.id.reply);
        this.myTabhost.addTab(tabspec3);

        TabSpec tabspec4 = this.myTabhost.newTabSpec(
                String.valueOf(TAB_MY_TIMELINE)).setIndicator(
                getString(R.string.activity_timeline_tab_MyTimeline),
                getResources().getDrawable(R.drawable.timeline_my_timeline_tab)).setContent(
                R.id.find);
        this.myTabhost.addTab(tabspec4);

        //Set Listener
        this.myTabhost.setOnTabChangedListener(this);

        Integer.valueOf(this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_UTC_OFFSET));

        addScrollListener();
    }

    //----------------------------------------------------------------------
    /**
     *  Set Title Bar with current Info..
     */
    //----------------------------------------------------------------------
    private void setApplicationTitle()
    {

        if (this.currentScreenName.length() > 12)
        {
            this.currentScreenName = this.currentScreenName.substring(0, 11)
                    + "...";
        }
        if (this.currentTab == TAB_FRIEND_TIMELINE)
        {
            setTitle(this.currentService + " : " + this.currentScreenName
                    + "  " + getString(R.string.activity_timeline_currentpage)
                    + this.currentPage_homeTimeLine);
        }
        else if (this.currentTab == TAB_AT_MESSAGE)
        {
            setTitle(this.currentService + " : " + this.currentScreenName
                    + "  " + getString(R.string.activity_timeline_currentpage)
                    + this.currentPage_atMessage);
        }
        else if (this.currentTab == TAB_DIRECT_MESSAGE)
        {
            setTitle(this.currentService + " : " + this.currentScreenName
                    + "  " + getString(R.string.activity_timeline_currentpage)
                    + this.currentPage_directMessage);
        }
        else if (this.currentTab == TAB_MY_TIMELINE)
        {
            setTitle(this.currentService + " : " + this.currentScreenName
                    + "  " + getString(R.string.activity_timeline_currentpage)
                    + this.currentPage_myTimeLine);
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Resumed.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onStart()
    {

        //Init DBAdapter
        if (this.myDbAdapter == null)
        {
            this.myDbAdapter = new MyDbAdapter(this);
            //		     myDbAdapter.open();
        }
        this.myDbAdapter.open();
        this.fontColor = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_FONT_COLOR);
        this.autoRefreshChageTab = this.myDbAdapter.getSettingValue(
                MyDbAdapter.PARAM_SETTING_AUTOREFRESH_SWITCH_TAB).equals(
                MyDbAdapter.PARAM_VALUE_OFF);
        //Set BackGround Image
        loadBackGroundImage();
        if (this.publicTimeLineInfoList != null)
        {

        }
        else
        {
            if (this.currentTab == TAB_FRIEND_TIMELINE)
            {
                refreshList(TAB_FRIEND_TIMELINE, this.currentPage_homeTimeLine);
            }
        }

        Intent i = getIntent();
        if (i.getExtras() != null)
        {
            Bundle b = i.getExtras();
            String directmessage = b.getString("direct");
            String atmessage = b.getString("atmessage");
            String gmessage = b.getString("gmessage");
            if (directmessage != null)
            {
                if (directmessage.equals("direct"))
                {
                    this.myTabhost.setCurrentTab(TAB_DIRECT_MESSAGE - 1);
                    //						  currentTab =TAB_DIRECT_MESSAGE;
                    atmessage = null;
                    refreshList(TAB_DIRECT_MESSAGE, 1);
                }

            }
            if (atmessage != null)
            {
                if (atmessage.equals("atmessage"))
                {
                    this.myTabhost.setCurrentTab(TAB_AT_MESSAGE - 1);
                    //						currentTab = TAB_AT_MESSAGE;
                    gmessage = null;
                    refreshList(TAB_AT_MESSAGE, 1);
                }
            }
            if (gmessage != null)
            {
                if (gmessage.equals("gmessage"))
                {
                    this.myTabhost.setCurrentTab(TAB_FRIEND_TIMELINE - 1);
                    //						currentTab =TAB_FRIEND_TIMELINE;
                    refreshList(TAB_FRIEND_TIMELINE, 1);
                }
            }

        }

        //auto refresh
        this.autoRefresh = new AutoRefreshHandler();
        this.autoRefresh.start();
        super.onStart();

    }

    @Override
    protected void onRestart()
    {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Paused.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onStop()
    {

        if (this.refreshTimeLine != null && this.refreshTimeLine.isAlive())
        {
            try
            {
                this.refreshTimeLine.join();

            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            this.refreshTimeLine = null;
        }

        //Stop Auto Refresh
        if (this.autoRefresh != null && this.autoRefresh.isAlive())
        {
            this.autoRefresh.stopAutoRefresh();
            try
            {
                this.autoRefresh.join();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            this.autoRefresh = null;
        }

        //Close D---------------------------------
        /**
         * Send @Message or DirectMessage according to current Tab.
         */
        // -----------------------------------------------B
        if (this.myDbAdapter != null)
        {
            this.myDbAdapter.close();
            //				 myDbAdapter = null;
        }
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
        {
            this.autoRefresh.lockAutoRefresh(false);
        }
        else
        {
            this.autoRefresh.lockAutoRefresh(true);
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Paused.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onDestroy()
    {

        this.userImageMap.clear();
        getWindow().setBackgroundDrawable(null);
        System.gc();

        super.onDestroy();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Tab Selection is Changed.
     */
    //-----------------------------------------------------------------------------
    public void onTabChanged(String tagString)
    {

        this.currentTab = Integer.valueOf(tagString);

        if (this.autoRefreshChageTab)
        {
            if (this.currentTab == TAB_FRIEND_TIMELINE
                    && this.publicTimeLineInfoList != null)
            {
                setTitle(this.currentService + " : " + this.currentScreenName
                        + "  "
                        + getString(R.string.activity_timeline_currentpage)
                        + this.currentPage_homeTimeLine);
                createListView(TAB_FRIEND_TIMELINE);
                return;
            }

            if (this.currentTab == TAB_AT_MESSAGE
                    && this.atMessageInfoList != null)
            {
                setTitle(this.currentService + " : " + this.currentScreenName
                        + "  "
                        + getString(R.string.activity_timeline_currentpage)
                        + this.currentPage_atMessage);
                createListView(TAB_AT_MESSAGE);
                return;
            }

            if (this.currentTab == TAB_DIRECT_MESSAGE
                    && this.directMessageInfoList != null)
            {
                setTitle(this.currentService + " : " + this.currentScreenName
                        + "  "
                        + getString(R.string.activity_timeline_currentpage)
                        + this.currentPage_directMessage);
                createListView(TAB_DIRECT_MESSAGE);
                return;
            }

            if (this.currentTab == TAB_MY_TIMELINE
                    && this.myTimeLineInfoList != null)
            {
                setTitle(this.currentService + " : " + this.currentScreenName
                        + "  "
                        + getString(R.string.activity_timeline_currentpage)
                        + this.currentPage_myTimeLine);
                createListView(TAB_MY_TIMELINE);
                return;
            }
        }

        this.currentTab = Integer.valueOf(tagString);
        //currentPage_homeTimeLine = 1;
        //currentPage_atMessage = 1;
        //currentPage_directMessage = 1;
        //currentPage_myTimeLine = 1;
        if (this.currentTab == TAB_DIRECT_MESSAGE)
        {

            this.imageReceive = (Button) findViewById(R.id.activity_timeline_button_receive);
            this.imageSend = (Button) findViewById(R.id.activity_timeline_button_send);
            this.imageReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    TimeLineActivity.this.imageReceive.setBackgroundResource(R.drawable.activity_directmessage_on);
                    TimeLineActivity.this.imageSend.setBackgroundResource(R.drawable.activity_directmessage_off);
                    // TODO Auto-generated method stub
                    TimeLineActivity.this.directMessageMode = TimeLineActivity.this.DIRECT_MESSAGE_MODE_RECEIVE;
                    if (TimeLineActivity.this.directMessageInfoListRecieve == null
                            || !TimeLineActivity.this.autoRefreshChageTab)
                    {
                        refreshList(TimeLineActivity.this.currentTab,
                                TimeLineActivity.this.currentPage_directMessage);
                    }
                    else
                    {
                        createListView(TAB_DIRECT_MESSAGE);
                    }
                }
            });

            this.imageSend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    TimeLineActivity.this.imageReceive.setBackgroundResource(R.drawable.activity_directmessage_off);
                    TimeLineActivity.this.imageSend.setBackgroundResource(R.drawable.activity_directmessage_on);
                    // TODO Auto-generated method stub
                    TimeLineActivity.this.directMessageMode = TimeLineActivity.this.DIRECT_MESSAGE_MODE_SENT;
                    if (TimeLineActivity.this.directMessageInfoListSend == null
                            || !TimeLineActivity.this.autoRefreshChageTab)
                    {
                        refreshList(TimeLineActivity.this.currentTab,
                                TimeLineActivity.this.currentPage_directMessage);
                    }
                    else
                    {
                        createListView(TAB_DIRECT_MESSAGE);
                    }
                }
            });

        }
        else
        {
            refreshList(this.currentTab, this.currentPage_directMessage);
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Create Menu.
     */
    //-----------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        String fFlag = "";
        fFlag = this.myDbAdapter.getSettingValue("twitter_api_proxy");

        super.onCreateOptionsMenu(menu);

        menu.add(2, MENU_REFRESH, 0, IGeneral.OPTION_MENU_TEXT_REFRESH).setIcon(
                IGeneral.OPTION_MENU_IMAGE_REFRESH);

        menu.add(2, MENU_TWEET, 1, IGeneral.OPTION_MENU_TEXT_TWEET).setIcon(
                IGeneral.OPTION_MENU_IMAGE_TWEET);

        // TODO ヘルプページを作成
        //        menu.add(2, MENU_SHOW_HELP, 2, IGeneral.OPTION_MENU_TEXT_HELP).setIcon(
        //                IGeneral.OPTION_MENU_IMAGE_HELP);

        menu.add(2, MENU_SETTING, 3, IGeneral.OPTION_MENU_TEXT_SETTINGS).setIcon(
                IGeneral.OPTION_MENU_IMAGE_SETTINGS);

        menu.add(0, MENU_MORE_PAGE, 4, IGeneral.OPTION_MENU_TEXT_MORE_PAGE).setIcon(
                IGeneral.OPTION_MENU_IMAGE_MORE_PAGE);

        menu.add(1, MENU_LESS_PAGE, 5, IGeneral.OPTION_MENU_TEXT_PREV_PAGE).setIcon(
                IGeneral.OPTION_MENU_IMAGE_PREV_PAGE);

        // TODO ライセンスページを作成
        //        menu.add(2, MENU_ABOUT_SOFTWARE_LICENCE, 8,
        //                IGeneral.OPTION_MENU_TEXT_LICENSE).setIcon(
        //                IGeneral.OPTION_MENU_IMAGE_LICENSE);

        menu.add(2, MENU_LOGOUT, 11, IGeneral.OPTION_MENU_LOGOUT_TITLE).setIcon(
                IGeneral.OPTION_MENU_LOGOUT);

        if (fFlag.equals("off"))
        {
            menu.add(2, MENU_NEW_DIRECT_MESSAGE, 7,
                    IGeneral.OPTION_MENU_TEXT_DIRECT_MESSAGE).setIcon(
                    IGeneral.OPTION_MENU_IMAGE_DIRECT_MESSAGE);
            menu.addSubMenu(2, MENU_FIND, 6, IGeneral.OPTION_MENU_TEXT_FIND).setIcon(
                    IGeneral.OPTION_MENU_IMAGE_FIND);
        }
        menu.addSubMenu(2, MENU_SEARCH, 9, IGeneral.OPTION_MENU_SEARCH).setIcon(
                IGeneral.OPTION_MENU_IMAGE_SEARCH);
        menu.addSubMenu(2, MENU_MY_FAVORITE, 10,
                IGeneral.OPTION_MENU_MY_FAVORITE_TITLE).setIcon(
                IGeneral.OPTION_MENU_MY_FAVORITE);

        return true;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Create Menu.
     */
    //-----------------------------------------------------------------------------
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        int currentPage;
        if (this.currentTab == TAB_FRIEND_TIMELINE)
        {
            currentPage = this.currentPage_homeTimeLine;
        }
        else if (this.currentTab == TAB_AT_MESSAGE)
        {
            currentPage = this.currentPage_atMessage;
        }
        else if (this.currentTab == TAB_DIRECT_MESSAGE)
        {
            currentPage = this.currentPage_directMessage;
        }
        else
        {
            currentPage = this.currentPage_myTimeLine;
        }

        //Set menu less disable
        menu.removeItem(MENU_LESS_PAGE);

        //Get count number of the list
        int count = getCountNumberOfList();

        //Set menu item enable of disable
        boolean isShowMore = true;
        boolean isShowLess = false;
        //check more page
        if (count >= 20)
        {
            isShowMore = true;
        }
        else
        {
            isShowMore = false;
        }
        //check less page
        if (currentPage > 1)
        {
            isShowLess = true;
        }
        else
        {
            isShowLess = false;
        }
        //check more and less
        if (count == -1)
        {
            isShowLess = false;
            isShowMore = false;
        }
        //process more page
        if (isShowMore)
        {
            //remove more page
            menu.removeItem(MENU_MORE_PAGE);
            //add more page
            menu.add(0, MENU_MORE_PAGE, 4, IGeneral.OPTION_MENU_TEXT_MORE_PAGE).setIcon(
                    IGeneral.OPTION_MENU_IMAGE_MORE_PAGE);
        }
        else
        {
            //remove more page
            menu.removeItem(MENU_MORE_PAGE);
        }
        //process less page
        if (isShowLess)
        {
            //remove less page
            menu.removeItem(MENU_LESS_PAGE);
            //add less page
            menu.add(0, MENU_LESS_PAGE, 5, IGeneral.OPTION_MENU_TEXT_PREV_PAGE).setIcon(
                    IGeneral.OPTION_MENU_IMAGE_PREV_PAGE);
        }
        else
        {
            //remove less page
            menu.removeItem(MENU_LESS_PAGE);
        }

        return true;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Get Item Count of List in Current Tab.
     */
    //-----------------------------------------------------------------------------
    private int getCountNumberOfList()
    {

        int count = -1;
        switch (this.currentTab)
        {
        case TAB_FRIEND_TIMELINE:
        {
            if (this.publicTimeLineInfoList == null)
            {
                return -1;
            }
            count = this.publicTimeLineInfoList.size();
        }
            break;
        case TAB_AT_MESSAGE:
        {
            if (this.atMessageInfoList == null)
            {
                return -1;
            }
            count = this.atMessageInfoList.size();
        }
            break;
        case TAB_DIRECT_MESSAGE:
        {
            if (this.directMessageInfoList == null)
            {
                return -1;
            }
            count = this.directMessageInfoList.size();
        }
            break;
        case TAB_MY_TIMELINE:
        {
            if (this.myTimeLineInfoList == null)
            {
                return -1;
            }
            count = this.myTimeLineInfoList.size();
        }
            break;
        }
        return count;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Menu Item is selected
     */
    //-----------------------------------------------------------------------------
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        int currentPage;
        if (this.currentTab == TAB_FRIEND_TIMELINE)
        {
            currentPage = this.currentPage_homeTimeLine;
        }
        else if (this.currentTab == TAB_AT_MESSAGE)
        {
            currentPage = this.currentPage_atMessage;
        }
        else if (this.currentTab == TAB_DIRECT_MESSAGE)
        {
            currentPage = this.currentPage_directMessage;
        }
        else
        {
            currentPage = this.currentPage_myTimeLine;
        }
        switch (item.getItemId())
        {

        case MENU_MORE_PAGE:
        {

            if (this.currentTab == TAB_FRIEND_TIMELINE)
            {
                this.currentPage_homeTimeLine = currentPage + 1;
                refreshList(this.currentTab, this.currentPage_homeTimeLine);
            }
            else if (this.currentTab == TAB_AT_MESSAGE)
            {
                this.currentPage_atMessage = currentPage + 1;
                refreshList(this.currentTab, this.currentPage_atMessage);
            }
            else if (this.currentTab == TAB_DIRECT_MESSAGE)
            {
                this.currentPage_directMessage = currentPage + 1;
                refreshList(this.currentTab, this.currentPage_directMessage);
            }
            else
            {
                this.currentPage_myTimeLine = currentPage + 1;
                refreshList(this.currentTab, this.currentPage_myTimeLine);
            }
        }
            break;

        case MENU_LESS_PAGE:
        {

            if (this.currentTab == TAB_FRIEND_TIMELINE)
            {
                this.currentPage_homeTimeLine = currentPage - 1;
                refreshList(this.currentTab, this.currentPage_homeTimeLine);
            }
            else if (this.currentTab == TAB_AT_MESSAGE)
            {
                this.currentPage_atMessage = currentPage - 1;
                refreshList(this.currentTab, this.currentPage_atMessage);
            }
            else if (this.currentTab == TAB_DIRECT_MESSAGE)
            {
                this.currentPage_directMessage = currentPage - 1;
                refreshList(this.currentTab, this.currentPage_directMessage);
            }
            else
            {
                this.currentPage_myTimeLine = currentPage - 1;
                refreshList(this.currentTab, this.currentPage_myTimeLine);
            }

        }
            break;

        case MENU_SETTING:
        {
            Toast.makeText(this, R.string.menu_timeline_settings,
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainSettingActivity.class);
            intent.putExtra(MainSettingActivity.KEY_SETTING_MODE,
                    MainSettingActivity.MODE_ACCOUNT_MANAGE_DISABLED);
            startActivity(intent);
        }
            break;

        case MENU_TWEET:
        {
            UpdateDialog dialog = new UpdateDialog(this,
                    UpdateDialog.MODE_NEW_TWEET, this.myDbAdapter);
            dialog.setTitle(R.string.dialog_update_newTweet);
            dialog.show();
        }
            break;

        case MENU_REFRESH:
        {
            //Release process.
            //				currentPage_homeTimeLine = 1;
            //				currentPage_atMessage=1;
            if (this.currentTab == TAB_FRIEND_TIMELINE)
            {
                this.currentPage_homeTimeLine = 1;
                refreshList(this.currentTab, this.currentPage_homeTimeLine);
            }
            else if (this.currentTab == TAB_AT_MESSAGE)
            {
                this.currentPage_atMessage = 1;
                refreshList(this.currentTab, this.currentPage_atMessage);
            }
            else if (this.currentTab == TAB_DIRECT_MESSAGE)
            {
                this.currentPage_directMessage = 1;
                refreshList(this.currentTab, this.currentPage_directMessage);
            }
            else
            {
                this.currentPage_myTimeLine = 1;
                refreshList(this.currentTab, this.currentPage_myTimeLine);
            }
            //				refreshList(currentTab, 1);
        }
            break;

        case MENU_SET_BACK_GROUND:
        {
            Intent it = new Intent();
            it.setType("image/*");
            it.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(it, 1);
            Toast.makeText(this,
                    R.string.toast_actvity_mainsetting_selectwalpaper,
                    Toast.LENGTH_SHORT).show();
        }
            break;

        case MENU_NEW_DIRECT_MESSAGE:
        {
            UserSelectDialog usd = new UserSelectDialog(this,
                    MENU_NEW_DIRECT_MESSAGE, this.myDbAdapter);
            usd.show();
            break;
        }

        case MENU_ABOUT_SOFTWARE_LICENCE:
        {
            Intent intent = new Intent(this, ShowWebContentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(IGeneral.TYPE, IGeneral.TYPE_LICENSE);
            intent.putExtras(bundle);
            startActivity(intent);
            break;
        }

        case MENU_SHOW_HELP:
        {
            Intent intent = new Intent(this, ShowWebContentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(IGeneral.TYPE, IGeneral.TYPE_HELP);
            intent.putExtras(bundle);
            startActivity(intent);
        }
            break;

        case MENU_FIND:
        {
            SearchUserDialog sd = new SearchUserDialog(this, this.myDbAdapter);
            sd.setTitle(R.string.dialog_searchuser_searchuser);
            sd.show();
        }
            break;
        case MENU_SEARCH:
        {
            SearchInfoDialog sid = new SearchInfoDialog(this, null,
                    this.myDbAdapter);
            sid.setTitle(R.string.dialog_searchinfo_searchsometing);
            sid.show();
        }
            break;

        case MENU_MY_FAVORITE:
        {
            FavoriteDialog favoriteDialog = new FavoriteDialog(this,
                    this.myDbAdapter);
            favoriteDialog.show();
        }
            break;

        case MENU_LOGOUT:
        {
            finish();
        }
            break;
        }
        return true;
    }

    //-----------------------------------------------------------------------------
    /**
     *  On Click Listener for List Item.
     *  you can select the direct message to update your message.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {

        }
        return super.onCreateDialog(id);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Long Click Listener for List Item.
     */
    //-----------------------------------------------------------------------------
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id)
    {

        //Show Multi Select Dialog
        MultiSelectDialog dialog = new MultiSelectDialog(this, this.myDbAdapter);
        BasicInfo info = null;
        if (this.currentTab == TAB_FRIEND_TIMELINE)
        {
            info = this.publicTimeLineInfoList.get(position);
        }
        else if (this.currentTab == TAB_AT_MESSAGE)
        {
            info = this.atMessageInfoList.get(position);
        }
        else if (this.currentTab == TAB_DIRECT_MESSAGE)
        {
            info = this.directMessageInfoList.get(position);
        }
        else if (this.currentTab == TAB_MY_TIMELINE)
        {
            info = this.myTimeLineInfoList.get(position);
        }
        dialog.setInfo(info, this.currentTab);

        dialog.show();

        return true;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Refresh List according to its tab and infoList.
     */
    //-----------------------------------------------------------------------------
    private void refreshList(final int tab, final int page)
    {

        //Show Progress Bar
        if (this.progress == null)
        {
            this.progress = new ProgressDialog(this);
            this.progress.setIndeterminate(false);
            this.progress.show();
        }

        //Prepare Thread
        this.refreshTimeLine = new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {
                //change the flag isRefreshing
                TimeLineActivity.this.isRefreshing = true;

                //last refresh time
                TimeLineActivity.this.lastRefreshTime = System.currentTimeMillis();

                //Toast Message
                String toastMessage = getString(R.string.dialog_multiselect_succeed);

                CommunicationHandlerResult result = new CommunicationHandlerResult();

                //Get New List
                if (tab == TAB_FRIEND_TIMELINE)
                {
                    result = TwitterHandler.getPublicTimeLine(page);
                    TimeLineActivity.this.publicTimeLineInfoList = (ArrayList<TimeLineInfo>) result.getData();
                    toastMessage = getString(result.getResultMessage());
                }
                else if (tab == TAB_AT_MESSAGE)
                {
                    result = TwitterHandler.getMensionList(page);
                    TimeLineActivity.this.atMessageInfoList = (ArrayList<AtMessageInfo>) result.getData();
                    toastMessage = getString(result.getResultMessage());
                }
                else if (tab == TAB_DIRECT_MESSAGE)
                {
                    if (TimeLineActivity.this.directMessageMode == TimeLineActivity.this.DIRECT_MESSAGE_MODE_RECEIVE)
                    {
                        result = TwitterHandler.getDirectMessageReceive(page);
                        TimeLineActivity.this.directMessageInfoListRecieve = (ArrayList<DirectMessageInfo>) result.getData();
                    }
                    else
                    {
                        result = TwitterHandler.getDirectMessageSend(page);
                        TimeLineActivity.this.directMessageInfoListSend = (ArrayList<DirectMessageInfo>) result.getData();
                    }
                    TimeLineActivity.this.directMessageInfoList = (ArrayList<DirectMessageInfo>) result.getData();
                    toastMessage = getString(result.getResultMessage());

                }
                else if (tab == TAB_MY_TIMELINE)
                {
                    result = TwitterHandler.getMyTimeLine(page);
                    TimeLineActivity.this.myTimeLineInfoList = (ArrayList<TimeLineInfo>) result.getData();
                    toastMessage = getString(result.getResultMessage());
                }

                //---------------------------------
                // Set Info List
                //---------------------------------
                ArrayList<?> infoList = null;
                if (tab == TAB_FRIEND_TIMELINE)
                {
                    infoList = TimeLineActivity.this.publicTimeLineInfoList;
                }
                else if (tab == TAB_AT_MESSAGE)
                {
                    infoList = TimeLineActivity.this.atMessageInfoList;
                }
                else if (tab == TAB_DIRECT_MESSAGE)
                {
                    infoList = TimeLineActivity.this.directMessageInfoList;
                }
                else if (tab == TAB_MY_TIMELINE)
                {
                    infoList = TimeLineActivity.this.myTimeLineInfoList;
                }

                //---------------------------------
                // Download User Image
                //---------------------------------
                loadUserImage(infoList);

                //---------------------------------
                // Translation
                //---------------------------------
                try
                {
                    autoTranslate(infoList);

                }
                catch (Exception e)
                {
                    toastMessage = getString(IErrorMessage.MESSAGE_TRANSLATION_ERROR);
                    Log.w("StatusDroid", "Error Occured", e);
                }

                //---------------------------------
                // Hide Specified Message
                //---------------------------------
                hideWithSpecifiedKeyword(infoList);
                hideWithSpecifiedKeyword_u(infoList);

                //---------------------------------
                //Set a Max Message ID to DB form info list
                //---------------------------------
                if (tab == TAB_AT_MESSAGE)
                {
                    setMaxMessageIdFromList(infoList, tab);
                }
                else if (tab == TAB_DIRECT_MESSAGE)
                {
                    setMaxMessageIdFromList(infoList, tab);
                }
                else if (tab == TAB_FRIEND_TIMELINE)
                {
                    setMaxMessageIdFromList(infoList, tab);
                }

                //---------------------------------
                // Notify to Handler
                //---------------------------------
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("type",
                        TimeLineActivity.this.HANDLER_TYPE_REFRESH_FINISHED);
                bundle.putString("message", toastMessage);
                msg.setData(bundle);
                TimeLineActivity.this.mHandler.sendMessage(msg);
            }
        }, "TimeLine Refresh");
        this.refreshTimeLine.start();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Refresh List according to its tab and infoList.
     */
    //-----------------------------------------------------------------------------
    private void setMaxMessageIdFromList(ArrayList<?> infoList, int tab)
    {

        if (infoList == null)
        {
            return;
        }
        long maxMessageId = -1;
        for (Object info : infoList)
        {

            TimeLineInfo timeInfo = new TimeLineInfo();
            timeInfo.setTime(((BasicInfo) info).getTime());

            String tempMessageId = timeInfo.getFormatTime(this.currentService);

            long currentMessageId = format(tempMessageId);
            if (maxMessageId < currentMessageId)
            {
                maxMessageId = currentMessageId;
            }
        }
        if (maxMessageId != -1)
        {
            if (tab == TAB_AT_MESSAGE)
            {
                this.myDbAdapter.updateStatus(
                        MyDbAdapter.PARAM_STATUS_NEWEST_AT_MESSAGE_ID,
                        String.valueOf(maxMessageId));
            }
            else if (tab == TAB_DIRECT_MESSAGE)
            {
                this.myDbAdapter.updateStatus(
                        MyDbAdapter.PARAM_STATUS_NEWEST_DIRECT_MESSAGE_ID,
                        String.valueOf(maxMessageId));
            }
            else if (tab == TAB_FRIEND_TIMELINE)
            {
                this.myDbAdapter.updateStatus(
                        MyDbAdapter.PARAM_STATUS_NEWEST_GENERAL_MESSAGE_ID,
                        String.valueOf(maxMessageId));
            }
        }
    }

    private long format(String tempMessageId)
    {

        long id = 0;
        try
        {
            String temp;
            temp = tempMessageId.replaceAll("-", " ").replaceAll(":", " ").replaceAll(
                    " ", "");
            id = Long.valueOf(temp);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        return id;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Refresh List View according to its tab and infoList.
     */
    //-----------------------------------------------------------------------------
    private void createListView(int tab)
    {

        ArrayList<Map<String, Object>> data;
        ListView itemlist = null;

        //Switch from tab
        switch (tab)
        {

        case TAB_FRIEND_TIMELINE:
            data = getDataFromInfo(this.publicTimeLineInfoList);
            itemlist = (ListView) findViewById(R.id.list_time_line);
            setListView(itemlist, data, true);
            break;

        case TAB_AT_MESSAGE:
            data = getDataFromInfo(this.atMessageInfoList);
            itemlist = (ListView) findViewById(R.id.list_at_message);
            setListView(itemlist, data, true);
            break;

        case TAB_DIRECT_MESSAGE:
            if (this.directMessageMode == this.DIRECT_MESSAGE_MODE_RECEIVE)
            {
                data = getDataFromInfo(this.directMessageInfoListRecieve);
            }
            else
            {
                data = getDataFromInfo(this.directMessageInfoListSend);
            }
            itemlist = (ListView) findViewById(R.id.list_direct_message);
            setListView(itemlist, data, true);
            break;

        case TAB_MY_TIMELINE:
            data = getDataFromInfo(this.myTimeLineInfoList);
            itemlist = (ListView) findViewById(R.id.list_my_time_line);
            setListView(itemlist, data, true);
            break;
        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Convert Info List to Data(HashMap) List.
     */
    //-----------------------------------------------------------------------------
    private ArrayList<Map<String, Object>> getDataFromInfo(ArrayList<?> infoList)
    {

        //Create Data from infoList
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        //Check Type of infoList
        if (infoList != null && infoList.size() != 0)
        {
            for (int i = 0; i < infoList.size(); i++)
            {
                UserInfo userInfo = ((BasicInfo) infoList.get(i)).getUserInfo();

                //Get Retweet User Info if Exist
                UserInfo retweetUserInfo = null;
                String retweetedByUser = null;
                if (this.currentTab == TAB_FRIEND_TIMELINE)
                {
                    retweetUserInfo = ((TimeLineInfo) infoList.get(i)).getRetweetUserInfo();
                    if (retweetUserInfo != null)
                    {
                        retweetedByUser = userInfo.getScreenName();
                        userInfo = retweetUserInfo;
                    }

                }

                HashMap<String, Object> item = new HashMap<String, Object>();
                if (retweetedByUser != null)
                {
                    item.put(UserInfo.SCREENNAME, userInfo.getScreenName()
                            + " [RT by " + retweetedByUser + "]");
                }
                else
                {
                    item.put(UserInfo.SCREENNAME, userInfo.getScreenName());
                }

                String status = ((BasicInfo) infoList.get(i)).getStatus();
                item.put(TimeLineInfo.STATUS, status.replaceAll("\r", ""));
                item.put(UserInfo.USER_IMAGE, userInfo.getUserImage());
                item.put(
                        TimeLineInfo.TIME,
                        ((BasicInfo) infoList.get(i)).getFormatTime(this.currentService));
                item.put(TimeLineInfo.MESSAGEID,
                        ((BasicInfo) infoList.get(i)).getMessageId());
                data.add(item);
            }
        }

        return data;

    }

    //-----------------------------------------------------------------------------
    /**
     *  Set List View with Given Data.
     */
    //-----------------------------------------------------------------------------
    private void setListView(ListView listView,
            ArrayList<Map<String, Object>> data, boolean isOnclickListener)
    {

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.list_item_time_line, new String[]
                { UserInfo.SCREENNAME, TimeLineInfo.STATUS,
                        UserInfo.USER_IMAGE, TimeLineInfo.TIME }, new int[]
                { R.id.screen_name, R.id.status, R.id.user_image,
                        R.id.update_time });

        adapter.setViewBinder(new MyImageBinder(this.fontColor, null));

        //Set Adapter to List View
        listView.setAdapter(adapter);

        //Set Action Listener
        if (isOnclickListener)
        {
            listView.setOnItemLongClickListener(this);
            listView.setOnItemClickListener(this);
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Set BackGrouned Image.
     */
    //-----------------------------------------------------------------------------
    private void loadBackGroundImage()
    {
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        String path = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_IMAGE_PATH);

        if (path != null)
        {
            File file = new File(path);
            if (file.exists() && file.isFile() && file.canRead())
            {

                FileInputStream input = null;
                try
                {
                    input = new FileInputStream(file);

                    //Get BitMap and set to background
                    BitmapDrawable drawable = new BitmapDrawable(input);
                    getWindow().setBackgroundDrawable(drawable);

                }
                catch (SecurityException e)
                {
                    return;
                }
                catch (FileNotFoundException e)
                {
                    return;
                }
                catch (OutOfMemoryError e)
                {
                    return;
                }
                catch (Exception e)
                {
                    return;
                }
                finally
                {
                    //Close Stream
                    if (input != null)
                    {
                        try
                        {
                            input.close();
                        }
                        catch (IOException e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                        }
                        catch (Exception e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                        }
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Load userImage.
     */
    //-----------------------------------------------------------------------------
    private void loadUserImage(ArrayList<?> infoList)
    {

        if (infoList == null)
        {
            return;
        }

        //garbage collection
        System.gc();

        boolean communicationFlag = true;

        //Get If Retweet User Exist
        if (this.currentTab == TAB_FRIEND_TIMELINE)
        {
            for (Object info : infoList)
            {
                //Get UserInfo
                UserInfo retweetUserInfo = ((TimeLineInfo) info).getRetweetUserInfo();

                if (retweetUserInfo != null)
                {
                    //Prepare userImageMap
                    if (!this.userImageMap.containsKey(retweetUserInfo.getUid()))
                    {

                        Bitmap bitmap;
                        try
                        {
                            bitmap = ImageBuilder.returnBitMap(retweetUserInfo.getUserImageURL());
                            this.userImageMap.put(retweetUserInfo.getUid(),
                                    bitmap);
                        }
                        catch (OutOfMemoryError e)
                        {
                            return;
                        }
                        catch (IOException e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                            communicationFlag = false;
                        }
                        catch (Exception e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                        }

                    }
                    //Set Image to Info
                    retweetUserInfo.setUserImage(this.userImageMap.get(retweetUserInfo.getUid()));
                }
                else
                {
                    //Get UserInfo
                    UserInfo userInfo = ((BasicInfo) info).getUserInfo();

                    //Prepare userImageMap
                    if (!this.userImageMap.containsKey(userInfo.getUid()))
                    {

                        Bitmap bitmap;
                        try
                        {
                            bitmap = ImageBuilder.returnBitMap(userInfo.getUserImageURL());
                            this.userImageMap.put(userInfo.getUid(), bitmap);
                        }
                        catch (OutOfMemoryError e)
                        {
                            return;
                        }
                        catch (IOException e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                            communicationFlag = false;
                        }
                        catch (Exception e)
                        {
                            Log.w("StatusDroid", "Error Occured", e);
                        }

                    }

                    //Set Image to Info
                    userInfo.setUserImage(this.userImageMap.get(userInfo.getUid()));
                }
            }
        }

        else
        {
            for (Object info : infoList)
            {

                //Get UserInfo
                UserInfo userInfo = ((BasicInfo) info).getUserInfo();

                //Prepare userImageMap
                if (!this.userImageMap.containsKey(userInfo.getUid()))
                {

                    Bitmap bitmap;
                    try
                    {
                        bitmap = ImageBuilder.returnBitMap(userInfo.getUserImageURL());
                        this.userImageMap.put(userInfo.getUid(), bitmap);
                    }
                    catch (OutOfMemoryError e)
                    {
                        return;
                    }
                    catch (IOException e)
                    {
                        Log.w("StatusDroid", "Error Occured", e);
                        communicationFlag = false;
                    }
                    catch (Exception e)
                    {
                        Log.w("StatusDroid", "Error Occured", e);
                    }

                }

                //Set Image to Info
                userInfo.setUserImage(this.userImageMap.get(userInfo.getUid()));

            }
        }

        //garbage collection
        System.gc();

        //Warning
        if (!communicationFlag)
        {

        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Auto Translate Status.
     */
    //-----------------------------------------------------------------------------
    private void autoTranslate(ArrayList<?> infoList) throws Exception
    {

        if (infoList == null)
        {
            return;
        }

        if (this.myDbAdapter.getSettingValue(
                MyDbAdapter.PARAM_SETTING_AUTO_TRANSLATION).equals(
                MyDbAdapter.PARAM_VALUE_ON))
        {

            for (Object info : infoList)
            {

                ArrayList<String[]> autoTranslationList = this.myDbAdapter.getTranslationList();
                String screenName = ((BasicInfo) info).getUserInfo().getScreenName();
                String originalStatus = ((BasicInfo) info).getStatus();
                for (String[] translationInfo : autoTranslationList)
                {
                    if (screenName.equals(translationInfo[0]))
                    {
                        String translatedStatus;
                        if (this.myDbAdapter.getSettingValue(
                                MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE).equals(
                                MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE))
                        {
                            translatedStatus = (String) GoogleTranslate.translate(
                                    originalStatus,
                                    (String) GoogleTranslate.detect(
                                            originalStatus).getData(),
                                    translationInfo[2]).getData();
                        }
                        else
                        {
                            translatedStatus = (String) BingTranslate.translate(
                                    originalStatus,
                                    (String) BingTranslate.detect(
                                            originalStatus).getData(),
                                    translationInfo[2]).getData();
                        }
                        ((BasicInfo) info).setStatus(translatedStatus);
                        break;
                    }
                }

            }

        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Hide Sttatus with specified Keyword.
     */
    //-----------------------------------------------------------------------------
    private void hideWithSpecifiedKeyword(ArrayList<?> infoList)
    {

        if (infoList == null)
        {
            return;
        }

        ArrayList<String> keywordsList = this.myDbAdapter.getKeywordsList();
        for (Object info : infoList)
        {
            String status = ((BasicInfo) info).getStatus();

            for (String keyword : keywordsList)
            {
                Matcher matcher = Pattern.compile(keyword,
                        Pattern.CASE_INSENSITIVE).matcher(status);

                //--------------------------------
                //Replace if keyword has matched
                //--------------------------------
                if (matcher.find())
                {
                    String replacedText = "";
                    for (int j = 0; j < status.length(); j++)
                    {
                        String filter = "#";
                        replacedText = replacedText + filter;
                    }

                    //Set Hidden Status
                    ((BasicInfo) info).setStatus(replacedText);
                    break;
                }

            }

        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Hide Sttatus with specified Keyword.
     */
    //-----------------------------------------------------------------------------
    private void hideWithSpecifiedKeyword_u(ArrayList<?> infoList)
    {

        if (infoList == null)
        {
            return;
        }

        ArrayList<String> keywordsList_u = this.myDbAdapter.getKeywordsList_u();
        for (Object info : infoList)
        {
            String username = ((BasicInfo) info).getUserInfo().getScreenName();
            String status = ((BasicInfo) info).getStatus();

            for (String keyword_u : keywordsList_u)
            {
                //				Matcher matcher = Pattern.compile(keyword_u, Pattern.CASE_INSENSITIVE).matcher(username);
                if (keyword_u.equalsIgnoreCase(username))
                {
                    String replacedText = "";
                    for (int j = 0; j < status.length(); j++)
                    {
                        String filter = "#";
                        replacedText = replacedText + filter;
                    }

                    //Set Hidden Status
                    ((BasicInfo) info).setStatus(replacedText);
                    break;
                }

            }

        }
    }

    /**
     * 画面上に存在しているListViewにスクロールを検知するListenerを付与する。
     * 付与対象のListViewは下記。
     * -Public Timeline
     * -Mensions
     * -My TimeLine
     */
    private void addScrollListener()
    {
        Log.w("addScrollListener", "Method Called!");

        ListView publicTimeLine = (ListView) findViewById(R.id.list_time_line);
        publicTimeLine.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                Log.w("onScrollStateChanged", "Method Called!");
                Log.w("onScrollStateChanged", "firstVisibleItem = "
                        + firstVisibleItem);
                Log.w("onScrollStateChanged", "visibleItemCount = "
                        + visibleItemCount);
                Log.w("onScrollStateChanged", "totalItemCount = "
                        + totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState)
            {
                Log.w("onScrollStateChanged", "scrollState = " + scrollState);
            }
        });
    }

    //--------------------------------------------------------------------------
    /**
     * Handle the Auto Refresh Function<br>
     */
    //--------------------------------------------------------------------------
    private class AutoRefreshHandler extends Thread
    {

        boolean refreshFlag     = true;

        boolean autoRefreshLock = false;

        //--------------------------------------------------------------------------
        /**
         * Constructor<br>
         */
        //--------------------------------------------------------------------------
        public AutoRefreshHandler()
        {
            super("AutoRefreshHandler");
        }

        //--------------------------------------------------------------------------
        /**
         * Constructor<br>
         */
        //--------------------------------------------------------------------------
        public void lockAutoRefresh(boolean lock)
        {
            this.autoRefreshLock = lock;
        }

        //--------------------------------------------------------------------------
        /**
         * Stop this Thread.
         */
        //--------------------------------------------------------------------------
        public void stopAutoRefresh()
        {
            this.refreshFlag = false;
        }

        //--------------------------------------------------------------------------
        /**
         * Run
         */
        //--------------------------------------------------------------------------
        @Override
        public void run()
        {
            int currentPage;
            if (TimeLineActivity.this.currentTab == TAB_FRIEND_TIMELINE)
            {
                currentPage = TimeLineActivity.this.currentPage_homeTimeLine;
            }
            else if (TimeLineActivity.this.currentTab == TAB_AT_MESSAGE)
            {
                currentPage = TimeLineActivity.this.currentPage_atMessage;
            }
            else if (TimeLineActivity.this.currentTab == TAB_DIRECT_MESSAGE)
            {
                currentPage = TimeLineActivity.this.currentPage_directMessage;
            }
            else
            {
                currentPage = TimeLineActivity.this.currentPage_myTimeLine;
            }
            while (this.refreshFlag)
            {
                Long refreshTime = 0l;

                //get auto-refresh parameter in DB
                String autoRefreshFlag = TimeLineActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_REFRESH_FLAG);
                refreshTime = (Long.valueOf(TimeLineActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_REFRESH_TIME))) * 60000;

                //get interval
                long intervalTime = System.currentTimeMillis()
                        - TimeLineActivity.this.lastRefreshTime;

                if (autoRefreshFlag.equals(MyDbAdapter.PARAM_VALUE_ON)
                        && !TimeLineActivity.this.isRefreshing
                        && !this.autoRefreshLock && intervalTime > refreshTime
                        && currentPage == 1)
                {

                    //Send Message to Handler
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("type",
                            TimeLineActivity.this.HANDLER_TYPE_REFRESH_REQUEST);
                    msg.setData(bundle);
                    TimeLineActivity.this.mHandler.sendMessage(msg);

                }

                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
            }

        }
    }

}
