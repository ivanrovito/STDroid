package jp.gr.kmtn.stdroid.dialog;

import java.io.IOException;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.ImageBuilder;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProfileDialog extends Dialog
{

    private ImageView      userImageView;

    private TextView       profileText1;

    private TextView       profileText2;

    private TextView       followedStatus;

    private CheckBox       followingStatus;

    private ImageButton    userStatusButton;

    private Button         closeButton;

    private UserInfo       info           = null;

    private ProgressDialog progress       = null;

    private String         CurrentService = null;

    private String[]       relation;

    private MyDbAdapter    db;

    Handler                mHandler       = new Handler() {
                                              @Override
                                              public void handleMessage(
                                                      Message msg)
                                              {

                                                  if (ProfileDialog.this.progress != null)
                                                  {
                                                      ProfileDialog.this.progress.dismiss();
                                                  }
                                                  //Close Progress Dialog

                                                  int type = msg.getData().getInt(
                                                          "type");

                                                  if (type == 5)
                                                  {//Set Info
                                                      setInfo(ProfileDialog.this.info);
                                                  }
                                                  else if (type == 6)
                                                  { //Could not find user

                                                      Toast toast = Toast.makeText(
                                                              getContext(),
                                                              R.string.dialog_profile_user_not_found,
                                                              Toast.LENGTH_LONG);
                                                      toast.show();
                                                      dismiss();
                                                  }
                                                  else
                                                  {
                                                      refreshProfile(
                                                              ProfileDialog.this.relation,
                                                              type);
                                                  }

                                              }

                                          };

    //-----------------------------------------------------------------------------
    /**
     *  Constructor
     */
    //-----------------------------------------------------------------------------
    public ProfileDialog(Context context, MyDbAdapter dbAdapter)
    {
        super(context);
        super.setContentView(R.layout.dialog_profile);

        this.db = dbAdapter;
        this.CurrentService = this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);
        //Init Views
        this.userImageView = (ImageView) findViewById(R.id.user_image);
        this.profileText1 = (TextView) findViewById(R.id.profile1);
        this.profileText2 = (TextView) findViewById(R.id.profile2);
        this.followedStatus = (TextView) findViewById(R.id.followedStatus);

        //Following status
        this.followingStatus = (CheckBox) findViewById(R.id.followingStatus);
        this.followingStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                //true -- destroy     false -- create
                sendFollowRequest(ProfileDialog.this.followingStatus.isChecked());

            }

        });

        this.closeButton = (Button) findViewById(R.id.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        this.userStatusButton = (ImageButton) findViewById(R.id.showDetailListButton);
        this.userStatusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                ProfileDialog.this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_UID);
                UserTimeLineDialog dialog = new UserTimeLineDialog(
                        getContext(), ProfileDialog.this.info,
                        ProfileDialog.this.db);
                dialog.show();

                /*
                UserFavoriteDialog mUserFavoriteDialog = new UserFavoriteDialog(getContext());
                String currentuid = db.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_UID);
                if(info.getUid().equals(currentuid))
                	mUserFavoriteDialog.setTitle(R.string.dialog_profile_myfavoritestatus);
                else
                	mUserFavoriteDialog.setTitle(info.getScreenName()+ getContext().getString(R.string.dialog_profile_status));
                mUserFavoriteDialog.setDialogInfo(info, TimeLineActivity.TAB_FRIEND_TIMELINE, currentuid,1);
                mUserFavoriteDialog.show();
                */
            }
        });
    }

    @Override
    public void show()
    {
        super.show();
        if (this.info == null)
        {
            this.progress = new ProgressDialog(getContext());
            this.progress.setIndeterminate(false);
            this.progress.show();
        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Set Info.
     */
    //-----------------------------------------------------------------------------
    public void setInfo(UserInfo info)
    {
        this.info = info;
        if (info.getFollowCount() == null || info.getUserImage() == null)
        {
            setInfo(info.getScreenName());
        }

        Bitmap image = info.getUserImage();
        //Set Data to View
        this.userImageView.setImageBitmap(image);
        String name = info.getScreenName();
        setTitle(name);
        String count2 = info.getFollowCount();
        String count1 = info.getFollowerCount();

        if (count1 == null || count2 == null)
        {
            this.profileText1.setText(String.format(getContext().getString(
                    R.string.dialog_profile_follower), " ", " "));
        }
        else
        {
            this.profileText1.setText(String.format(getContext().getString(
                    R.string.dialog_profile_follower), count1, count2));
        }

        this.profileText2.setText(info.getDescription());
        //Check follow/following status

        getFollowStatus();
    }

    public void setInfo(final String screenName)
    {
        //Prepare Thread
        Thread th = new Thread(new Runnable() {

            @Override
            public void run()
            {

                boolean isUserFound = false;

                ProfileDialog.this.info = (UserInfo) TwitterHandler.getUserInfo(
                        screenName).getData();

                if (ProfileDialog.this.info != null)
                {
                    isUserFound = true;
                    try
                    {
                        ProfileDialog.this.info.setUserImage(ImageBuilder.returnBitMap(ProfileDialog.this.info.getUserImageURL()));
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

                Message message = new Message();
                Bundle bundle = new Bundle();

                if (isUserFound)
                {
                    bundle.putInt("type", 5);
                }
                else
                {
                    bundle.putInt("type", 6);
                }

                message.setData(bundle);

                //Handler
                ProfileDialog.this.mHandler.sendMessage(message);

            }
        }, "sendRelationMessageThread");
        th.start();

    }

    private void refreshProfile(String data[], int type)
    {

        //type (0=getResult / 1=sendResult)

        if (type == 0)
        {

            if (data == null)
            {
                //Communication Error
                Toast.makeText(
                        getContext(),
                        getContext().getString(
                                R.string.tost_dialog_profile_communicationfailed),
                        Toast.LENGTH_SHORT).show();
                //Disable checkButton
                this.followingStatus.setEnabled(false);
                return;
            }

            else if (data[0] == null || data[1] == null)
            {
                //Server Error may occurred
                Toast.makeText(
                        getContext(),
                        getContext().getString(
                                R.string.tost_dialog_profile_serverError),
                        Toast.LENGTH_SHORT).show();
                this.followingStatus.setEnabled(false);
                return;
            }

        }

        String followed = data[1];
        String following = data[0];

        if (followed.equals("true"))
        {
            this.followedStatus.setText(R.string.dialog_profile_followedbyuser);
        }
        else
        {
            this.followedStatus.setText(R.string.dialog_profile_notfollowerdbyser);
        }

        if (following.equals("true"))
        {
            this.followingStatus.setChecked(true);
            this.followingStatus.setText(R.string.dialog_profile_followinguser);
        }
        else
        {
            this.followingStatus.setChecked(false);
            this.followingStatus.setText(R.string.dialog_profile_notfollowinguser);
        }

    }

    private void sendFollowRequest(final boolean follow)
    {

        this.db.getCurrentLoginAccountInfo();

        //Prepare Thread
        Thread thread = new Thread(new Runnable() {

            //			int result = 0;
            CommunicationHandlerResult result = new CommunicationHandlerResult();

            @Override
            public void run()
            {
                this.result = TwitterHandler.setFollow(
                        ProfileDialog.this.info.getUid(), follow);

                if (this.result.getResultCode() == 200)
                {
                    ProfileDialog.this.relation[0] = Boolean.toString(follow);
                }

                //resultSend (0=success/1=failed/-1=failed with error)			
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);

                message.setData(bundle);

                //Handler
                ProfileDialog.this.mHandler.sendMessage(message);

            }
        }, "sendFollowRequestThread");
        thread.start();

    }

    private void getFollowStatus()
    {

        this.db.getCurrentLoginAccountInfo();

        final String targetId = this.info.getUid();
        this.info.getScreenName();
        //Prepare Thread
        Thread th = new Thread(new Runnable() {

            @Override
            public void run()
            {
                ProfileDialog.this.relation = (String[]) TwitterHandler.showRelation(
                        targetId).getData();

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                message.setData(bundle);

                //Handler
                ProfileDialog.this.mHandler.sendMessage(message);

            }
        }, "sendRelationMessageThread");
        th.start();
    }
}
