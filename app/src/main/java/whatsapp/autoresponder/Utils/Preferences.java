package whatsapp.autoresponder.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import whatsapp.autoresponder.Model.SchedulerModel;

/**
 * Created by Paresh on 22-04-2017.
 */

public class Preferences {

    public static final String FCM_TOKEN = "FCM_TOKEN";

    public static String KEY_IS_LOCK = "KEY_IS_LOCK";
    public static String KEY_QUEUE = "KEY_QUEUE";
    public static String KEY_IN_EXECUTION = "KEY_IN_EXECUTION";
    public static String KEY_IN_LAST_TITLE = "KEY_IN_LAST_TITLE";

    public static void setPreferenceString(Context c, String pref, String val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putString(pref, val);
        e.commit();
    }

    public static void setPreferenceInt(Context c, String pref, int val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putInt(pref, val);
        e.commit();
    }

    public static void setPreferenceLong(Context c, String pref, Long val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putLong(pref, val);
        e.commit();
    }

    public static void setPreferenceBoolean(Context c, String pref, Boolean val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putBoolean(pref, val);
        e.commit();
    }

    public static void clearPreferenceUid(Context c, String pref) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.remove(pref);
        e.commit();
    }

    public static boolean getPreferenceBoolean(Context c, String pref,Boolean val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(pref, val);
    }

    public static int getPreferenceInt(Context c, String pref, int val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(pref,
                val);
    }

    public static Long getPreferenceLong(Context c, String pref, Long val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getLong(pref,
                val);
    }

    public static String getPreferenceString(Context c, String pref, String val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(pref,
                val);
    }

    public static void storeMessage(Context context,SchedulerModel schedulerModel){
        ArrayList<SchedulerModel> queue = new ArrayList<>();
        String queueStr = Preferences.getPreferenceString(context, Preferences.KEY_QUEUE, "");
        Gson gson = new Gson();
        if (!queueStr.equals("")) {
            queue = gson.fromJson(queueStr, new TypeToken<ArrayList<SchedulerModel>>() {
            }.getType());
        }
        queue.add(schedulerModel);
        String toJson = gson.toJson(queue);
        Preferences.setPreferenceString(context, Preferences.KEY_QUEUE, toJson);
    }
}
