package jp.gr.kmtn.stdroid.translate;

import java.io.BufferedReader;
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
import org.json.JSONObject;

import android.util.Log;


//-----------------------------------------------------------------------------------------------
/**
 * Translation class using Google AJAX Language API.
 * http://code.google.com/intl/ja/apis/ajaxlanguage/documentation/
 * http://code.google.com/intl/ja/apis/ajaxlanguage/documentation/reference.html#_intro_fonje
 */
//-----------------------------------------------------------------------------------------------
public class GoogleTranslate
{

    //-----------------------------------------------------------------------------------------------
    /**
     * Judge the language from text.
     */
    //-----------------------------------------------------------------------------------------------
    public static CommunicationHandlerResult detect(String query)
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        //Prepare Parameters
        String url = "http://ajax.googleapis.com/ajax/services/language/detect";
        String params = null;
        try
        {
            params = "?v=1.0" + "&q=" + URLEncoder.encode(query, HTTP.UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(1500);
            return result;
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

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
                String language = getDetectedLanguage(result.getMessage());
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
     */
    //-----------------------------------------------------------------------------------------------
    public static CommunicationHandlerResult translate(String query,
            String from, String to)
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        //Prepare Parameters
        String url = "http://ajax.googleapis.com/ajax/services/language/translate";
        String params = null;
        try
        {
            params = "?v=1.0" + "&q=" + URLEncoder.encode(query, HTTP.UTF_8)
                    + "&langpair=" + from + "%7C" + to;
        }
        catch (UnsupportedEncodingException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(1500);
            return result;
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

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
                String translatedText = getTranslatedText(result.getMessage());
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
     * Parse JSON and get translatedText.
     */
    //-----------------------------------------------------------------------------------
    private static String getTranslatedText(String msg) throws JSONException
    {
        JSONObject clientJSONObj = new JSONObject(msg);
        JSONObject responseData = clientJSONObj.getJSONObject("responseData");
        String translatedText = responseData.getString("translatedText");;

        //GC
        System.gc();

        return translatedText;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Parse JSON and get language.
     */
    //-----------------------------------------------------------------------------------
    private static String getDetectedLanguage(String msg) throws JSONException
    {
        JSONObject clientJSONObj = new JSONObject(msg);
        JSONObject responseData = clientJSONObj.getJSONObject("responseData");
        String language = responseData.getString("language");

        //GC
        System.gc();

        return language;
    }

}
