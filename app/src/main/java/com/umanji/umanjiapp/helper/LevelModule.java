package com.umanji.umanjiapp.helper;

/**
 * Created by paul on 8/20/16.
 */
public class LevelModule {
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
}
