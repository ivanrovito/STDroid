package com.anhuioss.crowdroid;

public interface IGeneral
{
    //Communication Lock
    public static Object       commLock__                       = new Object();

    public static final String SERVICE_NAME_TWITTER             = "StatusNet";

    public static final int    SERVICE_ID_TWITTER               = 0;

    public static final String TYPE                             = "type";

    public static final String TYPE_HELP                        = "help";

    public static final String TYPE_LICENSE                     = "license";

    //menu

    public static final int    OPTION_MENU_TEXT_REFRESH         = R.string.Refresh;

    public static final int    OPTION_MENU_IMAGE_REFRESH        = android.R.drawable.ic_menu_rotate;

    public static final int    OPTION_MENU_TEXT_TWEET           = R.string.dialog_update_newTweet;

    public static final int    OPTION_MENU_IMAGE_TWEET          = android.R.drawable.ic_menu_edit;

    public static final int    OPTION_MENU_TEXT_DIRECT_MESSAGE  = R.string.activity_timeline_tab_directmessage;

    public static final int    OPTION_MENU_IMAGE_DIRECT_MESSAGE = android.R.drawable.ic_menu_myplaces;

    public static final int    OPTION_MENU_TEXT_MORE_PAGE       = R.string.next;

    public static final int    OPTION_MENU_IMAGE_MORE_PAGE      = android.R.drawable.ic_menu_search;

    public static final int    OPTION_MENU_TEXT_PREV_PAGE       = R.string.prev;

    public static final int    OPTION_MENU_IMAGE_PREV_PAGE      = android.R.drawable.ic_menu_revert;

    public static final int    OPTION_MENU_TEXT_SETTINGS        = R.string.menu_timeline_settings;

    public static final int    OPTION_MENU_IMAGE_SETTINGS       = android.R.drawable.ic_menu_preferences;

    public static final int    OPTION_MENU_TEXT_LICENSE         = R.string.License;

    public static final int    OPTION_MENU_IMAGE_LICENSE        = android.R.drawable.ic_menu_info_details;

    public static final int    OPTION_MENU_TEXT_HELP            = R.string.Help;

    public static final int    OPTION_MENU_IMAGE_HELP           = android.R.drawable.ic_menu_help;

    public static final int    OPTION_MENU_TEXT_FIND            = R.string.FindUser;

    public static final int    OPTION_MENU_IMAGE_FIND           = android.R.drawable.ic_menu_search;

    public static final int    OPTION_MENU_TEXT_ABOUT           = R.string.About;

    public static final int    OPTION_MENU_IMAGE_ABOUT          = android.R.drawable.ic_menu_compass;

    public static final int    OPTION_MENU_SEARCH               = R.string.dialog_searchinfo_searchsometing;

    public static final int    OPTION_MENU_IMAGE_SEARCH         = android.R.drawable.ic_menu_search;

    public static final int    OPTION_MENU_MY_FAVORITE          = android.R.drawable.btn_star_big_on;

    public static final int    OPTION_MENU_MY_FAVORITE_TITLE    = R.string.Favorite;

    public static final int    OPTION_MENU_LOGOUT               = android.R.drawable.ic_menu_close_clear_cancel;

    public static final int    OPTION_MENU_LOGOUT_TITLE         = R.string.Logout;

}
