package com.umanji.umanjiapp;

public interface AppConfig {

//    String REST_SERVER_URL = "http://10.10.0.241:3000/v1"; /* 로컬 API Server */
    String REST_SERVER_URL  = "http://52.24.76.87:3000/v1"; /* 아마존 API Server */


    /* 아마존 S3 */
    String REST_S3_URL        = "https://s3-ap-northeast-1.amazonaws.com/umanji-0001/";
//    String REST_S3_URL        = "https://s3.ap-northeast-2.amazonaws.com/umanjiapp-0001/";

    int iItemViewCacheSize              = 30;
    int scaledImageLimit                = 800;


    int loadingLimit                    = 10;


    int LEVEL_LOCAL                     = 18;
    int LEVEL_COMPLEX                   = 15;
    int LEVEL_DONG                      = 13;
    int LEVEL_GUGUN                     = 11;
    int LEVEL_DOSI                      = 8;
    int LEVEL_COUNTRY                   = 2;


    int DEFAULT_MIN_FLING_VELOCITY      = 10000;

    String dataLoaded                   = "dataLoaded";


    String api_channels_gcm             = "PUT  /channels/id/gcm";

    String api_token_check              = "POST /token/check";
    String api_photo                    = "POST /photo";

    String api_signin                   = "POST /signin";
    String api_logout                   = "DELETE /logout";
    String api_signup                   = "POST /signup";

    String api_channels_getByPoint      = "GET  /channels/point";
    String api_channels_createSpot      = "POST /channels/spot";
    String api_channels_createComplex   = "POST /channels/complex";
    String api_channels_create          = "POST /channels";

    String api_channels_get             = "GET  /channels/id";

    String api_channels_findMarkers     = "GET  /channels/markers";
    String api_channels_findPosts       = "GET  /channels/posts";


    String api_channels_posts_find          = "GET  /channels/id/posts";
    String api_channels_keywords_find       = "GET  /channels/id/keywords";
    String api_channels_members_find        = "GET  /channels/id/members";
    String api_channels_likes_find          = "GET  /channels/id/likes";
    String api_channels_communities_find    = "GET  /channels/id/communities";
    String api_channels_spots_find          = "GET  /channels/id/spots";


    String api_profile_posts_find       = "GET  /profile/id/posts";
    String api_main_findPosts           = "GET  /main/posts";
    String api_main_findMarkers         = "GET  /main/markers";

    String api_complex_findSpots        = "GET  /complex/id/spots";


    String api_channels_id_update       = "PUT  /channels/id";
    String api_profile_id_update        = "PUT  /channels/id/profile";

    String api_channels_id_delete       = "DELETE /channels/id";

    String api_noites_find              = "GET  /noties";
    String api_noites_new_count         = "GET  /noties/new/count";

    String api_noites_read              = "PUT  /noties/read";

    String api_channels_id_join            = "POST /channels/id/join";
    String api_channels_id_unJoin          = "DELETE /channels/id/join";
    String api_channels_id_like            = "POST /channels/id/like";
    String api_channels_id_unLike          = "DELETE /channels/id/like";

    String api_channels_id_link            = "POST /channels/id/link";

    String TYPE_MAIN                    = "MAIN";
    String TYPE_SPOTS                   = "SPOTS";
    String TYPE_MAIN_MARKER             = "MAIN_MARKER";

    String TYPE_POST                    = "POST";
    String TYPE_SPOT                    = "SPOT";
    String TYPE_SPOT_INNER              = "SPOT_INNER";
    String TYPE_COMPLEX                 = "COMPLEX";

    String TYPE_INFO_CENTER             = "INFO_CENTER";
    String TYPE_USER                    = "USER";
    String TYPE_MEMBER                  = "MEMBER";
    String TYPE_COMMUNITY               = "COMMUNITY";
    String TYPE_KEYWORD                 = "KEYWORD";

    String TYPE_NOTY                    = "NOTY";
    String TYPE_LIKE                    = "LIKE";

    String TYPE_LINK                    = "LINK";


    String TYPE_ERROR_AUTH              = "ERROR_AUTH";

    String MAP_CREATE_COMPLEX           = "MAP_CREATE_COMPLEX";


    String ZOOM_IN                      = "ZOOM-IN";
    String ZOOM_OUT                     = "ZOOM-OUT";


    int POST_MARKER_INDEX               = -1;
}
