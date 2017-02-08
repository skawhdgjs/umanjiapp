package com.umanji.umanjiapp.model;

/**
 * Created by paul on 7/31/16.
 */
public class TestCDN {

    final String INTEREST_ADMINISTRATION      = "정치관심가";

    final String ROLE_INFO_WORLD              = "info_world";
    final String ROLE_INFO_COUNTRY            = "info_country";
    final String ROLE_INFO_ADMIN              = "info_admin";
    final String ROLE_INFO_LOCALITY           = "info_locality";
    final String ROLE_INFO_THOROUGHFARE       = "info_thoroughfare";
    final String ROLE_UMANJI_COW              = "umanji_cow";
    final String ROLE_UMANJI_CITIZEN          = "umanji_citizen";

    final String ROLE_AD_WORLD                = "ad_world";
    final String ROLE_AD_COUNTRY              = "ad_country";
    final String ROLE_AD_ADMIN                = "ad_admin";
    final String ROLE_AD_LOCALITY             = "ad_locality";
    final String ROLE_AD_THOROUGHFARE         = "ad_thoroughfare";

    private String aa;
    private String bb;

    int foo = 0;
    int bar = 0;
    /*
    public TestCDN(String a, String b){

        aa = a;
        bb = b;

    }
*/


    public boolean isUpper(String a, String b) {
        switch(a){

            case ROLE_INFO_WORLD:
                foo = 35;
                break;

            case ROLE_INFO_COUNTRY:
                foo = 30;
                break;

            case ROLE_INFO_ADMIN:
                foo = 25;
                break;

            case ROLE_INFO_LOCALITY:
                foo = 20;
                break;

            case ROLE_INFO_THOROUGHFARE:
                foo = 15;
                break;

            case ROLE_UMANJI_COW:
                foo = 50;
                break;

            case INTEREST_ADMINISTRATION:
                foo = 10;
                break;

            case ROLE_UMANJI_CITIZEN:
                foo = 5;
                break;

            default:
                foo = 0;
                break;
        }

        switch(b){

            case ROLE_INFO_WORLD:
                bar = 35;
                break;

            case ROLE_INFO_COUNTRY:
                bar = 30;
                break;

            case ROLE_INFO_ADMIN:
                bar = 25;
                break;

            case ROLE_INFO_LOCALITY:
                bar = 20;
                break;

            case ROLE_INFO_THOROUGHFARE:
                bar = 15;
                break;

            case ROLE_UMANJI_COW:
                bar = 50;
                break;

            case INTEREST_ADMINISTRATION:
                bar = 10;
                break;

            case ROLE_UMANJI_CITIZEN:
                bar = 5;
                break;

            default:
                bar = 0;
                break;
        }



        if(foo > bar){
            return true;
        } else {
            return false;
        }

    }




}
