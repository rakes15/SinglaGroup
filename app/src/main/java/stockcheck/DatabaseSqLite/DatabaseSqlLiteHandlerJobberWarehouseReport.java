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

public class DatabaseSqlLiteHandlerJobberWarehouseReport extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
	private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_JobberWarehouse";
 
    //----------------------------Table's Name--------------------------------------------------------------
    private static final String JOBBER_WAREHOUSE_TABLE = "jobberWarehouse";
    //-------------------Change Party Old Table Column Names---------------------------------------------
    private static final String JOBBER_WAREHOUSE_KEY_ID = "id";
    private static final String JOBBER_WAREHOUSE_DIVISION_ID = "divisionId";
    private static final String JOBBER_WAREHOUSE_DIVISION_NAME = "division";
    private static final String JOBBER_WAREHOUSE_GODOWN_ID = "godownId";
    private static final String JOBBER_WAREHOUSE_GODOWN_NAME = "godownName";
    private static final String JOBBER_WAREHOUSE_PARTY_ID = "partyId";
    private static final String JOBBER_WAREHOUSE_PARTY_NAME = "partyName";
    private static final String JOBBER_WAREHOUSE_ITEM_ID = "itemId";
    private static final String JOBBER_WAREHOUSE_ITEM_CODE = "itemCode";
    private static final String JOBBER_WAREHOUSE_SUB_ITEM_ID = "subItemId";
    private static final String JOBBER_WAREHOUSE_SUB_ITEM_NAME = "subItemName";
    private static final String JOBBER_WAREHOUSE_SUB_DET_ATTR_ID1 = "subDetAttrId1";
    private static final String JOBBER_WAREHOUSE_SUB_DETAILS = "subDetails";
    private static final String JOBBER_WAREHOUSE_ITEM_STOCK = "itemStock";
    private static final String JOBBER_WAREHOUSE_COLOR_ID = "colorID";
    private static final String JOBBER_WAREHOUSE_COLOR_NAME = "colorName";
    private static final String JOBBER_WAREHOUSE_SIZE_ID = "sizeId";
    private static final String JOBBER_WAREHOUSE_SIZE_NAME = "sizeName";
    private static final String JOBBER_WAREHOUSE_MD_STOCK = "mDStock";
    private static final String JOBBER_WAREHOUSE_BARCODE = "barcode";
    private static final String JOBBER_WAREHOUSE_SUBITEM_APPLICABLE = "subItemApplicable";
    private static final String JOBBER_WAREHOUSE_MD_APPLICABLE = "mdApplicable";
    private static final String JOBBER_WAREHOUSE_LAST_MOVE_DAYS = "lastMoveDays";
    private static final String JOBBER_WAREHOUSE_ITEM_NAME = "itemName";
    private static final String JOBBER_WAREHOUSE_SUB_GROUP_NAME = "subGroupName";
    
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerJobberWarehouseReport(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_JOBBER_WAREHOUSE_TABLE = "CREATE TABLE " + JOBBER_WAREHOUSE_TABLE + "("+ JOBBER_WAREHOUSE_KEY_ID + " INTEGER PRIMARY KEY," + JOBBER_WAREHOUSE_DIVISION_ID + " TEXT," + JOBBER_WAREHOUSE_DIVISION_NAME + " TEXT," + JOBBER_WAREHOUSE_GODOWN_ID + " TEXT," + JOBBER_WAREHOUSE_GODOWN_NAME + " TEXT," + JOBBER_WAREHOUSE_PARTY_ID + " TEXT," + JOBBER_WAREHOUSE_PARTY_NAME + " TEXT," + JOBBER_WAREHOUSE_ITEM_ID + " TEXT," + JOBBER_WAREHOUSE_ITEM_CODE + " TEXT," + JOBBER_WAREHOUSE_SUB_ITEM_ID + " TEXT," + JOBBER_WAREHOUSE_SUB_ITEM_NAME + " TEXT," + JOBBER_WAREHOUSE_SUB_DET_ATTR_ID1 + " TEXT," + JOBBER_WAREHOUSE_SUB_DETAILS + " TEXT," + JOBBER_WAREHOUSE_ITEM_STOCK + " TEXT," + JOBBER_WAREHOUSE_COLOR_ID + " TEXT," + JOBBER_WAREHOUSE_COLOR_NAME + " TEXT," + JOBBER_WAREHOUSE_SIZE_ID + " TEXT," + JOBBER_WAREHOUSE_SIZE_NAME + " TEXT," + JOBBER_WAREHOUSE_MD_STOCK + " TEXT," + JOBBER_WAREHOUSE_BARCODE + " TEXT," + JOBBER_WAREHOUSE_SUBITEM_APPLICABLE + " INTEGER," + JOBBER_WAREHOUSE_MD_APPLICABLE + " INTEGER," + JOBBER_WAREHOUSE_LAST_MOVE_DAYS + " TEXT," + JOBBER_WAREHOUSE_ITEM_NAME + " TEXT," + JOBBER_WAREHOUSE_SUB_GROUP_NAME + " TEXT) ";
    	db.execSQL(CREATE_JOBBER_WAREHOUSE_TABLE);
    }   
 //------------------------------------ Upgrading database-------------------------------------------------------------
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +JOBBER_WAREHOUSE_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteJobberwarehouse()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(JOBBER_WAREHOUSE_TABLE,null,null);
        db.close();
    }
    //---------------------------------- Inserting Data of Change Party New Table---------------------------------------
    @SuppressWarnings("deprecation")
	public void insertJobberwarehouseData(List<Map<String,String>> list){
    	
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,JOBBER_WAREHOUSE_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int DivisionID = ih.getColumnIndex(JOBBER_WAREHOUSE_DIVISION_ID);
        final int Division = ih.getColumnIndex(JOBBER_WAREHOUSE_DIVISION_NAME);
        final int GodownID = ih.getColumnIndex(JOBBER_WAREHOUSE_GODOWN_ID);
        final int Godown = ih.getColumnIndex(JOBBER_WAREHOUSE_GODOWN_NAME);
        final int PartyID = ih.getColumnIndex(JOBBER_WAREHOUSE_PARTY_ID);
        final int PartyName = ih.getColumnIndex(JOBBER_WAREHOUSE_PARTY_NAME);
        final int ItemID = ih.getColumnIndex(JOBBER_WAREHOUSE_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(JOBBER_WAREHOUSE_ITEM_CODE);
        final int SubItemID = ih.getColumnIndex(JOBBER_WAREHOUSE_SUB_ITEM_ID);
        final int SubItem = ih.getColumnIndex(JOBBER_WAREHOUSE_SUB_ITEM_NAME);
        final int SubDetAttribID1 = ih.getColumnIndex(JOBBER_WAREHOUSE_SUB_DET_ATTR_ID1);
        final int SubDetails = ih.getColumnIndex(JOBBER_WAREHOUSE_SUB_DETAILS);
        final int ItemStock = ih.getColumnIndex(JOBBER_WAREHOUSE_ITEM_STOCK);
        final int ColorID = ih.getColumnIndex(JOBBER_WAREHOUSE_COLOR_ID);
        final int Color = ih.getColumnIndex(JOBBER_WAREHOUSE_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(JOBBER_WAREHOUSE_SIZE_ID);
        final int Size = ih.getColumnIndex(JOBBER_WAREHOUSE_SIZE_NAME);
        final int MDStock = ih.getColumnIndex(JOBBER_WAREHOUSE_MD_STOCK);
        final int Barcode = ih.getColumnIndex(JOBBER_WAREHOUSE_BARCODE);
        final int SubItemApplicable = ih.getColumnIndex(JOBBER_WAREHOUSE_SUBITEM_APPLICABLE);
        final int MDApplicable = ih.getColumnIndex(JOBBER_WAREHOUSE_MD_APPLICABLE);
        final int LastMoveDays = ih.getColumnIndex(JOBBER_WAREHOUSE_LAST_MOVE_DAYS);
        final int ItemName = ih.getColumnIndex(JOBBER_WAREHOUSE_ITEM_NAME);
        final int SubGroup = ih.getColumnIndex(JOBBER_WAREHOUSE_SUB_GROUP_NAME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(DivisionID, list.get(x).get("DivisionID"));
                ih.bind(Division, list.get(x).get("Division"));
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(Godown, list.get(x).get("Godown"));
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(PartyName, list.get(x).get("PartyName"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(SubItem, list.get(x).get("SubItem"));
                ih.bind(SubDetAttribID1, list.get(x).get("SubDetAttribID1"));
                ih.bind(SubDetails, list.get(x).get("SubDetails"));
                ih.bind(ItemStock, list.get(x).get("ItemStock"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(Color, list.get(x).get("Color"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(Size, list.get(x).get("Size"));
                ih.bind(MDStock, list.get(x).get("MDStock"));
                ih.bind(Barcode, list.get(x).get("Barcode"));
                ih.bind(SubItemApplicable, list.get(x).get("SubItemApplicable"));
                ih.bind(MDApplicable, list.get(x).get("MDApplicable"));
                ih.bind(LastMoveDays, list.get(x).get("LastMoveDays"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(SubGroup, list.get(x).get("SubGroup"));
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
    public List<Map<String,String>> getPartyList(){
    	List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select distinct "+JOBBER_WAREHOUSE_PARTY_ID+","+JOBBER_WAREHOUSE_PARTY_NAME+"  from " + JOBBER_WAREHOUSE_TABLE; 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("PartyID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_PARTY_ID)));
            	map.put("PartyName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_PARTY_NAME)));
            	dataList.add(map);
        	}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public List<Map<String,String>> getItemList(String PartyID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+JOBBER_WAREHOUSE_ITEM_ID+"),"+JOBBER_WAREHOUSE_ITEM_CODE+","+JOBBER_WAREHOUSE_ITEM_NAME+","+JOBBER_WAREHOUSE_SUB_GROUP_NAME+","+JOBBER_WAREHOUSE_MD_APPLICABLE+","+JOBBER_WAREHOUSE_SUBITEM_APPLICABLE+"  from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_NAME)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_CODE)));
            	map.put("SubGroupName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SUB_GROUP_NAME)));
            	map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_MD_APPLICABLE)));
            	map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SUBITEM_APPLICABLE)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getSizeList(String PartyID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+JOBBER_WAREHOUSE_SIZE_ID+"),"+JOBBER_WAREHOUSE_SIZE_NAME+" from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' AND "+JOBBER_WAREHOUSE_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SIZE_NAME)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getColorList(String PartyID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+JOBBER_WAREHOUSE_COLOR_ID+"),"+JOBBER_WAREHOUSE_COLOR_NAME+" from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' AND "+JOBBER_WAREHOUSE_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_COLOR_NAME)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public float getBookQty(String PartyID,String ItemID,String SizeID,String ColorID){
    	// Select All Query
        String selectQuery = "Select "+JOBBER_WAREHOUSE_MD_STOCK+" from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' and "+JOBBER_WAREHOUSE_ITEM_ID+"= '"+ItemID+"' and "+JOBBER_WAREHOUSE_SIZE_ID+"= '"+SizeID+"' and "+JOBBER_WAREHOUSE_COLOR_ID+"= '"+ColorID+"'  ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        float Qty=0;
        if(cursor.getCount()>0)
        {
	        cursor.moveToFirst();
	        Qty=cursor.getFloat(cursor.getColumnIndex(JOBBER_WAREHOUSE_MD_STOCK));
	        // closing connection
	        cursor.close();
	        db.close();
    	}
    	// returning lables
    	return Qty;
    }
    public List<Map<String,String>> getSubItemList(String PartyID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+JOBBER_WAREHOUSE_SUB_ITEM_ID+"),"+JOBBER_WAREHOUSE_SUB_ITEM_NAME+","+JOBBER_WAREHOUSE_ITEM_STOCK+","+JOBBER_WAREHOUSE_BARCODE+","+JOBBER_WAREHOUSE_LAST_MOVE_DAYS+" from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' AND "+JOBBER_WAREHOUSE_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("SubItemID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SUB_ITEM_ID)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_SUB_ITEM_NAME)));
            	map.put("ItemStock", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_STOCK)));
            	map.put("Barcode", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_BARCODE)));
            	map.put("LastMoveDays", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_LAST_MOVE_DAYS)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    	
    }
    public List<Map<String,String>> getWithoutSubItemList(String PartyID,String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select Distinct("+JOBBER_WAREHOUSE_ITEM_ID+"),"+JOBBER_WAREHOUSE_ITEM_NAME+","+JOBBER_WAREHOUSE_ITEM_STOCK+","+JOBBER_WAREHOUSE_LAST_MOVE_DAYS+" from " + JOBBER_WAREHOUSE_TABLE+" where "+JOBBER_WAREHOUSE_PARTY_ID+"='"+PartyID+"' AND "+JOBBER_WAREHOUSE_ITEM_ID+"='"+ItemID+"' ";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_NAME)));
            	map.put("ItemStock", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_ITEM_STOCK)));
            	map.put("LastMoveDays", cursor.getString(cursor.getColumnIndex(JOBBER_WAREHOUSE_LAST_MOVE_DAYS)));
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