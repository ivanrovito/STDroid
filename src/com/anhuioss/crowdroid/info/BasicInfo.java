package com.anhuioss.crowdroid.info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

//-----------------------------------------------------------------------------------
/**
 * This class is use for store the message from the xml files.<br>
 * You can get the value using getXXX() method.<br>
 * You can store the value using setXXX(String value) method.
 */
//-----------------------------------------------------------------------------------
public abstract class BasicInfo
{

    public static final String MESSAGEID = "messageId";

    public static final String STATUS    = "status";

    public static final String TIME      = "time";

    /** Message ID*/
    private String             messageId;

    /**favorite flag*/
    private String             favorite;

    /** Status*/
    private String             status;

    /** Updated Time*/
    private String             time;

    /** User Info*/
    private UserInfo           userInfo;

    /**inReplyToStatusId to find reply who*/
    private String             inReplyToStatusId;

    //-----------------------------------------------------------------------------------
    /**
     * Constructor
     */
    //-----------------------------------------------------------------------------------
    public BasicInfo()
    {}

    //-----------------------------------------------------------------------------------
    /**
     * Set Message ID<br>
     * @param messageId
     */
    //-----------------------------------------------------------------------------------
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    //-----------------------------------------------------------------------------------
    /**
     * set the favorite method
     */
    //-----------------------------------------------------------------------------------
    public void setFavorite(String favorite)
    {
        this.favorite = favorite;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Set Status.<br>
     * @param status
     */
    //-----------------------------------------------------------------------------------
    public void setStatus(String status)
    {
        this.status = status;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Set Time.<br>
     * @param time
     */
    //-----------------------------------------------------------------------------------
    public void setTime(String time)
    {
        this.time = time;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Set User Info.<br>
     * @param userInfo
     * @return null
     */
    //-----------------------------------------------------------------------------------
    public void setUserInfo(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Set in reply status id.<br>
     * @param inreplytostatusid
     * @return null
     */
    //-----------------------------------------------------------------------------------
    public void setinReplyToStatusId(String inReplyToStatusId)
    {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Get Message ID.<br>
     * @return messageId <br>
     */
    //-----------------------------------------------------------------------------------
    public String getMessageId()
    {
        return this.messageId;
    }

    //-----------------------------------------------------------------------------------
    /**
     * get the getinReplyToStatusId<br>
     * @return String<br>
     */
    //-----------------------------------------------------------------------------------
    public String getinReplyToStatusId()
    {
        return this.inReplyToStatusId;
    }

    //-----------------------------------------------------------------------------------
    /**
     * get the favorite status<br>
     * @return String<br>
     */
    //-----------------------------------------------------------------------------------
    public String getFavorite()
    {
        return this.favorite;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Get Status.<br>
     * @return status
     */
    //-----------------------------------------------------------------------------------
    public String getStatus()
    {
        return this.status;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Get Time.<br>
     * @return time
     */
    //-----------------------------------------------------------------------------------
    public String getTime()
    {
        return this.time;
    }

    //------------------------------------------------------------------------------------
    /**
     * get format time
     * @return format time
     * @throws  
     */
    //-------------------------------------------------------------------------------------
    public String getFormatTime(String service)
    {

        String ttime = new String(this.time);
        //Prepare Format
        String DATE_PATTERN_OUT = "yyyy-MM-dd HH:mm:ss";
        String DATE_PATTERN_IN = null;

        // local change to China and Japan ,time will be wrong
        String month = ttime.substring(4, 7);
        if (month.equals("Jan"))
        {
            ttime = ttime.replace(month, "01");
        }
        else if (month.equals("Feb"))
        {
            ttime = ttime.replace(month, "02");
        }
        else if (month.equals("Mar"))
        {
            ttime = ttime.replace(month, "03");
        }
        else if (month.equals("Apr"))
        {
            ttime = ttime.replace(month, "04");
        }
        else if (month.equals("May"))
        {
            ttime = ttime.replace(month, "05");
        }
        else if (month.equals("Jun"))
        {
            ttime = ttime.replace(month, "06");
        }
        else if (month.equals("Jul"))
        {
            ttime = ttime.replace(month, "07");
        }
        else if (month.equals("Aug"))
        {
            ttime = ttime.replace(month, "08");
        }
        else if (month.equals("Sep"))
        {
            ttime = ttime.replace(month, "09");
        }
        else if (month.equals("Oct"))
        {
            ttime = ttime.replace(month, "10");
        }
        else if (month.equals("Nov"))
        {
            ttime = ttime.replace(month, "11");
        }
        else if (month.equals("Dec"))
        {
            ttime = ttime.replace(month, "12");
        }
        ttime = ttime.substring(4);
        DATE_PATTERN_IN = "MM dd HH:mm:ss Z yyyy";

        //Get Date Object from String
        SimpleDateFormat sourceFormat = new SimpleDateFormat(DATE_PATTERN_IN);
        Date date = null;
        try
        {
            date = sourceFormat.parse(ttime);
        }
        catch (ParseException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            return null;
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        //Get String from Date
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_OUT);
        String formatTime = sdf.format(date);

        return formatTime;

    }

    //-----------------------------------------------------------------------------------
    /**
     * Get User Info.<br>
     * @return userInfo
     */
    //-----------------------------------------------------------------------------------
    public UserInfo getUserInfo()
    {
        return this.userInfo;
    }

}
