package whatsapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;

import whatsapp.autoresponder.Model.SchedulerModel;

public class DBSqliteWhatsApp extends SQLiteOpenHelper {
    private static final String TAG = DBSqliteWhatsApp.class.getSimpleName();
    public static final int WHATS_APP_INCOMING_FLAG = 1;
    public static final int WHATS_APP_OUTGOING_FLAG = 2;
    public static final int WHATS_APP_SEND_FLAG_0 = 0;
    public static final int WHATS_APP_SEND_FLAG_1 = 1;
    public static final int WHATS_APP_SEND_FLAG_2 = 2;
    public static final int WHATS_APP_REGISTERED_FLAG = 2;
    public static final int WHATS_APP_UNREGISTERED_FLAG = 3;
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "whatsAppDB0";
    //TODo: Table Name
    private static final String WHATS_APP_TABLE = "whatsAppTbl";
    //TODO: Filter  Table Column's Name
    public static final String WHATS_APP_KEY_ID = "id";
    private static final String WHATS_APP_CONVER_NAME= "converName";
    private static final String WHATS_APP_PHONE_NUMBER = "phoneNumber";
    private static final String WHATS_APP_IS_GROUP = "isGroup";
    private static final String WHATS_APP_MESSAGE_ID = "msgId";
    private static final String WHATS_APP_MESSAGE_TYPE = "msgType";
    private static final String WHATS_APP_ACITIVITY_TYPE = "activityType";
    private static final String WHATS_APP_TEXT = "txt";
    private static final String WHATS_APP_FILE_NAME = "fileName";
    private static final String WHATS_APP_FILE_URL = "fileUrl";
    private static final String WHATS_APP_DEVICE_ID = "deviceId";
    private static final String WHATS_APP_TIME = "time";
    private static final String WHATS_APP_TYPE = "type";
    private static final String WHATS_APP_SEND_ACTION_FLG = "sendActionFlg";
    private static final String WHATS_APP_ENTRY_DATE_TIME = "entryDateTime";
    
