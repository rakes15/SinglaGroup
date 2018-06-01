package report.DatabaseSqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import report.revertforceclose.model.RevertFlagTypeWithNameDataset;
import report.revertforceclose.model.RevertForceCloseDataset;

public class DatabaseSqlLiteHandlerRevertForceClose extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "Android_DB_RevertForceClose";
 
    //----------------------------Table Name--------------------------------------------------------------
    private static final String REPORT_TABLE = "revertForceCloseTbl";   
    //-------------------Barcode Scanner Table Column Names---------------------------------------------
    private static final String REPORT_KEY_ID = "id";
    private static final String REPORT_PARTY_ID = "custID";
    private static final String REPORT_PARTY_NAME = "custName";
    private static final String REPORT_ORDER_ID = "orderID";
    private static final String REPORT_ORDER_NO = "orderNo";
    private static final String REPORT_ITEM_ID = "itemID";
    private static final String REPORT_ITEM_CODE = "itemCode";
    private static final String REPORT_ITEM_NAME = "itemName";
    private static final String REPORT_FLAG_TYPE = "flagType";
    private static final String REPORT_FLAG_TYPE_NAME = "flagTypeName";
    private static final String REPORT_TRANSACTION_ID = "transactionID";
    private static final String REPORT_ENTRY_DATE_TIME = "dateTime";
    private static final String REPORT_USER_ID = "userID";
    private static final String REPORT_USER_NAME = "userName";
    private static final String REPORT_ORDER_AMT = "orderAmt";
    private static final String REPORT_ITEM_BOOK_QTY = "itemBookQty";
    private static final String REPORT_ITEM_BOOK_AMT = "itemBookAmt";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerRevertForceClose(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_TABLE_REPORT_TABLE = "CREATE TABLE " + REPORT_TABLE + "("+ REPORT_KEY_ID + " INTEGER PRIMARY KEY," + REPORT_PARTY_ID + " TEXT," + REPORT_PARTY_NAME + " TEXT," + REPORT_ORDER_ID + " TEXT,"+ REPORT_ORDER_NO + " TEXT," + REPORT_ITEM_ID + " TEXT," + REPORT_ITEM_CODE + " TEXT,"  + REPORT_ITEM_NAME + " TEXT,"+ REPORT_FLAG_TYPE + " INTEGER,"+ REPORT_FLAG_TYPE_NAME + " TEXT," + REPORT_TRANSACTION_ID + " TEXT,"+ REPORT_ENTRY_DATE_TIME + " TEXT,"  + REPORT_USER_ID + " TEXT," + REPORT_USER_NAME + " TEXT,"+ REPORT_ORDER_AMT + " INTEGER," + REPORT_ITEM_BOOK_AMT + " INTEGER,"+ REPORT_ITEM_BOOK_QTY + " INTEGER) ";
    	db.execSQL(CREATE_TABLE_REPORT_TABLE);
    }
 //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +REPORT_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteRevertForceClose() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE,null,null);
        db.close();
    }
  //---------------------------------- Inserting Data of Item Details Table---------------------------------------
    public void insertRevertForceClose(List<Map<String,String>> list){
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,REPORT_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int CustID = ih.getColumnIndex(REPORT_PARTY_ID);
        final int CustName = ih.getColumnIndex(REPORT_PARTY_NAME);
        final int OrderID = ih.getColumnIndex(REPORT_ORDER_ID);
        final int OrderNo = ih.getColumnIndex(REPORT_ORDER_NO);
        final int ItemID = ih.getColumnIndex(REPORT_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(REPORT_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(REPORT_ITEM_NAME);
        final int FlagType = ih.getColumnIndex(REPORT_FLAG_TYPE);
        final int FlagTypeName = ih.getColumnIndex(REPORT_FLAG_TYPE_NAME);
        final int TransactionID = ih.getColumnIndex(REPORT_TRANSACTION_ID);
        final int EntryDateTime = ih.getColumnIndex(REPORT_ENTRY_DATE_TIME);
        final int UserID = ih.getColumnIndex(REPORT_USER_ID);
        final int UserName = ih.getColumnIndex(REPORT_USER_NAME);
        final int OrderAmt = ih.getColumnIndex(REPORT_ORDER_AMT);
        final int ItemBookQty = ih.getColumnIndex(REPORT_ITEM_BOOK_QTY);
        final int ItemBookAmt = ih.getColumnIndex(REPORT_ITEM_BOOK_AMT);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(CustID, list.get(x).get("CustID"));
                ih.bind(CustName, list.get(x).get("CustName"));
                ih.bind(OrderID, list.get(x).get("OrderID"));
                ih.bind(OrderNo, list.get(x).get("OrderNo"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(FlagType, list.get(x).get("FlagType"));
                ih.bind(FlagTypeName, list.get(x).get("FlagTypeName"));
                ih.bind(TransactionID, list.get(x).get("TransactionID"));
                ih.bind(EntryDateTime, list.get(x).get("EntryDateTime"));
                ih.bind(UserID, list.get(x).get("UserID"));
                ih.bind(UserName, list.get(x).get("UserName"));
                ih.bind(OrderAmt, list.get(x).get("OrderAmt"));
                ih.bind(ItemBookQty, list.get(x).get("ItemBookQty"));
                ih.bind(ItemBookAmt, list.get(x).get("ItemBookAmt"));
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
    public List<RevertFlagTypeWithNameDataset> getFlagTypeNameList(){
    	List<RevertFlagTypeWithNameDataset> dataList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_FLAG_TYPE+","+REPORT_FLAG_TYPE_NAME+" from " + REPORT_TABLE+" Order By "+REPORT_FLAG_TYPE+" ASC";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
            	dataList.add(new RevertFlagTypeWithNameDataset(cursor.getString(cursor.getColumnIndex(REPORT_FLAG_TYPE_NAME)),cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))));
                //System.out.println("Query:Data:-> "+cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE)));
               } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
    	}
    	// returning 
    	return dataList;
    }
    //TODO: Get Force Close Item Details By Flag
    public List<RevertForceCloseDataset> getItemDetailsByFlag(int FlagType){
        List<RevertForceCloseDataset> dataList=new ArrayList<>();
        String selectQuery = "";
        // Select All Query
        if (FlagType == 0){
            //TODO: Item Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ITEM_ID+","+REPORT_ITEM_CODE+" ,"+REPORT_ITEM_NAME+","+REPORT_TRANSACTION_ID+","+REPORT_ENTRY_DATE_TIME+","+REPORT_FLAG_TYPE+" from " + REPORT_TABLE+" WHERE "+REPORT_FLAG_TYPE+"="+FlagType+" Order By "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 1){
            //TODO: Item with Order Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ITEM_ID+","+REPORT_ITEM_CODE+","+REPORT_ITEM_NAME+","+REPORT_TRANSACTION_ID+","+REPORT_ORDER_ID+","+REPORT_ORDER_NO+","+REPORT_FLAG_TYPE+","+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_FLAG_TYPE+"="+FlagType+" Order By "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 2){
            //TODO: Item with Customer Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ITEM_ID+","+REPORT_ITEM_CODE+","+REPORT_ITEM_NAME+","+REPORT_TRANSACTION_ID+","+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_FLAG_TYPE+","+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_FLAG_TYPE+"="+FlagType+" Order By "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 3){
            //TODO: Customer Wise
            selectQuery = "SELECT DISTINCT "+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_TRANSACTION_ID+","+REPORT_FLAG_TYPE+","+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_FLAG_TYPE+"="+FlagType+" Order By "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 4){
            //TODO: Order Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ORDER_ID+","+REPORT_ORDER_NO+","+REPORT_TRANSACTION_ID+","+REPORT_FLAG_TYPE+","+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_FLAG_TYPE+"="+FlagType+" Order By "+REPORT_ENTRY_DATE_TIME+" DESC";
        }
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                Map<String,Integer> map = new HashMap<>();
                String EntryDateTime="";
                if (FlagType == 0) {
                    //TODO: Item Wise
                    EntryDateTime = getEntryDateTime(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),"","");
                    map = getTotalQtyAmt(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),"","");
                    dataList.add(new RevertForceCloseDataset(
                              cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                            , ""   // OrderID
                            , ""   // Order No
                            , ""   // Order Date
                            , cursor.getString(cursor.getColumnIndex(REPORT_TRANSACTION_ID))
                            , EntryDateTime  // Entry Date Time
                            , ""   // Party ID
                            , ""   // Party Name
                            , cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))
                            , map.get("TotalQty")
                            , map.get("TotalAmt")
                    ));
                } else if (FlagType == 1) {
                    //TODO: Item with Order Wise
                    EntryDateTime = getEntryDateTime(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),"",cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
                    map = getTotalQtyAmt(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),"",cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
                    dataList.add(new RevertForceCloseDataset(
                            cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))   // OrderID
                            , cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))   // Order No
                            , ""   // Order Date
                            , cursor.getString(cursor.getColumnIndex(REPORT_TRANSACTION_ID))
                            , EntryDateTime  // Entry Date Time
                            , ""   // Party ID
                            , ""   // Party Name
                            , cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))
                            , map.get("TotalQty")
                            , map.get("TotalAmt")
                    ));
                } else if (FlagType == 2) {
                    //TODO: Item with Party Wise
                    EntryDateTime = getEntryDateTime(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)),"");
                    map = getTotalQtyAmt(FlagType,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)),cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)),"");
                    dataList.add(new RevertForceCloseDataset(
                            cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                            , cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                            , ""   // OrderID
                            , ""   // Order No
                            , ""   // Order Date
                            , cursor.getString(cursor.getColumnIndex(REPORT_TRANSACTION_ID))
                            , EntryDateTime  // Entry Date Time
                            , cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))   // Party ID
                            , cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))   // Party Name
                            , cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))
                            , map.get("TotalQty")
                            , map.get("TotalAmt")
                    ));
                } else if (FlagType == 3) {
                    //TODO: Party Wise
                    EntryDateTime = getEntryDateTime(FlagType,"",cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)),"");
                    map = getTotalQtyAmt(FlagType,"",cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)),"");
                    dataList.add(new RevertForceCloseDataset(
                              ""   // Item ID
                            , ""   // Item Code
                            , ""   // Item Name
                            , ""   // Order ID
                            , ""   // Order No
                            , ""   // Order Date
                            , cursor.getString(cursor.getColumnIndex(REPORT_TRANSACTION_ID))
                            , EntryDateTime  // Entry Date Time
                            , cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))     // Party ID
                            , cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))   // Party Name
                            , cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))
                            , map.get("TotalQty")
                            , map.get("TotalAmt")
                    ));
                } else if (FlagType == 4) {
                    //TODO: Order Wise
                    EntryDateTime = getEntryDateTime(FlagType,"","",cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
                    map = getTotalQtyAmt(FlagType,"","",cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
                    dataList.add(new RevertForceCloseDataset(
                              ""   // Item ID
                            , ""   // Item Code
                            , ""   // Item Name
                            , cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))   // OrderID
                            , cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))   // Order No
                            , ""   // Order Date
                            , cursor.getString(cursor.getColumnIndex(REPORT_TRANSACTION_ID))
                            , EntryDateTime  // Entry Date Time
                            , ""     // Party ID
                            , ""   // Party Name
                            , cursor.getInt(cursor.getColumnIndex(REPORT_FLAG_TYPE))
                            , map.get("TotalQty")
                            , map.get("TotalAmt")
                    ));
                }
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    //TODO: Get Total Quantity and Total Amount
    private Map<String,Integer> getTotalQtyAmt(int FlagType,String ItemID,String CustID,String OrderID){
        Map<String,Integer> map=new HashMap<>();
        String selectQuery = "";
        // Select All Query
        if (FlagType == 0){
            //TODO: Item Wise
            selectQuery = "SELECT SUM("+REPORT_ITEM_BOOK_AMT+") as Amt,SUM("+REPORT_ITEM_BOOK_QTY+") as BookQty from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' ";
        }else if (FlagType == 1){
            //TODO: Item with Order Wise
            selectQuery = "SELECT SUM("+REPORT_ITEM_BOOK_AMT+") as Amt,SUM("+REPORT_ITEM_BOOK_QTY+") as BookQty from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"' ";
        }else if (FlagType == 2){
            //TODO: Item with Customer Wise
            selectQuery = "SELECT SUM("+REPORT_ITEM_BOOK_AMT+") as Amt,SUM("+REPORT_ITEM_BOOK_QTY+") as BookQty from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_PARTY_ID+"='"+CustID+"' ";
        }else if (FlagType == 3){
            //TODO: Customer Wise
            selectQuery = "SELECT "+REPORT_ITEM_BOOK_AMT+" as Amt,"+REPORT_ITEM_BOOK_QTY+" as BookQty from " + REPORT_TABLE+" WHERE "+REPORT_PARTY_ID+"='"+CustID+"' ";
        }else if (FlagType == 4){
            //TODO: Order Wise
            selectQuery = "SELECT "+REPORT_ITEM_BOOK_AMT+" as Amt,"+REPORT_ITEM_BOOK_QTY+" as BookQty from " + REPORT_TABLE+" WHERE "+REPORT_ORDER_ID+"='"+OrderID+"' ";
        }
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            map.put("TotalQty", cursor.getInt(cursor.getColumnIndex("BookQty")));
            map.put("TotalAmt", cursor.getInt(cursor.getColumnIndex("Amt")));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning 
        return map;
    }
    private String getEntryDateTime(int FlagType,String ItemID,String CustID,String OrderID){
        String EntryDateTime = "";
        String selectQuery = "";
        // Select All Query
        if (FlagType == 0){
            //TODO: Item Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' ORDER BY "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 1){
            //TODO: Item with Order Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"'  ORDER BY "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 2){
            //TODO: Item with Customer Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_PARTY_ID+"='"+CustID+"'  ORDER BY "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 3){
            //TODO: Customer Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_PARTY_ID+"='"+CustID+"'  ORDER BY "+REPORT_ENTRY_DATE_TIME+" DESC";
        }else if (FlagType == 4){
            //TODO: Order Wise
            selectQuery = "SELECT DISTINCT "+REPORT_ENTRY_DATE_TIME+" from " + REPORT_TABLE+" WHERE "+REPORT_ORDER_ID+"='"+OrderID+"'  ORDER BY "+REPORT_ENTRY_DATE_TIME+" DESC";
        }
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            EntryDateTime = cursor.getString(cursor.getColumnIndex(REPORT_ENTRY_DATE_TIME));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return EntryDateTime;
    }
    //TODO: delete By Transaction
    public void deleteByTransaction(String TransactionID,int Flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE,REPORT_TRANSACTION_ID+"= '"+TransactionID+"' AND "+REPORT_FLAG_TYPE+"= '"+Flag+"' " ,null);
        db.close();
    }
}