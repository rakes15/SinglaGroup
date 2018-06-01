package stockcheck.DatabaseSqLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseSqlLiteHandlerInTransit extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
	private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_InTransit";
 
    //----------------------------Table's Name--------------------------------------------------------------
    private static final String IN_TRANSIT_TABLE = "inTransit";
    //-------------------Change Party Old Table Column Names---------------------------------------------
    private static final String IN_TRANSIT_KEY_ID = "id";
    private static final String IN_TRANSIT_IBI_NO = "ibnNo";
    private static final String IN_TRANSIT_VDATE = "vDate";
    private static final String IN_TRANSIT_BRANCH_NAME = "branchName";
    private static final String IN_TRANSIT_TO_BRANCH_NAME = "toBranchName";
    private static final String IN_TRANSIT_DIVISION_ID = "divisionId";
    private static final String IN_TRANSIT_OVER_DUE_DAYS = "overDueDays";
    private static final String IN_TRANSIT_ITEM_CODE = "itemCode";
    private static final String IN_TRANSIT_ITEM_NAME = "itemName";
    private static final String IN_TRANSIT_SUB_GROUP_NAME = "subGroupName";
    private static final String IN_TRANSIT_SUB_ITEM_NAME = "subItemName";
    private static final String IN_TRANSIT_UNIT_NAME = "unitName";
    private static final String IN_TRANSIT_STOCK_QTY = "stockQty";
    private static final String IN_TRANSIT_BARCODE = "barcode";
    private static final String IN_TRANSIT_GODOWN_NAME = "godownName";
    private static final String IN_TRANSIT_TO_GODOWN_NAME = "toGodownName";
    private static final String IN_TRANSIT_IBR_NO = "ibrNo";
    private static final String IN_TRANSIT_IBR_STOCK_QTY = "ibrStockQty";
    private static final String IN_TRANSIT_PENDING_QTY = "pendingQty";
    private static final String IN_TRANSIT_SUB_DET_ATTR1 = "subDetAttr1";
    private static final String IN_TRANSIT_ITEM_ID = "itemId";
    private static final String IN_TRANSIT_COLOR_NAME = "colorName";
    private static final String IN_TRANSIT_SIZE_NAME = "sizeName";
    private static final String IN_TRANSIT_SUB_ITEM_ID = "subItemId";
    private static final String IN_TRANSIT_COLOR_ID = "colorID";
    private static final String IN_TRANSIT_SIZE_ID = "sizeId";
    private static final String IN_TRANSIT_MD_APPLICABLE = "mdApplicable";
    private static final String IN_TRANSIT_SUB_ITEM_APPLICABLE = "subItemApplicable";
    
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerInTransit(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_IN_TRANSIT_TABLE = "CREATE TABLE " + IN_TRANSIT_TABLE + "("+ IN_TRANSIT_KEY_ID + " INTEGER PRIMARY KEY," + IN_TRANSIT_IBI_NO + " TEXT," + IN_TRANSIT_VDATE + " TEXT," + IN_TRANSIT_BRANCH_NAME + " TEXT," + IN_TRANSIT_TO_BRANCH_NAME + " TEXT," + IN_TRANSIT_DIVISION_ID + " TEXT," + IN_TRANSIT_OVER_DUE_DAYS + " TEXT," + IN_TRANSIT_ITEM_CODE + " TEXT," + IN_TRANSIT_ITEM_NAME + " TEXT," + IN_TRANSIT_SUB_GROUP_NAME + " TEXT," + IN_TRANSIT_SUB_ITEM_NAME + " TEXT," + IN_TRANSIT_UNIT_NAME + " TEXT," + IN_TRANSIT_STOCK_QTY + " TEXT," + IN_TRANSIT_BARCODE + " TEXT," + IN_TRANSIT_GODOWN_NAME + " TEXT," + IN_TRANSIT_TO_GODOWN_NAME + " TEXT," + IN_TRANSIT_IBR_NO + " TEXT," + IN_TRANSIT_IBR_STOCK_QTY + " TEXT," + IN_TRANSIT_PENDING_QTY + " TEXT," + IN_TRANSIT_SUB_DET_ATTR1 + " TEXT," + IN_TRANSIT_ITEM_ID + " TEXT," + IN_TRANSIT_COLOR_NAME + " TEXT," + IN_TRANSIT_SIZE_NAME + " TEXT," + IN_TRANSIT_SUB_ITEM_ID + " TEXT," + IN_TRANSIT_COLOR_ID + " TEXT," + IN_TRANSIT_SIZE_ID + " TEXT," + IN_TRANSIT_MD_APPLICABLE + " INTEGER," + IN_TRANSIT_SUB_ITEM_APPLICABLE + " INTEGER) ";
    	db.execSQL(CREATE_IN_TRANSIT_TABLE);
    }   
 //------------------------------------ Upgrading database-------------------------------------------------------------
    @SuppressLint("NewApi")
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +IN_TRANSIT_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteInTransit()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_TRANSIT_TABLE,null,null);
        db.close();
    }
    //---------------------------------- Inserting Data of Change Party New Table---------------------------------------
    @SuppressWarnings("deprecation")
	public void insertInTransitData(List<Map<String,String>> list){
    	
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,IN_TRANSIT_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int IBINo = ih.getColumnIndex(IN_TRANSIT_IBI_NO);
        final int VDate = ih.getColumnIndex(IN_TRANSIT_VDATE);
        final int BranchName = ih.getColumnIndex(IN_TRANSIT_BRANCH_NAME);
        final int ToBranchName = ih.getColumnIndex(IN_TRANSIT_TO_BRANCH_NAME);
        final int DivisionID = ih.getColumnIndex(IN_TRANSIT_DIVISION_ID);
        final int OverDueDay = ih.getColumnIndex(IN_TRANSIT_OVER_DUE_DAYS);
        final int ItemCOde = ih.getColumnIndex(IN_TRANSIT_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(IN_TRANSIT_ITEM_NAME);
        final int SubGroupName = ih.getColumnIndex(IN_TRANSIT_SUB_GROUP_NAME);
        final int SubItemName = ih.getColumnIndex(IN_TRANSIT_SUB_ITEM_NAME);
        final int UnitName = ih.getColumnIndex(IN_TRANSIT_UNIT_NAME);
        final int StockQty = ih.getColumnIndex(IN_TRANSIT_STOCK_QTY);
        final int Barcode = ih.getColumnIndex(IN_TRANSIT_BARCODE);
        final int GodownName = ih.getColumnIndex(IN_TRANSIT_GODOWN_NAME);
        final int ToGodownName = ih.getColumnIndex(IN_TRANSIT_TO_GODOWN_NAME);
        final int IBRNo = ih.getColumnIndex(IN_TRANSIT_IBR_NO);
        final int IBRStockQty = ih.getColumnIndex(IN_TRANSIT_IBR_STOCK_QTY);
        final int PendingQty = ih.getColumnIndex(IN_TRANSIT_PENDING_QTY);
        final int SubDetAttrib1 = ih.getColumnIndex(IN_TRANSIT_SUB_DET_ATTR1);
        final int ItemID = ih.getColumnIndex(IN_TRANSIT_ITEM_ID);
        final int ColorName = ih.getColumnIndex(IN_TRANSIT_COLOR_NAME);
        final int SizeName = ih.getColumnIndex(IN_TRANSIT_SIZE_NAME);
        final int SubItemID = ih.getColumnIndex(IN_TRANSIT_SUB_ITEM_ID);
        final int ColorID = ih.getColumnIndex(IN_TRANSIT_COLOR_ID);
        final int SizeID = ih.getColumnIndex(IN_TRANSIT_SIZE_ID);
        final int MDApplicable = ih.getColumnIndex(IN_TRANSIT_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(IN_TRANSIT_SUB_ITEM_APPLICABLE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(IBINo, list.get(x).get("IBINo"));
                ih.bind(VDate, list.get(x).get("VDate"));
                ih.bind(BranchName, list.get(x).get("BranchName"));
                ih.bind(ToBranchName, list.get(x).get("ToBranchName"));
                ih.bind(DivisionID, list.get(x).get("DivisionID"));
                ih.bind(OverDueDay, list.get(x).get("OverDueDay"));
                ih.bind(ItemCOde, list.get(x).get("ItemCOde"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(SubGroupName, list.get(x).get("SubGroupName"));
                ih.bind(SubItemName, list.get(x).get("SubItemName"));
                ih.bind(UnitName, list.get(x).get("UnitName"));
                ih.bind(StockQty, list.get(x).get("StockQty"));
                ih.bind(Barcode, list.get(x).get("Barcode"));
                ih.bind(GodownName, list.get(x).get("GodownName"));
                ih.bind(ToGodownName, list.get(x).get("ToGodownName"));
                ih.bind(IBRNo, list.get(x).get("IBRNo"));
                ih.bind(IBRStockQty, list.get(x).get("IBRStockQty"));
                ih.bind(PendingQty, list.get(x).get("PendingQty"));
                ih.bind(SubDetAttrib1, list.get(x).get("SubDetAttrib1"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ColorName, list.get(x).get("ColorName"));
                ih.bind(SizeName, list.get(x).get("SizeName"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(MDApplicable, list.get(x).get("MDApplicable"));
                ih.bind(SubItemApplicable, list.get(x).get("SubItemApplicable"));
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
    public List<Map<String,String>> getInTransitDetails(){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct "+IN_TRANSIT_IBI_NO+","+IN_TRANSIT_BRANCH_NAME+","+IN_TRANSIT_VDATE+","+IN_TRANSIT_OVER_DUE_DAYS+","+IN_TRANSIT_TO_BRANCH_NAME+","+IN_TRANSIT_ITEM_CODE+","+IN_TRANSIT_ITEM_NAME+","+IN_TRANSIT_ITEM_ID+","+IN_TRANSIT_GODOWN_NAME+","+IN_TRANSIT_TO_GODOWN_NAME+","+IN_TRANSIT_SUB_GROUP_NAME+","+IN_TRANSIT_MD_APPLICABLE+","+IN_TRANSIT_SUB_ITEM_APPLICABLE+" from " + IN_TRANSIT_TABLE; 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("IBINO", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_IBI_NO)));
            	map.put("BranchName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_BRANCH_NAME)));
            	map.put("VDate", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_VDATE)));
            	map.put("OverDueDays", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_OVER_DUE_DAYS)));
            	map.put("ToBranchName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_TO_BRANCH_NAME)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_ITEM_CODE)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_ITEM_NAME)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_ITEM_ID)));
            	map.put("Godown", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_GODOWN_NAME)));
            	map.put("ToGodown", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_TO_GODOWN_NAME)));
            	map.put("SubGroupName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SUB_GROUP_NAME)));
            	map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_MD_APPLICABLE)));
            	map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SUB_ITEM_APPLICABLE)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public List<Map<String,String>> getInTransitColorList(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct "+IN_TRANSIT_COLOR_ID+","+IN_TRANSIT_COLOR_NAME+" from " + IN_TRANSIT_TABLE + " WHERE "+IN_TRANSIT_ITEM_ID+"='"+ItemID+"' "; 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_COLOR_NAME)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public List<Map<String,String>> getInTransitSizeList(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct "+IN_TRANSIT_SIZE_ID+","+IN_TRANSIT_SIZE_NAME+" from " + IN_TRANSIT_TABLE + " WHERE "+IN_TRANSIT_ITEM_ID+"='"+ItemID+"' "; 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SIZE_NAME)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public int getBookQty(String ItemID,String SizeID,String ColorID){
    	// Select All Query
        String selectQuery = "Select "+IN_TRANSIT_PENDING_QTY+" from " + IN_TRANSIT_TABLE+" where "+IN_TRANSIT_ITEM_ID+"= '"+ItemID+"' and "+IN_TRANSIT_SIZE_ID+"= '"+SizeID+"' and "+IN_TRANSIT_COLOR_ID+"= '"+ColorID+"'  ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        int Qty=0;
        if(cursor.getCount()>0)
        {
	        cursor.moveToFirst();
	        Qty=cursor.getInt(cursor.getColumnIndex(IN_TRANSIT_PENDING_QTY));
	        // closing connection
	        cursor.close();
	        db.close();
    	}
    	// returning lables
    	return Qty;
    }
    public List<Map<String,String>> getSubItemList(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_TRANSIT_SUB_ITEM_ID+"),"+IN_TRANSIT_SUB_ITEM_NAME+","+IN_TRANSIT_PENDING_QTY+","+IN_TRANSIT_BARCODE+","+IN_TRANSIT_OVER_DUE_DAYS+" from " + IN_TRANSIT_TABLE+" where "+IN_TRANSIT_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SubItemID", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SUB_ITEM_ID)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_SUB_ITEM_NAME)));
            	map.put("PendingQty", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_PENDING_QTY)));
            	map.put("Barcode", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_BARCODE)));
            	map.put("OverDueDays", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_OVER_DUE_DAYS)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getWithoutSubItemList(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_TRANSIT_ITEM_ID+"),"+IN_TRANSIT_ITEM_NAME+","+IN_TRANSIT_PENDING_QTY+","+IN_TRANSIT_OVER_DUE_DAYS+" from " + IN_TRANSIT_TABLE+" where "+IN_TRANSIT_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_ITEM_NAME)));
            	map.put("PendingQty", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_PENDING_QTY)));
            	map.put("OverDueDays", cursor.getString(cursor.getColumnIndex(IN_TRANSIT_OVER_DUE_DAYS)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
}