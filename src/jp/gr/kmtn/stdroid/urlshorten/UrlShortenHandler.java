package jp.gr.kmtn.stdroid.urlshorten;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

public class UrlShortenHandler
{

    private static final String URL_SHORTEN_API_KEY = "R_e453df42aeacd20e7fe5fc1b2ba515c4";

    private static final String url                 = "http://api.bit.ly/v3/";

    private static final String login               = "cnanhuioss";

    private static final String format              = "xml";

    public String getShortUrl(String longUrl)
    {
        String shortenUrl = null;
        try
        {
            Uri.encode(url + "shorten?login=" + login + "&apiKey="
                    + URL_SHORTEN_API_KEY + "&longUrl=" + longUrl + "&format="
                    + format);

            String encodedUrl = URLEncoder.encode(longUrl, "UTF-8");

            URL commUrl = new URL(url + "shorten?login=" + login + "&apiKey="
                    + URL_SHORTEN_API_KEY + "&longUrl=" + encodedUrl
                    + "&format=" + format);

            HttpURLConnection request = (HttpURLConnection) commUrl.openConnection();
            if (request.getResponseCode() == 200)
            {
                String str = inputStreamToString(request.getInputStream());

                //Parse XML
                shortenUrl = parseXML(str);
                /*				
                				JSONObject jObject = new JSONObject(str);
                			    JSONArray jArray = jObject.getJSONArray("data");
                			    for (int i = 0; i < jArray.length(); i++) {
                			        JSONObject result = jArray.getJSONObject(i);
                			        shortenUrl = result.getString("url");
                			    }
                			    */
            }

        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
            return null;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
            return null;
        }

        return shortenUrl;

    }

    @SuppressWarnings("finally")
    private String inputStreamToString(InputStream is)
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

                    if (xmlPullParser.getName().equals("url"))
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
