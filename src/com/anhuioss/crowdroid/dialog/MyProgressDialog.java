package com.anhuioss.crowdroid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.anhuioss.crowdroid.R;

public class MyProgressDialog extends AlertDialog.Builder
{

    public MyProgressDialog(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = LayoutInflater.from(context);
        final View textEntryView = inflater.inflate(R.layout.dialog_progress,
                null);
        setView(textEntryView);
    }

    @Override
    public AlertDialog show()
    {
        return super.show();
    }
}
