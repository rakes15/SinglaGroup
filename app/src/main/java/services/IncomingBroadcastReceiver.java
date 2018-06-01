package services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


import java.lang.reflect.Method;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

public class IncomingBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = IncomingBroadcastReceiver.class.getSimpleName();
    // This String will hold the incoming phone number
    private String number,stateString;
    CustomDialog dialog;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;
    Context context;
    @Override
    public void onReceive(final Context context, Intent intent) {

        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;

            // Else, try to do some action
        else {
            this.context = context;
            // Fetch the number of incoming call
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            stateString = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(dialog == null){
//                dialog = new CustomDialog(context);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                dialog.show();
            }
            listener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    stateString = "N/A";
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            stateString = "Idle";
                            dialog.dismiss();
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            stateString = "Off Hook";
                            dialog.dismiss();
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            stateString = "Ringing";
                            dialog.show();
                            break;
                    }
                    //Toast.makeText(context, "no."+number,Toast.LENGTH_LONG).show();
                }
            };

            // Register the listener with the telephony manager
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

            // Check, whether this is a member of "Black listed" phone numbers
            // stored in the database
			/*if (MainActivity.blockList.contains(new Blacklist(number))) {
				// If yes, invoke the method
				disconnectPhoneItelephony(context);
				return;
			}*/
        }
    }

    class CustomDialog extends Dialog{
        private Context dContext;
        private ImageButton ib_close;
        private TextView txtViewTitle,txtViewSubTitle;
        public CustomDialog(Context context) {
            super(context);
            this.dContext = context;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_item);
            Initialization();
            String mobile = "";
            txtViewTitle.setText("Mobile No. "+number);
            txtViewSubTitle.setText("State "+stateString);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                mobile = (telephonyManager.getLine1Number() == null) ? "" : telephonyManager.getLine1Number();
//            }else{
//                //mobile = telephonyManager.
//            }
        }
        private void Initialization(){
            ib_close = (ImageButton) findViewById(R.id.Image_Button_Close);
            txtViewTitle = (TextView) findViewById(R.id.text_Title);
            txtViewSubTitle = (TextView) findViewById(R.id.textView_subTitle);
            ib_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialog dialog = new CustomDialog(dContext);
                    dialog.dismiss();
                }
            });
        }
    }
}