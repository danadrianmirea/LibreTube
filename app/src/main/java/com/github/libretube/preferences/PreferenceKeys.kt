package com.github.libretube.preferences

/**
 * keys for the shared preferences
 */
object PreferenceKeys {
    /**
     * General
     */
    const val LANGUAGE = "language"
    const val REGION = "region"
    const val AUTO_ROTATION = "auto_rotation"

    /**
     * Appearance
     */
    const val THEME_MODE = "theme_toggle"
    const val PURE_THEME = "pure_theme"
    const val ACCENT_COLOR = "accent_color"
    const val GRID_COLUMNS = "grid"
    const val DEFAULT_TAB = "default_tab"
    const val LABEL_VISIBILITY = "label_visibility"
    const val HIDE_TRENDING_PAGE = "hide_trending_page"
    const val APP_ICON = "icon_change"

    /**
     * Instance
     */
    const val FETCH_INSTANCE = "selectInstance"
    const val AUTH_INSTANCE = "selectAuthInstance"
    const val AUTH_INSTANCE_TOGGLE = "auth_instance_toggle"
    const val CUSTOM_INSTANCE = "customInstance"
    const val CLEAR_CUSTOM_INSTANCES = "clearCustomInstances"
    const val LOGIN_REGISTER = "login_register"
    const val DELETE_ACCOUNT = "delete_account"
    const val IMPORT_SUBS = "import_from_yt"

    /**
     * Player
     */
    const val AUTO_FULLSCREEN = "auto_fullscreen"
    const val AUTO_PLAY = "autoplay"
    const val RELATED_STREAMS = "related_streams_toggle"
    const val PLAYBACK_SPEED = "playback_speed"
    const val FULLSCREEN_ORIENTATION = "fullscreen_orientation"
    const val PAUSE_ON_SCREEN_OFF = "pause_screen_off"
    const val WATCH_POSITION_TOGGLE = "watch_position_toggle"
    const val WATCH_HISTORY_TOGGLE = "watch_history_toggle"
    const val SEARCH_HISTORY_TOGGLE = "search_history_toggle"
    const val SYSTEM_CAPTION_STYLE = "system_caption_style"
    const val CAPTION_SETTINGS = "caption_settings"
    const val SEEK_INCREMENT = "seek_increment"
    const val PLAYER_VIDEO_FORMAT = "player_video_format"
    const val DEFAULT_RESOLUTION = "default_res"
    const val BUFFERING_GOAL = "buffering_goal"
    const val SEEKBAR_PREVIEW = "seekbar_preview"
    const val PLAYER_AUDIO_FORMAT = "player_audio_format"
    const val PLAYER_AUDIO_QUALITY = "player_audio_quality"
    const val DEFAULT_SUBTITLE = "default_subtitle"

    /**
     * Download
     */
    const val DOWNLOAD_LOCATION = "download_location"
    const val DOWNLOAD_FOLDER = "download_folder"

    /**
     * Notifications
     */
    const val NOTIFICATION_ENABLED = "notification_toggle"
    const val CHECKING_FREQUENCY = "checking_frequency"
    const val REQUIRED_NETWORK = "required_network"
    const val LAST_STREAM_VIDEO_ID = "last_stream_video_id"

    /**
     * Advanced
     */
    const val DATA_SAVER_MODE = "data_saver_mode"
    const val RESET_SETTINGS = "reset_settings"
    const val CLEAR_SEARCH_HISTORY = "clear_search_history"
    const val CLEAR_WATCH_HISTORY = "clear_watch_history"
    const val CLEAR_WATCH_POSITIONS = "clear_watch_positions"
    const val SHARE_WITH_TIME_CODE = "share_with_time_code"

    /**
     * Error logs
     */
    const val ERROR_LOG = "error_log"

    /**
     * Data
     */
    const val LOCAL_SUBSCRIPTIONS = "local_subscriptions"
}
