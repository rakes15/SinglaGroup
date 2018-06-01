package DatabaseController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.singlagroup.customwidgets.DateFormatsMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseSqlLiteHandlerActiveSessionManage {
    private static final String TAG = DatabaseSqlLiteHandlerActiveSessionManage.class.getSimpleName();
    //TODo: Table Name
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_TABLE = "activeSessionManageTbl";
    //TODO: Filter  Table Column's Name
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_KEY_ID = "id";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID = "sessionID";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_DATE = "activeDate";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME = "activeTime1";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG = "serverFlag";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG = "flag";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME = "loginDatetime";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME = "logoutDatetime";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME = "entryDatetime";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerActiveSessionManage(Context context) {
        this.context = context;
    }
    //TODO: Filter Table Delete
    public void ActiveSessionManageTableAllDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, null, null);
        db.close();
    }
    public void ActiveSessionManageTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG+"=1", null);
        db.close();
    }
    //TODO: Inserting Data of Filter Table
    public void insertActiveSessionManageTable(String Session_ID,String Active_Time) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_ACTIVE_SESSION_MANAGE_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int SessionID = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID);
        final int ActiveDate = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_DATE);
        final int ActiveTime = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME);
        final int ServerFlag = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG);
        final int LocalFlag = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG);
        final int LoginDatetime = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME);
        final int LogoutDatetime = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME);
        final int EntryDatetime = ih.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            //for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(SessionID, Session_ID);
                ih.bind(ActiveDate, getDateTime().substring(0,10));
                ih.bind(ActiveTime, Active_Time);
                ih.bind(ServerFlag, "0");
                ih.bind(LocalFlag, "0");
                ih.bind(LoginDatetime, getDateTime());
                ih.bind(LogoutDatetime, getDateTime());
                ih.bind(EntryDatetime, getDateTime());
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
    public List<Map<String,String>> getAllDetailsExistBySessionID(String SessionID) {
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT * from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + " WHERE "+SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID+" = '"+SessionID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SessionID", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID)));
                map.put("ActiveTime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //Log.d(TAG,"All ActiveTime :"+dataList.toString());
        // returning
        return dataList;

    }
    public void UpdateLogin(String SessionID,String ActiveTime){
        System.out.println("LoginIn:"+ActiveTime);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME, ActiveTime);
        values.put(SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG, 0);
        db.update(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, values, SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID+" = '"+SessionID+"' ", null);
        db.close();
    }
    public void UpdateLogout(String SessionID,String ActiveTime){
        System.out.println("LogoutIn:"+ActiveTime);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME, ActiveTime);
        values.put(SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG, 1);
        db.update(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, values, SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID+" = '"+SessionID+"' ", null);
        db.close();
//        Log.d(TAG,"ActiveTime:"+getActiveTime(SessionID));
    }
    public List<Map<String,String>> getAllActiveTimesByLocalFlag() {
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT "+SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID+","+SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME+" from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE  + " WHERE  " + SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG + "=1 AND " + SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG + "=0 ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SessionID", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID)));
                map.put("ActiveTime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //Log.d(TAG,"All ActiveTime :"+dataList.toString());
        // returning
        return dataList;

    }
    public List<Map<String,String>> getLogoutDetails(String SessionID) {
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT * from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + " WHERE "+SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG+"=1 AND "+SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG+"=0 ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SessionID", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID)));
                map.put("ActiveDate", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_DATE)));
                map.put("ActiveTime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME)));
                map.put("ServerFlag", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG)));
                map.put("LocalFlag", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG)));
                map.put("LoginDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME)));
                map.put("LogoutDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME)));
                map.put("EntryDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public List<Map<String,String>> getActiveSessionAllDetails(String SessionID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT * from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + " WHERE "+ SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID + "!='"+SessionID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SessionID", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID)));
                map.put("ActiveDate", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_DATE)));
                map.put("ActiveTime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME)));
                map.put("ServerFlag", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG)));
                map.put("LocalFlag", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG)));
                map.put("LoginDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME)));
                map.put("LogoutDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME)));
                map.put("EntryDatetime", cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public String getActiveTime(String SessionID){
        List<Map<String,String>> mapList = getLogoutDetails(SessionID);
        String CalculateActiveTime="";
        int second=0;
        if (!mapList.isEmpty()) {
            for(int i=0; i<mapList.size(); i++) {
                String LoginTime = (mapList.get(i).get("LoginDatetime") == null ? "" + getDateTime() : mapList.get(i).get("LoginDatetime"));
                String LogoutTime = (mapList.get(i).get("LogoutDatetime") == null ? "" + getDateTime() : mapList.get(i).get("LogoutDatetime"));
                // Select All Query
                String selectQuery = "SELECT strftime('%s', '" + LogoutTime + "') - strftime('%s', '" + LoginTime + "') ";
                System.out.println(selectQuery);
                DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
                SQLiteDatabase db = rootHandler.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                // looping through all rows and adding to list
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    second += cursor.getInt(0);
                    Log.d(TAG, "Time in seconds:" + cursor.getInt(0));
                    // closing connection
                    cursor.close();
                    db.close();
                }
            }
        }
        CalculateActiveTime = DateFormatsMethods.formatSeconds(second);
        Log.d(TAG, String.format("Total Active Time:" + CalculateActiveTime));
        // returning
        return CalculateActiveTime;
    }
    public String getTime(String SessionID,int flag){
        String CalculateActiveTime="";
        // Select All Query
        String selectQuery = "";
        if (flag == 0) {
            selectQuery = "SELECT " + SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME + " from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + " WHERE  " + SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG + "=1 AND " + SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG + "=0 ";
        }else if (flag == 1) {
            selectQuery = "SELECT " + SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME + " from " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + " WHERE  " + SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG + "=1 AND " + SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG + "=0 ";
        }
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
                CalculateActiveTime = cursor.getString(cursor.getColumnIndex(SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return CalculateActiveTime;
    }
    public void UpdateServerFlag(int Flag,String SessionID,int flagSessionEqual){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        if (flagSessionEqual == 0) {
            ContentValues values = new ContentValues();
            values.put(SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG, Flag);
            db.update(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, values, SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID + "!= '" + SessionID + "' ", null);
            db.close();
        }else if(flagSessionEqual == 1) {
            ContentValues values = new ContentValues();
            values.put(SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG, Flag);
            db.update(SINGLA_ACTIVE_SESSION_MANAGE_TABLE, values, SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID + "= '" + SessionID + "' ", null);
            db.close();
        }
    }
}