package com.anhuioss.crowdroid.longtweet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.anhuioss.crowdroid.util.CommunicationHandlerResult;

public class LongTweetHandler
{

    public static final String  API_SERVER  = "http://www.twitlonger.com/api_post";

    private static final String APPLICATION = "crowdroid";

    private static final String API_KEY     = "S2Uu24588d33Ii70";

    //--------------------------------------------------------------------
    /**
     * Get a Shrinked Massage with twitlong  link 
     */
    //--------------------------------------------------------------------
    public String getShirinkedMessage(String userName, String message)
    {

        //Get Shorten Url 
        String shortenUrl = registerMessageToAPI(userName, message);
        if (shortenUrl == null)
        {
            return null; //Failed
        }

        //Create new message
        String newMessage = combine(message, shortenUrl);

        return newMessage;
    }

    //--------------------------------------------------------------------------
    /**
     *  Register to Twit Longer. It will return shorten Url if suceeded.
     */
    //--------------------------------------------------------------------------
    private String registerMessageToAPI(String userName, String message)
    {

        String shortUrl = null;

        //Http
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("application", APPLICATION);
        paramMap.put("api_key", API_KEY);
        paramMap.put("username", userName);
        paramMap.put("message", message);

        CommunicationHandlerResult result;
        result = httpPost(API_SERVER, paramMap);

        //get shortenURL
        if (result.getResultCode() == 200)
        {
            shortUrl = parseXML(result.getMessage());
        }

        return shortUrl;

    }

    //--------------------------------------------------------------------------
    /**
     *  Conbine informatio and create 140 new Message.
     */
    //--------------------------------------------------------------------------
    private String combine(String message, String shortenURL)
    {

        String connectString = "... ";

        int reduceCount = message.length() + connectString.length()
                + shortenURL.length() - 140;
        if (reduceCount <= 0)
        {
            String newMessage = message + connectString + shortenURL;
            return newMessage;
        }
        else
        {
            String newMessage = message.substring(0, message.length()
                    - reduceCount)
                    + connectString + shortenURL;
            return newMessage;
        }

    }

    // -----------------------------------------------------------------------------------
    /**
     * Http Post
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult httpPost(String url,
            HashMap<String, String> map)
    {

        // Prepare Result Data
        CommunicationHandlerResult chResult = new CommunicationHandlerResult();
        int statusCode = 0;
        String msg = null;

        try
        {
            HttpPost httpPost = new HttpPost(url);

            // Set Post Parameters
            ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
            Set<String> set = map.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                postData.add(new BasicNameValuePair(key, map.get(key)));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
                    HTTP.UTF_8);
            httpPost.setEntity(entity);

            // Prepare HttpClient
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 1000); // Set
            // Connection
            // Time
            // Out
            HttpConnectionParams.setSoTimeout(params, 10000); // Set Data
            // Require
            // Time Out

            // Add Parameter in order to avoid 417 Error
            httpPost.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

            // Get Response
            HttpResponse response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            InputStream in = response.getEntity().getContent();
            msg = InputStreamToString(in);

        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            chResult.setResultCode(CommunicationHandlerResult.ERROR_COMMUNIATION_IOException);
        }
        finally
        {
            // Set result
            chResult.setMessage(msg);
            chResult.setResultCode(statusCode);
            return chResult;
        }

    }

    // -----------------------------------------------------------------------------------
    /**
     * Convert Input Stream to String
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    public static String InputStreamToString(InputStream is)
    {

        String value = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            StringBuffer buf = new StringBuffer();
            String str;

            while ((str = reader.readLine()) != null)
            {
                buf.append(str);
            }

            value = buf.toString();

        }
        catch (UnsupportedEncodingException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            return value;
        }

    }

    private String parseXML(String msg)
    {
        InputStream is = new ByteArrayInputStream(msg.getBytes());

        String newUrl = null;

        try
        {

            // Using Pull Parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            factory.setNamespaceAware(true);

            XmlPullParser xmlPullParser = factory.newPullParser();

            xmlPullParser.setInput(is, "UTF-8");

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
            {

                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                {

                    if (xmlPullParser.getName().equals("short"))
                    {

                        xmlPullParser.next();
                        newUrl = xmlPullParser.getText();
                        return newUrl;
                    }
                }
            }

            return newUrl;

        }
        catch (Exception e)
        {
            return newUrl;
        }

    }

}
