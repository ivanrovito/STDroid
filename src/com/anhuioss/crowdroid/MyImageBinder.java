package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class MyImageBinder implements ViewBinder
{
    int               color;

    ArrayList<String> tagList;

    //
    /**
     * this method is public
     * handler the color value
     */
    //

    @SuppressWarnings("static-access")
    public MyImageBinder(String colorString, ArrayList<String> tagList)
    {
        this.tagList = tagList;
        Integer it = new Integer(0);
        this.color = it.parseInt(colorString);
    }

    //---------------------------------------------------------------------------------------
    /**
     * Customized for Setting Bitmap to Image View.
     */
    //---------------------------------------------------------------------------------------
    @Override
    public boolean setViewValue(View view, Object data,
            String textRepresentation)
    {

        if ((view instanceof ImageView) & (data instanceof Bitmap))
        {

            ImageView iv = (ImageView) view;

            Bitmap bm = (Bitmap) data;
            iv.setImageBitmap(bm);
            return true;
        }

        else if ((view instanceof TextView) & (data instanceof String))
        {

            TextView tv = (TextView) view;
            tv.setTextColor(this.color);
            String ms = (String) data;

            if (this.tagList != null)
            {
                for (String tag : this.tagList)
                {
                    Matcher m = Pattern.compile(tag, Pattern.CASE_INSENSITIVE).matcher(
                            ms);
                    String replacetext = "";
                    for (int i = 0; i < ms.length(); i++)
                    {
                        String tmp = "#";
                        replacetext = replacetext + tmp;
                    }
                    ms = m.replaceAll(replacetext);
                }
                //      				  }
            }

            tv.setText(ms);

            return true;
        }

        return false;

    }
}
