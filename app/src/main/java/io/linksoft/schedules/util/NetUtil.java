package io.linksoft.schedules.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.Preference;
import android.preference.PreferenceManager;

import io.linksoft.schedules.data.Settings;

public class NetUtil {

    /**
     * Validate the network connection. Supports checking for WiFi only when
     * this settings has been set to true.
     *
     * @param activity Activity
     * @return Has network connection
     */
    public static boolean hasNetworkConnection(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null)
            return false;

        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(Settings.PREF_WIFI_ONLY, false))
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        return networkInfo.isConnected();
    }

}
