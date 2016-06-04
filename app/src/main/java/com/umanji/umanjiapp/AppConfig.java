package com.umanji.umanjiapp;

public interface AppConfig {

    int APP_VERSION      = 1;

//    String REST_SERVER_URL = "http://172.30.1.2:3000/v1"; /* 로컬 API Server */
   String REST_SERVER_URL  = "http://52.24.76.87:3000/v1"; /* 아마존 API Server */


    /* 아마존 S3 */
    String REST_S3_URL        = "https://s3-ap-northeast-1.amazonaws.com/umanji-0001/";
//    String REST_S3_URL        = "https://s3.ap-northeast-2.amazonaws.com/umanjiapp-0001/";

    int iItemViewCacheSize              = 30;
    int scaledImageLimit                = 800;


    int loadingLimit                    = 10;

    int LEVEL_LOCAL                     = 18;
    int LEVEL_COMPLEX                   = 15;
    int LEVEL_DONG                      = 14;
    int LEVEL_GUGUN                     = 12;
    int LEVEL_DOSI                      = 8;
    int LEVEL_COUNTRY                   = 2;


    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int DEFAULT_MIN_FLING_VELOCITY      = 10000;

    String dataLoaded                   = "dataLoaded";



    String api_system_version           = "GET  /system/version";

    String api_channels_gcm             = "PUT  /channels/id/gcm";

    String api_token_check              = "POST /token/check";
    String api_photo                    = "POST /photo";

    String api_signin                   = "POST /signin";
    String api_logout                   = "DELETE /logout";
    String api_signup                   = "POST /signup";

    String api_channels_getByPoint      = "GET  /channels/point";
    String api_channels_createSpot      = "POST /channels/spot";
    String api_channels_createComplex   = "POST /channels/complex";
    String api_channels_createCommunity = "POST /channels/community";
    String api_channels_createKeyword   = "POST /channels/keyword";

    String api_channels_createPost      = "POST /channels/post";
    String api_channels_create          = "POST /channels";

    String api_channels_get             = "GET  /channels/id";
    String api_channels_findOne         = "GET  /channels/findOne";
    String api_channels_findEmail       = "GET  /channels/findEmail";

    String api_channels_findMarkers     = "GET  /channels/markers";
    String api_channels_findPosts       = "GET  /channels/posts";


    String api_channels_posts_find          = "GET  /channels/id/posts";
    String api_channels_keywords_find       = "GET  /channels/id/keywords";
    String api_channels_members_find        = "GET  /channels/id/members";
    String api_channels_likes_find          = "GET  /channels/id/likes";
    String api_channels_communities_find    = "GET  /channels/id/communities";
    String api_channels_community_find      = "GET  /channels/id/keywords";     //community list on spot
    String api_channels_spots_find          = "GET  /channels/id/spots";
    String api_channels_roles_find          = "GET  /channels/findRole";



    String api_profile_posts_find       = "GET  /profile/id/posts";
    String api_profile_spots_find       = "GET  /profile/id/spots";
    String api_profile_communities_find = "GET  /profile/id/communities";

    String api_main_findPosts           = "GET  /main/posts";

    String api_keyword_findPosts        = "GET  /keyword/posts";
    String api_keyword_findChannels     = "GET  /keyword/channels";

    String api_main_findMarkers         = "GET  /main/markers";
    String api_main_findDistributions   = "GET  /main/distribution";
    String api_main_findAds             = "GET  /main/ads";
    String api_main_findAds2            = "GET  /main/ads2";

    String api_main_search              = "GET  /main/search";

    String api_complex_findSpots        = "GET  /complex/id/spots";


    String api_findCommunity            = "GET  /channels/community";

    String api_channels_id_update       = "PUT  /channels/id";
    String api_profile_id_update        = "PUT  /channels/id/profile";
    String api_profile_role_update      = "PUT  /channels/email";

    String api_channels_id_delete       = "DELETE /channels/id";

    String api_noites_find              = "GET  /noties";
    String api_noites_new_count         = "GET  /noties/new/count";

    String api_noites_read              = "PUT  /noties/read";

    String api_channels_id_vote            = "POST /channels/id/vote";

