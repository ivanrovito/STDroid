package com.anhuioss.crowdroid.twitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.info.SearchInfo;
import com.anhuioss.crowdroid.info.UserInfo;

public class TwitterJsonParser
{
    public static List<SearchInfo> parseSearchInfo(String msg)
            throws JSONException
    {

        // Prepare ResultList
        List<SearchInfo> jsonInfoList = new ArrayList<SearchInfo>();

        JSONObject clientJSONObj = new JSONObject(msg);
        JSONArray searchinfototal = clientJSONObj.getJSONArray("results");
        for (int i = 0; i < searchinfototal.length(); i++)
        {
            SearchInfo searchinfo = new SearchInfo();
            UserInfo userinfo = new UserInfo();
            JSONObject searchinfosingle = searchinfototal.getJSONObject(i);
            searchinfo.setTime_search(searchinfosingle.getString("created_at").replace(
                    ",", ""));
            userinfo.setScreenName(searchinfosingle.getString("from_user"));
            userinfo.setUserImageURL(searchinfosingle.getString("profile_image_url"));
            userinfo.setUid(searchinfosingle.getString("from_user_id"));
            searchinfo.setUserInfo(userinfo);
            searchinfo.setStatus(searchinfosingle.getString("text").replaceAll(
                    "&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;",
                    "\""));
            searchinfo.setMessageId(searchinfosingle.getString("id"));
            jsonInfoList.add(searchinfo);
        }

        return jsonInfoList;
    }

}
