package whatsapp.autoresponder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.master.permissionhelper.PermissionHelper;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import whatsapp.autoresponder.Utils.AccessibillityManagerC;

/**
 * Created by iqor on 2/26/2018.
 */

public class WhatsAppAutoResponderActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Button btnOnOffService;
    private Button btnOnOffAccess;
    private Switch btnSwitchService,btnSwitchAccess;
    private PermissionHelper permissionHelper;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_whats_app);
        Initialization();
        ModulePermission();
        ServiceCondition();
    }
    private void Initialization(){
        this.context = WhatsAppAutoResponderActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        btnOnOffService = findViewById(R.id.btnOnOffService);
        btnOnOffAccess = findViewById(R.id.btnOnOffAccess);
        btnSwitchService = findViewById(R.id.switch_btn_notif_service);
        btnSwitchAccess = findViewById(R.id.switch_btn_access);
    }
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            StaticValues.Vtype = bundle.getInt("Vtype");
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                contactPermission();
                ClickEvents();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void ClickEvents(){
        btnSwitchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    // The toggle is enabled
                    notificationPermission();
                } else {
                    // The toggle is disabled
                    btnSwitchService.setChecked(false);
                }
            }
        });
        btnSwitchAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    // The toggle is enabled
                    accessibilityServicePermission();
                } else {
                    // The toggle is disabled
                    btnSwitchAccess.setChecked(false);
                }
            }
        });
        btnOnOffService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationPermission();
            }
        });

        btnOnOffAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessibilityServicePermission();
            }
        });

    }
    private void ServiceCondition(){
        //TODO: Notification Service
        if (Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName())){
            //service is enabled do something
            btnSwitchService.setChecked(true);
        } else {
            //service is not enabled try to enabled by calling...
            btnSwitchService.setChecked(false);
        }
        //TODO: Accessbility Service
        if (AccessibillityManagerC.isAccessibilitySettingsOn(context)){
            //service is enabled do something
            btnSwitchAccess.setChecked(true);
        }else{
            //service is not enabled try to enabled by calling...
            btnSwitchAccess.setChecked(false);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        ServiceCondition();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ServiceCondition();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode== KeyEvent.KEYCODE_BACK){

            //TODO: Activity finish
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void notificationPermission() {
        showAlertBox("Read Notification Permission Required", "App wont work without this permission")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        else
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                dialog.dismiss();
            }
        }).show().setCanceledOnTouchOutside(false);
    }

    private void accessibilityServicePermission() {
        new AlertDialog.Builder(this).setTitle("Open the accessibility service")
                .setMessage("Make sure that the " + getString(R.string.app_name) + " service is turned on, \nTap TO OPEN the \"" + getString(R.string.app_name) + "\" service within accessibility")
                .setCancelable(false)
                .setPositiveButton("To open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private AlertDialog.Builder showAlertBox(String title, String message) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
    }

    private void contactPermission() {
        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }

            @Override
            public void onPermissionDeniedBySystem() {
                permissionHelper.openAppDetailsActivity();
            }
        });
    }
}