    //TODO:	Constructor
    public DBSqliteWhatsApp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Party Info Category table create query
        String CREATE_WHATS_APP_TABLE = "CREATE TABLE " + WHATS_APP_TABLE + "(" + WHATS_APP_KEY_ID + " INTEGER PRIMARY KEY," + WHATS_APP_CONVER_NAME + " TEXT," + WHATS_APP_PHONE_NUMBER + " TEXT," + WHATS_APP_IS_GROUP + " INTEGER," + WHATS_APP_MESSAGE_ID + " TEXT," + WHATS_APP_MESSAGE_TYPE + " INTEGER," + WHATS_APP_ACITIVITY_TYPE + " INTEGER," + WHATS_APP_TEXT + " INTEGER," + WHATS_APP_FILE_NAME + " TEXT,"+ WHATS_APP_FILE_URL + " TEXT,"+ WHATS_APP_DEVICE_ID + " TEXT,"+ WHATS_APP_TIME + " TEXT,"+ WHATS_APP_TYPE + " INTEGER,"+ WHATS_APP_SEND_ACTION_FLG + " INTEGER,"+ WHATS_APP_ENTRY_DATE_TIME + " TEXT) ";
        db.execSQL(CREATE_WHATS_APP_TABLE);
    }

    //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + WHATS_APP_TABLE);
        // Create tables again
        onCreate(db);
    }
    //TODO: Whats Table Delete
    public void DeleteWhatsAppTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WHATS_APP_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of Filter Table
    public void insertWhatsAppTable(SchedulerModel schedulerModel,int iType) {
        //System.out.println("Filter:"+list.toString());
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,WHATS_APP_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int ConverName = ih.getColumnIndex(WHATS_APP_CONVER_NAME);
        final int PhoneNo = ih.getColumnIndex(WHATS_APP_PHONE_NUMBER);
        final int IsGroup = ih.getColumnIndex(WHATS_APP_IS_GROUP);
        final int MessageID = ih.getColumnIndex(WHATS_APP_MESSAGE_ID);
        final int MessageType = ih.getColumnIndex(WHATS_APP_MESSAGE_TYPE);
        final int ActivityType = ih.getColumnIndex(WHATS_APP_ACITIVITY_TYPE);
        final int Text = ih.getColumnIndex(WHATS_APP_TEXT);
        final int FileName = ih.getColumnIndex(WHATS_APP_FILE_NAME);
        final int FileUrl = ih.getColumnIndex(WHATS_APP_FILE_URL);
        final int DeviceID = ih.getColumnIndex(WHATS_APP_DEVICE_ID);
        final int Time = ih.getColumnIndex(WHATS_APP_TIME);
        final int Type = ih.getColumnIndex(WHATS_APP_TYPE);
        final int SendFlag = ih.getColumnIndex(WHATS_APP_SEND_ACTION_FLG);
        final int EntryDateTime = ih.getColumnIndex(WHATS_APP_ENTRY_DATE_TIME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            //for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(ConverName, schedulerModel.getConversationName() == null ? "" : schedulerModel.getConversationName());
                ih.bind(PhoneNo, schedulerModel.getPhnNumber() == null ? "" : schedulerModel.getPhnNumber());
                ih.bind(IsGroup, schedulerModel.getIsFromGroup() == null ? "" : schedulerModel.getIsFromGroup());
                ih.bind(MessageID, schedulerModel.getMessageId() == null ? "" : schedulerModel.getMessageId());
                ih.bind(MessageType, schedulerModel.getMessageType() == null ? "" : schedulerModel.getMessageType());
                ih.bind(ActivityType, schedulerModel.getActivityType() == null ? "" : schedulerModel.getActivityType());
                ih.bind(Text, schedulerModel.getText() == null ? "" : schedulerModel.getText());
                ih.bind(FileName, schedulerModel.getFileName() == null ? "" : schedulerModel.getFileName());
                ih.bind(FileUrl, schedulerModel.getFileUrl() == null ? "" : schedulerModel.getFileUrl());
                ih.bind(DeviceID, schedulerModel.getDeviceId() == null ? "" : schedulerModel.getDeviceId());
                ih.bind(Time, schedulerModel.getTime() == null ? "" : schedulerModel.getTime());
                ih.bind(Type, iType);
                ih.bind(SendFlag, 0);
                ih.bind(EntryDateTime, DateFormatsMethods.getDateTime());
                // Insert the row into the database.
                ih.execute();
            //}
        }
        finally {
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
            ih.close();  // See comment below from Stefan Anca
            final long endtime = System.currentTimeMillis();
            Log.e("Time:", "" + String.valueOf(endtime - startTime));
        }
    }
    
    public ArrayList<SchedulerModel> GetInOutDataByType(int iType){
        // iType 0 is incoming and 1 is sending
        ArrayList<SchedulerModel> schedulerModelList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * from " + WHATS_APP_TABLE+" WHERE "+WHATS_APP_TYPE+"="+iType+" Order By "+WHATS_APP_ENTRY_DATE_TIME+" DESC";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                SchedulerModel schedulerModel = new SchedulerModel();
                schedulerModel.setConversationName(cursor.getString(cursor.getColumnIndex(WHATS_APP_CONVER_NAME)));
                schedulerModel.setPhnNumber(cursor.getString(cursor.getColumnIndex(WHATS_APP_PHONE_NUMBER)));
                schedulerModel.setIsFromGroup(cursor.getString(cursor.getColumnIndex(WHATS_APP_IS_GROUP)));
                schedulerModel.setMessageId(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_ID)));
                schedulerModel.setMessageType(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_TYPE)));
                schedulerModel.setActivityType(cursor.getString(cursor.getColumnIndex(WHATS_APP_ACITIVITY_TYPE)));
                schedulerModel.setText(cursor.getString(cursor.getColumnIndex(WHATS_APP_TEXT)));
                schedulerModel.setFileName(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_NAME)));
                schedulerModel.setFileUrl(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_URL)));
                schedulerModel.setDeviceId(cursor.getString(cursor.getColumnIndex(WHATS_APP_DEVICE_ID)));
                schedulerModel.setTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_TIME)));
                schedulerModel.setId(cursor.getString(cursor.getColumnIndex(WHATS_APP_KEY_ID)));
                schedulerModel.setUpdateTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_ENTRY_DATE_TIME)));
                schedulerModel.setFlag(cursor.getString(cursor.getColumnIndex(WHATS_APP_SEND_ACTION_FLG)));
                schedulerModelList.add(schedulerModel);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return schedulerModelList;
    }

    public ArrayList<SchedulerModel> GetInOutDataByTypeWithFlag(int iType,int flag){
        // iType 0 is incoming and 1 is sending
        ArrayList<SchedulerModel> schedulerModelList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * from " + WHATS_APP_TABLE+" WHERE "+WHATS_APP_TYPE+"="+iType+" AND "+WHATS_APP_SEND_ACTION_FLG+"="+flag+"  Order By "+WHATS_APP_TIME+" ASC";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                SchedulerModel schedulerModel = new SchedulerModel();
                schedulerModel.setConversationName(cursor.getString(cursor.getColumnIndex(WHATS_APP_CONVER_NAME)));
                schedulerModel.setPhnNumber(cursor.getString(cursor.getColumnIndex(WHATS_APP_PHONE_NUMBER)));
                schedulerModel.setIsFromGroup(cursor.getString(cursor.getColumnIndex(WHATS_APP_IS_GROUP)));
                schedulerModel.setMessageId(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_ID)));
                schedulerModel.setMessageType(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_TYPE)));
                schedulerModel.setActivityType(cursor.getString(cursor.getColumnIndex(WHATS_APP_ACITIVITY_TYPE)));
                schedulerModel.setText(cursor.getString(cursor.getColumnIndex(WHATS_APP_TEXT)));
                schedulerModel.setFileName(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_NAME)));
                schedulerModel.setFileUrl(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_URL)));
                schedulerModel.setDeviceId(cursor.getString(cursor.getColumnIndex(WHATS_APP_DEVICE_ID)));
                schedulerModel.setTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_TIME)));
                schedulerModel.setId(cursor.getString(cursor.getColumnIndex(WHATS_APP_KEY_ID)));
                schedulerModel.setUpdateTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_ENTRY_DATE_TIME)));
                schedulerModel.setFlag(cursor.getString(cursor.getColumnIndex(WHATS_APP_SEND_ACTION_FLG)));
                schedulerModelList.add(schedulerModel);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return schedulerModelList;
    }

    public SchedulerModel GetCurrentModel(String ID,int iType,int flag){
        // iType 0 is incoming and 1 is sending
        SchedulerModel schedulerModel = new SchedulerModel();
        // Select All Query
        String selectQuery = "SELECT * from " + WHATS_APP_TABLE+" WHERE "+WHATS_APP_KEY_ID+"="+ID+" AND "+WHATS_APP_TYPE+"="+iType+" AND "+WHATS_APP_SEND_ACTION_FLG+"="+flag+"  Order By "+WHATS_APP_TIME+" ASC";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                //SchedulerModel schedulerModel = new SchedulerModel();
                schedulerModel.setConversationName(cursor.getString(cursor.getColumnIndex(WHATS_APP_CONVER_NAME)));
                schedulerModel.setPhnNumber(cursor.getString(cursor.getColumnIndex(WHATS_APP_PHONE_NUMBER)));
                schedulerModel.setIsFromGroup(cursor.getString(cursor.getColumnIndex(WHATS_APP_IS_GROUP)));
                schedulerModel.setMessageId(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_ID)));
                schedulerModel.setMessageType(cursor.getString(cursor.getColumnIndex(WHATS_APP_MESSAGE_TYPE)));
                schedulerModel.setActivityType(cursor.getString(cursor.getColumnIndex(WHATS_APP_ACITIVITY_TYPE)));
                schedulerModel.setText(cursor.getString(cursor.getColumnIndex(WHATS_APP_TEXT)));
                schedulerModel.setFileName(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_NAME)));
                schedulerModel.setFileUrl(cursor.getString(cursor.getColumnIndex(WHATS_APP_FILE_URL)));
                schedulerModel.setDeviceId(cursor.getString(cursor.getColumnIndex(WHATS_APP_DEVICE_ID)));
                schedulerModel.setTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_TIME)));
                schedulerModel.setId(cursor.getString(cursor.getColumnIndex(WHATS_APP_KEY_ID)));
                schedulerModel.setUpdateTime(cursor.getString(cursor.getColumnIndex(WHATS_APP_ENTRY_DATE_TIME)));
                schedulerModel.setFlag(cursor.getString(cursor.getColumnIndex(WHATS_APP_SEND_ACTION_FLG)));
                //schedulerModelList.add(schedulerModel);
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return schedulerModel;
    }

    public void UpdateByFlag(String ID,int iType,int Flag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(WHATS_APP_SEND_ACTION_FLG, Flag);
        values.put(WHATS_APP_ENTRY_DATE_TIME, DateFormatsMethods.getDateTime());
        db.update(WHATS_APP_TABLE, values, WHATS_APP_KEY_ID+"="+ID+" AND "+WHATS_APP_TYPE+"= "+iType, null);
        db.close();
    }

    public void DeleteByFlag(String ID,int iType,int Flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WHATS_APP_TABLE, WHATS_APP_KEY_ID+"="+ID+" AND "+WHATS_APP_TYPE+"= "+iType, null);
        db.close();
    }
}