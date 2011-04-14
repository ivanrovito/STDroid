package com.anhuioss.crowdroid.info;

import android.graphics.Bitmap;

public class UserInfo
{

    public static final String UID            = "uid";

    public static final String USER_IMAGE_URL = "userImageURL";

    public static final String USER_IMAGE     = "userImage";

    public static final String SCREENNAME     = "screenName";

    private String             uid;

    private String             screenName;

    private String             description;

    private String             followCount;

    private String             followerCount;

    private String             notifications;

    private String             following;

    private String             userImageURL;

    private String             groupName;

    private Bitmap             userImage;

    private int                utcOffset;

    public UserInfo()
    {}

    public String getUid()
    {
        return this.uid;
    }

    public String getScreenName()
    {
        return this.screenName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getFollowCount()
    {
        return this.followCount;
    }

    public String getFollowerCount()
    {
        return this.followerCount;
    }

    public String getNotifications()
    {
        return this.notifications;
    }

    public String getFollowing()
    {
        return this.following;
    }

    public String getUserImageURL()
    {
        return this.userImageURL;
    }

    public String getGroupName()
    {
        return this.groupName;
    }

    public Bitmap getUserImage()
    {
        return this.userImage;
    }

    public int getUtcOffset()
    {
        return this.utcOffset;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setFollowCount(String followCount)
    {
        this.followCount = followCount;
    }

    public void setFollowerCount(String followerCount)
    {
        this.followerCount = followerCount;
    }

    public void setNotifications(String notifications)
    {
        this.notifications = notifications;
    }

    public void setFollowing(String following)
    {
        this.following = following;
    }

    public void setUserImageURL(String userImageURL)
    {
        this.userImageURL = userImageURL;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setUserImage(Bitmap userImage)
    {
        this.userImage = userImage;
    }

    public void setUtcOffset(int utcOffset)
    {
        this.utcOffset = utcOffset;
    }

}
