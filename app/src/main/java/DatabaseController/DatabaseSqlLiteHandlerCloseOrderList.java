package DatabaseController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orderbooking.StaticValues;
import orderbooking.customerlist.datasets.AgentDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.GodownDataset;
import orderbooking.customerlist.datasets.OrderDetails;

public class DatabaseSqlLiteHandlerCloseOrderList {
    private static final String TAG = DatabaseSqlLiteHandlerCloseOrderList.class.getSimpleName();
    //TODo: Table Name
    private static final String SINGLA_CLOSE_ORDER_TABLE = "closeOrderTbl";
    //TODO: Filter  Table Column's Name
    private static final String SINGLA_CLOSE_ORDER_KEY_ID = "id";
    private static final String SINGLA_CLOSE_ORDER_ORDER_ID = "orderID";
    private static final String SINGLA_CLOSE_ORDER_ORDER_NO = "orderNo";
    private static final String SINGLA_CLOSE_ORDER_ORDER_DATE = "orderDate";
    private static final String SINGLA_CLOSE_ORDER_GODOWN = "godown";
    private static final String SINGLA_CLOSE_ORDER_GODOWN_ID = "godownId";
    private static final String SINGLA_CLOSE_ORDER_PARTY_ID = "partyId";
    private static final String SINGLA_CLOSE_ORDER_PARTY_NAME = "partyName";
    private static final String SINGLA_CLOSE_ORDER_SUB_PARTY_ID = "subPArtyId";
    private static final String SINGLA_CLOSE_ORDER_SUB_PARTY_NAME= "subPArtyName";
    private static final String SINGLA_CLOSE_ORDER_SUB_PARTY_APPLICABLE = "subPartyApplicable";
    private static final String SINGLA_CLOSE_ORDER_REFERENCE_NAME = "refName";
    private static final String SINGLA_CLOSE_ORDER_REMARKS = "remarks";
    private static final String SINGLA_CLOSE_ORDER_USER_NAME = "username";
    private static final String SINGLA_CLOSE_ORDER_AGENT_ID = "agentId";
    private static final String SINGLA_CLOSE_ORDER_AGENT_NAME = "agentName";
    private static final String SINGLA_CLOSE_ORDER_CITY = "city";
    private static final String SINGLA_CLOSE_ORDER_STATE = "state";
    private static final String SINGLA_CLOSE_ORDER_MOBILE = "mobile";
    private static final String SINGLA_CLOSE_ORDER_FAIR_NAME = "fairName";
    private static final String SINGLA_CLOSE_ORDER_ITEM_COUNT = "itemCount";
    private static final String SINGLA_CLOSE_ORDER_TOTAL_BOOKED_QTY = "totalBookQty";
    private static final String SINGLA_CLOSE_ORDER_TOTAL_AMOUNT = "totalAmount";
    private static final String SINGLA_CLOSE_ORDER_LAST_BOOKED_DATE = "lastBookDate";
    private static final String SINGLA_CLOSE_ORDER_EMP_CV_NAME = "empCVName";
    private static final String SINGLA_CLOSE_ORDER_EMP_CV_TYPE = "empCVType";
    private static final String SINGLA_CLOSE_ORDER_CREDIT_DAYS = "creditDays";
    private static final String SINGLA_CLOSE_ORDER_CREDIT_LIMIT = "creditLimit";
    private static final String SINGLA_CLOSE_ORDER_TOTAL_DUE_AMOUNT = "totalDueAmt";
    private static final String SINGLA_CLOSE_ORDER_TOTAL_OVER_DUE_AMOUNT = "totalOverDueAmt";
    private static final String SINGLA_CLOSE_ORDER_EXCEED_AMOUNT = "exceedAmt";
    private static final String SINGLA_CLOSE_ORDER_ENTRY_DATE = "entryDate";
    private static final String SINGLA_CLOSE_ORDER_EMAIL = "email";
    private static final String SINGLA_CLOSE_ORDER_ACCOUNT_NO = "accountNo";
    private static final String SINGLA_CLOSE_ORDER_ACCOUNT_HOLDER_NAME = "accHolderName";
    private static final String SINGLA_CLOSE_ORDER_IFSC_CODE = "ifscCode";
    private static final String SINGLA_CLOSE_ORDER_ID_NAME = "idName";
    private static final String SINGLA_CLOSE_ORDER_GSTIN = "gstIn";
    private static final String SINGLA_CLOSE_ORDER_AVG_OVER_DUE_DAYS = "avgOverDueDays";
    private static final String SINGLA_CLOSE_ORDER_AVG_DUE_DAYS = "avgDueDays";
    private static final String SINGLA_CLOSE_ORDER_PRICE_LIST_ID = "pricelistID";
    private static final String SINGLA_CLOSE_ORDER_LABEL = "label";
    private static final String SINGLA_CLOSE_ORDER_UNDER_NAME = "underName";
    private Context context;
    //TODO:	Constructor
    public DatabaseSqlLiteHandlerCloseOrderList(Context context) {
        this.context = context;
    }
    //TODO: Filter Table Delete
    public void CloseOrderTableDelete() {
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        db.delete(SINGLA_CLOSE_ORDER_TABLE, null, null);
        db.close();
    }
    //TODO: Inserting Data of Filter Table
    public void insertCloseOrderTable(List<Map<String,String>> list) {
        final long startTime = System.currentTimeMillis();
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getWritableDatabase();
        DatabaseUtils.InsertHelper ih=new DatabaseUtils.InsertHelper(db,SINGLA_CLOSE_ORDER_TABLE);
        // Get the numeric indexes for each of the columns that we're updating
        final int OrderID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_ID);
        final int OrderNo = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_NO);
        final int OrderDate = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_DATE);
        final int Godown = ih.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN);
        final int GodownID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN_ID);
        final int PartyID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_ID);
        final int PartyName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_NAME);
        final int SubPartyID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_ID);
        final int SubParty = ih.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_NAME);
        final int SubPartyApplicable = ih.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_APPLICABLE);
        final int RefName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_REFERENCE_NAME);
        final int Remarks = ih.getColumnIndex(SINGLA_CLOSE_ORDER_REMARKS);
        final int UserName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_USER_NAME);
        final int AgentID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_ID);
        final int AgentName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_NAME);
        final int City = ih.getColumnIndex(SINGLA_CLOSE_ORDER_CITY);
        final int State = ih.getColumnIndex(SINGLA_CLOSE_ORDER_STATE);
        final int Mobile = ih.getColumnIndex(SINGLA_CLOSE_ORDER_MOBILE);
        final int FairName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_FAIR_NAME);
        final int ItemCount = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ITEM_COUNT);
        final int TotalBookQty = ih.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_BOOKED_QTY);
        final int TotalAmount = ih.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_AMOUNT);
        final int LastBookDate = ih.getColumnIndex(SINGLA_CLOSE_ORDER_LAST_BOOKED_DATE);
        final int EmpCVName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_NAME);
        final int EmpCVType = ih.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_TYPE);
        final int CreditDays = ih.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_DAYS);
        final int CreditLimit = ih.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_LIMIT);
        final int TotalDueAmt = ih.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_DUE_AMOUNT);
        final int TotalOverDueAmt = ih.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_OVER_DUE_AMOUNT);
        final int ExceedAmt = ih.getColumnIndex(SINGLA_CLOSE_ORDER_EXCEED_AMOUNT);
        final int EntryDate = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ENTRY_DATE);
        final int Email = ih.getColumnIndex(SINGLA_CLOSE_ORDER_EMAIL);
        final int AccountNo = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ACCOUNT_NO);
        final int AccountHolderName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ACCOUNT_HOLDER_NAME);
        final int IFSCCOde = ih.getColumnIndex(SINGLA_CLOSE_ORDER_IFSC_CODE);
        final int IDName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_ID_NAME);
        final int GSTIN = ih.getColumnIndex(SINGLA_CLOSE_ORDER_GSTIN);
        final int AvgOverDueDays = ih.getColumnIndex(SINGLA_CLOSE_ORDER_AVG_OVER_DUE_DAYS);
        final int AvgDueDays = ih.getColumnIndex(SINGLA_CLOSE_ORDER_AVG_DUE_DAYS);
        final int PricelistID = ih.getColumnIndex(SINGLA_CLOSE_ORDER_PRICE_LIST_ID);
        final int Label = ih.getColumnIndex(SINGLA_CLOSE_ORDER_LABEL);
        final int UnderName = ih.getColumnIndex(SINGLA_CLOSE_ORDER_UNDER_NAME);
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            for(int x=0; x<list.size(); x++){
                // ... Create the data for this row (not shown) ...
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
                // Add the data for each column
                ih.bind(OrderID, list.get(x).get("OrderID"));
                ih.bind(OrderNo, list.get(x).get("OrderNo"));
                ih.bind(OrderDate, list.get(x).get("OrderDate"));
                ih.bind(Godown, list.get(x).get("Godown"));
                ih.bind(GodownID, list.get(x).get("GodownID"));
                ih.bind(PartyID, list.get(x).get("PartyID"));
                ih.bind(PartyName, list.get(x).get("PartyName"));
                ih.bind(SubPartyID, list.get(x).get("SubPartyID"));
                ih.bind(SubParty, list.get(x).get("SubParty"));
                ih.bind(SubPartyApplicable, list.get(x).get("SubPartyApplicable"));
                ih.bind(RefName, list.get(x).get("RefName"));
                ih.bind(Remarks, list.get(x).get("Remarks"));
                ih.bind(UserName, list.get(x).get("UserName"));
                ih.bind(AgentID, list.get(x).get("AgentID"));
                ih.bind(AgentName, list.get(x).get("AgentName"));
                ih.bind(City, list.get(x).get("City"));
                ih.bind(State, list.get(x).get("State"));
                ih.bind(Mobile, list.get(x).get("Mobile"));
                ih.bind(FairName, list.get(x).get("FairName"));
                ih.bind(ItemCount, list.get(x).get("ItemCount"));
                ih.bind(TotalBookQty, list.get(x).get("TotalBookQty"));
                ih.bind(TotalAmount, list.get(x).get("TotalAmount"));
                ih.bind(LastBookDate, list.get(x).get("LastBookDate"));
                ih.bind(EmpCVName, list.get(x).get("EmpCVName"));
                ih.bind(EmpCVType, list.get(x).get("EmpCVType"));
                ih.bind(CreditDays, list.get(x).get("CreditDays"));
                ih.bind(CreditLimit, list.get(x).get("CreditLimit"));
                ih.bind(TotalDueAmt, list.get(x).get("TotalDueAmt"));
                ih.bind(TotalOverDueAmt, list.get(x).get("TotalOverDueAmt"));
                ih.bind(ExceedAmt, list.get(x).get("ExceedAmt"));
                ih.bind(EntryDate, list.get(x).get("EntryDate"));
                ih.bind(Email, list.get(x).get("Email"));
                ih.bind(AccountNo, list.get(x).get("AccountNo"));
                ih.bind(AccountHolderName, list.get(x).get("AccountHolderName"));
                ih.bind(IFSCCOde, list.get(x).get("IFSCCOde"));
                ih.bind(IDName, list.get(x).get("IDName"));
                ih.bind(GSTIN, list.get(x).get("GSTIN"));
                ih.bind(AvgOverDueDays, list.get(x).get("AvgOverDueDays"));
                ih.bind(AvgDueDays, list.get(x).get("AvgDueDays"));
                ih.bind(PricelistID, list.get(x).get("PricelistID"));
                ih.bind(Label, list.get(x).get("Label"));
                ih.bind(UnderName, list.get(x).get("UnderName"));
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
    public List<GodownDataset> getGodownList(){
        List<GodownDataset> dataList=new ArrayList<GodownDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_CLOSE_ORDER_GODOWN_ID+"), "+SINGLA_CLOSE_ORDER_GODOWN+" from " + SINGLA_CLOSE_ORDER_TABLE;
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataList.add(new GodownDataset(cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<AgentDataset> getAgentList(String GodownID){
        List<AgentDataset> dataList=new ArrayList<AgentDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT("+SINGLA_CLOSE_ORDER_AGENT_ID+"), "+SINGLA_CLOSE_ORDER_AGENT_NAME+","+SINGLA_CLOSE_ORDER_GODOWN_ID+" from " + SINGLA_CLOSE_ORDER_TABLE + " WHERE "+SINGLA_CLOSE_ORDER_GODOWN_ID+"='"+GodownID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                dataList.add(new AgentDataset(cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN_ID))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public List<CloseOrBookDataset> getOrderList(String GodownID,String AgentID){
        List<CloseOrBookDataset> dataList=new ArrayList<CloseOrBookDataset>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * from " + SINGLA_CLOSE_ORDER_TABLE;// + " WHERE "+SINGLA_CLOSE_ORDER_GODOWN_ID+"='"+GodownID+"'";// AND "+SINGLA_CLOSE_ORDER_AGENT_ID+"='"+AgentID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                String Godown = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN))+"\t\t Total Order :"+getGodownOrderCount(cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN)));
                //System.out.println(Godown);
                dataList.add(new CloseOrBookDataset(cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_MOBILE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CITY)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_STATE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CITY)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_NO)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_DATE)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_APPLICABLE)),Godown,cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_USER_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_FAIR_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_LAST_BOOKED_DATE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_TYPE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_REFERENCE_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_REMARKS)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_LIMIT)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ITEM_COUNT)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_BOOKED_QTY)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_AMOUNT)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_DAYS)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_DUE_AMOUNT)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_OVER_DUE_AMOUNT)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EXCEED_AMOUNT)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ENTRY_DATE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EMAIL)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ACCOUNT_NO)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ACCOUNT_HOLDER_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_IFSC_CODE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ID_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GSTIN)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AVG_OVER_DUE_DAYS)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AVG_DUE_DAYS)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PRICE_LIST_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_LABEL)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_UNDER_NAME))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public int getGodownOrderCount(String Godown){
        int count = 0;
        // Select All Query
        String selectQuery = "SELECT COUNT(DISTINCT "+SINGLA_CLOSE_ORDER_ORDER_ID+ ") as count from " + SINGLA_CLOSE_ORDER_TABLE + " WHERE "+SINGLA_CLOSE_ORDER_GODOWN_ID+"='"+Godown+"'";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                 count = cursor.getInt(cursor.getColumnIndex("count"));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return count;
    }
    public List<OrderDetails> getOrderDetailsList(String GodownID, String AgentID){
        List<OrderDetails> dataList=new ArrayList<OrderDetails>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * from " + SINGLA_CLOSE_ORDER_TABLE + " WHERE "+SINGLA_CLOSE_ORDER_GODOWN_ID+"='"+GodownID+"' AND "+SINGLA_CLOSE_ORDER_AGENT_ID+"='"+AgentID+"' ";
        //System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                //dataList.add(new OrderDetails(cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_ID)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_AGENT_NAME)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_MOBILE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CITY)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_STATE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CITY)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_NO)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_DATE)),cursor.getInt(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_APPLICABLE)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_GODOWN)),cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_USER_NAME))));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return dataList;
    }
    public String[][] getOrderDeatils(String GodownID,String AgentID){
        String[][] str = new String[17][17];
        // Select All Query
        String selectQuery = "SELECT DISTINCT * from " + SINGLA_CLOSE_ORDER_TABLE + " WHERE "+SINGLA_CLOSE_ORDER_GODOWN_ID+"='"+GodownID+"' AND "+SINGLA_CLOSE_ORDER_AGENT_ID+"='"+AgentID+"' ";
        System.out.println(selectQuery);
        DatabaseSqliteRootHandler rootHandler = new DatabaseSqliteRootHandler(context);
        SQLiteDatabase db = rootHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                str[0][0] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_ID));//cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_NO))+"\n"+cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ORDER_DATE));
                //TODO: Header
                str[1][0] =  "";
                str[2][0] =  "Party Name";
                str[3][0] =  "";
                str[4][0] =  "Sub Party Name";
                str[5][0] =  "Reference Name";
                str[6][0] =  "Mobile";
                str[7][0] =  "City / State";
                str[8][0] =  "Created By \n(Name/Type/Code)";
                str[9][0] =  "Total Item :";
                str[10][0] =  "Total Amount :";
                str[11][0] = "Total Booked Qty :";
                str[12][0] = "";
                str[13][0] = "";
                str[14][0] = "";
                str[15][0] = "";
                str[16][0] = "";

                //TODO: Data
                str[0][1] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_ID));
                str[0][2] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_PARTY_NAME));
                str[0][3] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_ID));
                str[0][4] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_SUB_PARTY_NAME));
                str[0][5] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_REFERENCE_NAME));
                str[0][6] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_MOBILE));
                str[0][7] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CITY)) + " / "+cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_STATE));
                str[0][8] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_NAME))+"\n"+cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_USER_NAME))+"-"+cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EMP_CV_TYPE));
                str[0][9] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_ITEM_COUNT));
                str[0][10] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_AMOUNT));
                str[0][11] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_BOOKED_QTY));
                str[0][12] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_DAYS));
                str[0][13] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_CREDIT_LIMIT));
                str[0][14] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_DUE_AMOUNT));
                str[0][15] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_TOTAL_OVER_DUE_AMOUNT));
                str[0][16] = cursor.getString(cursor.getColumnIndex(SINGLA_CLOSE_ORDER_EXCEED_AMOUNT));
            } while (cursor.moveToNext());
            // closing connection
            cursor.close();
            db.close();
        }
        // returning
        return str;
    }

}