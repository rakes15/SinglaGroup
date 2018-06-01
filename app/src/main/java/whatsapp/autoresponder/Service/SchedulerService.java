package whatsapp.autoresponder.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singlagroup.BuildConfig;
import com.singlagroup.MainActivity;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.DeviceInfoDataset;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import whatsapp.autoresponder.Api.RestApi;
import whatsapp.autoresponder.Model.Rest.ListResponse;
import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.Utils.Global;
import whatsapp.autoresponder.Utils.Preferences;
import whatsapp.database.DBSqliteWhatsApp;

/**
 * Created by SAISHRADDHA on 10-01-2017.
 */

public class SchedulerService extends AccessibilityService {

    public static final String SYMBOL_TO_IDENTIFY_GROUP = "~group~";
    public static final String FETCH_IMAGE = "FETCH_IMAGE";
    public static final String SCHEDULER_MODEL = "SCHEDULER_MODEL";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String TEXT_MESSAGE = "textMessage";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String FILE_URL = "fileUrl";
    public static final String FILE_NAME = "fileName";
    public static final String TYPE_TEXT = "0";
    public static final String TYPE_IMAGE = "1";
    public static final String TYPE_VIDEO = "2";
    public static final String TYPE_PROFILE_PIC = "10";
    public static final String MESSAGE_ID = "messageId";
    public static final String ACTIVITY_TYPE = "activityType";
    public static final String TIME = "time";
    public static final String SCHEDULE_TIME = "schduletime";
    private static final String TAG = SchedulerService.class.getSimpleName();
    DevicePolicyManager deviceManger;
    ComponentName compName;
    private Context context;
    private RestApi api,api2;
    private ArrayList<String> activities;
    private Handler handler = new Handler();
    private String SEARCH_BUTTON = "com.whatsapp:id/menuitem_search";
    private String SEARCH_EDITTEXT = "com.whatsapp:id/search_src_text";
    //    private String CONTACT_NAME_ROW = "com.whatsapp:id/conversations_row_contact_name";
    private String CONTACT_NAME_ROW = "com.whatsapp:id/contactpicker_row_name";
    private String TOOLBAR_NAME = "com.whatsapp:id/conversation_contact";
    private String PROFILE_IMAGE = "com.whatsapp:id/header_placeholder";
    private String SEND_BUTTON = "com.whatsapp:id/send";
    private String SHARE_BUTTON = "Share";
    private String SAVE_TO_BUTTON = "android:id/text1";
    private String SAVE_TO_TEXT = "Save to Gallery";
    private String CONVERSATION_BCAK_BUTTON = "com.whatsapp:id/back";
    private String CONFIRMATION_DIALOG_BTN = "android:id/button1";

    private String MENU_ITEM_SEARCH = "com.whatsapp:id/menuitem_search";
    private String EDIT_TEXT_SEARCH = "com.whatsapp:id/search_src_text";

    private Intent intent;
    private SchedulerModel schedulerModel;

    private boolean isLock = false;

