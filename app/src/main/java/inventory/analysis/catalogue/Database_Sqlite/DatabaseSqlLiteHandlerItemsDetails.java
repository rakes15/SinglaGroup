package inventory.analysis.catalogue.Database_Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqliteRootHandler;

public class DatabaseSqlLiteHandlerItemsDetails {
    //TODo: Table Name
    private static final String SINGLA_COLORS_TABLE = "colorsTbl";
    //TODO: Colors  Table Column's Name
    private static final String SINGLA_KEY_ID = "id";
    private static final String SINGLA_ITEM_ID = "itemID";
    private static final String SINGLA_ITEM_CODE = "itemCode";
    private static final String SINGLA_ITEM_NAME = "itemName";
    private static final String SINGLA_ITEM_IMAGE = "itemImage";
    private static final String SINGLA_ITEM_STOCK = "itemStock";
    private static final String SINGLA_MD_QTY = "mdQty";
    private static final String SINGLA_MD_STOCK = "mdStock";
    private static final String SINGLA_RATE = "rate";
    private static final String SINGLA_MD_RATE = "mdRate";
    private static final String SINGLA_UNIT = "unit";
    private static final String SINGLA_SIZE_ID = "sizeID";
    private static final String SINGLA_SIZE_NAME = "sizeName";
    private static final String SINGLA_COLOR_ID = "colorID";
    private static final String SINGLA_COLOR_NAME = "colorName";
    private static final String SINGLA_COLOR_FAMILY_ID = "colorFamilyID";
    private static final String SINGLA_COLOR_FAMILY_NAME = "colorFamilyName";
    private static final String SINGLA_ATTR1 = "attr1";
    private static final String SINGLA_ATTR2 = "attr2";
    private static final String SINGLA_ATTR3 = "attr3";
    private static final String SINGLA_ATTR4 = "attr4";
    private static final String SINGLA_ATTR5 = "attr5";
    private static final String SINGLA_ATTR6 = "attr6";
    private static final String SINGLA_ATTR7 = "attr7";
    private static final String SINGLA_ATTR8 = "attr8";
    private static final String SINGLA_ATTR9 = "attr9";
    private static final String SINGLA_ATTR10 = "attr10";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerItemsDetails(Context context) {
        this.context = context;
    }
    //TODO: COlor Table Delete
    public void ColorsTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_COLORS_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of Color Table
    public void insertItemDetailsTables(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        // you can use INSERT only
        String sql = "INSERT OR REPLACE INTO " + SINGLA_COLORS_TABLE + " ("+SINGLA_ITEM_ID+","+SINGLA_ITEM_CODE+","+SINGLA_ITEM_NAME+","+SINGLA_ITEM_IMAGE+","+SINGLA_ITEM_STOCK+","+SINGLA_COLOR_ID+","+SINGLA_COLOR_NAME+","+SINGLA_COLOR_FAMILY_ID+","+SINGLA_COLOR_FAMILY_NAME+","+SINGLA_MD_STOCK+","+SINGLA_RATE+","+SINGLA_MD_RATE+","+SINGLA_SIZE_ID+","+SINGLA_SIZE_NAME+","+SINGLA_UNIT+","+SINGLA_ATTR1+","+SINGLA_ATTR2+","+SINGLA_ATTR3+","+SINGLA_ATTR4+","+SINGLA_ATTR5+","+SINGLA_ATTR6+","+SINGLA_ATTR7+","+SINGLA_ATTR8+","+SINGLA_ATTR9+","+SINGLA_ATTR10+","+SINGLA_MD_QTY+") VALUES ( ?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ? )";
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.execSQL("PRAGMA synchronous=OFF");
        db.setLockingEnabled(false);
        db.beginTransactionNonExclusive();
        // db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);
        for(int x=0; x<list.size(); x++){

            stmt.bindString(1, list.get(x).get("ItemID"));
            stmt.bindString(2, list.get(x).get("ItemCode"));
            stmt.bindString(3, list.get(x).get("ItemName"));
            stmt.bindString(4, list.get(x).get("ItemImage"));
            stmt.bindString(5, (list.get(x).get("ItemStock")==null?"0":list.get(x).get("ItemStock")));
            stmt.bindString(6, list.get(x).get("ColorID"));
            stmt.bindString(7, list.get(x).get("Color"));
            stmt.bindString(8, list.get(x).get("ColorFamilyID"));
            stmt.bindString(9, list.get(x).get("ColorFamily"));
            stmt.bindString(10, list.get(x).get("MDStock"));
            stmt.bindString(11, list.get(x).get("Rate"));
            stmt.bindString(12, list.get(x).get("MDRate"));
            stmt.bindString(13, list.get(x).get("SizeID"));
            stmt.bindString(14, list.get(x).get("Size"));
            stmt.bindString(15, list.get(x).get("Unit"));
            stmt.bindString(16, list.get(x).get("Attr1"));
            stmt.bindString(17, list.get(x).get("Attr2"));
            stmt.bindString(18, list.get(x).get("Attr3"));
            stmt.bindString(19, list.get(x).get("Attr4"));
            stmt.bindString(20, list.get(x).get("Attr5"));
            stmt.bindString(21, list.get(x).get("Attr6"));
            stmt.bindString(22, list.get(x).get("Attr7"));
            stmt.bindString(23, list.get(x).get("Attr8"));
            stmt.bindString(24, list.get(x).get("Attr9"));
            stmt.bindString(25, list.get(x).get("Attr10"));
            stmt.bindString(26, list.get(x).get("BookQty"));

            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.setLockingEnabled(true);
        db.execSQL("PRAGMA synchronous=NORMAL");
        db.close();
        final long endtime = System.currentTimeMillis();
        Log.e("Time:", "" + String.valueOf(endtime - startTime));
    }
    public List<Map<String,String>> getColors(){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_COLOR_ID+", "+SINGLA_COLOR_NAME+","+SINGLA_ITEM_NAME+","+SINGLA_ITEM_IMAGE+" from " + SINGLA_COLORS_TABLE+" ORDER BY "+SINGLA_COLOR_NAME+" ASC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_NAME)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_NAME)));
                map.put("ItemImage", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_IMAGE)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<Map<String,String>> getColorByColorID(String ColorID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_COLOR_ID+", "+SINGLA_COLOR_NAME+","+SINGLA_ITEM_NAME+","+SINGLA_ITEM_IMAGE+" from " + SINGLA_COLORS_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_ID)));
                map.put("ColorName", cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_NAME)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_NAME)));
                map.put("ItemImage", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_IMAGE)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        String ColorName="",ItemImage="",tColorID="",tColorName="",tItemImage="";
        for(int i=0;i<dataList.size();i++){
            if(ColorID.equals(dataList.get(i).get("ColorID")) && i>0){
                ColorName=dataList.get(i).get("ColorName");
                ItemImage=dataList.get(i).get("ItemImage");
                tColorID=dataList.get(0).get("ColorID");
                tColorName=dataList.get(0).get("ColorName");
                tItemImage=dataList.get(0).get("ItemImage");
                Map< String, String> map=new HashMap<String,String>();
                map.put("ColorID", ColorID);
                map.put("ColorName", ColorName);
                map.put("ItemImage", ItemImage);
                dataList.set(0,map);
                Map< String, String> map2=new HashMap<String,String>();
                map2.put("ColorID", tColorID);
                map2.put("ColorName", tColorName);
                map2.put("ItemImage", tItemImage);
                dataList.set(i,map2);
            }
        }
        // returning
        return dataList;
    }
    public List<Map<String,String>> getSizeList(String ItemID,String ColorID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_SIZE_ID+","+SINGLA_ITEM_ID+","+SINGLA_COLOR_ID+", "+SINGLA_SIZE_NAME+","+SINGLA_MD_RATE+","+SINGLA_MD_STOCK+","+SINGLA_MD_QTY+" from " + SINGLA_COLORS_TABLE+" WHERE "+SINGLA_ITEM_ID+"='"+ItemID+"' AND  "+SINGLA_COLOR_ID+"='"+ColorID+"' Order By "+SINGLA_SIZE_NAME+" Asc";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(SINGLA_SIZE_ID)));
                map.put("SizeName", cursor.getString(cursor.getColumnIndex(SINGLA_SIZE_NAME)));
                map.put("ItemID", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_ID)));
                map.put("ColorID", cursor.getString(cursor.getColumnIndex(SINGLA_COLOR_ID)));
                map.put("MDRate", cursor.getString(cursor.getColumnIndex(SINGLA_MD_RATE)));
                map.put("MDStock", cursor.getString(cursor.getColumnIndex(SINGLA_MD_STOCK)));
                map.put("MDQTY", String.valueOf((cursor.getInt(cursor.getColumnIndex(SINGLA_MD_QTY))==0)?"":cursor.getInt(cursor.getColumnIndex(SINGLA_MD_QTY))));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<Map<String,String>> getSizeWiseQtyList(String ItemID,String ColorID,String SizeID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_SIZE_ID+","+SINGLA_MD_QTY+","+SINGLA_MD_STOCK+" from " + SINGLA_COLORS_TABLE+" WHERE "+SINGLA_ITEM_ID+"='"+ItemID+"' AND  "+SINGLA_COLOR_ID+"='"+ColorID+"' AND  "+SINGLA_SIZE_ID+"='"+SizeID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("SizeID", cursor.getString(cursor.getColumnIndex(SINGLA_SIZE_ID)));
                map.put("MDQTY", cursor.getString(cursor.getColumnIndex(SINGLA_MD_QTY)));
                map.put("MDStock", cursor.getString(cursor.getColumnIndex(SINGLA_MD_STOCK)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<Map<String,String>> getItemDetails(String ItemID,String ColorID){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_ITEM_ID+","+SINGLA_ITEM_NAME+", "+SINGLA_ITEM_CODE+","+SINGLA_ITEM_IMAGE+","+SINGLA_ATTR1+","+SINGLA_ATTR2+","+SINGLA_ATTR3+","+SINGLA_ATTR4+","+SINGLA_ATTR5+","+SINGLA_ATTR6+","+SINGLA_ATTR7+","+SINGLA_ATTR8+","+SINGLA_ATTR9+","+SINGLA_ATTR10+" from " + SINGLA_COLORS_TABLE+" WHERE "+SINGLA_ITEM_ID+"='"+ItemID+"' AND  "+SINGLA_COLOR_ID+"='"+ColorID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("ItemID", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_ID)));
                map.put("ItemName", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_NAME)));
                map.put("ItemCode", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_CODE)));
                map.put("ItemImage", cursor.getString(cursor.getColumnIndex(SINGLA_ITEM_IMAGE)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public String[] getOtherDetails(String ItemID,String ColorID){
        //List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        String[] strArr=new String[10];
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_ATTR1+","+SINGLA_ATTR2+","+SINGLA_ATTR3+","+SINGLA_ATTR4+","+SINGLA_ATTR5+","+SINGLA_ATTR6+","+SINGLA_ATTR7+","+SINGLA_ATTR8+","+SINGLA_ATTR9+","+SINGLA_ATTR10+" from " + SINGLA_COLORS_TABLE+" WHERE "+SINGLA_ITEM_ID+"='"+ItemID+"'";
        //Log.e("Print",""+selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            int i=0;
            cursor.moveToFirst();
            do {
                for(int j=1;j<=10;j++) {
                    if(cursor.getString(cursor.getColumnIndex("attr"+j))!="0")
                    {
                        strArr[i++] = cursor.getString(cursor.getColumnIndex("attr"+j));
                    }
                    else {
                        strArr[i++]="";
                    }
                }
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return strArr;
    }
    public void UpdateQty(String ItemID,String ColorID,String SizeID, int MDQty){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLA_MD_QTY, MDQty);
        db.update(SINGLA_COLORS_TABLE, values, SINGLA_ITEM_ID + "='" + ItemID + "' AND " + SINGLA_COLOR_ID + "='" + ColorID + "' AND " + SINGLA_SIZE_ID + "='" + SizeID + "' ", null);
        db.close();
    }
}