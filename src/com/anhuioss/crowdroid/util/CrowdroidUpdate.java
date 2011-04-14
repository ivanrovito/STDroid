package com.anhuioss.crowdroid.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.Log;

import com.anhuioss.crowdroid.R;

public class CrowdroidUpdate
{

    Context context;

    String  update;

    File    myTempFile;

    String  path = "";

    public CrowdroidUpdate(Context context)
    {
        this.context = context;
    }

    //----------------------------------------------------------
    /**
     * check the version to update or not.<br>
     * return String type,if the string equals "no",then do not to update 
     */
    //-----------------------------------------------------------
    public String check(String versionName, String sdk)
    {
        InputStream in = null;
        String result = null;
        //throws ClientProtocolException, IOException, ParserConfigurationException, SAXException
        HttpPost httpPost = new HttpPost(
                "http://218.22.178.67:8080/VersionManagerServlet/version");
        //Set Post Parameters
        ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
        postData.add(new BasicNameValuePair("apk-version", versionName));
        postData.add(new BasicNameValuePair("sdk-version", sdk));
        UrlEncodedFormEntity entity;
        try
        {
            entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
            httpPost.setEntity(entity);
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        //Prepare HttpClient
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 1000); //Set Connection Time Out  
        HttpConnectionParams.setSoTimeout(params, 10000); //Set Data Require Time Out
        //Add Parameter in order to avoid 417 Error 
        httpPost.getParams().setBooleanParameter(
                CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        //Sine Request With Token
        HttpResponse response;
        try
        {
            response = httpClient.execute(httpPost);

            in = response.getEntity().getContent();
        }
        catch (ClientProtocolException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        //Get result

        // String msg = InputStreamToString(in);
        SAXParserFactory sf = SAXParserFactory.newInstance();//create a SAXParserFactory 
        SAXParser sp;
        try
        {
            sp = sf.newSAXParser();
            CrowdroidUpdateHandler reader = new CrowdroidUpdateHandler();
            sp.parse(new InputSource(in), reader);
            result = reader.getCrowdroid();
        }
        catch (ParserConfigurationException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        return result;//get XMLFeed
    }

    //----------------------------------------------------------------
    /**
     * show a alter dialog to user to update or not.<br>
     * if user select the yes,then the program will update the program.<br>
     */
    //--------------------------------------------------------------
    public void showUpdateDialogIfNeed(String packageVersion, String sdkVersion)
    {
        //throws ClientProtocolException, IOException, ParserConfigurationException, SAXException 
        this.update = check(packageVersion, sdkVersion);
        if (!this.update.equals("no"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle(R.string.dialog_crowdroidupdate_versionupdate);
            builder.setMessage(R.string.dialog_crowdroidupdate_updatecrowdroid);
            builder.setNegativeButton(android.R.string.yes,
                    new OnClickListener() {

                        public void onClick(DialogInterface dialog, int which)
                        {
                            try
                            {

                                URL imageUrl = new URL(
                                        "http://218.22.178.67:8080/VersionManagerServlet/download?path=Crowdroid.apk");

                                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                                conn.connect();

                                InputStream is = conn.getInputStream();

                                CrowdroidUpdate.this.myTempFile = File.createTempFile(
                                        "Crowdroid", "." + "apk");

                                FileOutputStream fos = new FileOutputStream(
                                        CrowdroidUpdate.this.myTempFile);

                                byte buf[] = new byte[128];
                                do
                                {
                                    int numread = is.read(buf);
                                    if (numread <= 0)
                                    {
                                        break;
                                    }
                                    fos.write(buf, 0, numread);
                                }
                                while (true);
                                is.close();
                            }
                            catch (NullPointerException e)
                            {
                                Log.w("StatusDroid", "Error Occured", e);
                            }
                            catch (FileNotFoundException e)
                            {
                                Log.w("StatusDroid", "Error Occured", e);
                            }
                            catch (SecurityException e)
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
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(
                                    Uri.fromFile(CrowdroidUpdate.this.myTempFile),
                                    "application/vnd.android.package-archive");
                            CrowdroidUpdate.this.context.startActivity(intent);
                        }
                    });
            builder.setPositiveButton(android.R.string.no,
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {}
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

}
