package jp.gr.kmtn.stdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.gr.kmtn.stdroid.IGeneral;
import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


public class UserSelectDialog extends Dialog
{

    private int                 mode                = 0;

    public static final int     MODE_DIRECT_MESSAGE = 4;

    public static final int     MODE_PROFILE        = 2;

    public static final int     FRIENDS             = 0;

    public static final int     FOLLOWERS           = 0;

    /** Next Cursor (Twitter) */
    private long                next_cursor         = 0;

    /** Pre Cursor (Twitter) */
    private long                pre_cursor          = 0;

    /** List View */
    private ListView            listView;

    /** User Info List */
    private ArrayList<UserInfo> userInfoList;

    /** Progress */
    private ProgressDialog      progress;

    /** Pre Button */
    private Button              preButton           = null;

    /** Next Button */
    private Button              nextButton          = null;

    /** Close Button */
    private Button              closeButton;

    /** DB Adapter */
    private MyDbAdapter         db;

    Handler                     mHandler            = new Handler() {
                                                        @Override
                                                        public void handleMessage(
                                                                Message msg)
                                                        {

                                                            // Close Progress Dialog
                                                            UserSelectDialog.this.progress.dismiss();

                                                            // Set List View
                                                            if (UserSelectDialog.this.userInfoList != null)
                                                            {
                                                                setListView();
                                                            }

                                                            changeStatus();
                                                        }
                                                    };

    // -----------------------------------------------------------------------------
    /**
     * Constructor
     */
    // -----------------------------------------------------------------------------
    public UserSelectDialog(Context context, final int setmode,
            MyDbAdapter dbAdapter)
    {

        super(context);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.setContentView(R.layout.dialog_user_select);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                IGeneral.OPTION_MENU_IMAGE_DIRECT_MESSAGE);

        setTitle(IGeneral.OPTION_MENU_TEXT_DIRECT_MESSAGE);

        this.db = dbAdapter;

        // Init Views
        this.listView = (ListView) findViewById(R.id.user_select_listview);
        ArrayList<String> spinnnerChoice = new ArrayList<String>();
        spinnnerChoice.add(getContext().getString(
                R.string.dialog_userselect_Getfolowerslist));
        spinnnerChoice.add(getContext().getString(
                R.string.dialog_userselect_GetFriendslist));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, spinnnerChoice);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.mode = setmode;

        //---------------------
        // Close Button
        //---------------------
        this.closeButton = (Button) findViewById(R.id.close_button);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        //---------------------
        // Pre Button
        //---------------------
        this.preButton = (Button) findViewById(R.id.prev_button);
        this.preButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                refreshList(UserSelectDialog.this.pre_cursor);
            }
        });
        this.preButton.setEnabled(false);

        //---------------------
        // Next Button
        //---------------------
        this.nextButton = (Button) findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                refreshList(UserSelectDialog.this.next_cursor);
            }
        });
        this.nextButton.setEnabled(false);

    }

    // -----------------------------------------------------------------------------
    /**
     * Show Dialog
     */
    // -----------------------------------------------------------------------------
    @Override
    public void show()
    {
        super.show();
        refreshList(-1);

    }

    // -----------------------------------------------------------------------------
    /**
     * Refresh User Info List.
     */
    // -----------------------------------------------------------------------------
    public void refreshList(final long cusor)
    {

        this.db.getCurrentLoginAccountInfo();

        // show progress
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();

        // Prepare Thread
        Thread thread = new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {

                CommunicationHandlerResult result = null;

                result = TwitterHandler.getFollowersList(cusor);
                if (result != null && result.getResultCode() == 200)
                {
                    Object[] data = (Object[]) result.getData();
                    UserSelectDialog.this.userInfoList = (ArrayList<UserInfo>) data[0];

                    String[] cursors = (String[]) data[1];
                    UserSelectDialog.this.next_cursor = Long.valueOf(cursors[0]);
                    UserSelectDialog.this.pre_cursor = Long.valueOf(cursors[1]);
                }

                // Handler
                UserSelectDialog.this.mHandler.sendEmptyMessage(0);

            }
        }, "requestUserInfoListThread"); // Thread Name
        thread.start();

    }

    // -----------------------------------------------------------------------------
    /**
     * Set List View
     */
    // -----------------------------------------------------------------------------
    public void setListView()
    {

        if (this.userInfoList == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialog_userselect_Nomorefriends);
            builder.setCancelable(true);
            builder.show();
        }
        else
        {
            // Prepare Data in List View
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < this.userInfoList.size(); i++)
            {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(UserInfo.SCREENNAME,
                        this.userInfoList.get(i).getScreenName());
                data.add(item);
            }

            // Prepare Adapter
            SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                    R.layout.list_item_friends_or_followers, new String[]
                    { UserInfo.SCREENNAME }, new int[]
                    { R.id.followers_or_friends_screen_name });

            this.listView.setAdapter(adapter);

            // Set Click Listener
            this.listView.setOnItemClickListener(new OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id)
                {

                    if (UserSelectDialog.this.mode == MODE_DIRECT_MESSAGE)
                    {
                        UpdateDialog dialog = new UpdateDialog(getContext(),
                                UpdateDialog.MODE_DIRECT_MESSAGE,
                                UserSelectDialog.this.db);
                        dialog.setTarget(
                                UserSelectDialog.this.userInfoList.get(position).getScreenName(),
                                UserSelectDialog.this.userInfoList.get(position).getUid());
                        dialog.show();

                    }
                    else if (UserSelectDialog.this.mode == MODE_PROFILE)
                    {}
                }
            });

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
        if (this.pre_cursor == 0)
        {
            this.preButton.setEnabled(false);
        }
        else
        {
            this.preButton.setEnabled(true);
        }

        //Next Button
        if (this.next_cursor == 0)
        {
            this.nextButton.setEnabled(false);
        }
        else
        {
            this.nextButton.setEnabled(true);
        }

    }

}
