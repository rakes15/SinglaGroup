package inventory.analysis.catalogue.Database_Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqliteRootHandler;
import inventory.analysis.catalogue.wishlist.dataset.RecyclerWishlistDataset;
import inventory.analysis.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;

public class DatabaseSqlLiteHandlerWishlist {
    //TODo: Table Name
    private static final String SINGLA_WISHLIST_TABLE = "wishlistTbl";
    //TODO: Colors  Table Column's Name
    private static final String SINGLA_KEY_ID = "id";
    private static final String SINGLA_GROUP_ID = "groupID";
    private static final String SINGLA_GROUP_NAME = "groupName";
    private static final String SINGLA_GROUP_IMAGE = "groupImage";
    private static final String SINGLA_MAIN_GROUP = "mainGroup";
    private static final String SINGLA_ITEM_ID = "itemID";
    private static final String SINGLA_ITEM_CODE = "itemCode";
    private static final String SINGLA_ITEM_NAME = "itemName";
    private static final String SINGLA_ITEM_IMAGE = "itemImage";
    private static final String SINGLA_ITEM_STOCK = "itemStock";
    private static final String SINGLA_RATE = "rate";
    private static final String SINGLA_TOTAL_COLOR = "totalColor";
    private static final String SINGLA_UNIT = "unit";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerWishlist(Context context) {
        this.context  = context;
    }
    //TODO: WishList Table Delete
    public void WishlistTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_WISHLIST_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of wishlist Table
    public void insertWishListTable(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_WISHLIST_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int GroupID = ih.getColumnIndex(SINGLA_GROUP_ID);
        final int GroupName = ih.getColumnIndex(SINGLA_GROUP_NAME);
        final int GroupImage = ih.getColumnIndex(SINGLA_GROUP_IMAGE);
        final int MainGroup = ih.getColumnIndex(SINGLA_MAIN_GROUP);
        final int ItemID = ih.getColumnIndex(SINGLA_ITEM_ID);
        final int ItemCode = ih.getColumnIndex(SINGLA_ITEM_CODE);
        final int ItemName = ih.getColumnIndex(SINGLA_ITEM_NAME);
        final int ItemImage = ih.getColumnIndex(SINGLA_ITEM_IMAGE);
        final int ItemStock = ih.getColumnIndex(SINGLA_ITEM_STOCK);
        final int Rate = ih.getColumnIndex(SINGLA_RATE);
        final int TotalColor = ih.getColumnIndex(SINGLA_TOTAL_COLOR);
        final int Unit = ih.getColumnIndex(SINGLA_UNIT);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(GroupID, list.get(x).get("GroupID"));
                ih.bind(GroupName, list.get(x).get("GroupName"));
                ih.bind(GroupImage, list.get(x).get("GroupImage"));
                ih.bind(MainGroup, list.get(x).get("MainGroup"));
                ih.bind(ItemID, list.get(x).get("ItemID"));
                ih.bind(ItemCode, list.get(x).get("ItemCode"));
                ih.bind(ItemName, list.get(x).get("ItemName"));
                ih.bind(ItemImage, list.get(x).get("ItemImage"));
                ih.bind(ItemStock, list.get(x).get("ItemStock"));
                ih.bind(Rate, list.get(x).get("Rate"));
                ih.bind(TotalColor, list.get(x).get("TotalColor"));
                ih.bind(Unit, list.get(x).get("Unit"));
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
    public List<RecyclerWishlistGroupDataset> getGroupList(){
        List<RecyclerWishlistGroupDataset> dataList=new ArrayList<RecyclerWishlistGroupDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_GROUP_ID+", "+SINGLA_GROUP_NAME+","+SINGLA_GROUP_IMAGE+","+SINGLA_MAIN_GROUP+" from " + SINGLA_WISHLIST_TABLE+" ORDER BY "+SINGLA_GROUP_NAME+" ASC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                RecyclerWishlistGroupDataset dataset=new RecyclerWishlistGroupDataset(cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_IMAGE)),cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP)));
                dataList.add(dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<RecyclerWishlistDataset> getWishlistItemsGroupBy(String GroupID){
        List<RecyclerWishlistDataset> list=new ArrayList<RecyclerWishlistDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_GROUP_ID+","+SINGLA_GROUP_NAME+","+SINGLA_ITEM_ID+", "+SINGLA_ITEM_CODE+", "+SINGLA_ITEM_NAME+","+SINGLA_ITEM_IMAGE+","+SINGLA_ITEM_STOCK+","+SINGLA_RATE+","+SINGLA_TOTAL_COLOR+","+SINGLA_UNIT+" from " + SINGLA_WISHLIST_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                    RecyclerWishlistDataset recyclerWishlistDataset=new RecyclerWishlistDataset(cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_CODE)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_STOCK)),cursor.getString(cursor.getColumnIndex(SINGLA_RATE)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_IMAGE)),cursor.getString(cursor.getColumnIndex(SINGLA_TOTAL_COLOR)),cursor.getString(cursor.getColumnIndex(SINGLA_UNIT)));
                    list.add(recyclerWishlistDataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return list;
    }
    //TODO: Delete Item from Wishlist table
    public void DeleteItemFromWishlist(String ItemID) {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_WISHLIST_TABLE, SINGLA_ITEM_ID +"='"+ItemID+"'" , null);
        db.close();
    }
}