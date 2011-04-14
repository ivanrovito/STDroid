package com.anhuioss.crowdroid.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.settings.TwitterSettingActivity;

public class SearchServiceDialog extends Dialog
{
    ListView                    listView;

    private static final String SETTING_ADD_TWITTER = "  Twitter";

    private String[]            data                =
                                                    { SETTING_ADD_TWITTER };

    public SearchServiceDialog(final Context context)
    {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_search_service, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(textEntryView);
        this.listView = new ListView(context);
        this.listView.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, this.data));
        setContentView(this.listView);
        this.listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                // TODO Auto-generated method stub
                switch (position)
                {
                case 0:
                    Intent intent1 = new Intent(getContext(),
                            TwitterSettingActivity.class);
                    getContext().startActivity(intent1);
                    dismiss();
                    break;
                default:
                    break;
                }
            }
        });

    }

}
