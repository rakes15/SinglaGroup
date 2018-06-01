package administration.database;

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

import administration.datasets.VTypeDetailsDataset;
import administration.datasets.VoucherAuthorizeListDataset;

public class DatabaseSqlLiteHandlerVoucherAuthorization extends SQLiteOpenHelper {
    //----------------------- Database Version----------------------
    private static final int DATABASE_VERSION = 1;
 
    // -----------------------Database Name--------------
    public static final String DATABASE_NAME = "SG_VoucherAuthorization";
    //----------------------------Table Name--------------------------------------------------------------
    private static final String AUTHORIZATION_TABLE = "authorizationTbl";   
    //-------------------Barcode Scanner Table Column Names---------------------------------------------
    private static final String AUTHORIZATION_KEY_ID = "id";
    private static final String AUTHORIZATION_DOCUMENT_ID = "documentID";
    private static final String AUTHORIZATION_DOCUMENT_DATE = "documentDate";
    private static final String AUTHORIZATION_BRANCH = "branch";
    private static final String AUTHORIZATION_AMENDMEND_REVISION_NO = "amendmendRevisionNo"; 
    private static final String AUTHORIZATION_DOCUMENT_NO = "documentNo";
    private static final String AUTHORIZATION_PARTY_NAME = "partyName";
    private static final String AUTHORIZATION_NARRATION = "narration";
    private static final String AUTHORIZATION_REMARK = "remark";
    private static final String AUTHORIZATION_STATUS = "status";
    private static final String AUTHORIZATION_REQUESTED_BY = "requestedUserName";
    private static final String AUTHORIZATION_AMOUNT = "amount";
    private static final String AUTHORIZATION_VOUCHER_TYPE = "voucherType";
    private static final String AUTHORIZATION_VOUCHER_HEADING = "voucherHeading";
    private static final String AUTHORIZATION_ID = "authorizationId";
    private static final String AUTHORIZATION_LEVEL = "level";
    private static final String AUTHORIZATION_REQUESTED_USER_NAME = "username";
    private static final String AUTHORIZATION_REQUESTED_USER_ID = "userId";
    private static final String AUTHORIZATION_BRANCH_ID = "branchId";
    private static final String AUTHORIZATION_DIVISION_ID = "divisionId";
    private static final String AUTHORIZATION_BRAND_ID = "brandId";
    private static final String AUTHORIZATION_DEPARTMENT_ID = "departmentId";
    private static final String AUTHORIZATION_PROJECT_ID = "projectID";
    private static final String AUTHORIZATION_CLASSIFICATION_CODE_ID = "classificationID";
    private static final String AUTHORIZATION_PARTY_ID = "partyId";
    private static final String AUTHORIZATION_APPROVAL_DATE = "approvalDate";
    private static final String AUTHORIZATION_APPROVAL_TIME = "approvalTime";
    private static final String AUTHORIZATION_NO_OF_LEVELS = "noOfLevels";
    private static final String AUTHORIZATION_ITEM_GROUP_ID = "itemGroupID";
    private static final String AUTHORIZATION_ITEM_GROUP_NAME = "itemGroupNAme";
    private static final String AUTHORIZATION_GODOWN_ID = "godownId";
    private static final String AUTHORIZATION_GODOWN_NAME = "godownNAme";
    private static final String AUTHORIZATION_REMARKS = "remarks";


