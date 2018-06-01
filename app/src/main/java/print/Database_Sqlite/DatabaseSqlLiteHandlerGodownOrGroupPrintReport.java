package print.Database_Sqlite;

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

import print.godown_group_order_with_rate.model.Group;
import print.godown_group_order_with_rate.model.GroupOrGodown;

/**
 * Created by Rakesh on 13-Jun-16.
 */
public class DatabaseSqlLiteHandlerGodownOrGroupPrintReport extends SQLiteOpenHelper {
    //-------------------------Todo: Database Version----------------------
    private static final int DATABASE_VERSION = 1;
    // ----------------------- Todo: Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_GodownOrGroupPrintReport0";
    //-------------------------Todo: Table Name--------------------------------------------------------------
    private static final String ORDER_BOOKED_TABLE = "orderViewDetailsTbl";
    //------------------------ Todo: Order details Table Column Names---------------------------------------------
    private static final String KEY_ID = "id";
    private static final String BOOKED_VIEW_ORDER_ID = "orderId";
    private static final String BOOKED_VIEW_ORDER_DATE = "orderDate";
    private static final String BOOKED_VIEW_ORDER_NO = "orderNo";
    private static final String BOOKED_VIEW_MAIN_GROUP_ID = "mainGroupID";
    private static final String BOOKED_VIEW_MAIN_GROUP = "mainGroup";
    private static final String BOOKED_VIEW_GROUP_ID = "groupID";
    private static final String BOOKED_VIEW_GROUP_NAME = "groupName";
    private static final String BOOKED_VIEW_GROUP_IMAGE = "groupImage";
    private static final String BOOKED_VIEW_SUB_GROUP_ID = "subGroupID";
    private static final String BOOKED_VIEW_SUB_GROUP = "subGroupName";
    private static final String BOOKED_VIEW_ITEM_ID = "itemID";
    private static final String BOOKED_VIEW_ITEM_NAME = "itemName";
    private static final String BOOKED_VIEW_ITEM_CODE = "itemCode";
    private static final String BOOKED_VIEW_SUB_ITEM_ID = "subItemID";
    private static final String BOOKED_VIEW_SUB_ITEM_NAME = "subItemName";
    private static final String BOOKED_VIEW_SUB_ITEM_CODE = "subItemCode";
    private static final String BOOKED_VIEW_COLOR_ID = "colorID";
    private static final String BOOKED_VIEW_COLOR_NAME = "colorName";
    private static final String BOOKED_VIEW_SIZE_ID = "sizeId";
    private static final String BOOKED_VIEW_SIZE_NAME = "sizeName";
    private static final String BOOKED_VIEW_MD_APPLICABLE = "mdApplicable";
    private static final String BOOKED_VIEW_SUB_ITEM_APPLICABLE = "subItemApplicable";
    private static final String BOOKED_VIEW_ATTRIBUTE_2 = "attribute2";
    private static final String BOOKED_VIEW_ATTRIBUTE_6 = "attribute6";
    private static final String BOOKED_VIEW_ATTRIBUTE_7 = "attribute7";
    private static final String BOOKED_VIEW_ATTRIBUTE_8 = "attribute8";
    private static final String BOOKED_VIEW_ORDER_QTY = "orderQty";
    private static final String BOOKED_VIEW_OrderItemPendingQty = "orderItemPendingQty";
    private static final String BOOKED_VIEW_ItemStockGodown = "itemStockGodown";
    private static final String BOOKED_VIEW_MDOrderQty = "mDOrderQty";
    private static final String BOOKED_VIEW_MDPendingQty = "mDPendingQty";
    private static final String BOOKED_VIEW_MDStock = "mDStock";
    private static final String BOOKED_VIEW_GodownID = "godownID";
    private static final String BOOKED_VIEW_GodownName = "godownName";
    private static final String BOOKED_VIEW_GodownType = "godownType";
    private static final String BOOKED_VIEW_GodownTypeName = "godownTypeName";
    private static final String BOOKED_VIEW_RATE = "rate";

    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerGodownOrGroupPrintReport(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //------------------------------------TODO: Creating Table---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_TABLE_ORDER_BOOKED_TABLE = "CREATE TABLE " + ORDER_BOOKED_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + BOOKED_VIEW_ORDER_ID + " TEXT," + BOOKED_VIEW_ORDER_DATE + " TEXT," + BOOKED_VIEW_ORDER_NO + " TEXT," + BOOKED_VIEW_MAIN_GROUP_ID + " TEXT," + BOOKED_VIEW_MAIN_GROUP + " TEXT," + BOOKED_VIEW_GROUP_ID + " TEXT," + BOOKED_VIEW_GROUP_NAME + " TEXT," + BOOKED_VIEW_GROUP_IMAGE + " TEXT," + BOOKED_VIEW_SUB_GROUP_ID + " TEXT," + BOOKED_VIEW_SUB_GROUP + " TEXT," + BOOKED_VIEW_ITEM_ID + " TEXT," + BOOKED_VIEW_ITEM_NAME + " TEXT," + BOOKED_VIEW_ITEM_CODE + " TEXT," + BOOKED_VIEW_SUB_ITEM_ID + " TEXT," + BOOKED_VIEW_SUB_ITEM_NAME + " TEXT," + BOOKED_VIEW_SUB_ITEM_CODE + " TEXT," + BOOKED_VIEW_COLOR_ID + " TEXT," + BOOKED_VIEW_COLOR_NAME + " TEXT," + BOOKED_VIEW_SIZE_ID + " TEXT," + BOOKED_VIEW_SIZE_NAME + " TEXT," + BOOKED_VIEW_MD_APPLICABLE + " INTEGER," + BOOKED_VIEW_SUB_ITEM_APPLICABLE + " INTEGER," + BOOKED_VIEW_ATTRIBUTE_2 + " TEXT," + BOOKED_VIEW_ATTRIBUTE_6 + " TEXT," + BOOKED_VIEW_ATTRIBUTE_7 + " TEXT," + BOOKED_VIEW_ATTRIBUTE_8 + " TEXT," + BOOKED_VIEW_ORDER_QTY + " TEXT," + BOOKED_VIEW_OrderItemPendingQty + " TEXT," + BOOKED_VIEW_ItemStockGodown + " TEXT," + BOOKED_VIEW_MDOrderQty + " TEXT," + BOOKED_VIEW_MDPendingQty + " TEXT," + BOOKED_VIEW_MDStock + " TEXT," + BOOKED_VIEW_GodownID + " TEXT," + BOOKED_VIEW_GodownName + " TEXT," + BOOKED_VIEW_GodownType + " TEXT," + BOOKED_VIEW_GodownTypeName + " TEXT," + BOOKED_VIEW_RATE + " TEXT)";
        db.execSQL(CREATE_TABLE_ORDER_BOOKED_TABLE);
    }

