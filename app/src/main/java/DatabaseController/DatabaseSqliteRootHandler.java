package DatabaseController;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by rakes on 24-Feb-17.
 */

public class DatabaseSqliteRootHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseSqliteRootHandler.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    //TODO: Database Name
    public static final String DATABASE_NAME = "SinglaGroupsDB1";
    //TODo: Table Name
    private static final String SINGLA_USER_INFO_TABLE = "userInfoTbl";
    private static final String SINGLA_CAPTION_TABLE = "captionTbl";
    private static final String SINGLA_BASIC_INFO_TABLE = "basicInfoTbl";
    private static final String SINGLA_GROUPS_TABLE = "groupSTbl";
    private static final String SINGLA_FILTER_TABLE = "filterTbl";
    private static final String SINGLA_COLORS_TABLE = "colorsTbl";
    private static final String SINGLA_WISHLIST_TABLE = "wishlistTbl";
    private static final String SINGLA_BOX_TABLE = "addToBoxTbl";
    private static final String SINGLA_CHANGE_PARTY_OLD_TABLE = "changePartyOldList";
    private static final String SINGLA_CHANGE_PARTY_NEW_TABLE = "changePartyNewList";
    private static final String SINGLA_CHANGE_SUB_PARTY_NEW_TABLE = "changeSubPartyNewList";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_TABLE = "activeSessionManageTbl";
    private static final String SINGLA_CLOSE_ORDER_TABLE = "closeOrderTbl";
    private static final String SINGLA_SIZE_SET_TABLE = "sizeSetTbl";
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
        */
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
    private static final String SINGLA_BASIC_INFO_KEY_ID = "id";
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
    //TODO: AllGroup  Table Column's Name
    private static final String SINGLA_KEY_ID = "id";
    private static final String SINGLA_MAIN_GROUP_ID = "mainGroupNameID";
    private static final String SINGLA_MAIN_GROUP_NAME = "mainGroupName";
    private static final String SINGLA_MAIN_GROUP_SEQUENCE = "mainGroupSeq";
    private static final String SINGLA_GROUP_ID = "groupID";
    private static final String SINGLA_GROUP_NAME = "groupName";
    private static final String SINGLA_GROUP_IMAGE_PATH = "groupImagePath";
    //TODO: Filter  Table Column's Name
    private static final String SINGLA_FILTER_KEY_ID = "id";
    private static final String SINGLA_FILTER_ATTR_ID = "filterAttrID";
    private static final String SINGLA_FILTER_ATTR_NAME = "filterAttrName";
    private static final String SINGLA_FILTER_SEQUENCE = "filterSeq";
    private static final String SINGLA_FILTER_FLAG = "filterFlag";
    private static final String SINGLA_FILTER_FLAG_TEMP = "filterFlagTemp";
    //TODO: Colors (Item Details)  Table Column's Name
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
    //TODO: WishList  Table Column's Name
    private static final String SINGLA_GROUP_IMAGE = "groupImage";
    private static final String SINGLA_MAIN_GROUP = "mainGroup";
    private static final String SINGLA_TOTAL_COLOR = "totalColor";
    //TODO: AddtoCart  Table Column's Name
        /* All Column's Name Match it of above Column's.
         So, thats why column's not Decalaration All Column's check on Base class of Add to cart*/
    //TODO: Active Session Manage Table Column Names
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_KEY_ID = "id";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID = "sessionID";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_DATE = "activeDate";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME = "activeTime1";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG = "serverFlag";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG = "flag";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME = "loginDatetime";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME = "logoutDatetime";
    private static final String SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME = "entryDatetime";
    //TODO: Close Order Table Column Names
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
    //TODO: Size Set  Table Column's Name
    private static final String SINGLA_SIZE_COUNT = "sizeCount";
    private static final String SINGLA_REQUIRED = "required";
    //TODO:	Constructor
    public DatabaseSqliteRootHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //TODO: Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: User info table create query
        String CREATE_TABLE_USER_INFO = "CREATE TABLE " + SINGLA_USER_INFO_TABLE + "(" + SINGLA_USER_INFO_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_USER_INFO_ID + " TEXT," + SINGLA_USER_INFO_NAME + " TEXT," + SINGLA_USER_INFO_ACTIVE + " INTEGER,"+ SINGLA_USER_INFO_MASTER_TYPE + " INTEGER,"+ SINGLA_USER_INFO_UNDER_ID + " TEXT,"+ SINGLA_USER_INFO_COMPANY_ID + " TEXT,"+ SINGLA_USER_INFO_COMPANY_NAME + " TEXT,"+ SINGLA_USER_INFO_DB_NAMNE + " TEXT,"+ SINGLA_USER_INFO_IS_DEFAULT + " INTEGER,"+ SINGLA_USER_INFO_DATA_TYPE_NAME + " TEXT,"+ SINGLA_USER_INFO_DATA_TYPE + " INTEGER,"+ SINGLA_DATETIME + " DATETIME) ";
        db.execSQL(CREATE_TABLE_USER_INFO);
        // TODO: Captions table create query
        String CREATE_TABLE_CAPTION = "CREATE TABLE " + SINGLA_CAPTION_TABLE + "(" + SINGLA_CAPTION_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_CAPTION_ID + " TEXT," + SINGLA_CAPTION_NAME + " TEXT,"+ SINGLA_CAPTION_SEQUENCE + " INTEGER,"+ SINGLA_CAPTION_UNDER_ID + " TEXT," + SINGLA_CAPTION_IS_MODULE + " INTEGER," + SINGLA_CAPTION_VIEW_FLAG + " INTEGER," + SINGLA_CAPTION_EDIT_FLAG + " INTEGER," + SINGLA_CAPTION_CREATE_FLAG + " INTEGER," + SINGLA_CAPTION_REMOVE_FLAG + " INTEGER," + SINGLA_CAPTION_PRINT_FLAG + " INTEGER," + SINGLA_CAPTION_IMPORT_FLAG + " INTEGER," + SINGLA_CAPTION_EXPORT_FLAG + " INTEGER," + SINGLA_CAPTION_CONTENT_CLASS + " TEXT," + SINGLA_CAPTION_MODULE_VTYPE + " INTEGER,"+ SINGLA_DATETIME + " DATETIME) ";
        db.execSQL(CREATE_TABLE_CAPTION);
        // TODO: Basic info table create query
        String CREATE_TABLE_BASIC_INFO = "CREATE TABLE " + SINGLA_BASIC_INFO_TABLE+ "(" + SINGLA_BASIC_INFO_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_BASIC_INFO_USER_ID + " TEXT," + SINGLA_BASIC_INFO_USER_GROUP_ID + " TEXT,"+ SINGLA_BASIC_INFO_AUTO_LAUNCH_MODULE + " INTEGER," + SINGLA_BASIC_INFO_ID + " TEXT," + SINGLA_BASIC_INFO_NAME + " TEXT,"+ SINGLA_BASIC_INFO_CODE + " TEXT,"+ SINGLA_BASIC_INFO_MASTER_TYPE + " INTEGER,"+ SINGLA_BASIC_INFO_TYPE_NAME + " TEXT," + SINGLA_BASIC_INFO_CARD_NO + " TEXT," + SINGLA_BASIC_INFO_ADDRESS_1 + " TEXT," + SINGLA_BASIC_INFO_ADDRESS_2 + " TEXT," + SINGLA_BASIC_INFO_ADDRESS_3 + " TEXT," + SINGLA_BASIC_INFO_CITY + " TEXT," + SINGLA_BASIC_INFO_STATE + " TEXT," + SINGLA_BASIC_INFO_COUNTRY + " TEXT," + SINGLA_BASIC_INFO_PIN + " TEXT," + SINGLA_BASIC_INFO_MOBILE + " TEXT," + SINGLA_BASIC_INFO_IMAGE + " TEXT," + SINGLA_BASIC_INFO_USER_FULL_NAME + " TEXT,"+ SINGLA_DATETIME + " DATETIME) ";
        db.execSQL(CREATE_TABLE_BASIC_INFO);
        // TODO: Groups table create query
        String CREATE_TABLE_GROUPS = "CREATE TABLE " + SINGLA_GROUPS_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_MAIN_GROUP_ID + " TEXT," + SINGLA_MAIN_GROUP_NAME + " TEXT," + SINGLA_MAIN_GROUP_SEQUENCE + " INTEGER," + SINGLA_GROUP_ID + " TEXT," + SINGLA_GROUP_NAME + " TEXT," + SINGLA_GROUP_IMAGE_PATH + " TEXT) ";
        db.execSQL(CREATE_TABLE_GROUPS);
        // TODO: Size Set table create query
        String CREATE_TABLE_SIZE_SET = "CREATE TABLE " + SINGLA_SIZE_SET_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_MAIN_GROUP_ID + " TEXT," + SINGLA_GROUP_ID + " TEXT," + SINGLA_SIZE_COUNT + " INTEGER," + SINGLA_REQUIRED + " INTEGER) ";
        db.execSQL(CREATE_TABLE_SIZE_SET);
        // TODO: Filter table create query
        String CREATE_TABLE_FILTER = "CREATE TABLE " + SINGLA_FILTER_TABLE + "(" + SINGLA_FILTER_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_FILTER_ATTR_ID + " TEXT," + SINGLA_FILTER_ATTR_NAME + " TEXT," + SINGLA_FILTER_SEQUENCE + " INTEGER," + SINGLA_FILTER_FLAG + " INTEGER," + SINGLA_FILTER_FLAG_TEMP + " INTEGER) ";
        db.execSQL(CREATE_TABLE_FILTER);
        // TODO: Colors (Item Details) table create query
        String CREATE_TABLE_COLORS = "CREATE TABLE " + SINGLA_COLORS_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_ITEM_ID+ " TEXT," + SINGLA_ITEM_CODE+ " TEXT," + SINGLA_ITEM_NAME+ " TEXT," + SINGLA_ITEM_IMAGE+ " TEXT," + SINGLA_ITEM_STOCK+ " INTEGER," + SINGLA_COLOR_FAMILY_ID+ " TEXT," + SINGLA_COLOR_FAMILY_NAME+ " TEXT," + SINGLA_RATE+ " INTEGER," + SINGLA_MD_RATE+ " INTEGER," + SINGLA_MD_STOCK+ " INTEGER," + SINGLA_COLOR_ID+ " TEXT," + SINGLA_COLOR_NAME + " TEXT," + SINGLA_SIZE_ID + " TEXT," + SINGLA_SIZE_NAME+ " TEXT," + SINGLA_UNIT + " TEXT,"+SINGLA_ATTR1+" TEXT,"+SINGLA_ATTR2+" TEXT,"+SINGLA_ATTR3+" TEXT,"+SINGLA_ATTR4+" TEXT,"+SINGLA_ATTR5+" TEXT,"+SINGLA_ATTR6+" TEXT,"+SINGLA_ATTR7+" TEXT,"+SINGLA_ATTR8+" TEXT,"+SINGLA_ATTR9+" TEXT,"+SINGLA_ATTR10+" TEXT," + SINGLA_MD_QTY+" INTEGER) ";
        db.execSQL(CREATE_TABLE_COLORS);
        // TODO: Wishlist table create query
        String CREATE_TABLE_WISHLIST = "CREATE TABLE " + SINGLA_WISHLIST_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_GROUP_ID+ " TEXT," + SINGLA_GROUP_NAME+ " TEXT," + SINGLA_GROUP_IMAGE+ " TEXT," + SINGLA_MAIN_GROUP+ " TEXT," + SINGLA_ITEM_ID+ " TEXT," + SINGLA_ITEM_CODE+ " TEXT," + SINGLA_ITEM_NAME+ " TEXT," + SINGLA_ITEM_IMAGE+ " TEXT," + SINGLA_ITEM_STOCK+ " INTEGER," + SINGLA_RATE+ " INTEGER,"+ SINGLA_TOTAL_COLOR+ " TEXT,"+ SINGLA_UNIT+ " TEXT) ";
        db.execSQL(CREATE_TABLE_WISHLIST);
        // TODO: AddToCart (Box) table create query
        String CREATE_TABLE_CART = "CREATE TABLE " + SINGLA_BOX_TABLE + "(" + SINGLA_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_GROUP_ID+ " TEXT," + SINGLA_GROUP_NAME+ " TEXT," + SINGLA_GROUP_IMAGE+ " TEXT," + SINGLA_MAIN_GROUP+ " TEXT," + SINGLA_ITEM_ID+ " TEXT," + SINGLA_ITEM_CODE+ " TEXT," + SINGLA_ITEM_NAME+ " TEXT," + SINGLA_ITEM_IMAGE+ " TEXT," + SINGLA_ITEM_STOCK+ " INTEGER," + SINGLA_RATE+ " INTEGER," + SINGLA_COLOR_ID+ " TEXT," + SINGLA_COLOR_NAME+ " TEXT," + SINGLA_SIZE_ID+ " TEXT," + SINGLA_SIZE_NAME+ " TEXT," + SINGLA_MD_RATE+ " INTEGER," + SINGLA_MD_QTY+ " INTEGER) ";
        db.execSQL(CREATE_TABLE_CART);
        // TODO: Active Session Manage table create query
        String CREATE_SINGLA_ACTIVE_SESSION_MANAGE_TABLE = "CREATE TABLE " + SINGLA_ACTIVE_SESSION_MANAGE_TABLE + "("+ SINGLA_ACTIVE_SESSION_MANAGE_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_ACTIVE_SESSION_MANAGE_SESSION_ID + " TEXT," + SINGLA_ACTIVE_SESSION_MANAGE_DATE + " DATE," + SINGLA_ACTIVE_SESSION_MANAGE_ACTIVE_TIME + " TEXT," + SINGLA_ACTIVE_SESSION_MANAGE_SERVER_FLAG + " INTEGER," + SINGLA_ACTIVE_SESSION_MANAGE_LOCAL_FLAG + " INTEGER," + SINGLA_ACTIVE_SESSION_MANAGE_LOGIN_DATETIME + " DATETIME," + SINGLA_ACTIVE_SESSION_MANAGE_LOGOUT_DATETIME + " DATETIME," + SINGLA_ACTIVE_SESSION_MANAGE_ENTRY_DATETIME + " DATETIME) ";
        db.execSQL(CREATE_SINGLA_ACTIVE_SESSION_MANAGE_TABLE);
        // TODO: Close Order table create query
        String CREATE_SINGLA_CLOSE_ORDER_TABLE = "CREATE TABLE " + SINGLA_CLOSE_ORDER_TABLE + "("+ SINGLA_ACTIVE_SESSION_MANAGE_KEY_ID + " INTEGER PRIMARY KEY," + SINGLA_CLOSE_ORDER_ORDER_ID + " TEXT," + SINGLA_CLOSE_ORDER_ORDER_NO + " TEXT," + SINGLA_CLOSE_ORDER_ORDER_DATE + " TEXT," + SINGLA_CLOSE_ORDER_GODOWN + " TEXT," + SINGLA_CLOSE_ORDER_GODOWN_ID + " TEXT," + SINGLA_CLOSE_ORDER_PARTY_ID + " TEXT," + SINGLA_CLOSE_ORDER_PARTY_NAME + " TEXT," + SINGLA_CLOSE_ORDER_SUB_PARTY_ID + " TEXT," + SINGLA_CLOSE_ORDER_SUB_PARTY_NAME + " TEXT," + SINGLA_CLOSE_ORDER_SUB_PARTY_APPLICABLE + " INTEGER," + SINGLA_CLOSE_ORDER_REFERENCE_NAME + " TEXT," + SINGLA_CLOSE_ORDER_REMARKS + " TEXT," + SINGLA_CLOSE_ORDER_AGENT_ID + " TEXT," + SINGLA_CLOSE_ORDER_AGENT_NAME + " TEXT," + SINGLA_CLOSE_ORDER_USER_NAME + " TEXT," + SINGLA_CLOSE_ORDER_CITY + " TEXT," + SINGLA_CLOSE_ORDER_STATE + " TEXT," + SINGLA_CLOSE_ORDER_MOBILE + " TEXT," + SINGLA_CLOSE_ORDER_FAIR_NAME + " TEXT," + SINGLA_CLOSE_ORDER_ITEM_COUNT + " INTEGER," + SINGLA_CLOSE_ORDER_TOTAL_BOOKED_QTY + " INTEGER," + SINGLA_CLOSE_ORDER_TOTAL_AMOUNT + " INTEGER,"+ SINGLA_CLOSE_ORDER_LAST_BOOKED_DATE + " TEXT," + SINGLA_CLOSE_ORDER_EMP_CV_NAME + " TEXT,"+ SINGLA_CLOSE_ORDER_EMP_CV_TYPE + " TEXT,"+ SINGLA_CLOSE_ORDER_CREDIT_DAYS + " INTEGER,"+ SINGLA_CLOSE_ORDER_CREDIT_LIMIT + " INTEGER,"+ SINGLA_CLOSE_ORDER_TOTAL_DUE_AMOUNT + " INTEGER,"+ SINGLA_CLOSE_ORDER_TOTAL_OVER_DUE_AMOUNT + " INTEGER,"+ SINGLA_CLOSE_ORDER_EXCEED_AMOUNT + " INTEGER,"+ SINGLA_CLOSE_ORDER_ENTRY_DATE + " TEXT,"+ SINGLA_CLOSE_ORDER_EMAIL + " TEXT,"+ SINGLA_CLOSE_ORDER_ACCOUNT_NO + " TEXT,"+ SINGLA_CLOSE_ORDER_ACCOUNT_HOLDER_NAME + " TEXT,"+ SINGLA_CLOSE_ORDER_IFSC_CODE + " TEXT,"+ SINGLA_CLOSE_ORDER_ID_NAME + " TEXT,"+ SINGLA_CLOSE_ORDER_GSTIN + " TEXT,"+ SINGLA_CLOSE_ORDER_AVG_OVER_DUE_DAYS + " TEXT,"+ SINGLA_CLOSE_ORDER_AVG_DUE_DAYS + " TEXT,"+ SINGLA_CLOSE_ORDER_PRICE_LIST_ID + " TEXT,"+ SINGLA_CLOSE_ORDER_LABEL + " TEXT,"+ SINGLA_CLOSE_ORDER_UNDER_NAME + " TEXT) ";
        db.execSQL(CREATE_SINGLA_CLOSE_ORDER_TABLE);
        db.close();
    }
    //TODO: Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + SINGLA_GROUPS_TABLE);
//        // Create tables again
//        onCreate(db);
        // Drop older table if existed
        Log.e(TAG,"oldVersion:"+oldVersion+"\tnewVersion:"+newVersion);
        if (oldVersion < newVersion) {
            onAlter(db);
            //Log.e(TAG,"oldVersion:"+oldVersion+"\tnewVersion:"+newVersion);
        }
        db.close();
    }
    //TODO: onAlter
    public void onAlter(SQLiteDatabase db) {
        // Category table create query
        String ALTER_TABLE_COLUMNS = "ALTER TABLE " + SINGLA_USER_INFO_TABLE + " ADD COLUMN " + SINGLA_DATETIME + " DATETIME; ";
        db.execSQL(ALTER_TABLE_COLUMNS);
        db.close();
    }
}