    //	------------------------Constructor call----------------------------------------
    public DatabaseSqlLiteHandlerVoucherAuthorization(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } 
    // -----------------------------------------------Creating Tables---------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// Category table create query
    	String CREATE_TABLE_AUTHORIZATION = "CREATE TABLE " + AUTHORIZATION_TABLE + "("+ AUTHORIZATION_KEY_ID + " INTEGER PRIMARY KEY," + AUTHORIZATION_DOCUMENT_ID + " TEXT," + AUTHORIZATION_DOCUMENT_DATE + " TEXT," + AUTHORIZATION_BRANCH + " TEXT," + AUTHORIZATION_AMENDMEND_REVISION_NO + " INTEGER,"+ AUTHORIZATION_DOCUMENT_NO + " TEXT,"+ AUTHORIZATION_PARTY_NAME + " TEXT,"  + AUTHORIZATION_NARRATION + " TEXT,"+ AUTHORIZATION_REMARK + " TEXT,"+ AUTHORIZATION_STATUS + " TEXT,"+ AUTHORIZATION_REQUESTED_BY + " TEXT,"+ AUTHORIZATION_AMOUNT + " INTEGER,"+ AUTHORIZATION_VOUCHER_TYPE + " INTEGER,"+ AUTHORIZATION_VOUCHER_HEADING+ " TEXT,"+ AUTHORIZATION_ID+ " TEXT,"+ AUTHORIZATION_LEVEL+ " INTEGER,"+ AUTHORIZATION_REQUESTED_USER_NAME+ " TEXT,"+ AUTHORIZATION_REQUESTED_USER_ID+ " TEXT,"+ AUTHORIZATION_BRANCH_ID+ " TEXT," +AUTHORIZATION_DIVISION_ID +" TEXT,"+ AUTHORIZATION_BRAND_ID+ " TEXT,"+ AUTHORIZATION_DEPARTMENT_ID+ " TEXT,"+ AUTHORIZATION_PROJECT_ID+ " TEXT,"+ AUTHORIZATION_CLASSIFICATION_CODE_ID+ " TEXT,"+ AUTHORIZATION_PARTY_ID+ " TEXT,"+ AUTHORIZATION_APPROVAL_DATE+ " TEXT,"+ AUTHORIZATION_APPROVAL_TIME+ " TEXT," + AUTHORIZATION_NO_OF_LEVELS + " INTEGER," + AUTHORIZATION_ITEM_GROUP_ID + " TEXT," + AUTHORIZATION_ITEM_GROUP_NAME + " TEXT," + AUTHORIZATION_GODOWN_ID + " TEXT," + AUTHORIZATION_GODOWN_NAME + " TEXT," + AUTHORIZATION_REMARKS + " TEXT) ";
    	db.execSQL(CREATE_TABLE_AUTHORIZATION);
    }
 //------------------------------------ Upgrading database-------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
      	db.execSQL("DROP TABLE IF EXISTS " +AUTHORIZATION_TABLE);
      	 // Create tables again
    	onCreate(db);
    }
    public void deleteAuthoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AUTHORIZATION_TABLE,null,null);
        db.close();
    }
  //---------------------------------- Inserting Data of Item Details Table---------------------------------------
    public void insertAuthorizationTable(List<Map<String, String>> list) {
        final long startTime = System.currentTimeMillis();
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, AUTHORIZATION_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int ID = ih.getColumnIndex(AUTHORIZATION_ID);
        final int DocumentID = ih.getColumnIndex(AUTHORIZATION_DOCUMENT_ID);
        final int DocumentDate = ih.getColumnIndex(AUTHORIZATION_DOCUMENT_DATE);
        final int DocumentNumber = ih.getColumnIndex(AUTHORIZATION_DOCUMENT_NO);
        final int Amount = ih.getColumnIndex(AUTHORIZATION_AMOUNT);
        final int AuthLevel = ih.getColumnIndex(AUTHORIZATION_LEVEL);
        final int BranchName = ih.getColumnIndex(AUTHORIZATION_BRANCH);
        final int Status = ih.getColumnIndex(AUTHORIZATION_STATUS);
        final int AuthRemark = ih.getColumnIndex(AUTHORIZATION_REMARK);
        final int RequestedUserName = ih.getColumnIndex(AUTHORIZATION_REQUESTED_USER_NAME);
        final int RequestedUserID = ih.getColumnIndex(AUTHORIZATION_REQUESTED_USER_ID);
        final int PartyName = ih.getColumnIndex(AUTHORIZATION_PARTY_NAME);
        final int VType = ih.getColumnIndex(AUTHORIZATION_VOUCHER_TYPE);
        final int DocumentName = ih.getColumnIndex(AUTHORIZATION_VOUCHER_HEADING);
        final int BranchID = ih.getColumnIndex(AUTHORIZATION_BRANCH_ID);
        final int DivisionID = ih.getColumnIndex(AUTHORIZATION_DIVISION_ID);
        final int BrandID = ih.getColumnIndex(AUTHORIZATION_BRAND_ID);
        final int DepartmentID = ih.getColumnIndex(AUTHORIZATION_DEPARTMENT_ID);
        final int ProjectID = ih.getColumnIndex(AUTHORIZATION_PROJECT_ID);
        final int ClassificationCodeID = ih.getColumnIndex(AUTHORIZATION_CLASSIFICATION_CODE_ID);
        final int PartyID = ih.getColumnIndex(AUTHORIZATION_PARTY_ID);
        final int ApprovalDate = ih.getColumnIndex(AUTHORIZATION_APPROVAL_DATE);
        final int ApprovalTime = ih.getColumnIndex(AUTHORIZATION_APPROVAL_TIME);
        final int NoOfLevels = ih.getColumnIndex(AUTHORIZATION_NO_OF_LEVELS);
        final int Narration = ih.getColumnIndex(AUTHORIZATION_NARRATION);
        final int ItemGroupID = ih.getColumnIndex(AUTHORIZATION_ITEM_GROUP_ID);
        final int ItemGroupName = ih.getColumnIndex(AUTHORIZATION_ITEM_GROUP_NAME);
        final int GodownID = ih.getColumnIndex(AUTHORIZATION_GODOWN_ID);
        final int GodownName = ih.getColumnIndex(AUTHORIZATION_GODOWN_NAME);
        final int AmendmendRevisionNo = ih.getColumnIndex(AUTHORIZATION_AMENDMEND_REVISION_NO);
        final int Remarks = ih.getColumnIndex(AUTHORIZATION_REMARKS);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for (int x = 0; x < list.size(); x++) {
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(ID, list.get(x).get("ID"));
                ih.bind(DocumentID, list.get(x).get("DocumentID"));
                ih.bind(DocumentDate, list.get(x).get("DocumentDate"));
                ih.bind(DocumentNumber, list.get(x).get("DocumentNumber"));
                ih.bind(Amount, list.get(x).get("Amount"));
                ih.bind(AuthLevel, list.get(x).get("AuthLevel"));
                ih.bind(BranchName, list.get(x).get("BranchName"));
                ih.bind(Status, list.get(x).get("Status"));
                ih.bind(AuthRemark, list.get(x).get("AuthRemark"));
                ih.bind(RequestedUserName, list.get(x).get("RequestedUserName"));
                ih.bind(RequestedUserID, list.get(x).get("RequestedUserID"));
                ih.bind(PartyName, list.get(x).get("PartyName"));
                ih.bind(VType, list.get(x).get("VType"));
                ih.bind(DocumentName, list.get(x).get("DocumentName"));
                ih.bind(BranchID, list.get(x).get("BranchID"));
                ih.bind(DivisionID, list.get(x).get("DivisionID"));
                ih.bind(BrandID, list.get(x).get("BrandID"));
                ih.bind(DepartmentID, list.get(x).get("DepartmentID"));
                ih.bind(ProjectID, list.get(x).get("ProjectID"));
                ih.bind(ClassificationCodeID, list.get(x).get("ClassificationCodeID"));
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(ApprovalDate, list.get(x).get("ApprovalDate"));
                ih.bind(ApprovalTime, list.get(x).get("ApprovalTime"));
                ih.bind(NoOfLevels, list.get(x).get("NoOfLevels"));
                ih.bind(Narration, list.get(x).get("Narration"));
                ih.bind(AmendmendRevisionNo, list.get(x).get("AmendmendRevisionNo"));
                ih.bind(GodownName, list.get(x).get("GodownName"));
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(ItemGroupID, list.get(x).get("ItemGroupID"));
                ih.bind(ItemGroupName, list.get(x).get("ItemGroupName"));
                ih.bind(Remarks, "");
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
  //---------------------------------- Getting Data of report GroupName List ---------------------------------------
    public List<VoucherAuthorizeListDataset> getVoucherHeadingList(){
    	List<VoucherAuthorizeListDataset> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+AUTHORIZATION_VOUCHER_TYPE+" ,"+AUTHORIZATION_VOUCHER_HEADING+" from " + AUTHORIZATION_TABLE + " Order By "+AUTHORIZATION_VOUCHER_TYPE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
            	dataList.add(new VoucherAuthorizeListDataset(cursor.getString(cursor.getColumnIndex(AUTHORIZATION_VOUCHER_HEADING)),cursor.getString(cursor.getColumnIndex(AUTHORIZATION_VOUCHER_TYPE))));
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }
  //---------------------------------- Getting Data of report GroupName List ---------------------------------------
    public List<VTypeDetailsDataset> getVTypeDetails(int VType){
    	List<VTypeDetailsDataset> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+AUTHORIZATION_DOCUMENT_ID+" ,"+AUTHORIZATION_DOCUMENT_DATE+","+AUTHORIZATION_BRANCH+","+AUTHORIZATION_AMENDMEND_REVISION_NO+","+AUTHORIZATION_DOCUMENT_NO+","+AUTHORIZATION_PARTY_NAME+","+AUTHORIZATION_NARRATION+","+AUTHORIZATION_REMARK+","+AUTHORIZATION_STATUS+","+AUTHORIZATION_REQUESTED_USER_NAME+","+AUTHORIZATION_AMOUNT+","+AUTHORIZATION_VOUCHER_TYPE+","+AUTHORIZATION_REMARKS+" from " + AUTHORIZATION_TABLE +" WHERE "+AUTHORIZATION_VOUCHER_TYPE+"='"+VType+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        
        //map.put("GroupName", "All Group");
        if(cursor.getCount()>0)
        {
        cursor.moveToFirst();
            do {
                dataList.add(new VTypeDetailsDataset(
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_DOCUMENT_ID)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_DOCUMENT_DATE)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_BRANCH)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_AMENDMEND_REVISION_NO)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_DOCUMENT_NO)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_PARTY_NAME)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_NARRATION)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_REMARK)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_REQUESTED_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndex(AUTHORIZATION_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(AUTHORIZATION_VOUCHER_TYPE)),
                        cursor.getString(cursor.getColumnIndex(AUTHORIZATION_REMARKS))
                        ));
