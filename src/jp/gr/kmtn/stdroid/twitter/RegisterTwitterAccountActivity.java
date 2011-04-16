package jp.gr.kmtn.stdroid.twitter;

import java.net.HttpURLConnection;

import jp.gr.kmtn.stdroid.IGeneral;
import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.IErrorMessage;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class RegisterTwitterAccountActivity extends Activity
{

    /** Server Address */
    private String               apiServer      = "http://twitter.com";

    /** Key (API Server) */
    public static final String   KEY_API_SERVER = "api_server";

    /** Web View */
    private WebView              webView;

    /** Allow Check Box */
    private CheckBox             allowBox;

    /** PIN Input */
    private EditText             pinText;

    /** Back Button */
    private Button               backButton;

    /** Finish Button */
    private Button               finishButton;

    /** Consumer (OAuth) */
    private DefaultOAuthConsumer consumer;

    /** Provider (OAuth) */
    private OAuthProvider        provider;

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Created.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_twitter_account);

        //-----------------------
        //Set Server
        //-----------------------
        this.apiServer = getIntent().getExtras().getString(KEY_API_SERVER);

        //-----------------------
        //Init Views
        //-----------------------
        this.webView = (WebView) findViewById(R.id.web_view);
        this.allowBox = (CheckBox) findViewById(R.id.allow_box);
        this.backButton = (Button) findViewById(R.id.back_button);
        this.finishButton = (Button) findViewById(R.id.finish_button);
        this.pinText = (EditText) findViewById(R.id.pin_text);

        //-----------------------
        //Web View
        //-----------------------
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                RegisterTwitterAccountActivity.this.webView.loadUrl(url);
                return true;
            }
        });
        this.webView.clearCache(true);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.clearHistory();
        this.webView.clearFormData();

        //-----------------------------
        // Allow Check Box
        //-----------------------------
        this.allowBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked)
            {
                if (isChecked)
                {
                    RegisterTwitterAccountActivity.this.finishButton.setEnabled(true);
                }
                else
                {
                    RegisterTwitterAccountActivity.this.finishButton.setEnabled(false);
                }

            }
        });

        //-----------------------------
        // Back Button
        //-----------------------------
        this.backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        //-----------------------------
        // Finish Button
        //-----------------------------
        this.finishButton.setEnabled(false);
        this.finishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //Get New Token
                String pin = RegisterTwitterAccountActivity.this.pinText.getText().toString();
                if (pin != null && pin.length() > 0)
                {
                    getNewToken(pin);
                }
            }
        });

    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Started.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onStart()
    {
        super.onStart();

        String requestUrl = getRequestURL();
        if (requestUrl != null)
        {
            this.webView.loadUrl(requestUrl);
        }
    }

    //--------------------------------------------------------------
    /**
     *Get the Request URL 
     */
    //--------------------------------------------------------------
    private String getRequestURL()
    {

        CommunicationHandlerResult chResult = TwitterHandler.getRegistrationElement(this.apiServer);
        if (chResult.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            Object[] data = (Object[]) chResult.getData();
            this.consumer = (DefaultOAuthConsumer) data[0];
            this.provider = (DefaultOAuthProvider) data[1];
            String requestUrl = (String) data[2];
            return requestUrl;
        }
        else
        {
            Toast.makeText(this, getString(chResult.getResultMessage()),
                    Toast.LENGTH_LONG).show();
            finish();
            return null;
        }
    }

    //--------------------------------------------------------------
    /**
     * Get New Token 
     */
    //--------------------------------------------------------------
    private void getNewToken(String pin)
    {

        CommunicationHandlerResult chResult = TwitterHandler.getNewToken(
                this.consumer, this.provider, pin);
        if (chResult.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            String[] data = (String[]) chResult.getData();
            String accessToken = data[0];
            String tokenSecret = data[1];
            registerToken(accessToken, tokenSecret);
        }
        else
        {
            Toast.makeText(this, getString(chResult.getResultMessage()),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //--------------------------------------------------------------
    /**
     * Register Tocken Info to DB 
     */
    //--------------------------------------------------------------
    private void registerToken(String accessToken, String tokenSecret)
    {

        CommunicationHandlerResult chResult = TwitterHandler.verifyUser(
                accessToken, tokenSecret, TwitterHandler.AUTH_TYPE_OAUTH,
                TwitterHandler.TWITTER_ORIGINAL_API_SERVER);

        if (chResult.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            UserInfo userInfo = (UserInfo) chResult.getData();
            String uid = userInfo.getUid();
            String screenName = userInfo.getScreenName();
            MyDbAdapter myDbAdapter = new MyDbAdapter(this);
            myDbAdapter.open();
            if (!myDbAdapter.updateAccount1(uid, IGeneral.SERVICE_NAME_TWITTER,
                    screenName, screenName, null, accessToken, tokenSecret))
            {
                myDbAdapter.insertAccount(uid, IGeneral.SERVICE_NAME_TWITTER,
                        screenName, screenName, null, accessToken, tokenSecret);

                //OK
                Toast.makeText(
                        RegisterTwitterAccountActivity.this,
                        getString(IErrorMessage.MESSAGE_REGISTRATION_TWITTER_SUCCEEDED),
                        Toast.LENGTH_SHORT).show();
            }
            myDbAdapter.close();
        }
        else
        {
            Toast.makeText(RegisterTwitterAccountActivity.this,
                    getString(chResult.getResultMessage()), Toast.LENGTH_SHORT).show();
        }

        finish();

    }

}
