package orderbooking.Database_Sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;

public class DatabaseSqlLiteHandlerChangeParty extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
	private static final int DATABASE_OLD_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "AndroidDBChangeParty";
 
    //----------------------------Table's Name--------------------------------------------------------------
    private static final String UNIQUE_ID_TABLE = "uniqueIDTable";
    private static final String CHANGE_PARTY_OLD_TABLE = "changePartyOldList";
    private static final String CHANGE_PARTY_NEW_TABLE = "changePartyNewList";
    private static final String CHANGE_SUB_PARTY_NEW_TABLE = "changeSubPartyNewList";
    //-------------------UNIQUE Table Column Names---------------------------------------------
    private static final String UNIQUE_KEY_ID = "id";
    private static final String UNIQUE_ID = "uniqueID";
    //-------------------Change Party Old Table Column Names---------------------------------------------
    private static final String CHANGE_PARTY_KEY_ID = "id";
    private static final String CHANGE_PARTY_OLD_PARTY_ID = "oldPartyId";
    private static final String CHANGE_PARTY_OLD_SUB_PARTY_ID = "oldSubPartyId";
    private static final String CHANGE_PARTY_OLD_REFERENCE_NAME = "oldAdtiName";
    //-------------------Change Party New Table Column Names---------------------------------------------
    private static final String CHANGE_PARTY_NEW_PARTY_ID = "partyId";
    private static final String CHANGE_PARTY_NEW_PARTY_NAME = "partyName";
    private static final String CHANGE_PARTY_NEW_SUB_PARTY_ID = "subPartyId";
    private static final String CHANGE_PARTY_NEW_SUB_PARTY_NAME = "subPartyName";
    private static final String CHANGE_PARTY_NEW_SUB_PARTY_CODE = "subPartyCode";
    private static final String CHANGE_PARTY_NEW_REFERENCE_NAME = "refName";
    private static final String CHANGE_PARTY_NEW_AGENT_ID = "agentId";
    private static final String CHANGE_PARTY_NEW_AGENT_NAME = "agentName";
    private static final String CHANGE_PARTY_NEW_CITY = "city";
    private static final String CHANGE_PARTY_NEW_STATE = "state";
    private static final String CHANGE_PARTY_NEW_MOBILE = "mobile";
    private static final String CHANGE_PARTY_NEW_ADDRESS = "address";
    private static final String CHANGE_PARTY_NEW_ADDRESS_2 = "address2";
    private static final String CHANGE_PARTY_NEW_ACTIVE = "active";
    private static final String CHANGE_PARTY_NEW_MULTI_ORDER_FLAG = "multiOrderFlag";
    private static final String CHANGE_PARTY_NEW_SUB_PARTY_FLAG = "subPartyFlag";
    private static final String CHANGE_PARTY_EMAIL = "email";
    private static final String CHANGE_PARTY_ACCOUNT_NO = "accountNo";
    private static final String CHANGE_PARTY_ACCOUNT_HOLDER_NAME = "accHolderName";
    private static final String CHANGE_PARTY_IFSC_CODE = "ifscCode";
    private static final String CHANGE_PARTY_ID_NAME = "idName";
    private static final String CHANGE_PARTY_GSTIN = "gstIn";
    private static final String CHANGE_PARTY_LABEL = "label";
    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerChangeParty(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_OLD_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
        String CREATE_UNIQUE_ID_TABLE = "CREATE TABLE " + UNIQUE_ID_TABLE + "("+ UNIQUE_KEY_ID + " INTEGER PRIMARY KEY," + UNIQUE_ID + " TEXT) ";
        db.execSQL(CREATE_UNIQUE_ID_TABLE);

    	String CREATE_CHANGE_PARTY_OLD_TABLE = "CREATE TABLE " + CHANGE_PARTY_OLD_TABLE + "("+ CHANGE_PARTY_KEY_ID + " INTEGER PRIMARY KEY," + CHANGE_PARTY_OLD_PARTY_ID + " TEXT," + CHANGE_PARTY_OLD_SUB_PARTY_ID + " TEXT," + CHANGE_PARTY_OLD_REFERENCE_NAME + " TEXT) ";
    	db.execSQL(CREATE_CHANGE_PARTY_OLD_TABLE);
    	
    	String CREATE_CHANGE_PARTY_NEW_TABLE = "CREATE TABLE " + CHANGE_PARTY_NEW_TABLE + "("+ CHANGE_PARTY_KEY_ID + " INTEGER PRIMARY KEY," + CHANGE_PARTY_NEW_PARTY_ID + " TEXT," + CHANGE_PARTY_NEW_PARTY_NAME + " TEXT," + CHANGE_PARTY_NEW_SUB_PARTY_ID + " TEXT," + CHANGE_PARTY_NEW_SUB_PARTY_NAME + " TEXT," + CHANGE_PARTY_NEW_REFERENCE_NAME + " TEXT," + CHANGE_PARTY_NEW_AGENT_ID + " TEXT," + CHANGE_PARTY_NEW_AGENT_NAME+ " TEXT," + CHANGE_PARTY_NEW_CITY + " TEXT," + CHANGE_PARTY_NEW_STATE+ " TEXT," + CHANGE_PARTY_NEW_MOBILE + " TEXT," + CHANGE_PARTY_NEW_ADDRESS + " TEXT," + CHANGE_PARTY_NEW_ACTIVE + " INTEGER," + CHANGE_PARTY_NEW_MULTI_ORDER_FLAG + " INTEGER," + CHANGE_PARTY_NEW_SUB_PARTY_FLAG + " INTEGER,"+ CHANGE_PARTY_EMAIL + " TEXT,"+ CHANGE_PARTY_ACCOUNT_NO + " TEXT,"+ CHANGE_PARTY_ACCOUNT_HOLDER_NAME + " TEXT,"+ CHANGE_PARTY_IFSC_CODE + " TEXT,"+ CHANGE_PARTY_ID_NAME + " TEXT,"+ CHANGE_PARTY_GSTIN + " TEXT,"+ CHANGE_PARTY_LABEL + " TEXT) ";
    	db.execSQL(CREATE_CHANGE_PARTY_NEW_TABLE);
    	
    	String CREATE_CHANGE_SUB_PARTY_NEW_TABLE = "CREATE TABLE " + CHANGE_SUB_PARTY_NEW_TABLE + "("+ CHANGE_PARTY_KEY_ID + " INTEGER PRIMARY KEY," + CHANGE_PARTY_NEW_PARTY_ID + " TEXT," + CHANGE_PARTY_NEW_PARTY_NAME + " TEXT," + CHANGE_PARTY_NEW_SUB_PARTY_ID + " TEXT," + CHANGE_PARTY_NEW_SUB_PARTY_NAME + " TEXT," + CHANGE_PARTY_NEW_SUB_PARTY_CODE + " TEXT," + CHANGE_PARTY_NEW_CITY + " TEXT," + CHANGE_PARTY_NEW_STATE+ " TEXT," + CHANGE_PARTY_NEW_MOBILE + " TEXT," + CHANGE_PARTY_NEW_ADDRESS + " TEXT," + CHANGE_PARTY_NEW_ADDRESS_2 + " TEXT,"+ CHANGE_PARTY_EMAIL + " TEXT,"+ CHANGE_PARTY_ACCOUNT_NO + " TEXT,"+ CHANGE_PARTY_ACCOUNT_HOLDER_NAME + " TEXT,"+ CHANGE_PARTY_IFSC_CODE + " TEXT,"+ CHANGE_PARTY_ID_NAME + " TEXT,"+ CHANGE_PARTY_GSTIN + " TEXT) ";
    	db.execSQL(CREATE_CHANGE_SUB_PARTY_NEW_TABLE);
    }   
 //------------------------------------ Upgrading database-------------------------------------------------------------
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +UNIQUE_ID_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +CHANGE_PARTY_OLD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +CHANGE_PARTY_NEW_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +CHANGE_SUB_PARTY_NEW_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void deleteChangePartyOLD()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHANGE_PARTY_OLD_TABLE,null,null);
        db.close();
    }
    public void deleteChangePartyNEW()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHANGE_PARTY_NEW_TABLE,null,null);
        db.close();
    }
    public void deleteChangeSubPartyNEW()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHANGE_SUB_PARTY_NEW_TABLE,null,null);
        db.close();
    }
  //---------------------------------- Inserting Data of Change Party OLD Table---------------------------------------
    public void insertChangePartyOldData(String OldPartyID,String OldSubPartyID,String OldAdtiName){
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(CHANGE_PARTY_OLD_PARTY_ID, OldPartyID);
    	values.put(CHANGE_PARTY_OLD_SUB_PARTY_ID, OldSubPartyID);
    	values.put(CHANGE_PARTY_OLD_REFERENCE_NAME, OldAdtiName);
    	// Inserting Row
        db.insert(CHANGE_PARTY_OLD_TABLE, null, values);
        db.close(); // Closing database connection
    }
    //---------------------------------- Inserting Data of Change Party New Table---------------------------------------
    public void insertChangePartyNewData(List<Map<String,String>> list){
    	
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,CHANGE_PARTY_NEW_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int PartyID = ih.getColumnIndex(CHANGE_PARTY_NEW_PARTY_ID);
        final int PartyName = ih.getColumnIndex(CHANGE_PARTY_NEW_PARTY_NAME);
        final int SubPartyID = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_ID);
        final int SubPartyName = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_NAME);
        final int RefName = ih.getColumnIndex(CHANGE_PARTY_NEW_REFERENCE_NAME);
        final int AgentID = ih.getColumnIndex(CHANGE_PARTY_NEW_AGENT_ID);
        final int AgentName = ih.getColumnIndex(CHANGE_PARTY_NEW_AGENT_NAME);
        final int City = ih.getColumnIndex(CHANGE_PARTY_NEW_CITY);
        final int State = ih.getColumnIndex(CHANGE_PARTY_NEW_STATE);
        final int Mobile = ih.getColumnIndex(CHANGE_PARTY_NEW_MOBILE);
        final int Address = ih.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS);
        final int Active = ih.getColumnIndex(CHANGE_PARTY_NEW_ACTIVE);
        final int MultiOrder = ih.getColumnIndex(CHANGE_PARTY_NEW_MULTI_ORDER_FLAG);
        final int SubPartyApplicable = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_FLAG);
        final int Email = ih.getColumnIndex(CHANGE_PARTY_EMAIL);
        final int AccountNo = ih.getColumnIndex(CHANGE_PARTY_ACCOUNT_NO);
        final int AccountHolderName = ih.getColumnIndex(CHANGE_PARTY_ACCOUNT_HOLDER_NAME);
        final int IFSCCOde = ih.getColumnIndex(CHANGE_PARTY_IFSC_CODE);
        final int IDName = ih.getColumnIndex(CHANGE_PARTY_ID_NAME);
        final int GSTIN = ih.getColumnIndex(CHANGE_PARTY_GSTIN);
        final int Label = ih.getColumnIndex(CHANGE_PARTY_LABEL);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(PartyName, list.get(x).get("PartyName"));
                ih.bind(SubPartyID, list.get(x).get("SubPartyID"));
                ih.bind(SubPartyName, list.get(x).get("SubPartyName"));
                ih.bind(RefName, list.get(x).get("RefName"));
                ih.bind(AgentID, list.get(x).get("AgentID"));
                ih.bind(AgentName, list.get(x).get("AgentName"));
                ih.bind(City, list.get(x).get("City"));
                ih.bind(State, list.get(x).get("State"));
                ih.bind(Mobile, list.get(x).get("Mobile"));
                ih.bind(Address, list.get(x).get("Address"));
                ih.bind(Active, list.get(x).get("Active"));
                ih.bind(MultiOrder, list.get(x).get("MultiOrder"));
                ih.bind(SubPartyApplicable, list.get(x).get("SubPartyApplicable"));
                ih.bind(Email, list.get(x).get("Email"));
                ih.bind(AccountNo, list.get(x).get("AccountNo"));
                ih.bind(AccountHolderName, list.get(x).get("AccountHolderName"));
                ih.bind(IFSCCOde, list.get(x).get("IFSCCOde"));
                ih.bind(IDName, list.get(x).get("IDName"));
                ih.bind(GSTIN, list.get(x).get("GSTIN"));
                ih.bind(Label, list.get(x).get("Label"));
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
    public void insertChangeSubPartyNewData(List<Map<String,String>> list){
    	final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,CHANGE_SUB_PARTY_NEW_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int PartyID = ih.getColumnIndex(CHANGE_PARTY_NEW_PARTY_ID);
        final int SubPartyID = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_ID);
        final int SubPartyName = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_NAME);
        final int SubPartyCode = ih.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_CODE);
        final int City = ih.getColumnIndex(CHANGE_PARTY_NEW_CITY);
        final int State = ih.getColumnIndex(CHANGE_PARTY_NEW_STATE);
        final int Mobile = ih.getColumnIndex(CHANGE_PARTY_NEW_MOBILE);
        final int Address1 = ih.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS);
        final int Address2 = ih.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS_2);
        final int Email = ih.getColumnIndex(CHANGE_PARTY_EMAIL);
        final int AccountNo = ih.getColumnIndex(CHANGE_PARTY_ACCOUNT_NO);
        final int AccountHolderName = ih.getColumnIndex(CHANGE_PARTY_ACCOUNT_HOLDER_NAME);
        final int IFSCCOde = ih.getColumnIndex(CHANGE_PARTY_IFSC_CODE);
        final int IDName = ih.getColumnIndex(CHANGE_PARTY_ID_NAME);
        final int GSTIN = ih.getColumnIndex(CHANGE_PARTY_GSTIN);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(SubPartyID, list.get(x).get("SubPartyID"));
                ih.bind(SubPartyName, list.get(x).get("SubPartyName"));
                ih.bind(SubPartyCode, list.get(x).get("SubPartyCode"));
                ih.bind(City, list.get(x).get("City"));
                ih.bind(State, list.get(x).get("State"));
                ih.bind(Mobile, list.get(x).get("Mobile"));
                ih.bind(Address1, list.get(x).get("Address1"));
                ih.bind(Address2, list.get(x).get("Address2"));
                ih.bind(Email, list.get(x).get("Email"));
                ih.bind(AccountNo, list.get(x).get("AccountNo"));
                ih.bind(AccountHolderName, list.get(x).get("AccountHolderName"));
                ih.bind(IFSCCOde, list.get(x).get("IFSCCOde"));
                ih.bind(IDName, list.get(x).get("IDName"));
                ih.bind(GSTIN, list.get(x).get("GSTIN"));
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
    
    public List<SelectCustomerForOrderDataset> getPartyList(){
    	List<SelectCustomerForOrderDataset> dataList = new ArrayList<SelectCustomerForOrderDataset>();
        // Select All Query
        String selectQuery = "select distinct * from " + CHANGE_PARTY_NEW_TABLE;//+" where  "+CHANGE_PARTY_NEW_PARTY_ID+" NOT IN (select "+CHANGE_PARTY_OLD_PARTY_ID+" from "+CHANGE_PARTY_OLD_TABLE+" where" +
        		//" "+CHANGE_PARTY_OLD_SUB_PARTY_ID+" = '' or "+CHANGE_PARTY_OLD_SUB_PARTY_ID+" is null)";
        System.out.println("Change PartyListQuery:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
                dataList.add(new SelectCustomerForOrderDataset(cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_PARTY_ID)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_AGENT_ID)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_AGENT_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_MOBILE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_CITY)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_STATE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_ACTIVE)),cursor.getInt(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_FLAG)),cursor.getInt(cursor.getColumnIndex(CHANGE_PARTY_NEW_MULTI_ORDER_FLAG)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_EMAIL)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ACCOUNT_NO)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ACCOUNT_HOLDER_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_IFSC_CODE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ID_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_GSTIN)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_LABEL))));
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        //System.out.println("Change Party List NewParty:"+dataList.toString());
        
        }
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getOldPartyList(){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
        String selectQuery = "select * from " + CHANGE_PARTY_OLD_TABLE+" where "+CHANGE_PARTY_OLD_PARTY_ID+"='70669B3B-9C75-4BE0-B02C-B2FC8CED0A29'";
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		HashMap< String, String> map=new HashMap<String,String>();
            	map.put("OldPartyID", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_OLD_PARTY_ID)));
            	map.put("OldSubPartyID", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_OLD_SUB_PARTY_ID)));
            	map.put("OldAdtiName", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_OLD_REFERENCE_NAME)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
        System.out.println("OldParty:"+dataList.toString());
    	return dataList;
    	
    }
    public List<SelectSubCustomerForOrderDataset> getSubPartyList(){
    	List<SelectSubCustomerForOrderDataset> dataList = new ArrayList<SelectSubCustomerForOrderDataset>();
        // Select All Query
    	String selectQuery = "select distinct * from " + CHANGE_SUB_PARTY_NEW_TABLE;//+" where  "+CHANGE_PARTY_NEW_SUB_PARTY_ID+" NOT IN (select "+CHANGE_PARTY_OLD_SUB_PARTY_ID+" from "+CHANGE_PARTY_OLD_TABLE+")";//
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
                dataList.add(new SelectSubCustomerForOrderDataset(cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_PARTY_ID)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_ID)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_CODE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_MOBILE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_CITY)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_STATE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS_2)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_EMAIL)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ACCOUNT_NO)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ACCOUNT_HOLDER_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_IFSC_CODE)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_ID_NAME)),cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_GSTIN))));
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
    	return dataList;
    }
    public ArrayList<HashMap<String,String>> getALLSubPartyList(){
    	ArrayList<HashMap<String,String>> dataList =null;
    	dataList=new ArrayList<HashMap<String,String>>();
        // Select All Query
    	String selectQuery = "select * from " + CHANGE_SUB_PARTY_NEW_TABLE;
        System.out.println("Query:"+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
        	cursor.moveToFirst();
        	do {
        		HashMap< String, String> map=new HashMap<String,String>();
        		map.put("PartyID", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_PARTY_ID)));
            	map.put("PartyName", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_PARTY_NAME)));
            	map.put("SubPartyID", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_ID)));
            	map.put("SubPartyName", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_SUB_PARTY_NAME)));
            	map.put("AgentID", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_AGENT_ID)));
            	map.put("AgentName", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_AGENT_NAME)));
            	map.put("City", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_CITY)));
            	map.put("State", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_STATE)));
            	map.put("Mobile", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_MOBILE)));
            	map.put("Address", cursor.getString(cursor.getColumnIndex(CHANGE_PARTY_NEW_ADDRESS)));
            	dataList.add(map);
        		}while (cursor.moveToNext());	
        // closing connection
        cursor.close();
        db.close();
    	// returning lables
        }
        System.out.println("All  Sub Party List :"+dataList.toString());
    	return dataList;
    	
    }

}