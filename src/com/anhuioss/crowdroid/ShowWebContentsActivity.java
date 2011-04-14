package com.anhuioss.crowdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class ShowWebContentsActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String locale = getResources().getConfiguration().locale.getDisplayName();
        // Get Intent Data
        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString(IGeneral.TYPE);

        // Prepare WebView
        WebView webView;
        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(webView);

        if (type.equals(IGeneral.TYPE_HELP))
        {
            setTitle("User Guide");
            if (locale.equals("Chinese (China)") || locale.equals("中文 (中国)")
                    || locale.equals("中文 (臺灣)"))
            {
                webView.loadUrl("http://www.anhuioss.com/cn/crowdroid/manual.html");
            }
            else if (locale.equals("Japanese (Japan)"))
            {
                webView.loadUrl("http://nyango.com/crowdroid/jp/manual.html");
            }
            else
            {
                webView.loadUrl("http://nyango.com/crowdroid/en/manual.html");
            }
        }
        else if (type.equals(IGeneral.TYPE_LICENSE))
        {
            setTitle("License");
            if (locale.equals("Chinese (China)") || locale.equals("中文 (中国)")
                    || locale.equals("中文 (臺灣)"))
            {

                webView.loadUrl("file:///android_asset/license_cn.html");
            }
            else if (locale.equals("Japanese (Japan)"))
            {
                webView.loadUrl("file:///android_asset/license_jp.html");
            }
            else
            {
                webView.loadUrl("file:///android_asset/license.html");
            }
        }

    }
}