    String api_channels_id_join            = "POST /channels/id/join";
    String api_channels_id_unJoin          = "DELETE /channels/id/join";
    String api_channels_id_like            = "POST /channels/id/like";
    String api_channels_id_unLike          = "DELETE /channels/id/like";

    String api_channels_id_link            = "POST /channels/id/link";

    String api_channels_id_authority       = "GET /channels/id/authority";

    String TAB_ROLES                    = "역할";
    String TAB_POSTS                    = "정보광장";
    String TAB_LIKES                    = "LIKES";
    String TAB_COMMUNITIES              = "커뮤니티";
    String TAB_SPOTS                    = "장소";
    String TAB_NOTIES                   = "알림";
    String TAB_MEMBERS                  = "멤버";
    String TAB_ABOUT                    = "기타정보";


    String TYPE_MAIN                    = "MAIN";
    String TYPE_SPOTS                   = "SPOTS";
    String TYPE_PROFILE_SPOTS           = "PROFILE_SPOTS";

    String TYPE_PROFILE_COMMUNITIES     = "PROFILE_COMMUNITIES";
    String TYPE_MAIN_MARKER             = "MAIN_MARKER";

    String TYPE_POST                    = "POST";
    String TYPE_SPOT                    = "SPOT";
    String TYPE_SPOT_INNER              = "SPOT_INNER";
    String TYPE_COMPLEX                 = "COMPLEX";
    String TYPE_LOCAL_SPOT              = "LOCAL_SPOT";
    String TYPE_LOCAL_COMPLEX           = "LOCAL_COMPLEX";

    String TYPE_INFO_CENTER             = "INFO_CENTER";
    String TYPE_USER                    = "USER";
    String TYPE_MEMBER                  = "MEMBER";
    String TYPE_COMMUNITY               = "COMMUNITY";
    String TYPE_KEYWORD_COMMUNITY       = "KEYWORD_COMMUNITY";
    String TYPE_KEYWORD                 = "KEYWORD";

    String TYPE_NOTY                    = "NOTY";
    String TYPE_LIKE                    = "LIKE";
    String TYPE_SURVEY                  = "SURVEY";

    String TYPE_LINK                    = "LINK";

    String TYPE_POST_SURVEY             = "POST_SURVEY";

    String TYPE_ERROR_AUTH              = "ERROR_AUTH";

    String TYPE_ADS                     = "ADVERTISE";

    String MAP_CREATE_COMPLEX           = "CREATE_COMPLEX";
    String MAP_UPDATE_ADDRESS           = "UPDATE_ADDRESS";

    String ZOOM_IN                      = "ZOOM-IN";
    String ZOOM_OUT                     = "ZOOM-OUT";

    String LOGIN                        = "LOGIN";
    String LOGOUT                       = "LOGOUT";


    String EVENT_LOOK_AROUND            = "LOOK_AROUND";
    String EVENT_UPDATEVIEW             = "UPDATE_VIEW";

    String SLIDING_COLLAPSED            = "COLLAPSED";
    String SLIDING_ANCHORED             = "ANCHORED";
    String SLIDING_EXPANDED             = "EXPANDED";

    int POINT_DEFAULT                   = 10;
    int POINT_CREATE_CHANNEL            = 10;
    int POINT_LIKE                      = 10;
    int POINT_UNLIKE                    = 10;
    int POINT_JOIN                      = 10;
    int POINT_UNJOIN                    = 10;
    int POINT_LINK                      = 10;

    int POINT_CREATE_COMPLEX            = 1000;
    int POINT_STAR_LOCAL                = 1000;
    int POINT_STAR_COMPLEX              = 5000;
    int POINT_STAR_DONG                 = 10000;
    int POINT_STAR_GUGUN                = 50000;
    int POINT_STAR_CITY                 = 100000;
    int POINT_STAR_COUNTRY              = 500000;

    int MARKER_INDEX_CLICKED            = -1;
    int MARKER_INDEX_BY_POST            = -2;


    int CODE_GALLERY_ACTIVITY           = 30;
    int CODE_CAMERA_ACTIVITY            = 31;
}
