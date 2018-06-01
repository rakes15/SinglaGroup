package DatabaseController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.BranchDataset;
import com.singlagroup.datasets.CompanyDataset;
import com.singlagroup.datasets.DivisionDataset;
import com.singlagroup.datasets.GodownDataset;
import com.singlagroup.datasets.ModulePermissionDetailsDataset;
import com.singlagroup.datasets.ShowroomDataset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseSqlLiteHandlerUserInfo {
    private static final String TAG = DatabaseSqlLiteHandlerUserInfo.class.getSimpleName();
    //TODo: Table's Name
    private static final String SINGLA_USER_INFO_TABLE = "userInfoTbl";
    private static final String SINGLA_CAPTION_TABLE = "captionTbl";
    private static final String SINGLA_BASIC_INFO_TABLE = "basicInfoTbl";
    //TODO: UserInfo  Table Column's Name
    private static final String SINGLA_USER_INFO_KEY_ID = "id";
    private static final String SINGLA_USER_INFO_ID = "uiD";
    private static final String SINGLA_USER_INFO_NAME = "name";
    private static final String SINGLA_USER_INFO_ACTIVE = "active";
    private static final String SINGLA_USER_INFO_MASTER_TYPE = "masterType";
    private static final String SINGLA_USER_INFO_UNDER_ID = "underID";
    private static final String SINGLA_USER_INFO_COMPANY_ID = "companyID";
    private static final String SINGLA_USER_INFO_COMPANY_NAME = "companyName";
    private static final String SINGLA_USER_INFO_DB_NAMNE = "dbName";
    private static final String SINGLA_USER_INFO_IS_DEFAULT = "isDefault";
    private static final String SINGLA_USER_INFO_DATA_TYPE_NAME = "dataTypeName";
    private static final String SINGLA_USER_INFO_DATA_TYPE = "dataType";
    //TODO:Master Type Details
        /* Division - 1020
           Branch - 1022
           Godown - 1023
        **/
    //TODO: Alter Table Columns
    private static final String SINGLA_DATETIME = "createDateTime";
    //TODO: Caption Table Column's Name
    private static final String SINGLA_CAPTION_KEY_ID = "id";
    private static final String SINGLA_CAPTION_ID = "captionID";
    private static final String SINGLA_CAPTION_NAME = "captionName";
    private static final String SINGLA_CAPTION_SEQUENCE = "capSeq";
    private static final String SINGLA_CAPTION_UNDER_ID = "underID";
    private static final String SINGLA_CAPTION_IS_MODULE = "isModule";
    private static final String SINGLA_CAPTION_VIEW_FLAG = "ViewFlag";
    private static final String SINGLA_CAPTION_EDIT_FLAG= "editFlag";
    private static final String SINGLA_CAPTION_CREATE_FLAG = "createFlag";
    private static final String SINGLA_CAPTION_REMOVE_FLAG = "removeFlag";
    private static final String SINGLA_CAPTION_PRINT_FLAG = "printFlag";
    private static final String SINGLA_CAPTION_IMPORT_FLAG = "importFlag";
    private static final String SINGLA_CAPTION_EXPORT_FLAG = "exportFlag";
    private static final String SINGLA_CAPTION_CONTENT_CLASS = "contentClass";
    private static final String SINGLA_CAPTION_MODULE_VTYPE = "moduleVType";
    //TODO: BasicInfo Table Column's Name
    private static final String SINGLA_BASIC_INFO_USER_ID = "userID";
    private static final String SINGLA_BASIC_INFO_USER_GROUP_ID = "userGroupID";
    private static final String SINGLA_BASIC_INFO_AUTO_LAUNCH_MODULE = "autoLaunchModule";
    private static final String SINGLA_BASIC_INFO_ID = "basicInfoID";
    private static final String SINGLA_BASIC_INFO_NAME = "name";
    private static final String SINGLA_BASIC_INFO_CODE = "code";
    private static final String SINGLA_BASIC_INFO_MASTER_TYPE = "masterType";
    private static final String SINGLA_BASIC_INFO_TYPE_NAME = "typeName";
    private static final String SINGLA_BASIC_INFO_CARD_NO = "cardNo";
    private static final String SINGLA_BASIC_INFO_ADDRESS_1= "address1";
    private static final String SINGLA_BASIC_INFO_ADDRESS_2= "address2";
    private static final String SINGLA_BASIC_INFO_ADDRESS_3= "address3";
    private static final String SINGLA_BASIC_INFO_CITY = "city";
    private static final String SINGLA_BASIC_INFO_STATE = "state";
    private static final String SINGLA_BASIC_INFO_COUNTRY = "country";
    private static final String SINGLA_BASIC_INFO_PIN = "pin";
    private static final String SINGLA_BASIC_INFO_MOBILE = "mobile";
    private static final String SINGLA_BASIC_INFO_IMAGE = "image";
    private static final String SINGLA_BASIC_INFO_USER_FULL_NAME = "userFullName";

    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerUserInfo(Context context) {
        this.context = context;
    }
    //TODO: UserInfo Table Delete
    public void UserInfoTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_USER_INFO_TABLE, null, null);
        db.close();
    }
    //TODO: Caption Table Delete
    public void CaptionTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_CAPTION_TABLE, null, null);
        db.close();
    }
    //TODO: BasicInfo Table Delete
    public void BasicInfoTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_BASIC_INFO_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of UserInfo Table
    public void insertUserInfoTable(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_USER_INFO_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int ID = ih.getColumnIndex(SINGLA_USER_INFO_ID);
        final int Name = ih.getColumnIndex(SINGLA_USER_INFO_NAME);
        final int Active = ih.getColumnIndex(SINGLA_USER_INFO_ACTIVE);
        final int MasterType = ih.getColumnIndex(SINGLA_USER_INFO_MASTER_TYPE);
        final int UnderID = ih.getColumnIndex(SINGLA_USER_INFO_UNDER_ID);
        final int CompanyID = ih.getColumnIndex(SINGLA_USER_INFO_COMPANY_ID);
        final int CompanyName = ih.getColumnIndex(SINGLA_USER_INFO_COMPANY_NAME);
        final int DBName = ih.getColumnIndex(SINGLA_USER_INFO_DB_NAMNE);
        final int IsDefault = ih.getColumnIndex(SINGLA_USER_INFO_IS_DEFAULT);
        final int DataTypeName = ih.getColumnIndex(SINGLA_USER_INFO_DATA_TYPE_NAME);
        final int DataType = ih.getColumnIndex(SINGLA_USER_INFO_DATA_TYPE);
        final int CreateDateTime = ih.getColumnIndex(SINGLA_DATETIME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(ID, list.get(x).get("ID"));
                ih.bind(Name, list.get(x).get("Name"));
                ih.bind(Active, list.get(x).get("Active"));
                ih.bind(MasterType, list.get(x).get("Type"));
                ih.bind(UnderID, list.get(x).get("UnderID"));
                ih.bind(CompanyID, list.get(x).get("CompanyID"));
                ih.bind(CompanyName, list.get(x).get("CompanyName"));
                ih.bind(DBName, list.get(x).get("DBName"));
                ih.bind(IsDefault, list.get(x).get("IsDefault"));
                ih.bind(DataTypeName, list.get(x).get("DataTypeName"));
                ih.bind(DataType, list.get(x).get("DataType"));
                ih.bind(CreateDateTime, getDateTime());
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
    //TODO: Inserting Data of Caption Table
    public void insertCaptionTable(List<Map<String,String>> list) {
        Log.e("TAG",list.toString());
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_CAPTION_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int CaptionID = ih.getColumnIndex(SINGLA_CAPTION_ID);
        final int Caption = ih.getColumnIndex(SINGLA_CAPTION_NAME);
        final int Sequence = ih.getColumnIndex(SINGLA_CAPTION_SEQUENCE);
        final int UnderID = ih.getColumnIndex(SINGLA_CAPTION_UNDER_ID);
        final int IsModule = ih.getColumnIndex(SINGLA_CAPTION_IS_MODULE);
        final int ViewFlag = ih.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG);
        final int EditFlag = ih.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG);
        final int CreateFlag = ih.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG);
        final int RemoveFlag = ih.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG);
        final int PrintFlag = ih.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG);
        final int ImportFlag = ih.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG);
        final int ExportFlag = ih.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG);
        final int ContentClass = ih.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS);
        final int ModuleVType = ih.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE);
        final int CreateDateTime = ih.getColumnIndex(SINGLA_DATETIME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(CaptionID, list.get(x).get("CaptionID"));
                ih.bind(Caption, list.get(x).get("Caption"));
                ih.bind(Sequence, list.get(x).get("Sequence"));
                ih.bind(UnderID, list.get(x).get("UnderID"));
                ih.bind(IsModule, list.get(x).get("IsModule"));
                ih.bind(ViewFlag, list.get(x).get("ViewFlag"));
                ih.bind(EditFlag, list.get(x).get("EditFlag"));
                ih.bind(CreateFlag, list.get(x).get("CreateFlag"));
                ih.bind(RemoveFlag, list.get(x).get("RemoveFlag"));
                ih.bind(PrintFlag, list.get(x).get("PrintFlag"));
                ih.bind(ImportFlag, list.get(x).get("ImportFlag"));
                ih.bind(ExportFlag, list.get(x).get("ExportFlag"));
                ih.bind(ContentClass, list.get(x).get("ContentClass"));
                ih.bind(ModuleVType, list.get(x).get("VType"));
                ih.bind(CreateDateTime, getDateTime());
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
    //TODO: Inserting Data of BasicInfo Table
    public void insertBasicInfoTable(Map<String,String> list) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_BASIC_INFO_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int UserID = ih.getColumnIndex(SINGLA_BASIC_INFO_USER_ID);
        final int UserGroupID = ih.getColumnIndex(SINGLA_BASIC_INFO_USER_GROUP_ID);
        final int AutoLaunchModule = ih.getColumnIndex(SINGLA_BASIC_INFO_AUTO_LAUNCH_MODULE);
        final int ID = ih.getColumnIndex(SINGLA_BASIC_INFO_ID);
        final int Name = ih.getColumnIndex(SINGLA_BASIC_INFO_NAME);
        final int Code = ih.getColumnIndex(SINGLA_BASIC_INFO_CODE);
        final int MasterType = ih.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE);
        final int TypeName = ih.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME);
        final int CardNo = ih.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO);
        final int Address1 = ih.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1);
        final int Address2 = ih.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2);
        final int Address3 = ih.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3);
        final int City = ih.getColumnIndex(SINGLA_BASIC_INFO_CITY);
        final int State = ih.getColumnIndex(SINGLA_BASIC_INFO_STATE);
        final int Country = ih.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY);
        final int PIN = ih.getColumnIndex(SINGLA_BASIC_INFO_PIN);
        final int CellNo = ih.getColumnIndex(SINGLA_BASIC_INFO_MOBILE);
        final int Image = ih.getColumnIndex(SINGLA_BASIC_INFO_IMAGE);
        final int UserFullName = ih.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME);
        final int CreateDateTime = ih.getColumnIndex(SINGLA_DATETIME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(UserID, list.get("UserID"));
                ih.bind(UserGroupID, list.get("UserGroupID"));
                ih.bind(AutoLaunchModule, list.get("AutoLaunchModule"));
                ih.bind(ID, list.get("ID"));
                ih.bind(Name, list.get("Name"));
                ih.bind(Code, list.get("Code"));
                ih.bind(MasterType, list.get("MasterType"));
                ih.bind(TypeName, list.get("TypeName"));
                ih.bind(CardNo, list.get("CardNo"));
                ih.bind(Address1, list.get("Address1"));
                ih.bind(Address2, list.get("Address2"));
                ih.bind(Address3, list.get("Address3"));
                ih.bind(City, list.get("City"));
                ih.bind(State, list.get("State"));
                ih.bind(Country, list.get("Country"));
                ih.bind(PIN, list.get("PIN"));
                ih.bind(CellNo, list.get("CellNo"));
                ih.bind(Image, list.get("Image"));
                ih.bind(UserFullName, list.get("UserFullName"));
                ih.bind(CreateDateTime, getDateTime());
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
    //TODO: UserInfo Table Data Gets
    public List<Map<String,String>> getUserInfoCompanyList(){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_COMPANY_ID+", "+SINGLA_USER_INFO_COMPANY_NAME+","+SINGLA_USER_INFO_DB_NAMNE+","+SINGLA_USER_INFO_IS_DEFAULT+","+SINGLA_DATETIME+" from " + SINGLA_USER_INFO_TABLE + " ORDER BY "+SINGLA_USER_INFO_IS_DEFAULT+" DESC" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String>  map = new HashMap<String,String>();
                map.put("CompanyID",cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_COMPANY_ID)));
                map.put("CompanyName",cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_COMPANY_NAME)));
                map.put("DBName",cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_DB_NAMNE)));
                map.put("IsDefault",cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_IS_DEFAULT)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        System.out.println("CompanyList:"+mapList.toString());
        // returning
        return mapList;
    }
    public List<DivisionDataset> getUserInfoDivision(String CompanyID){
        List<DivisionDataset> divisionDatasetList=new ArrayList<DivisionDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_ID+", "+SINGLA_USER_INFO_NAME+","+SINGLA_USER_INFO_ACTIVE+","+SINGLA_USER_INFO_UNDER_ID+","+SINGLA_DATETIME+" from " + SINGLA_USER_INFO_TABLE + " Where "+SINGLA_USER_INFO_MASTER_TYPE+"=1020 AND "+SINGLA_USER_INFO_COMPANY_ID+"='"+CompanyID+"' ORDER BY "+SINGLA_USER_INFO_ACTIVE+" DESC" ;
        System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                DivisionDataset  divisionDataset = new DivisionDataset (cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)),cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_DATETIME)));
                divisionDatasetList.add(divisionDataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return divisionDatasetList;
    }
    public List<BranchDataset> getUserInfoBranch(String CompanyID,String UnderID){
        List<BranchDataset> branchDatasetList=new ArrayList<BranchDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_ID+", "+SINGLA_USER_INFO_NAME+","+SINGLA_USER_INFO_ACTIVE+","+SINGLA_USER_INFO_MASTER_TYPE+","+SINGLA_USER_INFO_UNDER_ID+" from " + SINGLA_USER_INFO_TABLE + " Where "+SINGLA_USER_INFO_UNDER_ID+"= '"+UnderID+"' AND "+SINGLA_USER_INFO_MASTER_TYPE+"=1022 AND "+SINGLA_USER_INFO_COMPANY_ID+"='"+CompanyID+"' ORDER BY "+SINGLA_USER_INFO_ACTIVE+" DESC" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                BranchDataset  branchDataset = new BranchDataset (cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)),cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)));
                branchDatasetList.add(branchDataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //Log.d(TAG,"Bracnch:"+branchDatasetList.toString());
        // returning
        return branchDatasetList;
    }
    public List<GodownDataset> getUserInfoGodown(String CompanyID,String UnderID){
        List<GodownDataset> godownDatasetList=new ArrayList<GodownDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_ID+", "+SINGLA_USER_INFO_NAME+","+SINGLA_USER_INFO_ACTIVE+","+SINGLA_USER_INFO_UNDER_ID+" from " + SINGLA_USER_INFO_TABLE + " Where "+SINGLA_USER_INFO_UNDER_ID+"= '"+UnderID+"' AND "+SINGLA_USER_INFO_MASTER_TYPE+"=1023 AND "+SINGLA_USER_INFO_COMPANY_ID+"='"+CompanyID+"' ORDER BY "+SINGLA_USER_INFO_ACTIVE+" DESC" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                GodownDataset  godownDataset = new GodownDataset (cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)),cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)),cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)));
                godownDatasetList.add(godownDataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //Log.d(TAG,"Godown:"+godownDatasetList.toString());
        // returning
        return godownDatasetList;
    }
    public List<GodownDataset> getReserveGodownList(String CompanyID,String DivisionID,String BranchID){
        List<GodownDataset> godownList=new ArrayList<GodownDataset>();
        int c=0;
        GodownDataset dataset = new GodownDataset("","Select showroom",1,"");
        godownList.add(c,dataset);
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + SINGLA_USER_INFO_ID + "," + SINGLA_USER_INFO_NAME + "," + SINGLA_USER_INFO_ACTIVE + "," + SINGLA_USER_INFO_UNDER_ID + " from " + SINGLA_USER_INFO_TABLE + " Where " + SINGLA_USER_INFO_MASTER_TYPE + "=1023 AND " + SINGLA_USER_INFO_DATA_TYPE + "='10' AND " + SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' AND " + SINGLA_USER_INFO_UNDER_ID + "='" + BranchID + "' ORDER BY " + SINGLA_USER_INFO_ACTIVE + " DESC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                c++;
                dataset = new GodownDataset(cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)), cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)));
                godownList.add(c, dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return godownList;
    }
    public List<ShowroomDataset> getShowroomList(String CompanyID, String DivisionID, String BranchID){
        List<ShowroomDataset> showroomList=new ArrayList<ShowroomDataset>();
        int c=0;
        ShowroomDataset dataset = new ShowroomDataset("","Select showroom",1,"");
        showroomList.add(c,dataset);
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + SINGLA_USER_INFO_ID + "," + SINGLA_USER_INFO_NAME + "," + SINGLA_USER_INFO_ACTIVE + "," + SINGLA_USER_INFO_UNDER_ID + " from " + SINGLA_USER_INFO_TABLE + " Where " + SINGLA_USER_INFO_MASTER_TYPE + "=1023 AND " + SINGLA_USER_INFO_DATA_TYPE + "='10' AND " + SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' AND " + SINGLA_USER_INFO_UNDER_ID + "='" + BranchID + "' ORDER BY " + SINGLA_USER_INFO_ACTIVE + " DESC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                c++;
                dataset = new ShowroomDataset(cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)), cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)));
                showroomList.add(c, dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return showroomList;
    }
    public List<GodownDataset> getGodownList(String CompanyID,String DivisionID,String BranchID){
        List<GodownDataset> godownList=new ArrayList<GodownDataset>();
        int c=0;
        GodownDataset dataset = new GodownDataset("","Select Godown",1,"");
        godownList.add(c,dataset);
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + SINGLA_USER_INFO_ID + "," + SINGLA_USER_INFO_NAME + "," + SINGLA_USER_INFO_ACTIVE + "," + SINGLA_USER_INFO_UNDER_ID + " from " + SINGLA_USER_INFO_TABLE + " Where " + SINGLA_USER_INFO_MASTER_TYPE + "=1023 AND " + SINGLA_USER_INFO_DATA_TYPE + "!='10' AND " + SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' AND " + SINGLA_USER_INFO_UNDER_ID + "='" + BranchID + "' ORDER BY " + SINGLA_USER_INFO_ACTIVE + " DESC";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                c++;
                dataset = new GodownDataset(cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME)), cursor.getInt(cursor.getColumnIndex(SINGLA_USER_INFO_ACTIVE)), cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_UNDER_ID)));
                godownList.add(c, dataset);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return godownList;
    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public void UpdateCompany(String CompanyID,int IsDefault){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLA_USER_INFO_IS_DEFAULT, 0);
        db.update(SINGLA_USER_INFO_TABLE, values, null, null);
        values = new ContentValues();
        values.put(SINGLA_USER_INFO_IS_DEFAULT, IsDefault);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "'", null);
        db.close();
    }
    public void UpdateDivision(String CompanyID,String DivisionID,int Active){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, 0);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_MASTER_TYPE + "=1020", null);
        values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, Active);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' And "+SINGLA_USER_INFO_ID + "='" + DivisionID + "' AND " + SINGLA_USER_INFO_MASTER_TYPE + "=1020", null);
        db.close();
    }
    public void UpdateBranch(String CompanyID,String BranchID,String UnderID,int Active){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, 0);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' And "+SINGLA_USER_INFO_MASTER_TYPE + "=1022 AND "+SINGLA_USER_INFO_UNDER_ID+"='"+UnderID+"'", null);
        values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, Active);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' And "+SINGLA_USER_INFO_ID + "='" + BranchID + "' AND " + SINGLA_USER_INFO_MASTER_TYPE + "=1022 AND "+SINGLA_USER_INFO_UNDER_ID+"='"+UnderID+"'", null);
        db.close();
    }
    public void UpdateGodown(String CompanyID,String GodownID,String UnderID,int Active){
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, 0);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' And "+SINGLA_USER_INFO_MASTER_TYPE + "=1023 AND "+SINGLA_USER_INFO_UNDER_ID+"='"+UnderID+"'", null);
        values = new ContentValues();
        values.put(SINGLA_USER_INFO_ACTIVE, Active);
        db.update(SINGLA_USER_INFO_TABLE, values, SINGLA_USER_INFO_COMPANY_ID + "='" + CompanyID + "' And "+SINGLA_USER_INFO_ID + "='" + GodownID + "' AND " + SINGLA_USER_INFO_MASTER_TYPE + "=1023 AND "+SINGLA_USER_INFO_UNDER_ID+"='"+UnderID+"'", null);
        db.close();
    }
    public String[] getDefaultCompany(){
        String[] Company=new String[2];
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_COMPANY_ID+", "+SINGLA_USER_INFO_COMPANY_NAME+" from " + SINGLA_USER_INFO_TABLE + " ORDER BY "+SINGLA_USER_INFO_IS_DEFAULT+" DESC LIMIT 1" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Company[0] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_COMPANY_ID));
                Company[1] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_COMPANY_NAME));
                //System.out.println(""+cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_COMPANY_NAME)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return Company;
    }
    public String[] getDefaultDivision(String CompanyID){
        String[] Division=new String[2];
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_ID+", "+SINGLA_USER_INFO_NAME+","+SINGLA_USER_INFO_ACTIVE+","+SINGLA_USER_INFO_UNDER_ID+","+SINGLA_DATETIME+" from " + SINGLA_USER_INFO_TABLE + " Where "+SINGLA_USER_INFO_MASTER_TYPE+"=1020 AND "+SINGLA_USER_INFO_COMPANY_ID+"='"+CompanyID+"' ORDER BY "+SINGLA_USER_INFO_ACTIVE+" DESC LIMIT 1" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Division[0] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID));
                Division[1] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return Division;
    }
    public String[] getDefaultBranch(String CompanyID,String UnderID){
        String[] Branch=new String[2];
        // Select All Query
        String selectQuery = "SELECT DISTINCT "+SINGLA_USER_INFO_ID+", "+SINGLA_USER_INFO_NAME+","+SINGLA_USER_INFO_ACTIVE+","+SINGLA_USER_INFO_MASTER_TYPE+","+SINGLA_USER_INFO_UNDER_ID+" from " + SINGLA_USER_INFO_TABLE + " Where "+SINGLA_USER_INFO_UNDER_ID+"= '"+UnderID+"' AND "+SINGLA_USER_INFO_MASTER_TYPE+"=1022 AND "+SINGLA_USER_INFO_COMPANY_ID+"='"+CompanyID+"' ORDER BY "+SINGLA_USER_INFO_ACTIVE+" DESC LIMIT 1" ;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Branch[0] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_ID));
                Branch[1] = cursor.getString(cursor.getColumnIndex(SINGLA_USER_INFO_NAME));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return Branch;
    }
    //TODO: Caption Table Data Gets
    public List<Map<String,String>> getParentCaption(){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_IMPORT_FLAG+","+SINGLA_CAPTION_EXPORT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE + " where "+SINGLA_CAPTION_UNDER_ID+" IS NULL AND "+SINGLA_CAPTION_IS_MODULE+"=1 ORDER BY "+SINGLA_CAPTION_SEQUENCE;
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return mapList;
    }
    public List<Map<String,String>> getParentCaptionWithBriefcase(){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_IMPORT_FLAG+","+SINGLA_CAPTION_EXPORT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE + " where "+SINGLA_CAPTION_UNDER_ID+" IS NULL ORDER BY "+SINGLA_CAPTION_SEQUENCE;
        //String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        int c=0;
        Map<String,String> map=new HashMap<String,String>();
        map.put("ID","Briefcase");
        map.put("Name","Briefcase");
        map.put("Sequence","1");
        map.put("UnderID","null");
        map.put("IsModule","0");
        map.put("ViewFlag","0");
        map.put("EditFlag","0");
        map.put("CreateFlag","0");
        map.put("RemoveFlag","0");
        map.put("PrintFlag","0");
        map.put("ImportFlag","0");
        map.put("ExportFlag","0");
        map.put("ContentClass","");
        map.put("Vtype","-1");
        mapList.add(c,map);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                c++;
                map=new HashMap<String,String>();
                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
                mapList.add(c,map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return mapList;
    }
    public List<Map<String,String>> getChildCaption(String UnderID){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_IMPORT_FLAG+","+SINGLA_CAPTION_EXPORT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE + " Where "+SINGLA_CAPTION_UNDER_ID+"='"+UnderID+"' ORDER BY "+SINGLA_CAPTION_SEQUENCE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);         SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return mapList;
    }
    //TODO: Module list Data Gets
    public List<Map<String,String>> getModuleList(){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_IMPORT_FLAG+","+SINGLA_CAPTION_EXPORT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE + " where "+SINGLA_CAPTION_IS_MODULE+"=1 AND "+SINGLA_CAPTION_MODULE_VTYPE+" IS NOT NULL  ORDER BY "+SINGLA_CAPTION_SEQUENCE;
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return mapList;
    }
    //TODO: Module list Data Gets
    public List<Map<String,String>> getModuleListByBriefcase(int equalFlag){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        String Brief = CommanStatic.Briefcase.replace("|","@");
        String[] briefcase = Brief.split("@");
        if (briefcase.length > 0) {
            Brief = "";
            for (int b = 0; b < briefcase.length; b++) {
                if (!briefcase[b].isEmpty())
                    Brief += briefcase[b]+",";
            }
            if (equalFlag == 1)
                Brief = Brief.length() > 1 ? " AND "+SINGLA_CAPTION_MODULE_VTYPE+" IN ("+Brief.substring(0,Brief.length()-1)+") " : "";
            else
                Brief = Brief.length() > 1 ? " AND "+SINGLA_CAPTION_MODULE_VTYPE+" NOT IN ("+Brief.substring(0,Brief.length()-1)+") " : "";
        }
        Log.i(TAG,"Briefcase:"+Brief);
        // Select All Query
        String selectQuery = "";
        if (equalFlag == 1) {
            selectQuery = "SELECT Distinct " + SINGLA_CAPTION_ID + ", " + SINGLA_CAPTION_NAME + "," + SINGLA_CAPTION_SEQUENCE + "," + SINGLA_CAPTION_UNDER_ID + "," + SINGLA_CAPTION_IS_MODULE + "," + SINGLA_CAPTION_VIEW_FLAG + "," + SINGLA_CAPTION_EDIT_FLAG + "," + SINGLA_CAPTION_CREATE_FLAG + "," + SINGLA_CAPTION_REMOVE_FLAG + "," + SINGLA_CAPTION_PRINT_FLAG + "," + SINGLA_CAPTION_IMPORT_FLAG + "," + SINGLA_CAPTION_EXPORT_FLAG + "," + SINGLA_CAPTION_CONTENT_CLASS + "," + SINGLA_CAPTION_MODULE_VTYPE + " from " + SINGLA_CAPTION_TABLE + " where " + SINGLA_CAPTION_IS_MODULE + "=1 AND " + SINGLA_CAPTION_MODULE_VTYPE + " IS NOT NULL "+Brief+" ORDER BY " + SINGLA_CAPTION_SEQUENCE;
        }else {
            selectQuery = "SELECT Distinct " + SINGLA_CAPTION_ID + ", " + SINGLA_CAPTION_NAME + "," + SINGLA_CAPTION_SEQUENCE + "," + SINGLA_CAPTION_UNDER_ID + "," + SINGLA_CAPTION_IS_MODULE + "," + SINGLA_CAPTION_VIEW_FLAG + "," + SINGLA_CAPTION_EDIT_FLAG + "," + SINGLA_CAPTION_CREATE_FLAG + "," + SINGLA_CAPTION_REMOVE_FLAG + "," + SINGLA_CAPTION_PRINT_FLAG + "," + SINGLA_CAPTION_IMPORT_FLAG + "," + SINGLA_CAPTION_EXPORT_FLAG + "," + SINGLA_CAPTION_CONTENT_CLASS + "," + SINGLA_CAPTION_MODULE_VTYPE + " from " + SINGLA_CAPTION_TABLE + " where " + SINGLA_CAPTION_IS_MODULE + "=1 AND " + SINGLA_CAPTION_MODULE_VTYPE + " IS NOT NULL "+Brief+" ORDER BY " + SINGLA_CAPTION_SEQUENCE;
        }
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                Map<String,String> map=new HashMap<String,String>();
                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
                mapList.add(map);
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        //Log.i(TAG, "Briefcase:"+mapList.toString());
        // returning
        return mapList;
    }
    //TODO: Module list Data Gets
    public Map<String,String> getModulePermissionByVtype(int VType){
        Map<String,String> map=new HashMap<String,String>();
        // Select All Query
        String selectQuery = "SELECT Distinct "+SINGLA_CAPTION_ID+", "+SINGLA_CAPTION_NAME+","+SINGLA_CAPTION_SEQUENCE+","+SINGLA_CAPTION_UNDER_ID+","+SINGLA_CAPTION_IS_MODULE+","+SINGLA_CAPTION_VIEW_FLAG+","+SINGLA_CAPTION_EDIT_FLAG+","+SINGLA_CAPTION_CREATE_FLAG+","+SINGLA_CAPTION_REMOVE_FLAG+","+SINGLA_CAPTION_PRINT_FLAG+","+SINGLA_CAPTION_IMPORT_FLAG+","+SINGLA_CAPTION_EXPORT_FLAG+","+SINGLA_CAPTION_CONTENT_CLASS+","+SINGLA_CAPTION_MODULE_VTYPE+" from " + SINGLA_CAPTION_TABLE + " where "+SINGLA_CAPTION_IS_MODULE+"=1 AND "+SINGLA_CAPTION_MODULE_VTYPE+"="+VType+" ";
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {

                map.put("ID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_ID)));
                map.put("Name",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_NAME)));
                map.put("Sequence",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_SEQUENCE)));
                map.put("UnderID",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_UNDER_ID)));
                map.put("IsModule",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)));
                map.put("ViewFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)));
                map.put("EditFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)));
                map.put("CreateFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)));
                map.put("RemoveFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)));
                map.put("PrintFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
                map.put("ImportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)));
                map.put("ExportFlag",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)));
                map.put("ContentClass",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_CONTENT_CLASS)));
                map.put("Vtype",cursor.getString(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return map;
    }
    //TODO: BasicInfo Table Data Gets
    public String[][] getBasicInfo() {
        String[][] str=new String[20][20];
        // Select All Query
        String selectQuery = "SELECT * from " + SINGLA_BASIC_INFO_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                str[0][0] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ID)));
                str[0][1] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME)));
                str[0][2] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE)));
                str[0][3] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE)));
                str[0][4] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME)));
                str[0][5] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO)));
                str[0][6] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1)));
                str[0][7] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2)));
                str[0][8] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3)));
                str[0][9] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY)));
                str[0][10] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE)));
                str[0][11] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY)));
                str[0][12] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN)));
                str[0][13] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE)));
                str[0][14] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE)));
                str[0][15] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_ID)));
                str[0][16] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_GROUP_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_GROUP_ID)));
                str[0][17] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME)));
                str[0][18] = (CommanStatic.Screenshot == 0 ? "OFF" : "ON");
                str[0][19] = (CommanStatic.AutoLaunchModule == 0 ? "No Module" : CommanStatic.AutoLaunchModuleName);

                //TODO: Headers
                //str[0][0] = "ID";
                str[1][0] = "Name";
                str[2][0] = "Code";
                str[3][0] = "Master Type";
                str[4][0] = "Type Name";
                str[5][0] = "Card No";
                str[6][0] = "Address 1";
                str[7][0] = "Address 2";
                str[8][0] = "Address 3";
                str[9][0] = "City";
                str[10][0] = "State";
                str[11][0] = "Country";
                str[12][0] = "Pin";
                str[13][0] = "Mobile";
                str[14][0] = "";
                str[15][0] = "";
                str[16][0] = "";
                str[17][0] = "Full Name";
                str[18][0] = "Screenshot";
                str[19][0] = "Auto launch module";
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return str;
    }
    public String[][] getProfileInfo() {
        String[][] str=new String[18][18];
        // Select All Query
        String selectQuery = "SELECT * from " + SINGLA_BASIC_INFO_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                int MasterType = cursor.getInt(cursor.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE));
                str[0][0] = "";
                str[0][1] = CommanStatic.LogIN_UserName;
                str[0][2] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME)));
                str[0][3] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME)));
                str[0][4] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE)));
                str[0][5] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO)));
                str[0][6] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME)));
                str[0][7] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1)))+""+(cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2))==null?"":", "+cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2)))+""+(cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3))==null?"":", "+cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3)));
                str[0][8] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY)));
                str[0][9] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE)));
                str[0][10] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY)));
                str[0][11] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN)));
                str[0][12] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE)));

                str[0][13] = (CommanStatic.Screenshot == 0 ? "No" : "Yes");
                str[0][14] = (CommanStatic.InternetAccess == 0 ? "LAN" : "LAN & WAN");
                str[0][15] = (CommanStatic.MultiSession == 0 ? "No" : "Yes");
                str[0][16] = (CommanStatic.AccessLogin == 0 ? "Default active device only" : "Any active devices");
                str[0][17] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE)));


                //TODO: Headers
                str[0][0] = "";
                str[1][0] = "Login ID";
                str[2][0] = "User Name";
                str[3][0] = "User Type";
                str[4][0] = "Account Code";
                str[5][0] = (MasterType == 2048 ? "Employee Code " : "");
                str[6][0] = (MasterType == 2048 ? "Employee Name " : "Company Name");
                str[7][0] = "Address";
                str[8][0] = "City";
                str[9][0] = "State";
                str[10][0] = "Country";
                str[11][0] = "Pin";
                str[12][0] = "Mobile";

                str[13][0] = "Screenshot allow";
                str[14][0] = "Login allowed area";
                str[15][0] = "Multi Session";
                str[16][0] = "Login allowed on";
                str[17][0] = "";
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return str;
    }
    public String[] getSomeBasicInfo() {
        String[] str=new String[21];
        String selectQuery = "SELECT * from " + SINGLA_BASIC_INFO_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                str[0] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ID)));
                str[1] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME)));
                str[2] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CODE)));
                str[3] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MASTER_TYPE)));
                str[4] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_TYPE_NAME)));
                str[5] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO)));
                str[6] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_1)));
                str[7] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_2)));
                str[8] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_ADDRESS_3)));
                str[9] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CITY)));
                str[10] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_STATE)));
                str[11] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_COUNTRY)));
                str[12] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_PIN)));
                str[13] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_MOBILE)));
                str[14] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_IMAGE)));
                str[15] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_ID)));
                str[16] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_GROUP_ID))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_GROUP_ID)));
                str[17] = (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME))==null?"":cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_USER_FULL_NAME)));
                String[] strings = getDefaultCompany();
                str[18] = strings[1];
                String[] strings2 = getDefaultDivision(strings[0]);
                str[19] = strings2[1];
                String[] strings3 = getDefaultBranch(strings[0],strings2[0]);
                str[20] = strings3[1];
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        System.out.println("String:"+str);
        // returning
        return str;
    }
    public ModulePermissionDetailsDataset getModulePermissionDetails(String CaptionID){
        ModulePermissionDetailsDataset dataset=null;
        // Select All Query
        String selectQuery = "SELECT b."+SINGLA_BASIC_INFO_CARD_NO+", b."+SINGLA_BASIC_INFO_NAME+",c.* from " + SINGLA_CAPTION_TABLE + " c CROSS JOIN "+SINGLA_BASIC_INFO_TABLE+" b Where c."+SINGLA_CAPTION_ID+"= '"+CaptionID+"' " ;

        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataset = new ModulePermissionDetailsDataset (cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_CARD_NO)),cursor.getString(cursor.getColumnIndex(SINGLA_BASIC_INFO_NAME)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_MODULE_VTYPE)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_IS_MODULE)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_VIEW_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_EDIT_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_CREATE_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_REMOVE_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_IMPORT_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_EXPORT_FLAG)),cursor.getInt(cursor.getColumnIndex(SINGLA_CAPTION_PRINT_FLAG)));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataset;
    }

}