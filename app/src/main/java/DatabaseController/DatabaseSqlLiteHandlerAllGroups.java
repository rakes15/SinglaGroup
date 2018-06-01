package DatabaseController;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.singlagroup.datasets.MenuDataset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import orderbooking.catalogue.dataset.RecyclerGroupDataset;

public class DatabaseSqlLiteHandlerAllGroups extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
    //TODO: Database Name
    private static final String DATABASE_NAME = "ItemCategory";
    //TODo: Table Name
    private static final String SINGLA_GROUPS_TABLE = "groupSTbl";
    private static final String SINGLA_SIZE_SET_TABLE = "sizeSetTbl";
    //TODO: Groups  Table Column's Name
    private static final String SINGLA_KEY_ID = "id";
    private static final String SINGLA_MAIN_GROUP_ID = "mainGroupNameID";
    private static final String SINGLA_MAIN_GROUP_NAME = "mainGroupName";
    private static final String SINGLA_MAIN_GROUP_SEQUENCE = "mainGroupSeq";
    private static final String SINGLA_GROUP_ID = "groupID";
    private static final String SINGLA_GROUP_NAME = "groupName";
    private static final String SINGLA_GROUP_IMAGE_PATH = "groupImagePath";
    
    //TODO: Size Set  Table Column's Name
    private static final String SINGLA_SIZE_COUNT = "sizeCount";
    private static final String SINGLA_REQUIRED = "required";
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerAllGroups(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //TODO: Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_TABLE_GROUPS = "CREATE TABLE " + SINGLA_GROUPS_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_MAIN_GROUP_ID + " TEXT," + SINGLA_MAIN_GROUP_NAME + " TEXT," + SINGLA_MAIN_GROUP_SEQUENCE + " INTEGER," + SINGLA_GROUP_ID + " TEXT," + SINGLA_GROUP_NAME + " TEXT," + SINGLA_GROUP_IMAGE_PATH + " TEXT) ";
        db.execSQL(CREATE_TABLE_GROUPS);
        String CREATE_TABLE_SIZE_SET = "CREATE TABLE " + SINGLA_SIZE_SET_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_MAIN_GROUP_ID + " TEXT," + SINGLA_GROUP_ID + " TEXT," + SINGLA_SIZE_COUNT + " INTEGER," + SINGLA_REQUIRED + " INTEGER) ";
        db.execSQL(CREATE_TABLE_SIZE_SET);
    }
    //TODO: Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SINGLA_GROUPS_TABLE);
        // Create tables again
        onCreate(db);
    }
    //TODO: All Groups Table Delete
    public void GroupsTableDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SINGLA_GROUPS_TABLE, null, null);
        db.close();
    }
    //TODO: Size set Table Delete
    public void SizeSetTableDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SINGLA_SIZE_SET_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of AllGroup Table
    public void insertGroupsTables(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_GROUPS_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int MainGroupID = ih.getColumnIndex(SINGLA_MAIN_GROUP_ID);
        final int MainGroup = ih.getColumnIndex(SINGLA_MAIN_GROUP_NAME);
        final int Sequence = ih.getColumnIndex(SINGLA_MAIN_GROUP_SEQUENCE);
        final int GroupID = ih.getColumnIndex(SINGLA_GROUP_ID);
        final int GroupName = ih.getColumnIndex(SINGLA_GROUP_NAME);
        final int GroupImage = ih.getColumnIndex(SINGLA_GROUP_IMAGE_PATH);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                 ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
                 ih.bind(MainGroup, list.get(x).get("MainGroup"));
                 ih.bind(Sequence, list.get(x).get("Sequence"));
                 ih.bind(GroupID, list.get(x).get("GroupID"));
                 ih.bind(GroupName, list.get(x).get("GroupName"));
                 ih.bind(GroupImage, list.get(x).get("GroupImage"));
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
    //TODO: Inserting Data of Size set Table
    public void insertSizeSetTables(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_SIZE_SET_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int MainGroupID = ih.getColumnIndex(SINGLA_MAIN_GROUP_ID);
        final int GroupID = ih.getColumnIndex(SINGLA_GROUP_ID);
        final int SizeCount = ih.getColumnIndex(SINGLA_SIZE_COUNT);
        final int Required = ih.getColumnIndex(SINGLA_REQUIRED);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
                ih.bind(GroupID, list.get(x).get("GroupID"));
                ih.bind(SizeCount, list.get(x).get("SizeCount"));
                ih.bind(Required, list.get(x).get("Required"));
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
    public List<Map<String,String>> getMainGroup(){
        List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_MAIN_GROUP_ID+"), "+SINGLA_MAIN_GROUP_NAME+" from " + SINGLA_GROUPS_TABLE+" ORDER BY "+SINGLA_MAIN_GROUP_SEQUENCE;
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map< String, String> map=new HashMap<String,String>();
                map.put("MainGroupID", cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP_ID)));
                map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP_NAME)));
                dataList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<MenuDataset> getMainGroupDatasetList(){
        List<MenuDataset> dataList=new ArrayList<MenuDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_MAIN_GROUP_ID+"), "+SINGLA_MAIN_GROUP_NAME+" from " + SINGLA_GROUPS_TABLE+" ORDER BY "+SINGLA_MAIN_GROUP_SEQUENCE;
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        MenuDataset dataset;
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataset=new MenuDataset(cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP_NAME)));
                dataList.add(dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<RecyclerGroupDataset> getGroup(String MainGroupID){
        List<RecyclerGroupDataset> dataList=new ArrayList<RecyclerGroupDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_GROUP_ID+"), "+SINGLA_GROUP_NAME+", "+SINGLA_GROUP_IMAGE_PATH+" from " + SINGLA_GROUPS_TABLE+" WHERE "+SINGLA_MAIN_GROUP_ID+"="+MainGroupID+" ORDER BY "+SINGLA_GROUP_NAME+" ASC";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                RecyclerGroupDataset dataset=new RecyclerGroupDataset(cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_IMAGE_PATH)));
                dataList.add(dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }

        // returning
        return dataList;
    }
    public String[] getGroupImage(String GroupID){
        String[] dataStr=new String[2];
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_GROUP_IMAGE_PATH+"),"+SINGLA_MAIN_GROUP_NAME+" from " + SINGLA_GROUPS_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataStr[0]=cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_IMAGE_PATH));
                dataStr[1]=cursor.getString(cursor.getColumnIndex(SINGLA_MAIN_GROUP_NAME));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataStr;
    }
    public String getGroupID(String GroupName){
        String dataStr="";
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_GROUP_ID+" from " + SINGLA_GROUPS_TABLE+" WHERE "+SINGLA_GROUP_NAME+"='"+GroupName+"' ";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataStr=cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_ID));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataStr;
    }
    public String getGroupName(String GroupID){
        String dataStr="";
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_GROUP_NAME+" from " + SINGLA_GROUPS_TABLE+" WHERE "+SINGLA_GROUP_ID+"='"+GroupID+"' ";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataStr=cursor.getString(cursor.getColumnIndex(SINGLA_GROUP_NAME));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataStr;
    }
    public int getSizeSet(String GroupID,int SizeCount){
        int required = 0;
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_SIZE_COUNT+"),"+SINGLA_REQUIRED+" from " + SINGLA_SIZE_SET_TABLE + " WHERE "+SINGLA_GROUP_ID+" = '"+GroupID+"' AND "+SINGLA_SIZE_COUNT+" = "+SizeCount+" ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                required = cursor.getInt(cursor.getColumnIndex(SINGLA_REQUIRED));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return required;
    }
}