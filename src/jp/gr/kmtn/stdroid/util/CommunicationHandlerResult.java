package jp.gr.kmtn.stdroid.util;

import java.util.HashMap;
import java.util.Map;

public class CommunicationHandlerResult
{

    public static final int ERROR_OAuthMessageSignerException     = 1000;

    public static final int ERROR_OAuthExpectationFailedException = 1001;

    public static final int ERROR_OAuthCommunicationException     = 1002;

    public static final int ERROR_ClientProtocolException         = 1003;

    public static final int ERROR_CLIENT_IOException              = 1004;

    public static final int ERROR_XmlPullParserException          = 1100;

    public static final int ERROR_COMMUNIATION_IOException        = 1101;

    private int             resultCode                            = 0;

    private String          message;

    private Object          data;

    //Map(code, Message)
    Map<String, String>     resultMap                             = new HashMap<String, String>();

    public CommunicationHandlerResult()
    {

        //Http Status
        this.resultMap.put("0", "No Server Ip");
        this.resultMap.put("200", "OK");
        this.resultMap.put("400", "Bad Request");
        this.resultMap.put("401", "Unauthorized");
        this.resultMap.put("403", "Forbidden");
        this.resultMap.put("404", "Not Found");
        this.resultMap.put("408", "Request Time-out");
        this.resultMap.put("500", "Internal Server Error");
        this.resultMap.put("502", "Bad Gateway");
        this.resultMap.put("503", "Service Unavailable");
        this.resultMap.put("504", "Gateway Time-out");

        //Exception
        this.resultMap.put("1000", "OAuthMessageSignerException has occurred");
        this.resultMap.put("1001",
                "OAuthExpectationFailedException has occurred");
        this.resultMap.put("1002", "OAuthCommunicationException has occurred");
        this.resultMap.put("1003", "ClientProtocolException has occurred");
        this.resultMap.put("1004", "Client IOException has occurred");
        this.resultMap.put("1100", "XmlPullParserException has occurred");
        this.resultMap.put("1101", "Communication IOException has occurred");

    }

    public Object getData()
    {
        return this.data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    public int getResultCode()
    {
        return this.resultCode;
    }

    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }

    public int getResultMessage()
    {

        int result = 0;
        switch (this.resultCode)
        {
        case 0:
            result = IErrorMessage.MESSAGE_COMMUNICATION_COMM_ERROR; //Temporary
            break;
        case 200:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_SUCEEDED;
        }
            break;
        case 400:
        case 401:
        case 402:
        case 403:
        case 404:
        case 408:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_AUTHORIZATION_FAILED;
        }
            break;
        case 500:
        case 502:
        case 503:
        case 504:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_SERVER_ERROR;
        }
            break;
        case ERROR_XmlPullParserException:
        case ERROR_CLIENT_IOException:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_CLIENT_ERROR;
        }
            break;

        case ERROR_OAuthMessageSignerException:
        case ERROR_OAuthExpectationFailedException:
        case ERROR_OAuthCommunicationException:
        case ERROR_ClientProtocolException:
        case ERROR_COMMUNIATION_IOException:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_COMM_ERROR;
        }
            break;

        default:
        {
            result = IErrorMessage.MESSAGE_COMMUNICATION_CLIENT_ERROR;
        }
        }
        return result;

    }

    public String getDetialMessage()
    {

        return this.resultMap.get(String.valueOf(this.resultCode));

    }

    public String getMessage()
    {

        return this.message;

    }

    public void setMessage(String message)
    {

        this.message = message;

    }

}
