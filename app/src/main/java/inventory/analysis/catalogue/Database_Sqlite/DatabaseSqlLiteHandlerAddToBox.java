package inventory.analysis.catalogue.Database_Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqliteRootHandler;
import inventory.analysis.catalogue.addtobox.dataset.RecyclerBoxGroupDataset;
import inventory.analysis.catalogue.addtobox.dataset.RecyclerBoxListDataset;

public class DatabaseSqlLiteHandlerAddToBox {
    //TODo: Table Name
    private static final String SINGLA_BOX_TABLE = "addToBoxTbl";
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
    private static final String SINGLA_COLOR_ID = "colorID";
    private static final String SINGLA_COLOR_NAME = "colorName";
    private static final String SINGLA_SIZE_ID = "sizeID";
    private static final String SINGLA_SIZE_NAME = "sizeName";
    private static final String SINGLA_MD_RATE = "mdRate";
    private static final String SINGLA_MD_QTY = "mdQty";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerAddToBox(Context context) {
        this.context = context;
    }
    //TODO: COlor Table Delete
    public void BoxTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context); SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_BOX_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of wishlist Table
    public void insertBoxListTable(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_BOX_TABLE);
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
        final int ColorID = ih.getColumnIndex(SINGLA_COLOR_ID);
        final int ColorName = ih.getColumnIndex(SINGLA_COLOR_NAME);
        final int SizeID = ih.getColumnIndex(SINGLA_SIZE_ID);
        final int SizeName = ih.getColumnIndex(SINGLA_SIZE_NAME);
        final int MDRate = ih.getColumnIndex(SINGLA_MD_RATE);
        final int MdQty = ih.getColumnIndex(SINGLA_MD_QTY);
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
                ih.bind(ColorID, list.get(x).get("ColorID"));
                ih.bind(ColorName, list.get(x).get("ColorName"));
                ih.bind(SizeID, list.get(x).get("SizeID"));
                ih.bind(SizeName, list.get(x).get("SizeName"));
                ih.bind(MDRate, list.get(x).get("MDRate"));
                ih.bind(MdQty, list.get(x).get("Quantity"));
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
    public List<RecyclerBoxGroupDataset> getBoxGroupList(){
        List<RecyclerBoxGroupDataset> dataList=new ArrayList<RecyclerBoxGroupDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_GROUP_ID+", "+SINGLA_GROUP_NAME+","+SINGLA_GROUP_IMAGE+","+SINGLA_MAIN_GROUP+" from " + SINGLA_BOX_TABLE+" ORDER BY "+SINGLA_GROUP_NAME+" ASC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                RecyclerBoxGroupDataset dataset=new RecyclerBoxGroupDataset(cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_IMAGE)),cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP)));
                dataList.add(dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<RecyclerBoxListDataset> getBoxItemsGroupBy(String GroupID){
        List<RecyclerBoxListDataset> list=new ArrayList<RecyclerBoxListDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_COLOR_ID+","+SINGLA_COLOR_NAME+","+SINGLA_GROUP_ID+","+SINGLA_GROUP_NAME+","+SINGLA_ITEM_ID+", "+SINGLA_ITEM_CODE+", "+SINGLA_ITEM_NAME+","+SINGLA_ITEM_IMAGE+","+SINGLA_ITEM_STOCK+","+SINGLA_RATE+" from " + SINGLA_BOX_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                RecyclerBoxListDataset dataset=new RecyclerBoxListDataset(cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_CODE)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_IMAGE)));
                list.add(dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return list;
    }
    public List<Map<String,String>> getSizeListWithQtyRate(String GroupID,String ItemID,String ColorID){
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_SIZE_ID+","+SINGLA_SIZE_NAME+","+SINGLA_MD_QTY+","+SINGLA_MD_RATE+",("+SINGLA_MD_QTY+"*"+SINGLA_MD_RATE+") AS TotalQty  from " + SINGLA_BOX_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' AND "+SINGLA_ITEM_ID+"='"+ItemID+"' AND "+SINGLA_COLOR_ID+"='"+ColorID+"' AND "+SINGLA_MD_QTY+">0 ";
        String selectQuery2 = "SELECT SUM("+SINGLA_MD_QTY+"*"+SINGLA_MD_RATE+") AS GTotalQty,SUM("+SINGLA_MD_QTY+") AS MTotalQty  from " + SINGLA_BOX_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' AND "+SINGLA_ITEM_ID+"='"+ItemID+"' AND "+SINGLA_COLOR_ID+"='"+ColorID+"' AND "+SINGLA_MD_QTY+">0 ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor2 = db.rawQuery(selectQuery2,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("SizeID",cursor.getString(cursor.getColumnIndex(SINGLA_SIZE_ID)));
                map.put("SizeName",cursor.getString(cursor.getColumnIndex(SINGLA_SIZE_NAME)));
                map.put("MDQty",cursor.getString(cursor.getColumnIndex(SINGLA_MD_QTY)));
                map.put("MDRate",cursor.getString(cursor.getColumnIndex(SINGLA_MD_RATE)));
                map.put("TotalQty", cursor.getString(cursor.getColumnIndex("TotalQty")));
                list.add(map);
                //Log.e("Print",""+cursor.getString(cursor.getColumnIndex("GTotalQty")));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            if(cursor2.getCount()>0)
            {
                cursor2.moveToFirst();
                Map<String,String> map=new HashMap<String,String>();
                map.put("SizeID","SizeID");
                map.put("SizeName","Total");
                map.put("MDQty",cursor2.getString(cursor2.getColumnIndex("MTotalQty")));
                map.put("MDRate","");
                map.put("TotalQty", cursor2.getString(cursor2.getColumnIndex("GTotalQty")));
                list.add(map);
            }
            cursor2.close();
            db.close();
        }
        // returning
        return list;
    }
    public List<Map<String,String>> getTotalQtyWithTotalItem(){
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT  COUNT(DISTINCT "+SINGLA_ITEM_ID+") as TotalItem, SUM("+SINGLA_MD_QTY+") as TotalQty,SUM("+SINGLA_MD_QTY+"*"+SINGLA_MD_RATE+")as TotaAmt from " + SINGLA_BOX_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("TotalItem",cursor.getString(cursor.getColumnIndex("TotalItem")));
                map.put("TotalQty",cursor.getString(cursor.getColumnIndex("TotalQty")));
                map.put("TotalAmt",cursor.getString(cursor.getColumnIndex("TotaAmt")));
                list.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return list;
    }
    public List<Map<String,String>> getTotalQtyWithTotalItemByGroup(String GroupID){
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT  COUNT(DISTINCT "+SINGLA_ITEM_ID+") as TotalItem, SUM("+SINGLA_MD_QTY+") as TotalQty,SUM("+SINGLA_MD_QTY+"*"+SINGLA_MD_RATE+")as TotaAmt from " + SINGLA_BOX_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("TotalItem",cursor.getString(cursor.getColumnIndex("TotalItem")));
                map.put("TotalQty",cursor.getString(cursor.getColumnIndex("TotalQty")));
                map.put("TotalAmt",cursor.getString(cursor.getColumnIndex("TotaAmt")));
                list.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return list;
    }
    //TODO: Delete Item from box table
    public void DeleteItemFromBox(String ItemID) {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_BOX_TABLE, SINGLA_ITEM_ID +"='"+ItemID+"'" , null);
        db.close();
    }
    public void DeleteItemFromBox(String ItemID,String ColorID) {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_BOX_TABLE, SINGLA_ITEM_ID +"='"+ItemID+"' AND "+SINGLA_COLOR_ID +"='"+ColorID+"' " , null);
        db.close();
    }
}