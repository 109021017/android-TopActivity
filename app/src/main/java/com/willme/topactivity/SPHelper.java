package com.willme.topactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPHelper {
    public static boolean isShowWindow(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_show_window", true);
    }

    public static void setIsShowWindow(Context context, boolean isShow) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_show_window", isShow).commit();
    }

    public static boolean hasQSTileAdded(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("has_qs_tile_added", false);
    }

    public static void setQSTileAdded(Context context, boolean added) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("has_qs_tile_added", added).commit();
    }

    public static boolean isNotificationToggleEnabled(Context context) {
        if (!hasQSTileAdded(context)) {
            return true;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_noti_toggle_enabled", true);
    }

    public static void setNotificationToggleEnabled(Context context, boolean isEnabled) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_noti_toggle_enabled", isEnabled).commit();
    }
}
