package truecaller;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by rakes on 02-Dec-17.
 */

public class CallRecordReceiver  extends BroadcastReceiver {
    private static final String TAG = CallRecordReceiver.class.getSimpleName();
    // This String will hold the incoming phone number
    MediaRecorder recorder;
    TelephonyManager telManager;
    boolean recordStarted;
    private Context ctx;
    static boolean status = false;
    Time today;
    String phoneNumber;
    byte[] incrept,decrpt;
    String selected_song_name;
    @Override
    public void onReceive(final Context context, Intent intent) {

        this.ctx = context;
        String action = intent.getAction();
        Log.i(TAG, "action:->>>" + action);
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        if (status == false) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // OFFHOOK
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                Log.w("DEBUG", "aa" + state);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    try {
//                        recorder = new MediaRecorder();
//                        recorder.reset();
//                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                        String date = today.monthDay + "_" + (today.month + 1) + "_"+ today.year;
//                        String time = today.format("%k_%M_%S");
//                        File file = createDirIfNotExists(phoneNumber+"_"+date + "_" + time);
//                        recorder.setOutputFile(file.getAbsolutePath());
//                        recorder.prepare();
//                        recorder.start();
//                        recordStarted = true;
//                        status = true;
//                    } catch (Exception ex) {
//                        Log.e(TAG, "On Receive Exception:" + ex.toString());
//                    }
//                    Log.i("number >>>>>>>>>>>>", "Ringing : " + phoneNumber);
//                    incoming(phoneNumber);
//                    incomingcallrecord(action, context);
                    answerPhoneHeadsethook(context, intent);
                    return;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

//                    phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                    Log.i("number >>>>>>>>>>>>>>", "Offhook:" + this.getResultData());
//                    incomingcallrecord(action, context);
                    Log.d(TAG, "CALL ANSWERED NOW!!");
                    phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    try {
                        recorder = new MediaRecorder();
                        recorder.reset();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        String date = today.monthDay + "_" + (today.month + 1) + "_"+ today.year;
                        String time = today.format("%k_%M_%S");
                        File file = createDirIfNotExists(phoneNumber+"_"+date + "_" + time);
                        recorder.setOutputFile(file.getAbsolutePath());
                        recorder.prepare();
                        recorder.start();
                        recordStarted = true;
                        status = true;
                    } catch (Exception ex) {
                        Log.e(TAG, "On Receive Exception:" + ex.toString());
                    }
                    Log.i("number >>>>>>>>>>>>", "Ringing : " + phoneNumber);
                    incoming(phoneNumber);
                    incomingcallrecord(action, context);
//                    try {
//                        synchronized(this) {
//                            Log.d(TAG, "Waiting for 10 sec ");
//                            this.wait(10000);
//                        }
//                    }
//                    catch(Exception e) {
//                        Log.e(TAG, "Exception while waiting !!"+e.toString());
//                    }
                    //disconnectPhoneItelephony(context);
                    return;
                }else {
                    Log.d(TAG, "ALL DONE ...... !!");
                }
            }
        } else {
            status = false;
        }
    }
    private void incoming(String number) {
//        DataHelper db1 = new DataHelper(ctx);
//        db1.open();
//        String date = today.monthDay + "_" + (today.month + 1) + "_"+ today.year;
//        String time = today.format("%k_%M_%S");
//        db1.insertUserProfile(number, " ", today.monthDay + "-"+ (today.month + 1) + "-" + today.year,today.format("%k:%M:%S"), date + "_" + time);
//        db1.close();
    }
    private void incomingcallrecord(String action, Context context) {
        // TODO: Read Phone State
        if (action.equals("android.intent.action.PHONE_STATE")) {
            telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d("calling number", "calling number" + incomingNumber);
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        Log.e("CALL_STATE_RINGING", "CALL_STATE_RINGING");
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        Log.e("CALL_STATE_OFFHOOK", "CALL_STATE_OFFHOOK");
//                             try {
//                                 recorder.reset();
//                                 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                                 recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                                 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                                 String date=today.monthDay + "_" + (today.month + 1) +"_" + today.year; String time=today.format("%k_%M_%S");
//                                 File file=createDirIfNotExists(incomingNumber+"_"+date+"_"+time);
//                                 recorder.setOutputFile(file.getAbsolutePath());
//                                 recorder.prepare();
//                                 recorder.start();
//                                 recordStarted = true;
//                             } catch(Exception ex) {
//                                 Log.e(TAG, "Call Offhook Exception:"+ex.toString());
//                             }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        Log.e("CALL_STATE_IDLE", "CALL_STATE_IDLE");
                        if (recordStarted) {
                            recorder.stop();
                            recorder.reset();
                            recorder.release();
                            recorder = null;
                            recordStarted = false;
                            //encriptCurrentRecordedFile();
                                  /*

                                   * PhoneRecieverBroadcastActivity.db.open();

                                   * doSomething(phoneNumber);

                                   * PhoneRecieverBroadcastActivity.db.close();

                                   */
                        }
                        break;
                    }
                    default: break;
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception:"+ex.toString());
            }
        }
        private void encriptCurrentRecordedFile() {
//            SimpleCrypto simpleCrypto = new SimpleCrypto();
//            try {
//                incrept = simpleCrypto.encrypt("abc", getAudioFileFromSdcard());
//                FileOutputStream fos = new FileOutputStream(new File("/sdcard/PhoneCallRecording/" + selected_song_name+ ".3GPP"));
//                fos.write(incrept);
//                fos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        private byte[] getAudioFileFromSdcard() throws FileNotFoundException {
            byte[] inarry = null;
            try {
                File sdcard = new File(Environment.getExternalStorageDirectory()+ "/SinglaGroups/PhoneCallRecording");
                File file = new File(sdcard, selected_song_name + ".3GPP");
                FileInputStream fileInputStream = null;
                byte[] bFile = new byte[(int) file.length()];
                // convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();
                inarry = bFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inarry;
        }
    };
    public File createDirIfNotExists(String path) {

        selected_song_name = path;
        File folder = new File(Environment.getExternalStorageDirectory()+ "/SinglaGroups/PhoneCallRecording");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.d(TAG, "folder is created");
            }
        }
        File file = new File(folder, path + ".3GPP");
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, "file is created");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception:"+e.toString());
        }
        return file;
    }
    public void answerPhoneHeadsethook(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "Incoming call from: " + number);
            Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
            buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
            try {
                context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
                Log.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
            }
            catch (Exception e) {
                Log.d(TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
            }
            Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
            headSetUnPluggedintent.putExtra("name", "Headset");
            // TODO: Should we require a permission?
            try {
                context.sendOrderedBroadcast(headSetUnPluggedintent, null);
                Log.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                Log.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
                Log.d(TAG, "Call Answered From Catch Block !!");
            }
            Log.d(TAG, "Answered incoming call from: " + number);
        }
        Log.d(TAG, "Call Answered using headsethook");
    }
    public static void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        Log.v(TAG, "Now disconnecting using ITelephony....");
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            //telephonyService.silenceRinger();
            Log.v(TAG, "Disconnecting Call now...");
            //telephonyService.answerRingingCall();
            //telephonyService.endcall();
            Log.v(TAG, "Call disconnected...");
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
        }
    }
}