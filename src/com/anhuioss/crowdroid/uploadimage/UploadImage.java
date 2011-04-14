package com.anhuioss.crowdroid.uploadimage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;
import com.anhuioss.crowdroid.util.MultiPartFormOutputStream;

public class UploadImage
{
    // ---------------------------------------------------------------------
    /**
     * upload image from local
     * 
     * @param serverMessage
     *            serverMessage[0]: the upload method, use oauth or anonymous
     *            serverMessage[1]: the oauth_token serverMessage[2]: the
     *            oauth_secret serverMessage[3]: consumer_token
     *            serverMessage[4]: consumer_secret serverMessage [5]: API_KEY,
     *            the server API key serverMessage
     * @throws IOException
     */
    // ---------------------------------------------------------------------
    public static CommunicationHandlerResult UploadImageToServer(
            String filePath, String[] serverMessage)
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        String auth_type = serverMessage[0];
        String consumer_token = serverMessage[1];
        String consumer_secret = serverMessage[2];
        String oauth_token = serverMessage[3];
        String oauth_secret = serverMessage[4];
        String apikey = serverMessage[5];

        if (auth_type.equals(TwitterHandler.AUTH_TYPE_OAUTH))
        {
            // use oauth server to upload image such as twitpic...
            result = uploadWithOauth(consumer_token, consumer_secret,
                    oauth_token, oauth_secret, apikey, filePath);
        }
        else
        {
            // use Anonymous server to upload the image, such as imageur...
            result = uploadWithAnonymous(apikey, filePath);
        }
        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * upload image with Anonymous
     * 
     * @return CommunicationHandlerResult
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult uploadWithAnonymous(
            String apikey, String filePath)
    {

        CommunicationHandlerResult result = new CommunicationHandlerResult();

        try
        {
            synchronized (IGeneral.commLock__)
            {
                URL url = new URL("http://api.imgur.com/2/upload.json");

                // create a boundary string
                String boundary = MultiPartFormOutputStream.createBoundary();
                HttpURLConnection connection = MultiPartFormOutputStream.createConnection(url);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Content-Type",
                        MultiPartFormOutputStream.getContentType(boundary));

                // set some other request headers...
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                // Put Parameters
                MultiPartFormOutputStream out = new MultiPartFormOutputStream(
                        connection.getOutputStream(), boundary);
                out.writeField("key", apikey);

                // upload a file
                out.writeFile("image", "text/plain", new File(filePath));
                out.close();

                // Set Result
                int code = connection.getResponseCode();
                // get image url xml file
                InputStream in = connection.getInputStream();
                result.setResultCode(code);
                result.setData(TwitterHandler.InputStreamToString(in));
            }
        }
        catch (MalformedURLException e)
        {
            result.setResultCode(1010);
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IOException e)
        {
            result.setResultCode(1011);
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);// fix exception
            // TODO: handle exception
        }
        finally
        {
            return result;
        }
    }

    // -----------------------------------------------------------------------------------
    /**
     * upload image with oauth
     * 
     * @return CommunicationHandlerResult
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult uploadWithOauth(
            String consumer_token, String consumer_secret, String oauth_token,
            String oauth_secret, String apikey, String filePath)
    {
        synchronized (IGeneral.commLock__)
        {

            CommunicationHandlerResult result = new CommunicationHandlerResult();

            try
            {
                URL url = new URL("http://api.twitpic.com/1/upload.json");

                // create a boundary string
                String boundary = MultiPartFormOutputStream.createBoundary();
                HttpURLConnection connection = MultiPartFormOutputStream.createConnection(url);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Content-Type",
                        MultiPartFormOutputStream.getContentType(boundary));

                // set some other request headers...
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                // Put Parameters
                MultiPartFormOutputStream out = new MultiPartFormOutputStream(
                        connection.getOutputStream(), boundary);

                out.writeField("key", apikey);
                out.writeField("consumer_token", consumer_token);
                out.writeField("consumer_secret", consumer_secret);
                out.writeField("oauth_token", oauth_token);
                out.writeField("oauth_secret", oauth_secret);
                // upload a file
                out.writeFile("media", "text/plain", new File(filePath));
                out.close();

                // Set Result
                int code = connection.getResponseCode();
                // get image url xml file
                InputStream in = connection.getInputStream();

                result.setResultCode(code);

                result.setData(TwitterHandler.InputStreamToString(in));

            }
            catch (MalformedURLException e)
            {
                result.setResultCode(1010);
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (IOException e)
            {
                result.setResultCode(1011);
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e); // fix exception
                // TODO: handle exception
            }
            finally
            {
                return result;
            }
        }
    }
}
