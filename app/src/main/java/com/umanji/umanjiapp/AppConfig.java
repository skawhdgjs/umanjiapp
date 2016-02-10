package com.umanji.umanjiapp;

public interface AppConfig {

    String REST_SERVER_URL = "http://172.30.1.3:3000/v1"; /* 로컬 API Server */
//    String REST_SERVER_URL  = "http://52.24.76.87:3000/v1"; /* 아마존 API Server */


    /* 아마존 S3 */
    String REST_S3_URL      = "https://s3-ap-northeast-1.amazonaws.com/umanji-0001/";
//    String REST_S3_URL      = "https://s3.ap-northeast-2.amazonaws.com/umanjiapp-0001/";

    int iItemViewCacheSize              = 30;
    int scaledImageLimit                = 800;


    int loadingLimit                    = 10;


    int LEVEL_LOCAL                     = 18;
    int LEVEL_COMPLEX                   = 15;
    int LEVEL_DONG                      = 13;
    int LEVEL_GUGUN                     = 11;
    int LEVEL_DOSI                      = 8;
    int LEVEL_COUNTRY                   = 2;


    String dataLoaded   = "dataLoaded";


    String api_token_check              = "POST /token/check";

    String api_signin                   = "POST /signin";
    String api_logout                   = "DELETE /logout";
    String api_signup                   = "POST /signup";

    String api_sign_getByPoint          = "GET  /channels/sign/point";
    String api_channels_getByPoint      = "GET  /channels/point";
    String api_channels_createSpot      = "POST /channels/spot";


    String api_channels_get             = "GET  /channels/id";

    String api_channels_findMarkers     = "GET  /channels/markers";
    String api_channels_findPosts       = "GET  /channels/posts";


    String api_channels_posts_find      = "GET  /channels/id/posts";
    String api_channels_keywords_find   = "GET  /channels/id/keywords";
    String api_channels_members_find    = "GET  /channels/id/members";
    String api_channels_likes_find      = "GET  /channels/id/likes";
    String api_channels_communities_find= "GET  /channels/id/communities";
    String api_channels_spots_find      = "GET  /channels/id/spots";

    String api_channels_spots_update    = "PUT  /channels/id/spots";



    String api_links_create             = "POST /channels/id/link";
    String api_links_createPost         = "POST /channels/id/post";
    String api_links_createSpot         = "POST /channels/id/spot";
    String api_links_createCommunity    = "POST /channels/id/community";
    String api_links_createKeyword      = "POST /channels/id/keyword";


    String api_channels_gcm             = "PUT  /channels/id/gcm";

    String api_channels_id_update       = "PUT  /channels/id";
    String api_profile_id_update        = "PUT  /channels/id/profile";

    String api_channels_id_delete       = "DELETE /channels/id";

    String api_noites_find              = "GET  /noties";
    String api_noites_new_count         = "GET  /noties/new/count";

    String api_noites_read              = "PUT  /noties/read";

    ////////

    String api_channels_join            = "POST /channels/id/join";
    String api_channels_unJoin          = "DELETE /channels/id/join";

    String api_channels_like            = "POST /channels/id/like";
    String api_channels_unLike          = "DELETE /channels/id/like";


    String api_users_get                = "GET  /users/id";

    String api_photo                    = "POST /photo";



    String TYPE_MAIN                    = "MAIN";
    String TYPE_SPOTS                   = "SPOTS";
    String TYPE_MAIN_MARKER             = "MAIN_MARKER";
    String TYPE_MAIN_POST               = "MAIN_POST";

    String TYPE_POST                    = "POST";
    String TYPE_SPOT                    = "SPOT";
    String TYPE_SPOT_INNER              = "SPOT_INNER";

    String TYPE_INFO_CENTER             = "INFO_CENTER";
    String TYPE_USER                    = "USER";
    String TYPE_MEMBER                  = "MEMBER";
    String TYPE_COMMUNITY               = "COMMUNITY";
    String TYPE_KEYWORD                 = "KEYWORD";

    String TYPE_NOTY                    = "NOTY";

    String TYPE_LIKE                    = "LIKE";



    // ERROR
    String TYPE_ERROR_AUTH              = "TYPE_ERROR_AUTH";


    String FOCUSED_ITEM_MARKER          = "FOCUSED_ITEM_MARKER";

}
