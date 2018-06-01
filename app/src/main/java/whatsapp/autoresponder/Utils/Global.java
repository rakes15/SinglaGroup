package whatsapp.autoresponder.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DatabaseController.CommanStatic;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import orderbooking.StaticValues;
import whatsapp.autoresponder.Api.RestApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static whatsapp.autoresponder.Api.RestApi.BASE_URL;


/**
 * Created by iqor on 8/9/2017.
 */

public class Global {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Log.e("Network Testing", "***Available***");
            return true;
        }

        // Log.e("Network Testing", "***Not Available***");
        return false;
    }

    public static void ShowText(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static RestApi initRetrofit(final Context context) {
        // For logging request & response (Optional)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(RestApi.class);
    }
    public static RestApi initRetrofit2(final Context context) {
        // For logging request & response (Optional)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StaticValues.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(RestApi.class);
    }

    public static final Pattern PATTERN =
            Pattern.compile("__w-((?:-?\\d+)+)__");


    public static String getUrl(String model) {
        model = BASE_URL + model;
        Matcher m = PATTERN.matcher(model);
        int bestBucket = 0;
        if (m.find()) {
            String[] found = m.group(1).split("-");
            for (String bucketStr : found) {
                bestBucket = Integer.parseInt(bucketStr);
                if (bestBucket >= getScreenWidth()) {
                    // the best bucket is the first immediately
                    // bigger than the requested width
                    break;
                }
            }
            if (bestBucket > 0) {
                model = m.replaceFirst("w" + bestBucket);
            }
        }
        return model;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
