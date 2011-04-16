package jp.gr.kmtn.stdroid.translate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


public class BingTranslate
{

    public static final String APIKEY = "9CBCCA72BFABFA9C1C3C75B8A62187487B3F9EC2";

    //-----------------------------------------------------------------------------------------------
    /**
     * Judge the language from text.
     * @throws UnsupportedEncodingException 
     */
    //-----------------------------------------------------------------------------------------------
    public static CommunicationHandlerResult detect(String query)
            throws UnsupportedEncodingException
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        //Prepare Parameters
        String url = "http://api.microsofttranslator.com/v2/Http.svc/Detect";
        String params = null;
        params = "?appId=" + APIKEY + "&text="
                + URLEncoder.encode(query, HTTP.UTF_8);

        //Send Request
        try
        {
            httpGet(url, params, result);
        }
        catch (ClientProtocolException e)
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

        //Parse
        if (result.getResultCode() == 200)
        {
            try
            {
                String language = null;
                try
                {
                    language = getXMLMessage(result.getMessage());
                }
                catch (XmlPullParserException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                catch (IOException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                result.setData(language);
            }
            catch (JSONException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(1001);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }

        }

        return result;
    }

    //-----------------------------------------------------------------------------------------------
    /**
     * Translate Text.
     * @throws UnsupportedEncodingException 
     */
    //-----------------------------------------------------------------------------------------------
    public static CommunicationHandlerResult translate(String query,
            String from, String to) throws UnsupportedEncodingException
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        //Prepare Parameters
        String url = "http://api.microsofttranslator.com/v2/Http.svc/Translate";
        String params = null;
        params = "?appId=" + APIKEY + "&text="
                + URLEncoder.encode(query, HTTP.UTF_8) + "&from=" + from
                + "&to=" + to;

        //Send Request
        try
        {
            httpGet(url, params, result);
        }
        catch (ClientProtocolException e)
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

        //Parse
        if (result.getResultCode() == 200)
        {
            try
            {
                String translatedText = null;
                try
                {
                    translatedText = getXMLMessage(result.getMessage());
                }
                catch (XmlPullParserException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                catch (IOException e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }
                result.setData(translatedText);
            }
            catch (JSONException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(1001);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
        }

        return result;
    }

    //-----------------------------------------------------------------------------------------------
    /**
     * Http Request (GET)
     */
    //-----------------------------------------------------------------------------------------------
    private static synchronized void httpGet(String url, String params,
            CommunicationHandlerResult result) throws ClientProtocolException,
            IOException
    {
        HttpGet httpGet = new HttpGet(url + params);

        //Prepare Http Client
        DefaultHttpClient httpClient = new DefaultHttpClient();

        //Connect
        HttpResponse response = httpClient.execute(httpGet);
        InputStream in = response.getEntity().getContent();
        String msg = InputStreamToString(in);

        int code = response.getStatusLine().getStatusCode();

        //Set Result
        result.setResultCode(code);
        result.setMessage(msg);
    }

    //-----------------------------------------------------------------------------------
    /**
     * Convert InputStream to String
     * @param is a instance of InputStream
     * @return String
     */
    //-----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static String InputStreamToString(InputStream is)
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

    //-----------------------------------------------------------------------------------
    /**
     * Parse xml and get translatedText.
     * @throws IOException 
     * @throws XmlPullParserException 
     */
    //-----------------------------------------------------------------------------------
    private static String getXMLMessage(String msg) throws JSONException,
            XmlPullParserException, IOException
    {

        String xmlMessage = null;
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
                if (xmlPullParser.getName().equals("string"))
                {
                    xmlPullParser.next();
                    xmlMessage = xmlPullParser.getText();
                    break;
                }
            }
        }

        return xmlMessage;
    }
}
