package jp.gr.kmtn.stdroid.dialog;

import java.io.IOException;
import java.util.ArrayList;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.TimeLineActivity;
import jp.gr.kmtn.stdroid.info.BasicInfo;
import jp.gr.kmtn.stdroid.info.SearchInfo;
import jp.gr.kmtn.stdroid.info.TimeLineInfo;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.ImageBuilder;
import jp.gr.kmtn.stdroid.util.MyClickableSpan;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;
import jp.gr.kmtn.stdroid.util.TagAnalysis;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class MultiSelectDialog extends Dialog
{

    // Views
    private ImageView                    userImageView;

    private TextView                     infoText1;

    private TextView                     infoText2;

    private ImageButton                  retweetButton;

    private ImageButton                  replyButton;

    private ImageButton                  deleteButton;

    private ImageButton                  profileButton;

    private ImageButton                  translateButton;

    private ImageButton                  emailButton;

    private ImageButton                  closeButton;

    private ImageView                    favoriteStar;

    private ImageButton                  reply;

    private ImageButton                  closeChildrenButton;

    CommunicationHandlerResult           replyStatusResult;

    TimeLineInfo                         resultInfo;

    // private ImageButton userStatusButton;

    private static final String          ON         = "on";

    private static final String          OFF        = "off";

    private static final String          TRUE       = "true";

    private static final String          FALSE      = "false";

    boolean                              isFavorite = true;

    String                               setFvorite = ON;

    String                               text;

    String                               mCurrentService;

    // Data
    private BasicInfo                    info;

    // Current Tab No in TimeLine Activity
    private int                          tabNo      = 0;

    // Progress Dialog
    private ProgressDialog               progress;

    //Db Adapter
    private MyDbAdapter                  db;

    //Parent List
    private ArrayList<MultiSelectDialog> childList;

    // Handled Called by child thead
    Handler                              mHandler   = new Handler() {
                                                        @Override
                                                        public void handleMessage(
                                                                Message msg)
                                                        {

                                                            // Close Progress Dialog
                                                            MultiSelectDialog.this.progress.dismiss();

                                                            // detail dialog
                                                            int result = msg.getData().getInt(
                                                                    "result");
                                                            if (result == 11
                                                                    || result == -1)
                                                            {
                                                                detailDialog(result);
                                                            }

                                                            int retweetResult = msg.getData().getInt(
                                                                    "retweetResult");
                                                            if (retweetResult == 11
                                                                    || retweetResult == -1)
                                                            {
                                                                detailDialog(retweetResult);
                                                            }
                                                            String starFavorite = msg.getData().getString(
                                                                    "favorite");
                                                            if (starFavorite != null
                                                                    && starFavorite.length() > 0)
                                                            {
                                                                if (starFavorite.equals(ON))
                                                                {
                                                                    MultiSelectDialog.this.favoriteStar.setImageResource(R.drawable.multiselectdialog_starton);
                                                                    MultiSelectDialog.this.info.setFavorite("true");
                                                                }
                                                                else if (starFavorite.equals(OFF))
                                                                {
                                                                    MultiSelectDialog.this.favoriteStar.setImageResource(R.drawable.multiselectdialog_staroff);
                                                                    MultiSelectDialog.this.info.setFavorite("false");

                                                                }
                                                            }

                                                            String reply = msg.getData().getString(
                                                                    "reply");
                                                            if (reply != null)
                                                            {
                                                                //bundle.putString("reply", "ok");
                                                                if (reply.equals("ok"))
                                                                {
                                                                    MultiSelectDialog mDialog = new MultiSelectDialog(
                                                                            getContext(),
                                                                            MultiSelectDialog.this.db);
                                                                    mDialog.setInfo(
                                                                            MultiSelectDialog.this.resultInfo,
                                                                            MultiSelectDialog.this.tabNo);
                                                                    mDialog.showAsChild(MultiSelectDialog.this.childList);
                                                                }
                                                            }
                                                        }

                                                    };

    // -----------------------------------------------------------------------------
    /**
     * Constructor
     */
    // -----------------------------------------------------------------------------
    public MultiSelectDialog(Context context, MyDbAdapter dbAdapter)
    {
        super(context);
        super.setContentView(R.layout.dialog_multi_select);

        this.db = dbAdapter;

        this.mCurrentService = this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);

        // Init Views
        this.userImageView = (ImageView) findViewById(R.id.user_image);
        this.infoText1 = (TextView) findViewById(R.id.info1);
        this.infoText2 = (TextView) findViewById(R.id.info2);

        this.retweetButton = (ImageButton) findViewById(R.id.retweetButton);
        this.retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                final String[] retweetlist = new String[]
                { getContext().getString(R.string.qt),
                        getContext().getString(R.string.inline_tweet) };

                Builder ad = new AlertDialog.Builder(getContext()).setItems(
                        retweetlist, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                switch (which)
                                {
                                case 0:
                                    retweet();
                                    break;
                                case 1:
                                    RTreply(MultiSelectDialog.this.info);
                                    break;
                                default:
                                    break;
                                }
                            }
                        });
                ad.show();
            }
        });

        this.replyButton = (ImageButton) findViewById(R.id.replyButton);
        this.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                reply();
            }
        });

        this.profileButton = (ImageButton) findViewById(R.id.profileButton);
        this.profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showProfile();
            }
        });

        this.deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        this.deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        this.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                delete();

            }
        });

        this.translateButton = (ImageButton) findViewById(R.id.translateButton);
        this.translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                translate();
            }
        });

        this.emailButton = (ImageButton) findViewById(R.id.emailButton);
        this.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                email();
            }
        });

        this.closeButton = (ImageButton) findViewById(R.id.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        this.favoriteStar = (ImageView) findViewById(R.id.favorite);
        this.favoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MultiSelectDialog.this.progress = new ProgressDialog(
                        getContext());
                MultiSelectDialog.this.progress.setIndeterminate(false);
                MultiSelectDialog.this.progress.show();
                // TODO Auto-generated method stub
                if (MultiSelectDialog.this.info.getFavorite().equals(TRUE))
                {
                    MultiSelectDialog.this.isFavorite = false;
                    MultiSelectDialog.this.setFvorite = OFF;
                }
                if (MultiSelectDialog.this.info.getFavorite().equals(FALSE))
                {
                    MultiSelectDialog.this.isFavorite = true;
                    MultiSelectDialog.this.setFvorite = ON;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        TwitterHandler.setFavorite(
                                MultiSelectDialog.this.info.getMessageId(),
                                MultiSelectDialog.this.isFavorite);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("favorite",
                                MultiSelectDialog.this.setFvorite);
                        msg.setData(bundle);
                        MultiSelectDialog.this.mHandler.sendMessage(msg);
                    }
                }).start();
            }
        });

        this.reply = (ImageButton) findViewById(R.id.dialog_multiselect_get_reply);

        this.reply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (MultiSelectDialog.this.info.getinReplyToStatusId() != null)
                {
                    MultiSelectDialog.this.progress = new ProgressDialog(
                            getContext());
                    MultiSelectDialog.this.progress.setIndeterminate(false);
                    MultiSelectDialog.this.progress.show();
                    new Thread(new Runnable() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void run()
                        {

                            // TODO Auto-generated method stub
                            MultiSelectDialog.this.replyStatusResult = TwitterHandler.getMessageById(MultiSelectDialog.this.info.getinReplyToStatusId());
                            if (MultiSelectDialog.this.replyStatusResult.getResultCode() == 200)
                            {
                                ArrayList<TimeLineInfo> resultList = (ArrayList<TimeLineInfo>) MultiSelectDialog.this.replyStatusResult.getData();
                                MultiSelectDialog.this.resultInfo = resultList.get(0);
                                try
                                {
                                    Bitmap bm = ImageBuilder.returnBitMap(MultiSelectDialog.this.resultInfo.getUserInfo().getUserImageURL());
                                    MultiSelectDialog.this.resultInfo.getUserInfo().setUserImage(
                                            bm);
                                }
                                catch (IOException e)
                                {
                                    Log.w("StatusDroid", "Error Occured", e);
                                }

                            }
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("reply", "ok");
                            msg.setData(bundle);
                            MultiSelectDialog.this.mHandler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        //---------------------------
        // Close Children Button
        //---------------------------
        this.closeChildrenButton = (ImageButton) findViewById(R.id.dialog_multiselect_close_children);
        this.closeChildrenButton.setVisibility(View.GONE);
        this.closeChildrenButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                closeChildren();

            }
        });

    }

    // -----------------------------------------------------------------------------
    /**
     * Set Info.
     */
    // -----------------------------------------------------------------------------
    public void setInfo(BasicInfo info, int tabNo)
    {

        this.info = info;
        this.tabNo = tabNo;

        if (tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE
                && ((TimeLineInfo) info).getRetweetUserInfo() != null)
        {
            setTitle(((TimeLineInfo) info).getRetweetUserInfo().getScreenName());
            this.userImageView.setImageBitmap(((TimeLineInfo) info).getRetweetUserInfo().getUserImage());
        }
        else
        {
            setTitle(info.getUserInfo().getScreenName());
            this.userImageView.setImageBitmap(info.getUserInfo().getUserImage());
        }
        if (info.getStatus().replaceAll("\r", "").contains("##")
                || info.getStatus().replaceAll("\r", "").equals("#"))
        {
            this.infoText2.setText(info.getStatus().replaceAll("\r", ""));
        }
        else
        {
            showMessage();
        }

        // Disable Menu
        // translateButton.setVisibility(View.INVISIBLE);

        final String currentService = this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_CURRENT_SERVICE);

        this.infoText1.setText(info.getFormatTime(currentService));

        if (tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE)
        {
            this.deleteButton.setVisibility(View.GONE);
        }
        else if (tabNo == TimeLineActivity.TAB_AT_MESSAGE)
        {
            this.deleteButton.setVisibility(View.GONE);

        }
        else if (tabNo == TimeLineActivity.TAB_DIRECT_MESSAGE)
        {
            this.retweetButton.setVisibility(View.GONE);
            this.deleteButton.setVisibility(View.GONE);
        }
        else if (tabNo == TimeLineActivity.TAB_MY_TIMELINE)
        {
            this.retweetButton.setVisibility(View.GONE);
            this.replyButton.setVisibility(View.GONE);
        }

        if (tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE)
        {
            if (info.getFavorite().equals(TRUE))
            {
                this.favoriteStar.setImageResource(R.drawable.multiselectdialog_starton);
            }
            else
            {
                this.favoriteStar.setImageResource(R.drawable.multiselectdialog_staroff);
            }
        }
        else
        {
            this.favoriteStar.setVisibility(View.GONE);
        }

        if (info.getinReplyToStatusId() == null)
        {
            this.reply.setVisibility(View.GONE);
        }
    }

    // -----------------------------------------------------------------------------
    /**
     * Set Info.
     */
    // -----------------------------------------------------------------------------
    public void setInfo(BasicInfo searchInfo)
    {

        // Create a Basic Info Object form searchInfo
        this.info = searchInfo;
        UserInfo userinfo = ((SearchInfo) this.info).getUserInfo();

        setTitle(userinfo.getScreenName());
        this.userImageView.setImageBitmap(userinfo.getUserImage());
        // Set Visibilty of Button
        showMessage_twitter();

        this.deleteButton.setVisibility(View.GONE);
        this.favoriteStar.setVisibility(View.GONE);

        if (searchInfo.getinReplyToStatusId() == null)
        {
            this.reply.setVisibility(View.GONE);
        }
    }

    private void retweet()
    {

        this.db.getCurrentLoginAccountInfo();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try
        {
            String comfirmMessage = getContext().getString(
                    R.string.retweet_comfirm_message);
            builder.setMessage(String.format(comfirmMessage,
                    this.info.getUserInfo().getScreenName()));
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MultiSelectDialog.this.progress = new ProgressDialog(
                                getContext());
                        MultiSelectDialog.this.progress.setIndeterminate(false);
                        MultiSelectDialog.this.progress.show();
                        new Thread(new Runnable() {

                            CommunicationHandlerResult result = new CommunicationHandlerResult();

                            public void run()
                            {
                                this.result = TwitterHandler.retweet(MultiSelectDialog.this.info.getMessageId());

                                MultiSelectDialog.this.mHandler.sendEmptyMessage(0);
                                // Show Toast if Error
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                if (this.result.getResultCode() == 200)
                                {
                                    this.result.setResultCode(11);
                                }
                                bundle.putInt("retweetResult",
                                        this.result.getResultCode());
                                msg.setData(bundle);
                                MultiSelectDialog.this.mHandler.sendMessage(msg);
                            }
                        }).start();

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    // -----------------------------------------------------------------------------
    /**
     * Send @Message or DirectMessage according to current Tab.
     */
    // -----------------------------------------------------------------------------
    private void reply()
    {

        if (this.tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE
                || this.tabNo == TimeLineActivity.TAB_AT_MESSAGE)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_AT_MESSAGE, this.db);
            dialog.setTarget(this.info.getUserInfo().getScreenName(), null);
            dialog.show();
        }
        else if (this.tabNo == TimeLineActivity.TAB_DIRECT_MESSAGE)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_DIRECT_MESSAGE, this.db);
            dialog.setTarget(this.info.getUserInfo().getScreenName(), null);
            dialog.show();
        }
        else if (this.info.getUserInfo() != null)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_AT_MESSAGE, this.db);
            dialog.setTarget(this.info.getUserInfo().getScreenName(), null);
            dialog.show();
        }

    };

    // -----------------------------------------------------------------------------
    /**
     * Send @Message or DirectMessage according to current Tab.
     */
    // -----------------------------------------------------------------------------
    private void RTreply(BasicInfo info)
    {

        if (this.tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE
                || this.tabNo == TimeLineActivity.TAB_AT_MESSAGE)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_AT_MESSAGE, this.db);
            dialog.setTarget(info);
            dialog.show();
        }
        else if (this.tabNo == TimeLineActivity.TAB_DIRECT_MESSAGE)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_DIRECT_MESSAGE, this.db);
            dialog.setTarget(info);
            dialog.show();
        }
        else if (info.getUserInfo() != null)
        {
            UpdateDialog dialog = new UpdateDialog(getContext(),
                    UpdateDialog.MODE_AT_MESSAGE, this.db);
            dialog.setTarget(info);
            dialog.show();
        }

    };

    // -----------------------------------------------------------------------------
    /**
     * Display User Profile.
     */
    // -----------------------------------------------------------------------------
    private void showProfile()
    {

        ProfileDialog dialog = new ProfileDialog(getContext(), this.db);

        if (this.tabNo == TimeLineActivity.TAB_FRIEND_TIMELINE
                && ((TimeLineInfo) this.info).getRetweetUserInfo() != null)
        {
            dialog.setInfo(((TimeLineInfo) this.info).getRetweetUserInfo());
        }
        else
        {
            dialog.setInfo(this.info.getUserInfo());
        }

        dialog.show();

    };

    // -----------------------------------------------------------------------------
    /**
     * Display Message.
     */
    // -----------------------------------------------------------------------------
    private void showMessage()
    {

        // Extract Hash
        this.text = this.info.getStatus().replaceAll("\r", "");
        if (this.text == null)
        {
            this.text = "";
        }

        final ArrayList<String> indexHashFlag = TagAnalysis.getIndex(this.text,
                "#");
        int number = indexHashFlag.size();

        SpannableString ss = new SpannableString(this.text);

        for (int i = 0; i < number / 2; i++)
        {

            final int start = Integer.valueOf(indexHashFlag.get(i * 2));
            final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

            // Prepare Clickable Span
            MyClickableSpan myClickableSpan = new MyClickableSpan(
                    new android.view.View.OnClickListener() {

                        @Override
                        public void onClick(View widget)
                        {
                            String tag = MultiSelectDialog.this.text.substring(
                                    start, end);

                            // Open search information dialog
                            SearchInfoDialog searchInfoDialog = new SearchInfoDialog(
                                    getContext(), tag,
                                    MultiSelectDialog.this.db);
                            searchInfoDialog.setTitle(R.string.dialog_searchinfo_searchsometing);
                            searchInfoDialog.show();
                        }

                    });

            ss.setSpan(myClickableSpan, start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final ArrayList<String> indexAtFlag = TagAnalysis.getIndex(this.text,
                "@");
        int numverAtFlag = indexAtFlag.size();

        for (int i = 0; i < numverAtFlag / 2; i++)
        {

            final int start = Integer.valueOf(indexAtFlag.get(i * 2));
            final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

            // Prepare Clickable Span
            MyClickableSpan myClickableSpan = new MyClickableSpan(
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View widget)
                        {
                            String tag = MultiSelectDialog.this.text.substring(
                                    start, end);
                            // Open profile dialog
                            ProfileDialog dialog = new ProfileDialog(
                                    getContext(), MultiSelectDialog.this.db);
                            dialog.setInfo(tag.substring(1, tag.length()));
                            dialog.show();
                        }

                    });

            ss.setSpan(myClickableSpan, start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        this.infoText2.setText(ss);
        this.infoText2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showMessage_twitter()
    {
        String time = ((SearchInfo) this.info).getTime_search();
        this.infoText1.setText(SearchInfo.getFormatTimeCrowdroid(time));

        // infoText2.setText(info.getStatus());

        // Extract Hash
        final String text = this.info.getStatus();
        final ArrayList<String> indexHashFlag = TagAnalysis.getIndex(text, "#");
        int number = indexHashFlag.size();

        SpannableString ss = new SpannableString(text);

        for (int i = 0; i < number / 2; i++)
        {

            final int start = Integer.valueOf(indexHashFlag.get(i * 2));
            final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

            // Prepare Clickable Span
            MyClickableSpan myClickableSpan = new MyClickableSpan(
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View widget)
                        {
                            String tag = text.substring(start, end);
                            // Open search information dialog
                            SearchInfoDialog searchInfoDialog = new SearchInfoDialog(
                                    getContext(), tag,
                                    MultiSelectDialog.this.db);
                            searchInfoDialog.show();
                        }

                    });

            ss.setSpan(myClickableSpan, start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final ArrayList<String> indexAtFlag = TagAnalysis.getIndex(text, "@");
        int numverAtFlag = indexAtFlag.size();

        for (int i = 0; i < numverAtFlag / 2; i++)
        {

            final int start = Integer.valueOf(indexAtFlag.get(i * 2));
            final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

            // Prepare Clickable Span
            MyClickableSpan myClickableSpan = new MyClickableSpan(
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View widget)
                        {
                            String tag = text.substring(start, end);
                            // Open profile dialog
                            ProfileDialog dialog = new ProfileDialog(
                                    getContext(), MultiSelectDialog.this.db);
                            dialog.setInfo(tag.substring(1, tag.length()));
                            dialog.show();
                        }

                    });

            ss.setSpan(myClickableSpan, start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // ss.setSpan(myClickableSpan, 12, 17,
        // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.infoText2.setText(ss);
        this.infoText2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // -----------------------------------------------------------------------------
    /**
     * Translate.
     */
    // -----------------------------------------------------------------------------
    private void translate()
    {
        TranslateDialog t = new TranslateDialog(getContext(), this.infoText2,
                this.db);
        t.show();
    };

    // -----------------------------------------------------------------------------
    /**
     * Send E-mail.
     */
    // -----------------------------------------------------------------------------
    private void email()
    {

        Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);
        /* set email's format plain/text */
        mEmailIntent.setType("text/plain");
        String s = this.info.getStatus();
        mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
        mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        mEmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
        getContext().startActivity(
                Intent.createChooser(mEmailIntent, getContext().getString(
                        R.string.dialog_multiselect_sending)));
        dismiss();
    }

    // -----------------------------------------------------------------------------
    /**
     * Delete.
     */
    // -----------------------------------------------------------------------------
    private void delete()
    {

        this.db.getCurrentLoginAccountInfo();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(R.string.dialog_multiselect_deletethemessage);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MultiSelectDialog.this.progress = new ProgressDialog(
                                getContext());
                        MultiSelectDialog.this.progress.setIndeterminate(false);
                        MultiSelectDialog.this.progress.show();
                        new Thread(new Runnable() {
                            public void run()
                            {

                                // int result = 11;

                                CommunicationHandlerResult result = TwitterHandler.destroy(MultiSelectDialog.this.info.getMessageId());

                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                if (result.getResultCode() == 200)
                                {
                                    result.setResultCode(11);
                                }
                                bundle.putInt("result", result.getResultCode());
                                message.setData(bundle);
                                MultiSelectDialog.this.mHandler.sendMessage(message);

                            }
                        }).start();

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();

    }

    public static void successfulDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setTitle("sd")
        builder.setMessage(R.string.dialog_multiselect_succeed);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void failedDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_multiselect_faidled);
        builder.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void detailDialog(int result)
    {

        if (result == 11)
        {
            successfulDialog(getContext());
        }
        else
        {
            failedDialog(getContext());
        }

    }

    //---------------------------------------------------------------
    /**
     * Open Dialog As Child
     */
    //---------------------------------------------------------------
    private void showAsChild(ArrayList<MultiSelectDialog> cList)
    {

        //Set ChildList
        this.childList = cList;
        if (this.childList == null)
        {
            this.childList = new ArrayList<MultiSelectDialog>();
        }

        //Add MySelf
        this.childList.add(this);

        //Activate Button
        this.closeChildrenButton.setVisibility(View.VISIBLE);

        //Open
        show();

    }

    //---------------------------------------------------------------
    /**
     * Close All Children
     */
    //---------------------------------------------------------------
    private void closeChildren()
    {

        if (this.childList != null)
        {

            //Close From Background
            for (int i = 0; i < this.childList.size(); i++)
            {
                this.childList.get(i).dismiss();
            }

        }

    }

}
