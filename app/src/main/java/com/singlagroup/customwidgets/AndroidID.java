package com.singlagroup.customwidgets;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by rakes on 18-Oct-17.
 */

public class AndroidID {

    public static String UniqueID(Context context){
        String UniqueID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return UniqueID.toUpperCase();
    }
}
