package jp.gr.kmtn.stdroid.uploadimage;

import jp.gr.kmtn.stdroid.twitter.TwitterHandler;

import org.json.JSONObject;


public class ImageUrlParser
{

    public static String getImageURL(String msg, String type)
    {

        String url = null;

        try
        {

            JSONObject clientJSONObj = new JSONObject(msg);

            if (type.equals(TwitterHandler.AUTH_TYPE_BASIC))
            {

                JSONObject searchinfototal = clientJSONObj.getJSONObject("upload");

                JSONObject searchinfosingle = searchinfototal.getJSONObject("links");

                // JSONArray link = searchinfosingle.getJSONArray("links");

                url = searchinfosingle.getString("original");

            }
            else
            {

                url = clientJSONObj.getString("url");
            }

        }
        catch (Exception e)
        {

        }

        return url;
    }
}
