package jp.gr.kmtn.stdroid.dialog;

import java.util.ArrayList;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.translate.BingTranslate;
import jp.gr.kmtn.stdroid.translate.GoogleTranslate;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.api.translate.Language;

@SuppressWarnings("unused")
public class TranslateDialog extends AlertDialog
{

    private Button              okButton;

    private Button              cancelButton;

    private Spinner             spinnerFrom;

    private Spinner             spinnerTo;

    private ArrayList<String>   languageList;

    private View                target;

    private String              fromLanguage;

    private String              toLanguage;

    private String              result;

    private String              originalText;

    /**
     * "zh-CHS", "zh-CHT" is bing translate
     * "zh-CN", "zh-TW" is google translate
     */
    private static final String BING_TRASLATE[] = new String[]
                                                { "zh-CHS", "zh-CHT" };

    private String              st1             = "";

    private MyDbAdapter         db;

    private ProgressDialog      progress;

    /** Language List */
    private String[]            languageValues  =
                                                {
            Language.ARABIC,
            //these are not same
            Language.CHINESE_SIMPLIFIED, Language.CHINESE_TRADITIONAL,
            Language.DUTCH, Language.ENGLISH, Language.FRENCH, Language.GERMAN,
            Language.GREEK, Language.ITALIAN, Language.JAPANESE,
            Language.KOREAN, Language.PORTUGESE, Language.RUSSIAN,
            Language.SPANISH                   };

    private String              ARABIC;

    private String              CHINISE_SIMPLE;

    private String              CHINESE_TRADITIONAL;

    private String              DUTC;

    private String              ENGLISH;

    private String              FRENCH;

    private String              GERMAN;

    private String              GREEK;

    private String              ITALIAN;

    private String              JAPANISE;

    private String              KOREA;

    private String              PORTUGESE;

    private String              RUSSIAN;

    private String              SPANISH;

    String                      translate_engine;

    /** Handler (Called when translation is finished) */
    Handler                     mHandler        = new Handler() {
                                                    @Override
                                                    public void handleMessage(
                                                            Message msg)
                                                    {
                                                        //Close Progress Dialog
                                                        TranslateDialog.this.progress.dismiss();

                                                        String translatedText = msg.getData().getString(
                                                                "translatedText");
                                                        if (translatedText != null)
                                                        {
                                                            showResult(translatedText);
                                                        }
                                                        String detectedResault = msg.getData().getString(
                                                                "detectedResault");
                                                        if (detectedResault != null)
                                                        {
                                                            TranslateDialog.this.spinnerFrom.setSelection(getItemPosition(detectedResault));
                                                            TranslateDialog.this.fromLanguage = detectedResault;
                                                        }
                                                        else
                                                        {
                                                            TranslateDialog.this.spinnerFrom.setSelection(getItemPosition(TranslateDialog.this.fromLanguage));
                                                        }
                                                        TranslateDialog.this.spinnerTo.setSelection(getItemPosition(TranslateDialog.this.toLanguage));
                                                    }
                                                };

    //-----------------------------------------------------------------------------
    /**
     *  Constructor
     */
    //-----------------------------------------------------------------------------
    public TranslateDialog(final Context context, View target,
            MyDbAdapter dbAdapter)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View textEntryView = inflater.inflate(R.layout.dialog_translate,
                null);
        setView(textEntryView);

        this.db = dbAdapter;

        //Get Target View to write the translated text
        this.target = target;

        //Init Views
        this.spinnerFrom = (Spinner) textEntryView.findViewById(R.id.Spinner_language_from);
        this.spinnerTo = (Spinner) textEntryView.findViewById(R.id.Spinner_language_to);
        this.okButton = (Button) textEntryView.findViewById(R.id.okButton);
        this.cancelButton = (Button) textEntryView.findViewById(R.id.cancelButton);

