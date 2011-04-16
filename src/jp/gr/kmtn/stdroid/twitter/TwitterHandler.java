package jp.gr.kmtn.stdroid.twitter;

import it.sauronsoftware.base64.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.gr.kmtn.stdroid.IGeneral;
import jp.gr.kmtn.stdroid.info.DirectMessageInfo;
import jp.gr.kmtn.stdroid.info.SearchInfo;
import jp.gr.kmtn.stdroid.info.TimeLineInfo;
import jp.gr.kmtn.stdroid.info.UserInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MultiPartFormOutputStream;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.util.Log;


public class TwitterHandler
{
    /** Twitter Original API */
    public static final String TWITTER_ORIGINAL_API_SERVER = "http://api.twitter.com/";

    /** Auth Type(OAuth) */
    public static final String AUTH_TYPE_OAUTH             = "oauth";

    /** Auth Type(Basic) */
    public static final String AUTH_TYPE_BASIC             = "basic";

    /** Consumer_Kye for Twitter */
    public static final String CONSUMER_KEY                = "rbkewUHsJc0pdKMmt3PHQ";

    /** Consumer_Secret for Twitter */
    public static final String CONSUMER_SECRET             = "qnfmOYFFiqzen67Ww6TxqlaeY45Ct7Cm1hOriyU";

    /** API Server Address */
    private static String      apiServer__                 = TWITTER_ORIGINAL_API_SERVER;

    /** Auth Type (Basic/OAuth) */
    public static String       authType__;

    /** Account Name(ScreenName/AccessToken) */
    public static String       accountName__;

    /** Account Secret(Password/TokenSecret) */
    public static String       accountSecret__;

    // -----------------------------------------------------------------------------------------------
    /**
     * Set Account Info for this Handler. Generally Called after Login Process.
     */
    // -----------------------------------------------------------------------------------------------
    public static void setAccount(String accountName, String accountSecret,
            String authType, String proxyServer)
    {

        TwitterHandler.accountName__ = accountName;
        TwitterHandler.accountSecret__ = accountSecret;
        TwitterHandler.authType__ = authType;
        if (proxyServer != null)
        {
            apiServer__ = proxyServer;
        }
    }

