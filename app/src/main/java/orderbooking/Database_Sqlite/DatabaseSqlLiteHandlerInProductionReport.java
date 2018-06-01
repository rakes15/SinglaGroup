package orderbooking.Database_Sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseSqlLiteHandlerInProductionReport extends SQLiteOpenHelper {
    //TODO: ----------------------- Database Version----------------------
	private static final int DATABASE_VERSION = 1;
 
    //TODO: -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_InProduction";
    //TODO: ----------------------------Table's Name--------------------------------------------------------------
    private static final String IN_PRODUCTION_TABLE = "inProduction";
    //TODO: -------------------In Production Table Column Names---------------------------------------------
    private static final String IN_PRODUCTION_KEY_ID = "id";
    private static final String IN_PRODUCTION_DIVISION_ID = "divisionId";
    private static final String IN_PRODUCTION_ORDER_ID = "orderId";
    private static final String IN_PRODUCTION_COMBINE_VNO = "combineVNo";
    private static final String IN_PRODUCTION_VDATE = "vDate";
    private static final String IN_PRODUCTION_PARTY_ID = "partyId";
    private static final String IN_PRODUCTION_PARTY = "partyName";
    private static final String IN_PRODUCTION_SUB_PARTY_ID = "subPartyId";
    private static final String IN_PRODUCTION_SUB_PARTY = "subPartyName";
    private static final String IN_PRODUCTION_PENDING_SINCE = "pendingSince";
    private static final String IN_PRODUCTION_EXPECTED_DEL_DAYS = "expectedDelDays";
    private static final String IN_PRODUCTION_EXPECTED_DEL_DATE = "expextedDelDate";
    private static final String IN_PRODUCTION_ROW_ID = "rowId";
    private static final String IN_PRODUCTION_ISSUE_ITEM_ID = "issueItemId";
    private static final String IN_PRODUCTION_ISSUE_ITEM = "issueItem";
    private static final String IN_PRODUCTION_ITEM_ID = "itemId";
    private static final String IN_PRODUCTION_ITEM = "itemName";
    private static final String IN_PRODUCTION_ITEM_CODE = "itemCode";
    private static final String IN_PRODUCTION_SUB_ITEM_ID = "subItemId";
    private static final String IN_PRODUCTION_SUB_ITEM = "subItemName";
    private static final String IN_PRODUCTION_PRODUCTION_QTY = "productionQty";
    private static final String IN_PRODUCTION_REPT_STOCK_QTY = "reptStockQty";
    private static final String IN_PRODUCTION_ITEM_DOC_QTY = "itemDocQty";
    private static final String IN_PRODUCTION_REPT_ITEM_STOCK_QTY = "reptItemStockQty";
    private static final String IN_PRODUCTION_SIZE_ID = "sizeId";
    private static final String IN_PRODUCTION_SIZE = "sizeName";
    private static final String IN_PRODUCTION_COLOR_ID = "colorID";
    private static final String IN_PRODUCTION_COLOR = "colorName";
    private static final String IN_PRODUCTION_SUB_GROUP_ID = "subGroupId";
    private static final String IN_PRODUCTION_SUB_GROUP = "subGroup";
    private static final String IN_PRODUCTION_GROUP_ID = "groupId";
    private static final String IN_PRODUCTION_GROUP = "groupName";
    private static final String IN_PRODUCTION_MAIN_GROUP = "mainGroup";
    private static final String IN_PRODUCTION_SUBITEM_APPLICABLE = "subItemApplicable";
    private static final String IN_PRODUCTION_MD_APPLICABLE = "mdApplicable";
    //TODO:	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerInProductionReport(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    //TODO: -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	//TODO: Category table create query
    	String CREATE_IN_PRODUCTION_TABLE = "CREATE TABLE " + IN_PRODUCTION_TABLE + "("+ IN_PRODUCTION_KEY_ID + " INTEGER PRIMARY KEY," + IN_PRODUCTION_DIVISION_ID + " TEXT," + IN_PRODUCTION_ORDER_ID + " TEXT," + IN_PRODUCTION_COMBINE_VNO + " TEXT," + IN_PRODUCTION_VDATE + " TEXT," + IN_PRODUCTION_PARTY_ID + " TEXT," + IN_PRODUCTION_PARTY + " TEXT," + IN_PRODUCTION_SUB_PARTY_ID + " TEXT," + IN_PRODUCTION_SUB_PARTY + " TEXT," + IN_PRODUCTION_PENDING_SINCE + " INTEGER," + IN_PRODUCTION_EXPECTED_DEL_DAYS + " TEXT," + IN_PRODUCTION_EXPECTED_DEL_DATE + " TEXT," + IN_PRODUCTION_ROW_ID + " TEXT," + IN_PRODUCTION_ISSUE_ITEM_ID + " TEXT," + IN_PRODUCTION_ISSUE_ITEM + " TEXT," + IN_PRODUCTION_ITEM_ID + " TEXT," + IN_PRODUCTION_ITEM + " TEXT," + IN_PRODUCTION_ITEM_CODE + " TEXT," + IN_PRODUCTION_SUB_ITEM_ID + " TEXT," + IN_PRODUCTION_SUB_ITEM + " TEXT," + IN_PRODUCTION_PRODUCTION_QTY + " INTEGER," + IN_PRODUCTION_REPT_STOCK_QTY + " INTEGER," + IN_PRODUCTION_ITEM_DOC_QTY + " INTEGER," + IN_PRODUCTION_REPT_ITEM_STOCK_QTY + " INTEGER," + IN_PRODUCTION_SIZE_ID + " TEXT," + IN_PRODUCTION_SIZE + " TEXT," + IN_PRODUCTION_COLOR_ID + " TEXT," + IN_PRODUCTION_COLOR + " TEXT," + IN_PRODUCTION_SUB_GROUP_ID + " TEXT," + IN_PRODUCTION_SUB_GROUP + " TEXT," + IN_PRODUCTION_GROUP_ID + " TEXT," + IN_PRODUCTION_GROUP + " TEXT," + IN_PRODUCTION_MAIN_GROUP + " TEXT," + IN_PRODUCTION_SUBITEM_APPLICABLE + " INTEGER," + IN_PRODUCTION_MD_APPLICABLE + " INTEGER) ";
    	db.execSQL(CREATE_IN_PRODUCTION_TABLE);
    }   
   //TODO: ------------------------------------ Upgrading database-------------------------------------------------------------
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +IN_PRODUCTION_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteInProduction(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,null,null);
        db.close();
    }
    //TODO: ---------------------------------- Inserting Data of In Production Table---------------------------------------
	public void insertInProductionTable(List<Map<String,String>> list){
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,IN_PRODUCTION_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int DivisionID = ih.getColumnIndex(IN_PRODUCTION_DIVISION_ID);
        final int OrderID = ih.getColumnIndex(IN_PRODUCTION_ORDER_ID);
        final int CombinedVNo = ih.getColumnIndex(IN_PRODUCTION_COMBINE_VNO);
        final int VDate = ih.getColumnIndex(IN_PRODUCTION_VDATE);
        final int PartyID = ih.getColumnIndex(IN_PRODUCTION_PARTY_ID);
        final int PartyName = ih.getColumnIndex(IN_PRODUCTION_PARTY);
        final int SubPartyID = ih.getColumnIndex(IN_PRODUCTION_SUB_PARTY_ID);
        final int SubPartyName = ih.getColumnIndex(IN_PRODUCTION_SUB_PARTY);
        final int PendingSince = ih.getColumnIndex(IN_PRODUCTION_PENDING_SINCE);
        final int ExpectedDeliveryDate = ih.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE);
        final int ExpectedDeliveryDays = ih.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DAYS);
        final int RowID = ih.getColumnIndex(IN_PRODUCTION_ROW_ID);
        final int IssueItemID = ih.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM_ID);
        final int IssueItem = ih.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM);
        final int ItemID = ih.getColumnIndex(IN_PRODUCTION_ITEM_ID);
        final int ItemName = ih.getColumnIndex(IN_PRODUCTION_ITEM);
        final int ItemCode = ih.getColumnIndex(IN_PRODUCTION_ITEM_CODE);
        final int SubItemID = ih.getColumnIndex(IN_PRODUCTION_SUB_ITEM_ID);
        final int SubItem = ih.getColumnIndex(IN_PRODUCTION_SUB_ITEM);
        final int ProductionQty = ih.getColumnIndex(IN_PRODUCTION_PRODUCTION_QTY);
        final int ReptStockQty = ih.getColumnIndex(IN_PRODUCTION_REPT_STOCK_QTY);
        final int ItemDocQty = ih.getColumnIndex(IN_PRODUCTION_ITEM_DOC_QTY);
        final int ReptItemStockQty = ih.getColumnIndex(IN_PRODUCTION_REPT_ITEM_STOCK_QTY);
        final int SizeID = ih.getColumnIndex(IN_PRODUCTION_SIZE_ID);
        final int SizeName = ih.getColumnIndex(IN_PRODUCTION_SIZE);
        final int ColorID = ih.getColumnIndex(IN_PRODUCTION_COLOR_ID);
        final int ColorName = ih.getColumnIndex(IN_PRODUCTION_COLOR);
        final int SubGroupID = ih.getColumnIndex(IN_PRODUCTION_SUB_GROUP_ID);
        final int SubGroup = ih.getColumnIndex(IN_PRODUCTION_SUB_GROUP);
        final int GroupID = ih.getColumnIndex(IN_PRODUCTION_GROUP_ID);
        final int Group = ih.getColumnIndex(IN_PRODUCTION_GROUP);
        final int MainGroup = ih.getColumnIndex(IN_PRODUCTION_MAIN_GROUP);
        final int SubItemApplicable = ih.getColumnIndex(IN_PRODUCTION_SUBITEM_APPLICABLE);
        final int MDApplicable = ih.getColumnIndex(IN_PRODUCTION_MD_APPLICABLE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(DivisionID, list.get(x).get("DivisionID"));
                ih.bind(OrderID, list.get(x).get("OrderID"));
                ih.bind(CombinedVNo, list.get(x).get("CombinedVNo"));
                ih.bind(VDate, list.get(x).get("VDate"));
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(PartyName, list.get(x).get("PartyName"));
                ih.bind(SubPartyID, list.get(x).get("SubPartyID"));
                ih.bind(SubPartyName, list.get(x).get("SubPartyName"));
                ih.bind(PendingSince, list.get(x).get("PendingSince"));
                ih.bind(ExpectedDeliveryDate, list.get(x).get("ExpectedDeliveryDate"));
                ih.bind(ExpectedDeliveryDays, list.get(x).get("ExpectedDeliveryDays"));
                ih.bind(RowID, list.get(x).get("RowID"));
                ih.bind(IssueItemID, list.get(x).get("IssueItemID"));
                ih.bind(IssueItem, list.get(x).get("IssueItem"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(SubItem, list.get(x).get("SubItem"));
                ih.bind(ProductionQty, list.get(x).get("ProductionQty"));
                ih.bind(ReptStockQty, list.get(x).get("ReptStockQty"));
                ih.bind(ItemDocQty, list.get(x).get("ItemDocQty"));
                ih.bind(ReptItemStockQty, list.get(x).get("ReptItemStockQty"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(SizeName, list.get(x).get("SizeName"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(ColorName, list.get(x).get("ColorName"));
                ih.bind(SubGroupID, list.get(x).get("SubGroupID"));
                ih.bind(SubGroup, list.get(x).get("SubGroup"));
                ih.bind(GroupID, list.get(x).get("GroupID"));
                ih.bind(Group, list.get(x).get("Group"));
                ih.bind(MainGroup, list.get(x).get("MainGroup"));
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
    public List<Map<String,String>> getOrdersList(){
    	List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select distinct "+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_COMBINE_VNO+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_PENDING_SINCE+","+IN_PRODUCTION_EXPECTED_DEL_DATE+","+IN_PRODUCTION_EXPECTED_DEL_DAYS+","+IN_PRODUCTION_PARTY_ID+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY_ID+","+IN_PRODUCTION_SUB_PARTY+"  from " + IN_PRODUCTION_TABLE + " Where ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0"; 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("CombinedVNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("VDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	map.put("PendingSince", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PENDING_SINCE)));
            	map.put("ExDelDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	map.put("ExDelDays", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DAYS)));
            	map.put("PartyID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY_ID)));
            	map.put("PartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubPartyID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY_ID)));
            	map.put("SubPartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	dataList.add(map);
        	}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public List<Map<String,String>> getItemList(String OrderID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_PRODUCTION_ITEM_ID+"),"+IN_PRODUCTION_ISSUE_ITEM_ID+","+IN_PRODUCTION_ITEM_CODE+","+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_SUB_GROUP+","+IN_PRODUCTION_GROUP+","+IN_PRODUCTION_MAIN_GROUP+","+IN_PRODUCTION_MD_APPLICABLE+","+IN_PRODUCTION_SUBITEM_APPLICABLE+"  from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("IssueItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	map.put("SubGroup", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_GROUP)));
            	map.put("Group", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("MainGroup", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MAIN_GROUP)));
            	map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MD_APPLICABLE)));
            	map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUBITEM_APPLICABLE)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getSizeList(String OrderID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_PRODUCTION_SIZE_ID+"),"+IN_PRODUCTION_SIZE+" from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getColorList(String OrderID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_PRODUCTION_COLOR_ID+"),"+IN_PRODUCTION_COLOR+" from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public int getMDPendingQty(String OrderID,String ItemID,String SizeID,String ColorID){
    	// Select All Query
        String selectQuery = "Select ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as MDPendingQty ,"+IN_PRODUCTION_PRODUCTION_QTY+" from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' and "+IN_PRODUCTION_ITEM_ID+"= '"+ItemID+"' and "+IN_PRODUCTION_SIZE_ID+"= '"+SizeID+"' and "+IN_PRODUCTION_COLOR_ID+"= '"+ColorID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        int Qty=0;
        if(cursor.getCount()>0)
        {
	        cursor.moveToFirst();
	        Qty=cursor.getInt(cursor.getColumnIndex("MDPendingQty"));
	        // closing connection
	        cursor.close();
	        db.close();
    	}
    	// returning lables
    	return Qty;
    }
    public List<Map<String, String>> getProductionPendingQty(String OrderID,String ItemID,String SizeID,String ColorID){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
    	// Select All Query
    	String selectQuery = "Select ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as MDPendingQty ,"+IN_PRODUCTION_PRODUCTION_QTY+" from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' and "+IN_PRODUCTION_ITEM_ID+"= '"+ItemID+"' and "+IN_PRODUCTION_SIZE_ID+"= '"+SizeID+"' and "+IN_PRODUCTION_COLOR_ID+"= '"+ColorID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("MDPendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("MDPendingQty"))));
            	map.put("ProductionQty", String.valueOf(cursor.getInt(cursor.getColumnIndex(IN_PRODUCTION_PRODUCTION_QTY))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
        }
        //System.out.println("List:"+dataList.toString());
    	// returning lables
    	return dataList;
    }
    public List<Map<String,String>> getSubItemList(String OrderID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_PRODUCTION_SUB_ITEM_ID+"),"+IN_PRODUCTION_SUB_ITEM+",("+IN_PRODUCTION_REPT_ITEM_STOCK_QTY+"-"+IN_PRODUCTION_ITEM_DOC_QTY+") as PendingQty from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND ("+IN_PRODUCTION_REPT_ITEM_STOCK_QTY+"-"+IN_PRODUCTION_ITEM_DOC_QTY+")>0 ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SubItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM_ID)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("PendingQty", cursor.getString(cursor.getColumnIndex("PendingQty")));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getWithoutSubItemList(String OrderID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+IN_PRODUCTION_ITEM_ID+"),"+IN_PRODUCTION_ITEM+",("+IN_PRODUCTION_REPT_ITEM_STOCK_QTY+"-"+IN_PRODUCTION_ITEM_DOC_QTY+") as PendingQty from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND ("+IN_PRODUCTION_REPT_ITEM_STOCK_QTY+"-"+IN_PRODUCTION_ITEM_DOC_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("PendingQty", cursor.getString(cursor.getColumnIndex("PendingQty")));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
     
     
    //TODO: In Production
    public List<Map<String,String>> getGroupNameList(){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_GROUP+"),"+IN_PRODUCTION_MAIN_GROUP+" from " + IN_PRODUCTION_TABLE + " WHERE ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MAIN_GROUP)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getCustomerList(){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_PARTY_ID+"),"+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY_ID+","+IN_PRODUCTION_SUB_PARTY+" from " + IN_PRODUCTION_TABLE + " WHERE ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("CustID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY_ID)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubCustID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY_ID)));
            	map.put("SubCustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getOrderList(){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ORDER_ID+"),"+IN_PRODUCTION_COMBINE_VNO+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY+","+IN_PRODUCTION_PENDING_SINCE+","+IN_PRODUCTION_EXPECTED_DEL_DATE+","+IN_PRODUCTION_EXPECTED_DEL_DAYS+"  from " + IN_PRODUCTION_TABLE + " WHERE ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ORDER BY "+IN_PRODUCTION_VDATE+ " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubCustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	map.put("PendingSince", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PENDING_SINCE)));
            	map.put("ExDelDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	map.put("ExDelDays", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DAYS)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Date DESC:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getOrderList(String Date){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COMBINE_VNO+"),"+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY+","+IN_PRODUCTION_PENDING_SINCE+","+IN_PRODUCTION_EXPECTED_DEL_DATE+","+IN_PRODUCTION_EXPECTED_DEL_DAYS+" from " + IN_PRODUCTION_TABLE + " WHERE ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ORDER BY "+IN_PRODUCTION_VDATE+ " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	//map.put("AStatus", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_A_STATUS)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubCustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	map.put("PendingSince", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PENDING_SINCE)));
            	map.put("ExDelDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	map.put("ExDelDays", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DAYS)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Date ASC:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getOrderListCustomerWise(){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COMBINE_VNO+"),"+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY+","+IN_PRODUCTION_PENDING_SINCE+","+IN_PRODUCTION_EXPECTED_DEL_DATE+","+IN_PRODUCTION_EXPECTED_DEL_DAYS+" from " + IN_PRODUCTION_TABLE + " WHERE ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ORDER BY "+IN_PRODUCTION_PARTY+ " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	//map.put("AStatus", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_A_STATUS)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubCustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	map.put("PendingSince", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PENDING_SINCE)));
            	map.put("ExDelDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	map.put("ExDelDays", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DAYS)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        ///System.out.println("Cust:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getOrderWiseDataCount(String OrderID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query  "+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+"
        String selectQuery = "SELECT COUNT(DISTINCT("+IN_PRODUCTION_ITEM_ID+")) as TotalItems,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as totalProductionQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("TotalItems", cursor.getString(cursor.getColumnIndex("TotalItems")));
            	map.put("TotalPendingQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	map.put("totalProductionQty", cursor.getString(cursor.getColumnIndex("totalProductionQty")));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getCustWiseDataCount(String PartyID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT("+IN_PRODUCTION_ITEM_ID+")) as TotalItems,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending,"+IN_PRODUCTION_VDATE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_PARTY_ID+"='"+PartyID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("TotalItems", cursor.getString(cursor.getColumnIndex("TotalItems")));
            	map.put("TotalQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	map.put("Date", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getGroupWiseDataCount(String GroupName,String MainGroupName){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT("+IN_PRODUCTION_ITEM_ID+")) as TotalItems ,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroupName+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 " ;
        System.out.println("Query: "+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("TotalItems", cursor.getString(cursor.getColumnIndex("TotalItems")));
            	map.put("TotalQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("DataList Group:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseData(String GroupName,String MainGroup){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+"),"+IN_PRODUCTION_ISSUE_ITEM_ID+","+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_SUB_ITEM+","+IN_PRODUCTION_GROUP+","+IN_PRODUCTION_VDATE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 GROUP BY  "+IN_PRODUCTION_ITEM_CODE+" ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("IssueItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
//            	map.put("Price", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PRICE)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("Date", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseDataPendingGreaterThenStock(String GroupName,String MainGroup){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+"),"+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_SUB_ITEM+","+IN_PRODUCTION_GROUP+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>"+IN_PRODUCTION_PRODUCTION_QTY+" AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        //System.out.println("Query: "+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseDataPendingLessThenStock(String GroupName,String MainGroup){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+"),"+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_SUB_ITEM+","+IN_PRODUCTION_GROUP+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND "+IN_PRODUCTION_ITEM_CODE+" NOT IN (SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")> "+IN_PRODUCTION_PRODUCTION_QTY+") AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        System.out.println("Query: "+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
//            	map.put("Price", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PRICE)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getOrderWiseCustomerNameWithTotalQty(String OrderNo,String ItemCode){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as totalOrderQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("TotalOrderQty", cursor.getString(cursor.getColumnIndex("totalOrderQty")));
            	map.put("TotalQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseSizeName(String ItemCode){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_SIZE+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseColorName(String ItemCode){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COLOR+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	map.put("ColorFamilyName", "No Color");
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String, String>> getPendingOrderStockQty(String ColorName,String SizeName,String ItemCode){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as PendingQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as BookedQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COLOR+"='"+ColorName+"' AND "+IN_PRODUCTION_SIZE+"='"+SizeName+"'AND "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        //System.out.println("Qry:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("PendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("PendingQty"))));
            	map.put("BookedQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("BookedQty"))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String, String>> getItemDetailsCustomerWiseWithTotalQty(String ItemCode){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COMBINE_VNO+"),"+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_PARTY_ID+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY_ID+","+IN_PRODUCTION_SUB_PARTY+","+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ISSUE_ITEM_ID+","+IN_PRODUCTION_ITEM_CODE+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_PENDING_SINCE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ORDER BY "+IN_PRODUCTION_PARTY+" ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("CustID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY_ID)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubCustID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY_ID)));
            	map.put("SubCustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("IssueItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM_ID)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	map.put("PendingsSince", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PENDING_SINCE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseSizeNameOrder(String ItemCode,String OrderNo){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_SIZE+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseColorNameOrder(String ItemCode,String OrderNo){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COLOR+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String, String>> getOrderWisePendingOrderStockQty(String ColorName,String SizeName,String ItemCode,String OrderNo){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as PendingQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as BookedQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COLOR+"='"+ColorName+"' AND "+IN_PRODUCTION_SIZE+"='"+SizeName+"' AND "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("PendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("PendingQty"))));
            	map.put("BookedQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("BookedQty"))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseGroupDataCount(String GroupName,String MainGroupName,String CustomerID){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT("+IN_PRODUCTION_ITEM_CODE+")) as TotalItems,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroupName+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustomerID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("TotalItems", cursor.getString(cursor.getColumnIndex("TotalItems")));
            	map.put("TotalQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
    	// returning 
    	return dataList;
    }    
    public List<Map<String,String>> getGroupNameListCustWise(String CustomerID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+IN_PRODUCTION_GROUP_ID+","+IN_PRODUCTION_GROUP+","+IN_PRODUCTION_MAIN_GROUP+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_PARTY_ID+"='"+CustomerID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("GroupID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP_ID)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MAIN_GROUP)));
//            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
//            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
//            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("PartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubPartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseData(String GroupName,String CustID,String MainGroup){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+"),"+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ISSUE_ITEM_ID+","+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_GROUP+","+IN_PRODUCTION_SUB_ITEM+","+IN_PRODUCTION_PARTY_ID+","+IN_PRODUCTION_VDATE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("IssueItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ISSUE_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("CustID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY_ID)));
            	map.put("Date", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("cust Data :"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseSizeNameCust(String ItemCode,String CustID){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_SIZE+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getCustomerWiseColorNameCust(String ItemCode,String CustID){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_COLOR+") from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String, String>> getCustomerWisePendingOrderStockQty(String ColorName,String SizeName,String ItemCode,String CustID){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as PendingQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as BookedQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COLOR+"='"+ColorName+"' AND "+IN_PRODUCTION_SIZE+"='"+SizeName+"' AND "+IN_PRODUCTION_ITEM_CODE+"='"+ItemCode+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("PendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("PendingQty"))));
            	map.put("BookedQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("BookedQty"))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getGroupNameListOrderWise(String OrderNo){
    	List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_GROUP+"),"+IN_PRODUCTION_GROUP_ID+","+IN_PRODUCTION_MAIN_GROUP+","+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_COMBINE_VNO+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_SUB_PARTY+","+IN_PRODUCTION_EXPECTED_DEL_DATE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("GroupID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP_ID)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MAIN_GROUP)));
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COMBINE_VNO)));
            	map.put("PartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("SubPartyName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_PARTY)));
            	map.put("ExDelDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("list:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getOrderWiseGroupDataCount(String GroupName,String MainGroupName,String OrderNo){
    	ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT("+IN_PRODUCTION_ITEM_CODE+")) as TotalItems,SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as totalPending  from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroupName+"' AND "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	//total=cursor.getInt(cursor.getColumnIndex("totalPending"));
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("TotalItems", cursor.getString(cursor.getColumnIndex("TotalItems")));
            	map.put("TotalQty", cursor.getString(cursor.getColumnIndex("totalPending")));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getOrderWiseData(String GroupName,String OrderNo,String MainGroup){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+IN_PRODUCTION_ITEM_CODE+"),"+IN_PRODUCTION_ORDER_ID+","+IN_PRODUCTION_PARTY+","+IN_PRODUCTION_VDATE+","+IN_PRODUCTION_ROW_ID+","+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ITEM+","+IN_PRODUCTION_SUB_ITEM+","+IN_PRODUCTION_GROUP_ID+","+IN_PRODUCTION_GROUP+","+IN_PRODUCTION_MAIN_GROUP+","+IN_PRODUCTION_EXPECTED_DEL_DATE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_GROUP+"='"+GroupName+"' AND "+IN_PRODUCTION_COMBINE_VNO+"='"+OrderNo+"' AND "+IN_PRODUCTION_MAIN_GROUP+"='"+MainGroup+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ORDER_ID)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_PARTY)));
            	map.put("RowID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ROW_ID)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_VDATE)));
            	map.put("GroupID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP_ID)));
            	map.put("GroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_GROUP)));
            	map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_MAIN_GROUP)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SUB_ITEM)));
            	map.put("ExDelDateTime", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_EXPECTED_DEL_DATE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connectiongetOrderWiseData
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }

    
    //TODO: Production Force close (delete)
    public void deleteItemWiseForceClose(String IssueItemID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_ISSUE_ITEM_ID+"= '"+IssueItemID+"' " ,null);
        db.close();
    }
    public void deleteItemWithOrderWiseForceClose(String IssueItemID,String OrderID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_ISSUE_ITEM_ID+"= '"+IssueItemID+"' AND "+IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"' " ,null);
        db.close();
    }
    public void deleteOrderWiseForceClose(String OrderID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"' " ,null);
        db.close();
    }
    public void deleteCustWiseForceClose(String CustID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_PARTY_ID+"= '"+CustID+"' " ,null);
        db.close();
    }
    public void deleteItemWiseOrderWithCustForceClose(String IssueItemID,String OrderID,String CustID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_ISSUE_ITEM_ID+"= '"+IssueItemID+"' AND "+IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"' AND "+IN_PRODUCTION_PARTY_ID+"= '"+CustID+"'" ,null);
        db.close();
    }
    public void deleteItemWiseWithCustForceClose(String IssueItemID,String CustID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IN_PRODUCTION_TABLE,IN_PRODUCTION_ISSUE_ITEM_ID+"= '"+IssueItemID+"' AND "+IN_PRODUCTION_PARTY_ID+"= '"+CustID+"' " ,null);
        db.close();
    }

    public void UpdateExpectedDeliveryDateTimeOrderWise(String OrderID,String ExDelDateTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IN_PRODUCTION_EXPECTED_DEL_DATE, ExDelDateTime);
        
        db.update(IN_PRODUCTION_TABLE,values,IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"' " ,null);
        db.close();
    }
    public void UpdateExpectedDeliveryDateTimeGroupWise(String OrderID,String GroupID,String ExDelDateTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IN_PRODUCTION_EXPECTED_DEL_DATE, ExDelDateTime);
        
        db.update(IN_PRODUCTION_TABLE,values,IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"'  AND "+IN_PRODUCTION_GROUP_ID+"= '"+GroupID+"' " ,null);
        db.close();
    }
    public void UpdateExpectedDeliveryDateTimeItemWise(String OrderID,String RowID,String ExDelDateTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IN_PRODUCTION_EXPECTED_DEL_DATE, ExDelDateTime);
        
        db.update(IN_PRODUCTION_TABLE,values,IN_PRODUCTION_ORDER_ID+"= '"+OrderID+"' AND "+IN_PRODUCTION_ROW_ID+"= '"+RowID+"'  " ,null);
        db.close();
    }

    public List<Map<String,String>> getItemList(String GroupID,String OrderID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
    	String selectQuery = "select Distinct "+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ITEM_CODE+","+IN_PRODUCTION_ITEM+"  from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseSize(String ItemID,String GroupID,String OrderID){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+IN_PRODUCTION_SIZE_ID+" , "+IN_PRODUCTION_SIZE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' AND "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"'  AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ORDER BY "+IN_PRODUCTION_SIZE+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseColor(String ItemID,String GroupID,String OrderID){
    	ArrayList<HashMap<String,String>> dataList =new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+IN_PRODUCTION_COLOR_ID+","+IN_PRODUCTION_COLOR+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' AND "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"'  AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }    
    public List<Map<String, String>> getBookQty(String ItemID,String SizeID,String ColorID,String GroupID,String OrderID){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
    	// Select All Query
    	String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as PendingQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as BookedQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COLOR_ID+"='"+ColorID+"' AND "+IN_PRODUCTION_SIZE_ID+"='"+SizeID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_ORDER_ID+"='"+OrderID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("PendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("PendingQty"))));
            	map.put("BookedQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("BookedQty"))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
        }
        //System.out.println("List:"+dataList.toString());
    	// returning lables
    	return dataList;
    }

    public List<Map<String,String>> getItemListCustWise(String GroupID,String CustID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
    	String selectQuery = "select Distinct "+IN_PRODUCTION_ITEM_ID+","+IN_PRODUCTION_ITEM_CODE+","+IN_PRODUCTION_ITEM+"  from " + IN_PRODUCTION_TABLE+" where "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_ITEM_CODE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseSizeByCust(String ItemID,String GroupID,String CustID){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+IN_PRODUCTION_SIZE_ID+" , "+IN_PRODUCTION_SIZE+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"'  AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0  ORDER BY "+IN_PRODUCTION_SIZE+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_SIZE)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getGroupWiseColorByCust(String ItemID,String GroupID,String CustID){
    	ArrayList<HashMap<String,String>> dataList =new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+IN_PRODUCTION_COLOR_ID+","+IN_PRODUCTION_COLOR+" from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_GROUP_ID+"='"+GroupID+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"'  AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(IN_PRODUCTION_COLOR)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }    
    public List<Map<String, String>> getBookQtyByCust(String ItemID,String SizeID,String ColorID,String GroupID,String CustID){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
    	// Select All Query
    	String selectQuery = "SELECT SUM("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+") as PendingQty,SUM("+IN_PRODUCTION_PRODUCTION_QTY+") as BookedQty from " + IN_PRODUCTION_TABLE+" WHERE "+IN_PRODUCTION_COLOR_ID+"='"+ColorID+"' AND "+IN_PRODUCTION_SIZE_ID+"='"+SizeID+"' AND "+IN_PRODUCTION_ITEM_ID+"='"+ItemID+"' AND "+IN_PRODUCTION_PARTY_ID+"='"+CustID+"' AND ("+IN_PRODUCTION_PRODUCTION_QTY+"-"+IN_PRODUCTION_REPT_STOCK_QTY+")>0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("PendingQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("PendingQty"))));
            	map.put("BookedQty", String.valueOf(cursor.getInt(cursor.getColumnIndex("BookedQty"))));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
        }
        //System.out.println("List:"+dataList.toString());
    	// returning lables
    	return dataList;
    }

}