    //------------------------------------TODO: Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ORDER_BOOKED_TABLE);
        // Create tables again
        onCreate(db);
    }

    //------------------------------------TODO: Delete Table---------------------------------------------
    public void deleteOrderDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ORDER_BOOKED_TABLE, null, null);
        db.close();
    }

    //---------------------------------- TODO: Inserting Data of Order details Table---------------------------------------
    public void insertOrderDetails(List<Map<String, String>> mapList) {

        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, ORDER_BOOKED_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int OrderID = ih.getColumnIndex(BOOKED_VIEW_ORDER_ID);
        final int OrderDate = ih.getColumnIndex(BOOKED_VIEW_ORDER_DATE);
        final int OrderNo = ih.getColumnIndex(BOOKED_VIEW_ORDER_NO);
        final int MainGroupID = ih.getColumnIndex(BOOKED_VIEW_MAIN_GROUP_ID);
        final int MainGroup = ih.getColumnIndex(BOOKED_VIEW_MAIN_GROUP);
        final int GroupID = ih.getColumnIndex(BOOKED_VIEW_GROUP_ID);
        final int GroupName = ih.getColumnIndex(BOOKED_VIEW_GROUP_NAME);
        final int GroupImage = ih.getColumnIndex(BOOKED_VIEW_GROUP_IMAGE);
        final int SubGroupID = ih.getColumnIndex(BOOKED_VIEW_SUB_GROUP_ID);
        final int SubGroup = ih.getColumnIndex(BOOKED_VIEW_SUB_GROUP);
        final int ItemID = ih.getColumnIndex(BOOKED_VIEW_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(BOOKED_VIEW_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(BOOKED_VIEW_ITEM_NAME);
        final int SubItemID = ih.getColumnIndex(BOOKED_VIEW_SUB_ITEM_ID);
        final int SubItemCode = ih.getColumnIndex(BOOKED_VIEW_SUB_ITEM_CODE);
        final int SubItemName = ih.getColumnIndex(BOOKED_VIEW_SUB_ITEM_NAME);
        final int ColorID = ih.getColumnIndex(BOOKED_VIEW_COLOR_ID);
        final int Color = ih.getColumnIndex(BOOKED_VIEW_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(BOOKED_VIEW_SIZE_ID);
        final int SizeName = ih.getColumnIndex(BOOKED_VIEW_SIZE_NAME);
        final int MDApplicable = ih.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE);
        final int AttribName2 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_2);
        final int AttribName6 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_6);
        final int AttribName7 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_7);
        final int AttribName8 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_8);
        final int OrderQty = ih.getColumnIndex(BOOKED_VIEW_ORDER_QTY);
        final int OrderItemPendingQty = ih.getColumnIndex(BOOKED_VIEW_OrderItemPendingQty);
        final int ItemStockGodown = ih.getColumnIndex(BOOKED_VIEW_ItemStockGodown);
        final int MDOrderQty = ih.getColumnIndex(BOOKED_VIEW_MDOrderQty);
        final int MDPendingQty = ih.getColumnIndex(BOOKED_VIEW_MDPendingQty);
        final int MDStock = ih.getColumnIndex(BOOKED_VIEW_MDStock);
        final int GodownID = ih.getColumnIndex(BOOKED_VIEW_GodownID);
        final int GodownName = ih.getColumnIndex(BOOKED_VIEW_GodownName);
        final int GodownType = ih.getColumnIndex(BOOKED_VIEW_GodownType);
        final int GodownTypeName = ih.getColumnIndex(BOOKED_VIEW_GodownTypeName);
        final int Rate = ih.getColumnIndex(BOOKED_VIEW_RATE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for (int x = 0; x < mapList.size(); x++) {
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(OrderID, mapList.get(x).get("OrderID"));
                ih.bind(OrderDate, mapList.get(x).get("OrderDate"));
                ih.bind(OrderNo, mapList.get(x).get("OrderNo"));
                ih.bind(MainGroupID, mapList.get(x).get("MainGroupID"));
                ih.bind(MainGroup, mapList.get(x).get("MainGroup"));
                ih.bind(GroupID, mapList.get(x).get("GroupID"));
                ih.bind(GroupName, mapList.get(x).get("GroupName"));
                ih.bind(GroupImage, mapList.get(x).get("GroupImage"));
                ih.bind(SubGroupID, mapList.get(x).get("SubGroupID"));
                ih.bind(SubGroup, mapList.get(x).get("SubGroup"));
                ih.bind(ItemID, mapList.get(x).get("ItemID"));
                ih.bind(ItemName, mapList.get(x).get("ItemName"));
                ih.bind(ItemCode, mapList.get(x).get("ItemCode"));
                ih.bind(SubItemID, mapList.get(x).get("SubItemID"));
                ih.bind(SubItemName, mapList.get(x).get("SubItemName"));
                ih.bind(SubItemCode, mapList.get(x).get("SubItemCode"));
                ih.bind(ColorID, mapList.get(x).get("ColorID"));
                ih.bind(Color, mapList.get(x).get("Color"));
                ih.bind(SizeID, mapList.get(x).get("SizeID"));
                ih.bind(SizeName, mapList.get(x).get("SizeName"));
                ih.bind(MDApplicable, mapList.get(x).get("MDApplicable"));
                ih.bind(SubItemApplicable, mapList.get(x).get("SubItemApplicable"));
                ih.bind(AttribName2, mapList.get(x).get("AttribName2"));
                ih.bind(AttribName6, mapList.get(x).get("AttribName6"));
                ih.bind(AttribName7, mapList.get(x).get("AttribName7"));
                ih.bind(AttribName8, mapList.get(x).get("AttribName8"));
                ih.bind(OrderQty, mapList.get(x).get("OrderQty"));
                ih.bind(OrderItemPendingQty, mapList.get(x).get("OrderItemPendingQty"));
                ih.bind(ItemStockGodown, mapList.get(x).get("ItemStockGodown"));
                ih.bind(MDOrderQty, mapList.get(x).get("MDOrderQty"));
                ih.bind(MDPendingQty, mapList.get(x).get("MDPendingQty"));
                ih.bind(MDStock, mapList.get(x).get("MDStock"));
                ih.bind(GodownID, mapList.get(x).get("GodownID"));
                ih.bind(GodownName, mapList.get(x).get("GodownName"));
                ih.bind(GodownType, mapList.get(x).get("GodownType"));
                ih.bind(GodownTypeName, mapList.get(x).get("GodownTypeName"));
                ih.bind(Rate, mapList.get(x).get("Rate"));
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

    //TODO: Get Group or MAinGroup List from print report Table
    public List<Group> getGroupMainGroupList(String OrderID) {
        List<Group> groupOrGodownList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_GROUP_ID + "," + BOOKED_VIEW_GROUP_NAME + "," + BOOKED_VIEW_MAIN_GROUP_ID + "," + BOOKED_VIEW_MAIN_GROUP + "," + BOOKED_VIEW_ORDER_ID + "," + BOOKED_VIEW_ORDER_NO + "," + BOOKED_VIEW_ORDER_DATE + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupOrGodownList.add(new Group(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP)),0,cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_NO)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_DATE))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return groupOrGodownList;
    }
    public List<Group> getGroupMainGroupList(String OrderID, String GodownID) {
        List<Group> groupList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_GROUP_ID + "," + BOOKED_VIEW_GROUP_NAME + "," + BOOKED_VIEW_MAIN_GROUP_ID + "," + BOOKED_VIEW_MAIN_GROUP + "," + BOOKED_VIEW_ORDER_ID + "," + BOOKED_VIEW_ORDER_NO + "," + BOOKED_VIEW_ORDER_DATE + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GodownID + "= '" + GodownID + "'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupList.add(new Group(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP)),1,cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_NO)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_DATE))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return groupList;
    }

    //TODO: Get Godown List by OrderID ,MainGroupID and GroupId from print report Table
    public List<GroupOrGodown> getGodownList(String OrderID) {
        List<GroupOrGodown> mapList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_GodownID + "," + BOOKED_VIEW_GodownName + "," + BOOKED_VIEW_GodownType + "," + BOOKED_VIEW_GodownTypeName + ","+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_ORDER_NO+","+BOOKED_VIEW_ORDER_DATE+"  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                mapList.add(new GroupOrGodown(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownName)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownType)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownTypeName)),0,cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_NO)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_DATE))));
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("GodownID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownID)));
//                map.put("GodownName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownName)));
//                map.put("GodownType", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownType)));
//                map.put("GodownTypeName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownTypeName)));
//                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }
    public List<Map<String, String>> getGodownList(String OrderID, String MainGroupID, String GroupID) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_GodownID + "," + BOOKED_VIEW_GodownName + "," + BOOKED_VIEW_GodownType + "," + BOOKED_VIEW_GodownTypeName + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + "= '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupID + "'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("GodownID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownID)));
                map.put("GodownName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownName)));
                map.put("GodownType", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownType)));
                map.put("GodownTypeName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GodownTypeName)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }

    //TODO: Get Item List from print report Table
    public List<Map<String, String>> getItemListWithMDOrSubItemApplicable(String OrderID, String MainGroupID, String GroupID, String GodownID) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_ITEM_ID + "," + BOOKED_VIEW_ITEM_CODE + "," + BOOKED_VIEW_ITEM_NAME + "," + BOOKED_VIEW_MD_APPLICABLE + "," + BOOKED_VIEW_SUB_ITEM_APPLICABLE + "," + BOOKED_VIEW_ATTRIBUTE_2 + "," + BOOKED_VIEW_ATTRIBUTE_6 + "," + BOOKED_VIEW_ATTRIBUTE_7 + "," + BOOKED_VIEW_ATTRIBUTE_8 + " from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ItemID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)));
                map.put("ItemCode", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_CODE)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_NAME)));
                map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)));
                map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                map.put("Attr2", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_2)));
                map.put("Attr6", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_6)));
                map.put("Attr7", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_7)));
                map.put("Attr8", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_8)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        System.out.println("Total:"+mapList.toString());
        return mapList;
    }

    //TODO: Get Color List from print report Table  MDApplicable
    public List<Map<String, String>> getColorList(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_COLOR_ID + "," + BOOKED_VIEW_COLOR_NAME + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_NAME)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }

    //TODO: Get Size List from print report Table   MDApplicable
    public List<Map<String, String>> getSizeList(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_SIZE_ID + "," + BOOKED_VIEW_SIZE_NAME + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "'  AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SIZE_ID)));
                map.put("SizeName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SIZE_NAME)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }

    //TODO: Get MD Quantity from print report Table   MDApplicable
    public Map<String, String> getMDQauntity(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID, String ColorID, String SizeID) {
        Map<String, String> map = new HashMap<String, String>();
        // Select All Query
        String selectQuery = "SELECT  " + BOOKED_VIEW_MDPendingQty + "," + BOOKED_VIEW_MDOrderQty + "," + BOOKED_VIEW_MDStock + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' AND " + BOOKED_VIEW_COLOR_ID + " = '" + ColorID + "' AND " + BOOKED_VIEW_SIZE_ID + " = '" + SizeID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                map.put("PendingQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MDPendingQty)));
                map.put("OrderQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MDOrderQty)));
                map.put("StockQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MDStock)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }

    //TODO: Get SubItem List from print report Table   SubItemApplicable
    public List<Map<String, String>> getSubItemList(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_SUB_ITEM_ID + "," + BOOKED_VIEW_SUB_ITEM_NAME + "," + BOOKED_VIEW_SUB_ITEM_CODE + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SubItemID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_ID)));
                map.put("SubItemName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_NAME)));
                map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_CODE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }

    //TODO: Get Subitem Quantity from print report Table   SubItemApplicable
    public Map<String, String> getSubItemQauntity(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID, String SubItemID) {
        Map<String, String> map = new HashMap<String, String>();
        // Select All Query
        String selectQuery = "SELECT  " + BOOKED_VIEW_OrderItemPendingQty+ "," + BOOKED_VIEW_ORDER_QTY+ "," + BOOKED_VIEW_ItemStockGodown+ "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' AND " + BOOKED_VIEW_SUB_ITEM_ID + " = '" + SubItemID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                map.put("PendingQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_OrderItemPendingQty)));
                map.put("OrderQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_QTY)));
                map.put("StockQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ItemStockGodown)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }

    //TODO: Get Item Only Quantity from print report Table   Item Only
    public Map<String, String> getItemOnlyQauntity(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID) {
        Map<String, String> map = new HashMap<String, String>();
        // Select All Query
        String selectQuery = "SELECT  " + BOOKED_VIEW_OrderItemPendingQty+ "," + BOOKED_VIEW_ORDER_QTY+ "," + BOOKED_VIEW_ItemStockGodown+ "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                map.put("PendingQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_OrderItemPendingQty)));
                map.put("OrderQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_QTY)));
                map.put("StockQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ItemStockGodown)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }


    //TODO: Get Rate from print report Table
    public List<Map<String, String>> getRate(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID) {
        List<Map<String, String>> mapList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  Distinct " + BOOKED_VIEW_RATE + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Map<String,String> map = new HashMap<>();
                map.put("Rate", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_RATE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //TODO: Get MD Quantity from print report Table   MDApplicable
    public Map<String, Integer> getQauntityByRate(String OrderID, String MainGroupID, String GroupID, String GodownID, String ItemID, String Rate) {
        Map<String, Integer> map = new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT  " + BOOKED_VIEW_MDOrderQty + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + " = '" + OrderID + "' AND " + BOOKED_VIEW_MAIN_GROUP_ID + " = '" + MainGroupID + "' AND " + BOOKED_VIEW_GROUP_ID + " = '" + GroupID + "' AND " + BOOKED_VIEW_GodownID + " = '" + GodownID + "' AND " + BOOKED_VIEW_ITEM_ID + " = '" + ItemID + "' AND " + BOOKED_VIEW_RATE + " = '" + Rate + "' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //do {
                map.put("OrderQty", cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MDOrderQty)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }
}
