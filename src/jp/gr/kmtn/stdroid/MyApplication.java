package jp.gr.kmtn.stdroid;

import jp.gr.kmtn.stdroid.notification.NotificationCheckService;
import android.app.Application;
import android.content.Intent;


public class MyApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Start Notification
        //	MyDbAdapter myDb = new MyDbAdapter(this);
        //	String notificationValue = myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION);
        //	if(notificationValue != null
        //			&& notificationValue.equals(MyDbAdapter.PARAM_VALUE_ON)){
        Intent serviceIntent = new Intent(this, NotificationCheckService.class);
        startService(serviceIntent);
        //	}
    }

}
