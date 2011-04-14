package com.anhuioss.crowdroid.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MySimpleAdapter extends SimpleAdapter
{

    private int[]                          mTo;

    private String[]                       mFrom;

    private ViewBinder                     mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int                            mResource;

    private LayoutInflater                 mInflater;

    public MySimpleAdapter(Context context,
            List<? extends Map<String, ?>> data, int resource, String[] from,
            int[] to)
    {
        super(context, data, resource, from, to);
        this.mData = data;
        this.mResource = resource;
        this.mFrom = from;
        this.mTo = to;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
    * @see android.widget.Adapter#getView(int, View, ViewGroup)
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return createViewFromResource(position, convertView, parent,
                this.mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource)
    {
        View v;
        if (convertView == null)
        {
            v = this.mInflater.inflate(resource, parent, false);

            final int[] to = this.mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++)
            {
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
        }
        else
        {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    @SuppressWarnings("unchecked")
    private void bindView(int position, View view)
    {
        final Map dataSet = this.mData.get(position);
        if (dataSet == null)
        {
            return;
        }

        final ViewBinder binder = this.mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = this.mFrom;
        final int[] to = this.mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++)
        {
            final View v = holder[i];
            if (v != null)
            {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null)
                {
                    text = "";
                }

                boolean bound = false;
                if (binder != null)
                {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound)
                {
                    if (v instanceof Checkable)
                    {
                        if (data instanceof Boolean)
                        {
                            ((Checkable) v).setChecked((Boolean) data);
                        }
                        else
                        {
                            throw new IllegalStateException(
                                    v.getClass().getName()
                                            + " should be bound to a Boolean, not a "
                                            + data.getClass());
                        }
                    }
                    else if (v instanceof TextView)
                    {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        if (text instanceof String)
                        {
                            if (text.equals(MainSettingActivity.SETTING_MENU_ACCOUNT_MANAGE)
                                    && MainSettingActivity.mode == MainSettingActivity.MODE_ACCOUNT_MANAGE_DISABLED)
                            {
                                ((TextView) v).setTextColor(Color.GRAY);
                            }
                            else
                            {
                                ((TextView) v).setTextColor(Color.WHITE);
                            }
                            //	if(text.length() == MainSettingActivity.SETTING_MENU_ACCOUNT_MANAGE.length())
                            //		((TextView)v).setBackgroundColor(Color.GRAY);
                        }

                        setViewText((TextView) v, text);
                    }
                    else if (v instanceof ImageView)
                    {

                        //                    	Bitmap bitmap = WebImageBuilder.returnBitMap("http://timg3.ddmapimg.com/city/images/citynew/2696c2126e903cf8d-7f23.jpg");
                        //                    	((ImageView) v).setImageBitmap(bitmap);
                        //                    	setViewImage((ImageView) v,"http://timg3.ddmapimg.com/city/images/citynew/2696c2126e903cf8d-7f23.jpg");
                        if (data instanceof Integer)
                        {
                            setViewImage((ImageView) v, (Integer) data);
                        }
                        else
                        {
                            setViewImage((ImageView) v, text);
                        }
                    }
                    else
                    {
                        throw new IllegalStateException(
                                v.getClass().getName()
                                        + " is not a "
                                        + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * This method is called instead of {@link #setViewImage(ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, String)
     */
    @Override
    public void setViewImage(ImageView v, int value)
    {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * By default, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     *
     * This method is called instead of {@link #setViewImage(ImageView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, int) 
     */
    @Override
    public void setViewImage(ImageView v, String value)
    {}

}
