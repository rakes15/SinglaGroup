package com.singlagroup.customwidgets;

import android.content.Context;
import android.util.Log;

import com.singlagroup.LoginActivity;
import com.singlagroup.responsedatasets.ResponseSessionDataset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerActiveSessionManage;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Rakesh on 16-Jan-17.
 */

public class SessionManage {
    private static String TAG = SessionManage.class.getSimpleName();
    private static Context context;
    public static void CallRetrofitSessionLogout(final Context context,final String DeviceID, final String SessionID, final String UserID,final String CompanyID){
        SessionManage.context = context;
        final ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("UserID", UserID);
        params.put("CompanyID", CompanyID);
        Log.d(TAG,"Logout Parameters:"+params.toString());
        Call<ResponseSessionDataset> call = apiService.getSessionLogout(params);
        call.enqueue(new Callback<ResponseSessionDataset>() {
            @Override
            public void onResponse(Call<ResponseSessionDataset> call, retrofit2.Response<ResponseSessionDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            Log.d(TAG,"Logout Success:"+msg);
                            if (context!=null)
                            CallRetrofitSessionActiveTime(context,1);
                        } else {
                            Log.d(TAG,"Logout Failure:"+msg);
                        }
                    }else {
                        Log.d(TAG,"response code:"+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG,"Login Exception:"+e.getMessage());
                    //MessageDialog.MessageDialog(LoginActivity.this,"LogInUser API",e.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseSessionDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
//                MessageDialog messageDialog=new MessageDialog();
//                messageDialog.MessageDialog(LoginActivity.this,"Failure","LogInUser API",t.toString());
            }
        });
    }
    public static void CallRetrofitSessionActiveTime(Context context, final int flag){
        SessionManage.context = context;
        final DatabaseSqlLiteHandlerActiveSessionManage DBSessionManage = new DatabaseSqlLiteHandlerActiveSessionManage(SessionManage.context);
        String ActiveTime = "";
        LoginActivity obj = new LoginActivity();
        final String[] str = obj.GetSharePreferenceSession(SessionManage.context);
        if(str!=null)
            ActiveTime = DBSessionManage.getTime(str[0],flag);
            //Log.d(TAG, "SessionManage:" + DBSessionManage.getActiveSessionAllDetails(str[0]).toString());
        if (!ActiveTime.isEmpty()) {
            final ApiInterface apiService = ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
            Map<String, String> params = new HashMap<>();
            params.put("DeviceID", str[3]);
            params.put("SessionID", str[0]);
            params.put("UserID", str[4]);
            params.put("CompanyID", str[14]);
            params.put("ActiveTime", ActiveTime);
            Log.e(TAG, "Active Time Parameters:" + params.toString());
            Call<ResponseSessionDataset> call = apiService.getUserActiveTimeOnApp(params);
            call.enqueue(new Callback<ResponseSessionDataset>() {
                @Override
                public void onResponse(Call<ResponseSessionDataset> call, retrofit2.Response<ResponseSessionDataset> response) {
                    try {
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                DBSessionManage.UpdateServerFlag(1, str[0],flag);
                                DBSessionManage.ActiveSessionManageTableDelete();
                                Log.d(TAG, "ActiveTime Success:" + msg);
                            } else {
                                Log.d(TAG, "ActiveTime failure:" + msg);
                            }
                        } else {
                            //MessageDialog.MessageDialog(LoginActivity.this,"","Server not responding"+response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Login Exception:" + e.getMessage());
                        //MessageDialog.MessageDialog(LoginActivity.this,"LogInUser API",e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseSessionDataset> call, Throwable t) {
                    Log.e(TAG, "Failure: " + t.toString());
//                MessageDialog messageDialog=new MessageDialog();
//                messageDialog.MessageDialog(LoginActivity.this,"Failure","LogInUser API",t.toString());
                }
            });
        }
    }
    public static void CallRetrofitSessionActiveTimeBySessionID(final Context context, String DeviceID, final String SessionID, String UserID, String CompanyID, String ActiveTime){
        SessionManage.context = context;
        final ApiInterface apiService = ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("UserID", UserID);
        params.put("CompanyID", CompanyID);
        params.put("ActiveTime", ActiveTime);
        Log.d(TAG, "Active Time Parameters:" + params.toString());
        Call<ResponseSessionDataset> call = apiService.getUserActiveTimeOnApp(params);
        call.enqueue(new Callback<ResponseSessionDataset>() {
            @Override
            public void onResponse(Call<ResponseSessionDataset> call, retrofit2.Response<ResponseSessionDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            DatabaseSqlLiteHandlerActiveSessionManage DBSessionManage = new DatabaseSqlLiteHandlerActiveSessionManage(context);
                            DBSessionManage.UpdateServerFlag(1, SessionID ,1);
                            DBSessionManage.ActiveSessionManageTableDelete();
                            Log.d(TAG, "ActiveTime Success:" + msg);
                        } else {
                            Log.d(TAG, "ActiveTime failure:" + msg);
                        }
                    } else {
                        //MessageDialog.MessageDialog(LoginActivity.this,"","Server not responding"+response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Login Exception:" + e.getMessage());
                    //MessageDialog.MessageDialog(LoginActivity.this,"LogInUser API",e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseSessionDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
//                MessageDialog messageDialog=new MessageDialog();
//                messageDialog.MessageDialog(LoginActivity.this,"Failure","LogInUser API",t.toString());
            }
        });
    }
}
