package report.DatabaseSqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import report.showroomitemcheck.model.Group;
import report.showroomitemcheck.model.ItemDetails;

public class DBSqlLiteHandlerShowroomItemCheck extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_ShowroomVsGodownReport";
 
    //----------------------------Table Name--------------------------------------------------------------
    private static final String REPORT_TABLE = "showroomReportTbl";   
    //-------------------Barcode Scanner Table Column Names---------------------------------------------
    private static final String SHOWROOM_REPORT_KEY_ID = "id";
    private static final String SHOWROOM_REPORT_MAIN_GROUP_ID = "mainGroupID";
    private static final String SHOWROOM_REPORT_MAIN_GROUP_NAME = "mainGroupName";
    private static final String SHOWROOM_REPORT_GROUP_ID = "groupID";
    private static final String SHOWROOM_REPORT_GROUP_NAME = "groupName";
    private static final String SHOWROOM_REPORT_SUB_GROUP_ID = "subGroupID";
    private static final String SHOWROOM_REPORT_SUB_GROUP_NAME = "subGroupName";
    private static final String SHOWROOM_REPORT_ITEM_ID = "itemID";
    private static final String SHOWROOM_REPORT_ITEM_CODE = "itemCode";
    private static final String SHOWROOM_REPORT_ITEM_NAME = "itemName";
    private static final String SHOWROOM_REPORT_COLOR_ID = "colorID";
    private static final String SHOWROOM_REPORT_COLOR_NAME = "colorName";
    private static final String SHOWROOM_REPORT_STOCK = "stock";
    private static final String SHOWROOM_REPORT_SUB_ITEM_ID = "subItemID";
    private static final String SHOWROOM_REPORT_SUB_ITEM_CODE = "subItemCode";
    private static final String SHOWROOM_REPORT_SUB_ITEM_NAME = "subItemName";
    private static final String SHOWROOM_REPORT_SUB_ITEM_STOCK = "subItemStock";
    private static final String SHOWROOM_REPORT_MD_APPLICABLE = "mDApplicable";
    private static final String SHOWROOM_REPORT_SUBITEM_APPLICABLE = "subItemApplicable";
    private static final String SHOWROOM_REPORT_GODOWN_ID = "godownID";
    private static final String SHOWROOM_REPORT_GODOWN_NAME = "godownName";
    //	------------------------Constructor call----------------------------------------
    public DBSqlLiteHandlerShowroomItemCheck(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_TABLE_SHOWROOM_REPORT_TABLE = "CREATE TABLE " + REPORT_TABLE + "("+ SHOWROOM_REPORT_KEY_ID + " INTEGER PRIMARY KEY," + SHOWROOM_REPORT_MAIN_GROUP_ID + " TEXT," + SHOWROOM_REPORT_MAIN_GROUP_NAME + " TEXT," + SHOWROOM_REPORT_GROUP_ID + " TEXT," + SHOWROOM_REPORT_GROUP_NAME + " TEXT," + SHOWROOM_REPORT_SUB_GROUP_ID + " TEXT," + SHOWROOM_REPORT_SUB_GROUP_NAME + " TEXT,"+ SHOWROOM_REPORT_ITEM_ID + " TEXT,"+ SHOWROOM_REPORT_ITEM_CODE + " TEXT,"  + SHOWROOM_REPORT_ITEM_NAME + " TEXT,"+ SHOWROOM_REPORT_COLOR_ID + " TEXT,"+ SHOWROOM_REPORT_COLOR_NAME + " TEXT,"+ SHOWROOM_REPORT_STOCK + " TEXT,"+ SHOWROOM_REPORT_SUB_ITEM_ID + " TEXT,"+ SHOWROOM_REPORT_SUB_ITEM_CODE + " TEXT,"+ SHOWROOM_REPORT_SUB_ITEM_NAME + " TEXT,"+ SHOWROOM_REPORT_SUB_ITEM_STOCK + " TEXT,"+ SHOWROOM_REPORT_MD_APPLICABLE + " INTEGER,"+ SHOWROOM_REPORT_SUBITEM_APPLICABLE + " INTEGER,"+ SHOWROOM_REPORT_GODOWN_ID + " TEXT,"+ SHOWROOM_REPORT_GODOWN_NAME + " TEXT) ";
    	db.execSQL(CREATE_TABLE_SHOWROOM_REPORT_TABLE);
    }
    //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +REPORT_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteShowroomVsGodown(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE,null,null);
        db.close();
    }
    //---------------------------------- Inserting Data of Item Details Table---------------------------------------
    public void insertShowroomReportData(List<Map<String,String>> list){
    	
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,REPORT_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int MainGroupID = ih.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_ID);
        final int MainGroupName = ih.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_NAME);
        final int GroupID = ih.getColumnIndex(SHOWROOM_REPORT_GROUP_ID);
        final int GroupName = ih.getColumnIndex(SHOWROOM_REPORT_GROUP_NAME);
        final int SubGroupID = ih.getColumnIndex(SHOWROOM_REPORT_SUB_GROUP_ID);
        final int SubGroupName = ih.getColumnIndex(SHOWROOM_REPORT_SUB_GROUP_NAME);
        final int ItemID = ih.getColumnIndex(SHOWROOM_REPORT_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(SHOWROOM_REPORT_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(SHOWROOM_REPORT_ITEM_NAME);
        final int ColorID = ih.getColumnIndex(SHOWROOM_REPORT_COLOR_ID);
        final int ColorName = ih.getColumnIndex(SHOWROOM_REPORT_COLOR_NAME);
        final int Stock = ih.getColumnIndex(SHOWROOM_REPORT_STOCK);
        final int SubItemID = ih.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_ID);
        final int SubItemCode = ih.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_CODE);
        final int SubItemName = ih.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_NAME);
        final int SubItemStock = ih.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_STOCK);
        final int MDApplicable = ih.getColumnIndex(SHOWROOM_REPORT_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(SHOWROOM_REPORT_SUBITEM_APPLICABLE);
        final int GodownID = ih.getColumnIndex(SHOWROOM_REPORT_GODOWN_ID);
        final int Godown = ih.getColumnIndex(SHOWROOM_REPORT_GODOWN_NAME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
                ih.bind(MainGroupName, list.get(x).get("MainGroupName"));
                ih.bind(GroupID, list.get(x).get("GroupID"));
                ih.bind(GroupName, list.get(x).get("GroupName"));
                ih.bind(SubGroupID, list.get(x).get("SubGroupID"));
                ih.bind(SubGroupName, list.get(x).get("SubGroupName"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(ColorName, list.get(x).get("Color"));
                ih.bind(Stock, list.get(x).get("Stock"));
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(Godown, list.get(x).get("Godown"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(SubItemCode, list.get(x).get("SubItemCode"));
                ih.bind(SubItemName, list.get(x).get("SubItemName"));
                ih.bind(SubItemStock, list.get(x).get("ItemSubItemStock"));
                ih.bind(SubItemApplicable, list.get(x).get("SubItemApplicable"));
                ih.bind(MDApplicable, list.get(x).get("MDApplicable"));
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
    //---------------------------------- Getting Data of report GroupName List ---------------------------------------
    public List<Group> getGroupList(){
    	List<Group> groupList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SHOWROOM_REPORT_GROUP_ID+","+SHOWROOM_REPORT_GROUP_NAME+","+SHOWROOM_REPORT_MAIN_GROUP_ID+","+SHOWROOM_REPORT_MAIN_GROUP_NAME+" from " + REPORT_TABLE + " ORDER By "+SHOWROOM_REPORT_GROUP_NAME+ " Asc";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                groupList.add(new Group(cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_NAME)),0));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
    	}
    	// returning 
    	return groupList;
    }
    public List<ItemDetails> getItemDetailsByGroup(String GroupID,String MainGroupID){
        List<ItemDetails> itemDetailsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SHOWROOM_REPORT_ITEM_ID+","+SHOWROOM_REPORT_ITEM_NAME+","+SHOWROOM_REPORT_ITEM_CODE+","+SHOWROOM_REPORT_SUB_ITEM_ID+","+SHOWROOM_REPORT_SUB_ITEM_NAME+","+SHOWROOM_REPORT_SUB_ITEM_CODE+","+SHOWROOM_REPORT_SUB_GROUP_ID+","+SHOWROOM_REPORT_SUB_GROUP_NAME+","+SHOWROOM_REPORT_GROUP_ID+","+SHOWROOM_REPORT_GROUP_NAME+","+SHOWROOM_REPORT_MAIN_GROUP_ID+","+SHOWROOM_REPORT_MAIN_GROUP_NAME+","+SHOWROOM_REPORT_MD_APPLICABLE+","+SHOWROOM_REPORT_SUBITEM_APPLICABLE+","+SHOWROOM_REPORT_STOCK+","+SHOWROOM_REPORT_SUB_ITEM_STOCK+" from "+ REPORT_TABLE +" WHERE "+SHOWROOM_REPORT_GROUP_ID+"='"+GroupID+"' AND "+SHOWROOM_REPORT_MAIN_GROUP_ID+"='"+MainGroupID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do { //c9925
                //System.out.println("Item:"+cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_ITEM_CODE)));
                itemDetailsList.add(new ItemDetails(
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_ITEM_CODE)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_CODE)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_GROUP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_GROUP_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_GROUP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_GROUP_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_MAIN_GROUP_NAME)),
                        cursor.getInt(cursor.getColumnIndex(SHOWROOM_REPORT_MD_APPLICABLE)),
                        cursor.getInt(cursor.getColumnIndex(SHOWROOM_REPORT_SUBITEM_APPLICABLE)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_STOCK)),
                        cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_STOCK))
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
        }
        // returning
        return itemDetailsList;
    }
    //---------------------------------- Getting Data of report GroupWise Data ---------------------------------------
    public List<Map<String,String>> getShowroomColorName(String ItemID,String GroupID,String MainGroupID){
    	List<Map<String,String>> mapList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SHOWROOM_REPORT_COLOR_ID+","+SHOWROOM_REPORT_COLOR_NAME+","+SHOWROOM_REPORT_SUB_ITEM_STOCK+","+SHOWROOM_REPORT_STOCK+" from " + REPORT_TABLE+" WHERE "+SHOWROOM_REPORT_ITEM_ID+"='"+ItemID+"' AND "+SHOWROOM_REPORT_GROUP_ID+"='"+GroupID+"' AND "+SHOWROOM_REPORT_MAIN_GROUP_ID+"='"+MainGroupID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_COLOR_NAME)));
            	map.put("ColorSizeStock", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_STOCK)));
           	 	map.put("ItemStock", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_STOCK)));
                mapList.add(map);
               } while (cursor.moveToNext());
            // closing connection
            cursor.close();
    	}
    	// returning 
    	return mapList;
    }
    //---------------------------------- Getting Data of report GroupWise Data ---------------------------------------
    public List<Map<String,String>> getShowroomSubItem(String ItemID,String GroupID,String MainGroupID){
        List<Map<String,String>> mapList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SHOWROOM_REPORT_SUB_ITEM_ID+","+SHOWROOM_REPORT_SUB_ITEM_NAME+","+SHOWROOM_REPORT_SUB_ITEM_STOCK+","+SHOWROOM_REPORT_STOCK+" from " + REPORT_TABLE+" WHERE "+SHOWROOM_REPORT_ITEM_ID+"='"+ItemID+"' AND "+SHOWROOM_REPORT_GROUP_ID+"='"+GroupID+"' AND "+SHOWROOM_REPORT_MAIN_GROUP_ID+"='"+MainGroupID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SubItem", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_NAME)));
                map.put("SubItemStock", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_SUB_ITEM_STOCK)));
                map.put("ItemStock", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_STOCK)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
        }
        // returning
        return mapList;
    }
    public Map<String,String> getShowroomItemOnly(String ItemID,String GroupID,String MainGroupID){
        Map<String,String> map = new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SHOWROOM_REPORT_STOCK+" from " + REPORT_TABLE+" WHERE "+SHOWROOM_REPORT_ITEM_ID+"='"+ItemID+"' AND "+SHOWROOM_REPORT_GROUP_ID+"='"+GroupID+"' AND "+SHOWROOM_REPORT_MAIN_GROUP_ID+"='"+MainGroupID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            map.put("ItemStock", cursor.getString(cursor.getColumnIndex(SHOWROOM_REPORT_STOCK)));
            // closing connection
            cursor.close();
        }
        // returning
        return map;
    }
}
