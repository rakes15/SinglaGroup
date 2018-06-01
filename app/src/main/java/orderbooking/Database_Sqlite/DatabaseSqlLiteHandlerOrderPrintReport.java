package orderbooking.Database_Sqlite;

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

import orderbooking.view_order_details.dataset.OrderViewColorOptionDataset;
import orderbooking.view_order_details.dataset.OrderViewGroupDataset;
import orderbooking.view_order_details.dataset.OrderViewItemByGroupDataset;

/**
 * Created by Rakesh on 13-Jun-16.
 */
public class DatabaseSqlLiteHandlerOrderPrintReport extends SQLiteOpenHelper {
    //-------------------------Todo: Database Version----------------------
    private static final int DATABASE_VERSION = 1;
    // ----------------------- Todo: Database Name--------------
    public static final String DATABASE_NAME = "SG_DB_OrderGroupItemPrintReport";
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
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_1 = "attributeID1";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_2 = "attributeID2";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_3 = "attributeID3";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_4 = "attributeID4";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_5 = "attributeID5";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_6 = "attributeID6";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_7 = "attributeID7";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_8 = "attributeID8";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_9 = "attributeID9";
    private static final String BOOKED_VIEW_ATTRIBUTE_ID_10 = "attributeID10";
    private static final String BOOKED_VIEW_ATTRIBUTE_1 = "attribute1";
    private static final String BOOKED_VIEW_ATTRIBUTE_2 = "attribute2";
    private static final String BOOKED_VIEW_ATTRIBUTE_3 = "attribute3";
    private static final String BOOKED_VIEW_ATTRIBUTE_4 = "attribute4";
    private static final String BOOKED_VIEW_ATTRIBUTE_5 = "attribute5";
    private static final String BOOKED_VIEW_ATTRIBUTE_6 = "attribute6";
    private static final String BOOKED_VIEW_ATTRIBUTE_7 = "attribute7";
    private static final String BOOKED_VIEW_ATTRIBUTE_8 = "attribute8";
    private static final String BOOKED_VIEW_ATTRIBUTE_9 = "attribute9";
    private static final String BOOKED_VIEW_ATTRIBUTE_10 = "attribute10";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerOrderPrintReport(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //------------------------------------TODO: Creating Table---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_TABLE_ORDER_BOOKED_TABLE = "CREATE TABLE " + ORDER_BOOKED_TABLE + "("+ KEY_ID + " INTEGER PRIMARY KEY," + BOOKED_VIEW_ORDER_ID + " TEXT,"+ BOOKED_VIEW_PARTY_ID + " TEXT,"+ BOOKED_VIEW_PARTY_NAME + " TEXT,"+ BOOKED_VIEW_SUB_PARTY_ID + " TEXT,"+ BOOKED_VIEW_SUB_PARTY_NAME + " TEXT,"+ BOOKED_VIEW_REFERENCE_NAME + " TEXT,"+ BOOKED_VIEW_REMARKS + " TEXT,"+ BOOKED_VIEW_ORDER_DATE + " TEXT,"+ BOOKED_VIEW_ORDER_NO + " TEXT,"+ BOOKED_VIEW_MAIN_GROUP_ID + " TEXT,"+ BOOKED_VIEW_MAIN_GROUP + " TEXT,"+ BOOKED_VIEW_GROUP_ID + " TEXT,"+ BOOKED_VIEW_GROUP_NAME + " TEXT,"+ BOOKED_VIEW_GROUP_IMAGE + " TEXT,"+ BOOKED_VIEW_SUB_GROUP_ID + " TEXT,"+ BOOKED_VIEW_SUB_GROUP + " TEXT,"+ BOOKED_VIEW_ITEM_ID + " TEXT,"+ BOOKED_VIEW_ITEM_NAME + " TEXT,"+ BOOKED_VIEW_ITEM_CODE + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_ID + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_NAME + " TEXT,"+ BOOKED_VIEW_SUB_ITEM_CODE + " TEXT,"+ BOOKED_VIEW_IMAGE_STATUS + " INTEGER,"+ BOOKED_VIEW_IMAGE_URL + " TEXT,"+ BOOKED_VIEW_COLOR_FAMILY_ID + " TEXT,"+ BOOKED_VIEW_COLOR_FAMILY + " TEXT,"+ BOOKED_VIEW_COLOR_ID + " TEXT,"+ BOOKED_VIEW_COLOR_NAME + " TEXT,"+ BOOKED_VIEW_SIZE_ID + " TEXT,"+ BOOKED_VIEW_SIZE_NAME + " TEXT,"+ BOOKED_VIEW_BOOKED_QTY + " INTEGER,"+ BOOKED_VIEW_BOOK_FROM + " TEXT,"+ BOOKED_VIEW_EXPECTED_DATE + " TEXT,"+ BOOKED_VIEW_RATE + " TEXT,"+ BOOKED_VIEW_MD_APPLICABLE + " INTEGER,"+ BOOKED_VIEW_SUB_ITEM_APPLICABLE + " INTEGER,"+ BOOKED_VIEW_CREATED_DATE + " TEXT,"+ BOOKED_VIEW_TOTAL_BOOKED_AMT + " INTEGER,"+ BOOKED_VIEW_BARCODE + " TEXT,"+ BOOKED_VIEW_UNIT + " TEXT,"+ BOOKED_VIEW_USERNAME + " TEXT,"+ BOOKED_VIEW_USER_FULL_NAME + " TEXT,"+ BOOKED_VIEW_EMP_CV_TYPE + " TEXT,"+ BOOKED_VIEW_EMP_CV_NAME + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_1 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_2 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_3 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_4 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_5 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_6 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_7 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_8 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_9 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_ID_10 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_1 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_2 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_3 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_4 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_5 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_6 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_7 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_8 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_9 + " TEXT,"+ BOOKED_VIEW_ATTRIBUTE_10 + " TEXT)";
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

        final int AttribID1 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_1);
        final int AttribID2 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_2);
        final int AttribID3 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_3);
        final int AttribID4 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_4);
        final int AttribID5 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_5);
        final int AttribID6 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_6);
        final int AttribID7 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_7);
        final int AttribID8 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_8);
        final int AttribID9 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_9);
        final int AttribID10 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_ID_10);

        final int AttribName1 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_1);
        final int AttribName2 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_2);
        final int AttribName3 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_3);
        final int AttribName4 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_4);
        final int AttribName5 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_5);
        final int AttribName6 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_6);
        final int AttribName7 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_7);
        final int AttribName8 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_8);
        final int AttribName9 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_9);
        final int AttribName10 = ih.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_10);
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
    //TODO: Get party details from print report Table
    public Map<String,String> getPartyDetails(){
        Map< String, String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_PARTY_ID+","+BOOKED_VIEW_PARTY_NAME+","+BOOKED_VIEW_SUB_PARTY_ID+","+BOOKED_VIEW_SUB_PARTY_NAME+","+BOOKED_VIEW_REFERENCE_NAME+","+BOOKED_VIEW_ORDER_ID+","+BOOKED_VIEW_ORDER_NO+","+BOOKED_VIEW_ORDER_DATE+"  from " + ORDER_BOOKED_TABLE;
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                map.put("PartyID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_PARTY_ID)));
                map.put("PartyName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_PARTY_NAME)));
                map.put("SubPartyID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_PARTY_ID)));
                map.put("SubPartyName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_PARTY_NAME)));
                map.put("RefName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_REFERENCE_NAME)));
                map.put("OrderID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_ID)));
                map.put("OrderNo", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_NO)));
                map.put("OrderDate", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ORDER_DATE)));
//                mapList.add(map);
//            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        return map;
    }
    //TODO: Get Group or MAinGroup List from print report Table
    public List<Map<String,String>> getGroupMainGroupList(String OrderID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_GROUP_ID+","+BOOKED_VIEW_GROUP_NAME+","+BOOKED_VIEW_MAIN_GROUP_ID+","+BOOKED_VIEW_MAIN_GROUP+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+"= '"+OrderID+"'";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("GroupID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_ID)));
                map.put("GroupName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_GROUP_NAME)));
                map.put("MainGroupID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP_ID)));
                map.put("MainGroup", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MAIN_GROUP)));
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
    public List<Map<String,String>> getItemListWithMDOrSubItemApplicable(String OrderID,String GroupID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_ITEM_ID+","+BOOKED_VIEW_ITEM_CODE+","+BOOKED_VIEW_ITEM_NAME+","+BOOKED_VIEW_IMAGE_URL+","+BOOKED_VIEW_MD_APPLICABLE+","+BOOKED_VIEW_SUB_ITEM_APPLICABLE+","+BOOKED_VIEW_ATTRIBUTE_2+","+BOOKED_VIEW_ATTRIBUTE_6+","+BOOKED_VIEW_ATTRIBUTE_7+","+BOOKED_VIEW_ATTRIBUTE_8+","+BOOKED_VIEW_REMARKS+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ItemID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_ID)));
                map.put("ItemCode", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_CODE)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ITEM_NAME)));
                map.put("ImageUrl", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_IMAGE_URL)));
                //map.put("SubItemID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_ID)));
                //map.put("SubItemName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_NAME)));
                //map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_CODE)));
                map.put("MDApplicable", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_MD_APPLICABLE)));
                map.put("SubItemApplicable", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_SUB_ITEM_APPLICABLE)));
                map.put("Attr2", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_2)));
                map.put("Attr6", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_6)));
                map.put("Attr7", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_7)));
                map.put("Attr8", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_ATTRIBUTE_8)));
                map.put("Remarks", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_REMARKS)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }
    //TODO: Get Color List from print report Table  MDApplicable
    public List<Map<String,String>> getColorList(String OrderID,String GroupID,String ItemID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_COLOR_ID+","+BOOKED_VIEW_COLOR_NAME+","+BOOKED_VIEW_COLOR_FAMILY_ID+","+BOOKED_VIEW_COLOR_FAMILY+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_NAME)));
                map.put("ColorFamilyID", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_FAMILY_ID)));
                map.put("ColorFamily", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_COLOR_FAMILY)));
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
    public List<Map<String,String>> getSizeList(String OrderID,String GroupID,String ItemID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_SIZE_ID+","+BOOKED_VIEW_SIZE_NAME+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ";
        //System.out.println("Query:"+selectQuery);
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
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }
    //TODO: Get MD Quantity from print report Table   MDApplicable
    public Map<String,String> getMDQauntity(String OrderID,String GroupID,String ItemID,String ColorID,String SizeID){
        Map< String, String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT  "+BOOKED_VIEW_BOOKED_QTY+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' AND "+BOOKED_VIEW_COLOR_ID+" = '"+ColorID+"' AND "+BOOKED_VIEW_SIZE_ID+" = '"+SizeID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map.put("BookedQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BOOKED_QTY)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }
    //TODO: Get SubItem List from print report Table   SubItemApplicable
    public List<Map<String,String>> getSubItemList(String OrderID,String GroupID,String ItemID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_SUB_ITEM_ID+","+BOOKED_VIEW_SUB_ITEM_NAME+","+BOOKED_VIEW_SUB_ITEM_CODE+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
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
    public Map<String,String> getSubItemQauntity(String OrderID,String GroupID,String ItemID,String SubItemID){
        Map< String, String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT  "+BOOKED_VIEW_BOOKED_QTY+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' AND "+BOOKED_VIEW_SUB_ITEM_ID+" = '"+SubItemID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map.put("BookedQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BOOKED_QTY)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }
    //TODO: Get Item Only Quantity from print report Table   Item Only
    public Map<String,String> getItemOnlyQauntity(String OrderID,String GroupID,String ItemID){
        Map< String, String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT  "+BOOKED_VIEW_BOOKED_QTY+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                map.put("BookedQty", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_BOOKED_QTY)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }
    //TODO: Get Item Rate List from print report Table
    public List<Map<String,String>> getRateList(String OrderID,String GroupID,String ItemID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_RATE+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("Rate", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_RATE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return mapList;
    }
    public int getQtyByRate(String OrderID,String GroupID,String ItemID,String Rate){
        int qty = 0;
        // Select All Query
        String selectQuery = "SELECT  SUM("+BOOKED_VIEW_BOOKED_QTY+") as Qty  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' AND "+BOOKED_VIEW_RATE+" = '"+Rate+"' ";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                qty = cursor.getInt(cursor.getColumnIndex("Qty"));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return qty;
    }
    //TODO: Get Item Rate List from print report Table
    public Map<String,String> getUserDetails(String OrderID,String GroupID,String ItemID){
        Map< String, String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+BOOKED_VIEW_USERNAME+","+BOOKED_VIEW_USER_FULL_NAME+","+BOOKED_VIEW_EXPECTED_DATE+"  from " + ORDER_BOOKED_TABLE + " where "+BOOKED_VIEW_ORDER_ID+" = '"+OrderID+"' AND "+BOOKED_VIEW_GROUP_ID+" = '"+GroupID+"' AND "+BOOKED_VIEW_ITEM_ID+" = '"+ItemID+"' ORDER BY "+BOOKED_VIEW_CREATED_DATE+" DESC Limit 1";
        //System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            //do {
                map.put("UserName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_USERNAME)));
                map.put("UserFullName", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_USER_FULL_NAME)));
                map.put("ExpectedDate", cursor.getString(cursor.getColumnIndex(BOOKED_VIEW_EXPECTED_DATE)));
            //} while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //System.out.println("Total:"+mapList.toString());
        return map;
    }
}
