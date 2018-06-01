package stockcheck.DatabaseSqLite;

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

import stockcheck.model.Godown;

public class DatabaseSqlLiteHandlerStockCheck extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_StockCheck";
 
    //----------------------------Table Name--------------------------------------------------------------
    private static final String BARCODE_SCANNER_TABLE = "barcodeScannerTbl";   
    private static final String BARCODE_SCANNER_GODDOWN_TABLE = "barcodeScannerGodownTbl";   
    //-------------------Barcode Scanner Table Column Names---------------------------------------------
    private static final String BARCODE_SCANNER_KEY_ID = "id";
    private static final String BARCODE_SCANNER_BARCODE = "barcode";
    private static final String BARCODE_SCANNER_ITEM_ID = "itemId";
    private static final String BARCODE_SCANNER_ITEM_NAME = "itemName";
    private static final String BARCODE_SCANNER_ITEM_CODE = "itemCode";
    private static final String BARCODE_SCANNER_MAIN_GROUP = "mainGroupName";
    private static final String BARCODE_SCANNER_GROUP = "groupName";
    private static final String BARCODE_SCANNER_COLOR_ID = "colorId";
    private static final String BARCODE_SCANNER_COLOR_NAME = "colorName";
    private static final String BARCODE_SCANNER_SIZE_ID = "sizeId";
    private static final String BARCODE_SCANNER_SIZE_NAME = "sizeName";
    private static final String BARCODE_SCANNER_SIZE_SEQUENCE = "sizeSequence";
    private static final String BARCODE_SCANNER_MD_STOCK = "mdStock";
    private static final String BARCODE_SCANNER_MD_RESERVE_STOCK = "mdReserveStock";
    private static final String BARCODE_SCANNER_MD_REJECTED_STOCK = "mdRejectedStock";
    private static final String BARCODE_SCANNER_MD_SALEABLE_STOCK = "mdSaleableStock";
    private static final String BARCODE_SCANNER_MD_SERVICE_ORDER_STOCK = "mdServiceOrderStock";
    private static final String BARCODE_SCANNER_PRICE = "price";
    private static final String BARCODE_SCANNER_GODOWN_NAME = "goDownName";
    private static final String BARCODE_SCANNER_GODOWN_ID = "goDownId";
    private static final String BARCODE_SCANNER_MD_APPLICABLE = "mDApplicable";
    private static final String BARCODE_SCANNER_SUB_ITEM_APPLICABLE = "subItemApplicable";
    private static final String BARCODE_SCANNER_SUB_ITEM_ID = "subItemId";
    private static final String BARCODE_SCANNER_SUB_ITEM_NAME = "subItemName";
    private static final String BARCODE_SCANNER_SUB_ITEM_CODE = "subItemCode";
    private static final String BARCODE_SCANNER_STOCK = "stock";
    private static final String BARCODE_SCANNER_RESERVE_STOCK = "reserveStock";
    private static final String BARCODE_SCANNER_REJECTED_STOCK = "rejectedStock";
    private static final String BARCODE_SCANNER_SALEABLE_STOCK = "saleableStock";
    private static final String BARCODE_SCANNER_SERVICE_ORDER_STOCK = "serviceOrderStock";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerStockCheck(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_TABLE_BARCODE_SCANNER_TABLE = "CREATE TABLE " + BARCODE_SCANNER_TABLE + "("+ BARCODE_SCANNER_KEY_ID + " INTEGER PRIMARY KEY," + BARCODE_SCANNER_BARCODE + " TEXT," + BARCODE_SCANNER_ITEM_ID + " TEXT," + BARCODE_SCANNER_ITEM_NAME + " TEXT," + BARCODE_SCANNER_ITEM_CODE + " TEXT," + BARCODE_SCANNER_MAIN_GROUP + " TEXT," + BARCODE_SCANNER_GROUP + " TEXT," + BARCODE_SCANNER_COLOR_ID+ " TEXT," + BARCODE_SCANNER_COLOR_NAME + " TEXT," + BARCODE_SCANNER_SIZE_ID + " TEXT," + BARCODE_SCANNER_SIZE_NAME + " TEXT," + BARCODE_SCANNER_MD_STOCK + " TEXT," + BARCODE_SCANNER_MD_RESERVE_STOCK + " TEXT," + BARCODE_SCANNER_MD_REJECTED_STOCK + " TEXT," + BARCODE_SCANNER_MD_SALEABLE_STOCK + " TEXT," + BARCODE_SCANNER_MD_SERVICE_ORDER_STOCK + " TEXT," + BARCODE_SCANNER_PRICE + " TEXT," + BARCODE_SCANNER_GODOWN_NAME + " TEXT," + BARCODE_SCANNER_GODOWN_ID + " TEXT," + BARCODE_SCANNER_MD_APPLICABLE + " INTEGER," + BARCODE_SCANNER_SUB_ITEM_APPLICABLE + " INTEGER," + BARCODE_SCANNER_SUB_ITEM_ID + " TEXT," + BARCODE_SCANNER_SUB_ITEM_NAME + " TEXT," + BARCODE_SCANNER_SUB_ITEM_CODE+ " TEXT," + BARCODE_SCANNER_STOCK+ " TEXT," + BARCODE_SCANNER_RESERVE_STOCK + " TEXT," + BARCODE_SCANNER_REJECTED_STOCK + " TEXT," + BARCODE_SCANNER_SALEABLE_STOCK + " TEXT," + BARCODE_SCANNER_SERVICE_ORDER_STOCK + " INTEGER," + BARCODE_SCANNER_SIZE_SEQUENCE + " INTEGER) ";
    	String CREATE_TABLE_BARCODE_SCANNER_GODOWN_TABLE = "CREATE TABLE " + BARCODE_SCANNER_GODDOWN_TABLE + "("+ BARCODE_SCANNER_KEY_ID + " INTEGER PRIMARY KEY," + BARCODE_SCANNER_ITEM_ID + " TEXT," + BARCODE_SCANNER_COLOR_ID+ " TEXT," + BARCODE_SCANNER_SIZE_ID + " TEXT," + BARCODE_SCANNER_GODOWN_NAME + " TEXT," + BARCODE_SCANNER_GODOWN_ID + " TEXT," + BARCODE_SCANNER_SUB_ITEM_ID+ " TEXT," + BARCODE_SCANNER_STOCK+ " TEXT) ";
    	db.execSQL(CREATE_TABLE_BARCODE_SCANNER_TABLE);
    	db.execSQL(CREATE_TABLE_BARCODE_SCANNER_GODOWN_TABLE);
    }
 //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +BARCODE_SCANNER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +BARCODE_SCANNER_GODDOWN_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteStocks(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BARCODE_SCANNER_TABLE,null,null);
        db.close();
    }
    public void deleteGodownStocks(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BARCODE_SCANNER_GODDOWN_TABLE,null,null);
        db.close();
    }
    public void insertStockCheckTable(List<Map<String, String>> list) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, BARCODE_SCANNER_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int Barcode = ih.getColumnIndex(BARCODE_SCANNER_BARCODE);
        final int ItemID = ih.getColumnIndex(BARCODE_SCANNER_ITEM_ID);
        final int ItemName = ih.getColumnIndex(BARCODE_SCANNER_ITEM_NAME);
        final int ItemCode = ih.getColumnIndex(BARCODE_SCANNER_ITEM_CODE);
        final int ColorID = ih.getColumnIndex(BARCODE_SCANNER_COLOR_ID);
        final int ColorName = ih.getColumnIndex(BARCODE_SCANNER_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(BARCODE_SCANNER_SIZE_ID);
        final int SizeName = ih.getColumnIndex(BARCODE_SCANNER_SIZE_NAME);
        final int Sequence = ih.getColumnIndex(BARCODE_SCANNER_SIZE_SEQUENCE);
        final int MDStock = ih.getColumnIndex(BARCODE_SCANNER_MD_STOCK);
        final int MDReserveStock = ih.getColumnIndex(BARCODE_SCANNER_MD_RESERVE_STOCK);
        final int MDRejectionStock = ih.getColumnIndex(BARCODE_SCANNER_MD_REJECTED_STOCK);
        final int MDSaleableStock = ih.getColumnIndex(BARCODE_SCANNER_MD_SALEABLE_STOCK);
        final int MDSrvOrdStock = ih.getColumnIndex(BARCODE_SCANNER_MD_SERVICE_ORDER_STOCK);
        final int MainGroup = ih.getColumnIndex(BARCODE_SCANNER_MAIN_GROUP);
        final int Group = ih.getColumnIndex(BARCODE_SCANNER_GROUP);
        final int Price = ih.getColumnIndex(BARCODE_SCANNER_PRICE);
        final int MDApplicable = ih.getColumnIndex(BARCODE_SCANNER_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_APPLICABLE);
        final int GodownID = ih.getColumnIndex(BARCODE_SCANNER_GODOWN_ID);
        final int GodownName = ih.getColumnIndex(BARCODE_SCANNER_GODOWN_NAME);
        final int SubItemID = ih.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_ID);
        final int SubItemName = ih.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_NAME);
        final int SubItemCode = ih.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_CODE);
        final int Stock = ih.getColumnIndex(BARCODE_SCANNER_STOCK);
        final int ReserveStock = ih.getColumnIndex(BARCODE_SCANNER_RESERVE_STOCK);
        final int RejectionStock = ih.getColumnIndex(BARCODE_SCANNER_REJECTED_STOCK);
        final int SaleableStock = ih.getColumnIndex(BARCODE_SCANNER_SALEABLE_STOCK);
        final int SrvOrdStock = ih.getColumnIndex(BARCODE_SCANNER_SERVICE_ORDER_STOCK);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for (int x = 0; x < list.size(); x++) {
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(Barcode, list.get(x).get("Barcode"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(ColorName, list.get(x).get("Color"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(SizeName, list.get(x).get("Size"));
                ih.bind(Sequence, list.get(x).get("Sequence"));
                ih.bind(MDStock, list.get(x).get("MDStock"));
                ih.bind(MDReserveStock, list.get(x).get("MDReserveStock"));
                ih.bind(MDRejectionStock, list.get(x).get("MDRejectionStock"));
                ih.bind(MDSaleableStock, list.get(x).get("MDSaleableStock"));
                ih.bind(MDSrvOrdStock, list.get(x).get("MDSrvOrdStock"));
                ih.bind(MainGroup, list.get(x).get("MainGroup"));
                ih.bind(Group, list.get(x).get("Group"));
                ih.bind(Price, list.get(x).get("Price"));
                ih.bind(MDApplicable, list.get(x).get("MDApplicable"));
                ih.bind(SubItemApplicable, list.get(x).get("SubItemApplicable"));
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(GodownName, list.get(x).get("GodownName"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(SubItemName, list.get(x).get("SubItemName"));
                ih.bind(SubItemCode, list.get(x).get("SubItemCode"));
                ih.bind(Stock, list.get(x).get("Stock"));
                ih.bind(ReserveStock, list.get(x).get("ReserveStock"));
                ih.bind(RejectionStock, list.get(x).get("RejectionStock"));
                ih.bind(SaleableStock, list.get(x).get("SaleableStock"));
                ih.bind(SrvOrdStock, list.get(x).get("SrvOrdStock"));
                // Insert the row into the database.
                ih.execute();
            }
        } finally {
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
            ih.close();  // See comment below from Stefan Anca
            final long endtime = System.currentTimeMillis();
            Log.e("Time:", "" + String.valueOf(endtime - startTime));
        }
    }
    //---------------------------------- Inserting Data of Item Details Table---------------------------------------
    public void insertStockCheckGodownWiseTable(List<Map<String, String>> list){
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, BARCODE_SCANNER_GODDOWN_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int GodownID = ih.getColumnIndex(BARCODE_SCANNER_GODOWN_ID);
        final int GodownName = ih.getColumnIndex(BARCODE_SCANNER_GODOWN_NAME);
        final int ItemID = ih.getColumnIndex(BARCODE_SCANNER_ITEM_ID);
        final int SizeID = ih.getColumnIndex(BARCODE_SCANNER_SIZE_ID);
        final int SubItemID = ih.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_ID);
        final int ColorID = ih.getColumnIndex(BARCODE_SCANNER_COLOR_ID);
        final int Stock = ih.getColumnIndex(BARCODE_SCANNER_STOCK);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for (int x = 0; x < list.size(); x++) {
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(GodownName, list.get(x).get("GodownName"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(Stock, list.get(x).get("Stock"));
                // Insert the row into the database.
                ih.execute();
            }
        } finally {
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
            ih.close();  // See comment below from Stefan Anca
            final long endtime = System.currentTimeMillis();
            Log.e("Time:", "" + String.valueOf(endtime - startTime));
        }
    }
    //---------------------------------- Getting Data of Booked Order Details Table---------------------------------------
    public List<Map<String,String>> getStockItemTotal(String ColorId,String SizeId,String ItemId){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select SUM("+BARCODE_SCANNER_MD_STOCK+") as Stock,SUM("+BARCODE_SCANNER_MD_RESERVE_STOCK+") as ReserveStock,SUM("+BARCODE_SCANNER_MD_REJECTED_STOCK+") as RejectedStock,SUM("+BARCODE_SCANNER_MD_SALEABLE_STOCK+") as SlableStock from " + BARCODE_SCANNER_TABLE +" where "+BARCODE_SCANNER_COLOR_ID+" = '"+ColorId+"' AND "+BARCODE_SCANNER_SIZE_ID+" = '"+SizeId+"' AND "+BARCODE_SCANNER_ITEM_ID+" = '"+ItemId+"' ";
        //Log.e("Query","Qry:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("Stock",String.valueOf(cursor.getInt(cursor.getColumnIndex("Stock"))));
                map.put("ReserveStock",String.valueOf(cursor.getInt(cursor.getColumnIndex("ReserveStock"))));
                map.put("RejectedStock",String.valueOf(cursor.getInt(cursor.getColumnIndex("RejectedStock"))));
                map.put("SlableStock",String.valueOf(cursor.getInt(cursor.getColumnIndex("SlableStock"))));
                mapList.add(map);
            }while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
        //Log.e("Print","Qty:"+mapList.toString());
    	return mapList;
    }
    //---------------------------------- Getting Data of Booked Order Details Table---------------------------------------
    public List<Map<String,String>> getStockGodownWise(String ColorId,String SizeId,String ItemId,String GodownID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select "+BARCODE_SCANNER_STOCK+" from " + BARCODE_SCANNER_GODDOWN_TABLE +" where "+BARCODE_SCANNER_COLOR_ID+" = '"+ColorId+"' AND "+BARCODE_SCANNER_SIZE_ID+" = '"+SizeId+"' AND "+BARCODE_SCANNER_ITEM_ID+" = '"+ItemId+"' AND "+BARCODE_SCANNER_GODOWN_ID+" = '"+GodownID+"' AND "+BARCODE_SCANNER_STOCK+">0 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
            Map<String,String> map=new HashMap<String,String>();
            map.put("Stock",String.valueOf(cursor.getInt(cursor.getColumnIndex(BARCODE_SCANNER_STOCK))));
            mapList.add(map);
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return mapList;
    }
    //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Map<String,String>> getBarcodeScannerMatrixColor(String itemID){
    	List<Map<String,String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_COLOR_ID+", "+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+itemID+"' AND "+BARCODE_SCANNER_MD_STOCK+" >0 Order By "+BARCODE_SCANNER_COLOR_NAME+" ASC";
        System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
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
    public List<Map<String,String>> getBarcodeScannerMatrixColorGodown(String itemID,String GodownID){
        List<Map<String,String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT b."+BARCODE_SCANNER_COLOR_ID+", a."+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" a INNER JOIN " + BARCODE_SCANNER_GODDOWN_TABLE+" b ON a."+BARCODE_SCANNER_COLOR_ID+"=b."+BARCODE_SCANNER_COLOR_ID+" Where b."+BARCODE_SCANNER_GODOWN_ID+"='"+GodownID+"' AND b."+BARCODE_SCANNER_STOCK+" >0 Order By a."+BARCODE_SCANNER_COLOR_NAME+" ASC";
        System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
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
    public List<Map<String,String>> getColorGodownByBarcode(String Barcode,String GodownID){
        List<Map<String,String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT b."+BARCODE_SCANNER_COLOR_ID+", a."+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" a INNER JOIN " + BARCODE_SCANNER_GODDOWN_TABLE+" b ON a."+BARCODE_SCANNER_COLOR_ID+"=b."+BARCODE_SCANNER_COLOR_ID+" Where b."+BARCODE_SCANNER_GODOWN_ID+"='"+GodownID+"' AND a."+BARCODE_SCANNER_BARCODE+"='"+Barcode+"' Order By a."+BARCODE_SCANNER_COLOR_NAME+" ASC";
        System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
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
    //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Map<String,String>> getBarcodeScannerMatrixColorGodownWise(String itemID,String GodownId){
    	List<Map<String,String>> dataList =null;
    	dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+BARCODE_SCANNER_COLOR_ID+"), "+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+itemID+"' AND "+BARCODE_SCANNER_GODOWN_ID+"='"+GodownId+"' AND "+BARCODE_SCANNER_STOCK+" >0";
        System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorId", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
    	// returning 
    	return dataList;
    }
    //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Map<String,String>> getBarcodeWiseColor(String Barcode){
    	List<Map<String,String>> dataList =null;
    	dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+BARCODE_SCANNER_COLOR_ID+"), "+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_BARCODE+"='"+Barcode+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
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
  //---------------------------------- Getting Data of BOOKED Order SIZE LIST Table---------------------------------------
    public List<Map<String,String>> getBarcodeScannerMatrixsize(String itemID){
    	List<Map<String,String>> dataList =null;
    	dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select DISTINCT "+BARCODE_SCANNER_SIZE_ID+" ,"+BARCODE_SCANNER_SIZE_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+itemID+"' AND "+BARCODE_SCANNER_MD_STOCK+" >0 Order By "+BARCODE_SCANNER_SIZE_SEQUENCE+" Asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("SizeID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_ID)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_NAME)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
    	// returning lables
    	return dataList;
    }
    public List<Map<String,String>> getBarcodeScannerMatrixsizeGodown(String Barcode,String GodownID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "Select DISTINCT b."+BARCODE_SCANNER_SIZE_ID+",a."+BARCODE_SCANNER_SIZE_NAME+"  from " + BARCODE_SCANNER_TABLE+" a INNER JOIN "+ BARCODE_SCANNER_GODDOWN_TABLE+" b ON a."+BARCODE_SCANNER_SIZE_ID+"=b."+BARCODE_SCANNER_SIZE_ID+" Where b."+BARCODE_SCANNER_GODOWN_ID+"='"+GodownID+"' AND b."+BARCODE_SCANNER_STOCK+" >0 Order By a."+BARCODE_SCANNER_SIZE_SEQUENCE+" Asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_ID)));
                map.put("SizeName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_NAME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning lables
        return dataList;
    }
  //---------------------------------- Getting Data of BOOKED Order SIZE LIST Table---------------------------------------
    public List<Map<String,String>> getBarcodeWiseColorList(String Barcode){
    	List<Map<String,String>> dataList=new ArrayList<>();
        // Select All Query
        String selectQuery = "select DISTINCT "+BARCODE_SCANNER_COLOR_ID+","+BARCODE_SCANNER_COLOR_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_BARCODE+"='"+Barcode+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ColorID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_ID)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_COLOR_NAME)));
            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
    	// returning lables
    	return dataList;
    }
    public List<Map<String,String>> getBarcodeWiseSizeList(String ItemID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select DISTINCT "+BARCODE_SCANNER_SIZE_ID+","+BARCODE_SCANNER_SIZE_NAME+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"' Order By "+BARCODE_SCANNER_SIZE_SEQUENCE+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_ID)));
                map.put("SizeName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SIZE_NAME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning lables
        return dataList;
    }
  //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Map<String,String>> getBarcodeScannerDetails(String itemID){
    	List<Map<String,String>> dataList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+BARCODE_SCANNER_ITEM_ID+"), "+BARCODE_SCANNER_ITEM_NAME+", "+BARCODE_SCANNER_ITEM_CODE+", "+BARCODE_SCANNER_MAIN_GROUP+", "+BARCODE_SCANNER_GROUP+", "+BARCODE_SCANNER_PRICE+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+itemID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_NAME)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_CODE)));
            	map.put("MainGroup", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_MAIN_GROUP)));
            	map.put("Group", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GROUP)));
            	map.put("Price", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_PRICE)));
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
    //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Godown> getBarcodeScannerGodownDetails(String itemID){
    	List<Godown> dataList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+BARCODE_SCANNER_GODOWN_ID+"), "+BARCODE_SCANNER_GODOWN_NAME+","+BARCODE_SCANNER_ITEM_ID+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+itemID+"' AND "+BARCODE_SCANNER_STOCK+">0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Godown godown = new Godown();
            	godown.setGodownID(cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_ID)));
            	godown.setGodownName(cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_NAME)));
            	godown.setItemID(cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
            	dataList.add(godown);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
        
        //System.out.println("Color ID:"+dataList.toString());
    	// returning 
    	return dataList;
    }
    public List<Map<String,String>> getGodownListByBarcode(String Barcode){
    	List<Map<String,String>>  dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_GODOWN_ID+", "+BARCODE_SCANNER_GODOWN_NAME+","+BARCODE_SCANNER_ITEM_ID+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_BARCODE+"='"+Barcode+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("GodownID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_ID)));
            	map.put("GodownName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_NAME)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
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
    //---------------------------------- Getting Data of Order Details Table---------------------------------------
    public List<Map<String,String>> getBarcodeWiseGodownDetails(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_GODOWN_ID+", "+BARCODE_SCANNER_GODOWN_NAME+","+BARCODE_SCANNER_ITEM_ID+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("GodownID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_ID)));
            	map.put("GodownName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GODOWN_NAME)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
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
    public List<Map<String,String>> getSubItemDetails(String ItemID){
    	List<Map<String,String>> dataList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_ITEM_ID+", "+BARCODE_SCANNER_SUB_ITEM_ID+","+BARCODE_SCANNER_SUB_ITEM_CODE+","+BARCODE_SCANNER_SUB_ITEM_NAME+","+BARCODE_SCANNER_STOCK+","+BARCODE_SCANNER_RESERVE_STOCK+","+BARCODE_SCANNER_REJECTED_STOCK+","+BARCODE_SCANNER_SALEABLE_STOCK+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
            	map.put("SubItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_ID)));
            	map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_CODE)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_NAME)));
                map.put("Stock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_STOCK)));
                map.put("ReserveStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_RESERVE_STOCK)));
                map.put("RejectedStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_REJECTED_STOCK)));
                map.put("SlableStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SALEABLE_STOCK)));
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
    public List<Map<String,String>> getSubItemGodownStock(String ItemID,String SubItemID,String godownID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT "+BARCODE_SCANNER_STOCK+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"' AND "+BARCODE_SCANNER_SUB_ITEM_ID+"='"+SubItemID+"' AND "+BARCODE_SCANNER_GODOWN_ID+"='"+godownID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("Stock", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_STOCK)));
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
    public List<Map<String,String>> getWithoutMDItemDetails(String ItemID){
    	List<Map<String,String>> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_ITEM_ID+","+BARCODE_SCANNER_STOCK+","+BARCODE_SCANNER_RESERVE_STOCK+","+BARCODE_SCANNER_REJECTED_STOCK+","+BARCODE_SCANNER_SALEABLE_STOCK+" from " + BARCODE_SCANNER_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
                map.put("Stock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_STOCK)));
                map.put("ReserveStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_RESERVE_STOCK)));
                map.put("RejectedStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_REJECTED_STOCK)));
                map.put("SlableStock",cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SALEABLE_STOCK)));
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
    public List<Map<String,String>> getItemOnlyGodown(String ItemID,String GodownID){
    	List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT "+BARCODE_SCANNER_STOCK+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_ITEM_ID+"='"+ItemID+"' AND "+BARCODE_SCANNER_GODOWN_ID+"='"+GodownID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("Stock", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_STOCK)));
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
    public Map<String,String> GetItemBasicDetails(){
        Map<String,String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BARCODE_SCANNER_ITEM_ID+","+BARCODE_SCANNER_ITEM_CODE+","+BARCODE_SCANNER_ITEM_NAME+","+BARCODE_SCANNER_SUB_ITEM_APPLICABLE+","+BARCODE_SCANNER_MD_APPLICABLE+","+BARCODE_SCANNER_MAIN_GROUP+","+BARCODE_SCANNER_GROUP+","+BARCODE_SCANNER_PRICE+" from " + BARCODE_SCANNER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                map.put("ItemID", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_ID)));
                map.put("ItemCode", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_CODE)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_ITEM_NAME)));
                map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_MD_APPLICABLE)));
                map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_SUB_ITEM_APPLICABLE)));
                map.put("MainGroup", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_MAIN_GROUP)));
                map.put("Group", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_GROUP)));
                map.put("Price", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_PRICE)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Color ID:"+dataList.toString());
        // returning
        return map;
    }
    public List<Map<String,String>> getBarcodeWiseStock(String GodownId,String ColorID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT "+BARCODE_SCANNER_STOCK+" from " + BARCODE_SCANNER_GODDOWN_TABLE+" where "+BARCODE_SCANNER_GODOWN_ID+"='"+GodownId+"' AND "+BARCODE_SCANNER_COLOR_ID+"='"+ColorID+"' ";
        Log.e("Qeury","Qry:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("Stock", cursor.getString(cursor.getColumnIndex(BARCODE_SCANNER_STOCK)));
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
}
