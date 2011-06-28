package jp.gr.kmtn.stdroid.notification;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.TimeLineActivity;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.AccountInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;
import jp.gr.kmtn.stdroid.util.UrlConvertUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class NotificationCheckService extends Service
{

    public static boolean    isChecking               = false;

    private MyDbAdapter      myDbAdapter              = null;

    private static final int KEY_TYPE_ACTIVATION      = 0;

    private static final int KEY_TYPE_AT_MESSAGE      = 1;

    private static final int KEY_TYPE_DIRECT_MESSAGE  = 2;

    private static final int KEY_TYPE_GENERAL_MESSAGE = 3;

    // -----------------------------------------------------------------
    /**
     * Called Service is Created
     */
    // -----------------------------------------------------------------
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    // -----------------------------------------------------------------
    /**
     * Called Service is started
     */
    // -----------------------------------------------------------------
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        isChecking = true;

        //Init DB
        if (this.myDbAdapter == null)
        {
            this.myDbAdapter = new MyDbAdapter(this);
            this.myDbAdapter.open();
        }

        //Start Thread
        NewMessageChecker th = new NewMessageChecker();
        th.start();
    }

    // -----------------------------------------------------------------
    /**
     * Called Service is stopped
     */
    // -----------------------------------------------------------------
    @Override
    public void onDestroy()
    {
        //Close DB
        this.myDbAdapter.close();
        this.myDbAdapter = null;

        isChecking = false;

        super.onDestroy();
    }

    // -----------------------------------------------------------------
    /**
     * Handle the Location retrieve and sending message to server. It is
     * expected to be executed every "executeInterval" value which is configured
     * in Intent.
     */
    // -----------------------------------------------------------------
    private void showNotification(int newDirectMessageCount,
            int newAtMessageCount, int newGeneralMessageCount)
    {

        //-----------------------------
        // Prepare Notification Manager
        //-----------------------------
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.number = 0;
        Intent notificationIntent = new Intent(this, TimeLineActivity.class);
        Bundle b = new Bundle();
        if (checkNewDirectMessage() > 0)
        {
            b.putString("direct", "direct");
        }
        else if (checkNewAtMessage() > 0)
        {
            b.putString("atmessage", "atmessage");
        }
        else if (checkNewGeneralMessage() > 0)
        {
            b.putString("gmessage", "gmessage");
        }
        notificationIntent.putExtras(b);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //-----------------------------
        // Cancel Notification
        //-----------------------------
        if (!isActivated(NotificationCheckService.KEY_TYPE_ACTIVATION)
                || (newDirectMessageCount <= 0 && newAtMessageCount <= 0 && newGeneralMessageCount <= 0))
        {
            notificationManager.cancel(1);
            return;
        }

        //-----------------------------
        // Create Notification
        //-----------------------------
        StringBuffer sb = new StringBuffer();
        if (newDirectMessageCount > 0)
        {
            sb.append(getString(R.string.activity_timeline_tab_directmessage)
                    + "(" + newDirectMessageCount + ")" + "\n");
        }

        if (newAtMessageCount > 0)
        {
            sb.append(getString(R.string.activity_timeline_tab_at_message)
                    + "(" + newAtMessageCount + ")" + "\n");
        }

        if (newGeneralMessageCount > 0)
        {
            sb.append(getString(R.string.activity_mainsetting_generalmessage)
                    + "(" + newGeneralMessageCount + ")" + "\n");
        }

        notification.setLatestEventInfo(getApplicationContext(), "Crowdroid",
                sb.toString(), contentIntent);

        notificationManager.notify(1, notification);

    }

    // -----------------------------------------------------------------
    /**
     * check if New Message is exist.
     */
    // -----------------------------------------------------------------
    private int checkNewAtMessage()
    {

        //Return 0 if service is not activated
        if (!isActivated(NotificationCheckService.KEY_TYPE_ACTIVATION)
                || !isActivated(NotificationCheckService.KEY_TYPE_AT_MESSAGE))
        {
            return 0;
        }

        // Get newestAtMessageId from DB
        String newestAtMessageId = this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_NEWEST_AT_MESSAGE_ID);

        int count = -1;
        String status = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_AT_MESSAGE);
        this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);

        // Twitter
        if (status.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            CommunicationHandlerResult chResult = TwitterHandler.checkNewestAtMessage(newestAtMessageId);
            if (chResult.getResultCode() == 200)
            {
                count = (Integer) chResult.getData();
            }
        }

        if (count > 0)
        {// Exist
            return count;
        }
        else
        {
            return 0;
        }

    }

    // -----------------------------------------------------------------
    /**
     * check if New Direct Message is exist.
     */
    // -----------------------------------------------------------------
    private int checkNewDirectMessage()
    {

        //Return 0 if service is not activated
        if (!isActivated(NotificationCheckService.KEY_TYPE_ACTIVATION)
                || !isActivated(NotificationCheckService.KEY_TYPE_DIRECT_MESSAGE))
        {
            return 0;
        }

        // Get newestAtMessageId from DB
        String newestDirectMessageId = this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_NEWEST_DIRECT_MESSAGE_ID);
        this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_UID);

        int count = -1;
        this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);
        String status = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE);

        if (status.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            // Twitter
            CommunicationHandlerResult chResult = TwitterHandler.checkNewestDirectMessage(newestDirectMessageId);
            if (chResult.getResultCode() == 200)
            {
                count = (Integer) chResult.getData();
            }
        }

        if (count > 0)
        {// Exist
            return count;

        }
        else
        {
            return 0;
        }

    }

    // -----------------------------------------------------------------
    /**
     * check if New General Message is exist.
     */
    // -----------------------------------------------------------------
    private int checkNewGeneralMessage()
    {

        //Return 0 if service is not activated
        if (!isActivated(NotificationCheckService.KEY_TYPE_ACTIVATION)
                || !isActivated(NotificationCheckService.KEY_TYPE_GENERAL_MESSAGE))
        {
            return 0;
        }

        // Get newestAtMessageId from DB
        String newestGeneralMessageId = this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_NEWEST_GENERAL_MESSAGE_ID);
        int count = -1;
        this.myDbAdapter.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);

        // Twitter
        CommunicationHandlerResult chResult = TwitterHandler.checkNewestGeneralMessage(newestGeneralMessageId);
        if (chResult.getResultCode() == 200)
        {
            count = (Integer) chResult.getData();
        }

        if (count > 0)
        {// Exist
            return count;
        }
        else
        {
            return 0;
        }

    }

    // -----------------------------------------------------------------
    /**
     * On Bind
     */
    // -----------------------------------------------------------------
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Init Communication Handler
     */
    //-----------------------------------------------------------------------------
    private boolean initCommunicationHandler()
    {

        //Get Current Login Account Info
        AccountInfo accountInfo = this.myDbAdapter.getCurrentLoginAccountInfo();
        if (accountInfo == null)
        {
            return false;
        }

        String proxyFlag = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY);
        String apiProxyServer = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER);

        //Use Proxy
        if (proxyFlag != null && proxyFlag.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            TwitterHandler.setAccount(accountInfo.getAccessToken(),
                    accountInfo.getTokenSecret(),
                    TwitterHandler.AUTH_TYPE_BASIC, UrlConvertUtil.createBaseApiUrlFromBase(apiProxyServer));
        }
        else
        {//Use Original
            TwitterHandler.setAccount(accountInfo.getAccessToken(),
                    accountInfo.getTokenSecret(),
                    TwitterHandler.AUTH_TYPE_OAUTH, null);
        }

        return true;

    }

    //-----------------------------------------------------------------------------
    /**
     *  Check if Service is activated
     */
    //-----------------------------------------------------------------------------
    private boolean isActivated(int key)
    {

        if (this.myDbAdapter == null)
        {
            return false;
        }

        switch (key)
        {
        case KEY_TYPE_ACTIVATION:
            String activationFlag = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION);
            if (activationFlag != null
                    && activationFlag.equals(MyDbAdapter.PARAM_VALUE_ON))
            {
                return true;
            }
            else
            {
                return false;
            }

        case KEY_TYPE_DIRECT_MESSAGE:
            String directMessageFlag = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE);
            if (directMessageFlag != null
                    && directMessageFlag.equals(MyDbAdapter.PARAM_VALUE_ON))
            {
                return true;
            }
            else
            {
                return false;
            }

        case KEY_TYPE_AT_MESSAGE:
            String atMessageFlag = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_AT_MESSAGE);
            if (atMessageFlag != null
                    && atMessageFlag.equals(MyDbAdapter.PARAM_VALUE_ON))
            {
                return true;
            }
            else
            {
                return false;
            }

        case KEY_TYPE_GENERAL_MESSAGE:
            String generalMessageFlag = this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_GENERAL_MESSAGE);
            if (generalMessageFlag != null
                    && generalMessageFlag.equals(MyDbAdapter.PARAM_VALUE_ON))
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        return false;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Thread for Checking New Message
     */
    //-----------------------------------------------------------------------------
    private class NewMessageChecker extends Thread
    {

        private long timer = 57000;

        //-----------------------------------------------------------------------------
        /**
         *  Constructor
         */
        //-----------------------------------------------------------------------------
        public NewMessageChecker()
        {
            super("NewMessageChecker");
        }

        //-----------------------------------------------------------------------------
        /**
         *  Run
         */
        //-----------------------------------------------------------------------------
        @Override
        public void run()
        {

            while (isActivated(NotificationCheckService.KEY_TYPE_ACTIVATION))
            {
                try
                {
                    if (this.timer > 60000 && initCommunicationHandler())
                    {

                        int directMessageCount = checkNewDirectMessage();
                        sleep(5000);

                        int atMessageCount = checkNewAtMessage();
                        sleep(5000);

                        int generalMessageCount = checkNewGeneralMessage();
                        showNotification(directMessageCount, atMessageCount,
                                generalMessageCount);
                        this.timer = 0;
                    }

                    //sleep
                    this.timer = this.timer + 1000;
                    Thread.sleep(1000);

                }
                catch (InterruptedException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                    break;
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                    break;
                }
            }

            //Stop Service
            if (NotificationCheckService.this.myDbAdapter != null)
            {
                NotificationCheckService.this.myDbAdapter.updateSetting(
                        MyDbAdapter.PARAM_SETTING_NOTIFICATION,
                        MyDbAdapter.PARAM_VALUE_OFF);
            }
            showNotification(0, 0, 0);
            stopSelf();

        }

    }

}
