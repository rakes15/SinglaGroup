package orderbooking.Database_Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;

public class DatabaseSqlLiteHandlerPartyInfo extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;

    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "singlaGroupsPartyInfo0";
    //----------------------------Table's Name--------------------------------------------------------------
    private static final String PARTY_INFO_TABLE = "partyInfo";
    private static final String GROUP_WISE_TABLE = "groupWise";
    //private static final String SIU_TABLE = "groupWise";
    //-------------------TODO: Party Info Table Column Names---------------------------------------------
    private static final String PARTY_INFO_KEY_ID = "id";
    private static final String PARTY_INFO_YEAR = "year";
    private static final String PARTY_INFO_MONTH_NAME = "monthName";
    private static final String PARTY_INFO_MONTH = "month";
    private static final String PARTY_INFO_QUANTITY = "quantity";
    private static final String PARTY_INFO_AMOUNT = "amount";
    private static final String PARTY_INFO_COMMON = "common";
    private static final String PARTY_INFO_COMMON_TYPE = "type";
    //-------------------TODO: Group Wise Table Column Names---------------------------------------------
    private static final String GROUP_WISE_GROUP_ID = "groupId";
    private static final String GROUP_WISE_GROUP_NAME = "groupName";
    private static final String GROUP_WISE_AMT_0 = "amt0";
    private static final String GROUP_WISE_AMT_1 = "amt1";
    private static final String GROUP_WISE_AMT_2 = "amt2";
    private static final String GROUP_WISE_QTY_0 = "qty0";
    private static final String GROUP_WISE_QTY_1 = "qty1";
    private static final String GROUP_WISE_QTY_2 = "qty2";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerPartyInfo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Party Info Category table create query
        String CREATE_PARTY_INFO_TABLE = "CREATE TABLE " + PARTY_INFO_TABLE + "(" + PARTY_INFO_KEY_ID + " INTEGER PRIMARY KEY," + PARTY_INFO_YEAR + " TEXT," + PARTY_INFO_MONTH_NAME + " TEXT," + PARTY_INFO_MONTH + " INTEGER," + PARTY_INFO_QUANTITY + " INTEGER," + PARTY_INFO_AMOUNT + " INTEGER," + PARTY_INFO_COMMON + " TEXT," + PARTY_INFO_COMMON_TYPE + " INTEGER) ";
        db.execSQL(CREATE_PARTY_INFO_TABLE);
        // TODO: Group Wise Category table create query
        String CREATE_GROUP_WISE_TABLE = "CREATE TABLE " + GROUP_WISE_TABLE + "(" + PARTY_INFO_KEY_ID + " INTEGER PRIMARY KEY," + GROUP_WISE_GROUP_ID + " TEXT," + GROUP_WISE_GROUP_NAME + " TEXT," + GROUP_WISE_AMT_0 + " REAL," + GROUP_WISE_AMT_1 + " REAL," + GROUP_WISE_AMT_2 + " REAL," + GROUP_WISE_QTY_0 + " REAL," + GROUP_WISE_QTY_1 + " REAL," + GROUP_WISE_QTY_2 + " REAL) ";
        db.execSQL(CREATE_GROUP_WISE_TABLE);
    }

    //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + PARTY_INFO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_WISE_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void deletePartyInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PARTY_INFO_TABLE, null, null);
        db.close();
    }
    public void deleteGroupWise() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GROUP_WISE_TABLE, null, null);
        db.close();
    }

    //---------------------------------- Inserting Data of Change Party OLD Table---------------------------------------
    public void insertPartyInfo(List<Map<String,String>> mapList) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,PARTY_INFO_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int Year = ih.getColumnIndex(PARTY_INFO_YEAR);
        final int MonthName = ih.getColumnIndex(PARTY_INFO_MONTH_NAME);
        final int Month = ih.getColumnIndex(PARTY_INFO_MONTH);
        final int Quantity = ih.getColumnIndex(PARTY_INFO_QUANTITY);
        final int Amount = ih.getColumnIndex(PARTY_INFO_AMOUNT);
        final int Common = ih.getColumnIndex(PARTY_INFO_COMMON);
        final int CommonType = ih.getColumnIndex(PARTY_INFO_COMMON_TYPE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<mapList.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(Year, mapList.get(x).get("Year"));
                ih.bind(MonthName, mapList.get(x).get("MonthName"));
                ih.bind(Month, mapList.get(x).get("Month"));
                ih.bind(Quantity, mapList.get(x).get("Quantity"));
                ih.bind(Amount, mapList.get(x).get("Amount"));
                ih.bind(Common, mapList.get(x).get("Common"));
                ih.bind(CommonType, (mapList.get(x).get("CommonType")==null?"0":mapList.get(x).get("CommonType")));
                // Insert the row into the database.
                ih.execute();
            }
        }
        finally {
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
            ih.close();  // See comment below from Stefan Anca
            final long endtime = System.currentTimeMillis();
            Log.e("Time:", "" + String.valueOf(endtime - startTime));
        }
    }
    public void insertGroupWise(List<Map<String,String>> mapList) {
        System.out.println("MapList:"+mapList.toString());
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,GROUP_WISE_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int GroupID = ih.getColumnIndex(GROUP_WISE_GROUP_ID);
        final int GroupName = ih.getColumnIndex(GROUP_WISE_GROUP_NAME);
        final int Amt0 = ih.getColumnIndex(GROUP_WISE_AMT_0);
        final int Amt1 = ih.getColumnIndex(GROUP_WISE_AMT_1);
        final int Amt2 = ih.getColumnIndex(GROUP_WISE_AMT_2);
        final int Qty0 = ih.getColumnIndex(GROUP_WISE_QTY_0);
        final int Qty1 = ih.getColumnIndex(GROUP_WISE_QTY_1);
        final int Qty2 = ih.getColumnIndex(GROUP_WISE_QTY_2);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<mapList.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(GroupID, mapList.get(x).get("GroupID"));
                ih.bind(GroupName, mapList.get(x).get("GroupName"));
                ih.bind(Amt0, mapList.get(x).get("Amt0"));
                ih.bind(Amt1, mapList.get(x).get("Amt1"));
                ih.bind(Amt2, mapList.get(x).get("Amt2"));
                ih.bind(Qty0, mapList.get(x).get("Qty0"));
                ih.bind(Qty1, mapList.get(x).get("Qty1"));
                ih.bind(Qty2, mapList.get(x).get("Qty2"));
                // Insert the row into the database.
                ih.execute();
            }
        }
        finally {
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
            ih.close();  // See comment below from Stefan Anca
            final long endtime = System.currentTimeMillis();
            Log.e("Time:", "" + String.valueOf(endtime - startTime));
        }
    }

    public List<Map<String, String>> getCommonList() {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_COMMON + "," + PARTY_INFO_COMMON_TYPE + " from " + PARTY_INFO_TABLE + " Order By " + PARTY_INFO_COMMON + " ASC";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Common", cursor.getString(cursor.getColumnIndex(PARTY_INFO_COMMON)));
                map.put("CommonType", cursor.getString(cursor.getColumnIndex(PARTY_INFO_COMMON_TYPE)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        //System.out.println("Year:"+dataList.toString());
        return dataList;

    }
    public List<Map<String, String>> getYearList() {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_YEAR + " from " + PARTY_INFO_TABLE + " Order By " + PARTY_INFO_YEAR + " ASC";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Year", cursor.getString(cursor.getColumnIndex(PARTY_INFO_YEAR)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        //System.out.println("Year:"+dataList.toString());
        return dataList;

    }
    public List<Map<String, String>> getMonthList() {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_MONTH_NAME + "," + PARTY_INFO_MONTH + " from " + PARTY_INFO_TABLE + " Order By " + PARTY_INFO_MONTH + " ASC";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("MonthName", cursor.getString(cursor.getColumnIndex(PARTY_INFO_MONTH_NAME)));
                map.put("Month", cursor.getString(cursor.getColumnIndex(PARTY_INFO_MONTH)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        return dataList;

    }
    public Map<String, Integer> getQtyAmtList(int Month,int Year) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_QUANTITY + ", "+PARTY_INFO_AMOUNT+" from " + PARTY_INFO_TABLE + " Where " + PARTY_INFO_MONTH + "="+Month+" AND "+PARTY_INFO_YEAR+"="+Year+" ";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //do {
                map.put("Quantity", cursor.getInt(cursor.getColumnIndex(PARTY_INFO_QUANTITY)));
                map.put("Amount", cursor.getInt(cursor.getColumnIndex(PARTY_INFO_AMOUNT)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        return map;

    }

    public List<Map<String, String>> getYearListByCommon(String Common) {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_YEAR + " from " + PARTY_INFO_TABLE + " WHERE "+PARTY_INFO_COMMON+"='"+Common+"' Order By " + PARTY_INFO_YEAR + " ASC";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Year", cursor.getString(cursor.getColumnIndex(PARTY_INFO_YEAR)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        //System.out.println("Year:"+dataList.toString());
        return dataList;

    }
    public List<Map<String, String>> getMonthListByCommon(String Common) {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_MONTH_NAME + "," + PARTY_INFO_MONTH + " from " + PARTY_INFO_TABLE + " WHERE "+PARTY_INFO_COMMON+"='"+Common+"' Order By " + PARTY_INFO_MONTH + " ASC";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("MonthName", cursor.getString(cursor.getColumnIndex(PARTY_INFO_MONTH_NAME)));
                map.put("Month", cursor.getString(cursor.getColumnIndex(PARTY_INFO_MONTH)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        return dataList;

    }
    public Map<String, Integer> getQtyAmtListByCommon(int Month,int Year,String Common) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        // Select All Query
        String selectQuery = "select Distinct " + PARTY_INFO_QUANTITY + ", "+PARTY_INFO_AMOUNT+" from " + PARTY_INFO_TABLE + " Where " + PARTY_INFO_MONTH + "="+Month+" AND "+PARTY_INFO_YEAR+"="+Year+" AND "+PARTY_INFO_COMMON+"='"+Common+"' ";
        System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //do {
            map.put("Quantity", cursor.getInt(cursor.getColumnIndex(PARTY_INFO_QUANTITY)));
            map.put("Amount", cursor.getInt(cursor.getColumnIndex(PARTY_INFO_AMOUNT)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        return map;

    }


    //TODO: Group Wise
    public List<Map<String, String>> getGroupWise() {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + GROUP_WISE_GROUP_ID + "," + GROUP_WISE_GROUP_NAME + " from " + GROUP_WISE_TABLE + " WHERE ("+GROUP_WISE_QTY_0+"+"+GROUP_WISE_QTY_1+"+"+GROUP_WISE_QTY_2+")>0 Order By " + GROUP_WISE_GROUP_NAME + " ASC";
        //System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("GroupID", cursor.getString(cursor.getColumnIndex(GROUP_WISE_GROUP_ID)));
                map.put("GroupName", cursor.getString(cursor.getColumnIndex(GROUP_WISE_GROUP_NAME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        //System.out.println("Year:"+dataList.toString());
        return dataList;

    }
    //TODO: Group Wise get Qty Amt
    public List<Map<String, String>> getQtyAmtByGroup(String GroupID) {
        List<Map<String, String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select Distinct " + GROUP_WISE_AMT_0 + "," + GROUP_WISE_AMT_1 + "," + GROUP_WISE_AMT_2 + "," + GROUP_WISE_QTY_0 + "," + GROUP_WISE_QTY_1 + "," + GROUP_WISE_QTY_2 + " from " + GROUP_WISE_TABLE + " WHERE ("+GROUP_WISE_QTY_0+"+"+GROUP_WISE_QTY_1+"+"+GROUP_WISE_QTY_2+")>0  AND " + GROUP_WISE_GROUP_ID+" = '"+GroupID+"' ";
        //System.out.println("Query:" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Amt0", cursor.getString(cursor.getColumnIndex(GROUP_WISE_AMT_0)));
                map.put("Amt1", cursor.getString(cursor.getColumnIndex(GROUP_WISE_AMT_1)));
                map.put("Amt2", cursor.getString(cursor.getColumnIndex(GROUP_WISE_AMT_2)));
                map.put("Qty0", cursor.getString(cursor.getColumnIndex(GROUP_WISE_QTY_0)));
                map.put("Qty1", cursor.getString(cursor.getColumnIndex(GROUP_WISE_QTY_1)));
                map.put("Qty2", cursor.getString(cursor.getColumnIndex(GROUP_WISE_QTY_2)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
            // returning lables
        }
        //System.out.println("Year:"+dataList.toString());
        return dataList;

    }
}