    /**
     * Verify Account
     */
    public static CommunicationHandlerResult verifyUser(String accessToken,
            String tokenSecret, String type, String apiServerAddress)
    {
        CommunicationHandlerResult result = httpGet(apiServerAddress
                + "/account/verify_credentials.xml", accessToken, tokenSecret,
                type);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractUserInfo(result);
        }
        return result;
    }

    /**
     * Get the user's name and screen_name from table.
     * 
     * @param accessToken
     *            user's accessToken.
     * @param accessSecret
     *            user's tokenSecret.
     * @return boolean<br>
     *         true if get the user's information, false otherwise.
     */
    public static CommunicationHandlerResult getUserInfo(String screenName)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/user_timeline.xml?screen_name=" + screenName,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractUserInfo(result);
        }
        return result;
    }

    /**
     * Get friend list
     * 
     * @param accessToken
     *            user's accessToken.
     * @param accessSecret
     *            user's tokenSecret.
     * @return ArrayList
     */
    public static CommunicationHandlerResult getFriendsList(String cursor)
    {
        CommunicationHandlerResult result = httpGet(
                "/statuses/friends.xml?count=10&cursor=" + cursor,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            extractUserInfoList(result);
        }
        return result;
    }

    /**
     * Get my followers' list
     * 
     * @param accessToken
     *            user's accessToken.
     * @param accessSecret
     *            user's tokenSecret.
     * @return ArrayList
     */
    public static CommunicationHandlerResult getFollowersList(long cursor)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/followers.xml?cursor=" + cursor, accountName__,
                accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractUserInfoList(result);
        }
        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Set Follow
     */
    // -----------------------------------------------------------------------------------
    public static CommunicationHandlerResult setFollow(String uid,
            boolean follow)
    {

        // Set Post Parameter
        HashMap<String, String> paramMap = new HashMap<String, String>();

        // Request
        CommunicationHandlerResult result;
        if (follow)
        {
            result = httpPost(apiServer__ + "/friendships/create/" + uid
                    + ".xml", paramMap, accountName__, accountSecret__,
                    authType__);
        }
        else
        {
            result = httpPost(apiServer__ + "/friendships/destroy/" + uid
                    + ".xml", paramMap, accountName__, accountSecret__,
                    authType__);
        }

        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Show Relation between target user.
     */
    public static CommunicationHandlerResult showRelation(String targetId)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/friendships/show.xml?target_id=" + targetId, accountName__,
                accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractRelation(result);
        }
        return result;
    }

    /**
     * パブリックタイムライン情報を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return パブリックタイムライン情報が設定された通信結果
     */
    public static CommunicationHandlerResult getPublicTimeLine(int page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/public_timeline.xml?count=20&page=" + page,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }

        return result;
    }

    /**
     * マイタイムライン情報を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return マイタイムライン情報が設定された通信結果
     */
    public static CommunicationHandlerResult getMyTimeLine(int page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/user_timeline.xml?count=20&page=" + page,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }
        return result;
    }

    /**
     * お気に入り一覧を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return お気に入り一覧が設定された通信結果
     */
    public static CommunicationHandlerResult getFavoriteList(int page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/favorites.xml?page=" + page, accountName__,
                accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }

        return result;
    }

    /**
     * 指定ユーザのタイムライン情報を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @param userId 指定ユーザID
     * @return 指定ユーザのタイムライン情報が設定された通信結果
     */
    public static CommunicationHandlerResult getUserStatusList(String userId,
            String page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/user_timeline.xml?user_id=" + userId + "&page="
                + page, accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }

        return result;
    }

    /**
     * 自分にあてたメッセージ一覧を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return 自分にあてたメッセージ一覧が設定された通信結果
     */
    public static CommunicationHandlerResult getMensionList(int page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/mentions.xml?count=21&page=" + page,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }

        return result;
    }

    /**
     * 特定のメッセージを取得する。
     * 
     * @param messageId メッセージID
     * @return 自分にあてたメッセージ一覧が設定された通信結果
     */
    public static CommunicationHandlerResult getMessageById(String messageId)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/show/" + messageId + ".xml", accountName__,
                accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractTimeLine(result);
        }

        return result;
    }

    /**
     * 受信ダイレクトメッセージ一覧を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return 受信ダイレクトメッセージ一覧が設定された通信結果
     */
    public static CommunicationHandlerResult getDirectMessageReceive(int page)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/direct_messages.xml?count=20&page=" + page, accountName__,
                accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractDirectMessage(result);
        }
        return result;
    }

    /**
     * 送信ダイレクトメッセージ一覧を取得する。
     * 
     * @param page 取得対象インデックス（ページ単位）
     * @return 送信ダイレクトメッセージ一覧が設定された通信結果
     */
    public static CommunicationHandlerResult getDirectMessageSend(int page)
    {

        CommunicationHandlerResult result = httpGet(apiServer__
                + "/direct_messages/sent.xml?count=20&page=" + page,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            result = extractDirectMessage(result);
        }

        return result;
    }

    /**
     * ツイートを投稿する。
     * 
     * @param text 投稿ツイート内容
     * @return 投稿結果が設定された通信結果
     */
    public static CommunicationHandlerResult updateStatus(String text)
    {
        // Set Post Parameter
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("status", text);

        // Request
        CommunicationHandlerResult result = null;

        result = httpPost(apiServer__ + "/statuses/update.xml", paramMap,
                accountName__, accountSecret__, authType__);

        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Update Image though twitpic
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    public static CommunicationHandlerResult uploadImage(String accessToken,
            String tokenSecret, String filePath, String message)
    {

        synchronized (IGeneral.commLock__)
        {

            CommunicationHandlerResult result = new CommunicationHandlerResult();

            try
            {
                URL url = new URL("http://api.twitpic.com/1/uploadAndPost.json");

                // create a boundary string
                String boundary = MultiPartFormOutputStream.createBoundary();
                HttpURLConnection connection = MultiPartFormOutputStream.createConnection(url);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Content-Type",
                        MultiPartFormOutputStream.getContentType(boundary));

                // set some other request headers...
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                // Put Parameters
                MultiPartFormOutputStream out = new MultiPartFormOutputStream(
                        connection.getOutputStream(), boundary);

                out.writeField("key", "49b501cfebb227634ae716c03bff6b0c");
                out.writeField("consumer_token", CONSUMER_KEY);
                out.writeField("consumer_secret", CONSUMER_SECRET);
                out.writeField("oauth_token", accessToken);
                out.writeField("oauth_secret", tokenSecret);
                out.writeField("message", message);
                //	
                // // upload a file
                // out.writeFile("media", "text/plain", new File(filePath));
                out.close();

                // Set Result
                int code = connection.getResponseCode();

                result.setResultCode(code);

            }
            catch (MalformedURLException e)
            {
                result.setResultCode(1010);
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (IOException e)
            {
                result.setResultCode(1011);
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                return result;
            }
        }
    }

    /**
     * リツイートを投稿する。
     * 
     * @param messageId リツイート対象メッセージID
     * @return 投稿結果が設定された通信結果
     */
    public static CommunicationHandlerResult retweet(String messageId)
    {
        // Set Post Parameter
        HashMap<String, String> paramMap = new HashMap<String, String>();

        // Request
        CommunicationHandlerResult result;
        result = httpPost(apiServer__ + "/statuses/retweet/" + messageId
                + ".xml", paramMap, accountName__, accountSecret__, authType__);

        return result;
    }

    /**
     * 投稿を削除する。
     * 
     * @param messageId 削除対象メッセージID
     * @return 削除結果が設定された通信結果
     */
    public static CommunicationHandlerResult destroy(String messageId)
    {
        // Set Post Parameter
        HashMap<String, String> paramMap = new HashMap<String, String>();

        // Request
        CommunicationHandlerResult result;
        result = httpPost(apiServer__ + "/statuses/destroy/" + messageId
                + ".xml", paramMap, accountName__, accountSecret__, authType__);

        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Set Favorite
     */
    // -----------------------------------------------------------------------------------
    public static CommunicationHandlerResult setFavorite(String messageId,
            boolean favorite)
    {
        HashMap<String, String> paramMap = new HashMap<String, String>();

        // Request
        CommunicationHandlerResult result;
        if (!favorite)
        {
            result = httpPost(apiServer__ + "/favorites/destroy/" + messageId
                    + ".xml", paramMap, accountName__, accountSecret__,
                    authType__);
        }
        else
        {
            result = httpPost(apiServer__ + "/favorites/create/" + messageId
                    + ".xml", paramMap, accountName__, accountSecret__,
                    authType__);
        }

        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Send Direct Message
     */
    // -----------------------------------------------------------------------------------
    public static CommunicationHandlerResult directMessage(String screenName,
            String text)
    {
        // Set Post Parameter
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("user", screenName);
        paramMap.put("text", text);

        // Request
        CommunicationHandlerResult result;
        result = httpPost(apiServer__ + "/direct_messages/new.xml", paramMap,
                accountName__, accountSecret__, authType__);

        return result;
    }

    /**
     * Check New At Message
     */
    public static synchronized CommunicationHandlerResult checkNewestAtMessage(
            String newestMessageId)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/mentions.xml", accountName__, accountSecret__,
                authType__);

        result.setData(0);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            int newMessageCount = 0;
            try
            {
                newMessageCount = TwitterXMLParser.parseToGetNewAtMessageCount(
                        newestMessageId, result.getMessage());
                result.setData(newMessageCount);
            }
            catch (XmlPullParserException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                result.setData(newMessageCount);
            }
        }
        return result;
    }

    /**
     * Check New Direct Message
     */
    public static synchronized CommunicationHandlerResult checkNewestDirectMessage(
            String newestMessageId)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/direct_messages.xml", accountName__, accountSecret__,
                authType__);
        result.setData(0);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            int newMessageCount = 0;
            try
            {
                newMessageCount = TwitterXMLParser.parseToGetNewDirectMessageCount(
                        newestMessageId, result.getMessage());
            }
            catch (XmlPullParserException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                result.setData(newMessageCount);
            }
        }
        return result;
    }

    /**
     * Check New General Message
     */
    public static synchronized CommunicationHandlerResult checkNewestGeneralMessage(
            String newestMessageId)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/statuses/public_timeline.xml", accountName__,
                accountSecret__, authType__);
        result.setData(0);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            int newMessageCount = 0;
            try
            {
                newMessageCount = TwitterXMLParser.parseToGetNewGeneralMessageCount(
                        newestMessageId, result.getMessage());
            }
            catch (XmlPullParserException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                result.setData(newMessageCount);
            }
        }
        return result;
    }

    /**
     * Get User List with Keyword.
     */
    public static synchronized CommunicationHandlerResult getFindPeopleInfo(
            int page, String query)
    {
        CommunicationHandlerResult result = httpGet(apiServer__
                + "/1/users/search.xml?q=" + query + "&page=" + page,
                accountName__, accountSecret__, authType__);

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            List<UserInfo> userInfo;
            try
            {
                userInfo = TwitterXMLParser.parseStrangersInfo(result.getMessage());
                result.setData(userInfo);
            }
            catch (XmlPullParserException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
        }
        return result;
    }

    /**
     * Search Time Line contains specified query.
     */
    public static CommunicationHandlerResult searchinfo(String query, int page)
    {

        CommunicationHandlerResult result;
        if (authType__.equals("oauth"))
        {
            result = httpGet(
                    "http://search.twitter.com/search.json?callback=?&q="
                            + query + "&page=" + String.valueOf(page),
                    accountName__, accountSecret__, authType__);
        }
        else
        {
            result = httpGet(apiServer__ + "/search.json?callback=?&q=" + query
                    + "&page=" + String.valueOf(page), accountName__,
                    accountSecret__, authType__);
        }

        if (result.getResultCode() == HttpURLConnection.HTTP_OK)
        {
            List<SearchInfo> searchInfo;
            try
            {
                searchInfo = TwitterJsonParser.parseSearchInfo(result.getMessage());
                result.setData(searchInfo);
            }
            catch (JSONException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Get data which is necessary for getting a new Access Token.
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    public static CommunicationHandlerResult getRegistrationElement(
            String server)
    {

        synchronized (IGeneral.commLock__)
        {
            CommunicationHandlerResult result = new CommunicationHandlerResult();
            result.setResultCode(HttpURLConnection.HTTP_OK);

            // Prepare Data
            Object[] data = new Object[3];
            DefaultOAuthConsumer consumer = new DefaultOAuthConsumer(
                    CONSUMER_KEY, CONSUMER_SECRET);
            OAuthProvider provider = new DefaultOAuthProvider(apiServer__
                    + "/oauth/request_token", apiServer__
                    + "/oauth/access_token", apiServer__ + "/oauth/authorize");
            String requestUrl = null;

            // Get Request Url
            try
            {
                requestUrl = provider.retrieveRequestToken(consumer,
                        OAuth.OUT_OF_BAND);
            }
            catch (OAuthMessageSignerException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (OAuthNotAuthorizedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (OAuthExpectationFailedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (OAuthCommunicationException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                data[0] = consumer;
                data[1] = provider;
                data[2] = requestUrl;
                result.setData(data);
                return result;
            }
        }
    }

    // -----------------------------------------------------------------------------------
    /**
     * Get Token and Token Secret.
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    public static CommunicationHandlerResult getNewToken(
            DefaultOAuthConsumer consumer, OAuthProvider provider, String pin)
    {

        synchronized (IGeneral.commLock__)
        {

            CommunicationHandlerResult result = new CommunicationHandlerResult();
            result.setResultCode(HttpURLConnection.HTTP_OK);

            String[] data = new String[2];

            try
            {
                provider.retrieveAccessToken(consumer, pin);
            }
            catch (OAuthMessageSignerException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthExpectationFailedException);
            }
            catch (OAuthNotAuthorizedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthExpectationFailedException);
            }
            catch (OAuthExpectationFailedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthExpectationFailedException);
            }
            catch (OAuthCommunicationException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                result.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                data[0] = consumer.getToken();
                data[1] = consumer.getTokenSecret();
                result.setData(data);
                return result;
            }
        }
    }

    // -----------------------------------------------------------------------------------
    /**
     * Send Request to Twitter Server and get Input Stream as a result.
     * 
     * @param url
     *            URL for requesting.
     * @param accessToken
     *            user's accessToken.
     * @param tokenSecret
     *            user's tokenSecret.
     * @return String
     */
    // -----------------------------------------------------------------------------------
    private static CommunicationHandlerResult httpGet(String url,
            String accessToken, String tokenSecret, String type)
    {
        CommunicationHandlerResult result = null;

        // HTTPS、Basic認証でアクセスすると１回目は必ず失敗となるため、
        // デフォルトで２回までは実行するよう修正。
        // ApacheHarmony特有の症状のように思われる。
        for (int i = 0; i < 2; i++)
        {
            if (type.equals(AUTH_TYPE_OAUTH))
            {
                result = httpGetOauth(url, accessToken, tokenSecret);
            }
            else
            {
                result = httpGetBasic(url, accessToken, tokenSecret);
            }

            if (result.getResultCode() == HttpURLConnection.HTTP_OK)
            {
                break;
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Http Get (OAuth)
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult httpGetOauth(String url,
            String accessToken, String tokenSecret)
    {

        synchronized (IGeneral.commLock__)
        {

            // Prepare Result Data
            CommunicationHandlerResult chResult = new CommunicationHandlerResult();
            int statusCode = 0;
            String msg = null;

            try
            {

                // Prepare OAuth Key
                CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                        CONSUMER_KEY, CONSUMER_SECRET);
                consumer.setTokenWithSecret(accessToken, tokenSecret);
                HttpGet httpGet = new HttpGet(url);

                // Prepare HttpClient
                HttpClient httpClient = new DefaultHttpClient();
                HttpParams params = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 1000); // Set
                // Connection
                // Time
                // Out
                HttpConnectionParams.setSoTimeout(params, 10000); // Set Data
                // Require
                // Time Out

                // Sign Request With Token
                consumer.sign(httpGet);

                // Get Response
                HttpResponse response = httpClient.execute(httpGet);
                statusCode = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                msg = InputStreamToString(in);

            }
            catch (OAuthMessageSignerException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthMessageSignerException);
            }
            catch (OAuthExpectationFailedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthExpectationFailedException);
            }
            catch (OAuthCommunicationException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (ClientProtocolException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_ClientProtocolException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_COMMUNIATION_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e); // fixexception
                // TODO: handle exception
            }
            finally
            {
                // Set result
                chResult.setMessage(msg);
                chResult.setResultCode(statusCode);
                return chResult;
            }

        }
    }

    // -----------------------------------------------------------------------------------
    /**
     * Http Get (Basic Auth)
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult httpGetBasic(String url,
            String user, String passwd)
    {
        synchronized (IGeneral.commLock__)
        {
            // Prepare Result Data
            CommunicationHandlerResult chResult = new CommunicationHandlerResult();
            int statusCode = 0;
            String msg = null;

            try
            {
                URL httpurl = new URL(url);
                HttpURLConnection request = (HttpURLConnection) httpurl.openConnection();

                // Basic Auth
                byte[] token = (user + ":" + passwd).getBytes("utf-8");
                String check = "Basic "
                        + new String(Base64.encode(token), "utf-8");
                request.setRequestProperty("Authorization", check);

                // Get Response
                request.connect();
                statusCode = request.getResponseCode();
                InputStream in = request.getInputStream();
                msg = InputStreamToString(in);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_COMMUNIATION_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                chResult.setMessage(msg);
                chResult.setResultCode(statusCode);
                return chResult;
            }
        }

    }

    // -----------------------------------------------------------------------------------
    /**
     * Send Update Request to Twitter Server.
     * 
     * @param url
     *            the URL for requesting.
     * @param map
     *            store the parameter for requesting.
     * @param accessToken
     *            user's accessToken.
     * @param accessSecret
     *            user's tokenSecret.
     * @return String
     */
    // -----------------------------------------------------------------------------------
    private static CommunicationHandlerResult httpPost(String url,
            HashMap<String, String> map, String accessToken,
            String accessSecret, String type)
    {
        CommunicationHandlerResult result = null;

        // HTTPS、Basic認証でアクセスすると１回目は必ず失敗となるため、
        // デフォルトで２回までは実行するよう修正。
        // ApacheHarmony特有の症状のように思われる。
        for (int i = 0; i < 2; i++)
        {
            if (type.equals(AUTH_TYPE_OAUTH))
            {
                result = httpPostOauth(url, map, accessToken, accessSecret);
            }
            else
            {
                result = httpPostBasic(url, map, accessToken, accessSecret);
            }

            if (result.getResultCode() == HttpURLConnection.HTTP_OK)
            {
                break;
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------------------
    /**
     * Http Post (OAuth)
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult httpPostOauth(String url,
            HashMap<String, String> map, String accessToken, String accessSecret)
    {
        Log.d("httppost", url);

        synchronized (IGeneral.commLock__)
        {
            // Prepare Result Data
            CommunicationHandlerResult chResult = new CommunicationHandlerResult();
            int statusCode = 0;
            String msg = null;

            try
            {
                // Prepare OAuth Key
                CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                        CONSUMER_KEY, CONSUMER_SECRET);
                consumer.setTokenWithSecret(accessToken, accessSecret);
                HttpPost httpPost = new HttpPost(url);

                // Set Post Parameters
                ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
                Set<String> set = map.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext())
                {
                    String key = iterator.next();
                    postData.add(new BasicNameValuePair(key, map.get(key)));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        postData, HTTP.UTF_8);
                httpPost.setEntity(entity);

                // Prepare HttpClient
                HttpClient httpClient = new DefaultHttpClient();
                HttpParams params = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 1000); // Set
                // Connection
                // Time
                // Out
                HttpConnectionParams.setSoTimeout(params, 10000); // Set Data
                // Require
                // Time Out

                // Add Parameter in order to avoid 417 Error
                httpPost.getParams().setBooleanParameter(
                        CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

                // Sign Request With Token
                consumer.sign(httpPost);

                // Get Response
                HttpResponse response = httpClient.execute(httpPost);
                statusCode = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                msg = InputStreamToString(in);

            }
            catch (OAuthMessageSignerException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthMessageSignerException);
            }
            catch (OAuthExpectationFailedException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthExpectationFailedException);
            }
            catch (OAuthCommunicationException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_OAuthCommunicationException);
            }
            catch (ClientProtocolException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_ClientProtocolException);
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_COMMUNIATION_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                // Set result
                chResult.setMessage(msg);
                chResult.setResultCode(statusCode);
                return chResult;
            }
        }
    }

    // -----------------------------------------------------------------------------------
    /**
     * Http Post (Basic)
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    private static CommunicationHandlerResult httpPostBasic(String url,
            HashMap<String, String> map, String user, String passwd)
    {

        synchronized (IGeneral.commLock__)
        {

            // Prepare Result Data
            CommunicationHandlerResult chResult = new CommunicationHandlerResult();
            int statusCode = 0;
            String msg = null;

            try
            {
                // デバッグ時用出力
                //                Log.w("url", url);
                //                Log.w("map", map.toString());
                //                Log.w("user", user);
                //                Log.w("passwd", passwd);

                URL httpurl = new URL(url);
                HttpURLConnection request = (HttpURLConnection) httpurl.openConnection();
                request.setDoOutput(true);

                // Basic Auth
                byte[] token = (user + ":" + passwd).getBytes("utf-8");
                String check = "Basic "
                        + new String(Base64.encode(token), "utf-8");
                request.setRequestProperty("Authorization", check);

                request.connect();

                // Put Post Params
                OutputStreamWriter bos = new OutputStreamWriter(
                        request.getOutputStream(), "utf-8");
                Set<String> set = map.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext())
                {
                    String key = iterator.next();

                    //Encode parameter value with UTF-8
                    bos.write(key + "=" + Uri.encode(map.get(key)));//Encode parameter value with UTF-8
                }
                bos.flush();
                bos.close();

                // Get Response
                statusCode = request.getResponseCode();
                InputStream in = request.getInputStream();
                msg = InputStreamToString(in);

            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
                chResult.setResultCode(CommunicationHandlerResult.ERROR_COMMUNIATION_IOException);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            finally
            {
                chResult.setMessage(msg);
                chResult.setResultCode(statusCode);
                return chResult;
            }
        }

    }

    // -----------------------------------------------------------------------------------
    /**
     * Convert InputStream to String
     * 
     * @param is
     *            a instance of InputStream
     * @return String
     */
    // -----------------------------------------------------------------------------------
    @SuppressWarnings("finally")
    public static String InputStreamToString(InputStream is)
    {
        String value = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            StringBuffer buf = new StringBuffer();
            String str;

            while ((str = reader.readLine()) != null)
            {
                buf.append(str);
            }

            value = buf.toString();

        }
        catch (UnsupportedEncodingException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            return value;
        }
    }

    /**
     * HTTP通信の通信結果からユーザ情報を抽出する。
     * 
     * @param result HTTP通信の通信結果
     * @return ユーザ情報が設定された通信結果
     */
    private static CommunicationHandlerResult extractUserInfo(
            CommunicationHandlerResult result)
    {
        try
        {
            UserInfo userInfo = TwitterXMLParser.parseUserInfo(result.getMessage());
            result.setData(userInfo);
        }
        catch (XmlPullParserException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        return result;
    }

    /**
     * HTTP通信の通信結果からユーザ情報リストを抽出する。
     * 
     * @param result HTTP通信の通信結果
     * @return ユーザ情報リストが設定された通信結果
     */
    private static CommunicationHandlerResult extractUserInfoList(
            CommunicationHandlerResult result)
    {
        Object[] parseData;
        try
        {
            parseData = TwitterXMLParser.parseUserInfoList(result.getMessage());
            result.setData(parseData);
        }
        catch (XmlPullParserException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        return result;
    }

    /**
     * HTTP通信の通信結果からタイムライン情報を抽出する。
     * 
     * @param result HTTP通信の通信結果
     * @return タイムライン情報が設定された通信結果
     */
    private static CommunicationHandlerResult extractTimeLine(
            CommunicationHandlerResult result)
    {
        List<TimeLineInfo> timeLine;
        try
        {
            timeLine = TwitterXMLParser.parseTimeLine(result.getMessage());
            result.setData(timeLine);
        }
        catch (XmlPullParserException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        return result;
    }

    /**
     * HTTP通信の通信結果からリレーション情報（フォロー／被フォロー等）を抽出する。
     * 
     * @param result HTTP通信の通信結果
     * @return リレーション情報が設定された通信結果
     */
    private static CommunicationHandlerResult extractRelation(
            CommunicationHandlerResult result)
    {
        String[] relation;
        try
        {
            relation = TwitterXMLParser.parserRelation(result.getMessage());
            result.setData(relation);
        }
        catch (XmlPullParserException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        return result;
    }

    /**
     * HTTP通信の通信結果からダイレクトメッセージ一覧を抽出する。
     * 
     * @param result HTTP通信の通信結果
     * @return ダイレクトメッセージ一覧が設定された通信結果
     */
    private static CommunicationHandlerResult extractDirectMessage(
            CommunicationHandlerResult result)
    {
        List<DirectMessageInfo> directMessageInfo;
        try
        {
            directMessageInfo = TwitterXMLParser.parseDirectMessage(
                    result.getMessage(), "receive");
            result.setData(directMessageInfo);
        }
        catch (XmlPullParserException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_XmlPullParserException);
        }
        catch (IOException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
            result.setResultCode(CommunicationHandlerResult.ERROR_CLIENT_IOException);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        return result;
    }
}
