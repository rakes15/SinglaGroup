package todo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.singlagroup.R;

import java.util.ArrayList;

import DatabaseController.CommanStatic;

public class CreateToDoActivity extends AppCompatActivity {
//    private static final String TAG = CreateToDoActivity.class.getSimpleName();;
//    private Context context;
//    private ActionBar actionBar;
//    private TextView txtTitle,txtAddMyDay,txtRemindMe,txtRemindMeSubTitle,txtDueDate,txtRepeat;
//    private TextView txtAddNote,txtAddNoteTime,txtToDoCreateTime;
//    private ImageView imgMyDay,imgRemindMe,imgDueDate,imgRepeat,imgDelete;
//    private ImageView imgRemindMeClose;
//    private LinearLayout lLayoutRepeat;
//    private TaskDbHelper mHelper;
//    private ListView mTaskListView;
//    private ArrayAdapter<String> mAdapter;
//    private static final String TAG = "Sample";
//    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
//    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";
//    private SwitchDateTimeDialogFragment dateTimeFragment;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.dialog_todo_task);
//        Initialization();
//        ClickEvents();
//        CalenderView();
//    }
//    private void Initialization(){
//        this.context = CreateToDoActivity.this;
//        this.actionBar = getSupportActionBar();
//        this.actionBar.setDisplayHomeAsUpEnabled(true);
//        //TODO : Text View
//        this.txtTitle = (TextView) findViewById(R.id.text_Title);
//        this.txtAddMyDay = (TextView) findViewById(R.id.txt_add_to_my_day);
//        this.txtRemindMe = (TextView) findViewById(R.id.txt_remind_me);
//        this.txtRemindMeSubTitle = (TextView) findViewById(R.id.txt_remind_me_sub_title);
//        this.txtDueDate = (TextView) findViewById(R.id.txt_due_date);
//        this.txtRepeat = (TextView) findViewById(R.id.txt_repeat);
//        this.txtAddNote = (TextView) findViewById(R.id.txt_add_note);
//        this.txtAddNoteTime = (TextView) findViewById(R.id.txt_add_note_time);
//        this.txtToDoCreateTime = (TextView) findViewById(R.id.txt_to_do_create_time);
//        //TODO : Image View
//        this.imgMyDay = (ImageView) findViewById(R.id.image_my_day_close);
//        this.imgRemindMe = (ImageView) findViewById(R.id.image_remind_me);
//        this.imgRemindMeClose = (ImageView) findViewById(R.id.image_remind_me_close);
//        this.imgDueDate = (ImageView) findViewById(R.id.image_due_date_close);
//        this.imgRepeat = (ImageView) findViewById(R.id.image_repeat_close);
//        //TODO : Linear Layout
//        this.lLayoutRepeat = (LinearLayout) findViewById(R.id.linear_repeat);
//
//    }
//    private void ClickEvents(){
//        //TODO: Add to My Day
//        this.txtAddMyDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgMyDay.getVisibility() == View.VISIBLE){
//                    imgMyDay.setVisibility(View.GONE);
//                    txtAddMyDay.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtAddMyDay.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_wb_sunny, 0, 0, 0);
//                }else{
//                    imgMyDay.setVisibility(View.VISIBLE);
//                    txtAddMyDay.setTextColor(context.getResources().getColor(R.color.attachment_icon_pdf));
//                    txtAddMyDay.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_wb_sunny_blue, 0, 0, 0);
//                }
//            }
//        });
//        this.imgMyDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgMyDay.getVisibility() == View.VISIBLE) {
//                    imgMyDay.setVisibility(View.GONE);
//                    txtAddMyDay.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtAddMyDay.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_wb_sunny, 0, 0, 0);
//                }
//            }
//        });
//        //TODO: Remind Me
//        this.txtRemindMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Re-init each time
//                dateTimeFragment.startAtCalendarView();
//                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
//                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
//
////                if(imgRemindMeClose.getVisibility() == View.VISIBLE){
////                    imgRemindMeClose.setVisibility(View.GONE);
////                    txtRemindMe.setTextColor(context.getResources().getColor(R.color.TextViewContent));
////                    imgRemindMe.setBackground(context.getResources().getDrawable(R.drawable.ic_alarm_grey));
////                }else{
////                    imgRemindMeClose.setVisibility(View.VISIBLE);
////                    txtRemindMe.setTextColor(context.getResources().getColor(R.color.attachment_icon_pdf));
////                    imgRemindMe.setBackground(context.getResources().getDrawable(R.drawable.ic_alarm_blue));
////                }
//            }
//        });
//        this.imgRemindMeClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgRemindMeClose.getVisibility() == View.VISIBLE){
//                    imgRemindMeClose.setVisibility(View.GONE);
//                    txtRemindMe.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    imgRemindMe.setBackground(context.getResources().getDrawable(R.drawable.ic_alarm_grey));
//                }
//            }
//        });
//        //TODO: Due date
//        this.txtDueDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgDueDate.getVisibility() == View.VISIBLE){
//                    imgDueDate.setVisibility(View.GONE);
//                    txtDueDate.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtDueDate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_calander_grey, 0, 0, 0);
//                    //TODO: Repeat Gone
//                    lLayoutRepeat.setVisibility(View.GONE);
//                }else{
//                    imgDueDate.setVisibility(View.VISIBLE);
//                    txtDueDate.setTextColor(context.getResources().getColor(R.color.attachment_icon_pdf));
//                    txtDueDate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_calander_blue, 0, 0, 0);
//                    //TODO: Repeat Visible
//                    lLayoutRepeat.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        this.imgDueDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgDueDate.getVisibility() == View.VISIBLE) {
//                    imgDueDate.setVisibility(View.GONE);
//                    txtDueDate.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtDueDate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_calander_grey, 0, 0, 0);
//                    //TODO: Repeat Gone
//                    lLayoutRepeat.setVisibility(View.GONE);
//                }
//            }
//        });
//        //TODO: Repeat
//        this.txtRepeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgRepeat.getVisibility() == View.VISIBLE){
//                    imgRepeat.setVisibility(View.GONE);
//                    txtRepeat.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtRepeat.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_repeat, 0, 0, 0);
//                }else{
//                    imgRepeat.setVisibility(View.VISIBLE);
//                    txtRepeat.setTextColor(context.getResources().getColor(R.color.attachment_icon_pdf));
//                    txtRepeat.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_repeat_blue, 0, 0, 0);
//                }
//            }
//        });
//        this.imgRepeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(imgRepeat.getVisibility() == View.VISIBLE) {
//                    imgRepeat.setVisibility(View.GONE);
//                    txtRepeat.setTextColor(context.getResources().getColor(R.color.TextViewContent));
//                    txtRepeat.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_repeat, 0, 0, 0);
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        //MenuItem menuItem = menu.findItem(R.id.action_attachment);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK ) {
//            //do your stuff
//            //Utils.setBadgeCount(NotificationActivity.this, PartyInfoActivity.iconNotification, CommanStatic.notificationCount);
//            finish();
//        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
//            Log.d("HomeKey","Home key pressed then restart app");
//            finishAffinity();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void CalenderView(){
//        // Construct SwitchDateTimePicker
//        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
//        if(dateTimeFragment == null) {
//            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
//                    getString(R.string.label_datetime_dialog),
//                    getString(android.R.string.ok),
//                    getString(android.R.string.cancel),
//                    getString(R.string.clean) // Optional
//            );
//        }
//
//        // Init format
//        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
//        // Assign unmodifiable values
//        dateTimeFragment.set24HoursMode(false);
//        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
//        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
//
//        // Define new day and month format
//        try {
//            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
//        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
//            Log.e(TAG, ""+e.toString());
//        }
//
//        // Set listener for date
//        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
//        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
//            @Override
//            public void onPositiveButtonClick(Date date) {
//                textView.setText(myDateFormat.format(date));
//            }
//
//            @Override
//            public void onNegativeButtonClick(Date date) {
//                // Do nothing
//            }
//
//            @Override
//            public void onNeutralButtonClick(Date date) {
//                // Optional if neutral button does'nt exists
//                textView.setText("");
//            }
//        });
//    }
}
