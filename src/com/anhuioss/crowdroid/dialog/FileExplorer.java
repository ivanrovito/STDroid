package com.anhuioss.crowdroid.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.uploadimage.ImageUrlParser;
import com.anhuioss.crowdroid.uploadimage.UploadImage;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;

public class FileExplorer extends Dialog
{

    private File                  baseDir;

    private File[]                files;

    private ListView              listView;

    private String                filePath;

    private Button                okButton;

    private Button                cancelButton;

    private TextView              targetView;

    private ImageView             prepareView;

    private Spinner               fileSpinner;

    private EditText              edit;

    String[]                      serverMessage;

    private static String         picLocation;

    private Context               context;

    // Preview Dialog
    private AlertDialog           previewDialog;

    private static final String[] allfilespinner =
                                                 { "/sdcard/download",
            "/sdcard/DCIM", "/sdcard/picture"   };

    protected static final int    SHOWPICTURE    = 1;

    ProgressDialog                progress       = new ProgressDialog(
                                                         getContext());

    // private Button cameraButton;
    //	
    // private Button pictureButton;

    ArrayList<String>             list           = new ArrayList<String>();

    /** Handler */
    Handler                       mHandler       = new Handler() {
                                                     @Override
                                                     public void handleMessage(
                                                             Message msg)
                                                     {
                                                         // Close Progress Dialog
                                                         String iamgeUrl = msg.getData().getString(
                                                                 "imageurl");
                                                         if (iamgeUrl != null)
                                                         {
                                                             int length = FileExplorer.this.edit.getText().length();
                                                             FileExplorer.this.edit.append("\n"
                                                                     + iamgeUrl);
                                                             FileExplorer.this.edit.setSelection(length);
                                                         }
                                                         FileExplorer.this.progress.dismiss();
                                                     }

                                                 };

