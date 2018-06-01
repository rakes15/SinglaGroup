package com.singlagroup.customwidgets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.singlagroup.MainActivity;

/**
 * Created by rakes on 06-Mar-17.
 */

public class AppPrinterService {
    private static String TAG = AppPrinterService.class.getSimpleName();
    private Context context;
    public int AppPrinterServiceCheck(Context context,String PackageName,String PlayStoreUrl){
        this.context = context;
        int status = 0;
        boolean isAppInstalled = appInstalledOrNot(PackageName);

        if(isAppInstalled) {
            //This intent will help you to launch if the package is already installed
//            Intent LaunchIntent = this.context.getPackageManager().getLaunchIntentForPackage(PackageName);
//            context.startActivity(LaunchIntent);
            Log.i(TAG,"Application is already installed.");
            status = 1;
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            Log.i(TAG,"Application is not currently installed.");
            status = 0;
        }
        return status;
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = this.context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"appInstalledOrNot Exception:"+e.toString());
            return false;
        }
    }
}