    private DBSqliteWhatsApp DbWhatsApp;
    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getProfilePicPath() {

        String waFolderPath = "/WhatsApp/Media/WhatsApp Profile Photos/";
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File directory = new File(sdCardRoot, waFolderPath);
        if (directory != null) {
            File[] files = directory.listFiles();

            if (files != null) {
                Arrays.sort(files, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }
                });

                if (files.length > 0) {
                    Log.e("storage", "path: " + files[0].getAbsolutePath());
                    return files[0].getAbsolutePath();
                }
            }
        }

        return null;
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        this.context = getApplicationContext();
        this.DbWhatsApp = new DBSqliteWhatsApp(context);
        this.api = Global.initRetrofit(context);
        this.api2 = Global.initRetrofit2(context);
        populateActivitesTotrack();
        info.packageNames = new String[]
                {
                        "com.whatsapp"
                };
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        this.setServiceInfo(info);
    }

    private void populateActivitesTotrack() {
        activities = new ArrayList<>();
        activities.add("com.whatsapp/.Conversation");
        activities.add("com.whatsapp/.HomeActivity");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.intent = intent;
            Preferences.setPreferenceBoolean(this, Preferences.KEY_IN_EXECUTION, true);

            //String Id = intent.getExtras().getString(SchedulerService.SCHEDULER_MODEL, "");
//            String Id = intent.getExtras().getString(DBSqliteWhatsApp.WHATS_APP_KEY_ID, "");
//            schedulerModel = DbWhatsApp.GetCurrentModel(Id,DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
            schedulerModel = (SchedulerModel) intent.getExtras().get(SchedulerService.SCHEDULER_MODEL);

            if (schedulerModel!=null) {

                //Gson gson = new Gson();
                //schedulerModel = gson.fromJson(strModel, SchedulerModel.class);
                //schedulerModel = DbWhatsApp.GetInOutDataByTypeWithFlag();

                String toNumber = schedulerModel.getPhnNumber(); // contains spaces.
                String textMessage = schedulerModel.getText();
                String msgType = schedulerModel.getMessageType();

                if (toNumber.contains(SYMBOL_TO_IDENTIFY_GROUP)) {

                } else {
                    toNumber = toNumber.replace("+", "").replace(" ", "");
                    int length = toNumber.length();
                    toNumber = toNumber.substring(length - 10);
                    toNumber = "91" + toNumber;
                }
                if (schedulerModel.getMessageType().equals(SchedulerService.TYPE_IMAGE)) {
                    String url = schedulerModel.getFileUrl();
                    String fileName = schedulerModel.getFileName();
                    openWhatsAppForMedia(toNumber, textMessage, url, fileName);
                    //new DownloadFileFromURL().execute(url,fileName,toNumber,textMessage);
                } else if (msgType.equals(TYPE_TEXT)) {
                    if (toNumber.contains(SYMBOL_TO_IDENTIFY_GROUP)) {
                        toNumber = toNumber.replace(SYMBOL_TO_IDENTIFY_GROUP, "");
                        openWhatsAppForGroupText(toNumber, textMessage);
                    } else {
                        openWhatsAppForText(toNumber, textMessage);
                    }
                } else if (msgType.equals(TYPE_PROFILE_PIC)) {
                    isLock = true;
                    openWhatsAppForText(toNumber, " ");
                } else if (schedulerModel.getMessageType().equals(SchedulerService.TYPE_VIDEO)) {
                    String url = schedulerModel.getFileUrl();
                    String fileName = schedulerModel.getFileName();
                    openWhatsAppForMedia(toNumber, textMessage, url, fileName);
                    //new DownloadFileFromURL().execute(url,fileName,toNumber,textMessage);
                } else {
                    String url = schedulerModel.getFileUrl();
                    String fileName = schedulerModel.getFileName();
                    openWhatsAppForMedia(toNumber, textMessage, url, fileName);
                    //new DownloadFileFromURL().execute(url,fileName,toNumber,textMessage);
                }
            }
        }catch (Exception e){
            Log.e(TAG,"Exception:"+e.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        boolean shouldToast = true;
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            shouldToast = false;
            ActivityInfo activityInfo = tryGetActivity(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                Log.e("Scheduler", "Activity : " + activityInfo.name);
                Log.e("Scheduler", "Activity : " + activityInfo.applicationInfo.className);
                boolean shouldTrack = isActivityToBeTracked(componentName.flattenToShortString());
            }
        }

        final AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

//        if (Preferences.getPreferenceBoolean(this, Preferences.KEY_IN_EXECUTION, false)) {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (clickById(SEARCH_BUTTON)) {
//                        if (pasteTextToEditText(reminderdata.getPhone())) {
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (listItemClick(CONTACT_NAME_ROW, reminderdata.getPhone())) {
//
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (fill(reminderdata.getMessage())) {
////                                send();
//                                                    if (clickById(SEND_BUTTON)) {
//
//                                                        Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);
//
//                                                        new Handler().postDelayed(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                if (clickById(CONVERSATION_BCAK_BUTTON)) {
////                                                            back2Home();
//                                                                    taskDone();
//                                                                }
//                                                            }
//                                                        }, 1000);
//                                                    }
//                                                }
//                                            }
//                                        }, 1000);
//
//                                    } else {
//                                        Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);
//                                        if (performGlobalAction(GLOBAL_ACTION_BACK)) {
//                                            new Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    performGlobalAction(GLOBAL_ACTION_BACK);
//                                                    new Handler().postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            taskDone();
//                                                        }
//                                                    }, 1000);
//                                                }
//                                            }, 1000);
//                                        }
//                                    }
//                                }
//                            }, 1000);
//                        }
//                    }
//                }
//            }, 1000);
//        }

        if (schedulerModel == null) {
            return;
        }

        String searchString = schedulerModel.getConversationName();

        if (schedulerModel.getMessageType().equals(SchedulerService.TYPE_IMAGE)) {
            if (searchString.contains(SYMBOL_TO_IDENTIFY_GROUP)) {
                searchString = searchString.replace(SYMBOL_TO_IDENTIFY_GROUP, "");
                searchAndSend(searchString);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickById(CONFIRMATION_DIALOG_BTN)) {
                            // TODO: Update Flag 2
                            if (schedulerModel != null) {
                                DbWhatsApp.UpdateByFlag(schedulerModel.getId(), DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (clickById(CONVERSATION_BCAK_BUTTON)) {
                                        taskDone();
                                    }
                                }
                            }, 1000);
                        } else {
                            pressSendButtonOnWA();
                        }
                    }
                }, 1000);
            }
        } else if (schedulerModel.getMessageType().equals(TYPE_TEXT)) {
            if (searchString.contains(SYMBOL_TO_IDENTIFY_GROUP)) {
                searchString = searchString.replace(SYMBOL_TO_IDENTIFY_GROUP, "");
                searchAndSend(searchString);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickById(CONFIRMATION_DIALOG_BTN)) {
                            if (Preferences.getPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false)) {
//--------------------- TODO : api call if number not found.-----------------------------------------------------------------------------
                                Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);
                                if (schedulerModel != null) {
                                    DbWhatsApp.UpdateByFlag(schedulerModel.getId(), DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, DBSqliteWhatsApp.WHATS_APP_UNREGISTERED_FLAG);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (clickById(CONVERSATION_BCAK_BUTTON)) {
                                            taskDone();
                                        } else {
                                            taskDone();
                                        }
                                    }
                                }, 1000);
                            }
                        } else {
                            pressSendButtonOnWA();
                        }
                    }
                }, 1000);
            }
        } else if (schedulerModel.getMessageType().equals(TYPE_PROFILE_PIC)) {
            pickProfilePicture();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickById(CONFIRMATION_DIALOG_BTN)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickById(CONVERSATION_BCAK_BUTTON)) {
                                    taskDone();
                                }
                            }
                        }, 1000);
                    } else {
                        pressSendButtonOnWA();
                    }
                }
            }, 1000);
        }
    }

    private void pressSendButtonOnWA() {
        if (Preferences.getPreferenceBoolean(this, Preferences.KEY_IN_EXECUTION, false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickById(SEND_BUTTON)) {
                        Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);
// TODO: ---------  Update flag -----------------------------------------
                        if (schedulerModel != null) {
                            DbWhatsApp.UpdateByFlag(schedulerModel.getId(), DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickById(CONVERSATION_BCAK_BUTTON)) {
//                                  back2Home();
                                    taskDone();
                                }
                            }
                        }, 1000);
                    }
                }
            }, 1000);
        }
    }

    private void searchAndSend(final String searchString) {
        if (Preferences.getPreferenceBoolean(this, Preferences.KEY_IN_EXECUTION, false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                if (clickById(CONFIRMATION_DIALOG_BTN)) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
                    if (clickById(SEARCH_BUTTON)) {
                        if (pasteTextToEditText(searchString)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listItemClick(CONTACT_NAME_ROW, searchString)) {
//                                        Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
                                                if (clickById(SEND_BUTTON)) {
//                                                    pressSendButtonOnWA();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (clickById(SEND_BUTTON)) {
// TODO: -----------------------------------------------------  Update flag ------------------------------------------------
                                                                if (schedulerModel != null) {
                                                                    DbWhatsApp.UpdateByFlag(schedulerModel.getId(), DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
                                                                }

                                                                Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IN_EXECUTION, false);

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (clickById(CONVERSATION_BCAK_BUTTON)) {
//                                                                          back2Home();
                                                                            taskDone();
                                                                        }
                                                                    }
                                                                }, 1000);
                                                            }
                                                        }
                                                    }, 1000);
                                                }