//            	Map<String,String> map=new HashMap<String,String>();
//            	map.put("DocID", cursor.getString(cursor.getColumnIndex(AUTHORIZATION_DOCUMENT_ID)));
//            	map.put("DocDate", cursor.getString(cursor.getColumnIndex(AUTHORIZATION_DOCUMENT_DATE)));
//            	map.put("Branch", cursor.getString(cursor.getColumnIndex(AUTHORIZATION_BRANCH)));
//            	map.put("AmendNo", cursor.getString(cursor.getColumnIndex(AUTHORIZATION_AMENDMEND_REVISION_NO)));
//            	map.put("DocNo", );
//            	map.put("PartyName", );
//            	map.put("Narration", );
//            	map.put("Remark", );
//            	map.put("Status", );
//            	map.put("Requested", );
//            	map.put("Amount", );
//            	map.put("VType", );
//            	dataList.add(map);
               } while (cursor.moveToNext());
        // closing connection
        cursor.close();
    	}
        //System.out.println("Count Total:"+i);
    	// returning 
    	return dataList;
    }


    public void UpdateStatusRemarks(int Status,String Remarks,String DocID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(AUTHORIZATION_STATUS, Status);
        values.put(AUTHORIZATION_REMARKS, Remarks);
        db.update(AUTHORIZATION_TABLE, values, AUTHORIZATION_DOCUMENT_ID+"= '"+DocID+"'", null);
        db.close();
    }
}
