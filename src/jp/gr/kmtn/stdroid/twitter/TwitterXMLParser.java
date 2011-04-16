package jp.gr.kmtn.stdroid.twitter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.gr.kmtn.stdroid.info.DirectMessageInfo;
import jp.gr.kmtn.stdroid.info.TimeLineInfo;
import jp.gr.kmtn.stdroid.info.UserInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


public class TwitterXMLParser
{
    /**
     * Analyze XML data for timeLine and return TimeLine List
     * 
     * @param is
     * @return timeLineList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized List<TimeLineInfo> parseTimeLine(String msg)
            throws XmlPullParserException, IOException
    {

        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        List<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");

        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <status> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("status"))
                {
                    // Create new Instance of TimeLineInfo
                    TimeLineInfo timeLineInfo = new TimeLineInfo();

                    // Read Tags in status
                    analyzeStatus(xmlPullParser, timeLineInfo);
                    timeLineInfoList.add(timeLineInfo);
                }
            }
        }
        return timeLineInfoList;
    }

    /**
     * Analyze XML data for direct message and return DirectMessage List
     * 
     * @param is
     * @return directMessageList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized List<DirectMessageInfo> parseDirectMessage(
            String msg, String mode) throws XmlPullParserException, IOException
    {

        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        List<DirectMessageInfo> directMessageInfoList = new ArrayList<DirectMessageInfo>();

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");

        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <direct_message> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("direct_message"))
                {
                    // Create new Instance of DirectTimeLine
                    DirectMessageInfo directMessageInfo = new DirectMessageInfo();

                    // Read Tags in direct_message
                    analyzeDirectMessage(xmlPullParser, directMessageInfo, mode);
                    directMessageInfoList.add(directMessageInfo);
                }
            }
        }
        return directMessageInfoList;
    }

    /**
     * Analyze XML data for timeLine and return UserInformation
     * 
     * @param msg
     * @return TwitterHandler.userInfo
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized UserInfo parseUserInfo(String msg)
            throws XmlPullParserException, IOException
    {
        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");

        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                if (xmlPullParser.getName().equals("user"))
                {
                    // Create new Instance of TimeLineInfo
                    UserInfo userInfo = new UserInfo();

                    // Read Tags in status
                    analyzeUser(xmlPullParser, userInfo, "user");
                    return userInfo;
                }
            }
        }
        return null;
    }

    /**
     * Analyze XML data for timeLine and return TimeLine List
     * 
     * @param is
     * @return timeLineList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized int parseToGetNewAtMessageCount(
            String newestAtMessageId, String msg)
            throws XmlPullParserException, IOException
    {

        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        int count = 0;

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");
        Log.i("newest at message id", newestAtMessageId);
        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <status> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("status"))
                {
                    // Read Tags in status
                    while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                        {
                            if (xmlPullParser.getName().equals("created_at"))
                            {
                                xmlPullParser.next();
                                String time = xmlPullParser.getText();
                                String AtMessageId = getFormatTime(time);
                                Log.i("at message", xmlPullParser.getText());
                                if (AtMessageId.compareTo(newestAtMessageId) > 0)
                                {
                                    count++;
                                    Log.i("Count", String.valueOf(count)
                                            + " -- at message -- "
                                            + xmlPullParser.getText());
                                }
                                break;
                            }
                        }

                        // End status analyze
                        if (xmlPullParser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (xmlPullParser.getName().equals("status"))
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Analyze XML data for timeLine and return TimeLine List
     * 
     * @param is
     * @return timeLineList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized int parseToGetNewDirectMessageCount(
            String newestDirectMessageId, String msg)
            throws XmlPullParserException, IOException
    {
        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        int count = 0;

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");
        Log.i("user infomation list...", newestDirectMessageId);
        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <direct_message> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("direct_message"))
                {
                    // Read Tags in direct_message
                    while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                        {
                            if (xmlPullParser.getName().equals("created_at"))
                            {
                                xmlPullParser.next();
                                String time = xmlPullParser.getText();
                                String DirectMessageId = getFormatTime(time);
                                Log.i("direct message", xmlPullParser.getText());
                                if (DirectMessageId.compareTo(newestDirectMessageId) > 0)
                                {
                                    count++;
                                    Log.i("Count", String.valueOf(count)
                                            + " -- direct message -- "
                                            + xmlPullParser.getText());
                                }
                                break;
                            }
                        }

                        // End directMessage analyze
                        if (xmlPullParser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (xmlPullParser.getName().equals("direct_message"))
                            {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Analyze XML data for timeLine and return TimeLine List
     * 
     * @param is
     * @return timeLineList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized int parseToGetNewGeneralMessageCount(
            String newestGeneralMessageId, String msg)
            throws XmlPullParserException, IOException
    {
        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        int count = 0;

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");
        Log.i("user infomation list...", newestGeneralMessageId);
        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <direct_message> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("status"))
                {
                    // Read Tags in direct_message
                    while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                        {
                            if (xmlPullParser.getName().equals("created_at"))
                            {
                                xmlPullParser.next();
                                String time = xmlPullParser.getText();
                                String GeneralMessageId = getFormatTime(time);
                                Log.i("general message",
                                        xmlPullParser.getText());
                                if (GeneralMessageId.compareTo(newestGeneralMessageId) > 0)
                                {
                                    count++;
                                    Log.i("Count", String.valueOf(count)
                                            + " -- general message -- "
                                            + xmlPullParser.getText());
                                }
                                break;
                            }
                        }

                        // End directMessage analyze
                        if (xmlPullParser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (xmlPullParser.getName().equals("status"))
                            {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Analyze XML data for timeLine and return TimeLine List
     * 
     * @param is
     * @return timeLineList
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static synchronized String[] parserRelation(String msg)
            throws XmlPullParserException, IOException
    {
        InputStream is = new ByteArrayInputStream(msg.getBytes());

        // Prepare ResultList
        String[] relation = new String[2];

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(is, "UTF-8");

        //--------------------------------------------------------
        // Start Parsing XML Data
        //--------------------------------------------------------
        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {
                //--------------------------------------------------------
                // Analyze <status> tag
                //--------------------------------------------------------
                if (xmlPullParser.getName().equals("source"))
                {
                    // Read Tags in status
                    while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                        {
                            // Time Stamp
                            if (xmlPullParser.getName().equals("following"))
                            {
                                xmlPullParser.next();
                                relation[0] = xmlPullParser.getText();
                                continue;

                            }
                            // Message ID
                            if (xmlPullParser.getName().equals("followed_by"))
                            {
                                xmlPullParser.next();
                                relation[1] = xmlPullParser.getText();
                                continue;

                            }
                        }

                        // End status analyze
                        if (xmlPullParser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (xmlPullParser.getName().equals("source"))
                            {
                                return relation;
                            }
                        }
                    }
                }
            }
        }
        return relation;
    }

    public static synchronized Object[] parseUserInfoList(String msg)
            throws XmlPullParserException, IOException
    {

        Object[] result = new Object[2]; //[ArrayList<UserInfo>, String[2]]

        InputStream is = new ByteArrayInputStream(msg.getBytes());

        String[] cusor = new String[2];

        ArrayList<UserInfo> userInfoList = null;

        boolean flag = false;

        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(true);

        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(is, "UTF-8");

        int eventType = xmlPullParser.getEventType();

        boolean done = false;

        UserInfo info = null;

        while (eventType != XmlPullParser.END_DOCUMENT && !done)
        {
            String name = null;
            switch (eventType)
            {
            case XmlPullParser.START_DOCUMENT:
                userInfoList = new ArrayList<UserInfo>();
                break;
            case XmlPullParser.START_TAG:
                name = xmlPullParser.getName();
                if (name.equalsIgnoreCase("user"))
                {
                    info = new UserInfo();
                    flag = false;
                }
                else if (name.equalsIgnoreCase("status"))
                {
                    flag = true;
                }
                else if (name.equalsIgnoreCase("id") && !flag)
                {
                    info.setUid(xmlPullParser.nextText());
                }
                else if (name.equalsIgnoreCase("screen_name"))
                {
                    info.setScreenName(xmlPullParser.nextText());
                }
                else if (name.equals("next_cursor"))
                {
                    cusor[0] = xmlPullParser.nextText();
                }
                else if (name.equals("previous_cursor"))
                {
                    cusor[1] = xmlPullParser.nextText();
                }
                break;

            case XmlPullParser.END_TAG:
                name = xmlPullParser.getName();
                if (name.equalsIgnoreCase("user") && info != null)
                {
                    userInfoList.add(info);
                }
                else if (name.equalsIgnoreCase("users_list"))
                {
                    done = true;
                }
                break;
            }
            eventType = xmlPullParser.next();
        }

        result[0] = userInfoList;

        result[1] = cusor;

        return result;
    }

    private static synchronized int analyzeUser(XmlPullParser parser,
            UserInfo info, String type) throws XmlPullParserException,
            IOException
    {

        while (parser.next() != XmlPullParser.END_DOCUMENT)
        {

            if (parser.getEventType() == XmlPullParser.START_TAG)
            {
                // User ID
                if (parser.getName().equals("id"))
                {
                    parser.next();
                    info.setUid(parser.getText());
                    continue;
                }
                // User Image
                if (parser.getName().equals("profile_image_url"))
                {
                    parser.next();
                    info.setUserImageURL(parser.getText());
                    continue;
                }
                //Screen Name
                if (parser.getName().equals("screen_name"))
                {
                    parser.next();
                    info.setScreenName(parser.getText());
                    continue;
                }

                //Description
                if (parser.getName().equals("description"))
                {
                    parser.next();
                    info.setDescription(parser.getText());
                    continue;
                }

                //Friends Count
                if (parser.getName().equals("friends_count"))
                {
                    parser.next();
                    info.setFollowCount(parser.getText());
                    continue;
                }

                //Followers Count
                if (parser.getName().equals("followers_count"))
                {
                    parser.next();
                    info.setFollowerCount(parser.getText());
                    continue;
                }

                //Notifications
                if (parser.getName().equals("notifications"))
                {
                    parser.next();
                    info.setNotifications(parser.getText());
                    continue;
                }

                //Following
                if (parser.getName().equals("following"))
                {
                    parser.next();
                    info.setFollowing(parser.getText());
                    continue;
                }

                //Status (There are some condition which user contains a status field)
                if (parser.getName().equals("status"))
                {
                    while (parser.next() != XmlPullParser.END_DOCUMENT)
                    {

                        // Exit Status Tag
                        if (parser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (parser.getName().equals("status"))
                            {
                                break;
                            }
                        }
                    }

                    continue;
                }

            }

            // Exit user analyze
            if (parser.getEventType() == XmlPullParser.END_TAG)
            {
                if (parser.getName().equals(type))
                {
                    return 0;
                }
            }

        }
        return -1;

    }

    private static synchronized int analyzeStatus(XmlPullParser parser,
            TimeLineInfo timeLineInfo) throws XmlPullParserException,
            IOException
    {

        // Read Tags in status
        while (parser.next() != XmlPullParser.END_DOCUMENT)
        {

            if (parser.getEventType() == XmlPullParser.START_TAG)
            {

                // Time Stamp
                if (parser.getName().equals("created_at"))
                {
                    parser.next();
                    timeLineInfo.setTime(parser.getText());
                    continue;

                }
                // Message ID
                if (parser.getName().equals("id"))
                {
                    parser.next();
                    timeLineInfo.setMessageId(parser.getText());
                    continue;
                }
                // Text
                if (parser.getName().equals("text"))
                {
                    parser.next();
                    String text_change = parser.getText().replaceAll("&lt;",
                            "<").replaceAll("&gt;", ">").replace("〜", "~");
                    timeLineInfo.setStatus(text_change);
                    continue;

                }

                if (parser.getName().equals("in_reply_to_status_id"))
                {
                    parser.next();
                    timeLineInfo.setinReplyToStatusId(parser.getText());
                    continue;

                }

                //favorite
                if (parser.getName().equals("favorited"))
                {
                    parser.next();
                    timeLineInfo.setFavorite(parser.getText());
                    continue;
                }

                //--------------------------------------------------------
                // Analyze <retweeted_status> tag in <status>
                //--------------------------------------------------------
                if ((parser.getName().equals("retweeted_status")))
                {

                    while (parser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (parser.getEventType() == XmlPullParser.START_TAG)
                        {

                            //--------------------------------------------------------
                            // Analyze <user> tag in <retweeted_status>
                            //--------------------------------------------------------
                            if (parser.getName().equals("user"))
                            {

                                //Create User Info
                                UserInfo userInfo = new UserInfo();

                                analyzeUser(parser, userInfo, "user");
                                timeLineInfo.setRetweetUserInfo(userInfo);
                                continue;

                            }

                            //--------------------------------------------------------
                            // (Analyze <user> tag in <status>)
                            //--------------------------------------------------------		

                        }

                        if (parser.getEventType() == XmlPullParser.END_TAG)
                        {
                            if (parser.getName().equals("retweeted_status"))
                            {
                                break;
                            }
                        }

                    }

                }

                //--------------------------------------------------------
                // Analyze <user> tag in <status>
                //--------------------------------------------------------
                if (parser.getName().equals("user"))
                {

                    //Create User Info
                    UserInfo userInfo = new UserInfo();

                    analyzeUser(parser, userInfo, "user");
                    timeLineInfo.setUserInfo(userInfo);
                    continue;

                }
                //--------------------------------------------------------
                // (Analyze <user> tag in <status>)
                //--------------------------------------------------------

            }

            // End status analyze
            if (parser.getEventType() == XmlPullParser.END_TAG)
            {
                if (parser.getName().equals("status"))
                {
                    return 0;
                }
            }

        }
        return -1;

    }

    private static synchronized int analyzeDirectMessage(XmlPullParser parser,
            DirectMessageInfo directMessageInfo, String mode)
            throws XmlPullParserException, IOException
    {

        while (parser.next() != XmlPullParser.END_DOCUMENT)
        {

            if (parser.getEventType() == XmlPullParser.START_TAG)
            {

                // Time Stamp
                if (parser.getName().equals("created_at"))
                {
                    parser.next();
                    directMessageInfo.setTime(parser.getText());
                    parser.next();
                    continue;

                }
                // Message ID
                if (parser.getName().equals("id"))
                {
                    parser.next();
                    directMessageInfo.setMessageId(parser.getText());
                    continue;

                }
                // Text
                if (parser.getName().equals("text"))
                {
                    parser.next();
                    String text_change = parser.getText().replace("&lt;", "<").replace(
                            "&gt;", ">").replace("〜", "~");
                    directMessageInfo.setStatus(text_change);
                    continue;

                }
                //--------------------------------------------------------
                // Analyze <sender> tag in <direct_message>
                //--------------------------------------------------------
                if (parser.getName().equals("recipient"))
                {
                    if (mode.equals("send"))
                    {
                        UserInfo userInfo = new UserInfo();
                        analyzeUser(parser, userInfo, "recipient");
                        directMessageInfo.setUserInfo(userInfo);
                        continue;
                    }
                    else
                    {
                        while (parser.next() != XmlPullParser.END_DOCUMENT)
                        {

                            //NOthing to do

                            // Exit recipient analyze
                            if (parser.getEventType() == XmlPullParser.END_TAG)
                            {
                                if (parser.getName().equals("recipient"))
                                {
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                }
                //--------------------------------------------------------
                // (Analyze <sender> tag in <direct_message>)
                //--------------------------------------------------------

                //--------------------------------------------------------
                // Analyze <recipient> tag in <direct_message>
                //--------------------------------------------------------
                if (parser.getName().equals("sender"))
                {

                    if (mode.equals("send"))
                    {
                        while (parser.next() != XmlPullParser.END_DOCUMENT)
                        {

                            //NOthing to do

                            // Exit recipient analyze
                            if (parser.getEventType() == XmlPullParser.END_TAG)
                            {
                                if (parser.getName().equals("sender"))
                                {
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                    else
                    {
                        UserInfo userInfo = new UserInfo();
                        analyzeUser(parser, userInfo, "sender");
                        directMessageInfo.setUserInfo(userInfo);
                        continue;
                    }
                }
                //--------------------------------------------------------
                // (Analyze <recipient> tag in <direct_message>)
                //--------------------------------------------------------	

            }

            // End direct_message analyze
            if (parser.getEventType() == XmlPullParser.END_TAG)
            {
                if (parser.getName().equals("direct_message"))
                {
                    return 0;
                }
            }
        }
        return -1;
    }

    public static synchronized List<UserInfo> parseStrangersInfo(String msg)
            throws XmlPullParserException, IOException
    {

        InputStream is = new ByteArrayInputStream(msg.getBytes());
        List<UserInfo> infoList = new ArrayList<UserInfo>();
        UserInfo info = null;
        // Using Pull Parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(true);

        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(is, "UTF-8");

        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
        {

            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
            {

                if (xmlPullParser.getName().equals("user"))
                {

                    // Create new Instance of Userinfo
                    info = new UserInfo();
                    // Read Tags in status
                    analyzeUser(xmlPullParser, info, "user");
                    //analyzeStatus(xmlPullParser, timeLineInfo);
                    infoList.add(info);
                }
            }
        }

        return infoList;
    }

    public static synchronized String getFormatTime(String time)
    {
        String result;
        //Prepare Format
        String DATE_PATTERN_OUT = "yyyy-MM-dd HH:mm:ss";
        String DATE_PATTERN_IN = null;

        // local change to China and Japan ,time will be wrong
        String month = time.substring(4, 7);
        if (month.equals("Jan"))
        {
            time = time.replace(month, "01");
        }
        else if (month.equals("Feb"))
        {
            time = time.replace(month, "02");
        }
        else if (month.equals("Mar"))
        {
            time = time.replace(month, "03");
        }
        else if (month.equals("Apr"))
        {
            time = time.replace(month, "04");
        }
        else if (month.equals("May"))
        {
            time = time.replace(month, "05");
        }
        else if (month.equals("Jun"))
        {
            time = time.replace(month, "06");
        }
        else if (month.equals("Jul"))
        {
            time = time.replace(month, "07");
        }
        else if (month.equals("Aug"))
        {
            time = time.replace(month, "08");
        }
        else if (month.equals("Sep"))
        {
            time = time.replace(month, "09");
        }
        else if (month.equals("Oct"))
        {
            time = time.replace(month, "10");
        }
        else if (month.equals("Nov"))
        {
            time = time.replace(month, "11");
        }
        else if (month.equals("Dec"))
        {
            time = time.replace(month, "12");
        }
        time = time.substring(4);
        DATE_PATTERN_IN = "MM dd HH:mm:ss Z yyyy";

        //Get Date Object from String
        SimpleDateFormat sourceFormat = new SimpleDateFormat(DATE_PATTERN_IN);
        Date date = null;
        try
        {
            date = sourceFormat.parse(time);
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

        result = formatTime.replaceAll("-", " ").replaceAll(":", " ").replaceAll(
                " ", "");
        return result;

    }
}