//                                            }
//                                        }, 700);
                                    }
                                }
                            }, 1000);
                        }
                    }
//                        }
//                    }, 1000);
//                }
                }
            }, 1000);
        }
    }

    private boolean pasteTextToEditText(String personName) {
        AccessibilityNodeInfo source = getRootInActiveWindow();

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId(SEARCH_EDITTEXT);

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
                if (child.getViewIdResourceName().equals(SEARCH_EDITTEXT)) {

                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                            AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                    arguments.putBoolean(AccessibilityNodeInfo
                                    .ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                            true);
                    child.performAction(AccessibilityNodeInfo
                                    .ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                            arguments);
                    child.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

                    ClipData clip = ClipData.newPlainText("label", personName);
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context
                            .CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(clip);

                    child.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                    return true;
                }
            }
        }
        return false;
    }

    private boolean clickById(String id) {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if (source == null)
            return false;

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId(id);

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
                if (child.getViewIdResourceName() != null) {
                    if (child.getViewIdResourceName().equals(id)) {
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean clickByText(String text) {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if (source == null)
            return false;

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText(text);

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
                if (child.getContentDescription() != null) {
                    if (child.getContentDescription().equals(text)) {
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean clickByIdAndText(String id, String text) {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if (source == null)
            return false;

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId(id);

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
                if (child.getViewIdResourceName() != null || child.getText() != null) {
                    if (child.getViewIdResourceName().equals(id) || child.getText().equals(text)) {
//                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        child.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean listItemClick(String id, String content) {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId(id);

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
//                Log.e(TAG,"Description : "+child.getText());
                if (child.getViewIdResourceName() != null && child.getText() != null) {
                    if (child.getViewIdResourceName().equals(id) && child.getText().toString().trim().equalsIgnoreCase(content)) {
//                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        child.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        child.getParent().performAction(AccessibilityNodeInfo.ACTION_SELECT);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean fill(String msg) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findEditTextAndPaste(rootNode, msg);
        }
        return false;
    }

    private boolean findEditTextAndPaste(AccessibilityNodeInfo rootNode, String content) {

        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);

            if (nodeInfo == null) {
                Log.e(TAG, "nodeInfo = null");
                continue;
            }

            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                Log.e(TAG, "EditText found and typing start");

                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                nodeInfo.performAction(AccessibilityNodeInfo
                                .ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

                ClipData clip = ClipData.newPlainText("label", content);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context
                        .CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);

                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                return true;
            }

            if (findEditTextAndPaste(nodeInfo, content)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private boolean isActivityToBeTracked(String activityInfo) {
        return activities.contains(activityInfo);
    }

    private void send() {

        AccessibilityNodeInfo source = getRootInActiveWindow();

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByViewId("com" +
                ".whatsapp:notificatioId/send");

        if (list != null && list.size() > 0) {
            for (AccessibilityNodeInfo child : list) {
                if (child.getViewIdResourceName().equals("com.whatsapp:notificatioId/send")) {
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.e(TAG, "AccessibilityNodeInfo.ACTION_CLICK : com.whatsapp:notificatioId/send");
                }
            }
        }

        back2Home();
    }

    private void back2Home() {

        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);

        this.stopSelf();
    }

    private void taskDone() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean backDone = performGlobalAction(GLOBAL_ACTION_BACK);
                Log.e(TAG, "backDone : " + backDone);
                if (backDone) {
                    Preferences.setPreferenceBoolean(SchedulerService.this, Preferences.KEY_IS_LOCK, false);
//                    AlarmReceiver.completeWakefulIntent(intent);
                    // TODO: Update Action Flag 2 for api work done
                    if (schedulerModel != null) {

//                        DbWhatsApp.UpdateByFlag(schedulerModel.getId(), DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
                        //TODO: Call Send Status Api
                        MainActivity obj = new MainActivity();
                        DeviceInfoDataset deviceInfo = obj.GetSharePreferenceDeviceInfo(context);
                        if (deviceInfo != null) {
                            //TODO: Call Send Status Api Registered
                            ArrayList<SchedulerModel> queue = DbWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_2);
                            for(int i = 0; i < queue.size(); i++) {
                                whatsappMsgStatusUpdateApi( deviceInfo.getAndroidDUID(),
                                                            queue.get(i).getMessageId(),
                                                            DBSqliteWhatsApp.WHATS_APP_REGISTERED_FLAG,
                                                            queue.get(i).getUpdateTime(),
                                                            queue.get(i).getId()
                                                           );
                            }
                            //TODO: Call Send Status Api Unregistered
                            queue = DbWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_UNREGISTERED_FLAG);
                            for(int i = 0; i < queue.size(); i++) {
                                whatsappMsgStatusUpdateApi( deviceInfo.getAndroidDUID(),
                                        queue.get(i).getMessageId(),
                                        DBSqliteWhatsApp.WHATS_APP_UNREGISTERED_FLAG,
                                        queue.get(i).getUpdateTime(),
                                        queue.get(i).getId()
                                );
                            }
                        }
                    }
                    ArrayList<SchedulerModel> queue = DbWhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG,DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0);
                    if (queue != null && queue.size() > 0) {
                        Intent intent = new Intent();
                        intent.setAction("paresh.reminder.REPEAT");
                        sendBroadcast(intent);
                    }
                }
            }
        }, 1000);

    }

    private void openWhatsAppForText(String conversationNumber, String msg) {
        String message = msg;
        if (conversationNumber.contains("91")) {
            conversationNumber = conversationNumber.replace("+", "").replace(" ", "");
        }

//        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        Intent sendIntent = new Intent("android.intent.action.SEND");
        Intent sendIntent = new Intent("android.intent.action.VIEW", Uri.parse("whatsapp://send/?text=" + message + "&phone=" + conversationNumber));
//        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker"));
//        sendIntent.putExtra("jid", conversationNumber + "@s.whatsapp.net");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
//        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        sendIntent.setPackage("com.whatsapp");
//        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void openWhatsAppForGroupText(String toNumber, String msg) {

        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker"));
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
//                            Intent sendIntent = new Intent("android.intent.action.VIEW", Uri.parse("whatsapp://send/?text=" + msg + "&phone=" + conversationNumber));
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        sendIntent.putExtra(Intent.EXTRA_STREAM, "");
//        sendIntent.setType(null);
        startActivity(sendIntent);
    }

    private void openWhatsAppForImage(final String conversationNumber, final String msg, String url, final String fileName) {

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

//        final String dirPath = Utils.getRootDirPath(getApplicationContext());

        String folder_main = "/SinglaGroups/WhatsApp";//"PareshTest";

        File f = new File(Environment.getExternalStorageDirectory() + folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        final String dirPath = f.getAbsolutePath();

        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
//                        Uri uri = Uri.fromFile(new File(dirPath + fileName));

                        Uri uri = FileProvider.getUriForFile(
                                getApplicationContext(),
                                getApplicationContext()
                                        .getPackageName() + ".fileprovider", new File(dirPath + "/" + fileName));

                        try {
//                            Intent intent = new Intent("android.intent.action.MAIN");
//                            intent.setAction(Intent.ACTION_SEND);
//                            intent.setType("text/plain");
//                            intent.putExtra(Intent.EXTRA_STREAM, uri);
//                            intent.putExtra("jid", conversationNumber + "@s.whatsapp.net"); //phone number without "+" prefix!
//                            intent.setPackage("com.whatsapp");
//                            startActivity(intent);
//                            Intent sendIntent = new Intent("android.intent.action.MAIN");
////                        Uri uri = FileProvider.getUriForFile(SchedulerService.this, "labalabiimage.whatsapp.fileprovider", new File(msg2));
////                            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker"));
//                            sendIntent.setType("image/jpg");
//                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            sendIntent.putExtra("jid", conversationNumber + "@s.whatsapp.net");
//                            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
//                            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(sendIntent);

                            Intent sendIntent = new Intent("android.intent.action.SEND");
                            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker"));
                            sendIntent.setType("image");
                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(conversationNumber) + "@s.whatsapp.net");
                            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(sendIntent);

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "Whatsapp is not installed.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    private void openWhatsAppForMedia(final String conversationNumber, final String msg, final String url, final String fileName) {
        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        String folder_main = "/SinglaGroups/WhatsApp";//"PareshTest";

        File f = new File(Environment.getExternalStorageDirectory() + folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        final String dirPath = f.getAbsolutePath();

        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(dirPath + "/" + fileName));
                        } else {
                            uri = Uri.fromFile(new File(dirPath + "/" + fileName));
                        }
                        Log.e(TAG,"Uri:"+uri);
                        try {
                            String toNumber = conversationNumber;
                            if (conversationNumber.contains(SYMBOL_TO_IDENTIFY_GROUP)) {
                                toNumber = toNumber.replace(SYMBOL_TO_IDENTIFY_GROUP, "");
                            }

                            Intent sendIntent = new Intent("android.intent.action.SEND");
                            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker"));
                            sendIntent.setPackage("com.whatsapp");
                            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                            if (!fileName.contains(".txt")) {
                                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                                sendIntent.setType("text/plain");
                            }
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            sendIntent.setType(getMimeType(url));
                            startActivity(sendIntent);

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "Whatsapp is not installed.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e(TAG, "Error: " + error.toString());
                    }
                });
    }

    void pickProfilePicture() {
        if (isLock) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickById(TOOLBAR_NAME)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickById(PROFILE_IMAGE)) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (clickByText(SHARE_BUTTON)) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (clickByIdAndText(SAVE_TO_BUTTON, SAVE_TO_TEXT)) {
                                                            isLock = false;
                                                            boolean backDone = performGlobalAction(GLOBAL_ACTION_BACK);
                                                            if (backDone) {
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        boolean backDone = performGlobalAction(GLOBAL_ACTION_BACK);
                                                                        if (backDone) {
                                                                            new Handler().postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    boolean backDone = performGlobalAction(GLOBAL_ACTION_BACK);
                                                                                    if (backDone) {
                                                                                        new Handler().postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                boolean backDone = performGlobalAction(GLOBAL_ACTION_BACK);
                                                                                                if (backDone) {

                                                                                                    String profilePicPath = getProfilePicPath();
                                                                                                    Log.e("SchedulerService", "Profile Picture Path: " + profilePicPath);

                                                                                                    taskDone();
                                                                                                }
                                                                                            }
                                                                                        }, 1000);
                                                                                    }
                                                                                }
                                                                            }, 1000);
                                                                        }
                                                                    }
                                                                }, 1000);
                                                            }
                                                        }
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }, 1000);
                                }
                            }
                        }, 1000);
                    }
                }
            }, 1000);
        }
    }

    private void whatsappMsgStatusUpdateApi(String DeviceID, String MsgID,final int Status, String StatusDateTime,final String Id) {
        //Log.e(TAG,"Send Api Called:DeviceID:"+DeviceID+"\t MsgID:"+MsgID);
        if (Global.isNetworkAvailable(context)) {

            Call<ListResponse<String>> listResponseCall = api2.whatsappMsgStatusUpdateApi(DeviceID,MsgID,""+Status,StatusDateTime);

            listResponseCall.enqueue(new Callback<ListResponse<String>>() {
                @Override
                public void onResponse(Call<ListResponse<String>> call, Response<ListResponse<String>> response) {
                    Log.e("Scheduler Service", "onResponse:"+response);
                    // TODO: Delete row behalf of Action Flag 2 for registered and 5 for unregistered
                    DbWhatsApp.DeleteByFlag(Id, DBSqliteWhatsApp.WHATS_APP_OUTGOING_FLAG, Status);
                }
                @Override
                public void onFailure(Call<ListResponse<String>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("Scheduler Service", "onFailure"+t.toString());
                }
            });
        }
    }
}