    public FileExplorer(Context context)
    {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_file_explorer);
        // Init Views
        System.gc();
        this.prepareView = (ImageView) findViewById(R.id.prepareView);
        this.listView = (ListView) findViewById(R.id.list_file);
        this.okButton = (Button) findViewById(R.id.okButton);
        this.fileSpinner = (Spinner) findViewById(R.id.file_spinner);
        this.prepareView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                showPictureDialog(FileExplorer.this.prepareView.getDrawable());
            }

        });
        this.prepareView.setDrawingCacheEnabled(true);
        this.okButton.setText(android.R.string.ok);
        // prepareView.getd
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String path = FileExplorer.this.filePath;
                if (isPicturePath(path))
                {
                    FileExplorer.this.targetView.setText(path);
                    dismiss();
                    showUploadProgress();
                    System.gc();
                }
                else
                {
                    setTitle(R.string.dialog_file_explorer_selectpicture);

                }
            }
        });
        this.cancelButton = (Button) findViewById(R.id.cancelButton);
        this.cancelButton.setText(android.R.string.cancel);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        final ArrayList<String> allfiles = new ArrayList<String>();
        for (int i = 0; i < allfilespinner.length; i++)
        {
            if (allfilespinner[i].equals("/sdcard/download"))
            {
                allfiles.add("download");
            }
            else if (allfilespinner[i].equals("/sdcard/DCIM"))
            {
                allfiles.add("camera");
            }
            else if (allfilespinner[i].equals("/sdcard/picture"))
            {
                File pictureFile = new File("/sdcard/picture");
                if (pictureFile.isDirectory())
                {
                    allfiles.add("picture");
                }

            }

        }
        ArrayAdapter<String> aspinfileAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, allfiles);
        aspinfileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.fileSpinner.setAdapter(aspinfileAdapter);

        this.fileSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {

                String location = allfiles.get(position);
                if (location.equals("download"))
                {
                    location = "/sdcard/download";
                }
                else if (location.equals("camera"))
                {
                    location = "/sdcard/DCIM";
                }
                else if (location.equals("picture"))
                {
                    location = "/sdcard/picture";
                }

                File selectedDir = new File(location);
                if (!selectedDir.exists() || !selectedDir.isDirectory())
                {
                    return;
                }

                try
                {

                    FileExplorer.this.baseDir = selectedDir;
                    if (FileExplorer.this.baseDir.isDirectory())
                    {
                        FileExplorer.this.files = FileExplorer.this.baseDir.listFiles();
                    }
                    setTitle(location);
                    setData(FileExplorer.this.files);
                }
                catch (Exception e)
                {
                    Log.w("StatusDroid", "Error Occured", e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}

        });
        // Set Default Path
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            setTitle(R.string.dialog_file_explorer_selectpicture);
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            this.baseDir = new File(sdcard);
            this.files = this.baseDir.listFiles();
            this.filePath = sdcard;
            setTitle(this.filePath);
            setData(this.files);
        }
        else
        {
            setTitle(R.string.dialog_file_explorer_nosdcard);
        }

    }

    protected void showUploadProgress()
    {
        if (this.serverMessage != null)
        {
            // TODO Auto-generated method stub
            this.progress.setIndeterminate(true);
            this.progress.show();
            new Thread(new Runnable() {

                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    if (FileExplorer.this.targetView.getText() != null)
                    {
                        CommunicationHandlerResult result = UploadImage.UploadImageToServer(
                                FileExplorer.this.targetView.getText().toString(),
                                FileExplorer.this.serverMessage);
                        String imageUrl = ImageUrlParser.getImageURL(
                                (String) result.getData(),
                                FileExplorer.this.serverMessage[0]);

                        // Show url
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("imageurl", imageUrl);
                        msg.setData(bundle);
                        FileExplorer.this.mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    }

    private void showPictureDialog(Drawable drawable)
    {

        // TODO Auto-generated method stub
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        final View textEntryView = inflater.inflate(
                R.layout.dialog_showpicture, null);
        builder.setView(textEntryView);

        ImageView picture = (ImageView) textEntryView.findViewById(R.id.show_picture);
        picture.setImageDrawable(drawable);
        picture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (FileExplorer.this.previewDialog != null)
                {
                    FileExplorer.this.previewDialog.dismiss();
                }
                FileExplorer.this.previewDialog = null;
                System.gc();
            }
        });

        if (picLocation != null)
        {
            System.gc();
            this.previewDialog = builder.show();
        }

    }

    public void setTarget(TextView uploadImagePathText, EditText edit,
            String[] serverMessage)
    {
        this.targetView = uploadImagePathText;
        this.edit = edit;
        this.serverMessage = serverMessage;
    }

    // ------------------------------------------------------------------------------
    /**
     * @param type
     *            the path of the file
     * @return true if the path is a picture's path<br>
     *         false otherwise
     **/
    // ------------------------------------------------------------------------------
    private boolean isPicturePath(String type)
    {

        String filePath = type.toLowerCase();
        if ((filePath.indexOf(".jpg") != -1)
                || (filePath.indexOf(".jpeg") != -1)
                || (filePath.indexOf(".gif") != -1)
                || (filePath.indexOf(".png") != -1)
                || (type.indexOf(".bmp") != -1))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // ------------------------------------------------------------------------------
    /**
     * @param fileList
     *            the array that stored the file path
     * @return void
     **/
    // ------------------------------------------------------------------------------
    private void setData(File[] fileList)
    {

        // Clean list
        this.list.clear();

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < fileList.length; i++)
        {
            Map<String, Object> item = new HashMap<String, Object>();
            // (flag = true) fileList[i] is a directory or picture path
            boolean flag = false;
            // File Type
            String type = "";
            if (fileList[i].isDirectory())
            {
                // It's a directory and set the picture
                type = type + R.drawable.file_explorer_folder;
                // type = "+";
                flag = true;
            }
            else
            {
                long picturelength = fileList[i].length();
                if (picturelength < 500000)
                {
                    type = fileList[i].getAbsolutePath();

                    if (isPicturePath(type))
                    {
                        // It's a picture path
                        type = "";
                        flag = true;
                    }
                }
            }

            if (flag)
            {
                // Set the image using file path
                item.put("file_type", type);
                // item.put("file_type", type.equals("+")?"+":"");
                // File Name
                item.put("file_name", fileList[i].getName());
                data.add(item);
                // Add the position of directory or picture path to the list
                this.list.add(String.valueOf(i));
            }

        }

        List<Map<String, Object>> newData = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("file_type", R.drawable.file_explorer_parent);
        item.put("file_name", "..");
        newData.add(item);
        for (int j = 0; j < data.size(); j++)
        {
            newData.add(data.get(j));
        }

        // Prepare Adapter
        SimpleAdapter adapter = new SimpleAdapter(getContext(), newData,
                R.layout.list_item_file_exploer, new String[]
                { "file_type", "file_name" }, new int[]
                { R.id.file_type, R.id.file_name });
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                    int position, long id)
            {
                picLocation = null;
                if (position == 0)
                {
                    File parent = FileExplorer.this.baseDir.getParentFile();
                    if (parent.getAbsolutePath().equals("/"))
                    {
                        return;
                    }
                    FileExplorer.this.baseDir = parent;
                    FileExplorer.this.filePath = FileExplorer.this.baseDir.getAbsolutePath().toString();
                    // filePath.setText(baseDir.getAbsolutePath());
                    setTitle(FileExplorer.this.filePath);
                    FileExplorer.this.files = FileExplorer.this.baseDir.listFiles();
                    setData(FileExplorer.this.files);
                    FileExplorer.this.prepareView.setImageBitmap(null);
                }
                else
                {
                    itemSelected(position, FileExplorer.this.list);
                }

            }
        });

        System.gc();
    }

    // ------------------------------------------------------------------------------
    /**
     * @param position
     *            the position of the item that you have selected
     * @param list
     *            the position of directory or picture path
     * @return void
     **/
    // ------------------------------------------------------------------------------
    private void itemSelected(int position, ArrayList<String> list)
    {

        this.prepareView.setImageBitmap(null);
        System.gc();
        // get the position in files
        int number = Integer.valueOf(list.get(position - 1));
        this.filePath = this.files[number].getAbsolutePath();
        setTitle(this.filePath);

        if (this.files[number].isDirectory())
        {
            this.baseDir = this.files[number];
            this.files = this.baseDir.listFiles();
            setData(this.files);
        }
        else
        {
            this.prepareView.setImageBitmap(BitmapFactory.decodeFile(this.filePath));
            picLocation = this.filePath;
        }
    }
}
