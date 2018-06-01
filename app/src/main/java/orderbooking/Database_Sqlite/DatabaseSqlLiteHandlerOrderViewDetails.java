package orderbooking.Database_Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orderbooking.view_order_details.dataset.OrderViewColorOptionDataset;
import orderbooking.view_order_details.dataset.OrderViewGroupDataset;
import orderbooking.view_order_details.dataset.OrderViewItemByGroupDataset;

/**
 * Created by Rakesh on 13-Jun-16.
 */
public class DatabaseSqlLiteHandlerOrderViewDetails extends SQLiteOpenHelper {
    //-------------------------Todo: Database Version----------------------
    private static final int DATABASE_VERSION = 1;
    // ----------------------- Todo: Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_OrderViewDetails";
    //-------------------------Todo: Table Name--------------------------------------------------------------
    private static final String ORDER_BOOKED_TABLE = "orderViewDetailsTbl";
    //------------------------ Todo: Order details Table Column Names---------------------------------------------
    private static final String KEY_ID = "id";
    private static final String BOOKED_VIEW_ORDER_ID = "orderId";
    private static final String BOOKED_VIEW_PARTY_ID = "partyId";
    private static final String BOOKED_VIEW_PARTY_NAME = "partyName";
    private static final String BOOKED_VIEW_SUB_PARTY_ID = "subPartyID";
    private static final String BOOKED_VIEW_SUB_PARTY_NAME = "subPArtyName";
    private static final String BOOKED_VIEW_REFERENCE_NAME= "refName";
    private static final String BOOKED_VIEW_REMARKS = "remarks";
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
    private static final String BOOKED_VIEW_IMAGE_STATUS = "imageStatus";
    private static final String BOOKED_VIEW_IMAGE_URL = "imageUrl";
    private static final String BOOKED_VIEW_COLOR_FAMILY_ID = "colorFamilyID";
    private static final String BOOKED_VIEW_COLOR_FAMILY = "colorFamily";
    private static final String BOOKED_VIEW_COLOR_ID = "colorID";
    private static final String BOOKED_VIEW_COLOR_NAME = "colorName";
    private static final String BOOKED_VIEW_SIZE_ID = "sizeId";
    private static final String BOOKED_VIEW_SIZE_NAME = "sizeName";
    private static final String BOOKED_VIEW_BOOKED_QTY = "bookedQty";
    private static final String BOOKED_VIEW_BOOK_FROM = "bookFrom";
    private static final String BOOKED_VIEW_EXPECTED_DATE = "expectedDate";
    private static final String BOOKED_VIEW_RATE = "rate";
    private static final String BOOKED_VIEW_MRP = "mrp";
    private static final String BOOKED_VIEW_MD_APPLICABLE = "mdApplicable";
    private static final String BOOKED_VIEW_SUB_ITEM_APPLICABLE = "subItemApplicable";
    private static final String BOOKED_VIEW_CREATED_DATE = "createdDate";
    private static final String BOOKED_VIEW_TOTAL_BOOKED_AMT = "totalBookedAmt";
    private static final String BOOKED_VIEW_BARCODE = "barcode";
    private static final String BOOKED_VIEW_UNIT = "unit";
    private static final String BOOKED_VIEW_USERNAME = "username";
    private static final String BOOKED_VIEW_USER_FULL_NAME = "userFullName";
    private static final String BOOKED_VIEW_EMP_CV_TYPE = "empCVType";
    private static final String BOOKED_VIEW_EMP_CV_NAME = "empCVName";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_0 = "attributeID0";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_1 = "attributeID1";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_2 = "attributeID2";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_3 = "attributeID3";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_4 = "attributeID4";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_5 = "attributeID5";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_6 = "attributeID6";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_7 = "attributeID7";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_8 = "attributeID8";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_9 = "attributeID9";
    private static final String BOOKED_VIEW_ATTRIBUTE_0 = "attribute0";
    private static final String BOOKED_VIEW_ATTRIBUTE_1 = "attribute1";
    private static final String BOOKED_VIEW_ATTRIBUTE_2 = "attribute2";
    private static final String BOOKED_VIEW_ATTRIBUTE_3 = "attribute3";
    private static final String BOOKED_VIEW_ATTRIBUTE_4 = "attribute4";
    private static final String BOOKED_VIEW_ATTRIBUTE_5 = "attribute5";
    private static final String BOOKED_VIEW_ATTRIBUTE_6 = "attribute6";
    private static final String BOOKED_VIEW_ATTRIBUTE_7 = "attribute7";
    private static final String BOOKED_VIEW_ATTRIBUTE_8 = "attribute8";
    private static final String BOOKED_VIEW_ATTRIBUTE_9 = "attribute9";
    private static final String BOOKED_VIEW_ATTRIBUTE_SEQUENCE = "attSeq";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerOrderViewDetails(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //------------------------------------TODO: Creating Table---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_TABLE_ORDER_BOOKED_TABLE = "CREATE TABLE " + ORDER_BOOKED_TABLE + "("+ KEY_ID + " INTEGER PRIMARY KEY," + BOOKED_VIEW_ORDER_ID + " TEXT,"+ BOOKED_VIEW_PARTY_ID + " TEXT,"+ BOOKED_VIEW_PARTY_NAME + " TEXT,"+ BOOKED_VIEW_SUB_PARTY_ID + " TEXT,"+ BOOKED_VIEW_SUB_PARTY_NAME + " TEXT,"+ BOOKED_VIEW_REFERENCE_NAME + " TEXT,"+ BOOKED_VIEW_REMARKS + " TEXT,"+ BOOKED_VIEW_ORDER_DATE + " TEXT,"+ BOOKED_VIEW_ORDER_NO + " TEXT,"+ BOOKED_VIEW_MAIN_GROUP_ID + " TEXT,"+ BOOKED_VIEW_MAIN_GROUP + " TEXT,"+ BOOKED_VIEW_GROUP_ID + " TEXT,"+ BOOKED_VIEW_GROUP_NAME + " TEXT,"+ BOOKED_VIEW_GROUP_IMAGE + " TEXT,"+ BOOKED_VIEW_SUB_GROUP_ID + " TEXT,"+ BOOKED_VIEW_SUB_GROUP + " TEXT,"+ BOOKED_VIEW_ITEM_ID + " TEXT,"+ BOOKED_VIEW_ITEM_NAME + " TEXT,"+ BOOKED_VIEW_ITEM_CODE + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_ID + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_NAME + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_CODE + " TEXT,"+ BOOKED_VIEW_IMAGE_STATUS + " INTEGER,"+ BOOKED_VIEW_IMAGE_URL + " TEXT,"+ BOOKED_VIEW_COLOR_FAMILY_ID + " TEXT,"+ BOOKED_VIEW_COLOR_FAMILY + " TEXT,"+ BOOKED_VIEW_COLOR_ID + " TEXT,"+ BOOKED_VIEW_COLOR_NAME + " TEXT,"+ BOOKED_VIEW_SIZE_ID + " TEXT,"+ BOOKED_VIEW_SIZE_NAME + " TEXT,"+ BOOKED_VIEW_BOOKED_QTY + " INTEGER,"+ BOOKED_VIEW_BOOK_FROM + " TEXT,"+ BOOKED_VIEW_EXPECTED_DATE + " TEXT,"+ BOOKED_VIEW_RATE + " TEXT,"+ BOOKED_VIEW_MRP + " TEXT,"+ BOOKED_VIEW_MD_APPLICABLE + " INTEGER,"+ BOOKED_VIEW_SUB_ITEM_APPLICABLE + " INTEGER,"+ BOOKED_VIEW_CREATED_DATE + " TEXT,"+ BOOKED_VIEW_TOTAL_BOOKED_AMT + " TEXT,"+ BOOKED_VIEW_BARCODE + " TEXT,"+ BOOKED_VIEW_UNIT + " TEXT,"+ BOOKED_VIEW_USERNAME + " TEXT,"+ BOOKED_VIEW_USER_FULL_NAME + " TEXT,"+ BOOKED_VIEW_EMP_CV_TYPE + " TEXT,"+ BOOKED_VIEW_EMP_CV_NAME + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_0 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_1 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_2 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_3 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_4 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_5 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_6 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_7 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_8 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_9 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_0 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_1 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_2 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_3 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_4 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_5 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_6 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_7 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_8 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_9 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_SEQUENCE + " INTEGER)";
        db.execSQL(CREATE_TABLE_ORDER_BOOKED_TABLE);
    }
    //------------------------------------TODO: Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +ORDER_BOOKED_TABLE);
        // Create tables again
        onCreate(db);
    }
    //------------------------------------TODO: Delete Table---------------------------------------------
    public void deleteOrderDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ORDER_BOOKED_TABLE,null,null);
        db.close();
    }
    //---------------------------------- TODO: Inserting Data of Order details Table---------------------------------------
    public void insertOrderDetails(List<Map<String,String>> mapList) {

        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,ORDER_BOOKED_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int OrderID = ih.getColumnIndex(BOOKED_VIEW_ORDER_ID);
        final int PartyID = ih.getColumnIndex(BOOKED_VIEW_PARTY_ID);
        final int PartyName = ih.getColumnIndex(BOOKED_VIEW_PARTY_NAME);
        final int SubPartyID = ih.getColumnIndex(BOOKED_VIEW_SUB_PARTY_ID);
        final int SubParty = ih.getColumnIndex(BOOKED_VIEW_SUB_PARTY_NAME);
        final int RefName = ih.getColumnIndex(BOOKED_VIEW_REFERENCE_NAME);
        final int Remarks = ih.getColumnIndex(BOOKED_VIEW_REMARKS);
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
        final int ImageStatus = ih.getColumnIndex(BOOKED_VIEW_IMAGE_STATUS);
        final int ImageUrl = ih.getColumnIndex(BOOKED_VIEW_IMAGE_URL);
        final int ColorFamilyID = ih.getColumnIndex(BOOKED_VIEW_COLOR_FAMILY_ID);
        final int ColorFamily = ih.getColumnIndex(BOOKED_VIEW_COLOR_FAMILY);
        final int ColorID = ih.getColumnIndex(BOOKED_VIEW_COLOR_ID);
        final int Color = ih.getColumnIndex(BOOKED_VIEW_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(BOOKED_VIEW_SIZE_ID);
        final int SizeName = ih.getColumnIndex(BOOKED_VIEW_SIZE_NAME);
        final int BookQty = ih.getColumnIndex(BOOKED_VIEW_BOOKED_QTY);
        final int BookFrom = ih.getColumnIndex(BOOKED_VIEW_BOOK_FROM);
        final int ExcepDelDt = ih.getColumnIndex(BOOKED_VIEW_EXPECTED_DATE);
        final int Rate = ih.getColumnIndex(BOOKED_VIEW_RATE);
        final int Mrp = ih.getColumnIndex(BOOKED_VIEW_MRP);
        final int MDApplicable = ih.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE);
        final int SubItemApplicable = ih.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE);
        final int CreatedDate = ih.getColumnIndex(BOOKED_VIEW_CREATED_DATE);
        final int TBookedAmt = ih.getColumnIndex(BOOKED_VIEW_TOTAL_BOOKED_AMT);
        final int Barcode = ih.getColumnIndex(BOOKED_VIEW_BARCODE);
        final int Unit = ih.getColumnIndex(BOOKED_VIEW_UNIT);
        final int UserName = ih.getColumnIndex(BOOKED_VIEW_USERNAME);
        final int UserFullName = ih.getColumnIndex(BOOKED_VIEW_USER_FULL_NAME);
        final int EmpCVType = ih.getColumnIndex(BOOKED_VIEW_EMP_CV_TYPE);
        final int EmpCVName = ih.getColumnIndex(BOOKED_VIEW_EMP_CV_NAME);

        final int AttribID1 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_0);
        final int AttribID2 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_1);
        final int AttribID3 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_2);
        final int AttribID4 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_3);
        final int AttribID5 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_4);
        final int AttribID6 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_5);
        final int AttribID7 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_6);
        final int AttribID8 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_7);
        final int AttribID9 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_8);
        final int AttribID10 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_9);

        final int AttribName1 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_0);
        final int AttribName2 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_1);
        final int AttribName3 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_2);
        final int AttribName4 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_3);
        final int AttribName5 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_4);
        final int AttribName6 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_5);
        final int AttribName7 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_6);
        final int AttribName8 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_7);
        final int AttribName9 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_8);
        final int AttribName10 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_9);
        final int AttSequence = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_SEQUENCE);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<mapList.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(OrderID, mapList.get(x).get("OrderID"));
                ih.bind(PartyID, mapList.get(x).get("PartyID"));
                ih.bind(PartyName, mapList.get(x).get("PartyName"));
                ih.bind(SubPartyID, mapList.get(x).get("SubPartyID"));
                ih.bind(SubParty, mapList.get(x).get("SubParty"));
                ih.bind(RefName, mapList.get(x).get("RefName"));
                ih.bind(Remarks, mapList.get(x).get("Remarks"));
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
                ih.bind(ImageStatus, mapList.get(x).get("ImageStatus"));
                ih.bind(ImageUrl, mapList.get(x).get("ImageUrl"));
                ih.bind(ColorFamilyID, mapList.get(x).get("ColorFamilyID"));
                ih.bind(ColorFamily, mapList.get(x).get("ColorFamily"));
                ih.bind(ColorID, mapList.get(x).get("ColorID"));
                ih.bind(Color, mapList.get(x).get("Color"));
                ih.bind(SizeID, mapList.get(x).get("SizeID"));
                ih.bind(SizeName, mapList.get(x).get("SizeName"));
                ih.bind(BookQty, mapList.get(x).get("BookQty"));
                ih.bind(BookFrom, mapList.get(x).get("BookFrom"));
                ih.bind(ExcepDelDt, mapList.get(x).get("ExcepDelDt"));
                ih.bind(Rate, mapList.get(x).get("Rate"));
                ih.bind(Mrp, mapList.get(x).get("Mrp"));
                ih.bind(MDApplicable, mapList.get(x).get("MDApplicable"));
                ih.bind(SubItemApplicable, mapList.get(x).get("SubItemApplicable"));
                ih.bind(CreatedDate, mapList.get(x).get("CreatedDate"));
                ih.bind(TBookedAmt, mapList.get(x).get("TBookedAmt"));

                ih.bind(Barcode, mapList.get(x).get("Barcode"));
                ih.bind(Unit, mapList.get(x).get("Unit"));
                ih.bind(UserName, mapList.get(x).get("UserName"));
                ih.bind(UserFullName, mapList.get(x).get("UserFullName"));
                ih.bind(EmpCVType, mapList.get(x).get("EmpCVType"));
                ih.bind(EmpCVName, mapList.get(x).get("EmpCVName"));

                ih.bind(AttribID1, mapList.get(x).get("AttribID1"));
                ih.bind(AttribID2, mapList.get(x).get("AttribID2"));
                ih.bind(AttribID3, mapList.get(x).get("AttribID3"));
                ih.bind(AttribID4, mapList.get(x).get("AttribID4"));
                ih.bind(AttribID5, mapList.get(x).get("AttribID5"));
                ih.bind(AttribID6, mapList.get(x).get("AttribID6"));
                ih.bind(AttribID7, mapList.get(x).get("AttribID7"));
                ih.bind(AttribID8, mapList.get(x).get("AttribID8"));
                ih.bind(AttribID9, mapList.get(x).get("AttribID9"));
                ih.bind(AttribID10, mapList.get(x).get("AttribID10"));

                ih.bind(AttribName1, mapList.get(x).get("AttribName1"));
                ih.bind(AttribName2, mapList.get(x).get("AttribName2"));
                ih.bind(AttribName3, mapList.get(x).get("AttribName3"));
                ih.bind(AttribName4, mapList.get(x).get("AttribName4"));
                ih.bind(AttribName5, mapList.get(x).get("AttribName5"));
                ih.bind(AttribName6, mapList.get(x).get("AttribName6"));
                ih.bind(AttribName7, mapList.get(x).get("AttribName7"));
                ih.bind(AttribName8, mapList.get(x).get("AttribName8"));
                ih.bind(AttribName9, mapList.get(x).get("AttribName9"));
                ih.bind(AttribName10, mapList.get(x).get("AttribName10"));
                ih.bind(AttSequence, mapList.get(x).get("AttSequence"));
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
    //---------------------------------- TODO: Get Group List fron Order details Table---------------------------------------
    public List<OrderViewGroupDataset> getGroupList(String OrderID){
        List<OrderViewGroupDataset> mapList=new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_GROUP_IMAGE+","+BOOKED_VIEW_MAIN_GROUP_ID+","+BOOKED_VIEW_MAIN_GROUP+","+BOOKED_VIEW_CREATED_DATE+"  from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                //mapList.add(new OrderViewGroupDataset(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_IMAGE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group wise Total Style, Qty ,Amt fron Order details Table---------------------------------------
    public List<Map<String,String>> getTotalStyleQtyAmt(String GroupID,String OrderID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT("+BOOKED_VIEW_ITEM_ID+")) as TotalStyle,SUM("+BOOKED_VIEW_BOOKED_QTY+") as TotalBookedQty,SUM("+BOOKED_VIEW_TOTAL_BOOKED_AMT+") as TotalAmt  from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("TotalStyle", cursor.getString(cursor.getColumnIndex("TotalStyle")));
                map.put("TotalBookedQty", cursor.getString(cursor.getColumnIndex("TotalBookedQty")));
                map.put("TotalAmt", cursor.getString(cursor.getColumnIndex("TotalAmt")));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item List fron Order details Table---------------------------------------
    public List<OrderViewItemByGroupDataset> getItemList(String orderId, String GroupID,String[] strFilter){
        List<OrderViewItemByGroupDataset> mapList=new ArrayList<OrderViewItemByGroupDataset>();
        String PartialQuery = "";
        if (strFilter!=null){
           for (int i=0; i < strFilter.length; i++){
               if (strFilter[i]!=null){
                   PartialQuery += " AND attributeID"+i+" IN ("+strFilter[i].substring(0,strFilter[i].length()-1)+") ";
               }
           }
        }
        String selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_ORDER_ID + "," + BOOKED_VIEW_GROUP_ID + "," + BOOKED_VIEW_GROUP_NAME + "," + BOOKED_VIEW_ITEM_ID + "," + BOOKED_VIEW_ITEM_CODE + "," + BOOKED_VIEW_ITEM_NAME + ",Min(" + BOOKED_VIEW_RATE + ")  as "+BOOKED_VIEW_RATE+",Min(" + BOOKED_VIEW_MRP + ")  as "+BOOKED_VIEW_MRP+"," + BOOKED_VIEW_UNIT + "," + BOOKED_VIEW_MD_APPLICABLE + "," + BOOKED_VIEW_SUB_ITEM_APPLICABLE + "  from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ORDER_ID + "='" + orderId + "' AND " + BOOKED_VIEW_GROUP_ID + "='" + GroupID + "' "+PartialQuery +" Group by " + BOOKED_VIEW_ORDER_ID + "," + BOOKED_VIEW_GROUP_ID + "," + BOOKED_VIEW_GROUP_NAME + "," + BOOKED_VIEW_ITEM_ID + "," + BOOKED_VIEW_ITEM_CODE + "," + BOOKED_VIEW_ITEM_NAME + "," + BOOKED_VIEW_UNIT + "," + BOOKED_VIEW_MD_APPLICABLE + "," + BOOKED_VIEW_SUB_ITEM_APPLICABLE ;
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                String[] ImageUrl = getImageUrl(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                mapList.add(new OrderViewItemByGroupDataset(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_CODE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),Integer.valueOf(ImageUrl[2]),ImageUrl[1],cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_UNIT)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_RATE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MRP)),ImageUrl[0],"","",cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)),ImageUrl[3]));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item List fron Order details Table---------------------------------------
    public List<OrderViewItemByGroupDataset> getItemListColorWise(String orderId, String GroupID,String[] strFilter,int MDApplicable,int SubItemApplicable){
        List<OrderViewItemByGroupDataset> mapList=new ArrayList<OrderViewItemByGroupDataset>();
        String selectQuery = "";
        String PartialQuery = "";
        if (strFilter!=null){
            for (int i=0; i < strFilter.length; i++){
                if (strFilter[i]!=null){
                    PartialQuery += " AND attributeID"+i+" IN ("+strFilter[i].substring(0,strFilter[i].length()-1)+") ";
                }
            }
        }
        // Select All Query
        if (MDApplicable == 1) {
            selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_UNIT+",Min(" + BOOKED_VIEW_RATE + ") as "+BOOKED_VIEW_RATE+",Min(" + BOOKED_VIEW_MRP + ")  as "+BOOKED_VIEW_MRP+","+BOOKED_VIEW_COLOR_ID+" as ID,"+BOOKED_VIEW_COLOR_NAME+" as Name,"+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+"  from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+orderId+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' "+PartialQuery+" Group By "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_UNIT+",ID,Name,"+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE;
        }else{
            if (SubItemApplicable == 1){
                selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_UNIT+",Min(" + BOOKED_VIEW_RATE + ") as "+BOOKED_VIEW_RATE+",Min(" + BOOKED_VIEW_MRP + ")  as "+BOOKED_VIEW_MRP+","+BOOKED_VIEW_SUB_ITEM_ID+" as ID,"+BOOKED_VIEW_SUB_ITEM_NAME+" as Name,"+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+"  from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+orderId+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' "+PartialQuery+" Group By "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_UNIT+",ID,Name,"+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE;
            }else {
                selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_UNIT+","+BOOKED_VIEW_RATE+","+BOOKED_VIEW_MRP+","+BOOKED_VIEW_ITEM_ID+" as ID,"+BOOKED_VIEW_ITEM_NAME+" as Name,"+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+"  from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+orderId+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' "+PartialQuery;
            }
        }
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                String[] Str = getColorDetails(orderId,GroupID,cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),cursor.getString(cursor.getColumnIndex("ID")),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                String[] ImageUrl = getImageUrl(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                mapList.add(new OrderViewItemByGroupDataset(cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_CODE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),Integer.valueOf(ImageUrl[2]),Str[1],cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_UNIT)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_RATE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MRP)),Str[0],cursor.getString(cursor.getColumnIndex("ID")),cursor.getString(cursor.getColumnIndex("Name")),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)),Str[3]));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item List fron Order details Table---------------------------------------
    public String[] getImageUrl(String ItemID,int MDApplicable,int SubItemApplicable){
        String[] Url = new String[4];
        String selectQuery = "";
        // Select All Query
        if (MDApplicable == 1) {
            //selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + ", SUM(" + BOOKED_VIEW_BOOKED_QTY + ") as BookedQty, Count(DISTINCT(" + BOOKED_VIEW_COLOR_ID + ")) as TotalColor from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "' GROUP BY " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + "  ORDER BY " + BOOKED_VIEW_IMAGE_STATUS + " DESC";
            selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + ", (Select SUM(" + BOOKED_VIEW_BOOKED_QTY +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as BookedQty, (Select Count(DISTINCT " + BOOKED_VIEW_COLOR_ID +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as TotalColor from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "' ORDER BY " + BOOKED_VIEW_IMAGE_STATUS + " DESC";
        }else{
            if (SubItemApplicable == 1){
                selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + ", (Select SUM(" + BOOKED_VIEW_BOOKED_QTY +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as BookedQty, (Select Count(DISTINCT " + BOOKED_VIEW_SUB_ITEM_ID +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as TotalColor from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "' ORDER BY " + BOOKED_VIEW_IMAGE_STATUS + " DESC";
            }else {
                //selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + ", SUM(" + BOOKED_VIEW_BOOKED_QTY + ") as BookedQty, Count(DISTINCT(" + BOOKED_VIEW_ITEM_ID + ")) as TotalColor from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "' GROUP BY " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + "  ORDER BY " + BOOKED_VIEW_IMAGE_STATUS + " DESC";
                selectQuery = "SELECT DISTINCT " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + ", (Select SUM(" + BOOKED_VIEW_BOOKED_QTY +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as BookedQty, (Select Count(DISTINCT " + BOOKED_VIEW_ITEM_ID +") from "+ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "') as TotalColor from " + ORDER_BOOKED_TABLE + " where " + BOOKED_VIEW_ITEM_ID + "='" + ItemID + "' ORDER BY " + BOOKED_VIEW_IMAGE_STATUS + " DESC";
            }
        }
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                Url[0] = cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_IMAGE_URL));
                Url[1] = cursor.getString(cursor.getColumnIndex("BookedQty"));
                Url[2] = cursor.getString(cursor.getColumnIndex("TotalColor"));
                Url[3] = cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BARCODE));
                //System.out.println("Barcode:"+Url[3]+" Total Color:"+Url[2]+" ImageStatus:"+cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_IMAGE_STATUS)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return Url;
    }
    //---------------------------------- TODO: Get Group Item Color List fron Order details Table---------------------------------------
    public List<OrderViewColorOptionDataset> getColorList(String OrderID,String GroupID, String ItemID,int MDApplicable,int SubItemApplicable){
        List<OrderViewColorOptionDataset> mapList=new ArrayList<OrderViewColorOptionDataset>();
        // Select All Query
        String selectQuery = "";
        if (MDApplicable == 1) {
            selectQuery = "select distinct "+BOOKED_VIEW_COLOR_ID+" as ID,"+BOOKED_VIEW_COLOR_NAME+" as Name,"+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_RATE+","+BOOKED_VIEW_UNIT+","+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+"   from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' Order By "+BOOKED_VIEW_COLOR_NAME;
        }else{
            if (SubItemApplicable == 1){
                selectQuery = "select distinct "+BOOKED_VIEW_SUB_ITEM_ID+" as ID,"+BOOKED_VIEW_SUB_ITEM_NAME+" as Name,"+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_RATE+","+BOOKED_VIEW_UNIT+","+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+"   from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' Order By "+BOOKED_VIEW_SUB_ITEM_NAME;
            }
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                String[] Str = getColorDetails(OrderID,GroupID,ItemID,cursor.getString(cursor.getColumnIndex("ID")),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                mapList.add(new OrderViewColorOptionDataset(OrderID,cursor.getString(cursor.getColumnIndex("ID")),cursor.getString(cursor.getColumnIndex("Name")),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_NAME)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_CODE)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)),cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_RATE)),Str[0],Str[1],cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_UNIT)),Str[3],cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)),cursor.getInt(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item List fron Order details Table---------------------------------------
    public String[] getColorDetails(String OrderID,String GroupID,String ItemID,String ColorID,int MDApplicable,int SubItemApplicable){
        String[] Url = new String[4];
        // Select All Query
        String selectQuery = "";
        if (MDApplicable == 1) {
            selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_IMAGE_URL+","+BOOKED_VIEW_BARCODE+", SUM("+BOOKED_VIEW_BOOKED_QTY+") as BookedQty, Count(DISTINCT("+BOOKED_VIEW_COLOR_ID+")) as TotalColor from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' AND "+BOOKED_VIEW_COLOR_ID+"='"+ ColorID +"'  GROUP BY " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + " ORDER BY "+BOOKED_VIEW_IMAGE_STATUS+" DESC";
        }else{
            if (SubItemApplicable == 1){
                selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_IMAGE_URL+","+BOOKED_VIEW_BARCODE+", SUM("+BOOKED_VIEW_BOOKED_QTY+") as BookedQty, Count(DISTINCT("+BOOKED_VIEW_SUB_ITEM_ID+")) as TotalColor from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' AND "+BOOKED_VIEW_SUB_ITEM_ID+"='"+ ColorID +"' GROUP BY " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + "  ORDER BY "+BOOKED_VIEW_IMAGE_STATUS+" DESC";
            }else{
                selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_IMAGE_URL+","+BOOKED_VIEW_BARCODE+", SUM("+BOOKED_VIEW_BOOKED_QTY+") as BookedQty, Count(DISTINCT("+BOOKED_VIEW_ITEM_ID+")) as TotalColor from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_ORDER_ID+"='"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' GROUP BY " + BOOKED_VIEW_IMAGE_URL + "," + BOOKED_VIEW_BARCODE + "  ORDER BY "+BOOKED_VIEW_IMAGE_STATUS+" DESC";
            }
        }
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                Url[0] = cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_IMAGE_URL));
                Url[1] = cursor.getString(cursor.getColumnIndex("BookedQty"));
                Url[2] = cursor.getString(cursor.getColumnIndex("TotalColor"));
                Url[3] = cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BARCODE));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return Url;
    }
    //---------------------------------- TODO: Get Group Item Size List fron Order details Table---------------------------------------
    public List<Map<String,String>> getSizeList(String GroupID,String ItemID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select distinct "+BOOKED_VIEW_SIZE_ID+","+BOOKED_VIEW_SIZE_NAME+" from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' Order By "+BOOKED_VIEW_SIZE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SIZE_ID)));
                map.put("SizeName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SIZE_NAME)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item Color and Size Booked Qty  fron Order details Table---------------------------------------
    public List<Map<String,String>> getBookedQty(String GroupID,String ItemID,String ColorID,String SizeID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "select "+BOOKED_VIEW_BOOKED_QTY+" from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+"='"+ItemID+"' AND "+BOOKED_VIEW_COLOR_ID+"='"+ColorID+"' AND "+BOOKED_VIEW_SIZE_ID+"='"+SizeID+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("Qty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BOOKED_QTY)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
    //---------------------------------- TODO: Get Group Item delete fron Order details Table---------------------------------------
    public void DeleteOrderGroupItemColorOrSubItem(String OrderID,String GroupId,String ItemID,String ColorOrSubItemID,int MDApplicable,int SubItemApplicable){
        SQLiteDatabase db = this.getWritableDatabase();
        if (MDApplicable == 1){
            if (GroupId.isEmpty()){
                //TODO: Order Wise Delete
                db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "'", null);
            }else {
                if (ItemID.isEmpty()){
                    //TODO: Group Wise Delete
                    db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                }else {
                    if (ColorOrSubItemID.isEmpty()) {
                        //TODO: Item Wise Delete
                        db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ITEM_ID + "= '" + ItemID + "' AND " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                    } else {
                        //TODO: Color Wise Delete
                        db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_COLOR_ID + "= '" + ColorOrSubItemID + "' AND " + BOOKED_VIEW_ITEM_ID + "= '" + ItemID + "' AND " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                    }
                }
            }
        }else{
            if (SubItemApplicable == 1){
                if (GroupId.isEmpty()){
                    //TODO: Order Wise Delete
                    db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "'", null);
                }else {
                    if (ItemID.isEmpty()){
                        //TODO: Group Wise Delete
                        db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                    }else {
                        if (ColorOrSubItemID.isEmpty()) {
                            //TODO: Item Wise Delete
                            db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ITEM_ID + "= '" + ItemID + "' AND " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                        } else {
                            //TODO: Color Wise Delete
                            db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_SUB_ITEM_ID+ "= '" + ColorOrSubItemID + "' AND " + BOOKED_VIEW_ITEM_ID + "= '" + ItemID + "' AND " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                        }
                    }
                }
            }else {
                if (GroupId.isEmpty()){
                    //TODO: Order Wise Delete
                    db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "'", null);
                }else {
                    if (ItemID.isEmpty()){
                        //TODO: Group Wise Delete
                        db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);
                    }else {
                        //TODO: Item Wise Delete
                        db.delete(ORDER_BOOKED_TABLE, BOOKED_VIEW_ITEM_ID + "= '" + ItemID + "' AND " + BOOKED_VIEW_ORDER_ID + "= '" + OrderID + "' AND " + BOOKED_VIEW_GROUP_ID + "= '" + GroupId + "'", null);

                    }
                }
            }
        }
        db.close();
    }
    //TODO: Get Attribute list  fron Order details Table---------------------------------------
    public List<Map<String,String>> getAttributesWithIDs(String GroupID,int seq){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "Select Distinct attributeID"+seq+",attribute"+seq+" from " + ORDER_BOOKED_TABLE+ " where "+BOOKED_VIEW_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("AttributeID", cursor.getString(cursor.getColumnIndex("attributeID"+seq)));
                map.put("Attribute", cursor.getString(cursor.getColumnIndex("attribute"+seq)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return mapList;
    }
}
