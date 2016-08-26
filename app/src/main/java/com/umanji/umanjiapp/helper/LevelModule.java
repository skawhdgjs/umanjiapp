package com.umanji.umanjiapp.helper;

/**
 * Created by paul on 8/20/16.
 */
public class LevelModule {

    public static boolean isHighLevel(int zoom) {
        if (zoom >= 2 && zoom <= 18) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLowLevel(int zoom) {
        if (zoom >= 19 && zoom <= 21) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isGlobalViewLevel(int zoom) {
        if (zoom >= 2 && zoom <= 6) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCountryViewLevel(int zoom) {
        if (zoom >= 7 && zoom <= 14) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isKeywordTouchable(int zoom) {
        if (zoom >= 2 && zoom <= 7) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTalkTouchable(int zoom) {
        if (zoom >= 8) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPoliticTouchable(int zoom) {
        if (zoom >= 6 && zoom <= 7) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isComplexCreatable(int zoom) {
        if (zoom >= 15 && zoom <= 17) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSpotCreatable(int zoom) {
        if (zoom >= 18) {
            return true;
        } else {
            return false;
        }
    }

    public static int getMainPostActionCode(int level) {

        int result = 1800;

        switch (level) {
            case 2:
                result = 200;
                break;

            case 3:
                result = 300;
                break;

            case 4:
                result = 400;
                break;

            case 5:
                result = 500;
                break;

            case 6:
                result = 600;
                break;

            case 7:
                result = 700;
                break;

            case 8:
                result = 800;
                break;

            case 9:
                result = 900;
                break;

            case 10:
                result = 1000;
                break;

            case 11:
                result = 1100;
                break;

            case 12:
                result = 1200;
                break;

            case 13:
                result = 1300;
                break;

            case 14:
                result = 1400;
                break;

            case 15:
                result = 1500;
                break;

            case 16:
                result = 1600;
                break;

            case 17:
                result = 1700;
                break;

            default:
                result = 1800;
                break;
        }

        return result;
    }

}
