package inventory.analysis.catalogue.Database_Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqliteRootHandler;
import orderbooking.StaticValues;

public class DatabaseSqlLiteHandlerFilter{
    private static final String TAG = DatabaseSqlLiteHandlerFilter.class.getSimpleName();
    //TODo: Table Name
    private static final String SINGLA_FILTER_TABLE = "filterTbl";
    //TODO: Filter  Table Column's Name
    private static final String SINGLA_FILTER_KEY_ID = "id";
    private static final String SINGLA_FILTER_ATTR_ID = "filterAttrID";
    private static final String SINGLA_FILTER_ATTR_NAME = "filterAttrName";
    private static final String SINGLA_FILTER_SEQUENCE = "filterSeq";
    private static final String SINGLA_FILTER_FLAG = "filterFlag";
    private static final String SINGLA_FILTER_FLAG_TEMP = "filterFlagTemp";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerFilter(Context context) {
        this.context = context;
    }
    //TODO: Filter Table Delete
    public void FilterTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_FILTER_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of Filter Table
    public void insertFilterTable(List<Map<String,String>> list) {
        //System.out.println("Filter:"+list.toString());
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_FILTER_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int AttrID = ih.getColumnIndex(SINGLA_FILTER_ATTR_ID);
        final int Attr = ih.getColumnIndex(SINGLA_FILTER_ATTR_NAME);
        final int Seq = ih.getColumnIndex(SINGLA_FILTER_SEQUENCE);
        final int Flag = ih.getColumnIndex(SINGLA_FILTER_FLAG);
        final int FlagTemp = ih.getColumnIndex(SINGLA_FILTER_FLAG_TEMP);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(AttrID, list.get(x).get("ID"));
                String TotalItem = (list.get(x).get("TotalItem")=="")?"":(list.get(x).get("TotalItem").equals("0")?"":"("+list.get(x).get("TotalItem")+")");
                ih.bind(Attr, list.get(x).get("Name")+TotalItem);
                ih.bind(Seq, list.get(x).get("Seq"));
                ih.bind(Flag, "0");
                ih.bind(FlagTemp, "0");
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
    public List<Map<String,String>> getFilter(){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_FILTER_ATTR_ID+"), "+SINGLA_FILTER_ATTR_NAME+", "+SINGLA_FILTER_SEQUENCE+", "+SINGLA_FILTER_FLAG+" from " + SINGLA_FILTER_TABLE;
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
                map.put("AttrID", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_ID)));
                map.put("Attr", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_NAME)));
                map.put("Seq", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE)));
                map.put("Flag", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_FLAG)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<Map<String,String>> getColor(int Seq){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_FILTER_ATTR_ID+", "+SINGLA_FILTER_ATTR_NAME+", "+SINGLA_FILTER_SEQUENCE+", "+SINGLA_FILTER_FLAG+" from " + SINGLA_FILTER_TABLE+" WHERE "+SINGLA_FILTER_ATTR_ID+" is not null AND "+SINGLA_FILTER_SEQUENCE+"="+Seq;
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
                map.put("AttrID", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_ID)));
                map.put("Attr", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_NAME)));
                map.put("Seq", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE)));
                map.put("Flag", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_FLAG)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        System.out.println("Attributes:"+dataList.toString());
        // returning
        return dataList;
    }
    public void UpdateFlag(int Flag,int Seq,String AttrID)
    {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SINGLA_FILTER_FLAG, Flag);
        db.update(SINGLA_FILTER_TABLE, values, SINGLA_FILTER_ATTR_ID+"= '"+AttrID+"' AND "+SINGLA_FILTER_SEQUENCE+"="+Seq, null);
        db.close();
    }
    public void ApplyFilterTempFlag()
    {
        // Select All Query
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        String UpdateQuery="Update "+SINGLA_FILTER_TABLE+" SET "+SINGLA_FILTER_FLAG_TEMP+"=1 where "+SINGLA_FILTER_FLAG+"=1";
        db.execSQL(UpdateQuery);
        db.close();
        getDispaly(0);
    }
    public void RestoreFilterFlag()
    {
        // Select All Query
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        String UpdateQuery="Update "+SINGLA_FILTER_TABLE+" SET "+SINGLA_FILTER_FLAG+"=1 where "+SINGLA_FILTER_FLAG_TEMP+"=1";
        db.execSQL(UpdateQuery);
        db.close();
        getDispaly(0);
    }
    public List<Map<String,String>> getDispaly(int flag){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery="";
        if (flag==0) {
            selectQuery = "SELECT DISTINCT " + SINGLA_FILTER_ATTR_NAME + ", " + SINGLA_FILTER_FLAG_TEMP + ", " + SINGLA_FILTER_FLAG + " from " + SINGLA_FILTER_TABLE;
        }
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("Attr", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_NAME)));
                map.put("Flag", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_FLAG)));
                map.put("FlagTemp", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_FLAG_TEMP)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public void UpdateFlagClear(int Flag)
    {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SINGLA_FILTER_FLAG, Flag);
        db.update(SINGLA_FILTER_TABLE, values, null, null);
        db.close();
    }
    public String[] ApplyFilter(int StrLength)
    {
        String[] str=new String[StrLength];
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_FILTER_ATTR_ID+","+SINGLA_FILTER_SEQUENCE+" from " + SINGLA_FILTER_TABLE+" WHERE "+SINGLA_FILTER_FLAG+"=1";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                str[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))]=(str[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))]==null)?"":str[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))];
                str[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))] += "'"+cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_ATTR_ID))+"',";
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        ApplyFilterTempFlag();
        return  str;
    }
    public int FilterStatus()
    {
        int status=0;
        // Select All Query
        String selectQuery = "SELECT COUNT("+SINGLA_FILTER_ATTR_ID+") as total from " + SINGLA_FILTER_TABLE+" WHERE "+SINGLA_FILTER_FLAG+"=1";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                status=cursor.getInt(cursor.getColumnIndex("total"));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return  status;
    }
    public List<Map<String,String>> CaptionWithSeq(){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_FILTER_SEQUENCE+") from " + SINGLA_FILTER_TABLE+" ORDER BY "+SINGLA_FILTER_SEQUENCE+" ASC" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                if(StaticValues.FilterCaptionSequence[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))]!=null) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Seq", cursor.getString(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE)));
                    map.put("Caption", StaticValues.FilterCaptionSequence[cursor.getInt(cursor.getColumnIndex(SINGLA_FILTER_SEQUENCE))]);
                    dataList.add(map);
                }
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
}