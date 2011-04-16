package jp.gr.kmtn.stdroid.util;

public class AccountInfo
{

    /**
     * twitter or follow5 and crowdroid business userid
     */
    private String userId      = null;

    /**
     * current service such as twitter or follow5...
     */
    private String service     = null;

    /**
     * user name, follow5 login user name, twitter is null
     */
    private String name        = null;

    /**
     * twitter screen name
     */
    private String screenName  = null;

    /**
     * follow5 login password
     */
    private String password    = null;

    /**
     * twitter access_token
     */
    private String accessToken = null;

    /**
     * twitter token_secret
     */
    private String tokenSecret = null;

    //private String 
    //------------------------
    /**
     * get name
     @return string
     */
    //------------------------
    public String getName()
    {
        return this.name;
    }

    //------------------------
    /**
     * get accessToken
     @return string
     */
    //------------------------
    public String getAccessToken()
    {
        return this.accessToken;
    }

    //------------------------
    /**
     * get tokenSecret
     @return string
     */
    //------------------------
    public String getTokenSecret()
    {
        return this.tokenSecret;
    }

    //------------------------
    /**
     * get userId
     @return string
     */
    //------------------------
    public String getUserId()
    {
        return this.userId;
    }

    //------------------------
    /**
     * get password
     @return String
     */
    //------------------------
    public String getPassWord()
    {
        return this.password;
    }

    //------------------------
    /**
     * get service
     @return String
     */
    //------------------------
    public String getService()
    {
        return this.service;
    }

    //------------------------
    /**
     * get screenName
     @return String
     */
    //------------------------
    public String getScreenName()
    {
        return this.screenName;
    }

    //------------------------
    /**
     * set userId
     @param userId String
     */
    //------------------------
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    //------------------------
    /**
     * set password
     @param password String
     */
    //------------------------
    public void setPassword(String password)
    {
        this.password = password;
    }

    //------------------------
    /**
     * set service
     @param service String
     */
    //------------------------
    public void setService(String service)
    {
        this.service = service;
    }

    //------------------------
    /**
     * set screen name
     @param screenName String
     */
    //------------------------
    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    //------------------------
    /**
     * set name
     @param name String
     */
    //------------------------
    public void setName(String name)
    {
        this.name = name;
    }

    //------------------------
    /**
     * set accessToken
     @param name String
     */
    //------------------------
    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    //------------------------
    /**
     * set accessToken
     @param name String
     */
    //------------------------
    public void setTokenSecret(String tokenSecret)
    {
        this.tokenSecret = tokenSecret;
    }
}
