package report.DatabaseSqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import report.godownwiseorderitem.model.GroupWise;
import report.godownwiseorderitem.model.ItemDetails;
import report.godownwiseorderitem.model.OrderWise;
import report.godownwiseorderitem.model.PartyWise;

public class DBSqlLiteSalesReportWithOrderType extends SQLiteOpenHelper {
    //TODO:--------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SinglaGroups_SalesReport";
 
    //TODO:--------------------------Table Name--------------------------------------------------------------
    private static final String REPORT_TABLE = "reportTbl";
    //TODO:-----------------Barcode Scanner Table Column Names---------------------------------------------
    private static final String REPORT_KEY_ID = "id";
    private static final String REPORT_MAIN_GROUP_ID = "mainGroupId";
    private static final String REPORT_MAIN_GROUP_NAME = "mainGroupName";
    private static final String REPORT_GROUP_ID = "groupId";
    private static final String REPORT_GROUP_NAME = "groupName";
    private static final String REPORT_SUB_GROUP_NAME = "subGroupName";
    private static final String REPORT_PARTY_ID = "custID";
    private static final String REPORT_PARTY_NAME = "custName";
    private static final String REPORT_SUB_PARTY_ID = "subCustID";
    private static final String REPORT_SUB_PARTY_NAME = "subCustName";
    private static final String REPORT_ORDER_ID = "orderID";
    private static final String REPORT_ORDER_NO = "orderNo";
    private static final String REPORT_ITEM_ID = "itemID";
    private static final String REPORT_ITEM_CODE = "itemCode";
    private static final String REPORT_ITEM_NAME = "itemName";
    private static final String REPORT_SUB_ITEM_ID = "subItemID";
    private static final String REPORT_SUB_ITEM_NAME = "subItemName";
    private static final String REPORT_COLOR_ID = "colorId";
    private static final String REPORT_COLOR_NAME = "colorName";
    private static final String REPORT_SIZE_ID = "sizeId";
    private static final String REPORT_SIZE_NAME = "sizeName";
    private static final String REPORT_RATE = "price";
    private static final String REPORT_ITEM_STOCK = "itemStock";
    private static final String REPORT_PENDING_QTY = "pendingQty";
    private static final String REPORT_COLOR_SIZE_BOOKED_QTY = "bookedQty";
    private static final String REPORT_DISPATCH_QTY = "dispatchQty";
    private static final String REPORT_COLOR_SIZE_STOCK = "colorSizeStock";
    private static final String REPORT_SUB_ITEM_STOCK = "subItemStock";
    private static final String REPORT_ORDER_DATE = "orderDate";
    private static final String REPORT_A_STATUS = "aStatus";
    private static final String REPORT_COLOR_FAMILY_NAME = "colorFamilyName";
    private static final String REPORT_REFRENCE_NAME = "refrenceName";
    private static final String REPORT_OrderType = "orderType";
    private static final String REPORT_OrderQty = "orderQty";
    private static final String REPORT_OrderType_Name = "orderTypeName";
    private static final String REPORT_Showroom_ID = "showroomId";
    private static final String REPORT_Showroom_Name = "showroomName";
    private static final String REPORT_Expected_Delivery_DateTime = "expectedDeliveryDateTime";
    private static final String REPORT_Urgency_Level = "urgencyLevel";
    private static final String REPORT_AGENT = "agent";
    private static final String REPORT_MD_APPLICABLE = "mdApplicable";
    private static final String REPORT_SUBITEM_APPLICABLE = "subItemApplicable";
    //TODO:-------------------Report 2 Table Column Names---------------------------------------------
    private static final String REPORT2_ITEM_ID = "itemID";
    private static final String REPORT2_VTYPE = "vType";
    //TODO:------------------------Constructor call----------------------------------------
    public DBSqlLiteSalesReportWithOrderType(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    //TODO:-----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_TABLE_REPORT_TABLE = "CREATE TABLE " + REPORT_TABLE + "("+ REPORT_KEY_ID + " INTEGER PRIMARY KEY," + REPORT_MAIN_GROUP_ID + " TEXT," + REPORT_MAIN_GROUP_NAME + " TEXT," + REPORT_GROUP_ID + " TEXT," + REPORT_GROUP_NAME + " TEXT," + REPORT_SUB_GROUP_NAME + " TEXT," + REPORT_PARTY_ID + " TEXT," + REPORT_PARTY_NAME + " TEXT," + REPORT_SUB_PARTY_ID + " TEXT," + REPORT_SUB_PARTY_NAME + " TEXT," + REPORT_ORDER_ID + " TEXT,"+ REPORT_ORDER_NO + " TEXT," + REPORT_ITEM_ID + " TEXT," + REPORT_ITEM_CODE + " TEXT,"  + REPORT_ITEM_NAME + " TEXT," + REPORT_SUB_ITEM_ID + " TEXT," + REPORT_SUB_ITEM_NAME + " TEXT,"+ REPORT_COLOR_ID + " TEXT,"+ REPORT_COLOR_NAME + " TEXT,"  + REPORT_SIZE_ID + " TEXT,"  + REPORT_SIZE_NAME + " TEXT," + REPORT_RATE + " INTEGER,"+ REPORT_ITEM_STOCK + " INTEGER," + REPORT_PENDING_QTY + " INTEGER,"+ REPORT_COLOR_SIZE_BOOKED_QTY + " INTEGER,"+ REPORT_DISPATCH_QTY + " INTEGER,"+ REPORT_COLOR_SIZE_STOCK + " INTEGER,"+ REPORT_SUB_ITEM_STOCK + " INTEGER,"+ REPORT_ORDER_DATE+ " TEXT,"+ REPORT_COLOR_FAMILY_NAME + " TEXT,"+ REPORT_A_STATUS + " TEXT,"+ REPORT_REFRENCE_NAME + " TEXT,"+ REPORT_OrderType + " TEXT,"+ REPORT_OrderQty + " INTEGER,"+ REPORT_OrderType_Name + " TEXT,"+ REPORT_Showroom_ID + " TEXT,"+ REPORT_Showroom_Name + " TEXT,"+ REPORT_Expected_Delivery_DateTime+ " DATETIME,"+ REPORT_Urgency_Level + " INTEGER,"+ REPORT_AGENT+ " TEXT,"+ REPORT_MD_APPLICABLE + " INTEGER,"+ REPORT_SUBITEM_APPLICABLE + " INTEGER) ";
    	db.execSQL(CREATE_TABLE_REPORT_TABLE);
    }
    //TODO:---------------------------------- Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +REPORT_TABLE);
        // Create tables again
        onCreate(db);
    }
    //TODO:---------------------------------- Delete table 1-------------------------------------------------------------
    public void deleteReportData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REPORT_TABLE,null,null);
        db.close();
    }
    //TODO:---------------------------------- Inserting Data of Pending Dispatch table-------------------------------------------------------------
    public void insertSalesReportTable(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,REPORT_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int MainGroupName = ih.getColumnIndex(REPORT_MAIN_GROUP_NAME);
        final int GroupID = ih.getColumnIndex(REPORT_GROUP_ID);
        final int GroupName = ih.getColumnIndex(REPORT_GROUP_NAME);
        final int SubGroupName = ih.getColumnIndex(REPORT_SUB_GROUP_NAME);
        final int CustID = ih.getColumnIndex(REPORT_PARTY_ID);
        final int CustName = ih.getColumnIndex(REPORT_PARTY_NAME);
        final int SubCustID = ih.getColumnIndex(REPORT_SUB_PARTY_ID);
        final int SubCustName = ih.getColumnIndex(REPORT_SUB_PARTY_NAME);
        final int OrderID = ih.getColumnIndex(REPORT_ORDER_ID);
        final int OrderNo = ih.getColumnIndex(REPORT_ORDER_NO);
        final int ItemID = ih.getColumnIndex(REPORT_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(REPORT_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(REPORT_ITEM_NAME);
        final int SubItemID = ih.getColumnIndex(REPORT_SUB_ITEM_ID);
        final int SubItemName = ih.getColumnIndex(REPORT_SUB_ITEM_NAME);
        final int ColorID = ih.getColumnIndex(REPORT_COLOR_ID);
        final int ColorName = ih.getColumnIndex(REPORT_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(REPORT_SIZE_ID);
        final int SizeName = ih.getColumnIndex(REPORT_SIZE_NAME);
        final int price = ih.getColumnIndex(REPORT_RATE);
        final int stock = ih.getColumnIndex(REPORT_ITEM_STOCK);
        final int pendingQty = ih.getColumnIndex(REPORT_PENDING_QTY);
        final int bookedQty = ih.getColumnIndex(REPORT_COLOR_SIZE_BOOKED_QTY);
        final int DispatchQty = ih.getColumnIndex(REPORT_DISPATCH_QTY);
        final int colorSizeStock = ih.getColumnIndex(REPORT_COLOR_SIZE_STOCK);
        final int subItemStock = ih.getColumnIndex(REPORT_SUB_ITEM_STOCK);
        final int Date = ih.getColumnIndex(REPORT_ORDER_DATE);
        final int ColorFamilyName = ih.getColumnIndex(REPORT_COLOR_FAMILY_NAME);
        final int AStatus = ih.getColumnIndex(REPORT_A_STATUS);
        final int RefrenceName = ih.getColumnIndex(REPORT_REFRENCE_NAME);
        final int OrderType = ih.getColumnIndex(REPORT_OrderType);
        final int OrderQty = ih.getColumnIndex(REPORT_OrderQty);
        final int OrderTypeVal = ih.getColumnIndex(REPORT_OrderType_Name);
        final int ShowroomID = ih.getColumnIndex(REPORT_Showroom_ID);
        final int Showroom = ih.getColumnIndex(REPORT_Showroom_Name);
        final int ExpectedDeliveryDate = ih.getColumnIndex(REPORT_Expected_Delivery_DateTime);
        final int urgencyLevel = ih.getColumnIndex(REPORT_Urgency_Level);
        final int Agent = ih.getColumnIndex(REPORT_AGENT);
        final int MDApplicable = ih.getColumnIndex(REPORT_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(REPORT_SUBITEM_APPLICABLE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(MainGroupName, list.get(x).get("MainGroupName"));
                ih.bind(GroupID, list.get(x).get("GroupID"));
                ih.bind(GroupName, list.get(x).get("GroupName"));
                ih.bind(SubGroupName, list.get(x).get("SubGroupName"));
                ih.bind(CustID, list.get(x).get("CustID"));
                ih.bind(CustName, list.get(x).get("CustName"));
                ih.bind(SubCustID, list.get(x).get("SubCustID"));
                ih.bind(SubCustName, list.get(x).get("SubCustName"));
                ih.bind(OrderID, list.get(x).get("OrderID"));
                ih.bind(OrderNo, list.get(x).get("OrderNo"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(SubItemID, list.get(x).get("SubItemID"));
                ih.bind(SubItemName, list.get(x).get("SubItemName"));
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(ColorName, list.get(x).get("ColorName"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(SizeName, list.get(x).get("SizeName"));
                ih.bind(price, list.get(x).get("price"));
                ih.bind(stock, list.get(x).get("stock"));
                ih.bind(pendingQty, list.get(x).get("pendingQty"));
                ih.bind(bookedQty, list.get(x).get("bookedQty"));
                ih.bind(DispatchQty, list.get(x).get("DispatchQty"));
                ih.bind(colorSizeStock, list.get(x).get("colorSizeStock"));
                ih.bind(subItemStock, list.get(x).get("subItemStock"));
                ih.bind(Date, list.get(x).get("Date"));
                ih.bind(ColorFamilyName, list.get(x).get("ColorFamilyName"));
                ih.bind(AStatus, list.get(x).get("AStatus"));
                ih.bind(RefrenceName, list.get(x).get("RefrenceName"));
                ih.bind(OrderType, list.get(x).get("OrderType"));
                ih.bind(OrderQty, list.get(x).get("OrderQty"));
                ih.bind(OrderTypeVal, list.get(x).get("OrderTypeVal"));
                ih.bind(ShowroomID, list.get(x).get("ShowroomID"));
                ih.bind(Showroom, list.get(x).get("Showroom"));
                ih.bind(ExpectedDeliveryDate, list.get(x).get("ExpectedDeliveryDate"));
                ih.bind(urgencyLevel, list.get(x).get("urgencyLevel"));
                ih.bind(Agent, list.get(x).get("Agent"));
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
    //TODO: Grand Total By All Wise
    public Map<String,Integer> getGrandTotalByAllWise(){
        Map<String,Integer>  map=new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+REPORT_ITEM_ID+") as TotalItems,SUM("+REPORT_OrderQty+") as TotalQty,SUM("+REPORT_ITEM_STOCK+") as TotalAmt from " + REPORT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list

        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalQty", cursor.getInt(cursor.getColumnIndex("TotalQty")));
            map.put("TotalAmt", cursor.getInt(cursor.getColumnIndex("TotalAmt")));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }









    //TODO:-------------------------------            Group Wise                ---------------------------------------
    public List<GroupWise> getGroupWise(){
    	List<GroupWise> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_ID+","+REPORT_MAIN_GROUP_NAME+" from " + REPORT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                Map<String,Integer> map = getCountGroupWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID)));
                int PendingItems = map.get("TotalItems");
                int PendingQty = map.get("TotalPendingQty");
            	dataList.add(new GroupWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,PendingItems  // Pending Items
                        ,PendingQty   // Pending Qty
                        ,""        //PartyID
                        ,""        //PartyName
                        ,""        //OrderID
                        ,""        //OrderNo
                ));
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
        db.close();
    	}
    	// returning 
    	return dataList;
    }
    public Map<String,Integer> getCountGroupWise(String GroupID){
        Map<String,Integer> map=new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+REPORT_ITEM_ID+" ) as TotalItems,SUM("+REPORT_PENDING_QTY+") as totalPending from " + REPORT_TABLE+" WHERE "+REPORT_GROUP_ID+"='"+GroupID+"' " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalPendingQty", cursor.getInt(cursor.getColumnIndex("totalPending")));
            //map.put("TotalOrderQty", cursor.getInt(cursor.getColumnIndex(REPORT_OrderQty)));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    public List<ItemDetails> getItemListByGroupWise(String GroupID,int sortFlag){
        List<ItemDetails> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "";
        if (sortFlag == 0) {
            selectQuery = "SELECT DISTINCT " + REPORT_ITEM_ID+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_NAME+"," + REPORT_MD_APPLICABLE + "," + REPORT_SUBITEM_APPLICABLE + " from " + REPORT_TABLE + " WHERE " + REPORT_GROUP_ID + "='" + GroupID + "' Order BY  " + REPORT_ITEM_CODE + " ASC ";
        }else if (sortFlag == 1){
            //TODO: Pending Greater than Stock
            selectQuery = "SELECT DISTINCT " + REPORT_ITEM_ID+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_NAME+"," + REPORT_MD_APPLICABLE + "," + REPORT_SUBITEM_APPLICABLE + " from " + REPORT_TABLE + " WHERE " + REPORT_GROUP_ID + "='" + GroupID + "' AND "+REPORT_PENDING_QTY+" > "+REPORT_COLOR_SIZE_STOCK;
        }else if (sortFlag == 2){
            //TODO: Pending Less than Stock
            selectQuery = "SELECT DISTINCT " + REPORT_ITEM_ID+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+","+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_NAME+"," + REPORT_MD_APPLICABLE + "," + REPORT_SUBITEM_APPLICABLE + " from " + REPORT_TABLE + " WHERE " + REPORT_GROUP_ID + "='" + GroupID + "' AND "+REPORT_PENDING_QTY+" < "+REPORT_COLOR_SIZE_STOCK;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                dataList.add(new ItemDetails(cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID))//SubItem ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME))//SubItem Name
                        ,""   //SubItem Code
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,""   //Party ID
                        ,""   //Party Name
                        ,""   //Order ID
                        ,""   //Order Date
                        ,""   //Order No
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_MD_APPLICABLE))
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_SUBITEM_APPLICABLE))
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
            //TODO: Group Wise Matrix
            public List<Map<String,String>> getSizeListByGroupWise(String ItemID,String GroupID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_SIZE_ID+","+REPORT_SIZE_NAME+","+REPORT_RATE+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' ORDER BY "+REPORT_SIZE_NAME+" ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("SizeID", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_ID)));
                        map.put("SizeName", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_NAME)));
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning 
                return dataList;
            }
            public List<Map<String,String>> getColorListByGroupWise(String ItemID,String GroupID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_COLOR_ID+","+REPORT_COLOR_NAME+","+REPORT_COLOR_FAMILY_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' ORDER BY " + REPORT_COLOR_NAME + " ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("ColorID", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_ID)));
                        map.put("ColorName", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_NAME)));
                        map.put("ColorFamily", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_FAMILY_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning 
                return dataList;
            }
            public Map<String, Integer> getPendingOrderStockQtyByGroupWise(String SizeID,String ColorID,String ItemID,String GroupID){
                Map<String, Integer> map = new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_COLOR_SIZE_BOOKED_QTY+") as OrderQty,"+REPORT_COLOR_SIZE_STOCK+" as StockQty from " + REPORT_TABLE+" WHERE "+REPORT_SIZE_ID+"='"+SizeID+"' AND "+REPORT_COLOR_ID+"='"+ColorID+"' AND "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQty",cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("OrderQty",cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("StockQty",cursor.getInt(cursor.getColumnIndex("StockQty")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return map;
            }
            public List<Map<String,String>> getPriceListByGroupWise(String ItemID,String GroupID){
                List<Map<String,String>> dataList =null;
                dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "select DISTINCT "+REPORT_RATE+"  from " + REPORT_TABLE+ " where "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' AND "+REPORT_COLOR_SIZE_BOOKED_QTY+"> 0 ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return dataList;
            }
            //TODO: Group Wise Item Only Matrix
            public Map<String,Integer> getWithoutSubItemDetailsByGroupWise(String ItemID,String GroupID){
                Map<String,Integer>  map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_ITEM_STOCK+") as ItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list

                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("ItemStock", cursor.getInt(cursor.getColumnIndex("ItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return map;
            }
            //TODO: Group Wise Sub Item Matrix
            public List<Map<String,String>> getSubItemDetailsByGroupWise(String ItemID,String GroupID){
                List<Map<String,String>> dataList = new ArrayList<>();
                // Select All Query
                String selectQuery = "SELECT Distinct "+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        HashMap< String, String> map=new HashMap<String,String>();
                        map.put("SubItemID", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID)));
                        map.put("SubItemName", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public Map<String,Integer> getSubItemStockDetailsByGroupWise(String ItemID,String SubItemID,String GroupID){
                Map<String,Integer> map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_SUB_ITEM_STOCK+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_SUB_ITEM_STOCK+") as SubItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_SUB_ITEM_ID+"='"+SubItemID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list

                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("SubItemStock", cursor.getInt(cursor.getColumnIndex("SubItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                //System.out.println("Color ID:"+dataList.toString());
                // returning
                return map;
            }
    


    
    
    



    //TODO:-------------------------------            Party Wise                ---------------------------------------
    public List<PartyWise> getPartyWise(){
        List<PartyWise> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_SUB_PARTY_ID+","+REPORT_SUB_PARTY_NAME+","+REPORT_REFRENCE_NAME+" from " + REPORT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                Map<String,Integer> map = getCountPartyWise(cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)),cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_ID)),cursor.getString(cursor.getColumnIndex(REPORT_REFRENCE_NAME)));
                int PendingItems = map.get("TotalItems");
                int PendingQty = map.get("TotalPendingQty");
                dataList.add(new PartyWise(cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_REFRENCE_NAME))
                        ,PendingItems // Pending Items
                        ,PendingQty // Pending Qty
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public Map<String,Integer> getCountPartyWise(String PartyID,String SubPartyID,String RefName){
        Map<String,Integer> map=new HashMap<>();
        // Select All Query
        String selectQuery = "";
        if (!SubPartyID.isEmpty()) {
            selectQuery = "SELECT COUNT(DISTINCT " + REPORT_ITEM_ID + " ) as TotalItems,SUM(" + REPORT_PENDING_QTY + ") as totalPending from " + REPORT_TABLE + " WHERE " + REPORT_PARTY_ID + "='" + PartyID + "' AND " + REPORT_SUB_PARTY_ID + "='" + SubPartyID + "'";
        }else if (!RefName.isEmpty()) {
            selectQuery = "SELECT COUNT(DISTINCT " + REPORT_ITEM_ID + " ) as TotalItems,SUM(" + REPORT_PENDING_QTY + ") as totalPending from " + REPORT_TABLE + " WHERE " + REPORT_PARTY_ID + "='" + PartyID + "' AND " + REPORT_REFRENCE_NAME + "='" + RefName + "'";
        } else {
            selectQuery = "SELECT COUNT(DISTINCT " + REPORT_ITEM_ID + " ) as TotalItems,SUM(" + REPORT_PENDING_QTY + ") as totalPending from " + REPORT_TABLE + " WHERE " + REPORT_PARTY_ID + "='" + PartyID + "' ";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalPendingQty", cursor.getInt(cursor.getColumnIndex("totalPending")));
            //map.put("TotalOrderQty", cursor.getInt(cursor.getColumnIndex(REPORT_OrderQty)));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    public Map<String,Integer> getCountGroupWiseByPartyWise(String GroupID,String PartyID){
        Map<String,Integer> map=new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+REPORT_ITEM_ID+" ) as TotalItems,SUM("+REPORT_PENDING_QTY+") as totalPending from " + REPORT_TABLE+" WHERE "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"' " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalPendingQty", cursor.getInt(cursor.getColumnIndex("totalPending")));
            //map.put("TotalOrderQty", cursor.getInt(cursor.getColumnIndex(REPORT_OrderQty)));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    public List<GroupWise> getGroupListByPartyWise(String PartyID){
        List<GroupWise> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_ID+","+REPORT_MAIN_GROUP_NAME+","+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+" from " + REPORT_TABLE + " WHERE "+ REPORT_PARTY_ID + "='"+PartyID+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                Map<String,Integer> map = getCountGroupWiseByPartyWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID)),cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)));
                int PendingItems = map.get("TotalItems");
                int PendingQty = map.get("TotalPendingQty");
                dataList.add(new GroupWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,PendingItems  // Pending Items
                        ,PendingQty   // Pending Qty
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))        //PartyID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))        //PartyName
                        ,""        //OrderID
                        ,""        //OrderNo
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<ItemDetails> getItemListByPartyWise(String GroupID,String PartyID){
        List<ItemDetails> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_ITEM_ID+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+","+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_NAME+","+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_MD_APPLICABLE+","+REPORT_SUBITEM_APPLICABLE+" from " + REPORT_TABLE+" WHERE "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataList.add(new ItemDetails(cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID))//SubItem ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME))//SubItem Name
                        ,"" //SubItem Code
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))   //Party ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME)) //Party Name
                        ,""   //Order ID
                        ,"" //Order Date
                        ,""   //Order No
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_MD_APPLICABLE))
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_SUBITEM_APPLICABLE))
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
            //TODO: Party Wise Matrix
            public List<Map<String,String>> getSizeListByPartyWise(String ItemID,String GroupID,String PartyID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_SIZE_ID+","+REPORT_SIZE_NAME+","+REPORT_RATE+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"' ORDER BY "+REPORT_SIZE_NAME+" ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("SizeID", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_ID)));
                        map.put("SizeName", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_NAME)));
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public List<Map<String,String>> getColorListByPartyWise(String ItemID,String GroupID,String PartyID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_COLOR_ID+","+REPORT_COLOR_NAME+","+REPORT_COLOR_FAMILY_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"' ORDER BY " + REPORT_COLOR_NAME + " ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("ColorID", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_ID)));
                        map.put("ColorName", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_NAME)));
                        map.put("ColorFamily", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_FAMILY_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public Map<String, Integer> getPendingOrderStockQtyByPartyWise(String SizeID,String ColorID,String ItemID,String GroupID,String PartyID){
                Map<String, Integer> map = new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_COLOR_SIZE_BOOKED_QTY+") as OrderQty,"+REPORT_COLOR_SIZE_STOCK+" as StockQty from " + REPORT_TABLE+" WHERE "+REPORT_SIZE_ID+"='"+SizeID+"' AND "+REPORT_COLOR_ID+"='"+ColorID+"' AND "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"= '"+PartyID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQty",cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("OrderQty",cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("StockQty",cursor.getInt(cursor.getColumnIndex("StockQty")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return map;
            }
            public List<Map<String,String>> getPriceListByPartyWise(String ItemID,String GroupID,String PartyID){
                List<Map<String,String>> dataList =null;
                dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "select DISTINCT "+REPORT_RATE+"  from " + REPORT_TABLE+ " where "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"= '"+PartyID+"' AND "+REPORT_COLOR_SIZE_BOOKED_QTY+"> 0 ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return dataList;
            }
            //TODO: Party Wise Item Only Matrix
            public Map<String,Integer> getWithoutSubItemDetailsByPartyWise(String ItemID,String GroupID,String PartyID){
                Map<String,Integer>  map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_ITEM_STOCK+") as ItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("ItemStock", cursor.getInt(cursor.getColumnIndex("ItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return map;
            }
            //TODO: Party Wise Sub Item Matrix
            public List<Map<String,String>> getSubItemDetailsByPartyWise(String ItemID,String GroupID,String PartyID){
                List<Map<String,String>> dataList = new ArrayList<>();
                // Select All Query
                String selectQuery = "SELECT Distinct "+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"'  ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        HashMap< String, String> map=new HashMap<String,String>();
                        map.put("SubItemID", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID)));
                        map.put("SubItemName", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public Map<String,Integer> getSubItemStockDetailsByPartyWise(String ItemID,String SubItemID,String GroupID,String PartyID){
                Map<String,Integer> map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_SUB_ITEM_STOCK+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_SUB_ITEM_STOCK+") as SubItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_SUB_ITEM_ID+"='"+SubItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_PARTY_ID+"='"+PartyID+"'  ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list

                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("SubItemStock", cursor.getInt(cursor.getColumnIndex("SubItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                //System.out.println("Color ID:"+dataList.toString());
                // returning
                return map;
            }







    



    
    
    
    
    //TODO:-------------------------------            Order Wise                ---------------------------------------
    public List<OrderWise> getOrderWise(){
        List<OrderWise> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_ORDER_NO+" ,"+REPORT_ORDER_ID+","+REPORT_ORDER_DATE+","+REPORT_A_STATUS+" ,"+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_SUB_PARTY_ID+","+REPORT_SUB_PARTY_NAME+","+REPORT_REFRENCE_NAME+","+REPORT_OrderType_Name+","+REPORT_Urgency_Level+","+REPORT_Showroom_Name+"  from " + REPORT_TABLE + " ORDER BY "+REPORT_ORDER_DATE+ " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                String OrderID = cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID));
                String ExpectedDeliveryDate = getExpectedDeliveryDateTime(OrderID);
                String PendingSince = DateFormatsMethods.DaysHoursMinutesCount(cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE)));
                Map<String,Integer> map = getCountOrderWise(OrderID);
                int pendingItems = map.get("TotalItems");
                int pendingQty = map.get("TotalPendingQty");
                int orderQty = map.get("TotalOrderQty");
                int pendingPercentage = Math.round((pendingQty/orderQty)*100);
                dataList.add(new OrderWise(cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_A_STATUS))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_OrderType_Name))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_Urgency_Level))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_Showroom_Name))
                        ,ExpectedDeliveryDate // Expected Delivery Date
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_REFRENCE_NAME))
                        ,PendingSince// Pending Since
                        ,pendingItems // Pending Items
                        ,pendingQty // Pending Qty
                        ,orderQty // Order Qty
                        ,pendingPercentage // Pending Percentage
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public Map<String,Integer> getCountOrderWise(String OrderID){
        Map<String,Integer> map=new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+REPORT_ITEM_ID+" ) as TotalItems,SUM("+REPORT_PENDING_QTY+") as totalPending,"+REPORT_OrderQty+" from " + REPORT_TABLE+" WHERE "+REPORT_ORDER_ID+"='"+OrderID+"' " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalPendingQty", cursor.getInt(cursor.getColumnIndex("totalPending")));
            map.put("TotalOrderQty", cursor.getInt(cursor.getColumnIndex(REPORT_OrderQty)));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    public String getExpectedDeliveryDateTime(String OrderID){
        String exDate="";
        // Select All Query
        String selectQuery = "Select "+REPORT_Expected_Delivery_DateTime+" from " + REPORT_TABLE + " where "+REPORT_ORDER_ID+"= '"+OrderID+"' ORDER BY "+REPORT_Expected_Delivery_DateTime+ " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {

            cursor.moveToFirst();
            exDate = cursor.getString(cursor.getColumnIndex(REPORT_Expected_Delivery_DateTime));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return exDate;
    }
    public String getExpectedDeliveryDateTime(String OrderID,String GroupID){
        String exDate="";
        // Select All Query
        String selectQuery = "Select "+REPORT_Expected_Delivery_DateTime+" from " + REPORT_TABLE + " where "+REPORT_ORDER_ID+"= '"+OrderID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"'  ORDER BY "+REPORT_Expected_Delivery_DateTime+ " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0){

            cursor.moveToFirst();
            exDate = cursor.getString(cursor.getColumnIndex(REPORT_Expected_Delivery_DateTime));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return exDate;
    }
    public String getExpectedDeliveryDateTime(String OrderID,String GroupID,String ItemID){
        String exDate="";
        // Select All Query
        String selectQuery = "Select "+REPORT_Expected_Delivery_DateTime+" from " + REPORT_TABLE + " where "+REPORT_ORDER_ID+"= '"+OrderID+"' AND "+REPORT_GROUP_ID+"= '"+GroupID+"' AND "+REPORT_ITEM_ID+"= '"+ItemID+"'   ORDER BY "+REPORT_Expected_Delivery_DateTime+ " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0) {

            cursor.moveToFirst();
            exDate = cursor.getString(cursor.getColumnIndex(REPORT_Expected_Delivery_DateTime));
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Color ID:"+dataList.toString());
        // returning
        return exDate;
    }
    public Map<String,Integer> getCountGroupByOrderWise(String GroupID,String OrderID){
        Map<String,Integer> map=new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+REPORT_ITEM_ID+" ) as TotalItems,SUM("+REPORT_PENDING_QTY+") as totalPending from " + REPORT_TABLE+" WHERE "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"' " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            map.put("TotalItems", cursor.getInt(cursor.getColumnIndex("TotalItems")));
            map.put("TotalPendingQty", cursor.getInt(cursor.getColumnIndex("totalPending")));
            //map.put("TotalOrderQty", cursor.getInt(cursor.getColumnIndex(REPORT_OrderQty)));
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    public List<GroupWise> getGroupListByOrderWise(String OrderID){
        List<GroupWise> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_ID+","+REPORT_MAIN_GROUP_NAME+","+REPORT_ORDER_ID+","+REPORT_ORDER_NO+" from " + REPORT_TABLE + " WHERE "+ REPORT_ORDER_ID + "='"+OrderID+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                Map<String,Integer> map = getCountGroupByOrderWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID)),cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
                int PendingItems = map.get("TotalItems");
                int PendingQty = map.get("TotalPendingQty");
                dataList.add(new GroupWise(cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,PendingItems  // Pending Items
                        ,PendingQty   // Pending Qty
                        ,""        //PartyID
                        ,""        //PartyName
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))      //OrderID  
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))     //OrderNo   
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<Map<String, String>> getShowroomList(){
        List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+REPORT_Showroom_ID+","+REPORT_Showroom_Name+" from " + REPORT_TABLE+" Order By "+REPORT_Showroom_Name+" ASC ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        Map< String, String> map=new HashMap<String,String>();
        map.put("ID", "Select showroom");
        map.put("Name", "Select showroom");
        dataList.add(map);
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map=new HashMap<String,String>();
                map.put("ID", cursor.getString(cursor.getColumnIndex(REPORT_Showroom_ID)));
                map.put("Name", cursor.getString(cursor.getColumnIndex(REPORT_Showroom_Name)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("List:"+dataList.toString());
        // returning
        return dataList;
    }
    public List<Map<String, String>> getAgentList(){
        List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct("+REPORT_AGENT+") from " + REPORT_TABLE+" Order By "+REPORT_AGENT+" ASC ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        Map< String, String> map=new HashMap<String,String>();
        map.put("ID", "Select agent");
        map.put("Name", "Select agent");
        dataList.add(map);
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map=new HashMap<String,String>();
                map.put("ID", cursor.getString(cursor.getColumnIndex(REPORT_AGENT)));
                map.put("Name", cursor.getString(cursor.getColumnIndex(REPORT_AGENT)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("List:"+dataList.toString());
        // returning
        return dataList;
    }
    public List<Map<String, String>> getOrderTypeList(){
        List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct("+REPORT_OrderType+"),"+REPORT_OrderType_Name+" from " + REPORT_TABLE+" Order By "+REPORT_OrderType_Name+" ASC ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        Map< String, String> map=new HashMap<String,String>();
        map.put("ID", "Select order type");
        map.put("Name", "Select order type");
        dataList.add(map);
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map=new HashMap<String,String>();
                map.put("ID", cursor.getString(cursor.getColumnIndex(REPORT_OrderType)));
                map.put("Name", cursor.getString(cursor.getColumnIndex(REPORT_OrderType_Name)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("List:"+dataList.toString());
        // returning
        return dataList;
    }
    public List<OrderWise> getOrderListFiltered(String CustFlag,String PendingFlag,String ExDelDateFlag,String Agent,String Showroom, String OrderType,String ExDelFromDt,String ExDelToDt){
        Log.d("Parameters", "CustFlag:"+ CustFlag+"\n PendingFlag:"+ PendingFlag+"\n ExDelDateFlag:"+ ExDelDateFlag+"\n Agent:"+Agent+"\n Showroom:"+ Showroom+"\n OrderType:"+ OrderType+"\n ExDelFromDt:"+ ExDelFromDt+"\n ExDelToDt:"+ExDelToDt);
        List<OrderWise> dataList=new ArrayList<>();
        String partialOrderByQuery = "",partialWhereQuery="";
        // Filtered Query
        if(CustFlag.equals("1") && PendingFlag.equals("1") && ExDelDateFlag.equals("1")){
            partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_ORDER_DATE+","+REPORT_Expected_Delivery_DateTime+" ASC";
        }if(CustFlag.equals("2") && PendingFlag.equals("2") && ExDelDateFlag.equals("2")){
            partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_ORDER_DATE+","+REPORT_Expected_Delivery_DateTime+" DESC";
        }else if(CustFlag.equals("1") || PendingFlag.equals("1") || ExDelDateFlag.equals("1")){
            if(CustFlag.equals("1") && PendingFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_ORDER_DATE+" ASC";
            }else if(PendingFlag.equals("1") && ExDelDateFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_ORDER_DATE+","+REPORT_Expected_Delivery_DateTime+" ASC";
            }else if(CustFlag.equals("1") && ExDelDateFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_Expected_Delivery_DateTime+" ASC";
            }else if(CustFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+" ASC";
            }else if(PendingFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_ORDER_DATE+" ASC";
            }else if(ExDelDateFlag.equals("1")){
                partialOrderByQuery = " Order By "+REPORT_Expected_Delivery_DateTime+" ASC";
            }
        }else if(CustFlag.equals("2") || PendingFlag.equals("2") || ExDelDateFlag.equals("2")){
            if(CustFlag.equals("2") && PendingFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_ORDER_DATE+" DESC";
            }else if(PendingFlag.equals("2") && ExDelDateFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_ORDER_DATE+","+REPORT_Expected_Delivery_DateTime+" DESC";
            }else if(CustFlag.equals("2") && ExDelDateFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+","+REPORT_Expected_Delivery_DateTime+" DESC";
            }else if(CustFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_PARTY_NAME+" DESC";
            }else if(PendingFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_ORDER_DATE+" DESC";
            }else if(ExDelDateFlag.equals("2")){
                partialOrderByQuery = " Order By "+REPORT_Expected_Delivery_DateTime+" DESC";
            }
        }
        Agent = (Agent.isEmpty()==false)?" And "+REPORT_AGENT+"='"+Agent+"' ":"";
        Showroom = (Showroom.isEmpty()==false)?" And "+REPORT_Showroom_Name+"='"+Showroom+"' ":"";
        OrderType = (OrderType.isEmpty()==false)?" And "+REPORT_OrderType_Name+"='"+OrderType+"' ":"";
        ExDelFromDt = (ExDelFromDt.isEmpty()==false)?" And "+REPORT_Expected_Delivery_DateTime+">='"+ExDelFromDt+" 00:01:01.0"+"' ":"";
        ExDelToDt = (ExDelToDt.isEmpty()==false)?" And "+REPORT_Expected_Delivery_DateTime+"<='"+ExDelToDt+" 23:59:59.0"+"' ":"";
        partialWhereQuery  = " Where 1=1 "+Agent+Showroom+OrderType+ExDelFromDt+ExDelToDt;

        String selectQuery = "SELECT DISTINCT "+REPORT_ORDER_NO+" ,"+REPORT_ORDER_ID+","+REPORT_ORDER_DATE+","+REPORT_A_STATUS+" ,"+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_SUB_PARTY_ID+","+REPORT_SUB_PARTY_NAME+","+REPORT_REFRENCE_NAME+","+REPORT_OrderType_Name+","+REPORT_Urgency_Level+","+REPORT_Showroom_Name+"  from " + REPORT_TABLE + partialWhereQuery + " "+partialOrderByQuery;// ORDER BY "+REPORT_ORDER_DATE+ " DESC";
        System.out.println("Query: "+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list

        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                String OrderID = cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID));
                String ExpectedDeliveryDate = getExpectedDeliveryDateTime(OrderID);
                String PendingSince = DateFormatsMethods.DaysHoursMinutesCount(cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE)));
                Map<String,Integer> map = getCountOrderWise(OrderID);
                int pendingItems = map.get("TotalItems");
                int pendingQty = map.get("TotalPendingQty");
                int orderQty = map.get("TotalOrderQty");
                int pendingPercentage = Math.round((pendingQty/orderQty)*100);
                dataList.add(new OrderWise(cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_A_STATUS))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_OrderType_Name))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_Urgency_Level))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_Showroom_Name))
                        ,ExpectedDeliveryDate // Expected Delivery Date
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_PARTY_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_REFRENCE_NAME))
                        ,PendingSince// Pending Since
                        ,pendingItems // Pending Items
                        ,pendingQty // Pending Qty
                        ,orderQty // Order Qty
                        ,pendingPercentage // Pending Percentage
                ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<ItemDetails> getItemListByOrderWise(String GroupID, String OrderID){
        List<ItemDetails> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+REPORT_ITEM_ID+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+","+REPORT_GROUP_ID+","+REPORT_GROUP_NAME+","+REPORT_MAIN_GROUP_NAME+","+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_ORDER_ID+","+REPORT_ORDER_DATE+","+REPORT_ORDER_NO+","+REPORT_MD_APPLICABLE+","+REPORT_SUBITEM_APPLICABLE+" from " + REPORT_TABLE+" WHERE "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                dataList.add(new ItemDetails(cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID))//SubItem ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME))//SubItem Name
                        ,"" //SubItem Code
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_ID))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_MAIN_GROUP_NAME))
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID))   //Party ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME)) //Party Name
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID))   //Order ID
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE)) //Order Date
                        ,cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO))   //Order No
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_MD_APPLICABLE))
                        ,cursor.getInt(cursor.getColumnIndex(REPORT_SUBITEM_APPLICABLE))
                        ));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
            //TODO: Order Wise Matrix
            public List<Map<String,String>> getSizeListByOrderWise(String ItemID,String GroupID,String OrderID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_SIZE_ID+","+REPORT_SIZE_NAME+","+REPORT_RATE+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"' ORDER BY "+REPORT_SIZE_NAME+" ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("SizeID", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_ID)));
                        map.put("SizeName", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_NAME)));
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public List<Map<String,String>> getColorListByOrderWise(String ItemID,String GroupID,String OrderID){
                List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "SELECT DISTINCT "+REPORT_COLOR_ID+","+REPORT_COLOR_NAME+","+REPORT_COLOR_FAMILY_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"' ORDER BY " + REPORT_COLOR_NAME + " ASC";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("ColorID", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_ID)));
                        map.put("ColorName", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_NAME)));
                        map.put("ColorFamily", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_FAMILY_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public Map<String, Integer> getPendingOrderStockQtyByOrderWise(String SizeID,String ColorID,String ItemID,String GroupID,String OrderID){
                Map<String, Integer> map = new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_COLOR_SIZE_BOOKED_QTY+") as OrderQty,"+REPORT_COLOR_SIZE_STOCK+" as StockQty from " + REPORT_TABLE+" WHERE "+REPORT_SIZE_ID+"='"+SizeID+"' AND "+REPORT_COLOR_ID+"='"+ColorID+"' AND "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"= '"+OrderID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQty",cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("OrderQty",cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("StockQty",cursor.getInt(cursor.getColumnIndex("StockQty")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return map;
            }
            public List<Map<String,String>> getPriceListByOrderWise(String ItemID,String GroupID,String OrderID){
                List<Map<String,String>> dataList =null;
                dataList=new ArrayList<Map<String,String>>();
                // Select All Query
                String selectQuery = "select DISTINCT "+REPORT_RATE+"  from " + REPORT_TABLE+ " where "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"= '"+OrderID+"' AND "+REPORT_COLOR_SIZE_BOOKED_QTY+"> 0 ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        Map< String, String> map=new HashMap<String,String>();
                        map.put("Rate", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                return dataList;
            }
            //TODO: Order Wise Item Only Matrix
            public Map<String,Integer> getWithoutSubItemDetailsByOrderWise(String ItemID,String GroupID,String OrderID){
                Map<String,Integer>  map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_PENDING_QTY+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_ITEM_STOCK+") as ItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"' ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("ItemStock", cursor.getInt(cursor.getColumnIndex("ItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return map;
            }
            //TODO: Order Wise Sub Item Matrix
            public List<Map<String,String>> getSubItemDetailsByOrderWise(String ItemID,String GroupID,String OrderID){
                List<Map<String,String>> dataList = new ArrayList<>();
                // Select All Query
                String selectQuery = "SELECT Distinct "+REPORT_SUB_ITEM_ID+","+REPORT_SUB_ITEM_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"'  ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    do {
                        HashMap< String, String> map=new HashMap<String,String>();
                        map.put("SubItemID", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_ID)));
                        map.put("SubItemName", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME)));
                        dataList.add(map);
                    } while (cursor.moveToNext());
                    // closing connection
                    cursor.close();
                    db.close();
                }
                // returning
                return dataList;
            }
            public Map<String,Integer> getSubItemStockDetailsByOrderWise(String ItemID,String SubItemID,String GroupID,String OrderID){
                Map<String,Integer> map=new HashMap<>();
                // Select All Query
                String selectQuery = "SELECT SUM("+REPORT_SUB_ITEM_STOCK+") as PendingQty,SUM("+REPORT_OrderQty+") as OrderQty,SUM("+REPORT_SUB_ITEM_STOCK+") as SubItemStock from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_ID+"='"+ItemID+"' AND "+REPORT_SUB_ITEM_ID+"='"+SubItemID+"' AND "+REPORT_GROUP_ID+"='"+GroupID+"' AND "+REPORT_ORDER_ID+"='"+OrderID+"'  ";
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery,null);
                // looping through all rows and adding to list

                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    map.put("PendingQry", cursor.getInt(cursor.getColumnIndex("PendingQty")));
                    map.put("BookedQty", cursor.getInt(cursor.getColumnIndex("OrderQty")));
                    map.put("SubItemStock", cursor.getInt(cursor.getColumnIndex("SubItemStock")));
                    // closing connection
                    cursor.close();
                    db.close();
                }
                //System.out.println("Color ID:"+dataList.toString());
                // returning
                return map;
            }















    //TODO:--------------------------------       Others      ---------------------------------------

    public ArrayList<HashMap<String,String>> getOrderWiseCustomerNameWithTotalQty(String OrderNo,String ItemCode){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT SUM("+REPORT_COLOR_SIZE_BOOKED_QTY+") as totalOrderQty,SUM("+REPORT_PENDING_QTY+") as totalPending from " + REPORT_TABLE+" WHERE "+REPORT_ORDER_NO+"='"+OrderNo+"' AND "+REPORT_ITEM_CODE+"='"+ItemCode+"' ";
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
   //TODO:-------------------------------- Getting Data of report GroupWise Data ---------------------------------------
    public ArrayList<HashMap<String,String>> getCustomerWiseDataSortByPendingQty(String CustName){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+REPORT_ITEM_NAME+"),"+REPORT_PARTY_NAME+","+REPORT_ORDER_NO+","+REPORT_ORDER_DATE+","+REPORT_ITEM_NAME+","+REPORT_ITEM_CODE+","+REPORT_ITEM_STOCK+","+REPORT_PENDING_QTY+","+REPORT_COLOR_NAME+","+REPORT_SIZE_NAME+","+REPORT_SUB_ITEM_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_PARTY_NAME+"=\""+CustName+"\" ORDER BY "+REPORT_PENDING_QTY ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	HashMap< String, String> map=new HashMap<String,String>();
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO)));
            	map.put("ItemName", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_NAME)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE)));
            	//map.put("Price", cursor.getString(cursor.getColumnIndex(REPORT_RATE)));
            	map.put("ItemStock", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_STOCK)));
            	map.put("PendingQty", cursor.getString(cursor.getColumnIndex(REPORT_PENDING_QTY)));
            	map.put("ColorName", cursor.getString(cursor.getColumnIndex(REPORT_COLOR_NAME)));
            	map.put("SizeName", cursor.getString(cursor.getColumnIndex(REPORT_SIZE_NAME)));
            	map.put("SubItemName", cursor.getString(cursor.getColumnIndex(REPORT_SUB_ITEM_NAME)));
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
  //TODO:-------------------------------- Getting Data of report GroupWise Data ---------------------------------------
    public List<Map<String, String>> getItemDetailsCustomerWiseWithTotalQty(String ItemCode){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+REPORT_ORDER_NO+"),"+REPORT_ORDER_ID+","+REPORT_PARTY_ID+","+REPORT_PARTY_NAME+","+REPORT_ITEM_ID+","+REPORT_ITEM_CODE+","+REPORT_ORDER_DATE+","+REPORT_REFRENCE_NAME+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_CODE+"='"+ItemCode+"' ORDER BY "+REPORT_PARTY_NAME+" ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("CustID", cursor.getString(cursor.getColumnIndex(REPORT_PARTY_ID)));
            	map.put("CustName", cursor.getString(cursor.getColumnIndex(REPORT_PARTY_NAME)));
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
            	map.put("OrderNo", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_NO)));
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)));
            	map.put("ItemCode", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_CODE)));
            	map.put("OrderDate", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_DATE)));
            	map.put("RefrenceName", cursor.getString(cursor.getColumnIndex(REPORT_REFRENCE_NAME)));
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
    //TODO:-------------------------------- Getting Data of report GroupWise Data ---------------------------------------
    public List<Map<String, String>> getItemIDWithOrderID(String ItemCode,String OrderNO){
    	List<Map<String, String>> dataList=new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT "+REPORT_ITEM_ID+","+REPORT_ORDER_ID+" from " + REPORT_TABLE+" WHERE "+REPORT_ITEM_CODE+"='"+ItemCode+"' AND "+REPORT_ORDER_NO+"='"+OrderNO+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	Map< String, String> map=new HashMap<String,String>();
            	map.put("ItemID", cursor.getString(cursor.getColumnIndex(REPORT_ITEM_ID)));
            	map.put("OrderID", cursor.getString(cursor.getColumnIndex(REPORT_ORDER_ID)));
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