        //Get Last Translate Language
        this.fromLanguage = this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_LAST_TRANSLATION_FROM);
        this.toLanguage = this.db.getStatusValue(MyDbAdapter.PARAM_STATUS_LAST_TRANSLATION_TO);
        this.translate_engine = this.db.getSettingValue(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE);

        if (this.translate_engine.equals(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_MICROSOFT_BING))
        {
            this.languageValues[1] = BING_TRASLATE[0];
            this.languageValues[2] = BING_TRASLATE[1];
        }
        this.ARABIC = getContext().getString(R.string.arabic);
        this.CHINISE_SIMPLE = getContext().getString(R.string.chinese_simple);
        this.CHINESE_TRADITIONAL = getContext().getString(
                R.string.chinese_traditional);
        this.DUTC = getContext().getString(R.string.dutch);
        this.ENGLISH = getContext().getString(R.string.english);
        this.FRENCH = getContext().getString(R.string.french);
        this.GERMAN = getContext().getString(R.string.german);
        this.GREEK = getContext().getString(R.string.greek);
        this.ITALIAN = getContext().getString(R.string.italian);
        this.JAPANISE = getContext().getString(R.string.japanese);
        this.KOREA = getContext().getString(R.string.korea);
        this.PORTUGESE = getContext().getString(R.string.portugese);
        this.RUSSIAN = getContext().getString(R.string.russian);
        this.SPANISH = getContext().getString(R.string.spanish);

        /** Language List (Only for display to Spinner) */
        String[] languageNames =
        { this.ARABIC, this.CHINISE_SIMPLE, this.CHINESE_TRADITIONAL,
                this.DUTC, this.ENGLISH, this.FRENCH, this.GERMAN, this.GREEK,
                this.ITALIAN, this.JAPANISE, this.KOREA, this.PORTUGESE,
                this.RUSSIAN, this.SPANISH };

        //Create Array List of Language
        ArrayList<String> languageNameList = new ArrayList<String>();
        for (int i = 0; i < languageNames.length; i++)
        {
            languageNameList.add(languageNames[i]);
        }

        //---------------------------------
        // Prepare Layout
        //---------------------------------
        setTitle(R.string.dialog_translate_translationresault);

        //Set Adapter to Spinner
        ArrayAdapter<String> languageSelectAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, languageNameList);
        languageSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerFrom.setAdapter(languageSelectAdapter);
        this.spinnerTo.setAdapter(languageSelectAdapter);

        if (target instanceof TextView)
        {
            detected(((TextView) target).getText().toString());
        }
        if (target instanceof EditText)
        {
            detected(((EditText) target).getText().toString());
            //Select lastSecected Language
        }

        //---------------------------------
        // Set listener (From Spinner)
        //---------------------------------
        this.spinnerFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                TranslateDialog.this.fromLanguage = TranslateDialog.this.languageValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //---------------------------------
        // Set listener (To Spinner)
        //---------------------------------
        this.spinnerTo.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                TranslateDialog.this.toLanguage = TranslateDialog.this.languageValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //---------------------------------
        // Set listener (OK Button)
        //---------------------------------
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //Close Dialog
                dismiss();

                //Update DB
                TranslateDialog.this.db.updateStatus(
                        MyDbAdapter.PARAM_STATUS_LAST_TRANSLATION_FROM,
                        TranslateDialog.this.fromLanguage);
                TranslateDialog.this.db.updateStatus(
                        MyDbAdapter.PARAM_STATUS_LAST_TRANSLATION_TO,
                        TranslateDialog.this.toLanguage);

                //Translate
                //translate(getSourceText(), from, to);
                translate(getSourceText(), TranslateDialog.this.fromLanguage,
                        TranslateDialog.this.toLanguage);

            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();

            }
        });

    }

    //-----------------------------------------------------------------------------
    /**
     *  Get Item Position from Language Value
     */
    //-----------------------------------------------------------------------------
    private int getItemPosition(String name)
    {

        int position = -1;

        if (name != null)
        {
            for (int i = 0; i < this.languageValues.length; i++)
            {

                if (name.equals(this.languageValues[i]))
                {
                    ;
                    position = i;
                    return position;
                }
            }
        }

        return position;

    }

    //-----------------------------------------------------------------------------
    /**
     *  Get source text from view
     */
    //-----------------------------------------------------------------------------
    private String getSourceText()
    {

        String text = null;

        if (this.target instanceof EditText)
        {
            text = ((EditText) this.target).getText().toString();

        }
        else if (this.target instanceof TextView)
        {
            text = ((TextView) this.target).getText().toString();
        }

        return text;
    }

    //
    /**
     * detected the language<br>
     * @return String language
     */
    //
    private void detected(final String text)
    {
        this.originalText = text;

        if (text == null)
        {
            return;
        }
        //Prepare Thread
        new Thread(new Runnable() {

            @Override
            public void run()
            {
                String detectedResault = null;
                // TODO Auto-generated method stub
                try
                {
                    if (TranslateDialog.this.translate_engine.equals(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE))
                    {
                        detectedResault = (String) GoogleTranslate.detect(text).getData();
                    }
                    else
                    {
                        detectedResault = (String) BingTranslate.detect(text).getData();
                    }
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                    return;
                }

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("detectedResault", detectedResault);
                msg.setData(bundle);
                TranslateDialog.this.mHandler.sendMessage(msg);
            }

        }).start();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Translate
     */
    //-----------------------------------------------------------------------------
    private void translate(final String text, String from, String to)
    {

        if (text == null)
        {
            return;
        }

        //Show Progress
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();
        //Prepare Thread
        new Thread(new Runnable() {

            @Override
            public void run()
            {

                try
                {
                    if (TranslateDialog.this.translate_engine.equals(MyDbAdapter.PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE))
                    {
                        TranslateDialog.this.result = GoogleTranslate.translate(
                                text, TranslateDialog.this.fromLanguage,
                                TranslateDialog.this.toLanguage).getData().toString();
                    }
                    else
                    {
                        TranslateDialog.this.result = BingTranslate.translate(
                                text, TranslateDialog.this.fromLanguage,
                                TranslateDialog.this.toLanguage).getData().toString();
                    }
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }

                //Notify to Handler
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("translatedText", TranslateDialog.this.result);
                msg.setData(bundle);
                TranslateDialog.this.mHandler.sendMessage(msg);
            }
        }, "translate").start();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Show Translation Result to Alert Dialog.
     */
    //-----------------------------------------------------------------------------
    private Dialog showResult(final String translatedText)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View textEntryView = inflater.inflate(
                R.layout.dialog_translate_show, null);
        TextView tx1 = (TextView) textEntryView.findViewById(R.id.translatedtext);
        tx1.setText(translatedText);
        TextView tx2 = (TextView) textEntryView.findViewById(R.id.originaltext);
        tx2.setText(this.originalText);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_translate_translationresault);
        builder.setView(textEntryView);

        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub

                        if (TranslateDialog.this.target instanceof EditText
                                && translatedText != null)
                        {
                            ((EditText) TranslateDialog.this.target).setText(translatedText);
                        }

                        dismiss();
                    }
                });
        return builder.show();
    }

    @Override
    public void show()
    {
        //Show Progress	
        super.show();
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();
    }
}
