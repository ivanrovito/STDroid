package jp.gr.kmtn.stdroid.info;

//-----------------------------------------------------------------------------------
/**
 * This class is use for store the message from the xml files.<br>
 * You can get the value using getXXX() method.<br>
 * You can store the value using setXXX(String value) method.
 */
//-----------------------------------------------------------------------------------
public class TimeLineInfo extends BasicInfo
{

    private UserInfo retweetUserInfo = null;

    public TimeLineInfo()
    {

    }

    public void setRetweetUserInfo(UserInfo retweetUserInfo)
    {
        this.retweetUserInfo = retweetUserInfo;
    }

    public UserInfo getRetweetUserInfo()
    {
        return this.retweetUserInfo;
    }

}
