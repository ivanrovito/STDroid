package jp.gr.kmtn.stdroid.dialog;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.urlshorten.UrlShortenHandler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class UrlShortenDialog extends Dialog
{

    private EditText target;

    private EditText longUrlText;

    private EditText shortenUrlText;

    private Button   btnConvert;

    private Button   okButton;

    /** Handler */
    Handler          myHandler = new Handler() {

                                   @Override
                                   public void handleMessage(Message msg)
                                   {

                                       //Set to Shorten URL Area
                                       Bundle data = msg.getData();
                                       String shortenUrl = data.getString("new-url");
                                       if (shortenUrl != null)
                                       {
                                           UrlShortenDialog.this.shortenUrlText.setText(shortenUrl);
                                       }

                                   }
                               };

    //-------------------------------------------------------------------
    /**
     * Constructor
     */
    //-------------------------------------------------------------------
    public UrlShortenDialog(Context context, EditText target)
    {
        super(context);
        super.setContentView(R.layout.dialog_url_shorten);

        this.target = target;

        //Title
        setTitle(context.getString(R.string.dialog_activity_message_sent_shortenurl));

        //Long URL
        this.longUrlText = (EditText) findViewById(R.id.willconvert);
        this.longUrlText.setEnabled(false);

        //Short URL
        this.shortenUrlText = (EditText) findViewById(R.id.haveconverted);
        this.shortenUrlText.setEnabled(false);

        //Convert Button
        this.btnConvert = (Button) findViewById(R.id.convert);
        this.btnConvert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String longUrl = UrlShortenDialog.this.longUrlText.getText().toString();
                if (longUrl == null || longUrl.length() == 0)
                {
                    return;
                }
                getShortenUrl();
            }
        });

        //OK Button
        this.okButton = (Button) findViewById(R.id.okButton);
        this.okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                String sortenUrl = UrlShortenDialog.this.shortenUrlText.getText().toString();
                if (sortenUrl == null || sortenUrl.length() == 0)
                {
                    return;
                }

                replaceUrl();
                dismiss();
            }
        });

        //Cancel Button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();

            }
        });

        //Extract URL
        extractUrl();

    }

    //-------------------------------------------------------------------
    /**
     *  Extract URL pattern and set to longUrlText Area.
     */
    //-------------------------------------------------------------------
    private void extractUrl()
    {

        //Get Message
        String message = this.target.getText().toString();

        //Extract http://---
        ArrayList<String> urlList = new ArrayList<String>();
        Pattern pattern = Pattern.compile(
                "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        while (matcher.find())
        {
            urlList.add(matcher.group());
        }

        //Set to Text View
        String longUrl = null;
        if (urlList.size() > 0)
        {
            longUrl = urlList.get(0); //Get first URL pattern
        }

        if (longUrl != null)
        {
            this.longUrlText.setText(longUrl);
        }
        else
        {
            this.longUrlText.setText("");
        }
    }

    //-------------------------------------------------------------------
    /**
     *  Get Shorten Url
     */
    //-------------------------------------------------------------------
    private void getShortenUrl()
    {

        //Thread
        Thread th = new Thread(new Runnable() {
            public void run()
            {
                String longUrl = UrlShortenDialog.this.longUrlText.getText().toString();
                UrlShortenHandler handler = new UrlShortenHandler();
                String shortenUrl = handler.getShortUrl(longUrl);

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("new-url", shortenUrl);
                msg.setData(data);

                UrlShortenDialog.this.myHandler.sendMessage(msg);

            }
        }, "requesting shorten url");

        th.start();
    }

    //-------------------------------------------------------------------
    /**
     *  Replace with new URL
     */
    //-------------------------------------------------------------------
    private void replaceUrl()
    {
        String originalMsg = this.target.getText().toString();
        String longUrl = this.longUrlText.getText().toString();
        String shortenUrl = this.shortenUrlText.getText().toString();

        /*  Pattern pattern = Pattern.compile("<.*?>");  
          Matcher matcher =pattern.matcher(longUrl);  
          while (matcher.find()) {
           matcher.replaceAll("\\?");
          }
        */
        //String tmp = longUrl.replaceAll("[?]", "\\?");
        longUrl = longUrl.replaceAll("\\?", "\\\\?");
        //System.out.print(tmp);

        String replacedMsg = originalMsg.replaceFirst(longUrl, shortenUrl);
        this.target.setText(replacedMsg);
    }

}
