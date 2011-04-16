package jp.gr.kmtn.stdroid.settings;

import jp.gr.kmtn.stdroid.R;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;


public class KeywordTabActivity extends TabActivity
{
    TabHost host = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String addUser = getString(R.string.activity_setting_keywordtab_adduser);
        String addKeyword = getString(R.string.activity_setting_keywordtab_addkeyword);
        this.host = getTabHost();
        this.host.addTab(this.host.newTabSpec("add_user").setIndicator(
                addUser,
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_menu_myplaces))).setContent(
                new Intent(this, UserKeySettingActivity.class)));
        this.host.addTab(this.host.newTabSpec("add_keyword").setIndicator(
                addKeyword,
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_menu_sort_alphabetically))).setContent(
                new Intent(this, KeywordSettingActivity.class)));

        this.host.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId)
            {
                prepareTab(tabId);
            }
        });
    }

    protected void prepareTab(String tabId)
    {
        if (tabId.equals("add_user"))
        {
            ImageView iv = (ImageView) this.host.getTabWidget().getChildAt(0).findViewById(
                    android.R.id.icon);
            iv.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_myplaces));
            iv = (ImageView) this.host.getTabWidget().getChildAt(1).findViewById(
                    android.R.id.icon);
            iv.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_sort_alphabetically));
        }
        else if (tabId.equals("add_keyword"))
        {
            ImageView iv = (ImageView) this.host.getTabWidget().getChildAt(0).findViewById(
                    android.R.id.icon);
            iv.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_myplaces));
            iv = (ImageView) this.host.getTabWidget().getChildAt(1).findViewById(
                    android.R.id.icon);
            iv.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_sort_alphabetically));
        }
    }

}
