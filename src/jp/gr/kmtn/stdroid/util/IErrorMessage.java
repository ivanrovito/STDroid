package jp.gr.kmtn.stdroid.util;

import jp.gr.kmtn.stdroid.R;

public interface IErrorMessage
{

    public static final int MESSAGE_TRANSLATION_ERROR                        = R.string.error_message_auto_translation;

    //--------------------------------------
    // Resister Account
    //--------------------------------------
    public static final int MESSAGE_REGISTRATION_TWITTER_SUCCEEDED           = R.string.error_message_twitter_account_register_suceeded;

    public static final int MESSAGE_REGISTRATION_TWITTER_FAILED              = R.string.error_message_twitter_account_register_failed;

    public static final int MESSAGE_REGISTRATION_CROWDROIDBUSINESS_SUCCEEDED = R.string.error_message_crowdroid_account_register_suceeded;

    public static final int MESSAGE_REGISTRATION_CROWDROIDBUSINESS_FAILED    = R.string.error_message_crowdroid_account_register_failed;

    //--------------------------------------
    // Communication
    //--------------------------------------
    public static final int MESSAGE_COMMUNICATION_SUCEEDED                   = R.string.error_message_communication_handler_suceeded;

    public static final int MESSAGE_COMMUNICATION_AUTHORIZATION_FAILED       = R.string.error_message_communication_handler_authorization_failed;

    public static final int MESSAGE_COMMUNICATION_SERVER_ERROR               = R.string.error_message_communication_handler_server_error;

    public static final int MESSAGE_COMMUNICATION_COMM_ERROR                 = R.string.error_message_communication_handler_communication_error;

    public static final int MESSAGE_COMMUNICATION_CLIENT_ERROR               = R.string.error_message_communication_handler_client_error;

}
