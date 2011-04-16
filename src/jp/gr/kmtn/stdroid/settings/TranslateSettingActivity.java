package jp.gr.kmtn.stdroid.settings;

import java.util.ArrayList;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.api.translate.Language;

public class TranslateSettingActivity extends Activity
{
    private MyDbAdapter myDb;

    private final int   ADD            = 1;

    private final int   DELETE         = 2;

    private Cursor      myCursor;

    private Button      add;

    private EditText    userId;

    private Spinner     translateFrom;

    private Spinner     translateTo;

    private CheckBox    autoTranlate;

    private RadioButton google;

    private RadioButton bing;

    private String      _id            = null;

    private String      fromLanguage   = null;

    private String      toLanguage     = null;

    private String      ARABIC;

    private String      CHINISE_SIMPLE;

    private String      CHINESE_TRADITIONAL;

    private String      DUTC;

    private String      ENGLISH;

    private String      FRENCH;

    private String      GERMAN;

    private String      GREEK;

    private String      ITALIAN;

    private String      JAPANISE;

    private String      KOREA;

    private String      PORTUGESE;

    private String      RUSSIAN;

    private String      SPANISH;

    /** Language List */
    private String[]    languageValues =
                                       { Language.ARABIC,
            Language.CHINESE_SIMPLIFIED, Language.CHINESE_TRADITIONAL,
            Language.DUTCH, Language.ENGLISH, Language.FRENCH, Language.GERMAN,
            Language.GREEK, Language.ITALIAN, Language.JAPANESE,
            Language.KOREAN, Language.PORTUGESE, Language.RUSSIAN,
            Language.SPANISH          };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_translate);

        this.add = (Button) findViewById(R.id.add);

        this.autoTranlate = (CheckBox) findViewById(R.id.CheckBox01_auto_translate);

        this.google = (RadioButton) findViewById(R.id.google);

        this.bing = (RadioButton) findViewById(R.id.bing);

        this.myDb = new MyDbAdapter(this);

        this.myDb.open();

        this.myCursor = this.myDb.getTranslationCursor();
        // used to check autoTranlate
        String flag = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_AUTO_TRANSLATION);

        if (flag.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            this.autoTranlate.setChecked(true);
            this.autoTranlate.setText(R.string.activity_translatesetting_enabletranslate);
        }
        else
        {
            this.autoTranlate.setChecked(false);
            this.autoTranlate.setText(R.string.activity_translatesetting_disabletranslate);

        }

        startManagingCursor(this.myCursor);

        String[] from = new String[]
        { MyDbAdapter.FIELD_TRANSLATION_UID,
                MyDbAdapter.FIELD_TRANSLATION_FROM,
                MyDbAdapter.FIELD_TRANSLATION_TO };

        int[] to = new int[]
        { R.id.tranlation_id, R.id.tranlation_from, R.id.tranlation_to };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_item_language, this.myCursor, from, to);

        ListView lv = (ListView) findViewById(R.id.list_translation);

        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                TranslateSettingActivity.this._id = String.valueOf(id);
                showDialog(TranslateSettingActivity.this.DELETE);
                // TODO Auto-generated method stub
                return false;
            }
        });

        this.add.setOnClickListener(new OnClickListener() {

            public void onClick(View v)
            {
                showDialog(TranslateSettingActivity.this.ADD);
            }
        });

        this.autoTranlate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (TranslateSettingActivity.this.autoTranlate.isChecked())
                {
                    TranslateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_AUTO_TRANSLATION,
                            MyDbAdapter.PARAM_VALUE_ON);
                    TranslateSettingActivity.this.autoTranlate.setText(R.string.activity_translatesetting_enabletranslate);
                }
                else
                {
                    TranslateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_AUTO_TRANSLATION,
                            MyDbAdapter.PARAM_VALUE_OFF);

                    TranslateSettingActivity.this.autoTranlate.setText(R.string.activity_translatesetting_disabletranslate);
                }
            }
        });

        String translateEngine = null;
        translateEngine = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE);
        if (translateEngine.equals(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE))
        {
            this.google.setChecked(true);
        }
        else
        {
            this.bing.setChecked(true);
        }

        this.bing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (TranslateSettingActivity.this.bing.isChecked())
                {
                    TranslateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE,
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_MICROSOFT_BING);
                    TranslateSettingActivity.this.google.setChecked(false);
                }
            }
        });

        this.google.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (TranslateSettingActivity.this.google.isChecked())
                {
                    TranslateSettingActivity.this.myDb.updateSetting(
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE,
                            MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE);
                    TranslateSettingActivity.this.bing.setChecked(false);
                }
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case ADD:
            return showAdd(this);
        case DELETE:
            return showDelete(this);
        }
        return super.onCreateDialog(id);
    }

    // -------------------------------
    // delete translate
    // -------------------------------
    private Dialog showDelete(Context ctx)
    {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(R.string.activity_translatesetting_deletethis);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TranslateSettingActivity.this.myDb.deleteTranslation(TranslateSettingActivity.this._id);
                        TranslateSettingActivity.this.myCursor.requery();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        this.myCursor.requery();
        return builder.create();
    }

    // ------------------------------------
    // add translate
    // -------------------------------------
    private Dialog showAdd(Context ctx)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.activity_translatesetting_addnewtranlate);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_add_translate, null);
        builder.setView(textEntryView);

        this.userId = (EditText) textEntryView.findViewById(R.id.uid_translate);
        this.translateFrom = (Spinner) textEntryView.findViewById(R.id.Spinner_setting_from);
        this.translateTo = (Spinner) textEntryView.findViewById(R.id.Spinner_setting_to);

        this.ARABIC = getString(R.string.arabic);
        this.CHINISE_SIMPLE = getString(R.string.chinese_simple);
        this.CHINESE_TRADITIONAL = getString(R.string.chinese_traditional);
        this.DUTC = getString(R.string.dutch);
        this.ENGLISH = getString(R.string.english);
        this.FRENCH = getString(R.string.french);
        this.GERMAN = getString(R.string.german);
        this.GREEK = getString(R.string.greek);
        this.ITALIAN = getString(R.string.italian);
        this.JAPANISE = getString(R.string.japanese);
        this.KOREA = getString(R.string.korea);
        this.PORTUGESE = getString(R.string.portugese);
        this.RUSSIAN = getString(R.string.russian);
        this.SPANISH = getString(R.string.spanish);

        /** Language List (Only for display to Spinner) */
        String[] languageNames =
        { this.ARABIC, this.CHINISE_SIMPLE, this.CHINESE_TRADITIONAL,
                this.DUTC, this.ENGLISH, this.FRENCH, this.GERMAN, this.GREEK,
                this.ITALIAN, this.JAPANISE, this.KOREA, this.PORTUGESE,
                this.RUSSIAN, this.SPANISH };
        // Create Array List of Language
        ArrayList<String> languageNameList = new ArrayList<String>();
        for (int i = 0; i < languageNames.length; i++)
        {
            languageNameList.add(languageNames[i]);
        }

        // ---------------------------------
        // Prepare Layout
        // ---------------------------------
        setTitle(R.string.dialog_translate_SelectTranslationLanguage);

        // Set Adapter to Spinner
        ArrayAdapter<String> languageSelectAdapter = new ArrayAdapter<String>(
                ctx, android.R.layout.simple_spinner_item, languageNameList);
        languageSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.translateFrom.setAdapter(languageSelectAdapter);

        this.translateTo.setAdapter(languageSelectAdapter);

        // ---------------------------------
        // Set listener (From Spinner)
        // ---------------------------------
        this.translateFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                TranslateSettingActivity.this.fromLanguage = TranslateSettingActivity.this.languageValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // ---------------------------------
        // Set listener (To Spinner)
        // ---------------------------------
        this.translateTo.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                TranslateSettingActivity.this.toLanguage = TranslateSettingActivity.this.languageValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TranslateSettingActivity.this.myDb.insertTranslation(
                                TranslateSettingActivity.this.userId.getText().toString(),
                                TranslateSettingActivity.this.fromLanguage,
                                TranslateSettingActivity.this.toLanguage);
                        TranslateSettingActivity.this.myCursor.requery();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
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
}
