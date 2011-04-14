package com.anhuioss.crowdroid.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.longtweet.LongTweetHandler;
import com.anhuioss.crowdroid.util.AccountInfo;

public class LongTweetDialog extends Dialog
{

    private EditText       originalMsg;

    private Button         btnTwitLonger;

    private EditText       shrinkedMsg;

    private Button         btnOk;

    private Button         btnCancel;

    private EditText       target;

    private AccountInfo    currentLoginAccountInfo;

    private boolean        isShrinking = false;

    /** Progress Dialog */
    private ProgressDialog progress;

    Handler                myHandler   = new Handler() {
                                           @Override
                                           public void handleMessage(Message msg)
                                           {
                                               LongTweetDialog.this.progress.dismiss();

                                               Bundle bundle = msg.getData();
                                               String newText = bundle.getString("newText");
                                               if (newText != null)
                                               {
                                                   LongTweetDialog.this.shrinkedMsg.setText(newText);
                                               }

                                               LongTweetDialog.this.isShrinking = false;
                                           }
                                       };

    //----------------------------------------------------------
    /**
     * Constructor
     */
    //----------------------------------------------------------
    public LongTweetDialog(Context context, EditText target,
            AccountInfo currentLoginAccountInfo)
    {
        super(context);
        super.setContentView(R.layout.dialog_long_tweet);

        this.currentLoginAccountInfo = currentLoginAccountInfo;
        this.target = target;

        //------------------
        // Init View
        //------------------
        setTitle(R.string.dialog_long_tweet_twitlonger);

        //Original Message
        this.originalMsg = (EditText) findViewById(R.id.originalmsg);
        this.originalMsg.setText(target.getText().toString());
        this.originalMsg.setEnabled(false);

        //Twit Longer Button
        this.btnTwitLonger = (Button) findViewById(R.id.twitlonger);
        this.btnTwitLonger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (!LongTweetDialog.this.isShrinking)
                {
                    shrinkMessage();
                }
            }
        });

        //Shorten Message
        this.shrinkedMsg = (EditText) findViewById(R.id.respondmsg);
        this.shrinkedMsg.setEnabled(false);

        //OK
        this.btnOk = (Button) findViewById(R.id.twitlongerok);
        this.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String message = LongTweetDialog.this.shrinkedMsg.getText().toString();
                if (message == null || message.length() == 0)
                {
                    return;
                }
                replaceText();
                dismiss();
            }
        });

        //Cancel
        this.btnCancel = (Button) findViewById(R.id.twitlongercancel);
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    //----------------------------------------------------------
    /**
     * Shrink a long message and set to 
     */
    //----------------------------------------------------------
    private void shrinkMessage()
    {
        this.isShrinking = true;

        // Prepare Progress
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();

        Thread th = new Thread(new Runnable() {
            public void run()
            {

                //Get New Message
                LongTweetHandler handler = new LongTweetHandler();
                String newMessage = handler.getShirinkedMessage(
                        LongTweetDialog.this.currentLoginAccountInfo.getName(),
                        LongTweetDialog.this.originalMsg.getText().toString());

                //Set Result
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("newText", newMessage);
                msg.setData(bundle);

                LongTweetDialog.this.myHandler.sendMessage(msg);

            }
        });
        th.start();
    }

    //----------------------------------------------------------
    /**
     * Replace target Text
     */
    //----------------------------------------------------------
    private void replaceText()
    {
        String message = this.shrinkedMsg.getText().toString();
        this.target.setText(message);
    }

}
