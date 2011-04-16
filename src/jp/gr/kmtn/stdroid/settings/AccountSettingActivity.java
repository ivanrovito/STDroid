package jp.gr.kmtn.stdroid.settings;

import java.util.ArrayList;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.twitter.RegisterTwitterAccountActivity;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class AccountSettingActivity extends ListActivity
{

    private Cursor      myCursor;

    private MyDbAdapter myDb;

    private int         DELETE         = 1;

    private long        selectid;

    private Button      button_add;

    private Bundle      extras;

    private String      service;

    private Spinner     spinner;

    private String[]    spinner_sevice =
                                       { "twitter", "follow5",
            "crowdroid_business"      };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_account);

        this.button_add = (Button) findViewById(R.id.follow5_add);

        this.spinner = (Spinner) findViewById(R.id.Spinner_account);

        this.extras = getIntent().getExtras();//接收数据

        this.service = this.extras.getString("FLAGE");

        this.myDb = new MyDbAdapter(this);

        this.myDb.open();

        ArrayList<String> account_list = new ArrayList<String>();
        for (int i = 0; i < this.spinner_sevice.length; i++)
        {
            account_list.add(this.spinner_sevice[i]);
        }

        ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, account_list);

        account_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinner.setAdapter(account_adapter);

        this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {

                AccountSettingActivity.this.service = AccountSettingActivity.this.spinner_sevice[position];

                AccountSettingActivity.this.myCursor = AccountSettingActivity.this.myDb.getAccountCursor(
                        AccountSettingActivity.this.service, null);

                startManagingCursor(AccountSettingActivity.this.myCursor);

                String[] from = new String[]
                { MyDbAdapter.FIELD_ACCOUNT_NAME,
                        MyDbAdapter.FIELD_ACCOUNT_SERVICE };

                int[] to = new int[]
                { R.id.username, R.id.service };

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        AccountSettingActivity.this,

                        R.layout.list_item_oauth,
                        AccountSettingActivity.this.myCursor, from, to);

                setListAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });

        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                AccountSettingActivity.this.selectid = id;
                //setTitle("你删除了："+id+"记录");
                showDialog(AccountSettingActivity.this.DELETE);
                return true;
            }
        });
        this.button_add.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                startActivity(new Intent(AccountSettingActivity.this,
                        RegisterTwitterAccountActivity.class));
            }
        });
    }

    //-------------------------------------------------------------------
    /**
     * Called when Activity was Stopped
     */
    //-------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        this.myDb.close();
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case 1:
            return showDelete(this);
        case 2:
            return showAdd(this);
        }
        return super.onCreateDialog(id);
    }

    private Dialog showAdd(Context context)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.activity_login_addaccount);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_add_follow5, null);
        builder.setView(textEntryView);

        builder.setPositiveButton(R.string.actvity_twittersetting_add,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        AccountSettingActivity.this.myCursor.requery();
                        Intent data = new Intent();
                        data.putExtra("1", "1");
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });

        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }

    private Dialog showDelete(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.activity_ccountsetting_deleteaccount);
        builder.setPositiveButton("Cofirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        AccountSettingActivity.this.myDb.deleteAccount(AccountSettingActivity.this.selectid);
                        AccountSettingActivity.this.myCursor.requery();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        this.myCursor.requery();
        return builder.create();
    }

}
