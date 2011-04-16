package jp.gr.kmtn.stdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.gr.kmtn.stdroid.IGeneral;
import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.TimeLineActivity;
import jp.gr.kmtn.stdroid.info.BasicInfo;
import jp.gr.kmtn.stdroid.info.TimeLineInfo;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.AccountInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


public class UserTimeLineDialog extends Dialog implements OnItemClickListener
{

    /** User Info */
    private UserInfo          userInfo;

    /** Current Page */
    private int               currentPage  = 1;

    /** Pre Button*/
    private Button            preButton;

    /** Next Button*/
    private Button            nextButton;

    /** Progress Dialog */
    private ProgressDialog    progress;

    /** Time Line Info List */
    ArrayList<TimeLineInfo>   timelineList = null;

    /** Current Login Account */
    private AccountInfo       loginAccount;

    /** MultiSelect Dialog */
    private MultiSelectDialog multiDialog;

    //-------------------------------------------------------------
    /**
     * Handler
     */
    //-------------------------------------------------------------
    Handler                   mHandler     = new Handler() {
                                               @Override
                                               public void handleMessage(
                                                       Message msg)
                                               {

                                                   //Close Progress Dialog
                                                   if (UserTimeLineDialog.this.progress != null)
                                                   {
                                                       UserTimeLineDialog.this.progress.dismiss();
                                                       UserTimeLineDialog.this.progress = null;
                                                   }

                                                   //Create List View
                                                   createListView();

                                                   //Change Status
                                                   changeStatus();

                                               }
                                           };

    //---------------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------------
    public UserTimeLineDialog(Context context, UserInfo info, MyDbAdapter db)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.setContentView(R.layout.dialog_user_timeline);

        //Set Data
        this.userInfo = info;
        this.multiDialog = new MultiSelectDialog(context, db);

        //Set Title
        BitmapDrawable drawable = new BitmapDrawable(
                this.userInfo.getUserImage());
        setFeatureDrawable(Window.FEATURE_LEFT_ICON, drawable);
        setTitle(this.userInfo.getScreenName()
                + context.getString(R.string.dialog_profile_status));

        //Get Current Account
        this.loginAccount = db.getCurrentLoginAccountInfo();

        //--------------------------------
        // Pre Button
        //--------------------------------
        this.preButton = (Button) findViewById(R.id.Button_user_message_pre);
        this.preButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (UserTimeLineDialog.this.currentPage > 1)
                {
                    refresh(UserTimeLineDialog.this.userInfo.getUid(),
                            UserTimeLineDialog.this.currentPage - 1);
                }
            }
        });
        this.preButton.setEnabled(false);

        //--------------------------------
        // Next Button
        //--------------------------------
        this.nextButton = (Button) findViewById(R.id.Button_user_message_more);
        this.nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                //			if(timelineList.size() == 20){
                refresh(UserTimeLineDialog.this.userInfo.getUid(),
                        UserTimeLineDialog.this.currentPage + 1);
                //			}
            }
        });
        this.nextButton.setEnabled(false);

        //--------------------------------
        // Close Button
        //--------------------------------
        Button closeButton = (Button) findViewById(R.id.Button_user_message_close);
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    //--------------------------------------------------------------------
    /**
     * Refresh Time Line Info List
     */
    //--------------------------------------------------------------------
    private void refresh(final String userId, final int page)
    {

        //Currently Twitter Only
        final String service = this.loginAccount.getService();
        if (service == null)
        {
            return;
        }

        //------------------
        // Open Dialog
        //------------------
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();

        //------------------
        // Start
        //------------------
        new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {

                //------------------------
                // Request
                //------------------------
                CommunicationHandlerResult result = null;

                result = TwitterHandler.getUserStatusList(userId,
                        String.valueOf(page));

                //------------------------
                // Get List
                //------------------------
                if (result.getResultCode() == 200)
                {
                    UserTimeLineDialog.this.timelineList = (ArrayList<TimeLineInfo>) result.getData();

                    //-----------
                    // Set Image
                    //-----------
                    for (TimeLineInfo ti : UserTimeLineDialog.this.timelineList)
                    {
                        ti.getUserInfo().setUserImage(
                                UserTimeLineDialog.this.userInfo.getUserImage());
                    }

                    UserTimeLineDialog.this.currentPage = page;

                }
                else
                {

                }

                UserTimeLineDialog.this.mHandler.sendEmptyMessage(0);

            }
        }).start();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Create List View
     */
    //-----------------------------------------------------------------------------
    private void createListView()
    {

        ListView listView = (ListView) findViewById(R.id.listView_user_status_favorite);
        ArrayList<Map<String, Object>> data = getDataFromInfo(this.timelineList);

        //-----------------
        // Set Adapter
        //-----------------
        SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                data,
                R.layout.list_item_favorite,
                new String[]
                { UserInfo.SCREENNAME, TimeLineInfo.STATUS, TimeLineInfo.TIME },
                new int[]
                { R.id.user_favorite_list_screenname,
                        R.id.user_favorite_list_status,
                        R.id.user_favorite_list_time });
        listView.setAdapter(adapter);

        //----------------------
        // Set Onclick Listener
        //----------------------
        listView.setOnItemClickListener(this);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Convert Info List to Data(HashMap) List.
     */
    //-----------------------------------------------------------------------------
    private ArrayList<Map<String, Object>> getDataFromInfo(ArrayList<?> infoList)
    {

        //Init Data List
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        //Convert
        if (infoList != null && infoList.size() != 0)
        {
            for (int i = 0; i < infoList.size(); i++)
            {
                UserInfo userInfo = ((BasicInfo) infoList.get(i)).getUserInfo();

                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(UserInfo.SCREENNAME, userInfo.getScreenName());
                item.put(TimeLineInfo.STATUS,
                        ((BasicInfo) infoList.get(i)).getStatus());
                item.put(
                        TimeLineInfo.TIME,
                        ((BasicInfo) infoList.get(i)).getFormatTime(IGeneral.SERVICE_NAME_TWITTER));
                data.add(item);
            }
        }
        return data;

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
        if (this.timelineList != null && this.timelineList.size() >= 15)
        {
            this.nextButton.setEnabled(true);
        }
        else
        {
            this.nextButton.setEnabled(false);
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Show
     */
    //-----------------------------------------------------------------------------
    @Override
    public void show()
    {
        super.show();
        refresh(this.userInfo.getUid(), 1);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Item in ListView was Clicked.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {

        //Open Multi Select Dialog
        this.multiDialog.setInfo(this.timelineList.get(position),
                TimeLineActivity.TAB_FRIEND_TIMELINE);
        this.multiDialog.show();
    }

}
