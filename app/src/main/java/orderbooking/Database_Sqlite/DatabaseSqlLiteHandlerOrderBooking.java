package orderbooking.Database_Sqlite;

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

import com.singlagroup.customwidgets.DateFormatsMethods;

import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.customerlist.temp.BookOrderAdapter;

public class DatabaseSqlLiteHandlerOrderBooking extends SQLiteOpenHelper {
	public static int DATABASE_VERSION = 1;
	//TODO: Database Name
	public static final String DATABASE_NAME = "SinglaGroupsOrderBookingDB";
	//----------------------------TODO: Table Name--------------------------------------------------------------
	private static final String ORDER_BOOKING_TABLE = "orderBookingTbl";
	private static final String SINGLA_SIZE_SET_TABLE = "sizeSetTbl";
	private static final String OUT_OF_STOCK_TABLE = "tempOutOfstockTbl";
	//-------------------TODO: Barcode Scanner Table Column Names---------------------------------------------
	private static final String ORDER_BOOKING_KEY_ID = "id";
	private static final String ORDER_BOOKING_ITEM_ID = "itemId";
	private static final String ORDER_BOOKING_ITEM_NAME = "itemName";
	private static final String ORDER_BOOKING_ITEM_CODE = "itemCode";
	private static final String ORDER_BOOKING_COLOR_ID = "colorId";
	private static final String ORDER_BOOKING_COLOR_NAME = "colorName";
	private static final String ORDER_BOOKING_COLOR_FAMILY_NAME = "colorFamilyName";
	private static final String ORDER_BOOKING_COLOR_CODE = "colorCode";
	private static final String ORDER_BOOKING_MAIN_GROUP_ID = "mainGroupId";
	private static final String ORDER_BOOKING_MAIN_GROUP = "mainGroupName";
	private static final String ORDER_BOOKING_GROUP_ID = "groupId";
	private static final String ORDER_BOOKING_GROUP = "groupName";
	private static final String ORDER_BOOKING_SUB_GROUP_ID = "subGroupId";
	private static final String ORDER_BOOKING_SUB_GROUP_NAME = "subGroupName";
	private static final String ORDER_BOOKING_SIZE_ID = "sizeId";
	private static final String ORDER_BOOKING_SIZE_NAME = "sizeName";
	private static final String ORDER_BOOKING_SIZE_SEQUENCE = "sizeSequence";
	private static final String ORDER_BOOKING_BARCODE = "barcode";
	private static final String ORDER_BOOKING_STOCK = "cololSizestock";
	private static final String ORDER_BOOKING_RESERVE_STOCK = "reserveCololSizestock";
	private static final String ORDER_BOOKING_IN_PRODUCTION = "inProduction";
	private static final String ORDER_BOOKING_MD_APPLICABLE = "mDApplicable";
	private static final String ORDER_BOOKING_SUB_ITEM_APPLICABLE = "subItemApplicable";
	private static final String ORDER_BOOKING_FLAG_LOCAL_STATUS = "flagLocalStatus";
	private static final String ORDER_BOOKING_REMARKS = "remarks";
	private static final String ORDER_BOOKING_IMAGE_STATUS = "imageStatus";
	private static final String ORDER_BOOKING_IMAGE_URL = "imageUrl";
	//TODO: SubItem Column's name
	private static final String ORDER_BOOKING_SUB_ITEM_ID = "subItemId";
	private static final String ORDER_BOOKING_SUB_ITEM_NAME = "subItemName";
	private static final String ORDER_BOOKING_SUB_ITEM_CODE = "subItemCode";
	//TODO: Multiple Column create according to multi customer
	private static final String ORDER_BOOKING_ORDER_ID = "orderId";
	private static final String ORDER_BOOKING_ORDER_BOOKED_QTY = "orderBookedQty";
	private static final String ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS = "orderBookedQtyPrevious";
	private static final String ORDER_BOOKING_RATE = "rate";
	private static final String ORDER_BOOKING_MRP = "mrp";
	private static final String ORDER_BOOKING_DISCOUNT_RATE = "discountRate";
	private static final String ORDER_BOOKING_DISCOUNT_PERCENTAGE = "disPercentage";
	private static final String ORDER_BOOKING_EXPECTED_DELIVERY_DATE = "expectedDeliveryDate";

	//TODO: Size Set  Table Column's Name
	private static final String SINGLA_SIZE_COUNT = "sizeCount";
	private static final String SINGLA_REQUIRED = "required";

	//	------------------------TODO: Constructor call----------------------------------------
	//TODO:	Constructor
	public DatabaseSqlLiteHandlerOrderBooking(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//TODO: Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (StaticValues.MDApplicable == 1) {
			String CompleteQuery = "";
			String OrderIDQuery = "", OrderQuery = "", OrderPreQuery = "", RateQuery = "", MrpQuery = "", DiscountRateQuery = "", DisPercentageQuery = "", ExpectedDeliveryDateQuery = "";
			for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
				OrderIDQuery += "," + ORDER_BOOKING_ORDER_ID + i + " TEXT";
				OrderQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " TEXT";
				OrderPreQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i + " TEXT";
				RateQuery += "," + ORDER_BOOKING_RATE + i + " TEXT";
				MrpQuery += "," + ORDER_BOOKING_MRP + i + " TEXT";
				DiscountRateQuery += "," + ORDER_BOOKING_DISCOUNT_RATE + i + " TEXT";
				DisPercentageQuery += "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + i + " TEXT";
				ExpectedDeliveryDateQuery += "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i + " TEXT";
			}
			CompleteQuery = OrderIDQuery + OrderQuery + OrderPreQuery + RateQuery + MrpQuery + DiscountRateQuery + DisPercentageQuery + ExpectedDeliveryDateQuery;
			//System.out.println("CompleteQuery:"+CompleteQuery);
			//TODO:CREATE ORDER_BOOKING TABLE
			String CREATE_ORDER_BOOKING_TABLE = "CREATE TABLE " + ORDER_BOOKING_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_ITEM_ID + " TEXT," + ORDER_BOOKING_ITEM_NAME + " TEXT," + ORDER_BOOKING_ITEM_CODE + " TEXT," + ORDER_BOOKING_COLOR_ID + " TEXT," + ORDER_BOOKING_COLOR_NAME + " TEXT," + ORDER_BOOKING_COLOR_FAMILY_NAME + " TEXT," + ORDER_BOOKING_COLOR_CODE + " TEXT," + ORDER_BOOKING_MAIN_GROUP_ID + " TEXT," + ORDER_BOOKING_MAIN_GROUP + " TEXT," + ORDER_BOOKING_GROUP_ID + " TEXT," + ORDER_BOOKING_GROUP + " TEXT," + ORDER_BOOKING_SUB_GROUP_ID + " TEXT," + ORDER_BOOKING_SUB_GROUP_NAME + " TEXT," + ORDER_BOOKING_SIZE_ID + " TEXT," + ORDER_BOOKING_SIZE_NAME + " TEXT," + ORDER_BOOKING_SIZE_SEQUENCE + " INTEGER," + ORDER_BOOKING_BARCODE + " TEXT," + ORDER_BOOKING_STOCK + " INTEGER," + ORDER_BOOKING_RESERVE_STOCK + " INTEGER," + ORDER_BOOKING_IN_PRODUCTION + " INTEGER," + ORDER_BOOKING_MD_APPLICABLE + " TEXT," + ORDER_BOOKING_SUB_ITEM_APPLICABLE + " TEXT," + ORDER_BOOKING_FLAG_LOCAL_STATUS + " INTEGER," + ORDER_BOOKING_REMARKS + " TEXT," + ORDER_BOOKING_IMAGE_STATUS + " INTEGER," + ORDER_BOOKING_IMAGE_URL + " TEXT" + CompleteQuery + ") ";
			db.execSQL(CREATE_ORDER_BOOKING_TABLE);
			//TODO:CREATE  OUT OF STOCK TABLE
			String CREATE_OUT_OF_STOCK_TABLE = "CREATE TABLE " + OUT_OF_STOCK_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_ITEM_ID + " TEXT," + ORDER_BOOKING_ITEM_NAME + " TEXT," + ORDER_BOOKING_ITEM_CODE + " TEXT," + ORDER_BOOKING_COLOR_ID + " TEXT," + ORDER_BOOKING_COLOR_NAME + " TEXT," + ORDER_BOOKING_SIZE_ID + " TEXT," + ORDER_BOOKING_SIZE_NAME + " TEXT," + ORDER_BOOKING_SIZE_SEQUENCE + " INTEGER," + ORDER_BOOKING_STOCK + " INTEGER," + ORDER_BOOKING_REMARKS + " TEXT," + ORDER_BOOKING_ORDER_ID + " TEXT," + ORDER_BOOKING_ORDER_BOOKED_QTY + " TEXT," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + " TEXT," + ORDER_BOOKING_RATE + " TEXT," + ORDER_BOOKING_MRP + " TEXT," + ORDER_BOOKING_DISCOUNT_RATE + " TEXT," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + " TEXT) ";
			db.execSQL(CREATE_OUT_OF_STOCK_TABLE);
		} else {
			if (StaticValues.SubItemApplicable == 1) {
				//TODO : SubItem
				String CompleteQuery = "";
				String OrderIDQuery = "", OrderQuery = "", OrderPreQuery = "", RateQuery = "", MrpQuery = "", DiscountRateQuery = "", DisPercentageQuery = "", ExpectedDeliveryDateQuery = "";
				for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
					OrderIDQuery += "," + ORDER_BOOKING_ORDER_ID + i + " TEXT";
					OrderQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " TEXT";
					OrderPreQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i + " TEXT";
					RateQuery += "," + ORDER_BOOKING_RATE + i + " TEXT";
					MrpQuery += "," + ORDER_BOOKING_MRP + i + " TEXT";
					DiscountRateQuery += "," + ORDER_BOOKING_DISCOUNT_RATE + i + " TEXT";
					DisPercentageQuery += "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + i + " TEXT";
					ExpectedDeliveryDateQuery += "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i + " TEXT";
				}
				CompleteQuery = OrderIDQuery + OrderQuery + OrderPreQuery + RateQuery + MrpQuery + DiscountRateQuery + DisPercentageQuery + ExpectedDeliveryDateQuery;
				//System.out.println("CompleteQuery:"+CompleteQuery);
				// Category table create query
				String CREATE_ORDER_BOOKING_TABLE = "CREATE TABLE " + ORDER_BOOKING_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_ITEM_ID + " TEXT," + ORDER_BOOKING_ITEM_NAME + " TEXT," + ORDER_BOOKING_ITEM_CODE + " TEXT," + ORDER_BOOKING_SUB_ITEM_ID + " TEXT," + ORDER_BOOKING_SUB_ITEM_NAME + " TEXT," + ORDER_BOOKING_SUB_ITEM_CODE + " TEXT," + ORDER_BOOKING_MAIN_GROUP_ID + " TEXT," + ORDER_BOOKING_MAIN_GROUP + " TEXT," + ORDER_BOOKING_GROUP_ID + " TEXT," + ORDER_BOOKING_GROUP + " TEXT," + ORDER_BOOKING_SUB_GROUP_ID + " TEXT," + ORDER_BOOKING_SUB_GROUP_NAME + " TEXT," + ORDER_BOOKING_BARCODE + " TEXT," + ORDER_BOOKING_STOCK + " INTEGER," + ORDER_BOOKING_RESERVE_STOCK + " INTEGER," + ORDER_BOOKING_IN_PRODUCTION + " INTEGER," + ORDER_BOOKING_FLAG_LOCAL_STATUS + " INTEGER," + ORDER_BOOKING_REMARKS + " TEXT," + ORDER_BOOKING_IMAGE_STATUS + " INTEGER," + ORDER_BOOKING_IMAGE_URL + " TEXT" + CompleteQuery + ") ";
				db.execSQL(CREATE_ORDER_BOOKING_TABLE);
				//TODO:CREATE  OUT OF STOCK TABLE
				String CREATE_OUT_OF_STOCK_TABLE = "CREATE TABLE " + OUT_OF_STOCK_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_ITEM_ID + " TEXT," + ORDER_BOOKING_ITEM_NAME + " TEXT," + ORDER_BOOKING_ITEM_CODE + " TEXT," + ORDER_BOOKING_SUB_ITEM_ID + " TEXT," + ORDER_BOOKING_SUB_ITEM_NAME + " TEXT," + ORDER_BOOKING_SUB_ITEM_CODE + " TEXT," + ORDER_BOOKING_STOCK + " INTEGER," + ORDER_BOOKING_REMARKS + " TEXT," + ORDER_BOOKING_ORDER_ID + " TEXT," + ORDER_BOOKING_ORDER_BOOKED_QTY + " TEXT," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + " TEXT," + ORDER_BOOKING_RATE + " TEXT," + ORDER_BOOKING_MRP + " TEXT," + ORDER_BOOKING_DISCOUNT_RATE + " TEXT," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + " TEXT) ";
				db.execSQL(CREATE_OUT_OF_STOCK_TABLE);
			} else {
				//TODO : Only Item Without multidetails
				String CompleteQuery = "";
				String OrderIDQuery = "", OrderQuery = "", OrderPreQuery = "", RateQuery = "", MrpQuery = "", DiscountRateQuery = "", DisPercentageQuery = "", ExpectedDeliveryDateQuery = "";
				for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
					OrderIDQuery += "," + ORDER_BOOKING_ORDER_ID + i + " TEXT";
					OrderQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " TEXT";
					OrderPreQuery += "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i + " TEXT";
					RateQuery += "," + ORDER_BOOKING_RATE + i + " TEXT";
					MrpQuery += "," + ORDER_BOOKING_MRP + i + " TEXT";
					DiscountRateQuery += "," + ORDER_BOOKING_DISCOUNT_RATE + i + " TEXT";
					DisPercentageQuery += "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + i + " TEXT";
					ExpectedDeliveryDateQuery += "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i + " TEXT";
				}
				CompleteQuery = OrderIDQuery + OrderQuery + OrderPreQuery + RateQuery +  MrpQuery + DiscountRateQuery + DisPercentageQuery + ExpectedDeliveryDateQuery;
				//System.out.println("CompleteQuery:"+CompleteQuery);
				// Category table create query
				String CREATE_ORDER_BOOKING_TABLE = "CREATE TABLE " + ORDER_BOOKING_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_ITEM_ID + " TEXT," + ORDER_BOOKING_ITEM_NAME + " TEXT," + ORDER_BOOKING_ITEM_CODE + " TEXT," + ORDER_BOOKING_MAIN_GROUP_ID + " TEXT," + ORDER_BOOKING_MAIN_GROUP + " TEXT," + ORDER_BOOKING_GROUP_ID + " TEXT," + ORDER_BOOKING_GROUP + " TEXT," + ORDER_BOOKING_SUB_GROUP_ID + " TEXT," + ORDER_BOOKING_SUB_GROUP_NAME + " TEXT," + ORDER_BOOKING_BARCODE + " TEXT," + ORDER_BOOKING_STOCK + " INTEGER," + ORDER_BOOKING_RESERVE_STOCK + " INTEGER," + ORDER_BOOKING_IN_PRODUCTION + " INTEGER," + ORDER_BOOKING_FLAG_LOCAL_STATUS + " INTEGER," + ORDER_BOOKING_REMARKS + " TEXT," + ORDER_BOOKING_IMAGE_STATUS + " INTEGER," + ORDER_BOOKING_IMAGE_URL + " TEXT" + CompleteQuery + ") ";
				db.execSQL(CREATE_ORDER_BOOKING_TABLE);
			}
		}
		String CREATE_TABLE_SIZE_SET = "CREATE TABLE " + SINGLA_SIZE_SET_TABLE + "(" + ORDER_BOOKING_KEY_ID + " INTEGER PRIMARY KEY," + ORDER_BOOKING_MAIN_GROUP_ID + " TEXT," + ORDER_BOOKING_GROUP_ID + " TEXT," + SINGLA_SIZE_COUNT + " INTEGER," + SINGLA_REQUIRED + " INTEGER) ";
		db.execSQL(CREATE_TABLE_SIZE_SET);
	}

	//TODO: Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + ORDER_BOOKING_TABLE);
		// Create tables again
		onCreate(db);
	}

	//------------------------------------ TODO: Delete table data-------------------------------------------------------------
	public void deleteTablesData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ORDER_BOOKING_TABLE, null, null);
		db.delete(SINGLA_SIZE_SET_TABLE, null, null);
		db.close();
	}
	public void deleteOutOfStockTablesData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(OUT_OF_STOCK_TABLE, null, null);
		db.close();
	}
	//------------------------------------ TODO: Insert data into Multi Details table-------------------------------------------------------------
	public void insertOrderBookingTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, ORDER_BOOKING_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int ItemID = ih.getColumnIndex(ORDER_BOOKING_ITEM_ID);
		final int ItemName = ih.getColumnIndex(ORDER_BOOKING_ITEM_NAME);
		final int ItemCode = ih.getColumnIndex(ORDER_BOOKING_ITEM_CODE);
		final int ColorID = ih.getColumnIndex(ORDER_BOOKING_COLOR_ID);
		final int Color = ih.getColumnIndex(ORDER_BOOKING_COLOR_NAME);
		final int ColorFamily = ih.getColumnIndex(ORDER_BOOKING_COLOR_FAMILY_NAME);
		final int ColorCode = ih.getColumnIndex(ORDER_BOOKING_COLOR_CODE);
		final int MainGroupID = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP_ID);
		final int MainGroup = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP);
		final int GroupID = ih.getColumnIndex(ORDER_BOOKING_GROUP_ID);
		final int GroupName = ih.getColumnIndex(ORDER_BOOKING_GROUP);
		final int SubGroupID = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_ID);
		final int SubGroup = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_NAME);
		final int MdApplicable = ih.getColumnIndex(ORDER_BOOKING_MD_APPLICABLE);
		final int SubItemApplicable = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_APPLICABLE);
		final int InProduction = ih.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION);
		final int SizeID = ih.getColumnIndex(ORDER_BOOKING_SIZE_ID);
		final int Size = ih.getColumnIndex(ORDER_BOOKING_SIZE_NAME);
		final int SizeSequence = ih.getColumnIndex(ORDER_BOOKING_SIZE_SEQUENCE);
		final int Barcode = ih.getColumnIndex(ORDER_BOOKING_BARCODE);
		final int Stock = ih.getColumnIndex(ORDER_BOOKING_STOCK);
		final int ReserveStock = ih.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK);
		final int FlagLocalStatus = ih.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS);
		final int Remarks = ih.getColumnIndex(ORDER_BOOKING_REMARKS);
		final int ImageStatus = ih.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS);
		final int ImageUrl = ih.getColumnIndex(ORDER_BOOKING_IMAGE_URL);
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {

			final int OrderID = ih.getColumnIndex(ORDER_BOOKING_ORDER_ID + i);
			final int Ord = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i);
			final int OrdPrevious = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i);
			final int ExpectedDate = ih.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i);
			final int Rate = ih.getColumnIndex(ORDER_BOOKING_RATE + i);
			final int Mrp = ih.getColumnIndex(ORDER_BOOKING_MRP + i);
			final int DiscountRate = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + i);
			final int DiscountPercentage = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + i);
			try {
				db.execSQL("PRAGMA synchronous=OFF");
				db.setLockingEnabled(false);
				for (int x = 0; x < list.size(); x++) {
					// ... Create the data for this row (not shown) ...
					// Get the InsertHelper ready to insert a single row
					ih.prepareForInsert();
					// Add the data for each column
					ih.bind(ItemID, list.get(x).get("ItemID"));
					ih.bind(ItemName, list.get(x).get("ItemName"));
					ih.bind(ItemCode, list.get(x).get("ItemCode"));
					ih.bind(MainGroup, list.get(x).get("MainGroup"));
					ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
					ih.bind(GroupID, list.get(x).get("GroupID"));
					ih.bind(GroupName, list.get(x).get("GroupName"));
					ih.bind(SubGroupID, list.get(x).get("SubGroupID"));
					ih.bind(SubGroup, list.get(x).get("SubGroup"));
					ih.bind(SubItemApplicable, list.get(x).get("SubItemApplicable"));
					ih.bind(MdApplicable, list.get(x).get("MdApplicable"));
					ih.bind(InProduction, list.get(x).get("InProduction"));
					ih.bind(Barcode, list.get(x).get("Barcode"));
					ih.bind(ColorID, list.get(x).get("ColorID"));
					ih.bind(Color, list.get(x).get("Color"));
					ih.bind(ColorFamily, list.get(x).get("ColorFamily"));
					ih.bind(ColorCode, list.get(x).get("ColorCode"));
					ih.bind(SizeID, list.get(x).get("SizeID"));
					ih.bind(Size, list.get(x).get("Size"));
					ih.bind(SizeSequence, list.get(x).get("SizeSequence"));
					ih.bind(Stock, list.get(x).get("Stock"));
					ih.bind(ReserveStock, list.get(x).get("ReserveStock"));
					ih.bind(FlagLocalStatus, list.get(x).get("FlagLocalStatus"));
					ih.bind(Remarks, list.get(x).get("Remarks"));
					ih.bind(ImageStatus, list.get(x).get("ImageStatus"));
					ih.bind(ImageUrl, list.get(x).get("ImageUrl"));

					ih.bind(OrderID, list.get(x).get("OrderID" + i));
					ih.bind(Ord, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(OrdPrevious, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(ExpectedDate, (list.get(x).get("ExpectedDate" + i) == null || list.get(x).get("ExpectedDate" + i).equals("null")) ? DateFormatsMethods.getDateTime() : list.get(x).get("ExpectedDate" + i));
					ih.bind(Rate, (list.get(x).get("Rate" + i) == null || list.get(x).get("Rate" + i).equals("null")) ? "0" : list.get(x).get("Rate" + i));
					ih.bind(Mrp, (list.get(x).get("Mrp" + i) == null || list.get(x).get("Mrp" + i).equals("null")) ? "0" : list.get(x).get("Mrp" + i));
					ih.bind(DiscountRate, (list.get(x).get("DiscountRate" + i) == null || list.get(x).get("DiscountRate" + i).equals("null")) ? list.get(x).get("Rate" + i) : list.get(x).get("DiscountRate" + i));
					ih.bind(DiscountPercentage, (list.get(x).get("DiscountPercentage" + i) == null || list.get(x).get("DiscountPercentage" + i).equals("null")) ? "0" : list.get(x).get("DiscountPercentage" + i));
					System.out.println("Discount Rate:"+list.get(x).get("DiscountRate" + i)+"\nDis:"+list.get(x).get("DiscountPercentage" + i));
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
	}
	//TODO: Out Of Stock
	public void insertOutOfStockTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, OUT_OF_STOCK_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int ItemID = ih.getColumnIndex(ORDER_BOOKING_ITEM_ID);
		final int ItemName = ih.getColumnIndex(ORDER_BOOKING_ITEM_NAME);
		final int ItemCode = ih.getColumnIndex(ORDER_BOOKING_ITEM_CODE);
		final int ColorID = ih.getColumnIndex(ORDER_BOOKING_COLOR_ID);
		final int ColorName = ih.getColumnIndex(ORDER_BOOKING_COLOR_NAME);
		final int SizeID = ih.getColumnIndex(ORDER_BOOKING_SIZE_ID);
		final int SizeName = ih.getColumnIndex(ORDER_BOOKING_SIZE_NAME);
		final int SizeSequence = ih.getColumnIndex(ORDER_BOOKING_SIZE_SEQUENCE);
		final int Stock = ih.getColumnIndex(ORDER_BOOKING_STOCK);
		final int Remarks = ih.getColumnIndex(ORDER_BOOKING_REMARKS);
		final int OrderID = ih.getColumnIndex(ORDER_BOOKING_ORDER_ID);
		final int BookQty = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY);
		final int ExpectedDate = ih.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE);
		final int Rate = ih.getColumnIndex(ORDER_BOOKING_RATE);
		final int Mrp = ih.getColumnIndex(ORDER_BOOKING_MRP);
		final int DiscountRate = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE);
		final int DisPercentage = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE);
		try {
			db.execSQL("PRAGMA synchronous=OFF");
			db.setLockingEnabled(false);
			for (int x = 0; x < list.size(); x++) {
				// ... Create the data for this row (not shown) ...
				// Get the InsertHelper ready to insert a single row
				ih.prepareForInsert();
				// Add the data for each column
				ih.bind(ItemID, list.get(x).get("ItemID"));
				ih.bind(ItemName, list.get(x).get("ItemName"));
				ih.bind(ItemCode, list.get(x).get("ItemCode"));
				ih.bind(ColorID, list.get(x).get("ColorID"));
				ih.bind(ColorName, list.get(x).get("ColorName"));
				ih.bind(SizeID, list.get(x).get("SizeID"));
				ih.bind(SizeName, list.get(x).get("SizeName"));
				ih.bind(SizeSequence, list.get(x).get("SizeSequence"));
				ih.bind(Stock, list.get(x).get("Stock"));
				ih.bind(Remarks, list.get(x).get("Remarks"));
				ih.bind(OrderID, list.get(x).get("OrderID"));
				ih.bind(BookQty, list.get(x).get("BookQty"));
				ih.bind(ExpectedDate, list.get(x).get("ExpectedDate"));
				ih.bind(Rate, list.get(x).get("Rate"));
				ih.bind(Mrp, list.get(x).get("Mrp"));
				ih.bind(DiscountRate, list.get(x).get("DiscountRate"));
				ih.bind(DisPercentage, list.get(x).get("DisPercentage"));
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
	//------------------------------------ TODO: Insert data into Subitem table-------------------------------------------------------------
	public void insertOrderBookingSubItemTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, ORDER_BOOKING_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int ItemID = ih.getColumnIndex(ORDER_BOOKING_ITEM_ID);
		final int ItemName = ih.getColumnIndex(ORDER_BOOKING_ITEM_NAME);
		final int ItemCode = ih.getColumnIndex(ORDER_BOOKING_ITEM_CODE);
		final int SubItemID = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID);
		final int SubItemName = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME);
		final int SubItemCode = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE);
		final int MainGroupID = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP_ID);
		final int MainGroup = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP);
		final int GroupID = ih.getColumnIndex(ORDER_BOOKING_GROUP_ID);
		final int GroupName = ih.getColumnIndex(ORDER_BOOKING_GROUP);
		final int SubGroupID = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_ID);
		final int SubGroup = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_NAME);
		final int InProduction = ih.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION);
		final int Barcode = ih.getColumnIndex(ORDER_BOOKING_BARCODE);
		final int Stock = ih.getColumnIndex(ORDER_BOOKING_STOCK);
		final int ReserveStock = ih.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK);
		final int FlagLocalStatus = ih.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS);
		final int Remarks = ih.getColumnIndex(ORDER_BOOKING_REMARKS);
		final int ImageStatus = ih.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS);
		final int ImageUrl = ih.getColumnIndex(ORDER_BOOKING_IMAGE_URL);
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {

			final int OrderID = ih.getColumnIndex(ORDER_BOOKING_ORDER_ID + i);
			final int Ord = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i);
			final int OrdPrevious = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i);
			final int ExpectedDate = ih.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i);
			final int Rate = ih.getColumnIndex(ORDER_BOOKING_RATE + i);
			final int Mrp = ih.getColumnIndex(ORDER_BOOKING_MRP + i);
			final int DiscountRate = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + i);
			final int DiscountPercentage = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + i);
			try {
				db.execSQL("PRAGMA synchronous=OFF");
				db.setLockingEnabled(false);
				for (int x = 0; x < list.size(); x++) {
					// ... Create the data for this row (not shown) ...
					// Get the InsertHelper ready to insert a single row
					ih.prepareForInsert();
					// Add the data for each column
					ih.bind(ItemID, list.get(x).get("ItemID"));
					ih.bind(ItemName, list.get(x).get("ItemName"));
					ih.bind(ItemCode, list.get(x).get("ItemCode"));
					ih.bind(MainGroup, list.get(x).get("MainGroup"));
					ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
					ih.bind(GroupID, list.get(x).get("GroupID"));
					ih.bind(GroupName, list.get(x).get("GroupName"));
					ih.bind(SubGroupID, list.get(x).get("SubGroupID"));
					ih.bind(SubGroup, list.get(x).get("SubGroup"));
					ih.bind(InProduction, list.get(x).get("InProduction"));
					ih.bind(Barcode, list.get(x).get("Barcode"));
					ih.bind(SubItemID, list.get(x).get("SubItemID"));
					ih.bind(SubItemName, list.get(x).get("SubItemName"));
					ih.bind(SubItemCode, list.get(x).get("SubItemCode"));
					ih.bind(Stock, list.get(x).get("Stock"));
					ih.bind(ReserveStock, list.get(x).get("ReserveStock"));
					ih.bind(FlagLocalStatus, list.get(x).get("FlagLocalStatus"));
					ih.bind(Remarks, list.get(x).get("Remarks"));
					ih.bind(ImageStatus, list.get(x).get("ImageStatus"));
					ih.bind(ImageUrl, list.get(x).get("ImageUrl"));

					ih.bind(OrderID, list.get(x).get("OrderID" + i));
					ih.bind(Ord, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(OrdPrevious, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(ExpectedDate, (list.get(x).get("ExpectedDate" + i) == null || list.get(x).get("ExpectedDate" + i).equals("null")) ? DateFormatsMethods.getDateTime() : list.get(x).get("ExpectedDate" + i));
					ih.bind(Rate, (list.get(x).get("Rate" + i) == null || list.get(x).get("Rate" + i).equals("null")) ? "0" : list.get(x).get("Rate" + i));
					ih.bind(Mrp, (list.get(x).get("Mrp" + i) == null || list.get(x).get("Mrp" + i).equals("null")) ? "0" : list.get(x).get("Mrp" + i));
					ih.bind(DiscountRate, (list.get(x).get("DiscountRate" + i) == null || list.get(x).get("DiscountRate" + i).equals("null")) ? list.get(x).get("Rate" + i) : list.get(x).get("DiscountRate" + i));
					ih.bind(DiscountPercentage, (list.get(x).get("DiscountPercentage" + i) == null || list.get(x).get("DiscountPercentage" + i).equals("null")) ? "0" : list.get(x).get("DiscountPercentage" + i));
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
	}
	//TODO: Out Of Stock
	public void insertOutOfStockSubItemTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, OUT_OF_STOCK_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int ItemID = ih.getColumnIndex(ORDER_BOOKING_ITEM_ID);
		final int ItemName = ih.getColumnIndex(ORDER_BOOKING_ITEM_NAME);
		final int ItemCode = ih.getColumnIndex(ORDER_BOOKING_ITEM_CODE);
		final int SubItemID = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID);
		final int SubItemName = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME);
		final int SubItemCode = ih.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE);
		final int Stock = ih.getColumnIndex(ORDER_BOOKING_STOCK);
		final int Remarks = ih.getColumnIndex(ORDER_BOOKING_REMARKS);
		final int OrderID = ih.getColumnIndex(ORDER_BOOKING_ORDER_ID);
		final int BookQty = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY);
		final int ExpectedDate = ih.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE);
		final int Rate = ih.getColumnIndex(ORDER_BOOKING_RATE);
		final int Mrp = ih.getColumnIndex(ORDER_BOOKING_MRP);
		final int DiscountRate = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE);
		final int DisPercentage = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE);
		try {
			db.execSQL("PRAGMA synchronous=OFF");
			db.setLockingEnabled(false);
			for (int x = 0; x < list.size(); x++) {
				// ... Create the data for this row (not shown) ...
				// Get the InsertHelper ready to insert a single row
				ih.prepareForInsert();
				// Add the data for each column
				ih.bind(ItemID, list.get(x).get("ItemID"));
				ih.bind(ItemName, list.get(x).get("ItemName"));
				ih.bind(ItemCode, list.get(x).get("ItemCode"));
				ih.bind(SubItemID, list.get(x).get("SubItemID"));
				ih.bind(SubItemName, list.get(x).get("SubItemName"));
				ih.bind(SubItemCode, list.get(x).get("SubItemCode"));
				ih.bind(Stock, list.get(x).get("Stock"));
				ih.bind(Remarks, list.get(x).get("Remarks"));
				ih.bind(OrderID, list.get(x).get("OrderID"));
				ih.bind(BookQty, list.get(x).get("BookQty"));
				ih.bind(ExpectedDate, list.get(x).get("ExpectedDate"));
				ih.bind(Rate, list.get(x).get("Rate"));
				ih.bind(Mrp, list.get(x).get("Mrp"));
				ih.bind(DiscountRate, list.get(x).get("DiscountRate"));
				ih.bind(DisPercentage, list.get(x).get("DisPercentage"));
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
	//------------------------------------ TODO: Insert data into Without subitem or item only table-------------------------------------------------------------
	public void insertOrderBookingWithoutSubItemTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, ORDER_BOOKING_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int ItemID = ih.getColumnIndex(ORDER_BOOKING_ITEM_ID);
		final int ItemName = ih.getColumnIndex(ORDER_BOOKING_ITEM_NAME);
		final int ItemCode = ih.getColumnIndex(ORDER_BOOKING_ITEM_CODE);
		final int MainGroupID = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP_ID);
		final int MainGroup = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP);
		final int GroupID = ih.getColumnIndex(ORDER_BOOKING_GROUP_ID);
		final int GroupName = ih.getColumnIndex(ORDER_BOOKING_GROUP);
		final int SubGroupID = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_ID);
		final int SubGroup = ih.getColumnIndex(ORDER_BOOKING_SUB_GROUP_NAME);
		final int InProduction = ih.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION);
		final int Barcode = ih.getColumnIndex(ORDER_BOOKING_BARCODE);
		final int Stock = ih.getColumnIndex(ORDER_BOOKING_STOCK);
		final int ReserveStock = ih.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK);
		final int FlagLocalStatus = ih.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS);
		final int Remarks = ih.getColumnIndex(ORDER_BOOKING_REMARKS);
		final int ImageStatus = ih.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS);
		final int ImageUrl = ih.getColumnIndex(ORDER_BOOKING_IMAGE_URL);
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {

			final int OrderID = ih.getColumnIndex(ORDER_BOOKING_ORDER_ID + i);
			final int Ord = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i);
			final int OrdPrevious = ih.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i);
			final int ExpectedDate = ih.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i);
			final int Rate = ih.getColumnIndex(ORDER_BOOKING_RATE + i);
			final int Mrp = ih.getColumnIndex(ORDER_BOOKING_MRP + i);
			final int DiscountRate = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + i);
			final int DiscountPercentage = ih.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + i);
			try {
				db.execSQL("PRAGMA synchronous=OFF");
				db.setLockingEnabled(false);
				for (int x = 0; x < list.size(); x++) {
					// ... Create the data for this row (not shown) ...
					// Get the InsertHelper ready to insert a single row
					ih.prepareForInsert();
					// Add the data for each column
					ih.bind(ItemID, list.get(x).get("ItemID"));
					ih.bind(ItemName, list.get(x).get("ItemName"));
					ih.bind(ItemCode, list.get(x).get("ItemCode"));
					ih.bind(MainGroup, list.get(x).get("MainGroup"));
					ih.bind(MainGroupID, list.get(x).get("MainGroupID"));
					ih.bind(GroupID, list.get(x).get("GroupID"));
					ih.bind(GroupName, list.get(x).get("GroupName"));
					ih.bind(SubGroupID, list.get(x).get("SubGroupID"));
					ih.bind(SubGroup, list.get(x).get("SubGroup"));
					ih.bind(InProduction, list.get(x).get("InProduction"));
					ih.bind(Barcode, list.get(x).get("Barcode"));
					ih.bind(Stock, list.get(x).get("Stock"));
					ih.bind(ReserveStock, list.get(x).get("ReserveStock"));
					ih.bind(FlagLocalStatus, list.get(x).get("FlagLocalStatus"));
					ih.bind(Remarks, list.get(x).get("Remarks"));
					ih.bind(ImageStatus, list.get(x).get("ImageStatus"));
					ih.bind(ImageUrl, list.get(x).get("ImageUrl"));

					ih.bind(OrderID, list.get(x).get("OrderID" + i));
					ih.bind(Ord, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(OrdPrevious, (list.get(x).get("Ord" + i) == null || list.get(x).get("Ord" + i).equals("null")) ? "0" : list.get(x).get("Ord" + i));
					ih.bind(ExpectedDate, (list.get(x).get("ExpectedDate" + i) == null || list.get(x).get("ExpectedDate" + i).equals("null")) ? DateFormatsMethods.getDateTime() : list.get(x).get("ExpectedDate" + i));
					ih.bind(Rate, (list.get(x).get("Rate" + i) == null || list.get(x).get("Rate" + i).equals("null")) ? "0" : list.get(x).get("Rate" + i));
					ih.bind(Mrp, (list.get(x).get("Mrp" + i) == null || list.get(x).get("Mrp" + i).equals("null")) ? "0" : list.get(x).get("Mrp" + i));
					ih.bind(DiscountRate, (list.get(x).get("DiscountRate" + i) == null || list.get(x).get("DiscountRate" + i).equals("null")) ? "0" : list.get(x).get("DiscountRate" + i));
					ih.bind(DiscountPercentage, (list.get(x).get("DiscountPercentage" + i) == null || list.get(x).get("DiscountPercentage" + i).equals("null")) ? "0" : list.get(x).get("DiscountPercentage" + i));
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
	}
	//------------------------------------ TODO: Insert data into SizeSet table-------------------------------------------------------------
	public void insertSizeSetTable(List<Map<String, String>> list) {
		final long startTime = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, SINGLA_SIZE_SET_TABLE);
		// Get the numeric indexes for each of the columns that we're updating
		final int MainGroupID = ih.getColumnIndex(ORDER_BOOKING_MAIN_GROUP_ID);
		final int GroupID = ih.getColumnIndex(ORDER_BOOKING_GROUP_ID);
		final int SizeCount = ih.getColumnIndex(SINGLA_SIZE_COUNT);
		final int Required = ih.getColumnIndex(SINGLA_REQUIRED);
		try {
			db.execSQL("PRAGMA synchronous=OFF");
			db.setLockingEnabled(false);
			for (int x = 0; x < list.size(); x++) {
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
		} finally {
			db.setLockingEnabled(true);
			db.execSQL("PRAGMA synchronous=NORMAL");
			ih.close();  // See comment below from Stefan Anca
			final long endtime = System.currentTimeMillis();
			Log.e("Time:", "" + String.valueOf(endtime - startTime));
		}
	}
	//TODO: Size Set method
    public int getRequiredBySizeSet(int SizeCount) {
        int required = 0;
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + SINGLA_REQUIRED + " from " + SINGLA_SIZE_SET_TABLE + " WHERE " + SINGLA_SIZE_COUNT + "=" + SizeCount + " ";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
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

	//-----------------------------------TODO: Multi Details Methods--------------------------------------------------
	//TODO: Getting Color list of Order Booking Table
	public String[] getDefaultColorID() {
		String[] ColorID = new String[2];
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_COLOR_ID + "," + ORDER_BOOKING_BARCODE + " from " + ORDER_BOOKING_TABLE + " WHERE " + ORDER_BOOKING_BARCODE + "='" + BarcodeSearchViewPagerActivity.Barcode + "' ";
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				ColorID[0] = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID));
				ColorID[1] = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE));
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning
		return ColorID;
	}
	public List<Map<String, String>> getColorList() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_COLOR_ID + " , " + ORDER_BOOKING_COLOR_NAME + ", " + ORDER_BOOKING_COLOR_FAMILY_NAME + ", " + ORDER_BOOKING_COLOR_CODE + "," + ORDER_BOOKING_ITEM_ID + "," +ORDER_BOOKING_IMAGE_STATUS+","+ORDER_BOOKING_IMAGE_URL+" from " + ORDER_BOOKING_TABLE +" ORDER BY "+ORDER_BOOKING_COLOR_NAME+" ASC";
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("ColorFamilyName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_FAMILY_NAME)));
				map.put("ColorCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_CODE)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("Barcode", "");//cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("ImageStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS)));
				map.put("ImageUrl", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_URL)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		String ColorID = "", ColorName = "", ColorFamilyName = "", ColorCode = "", ItemID = "", Barcode = "",ImageStatus = "",ImageUrl = "", tColorID = "", tColorName = "", tColorFamilyName = "", tColorCode = "", tItemID = "", tBarcode = "",tImageStatus = "",tImageUrl = "";
		if (getDefaultColorID()[0] != null) {
			ColorID = getDefaultColorID()[0];
			for (int i = 0; i < dataList.size(); i++) {
				if (ColorID.equals(dataList.get(i).get("ColorID")) && i > 0) {
					ColorName = dataList.get(i).get("ColorName");
					ColorFamilyName = dataList.get(i).get("ColorFamilyName");
					ColorCode = dataList.get(i).get("ColorCode");
					ItemID = dataList.get(i).get("ItemID");
					Barcode = dataList.get(i).get("Barcode");
					ImageStatus = dataList.get(i).get("ImageStatus");
					ImageUrl = dataList.get(i).get("ImageUrl");
					tColorID = dataList.get(0).get("ColorID");
					tColorName = dataList.get(0).get("ColorName");
					tColorFamilyName = dataList.get(0).get("ColorFamilyName");
					tColorCode = dataList.get(0).get("ColorCode");
					tItemID = dataList.get(0).get("ItemID");
					tBarcode = dataList.get(0).get("Barcode");
					tImageStatus = dataList.get(0).get("ImageStatus");
					tImageUrl = dataList.get(0).get("ImageUrl");
					Map<String, String> map = new HashMap<String, String>();
					map.put("ColorID", ColorID);
					map.put("ColorName", ColorName);
					map.put("ColorFamilyName", ColorFamilyName);
					map.put("ColorCode", ColorCode);
					map.put("ItemID", ItemID);
					map.put("Barcode", Barcode);
					map.put("ImageStatus", ImageStatus);
					map.put("ImageUrl", ImageUrl);
					dataList.set(0, map);
					Map<String, String> map2 = new HashMap<String, String>();
					map2.put("ColorID", tColorID);
					map2.put("ColorName", tColorName);
					map2.put("ColorFamilyName", tColorFamilyName);
					map2.put("ColorCode", tColorCode);
					map2.put("ItemID", tItemID);
					map2.put("Barcode", tBarcode);
					map2.put("ImageStatus", tImageStatus);
					map2.put("ImageUrl", tImageUrl);
					dataList.set(i, map2);
				}
			}
		}
		//System.out.println("Color:"+dataList.toString());
		// returning
		return dataList;
	}
	public List<Map<String, String>> getColorListAll() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		int c = 0;
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_COLOR_ID + " , " + ORDER_BOOKING_COLOR_NAME + ", " + ORDER_BOOKING_COLOR_FAMILY_NAME + ", " + ORDER_BOOKING_COLOR_CODE + "," + ORDER_BOOKING_ITEM_ID + "," +ORDER_BOOKING_IMAGE_STATUS+","+ORDER_BOOKING_IMAGE_URL+" from " + ORDER_BOOKING_TABLE +" ORDER BY "+ORDER_BOOKING_COLOR_NAME+" ASC";
		//System.out.println(selectQuery);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ColorID", "ALL");
		map.put("ColorName", "ALL");
		map.put("ColorFamilyName", "ALL");
		map.put("ColorCode", "ALL");
		map.put("ItemID", "ALL");
		map.put("Barcode", "ALL");
		map.put("ImageStatus", "0");
		map.put("ImageUrl", "ALL");
		dataList.add(c,map);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				c++;
				map = new HashMap<String, String>();
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("ColorFamilyName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_FAMILY_NAME)));
				map.put("ColorCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_CODE)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("Barcode", "");//cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("ImageStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS)));
				map.put("ImageUrl", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_URL)));
				dataList.add(c,map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning
		return dataList;
	}
	//TODO: Getting Item Details of Order Booking Table
	public Map<String, String> getItemDetails(String ColorID) {
		Map<String, String> map = new HashMap<String, String>();
		String Barcode = "";
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ITEM_ID + "," + ORDER_BOOKING_ITEM_CODE + "," + ORDER_BOOKING_ITEM_NAME + "," + ORDER_BOOKING_GROUP_ID + "," + ORDER_BOOKING_GROUP + "," + ORDER_BOOKING_MAIN_GROUP + "," + ORDER_BOOKING_SUB_GROUP_NAME + "," + ORDER_BOOKING_BARCODE + "," + ORDER_BOOKING_REMARKS + " from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String barcode = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE));
				if (getDefaultColorID()[1] != null && barcode.equals(getDefaultColorID()[1])) {
					Barcode = barcode;
				}
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MAIN_GROUP)));
				map.put("Barcode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("Remarks", (getRemarksByColor(ColorID).isEmpty() || getRemarksByColor(ColorID)==null ? "" : getRemarksByColor(ColorID).get("Remarks")));
				//System.out.println("map:"+map.toString());
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//map.put("Barcode", Barcode);
		// returning lables
		return map;
	}
	//TODO: Remarks Color wise
	public Map<String, String> getRemarksByColor(String ColorID) {
		Map<String, String> map = new HashMap<String, String>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_REMARKS + " from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' AND "+ORDER_BOOKING_REMARKS+"!='' LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//do {
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				//System.out.println("map:"+map.toString());
			//} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("Remarks map:"+map.toString());
		// returning lables
		return map;
	}
	//TODO: Getting Data of Order SIZE LIST Table
	public List<Map<String, String>> getSizeList(String itemID, String ColorID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_SIZE_ID + "," + ORDER_BOOKING_SIZE_NAME + "," + ORDER_BOOKING_STOCK + "," + ORDER_BOOKING_RESERVE_STOCK + "," + ORDER_BOOKING_IN_PRODUCTION + " , "+ORDER_BOOKING_BARCODE+"  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + "  ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("ReserveStock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK)));
				map.put("InProduction", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION)));
				map.put("Barcode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getSizeListWithoutItemID() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_SIZE_ID + "," + ORDER_BOOKING_SIZE_NAME + "  from " + ORDER_BOOKING_TABLE + " ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + "  ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
//				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
//				map.put("ReserveStock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK)));
//				map.put("InProduction", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	//TODO: Getting Expected Delivery Datetime by Color
	public Map<String, String> getExpectedDeliveryDatetime(String OrderID, int PostFix, String itemID) {
		Map<String, String> map = new HashMap<String, String>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND "+ ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "!='' LIMIT 1";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("Map:"+map.toString());
		// returning lables
		return map;
	}
	//TODO: Getting Data of Order Quantity List Table
	public List<Map<String, String>> getQuantityList(String OrderID, int PostFix, String itemID, String ColorID, String SizeID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ORDER_ID + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix + "," + ORDER_BOOKING_RATE + PostFix + "," + ORDER_BOOKING_MRP + PostFix + "," + ORDER_BOOKING_DISCOUNT_RATE + PostFix + "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix + "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + "  ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + PostFix)));
				map.put("Ord" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix)));
				map.put("OrdPrevious" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix)));
				map.put("Rate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE + PostFix)));
				map.put("Mrp" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP + PostFix)));
				map.put("DiscountRate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + PostFix)));
				map.put("DisPercentage" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList.toString());
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getQuantityListdemo(String OrderID, int PostFix, String itemID, String ColorID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ORDER_ID + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix + "," + ORDER_BOOKING_RATE + PostFix + "," + ORDER_BOOKING_MRP + PostFix + "," + ORDER_BOOKING_DISCOUNT_RATE + PostFix + "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix + "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + " DESC LIMIT 1";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + PostFix)));
				map.put("Ord" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix)));
				map.put("OrdPrevious" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix)));
				map.put("Rate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE + PostFix)));
				map.put("Mrp" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP + PostFix)));
				map.put("DiscountRate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + PostFix)));
				map.put("DisPercentage" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList.toString());
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getQuantityListWithoutColorID(String OrderID, int PostFix, String SizeID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ORDER_ID + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix + "," + ORDER_BOOKING_RATE + PostFix + "," + ORDER_BOOKING_MRP + PostFix + "," + ORDER_BOOKING_DISCOUNT_RATE + PostFix + "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix + "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + "  ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + PostFix)));
				map.put("Ord" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix)));
				map.put("OrdPrevious" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix)));
				map.put("Rate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE + PostFix)));
				map.put("Mrp" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP + PostFix)));
				map.put("DiscountRate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + PostFix)));
				map.put("DisPercentage" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList);
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getRemarksOrExpectedDatetime(String OrderID, int PostFix, String itemID, String ColorID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "," + ORDER_BOOKING_REMARKS + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String ExDatetime = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix));
				if (ExDatetime.equals(StaticValues.DateTimeDummy)){

				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("Remark" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList);
		// returning lables
		return dataList;
	}
	//TODO: Update Quantity of Order Booking Table
	public void updateQty(String OrderID, String ItemID, String ColorID, String SizeID, int BookedQty, String ExDelDate, int postFix,String Remarks) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix, BookedQty);
		values.put(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + postFix, ExDelDate);
		values.put(ORDER_BOOKING_REMARKS, Remarks);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ", null);
		db.close();
		//getQuantityListdemo(OrderID, postFix, ItemID, ColorID);
	}
	public void updateQtyColorWise(String OrderID, String SizeID, int BookedQty, String ExDelDate, int postFix,String Remarks) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix, BookedQty);
		values.put(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + postFix, ExDelDate);
		values.put(ORDER_BOOKING_REMARKS, Remarks);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ", null);
		db.close();
		//System.out.println("Values:"+values.toString());
		//System.out.println("List:"+getQuantityListWithoutColorID(OrderID, postFix,SizeID).toString());
	}
	//TODO: Getting Booked item details of Order Booking Table
	public List<Map<String, String>> getAllOrderDetails() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		//TODO: Partial Query
		String PartialQry = "";
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
			PartialQry += (i == 0 ? "" : " OR ") + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " != " + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i;
		}
		// Select All Query
		String selectQuery = "SELECT DISTINCT * from " + ORDER_BOOKING_TABLE+ " where " + PartialQry;
		//System.out.println("Query:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				String OrderID = "", Ords = "", OrdsPrevious = "", ExDelDates = "",Rate = "",Mrp = "",DiscountRate = "",DisPercentage = "";
				for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
					OrderID += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) + "|");
					Ords += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) + "|");
					Rate += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE+i))+"|");
					Mrp += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP+i))+"|");
					DiscountRate += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE+i))+"|");
					DisPercentage += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE+i))+"|");
					ExDelDates += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) + "|");
				}
				map.put("OrderID", OrderID.substring(0, OrderID.length() - 1));
				map.put("BookQty", Ords.substring(0, Ords.length() - 1));
				map.put("Rate", Rate.substring(0,Rate.length()-1));
				map.put("Mrp", Mrp.substring(0,Mrp.length()-1));
				map.put("DiscountRate", DiscountRate.substring(0,DiscountRate.length()-1));
				map.put("DisPercentage", DisPercentage.substring(0,DisPercentage.length()-1));
				map.put("ExpectedDate", ExDelDates.substring(0, ExDelDates.length() - 1));
				dataList.add(map);
				//map.put("Remark", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARK)));
				//map.put("FlagLocalStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS)));

			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
			// returning lables
		}
		return dataList;
	}
	//TODO: Update expected delivery datetime of Order Booking Table
	public void UpdateExpectedDelDateTime(String OrderID, String ItemID, String ExpectedDate, int PostFix) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix, ExpectedDate);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "'", null);
		db.close();
		System.out.println("Ex:"+ExpectedDate);
	}
	//TODO: Update Remarks of Order Booking Table
	public void UpdateRemarks(String OrderID, String ItemID, String Remarks, int PostFix) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(ORDER_BOOKING_REMARKS + PostFix, Remarks);
		values.put(ORDER_BOOKING_REMARKS, Remarks);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "'", null);
		db.close();
	}
	//TODO: -----------------------------------------Out of Stock table Methods--------------------------------------------------
	public List<Map<String, String>> getColorListOOS() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_COLOR_ID + " , " + ORDER_BOOKING_COLOR_NAME + ","+ ORDER_BOOKING_ITEM_ID + " from " + OUT_OF_STOCK_TABLE;
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		String ColorID = "", ColorName = "", ColorFamilyName = "", ColorCode = "", ItemID = "", Barcode = "", tColorID = "", tColorName = "", tColorFamilyName = "", tColorCode = "", tItemID = "", tBarcode = "";
		if (getDefaultColorID()[0] != null) {
			ColorID = getDefaultColorID()[0];
			for (int i = 0; i < dataList.size(); i++) {
				if (ColorID.equals(dataList.get(i).get("ColorID")) && i > 0) {
					ColorName = dataList.get(i).get("ColorName");
					ColorFamilyName = dataList.get(i).get("ColorFamilyName");
					ColorCode = dataList.get(i).get("ColorCode");
					ItemID = dataList.get(i).get("ItemID");
					Barcode = dataList.get(i).get("Barcode");
					tColorID = dataList.get(0).get("ColorID");
					tColorName = dataList.get(0).get("ColorName");
					tColorFamilyName = dataList.get(0).get("ColorFamilyName");
					tColorCode = dataList.get(0).get("ColorCode");
					tItemID = dataList.get(0).get("ItemID");
					tBarcode = dataList.get(0).get("Barcode");
					Map<String, String> map = new HashMap<String, String>();
					map.put("ColorID", ColorID);
					map.put("ColorName", ColorName);
					map.put("ColorFamilyName", ColorFamilyName);
					map.put("ColorCode", ColorCode);
					map.put("ItemID", ItemID);
					map.put("Barcode", Barcode);
					dataList.set(0, map);
					Map<String, String> map2 = new HashMap<String, String>();
					map2.put("ColorID", tColorID);
					map2.put("ColorName", tColorName);
					map2.put("ColorFamilyName", tColorFamilyName);
					map2.put("ColorCode", tColorCode);
					map2.put("ItemID", tItemID);
					map2.put("Barcode", tBarcode);
					dataList.set(i, map2);
				}
			}
		}
		// returning
		return dataList;
	}
	public List<Map<String, String>> getSizeListOOS(String itemID, String ColorID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_SIZE_ID + "," + ORDER_BOOKING_SIZE_NAME + "," + ORDER_BOOKING_STOCK + "," + ORDER_BOOKING_RATE + "," + ORDER_BOOKING_MRP + "  from " + OUT_OF_STOCK_TABLE + " where " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' ORDER BY " + ORDER_BOOKING_SIZE_SEQUENCE + "  ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getAllDetailsOOS() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT "+ORDER_BOOKING_COLOR_ID+","+ORDER_BOOKING_COLOR_NAME+","+ORDER_BOOKING_ITEM_ID+","+ORDER_BOOKING_ITEM_CODE+","+ORDER_BOOKING_SIZE_ID+","+ORDER_BOOKING_SIZE_NAME+","+ORDER_BOOKING_STOCK+","+ORDER_BOOKING_RATE+","+ORDER_BOOKING_MRP+","+ORDER_BOOKING_DISCOUNT_RATE+","+ORDER_BOOKING_DISCOUNT_PERCENTAGE+"  from " + OUT_OF_STOCK_TABLE;
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("Out:"+dataList.toString());
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getOrderDetailsOOS(String OrderID,String ColorID,String SizeID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT *  from " + OUT_OF_STOCK_TABLE + " WHERE " + ORDER_BOOKING_ORDER_ID + "='" + OrderID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "'  AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID)));
				map.put("BookQty", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY)));
				map.put("ExpectedDate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	public void updateQtyOOS(String OrderID, String ColorID, String SizeID, String BookedQty) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY , BookedQty);
		db.update(OUT_OF_STOCK_TABLE, values, ORDER_BOOKING_ORDER_ID + "='" + OrderID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ", null);
		db.close();
	}
	public List<Map<String, String>> getOutOfStockDetails() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT "+ORDER_BOOKING_ORDER_ID+","+ORDER_BOOKING_COLOR_ID+","+ORDER_BOOKING_COLOR_NAME+","+ORDER_BOOKING_ITEM_ID+","+ORDER_BOOKING_ITEM_CODE+","+ORDER_BOOKING_SIZE_ID+","+ORDER_BOOKING_SIZE_NAME+","+ORDER_BOOKING_EXPECTED_DELIVERY_DATE+","+ORDER_BOOKING_RATE+","+ORDER_BOOKING_MRP+","+ORDER_BOOKING_DISCOUNT_RATE+","+ORDER_BOOKING_DISCOUNT_PERCENTAGE+","+ORDER_BOOKING_ORDER_BOOKED_QTY+","+ORDER_BOOKING_REMARKS+"  from " + OUT_OF_STOCK_TABLE;
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID)));
				map.put("ColorID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_ID)));
				map.put("ColorName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_COLOR_NAME)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("SizeID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_ID)));
				map.put("SizeName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SIZE_NAME)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				map.put("BookQty", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY)));
				map.put("ExpectedDate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("OutOfStock:"+dataList.toString());
		// returning lables
		return dataList;
	}
	//TODO: -----------------------------------------Out of Stock table Sub Item Methods--------------------------------------------------
	public List<Map<String, String>> getAllDetailsOOSSubItem() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT "+ORDER_BOOKING_SUB_ITEM_ID+","+ORDER_BOOKING_SUB_ITEM_NAME+","+ORDER_BOOKING_SUB_ITEM_CODE+","+ORDER_BOOKING_ITEM_ID+","+ORDER_BOOKING_ITEM_CODE+","+ORDER_BOOKING_STOCK+","+ORDER_BOOKING_RATE+","+ORDER_BOOKING_MRP+","+ORDER_BOOKING_DISCOUNT_RATE+","+ORDER_BOOKING_DISCOUNT_PERCENTAGE+"  from " + OUT_OF_STOCK_TABLE;
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SubItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID)));
				map.put("SubItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME)));
				map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("Out:"+dataList.toString());
		// returning lables
		return dataList;
	}
	public List<Map<String, String>> getOrderDetailsOOSSubItem(String OrderID,String ItemID,String SubItemID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT *  from " + OUT_OF_STOCK_TABLE + " WHERE " + ORDER_BOOKING_ORDER_ID + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "'  AND " + ORDER_BOOKING_SUB_ITEM_ID + "='" + SubItemID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID)));
				map.put("BookQty", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY)));
				map.put("ExpectedDate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	public void updateQtyOOSSubItem(String OrderID, String ItemID, String SubItemID, String BookedQty) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY , BookedQty);
		db.update(OUT_OF_STOCK_TABLE, values, ORDER_BOOKING_ORDER_ID + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' AND " + ORDER_BOOKING_SUB_ITEM_ID + "='" + SubItemID + "'", null);
		db.close();
	}
	public List<Map<String, String>> getOutOfStockSubItemDetails() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT "+ORDER_BOOKING_ORDER_ID+","+ORDER_BOOKING_ITEM_ID+","+ORDER_BOOKING_ITEM_CODE+","+ORDER_BOOKING_SUB_ITEM_ID+","+ORDER_BOOKING_SUB_ITEM_NAME+","+ORDER_BOOKING_SUB_ITEM_CODE+","+ORDER_BOOKING_EXPECTED_DELIVERY_DATE+","+ORDER_BOOKING_RATE+","+ORDER_BOOKING_MRP+","+ORDER_BOOKING_DISCOUNT_RATE+","+ORDER_BOOKING_DISCOUNT_PERCENTAGE+","+ORDER_BOOKING_ORDER_BOOKED_QTY+","+ORDER_BOOKING_REMARKS+"  from " + OUT_OF_STOCK_TABLE;
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID)));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("SubItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID)));
				map.put("SubItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME)));
				map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE)));
				map.put("Rate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE)));
				map.put("Mrp", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP)));
				map.put("DiscountRate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE)));
				map.put("DisPercentage", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE)));
				map.put("BookQty", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY)));
				map.put("ExpectedDate", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("OutOfStock:"+dataList.toString());
		// returning lables
		return dataList;
	}
	//TODO: -----------------------------------------SubItem Methods--------------------------------------------------

	//TODO: Getting SubItem Details of Order Booking Table---------------------------------------
	public Map<String, String> getSubItemDetails() {
		Map<String, String> map = new HashMap<String, String>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ITEM_ID + "," + ORDER_BOOKING_ITEM_CODE + "," + ORDER_BOOKING_ITEM_NAME + "," + ORDER_BOOKING_GROUP_ID + "," + ORDER_BOOKING_GROUP + "," + ORDER_BOOKING_MAIN_GROUP + "," + ORDER_BOOKING_SUB_GROUP_NAME + "," + ORDER_BOOKING_BARCODE + " from " + ORDER_BOOKING_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//do {
				String ItemID = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID));
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MAIN_GROUP)));
				map.put("Barcode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("Remarks", (getRemarksBySubItem(ItemID).isEmpty() || getRemarksBySubItem(ItemID)==null ? "" : getRemarksBySubItem(ItemID).get("Remarks")));
			//} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//map.put("Barcode", Barcode);
		// returning lables
		return map;
	}
	//TODO: Remarks Subitem wise
	public Map<String, String> getRemarksBySubItem(String ItemID) {
		Map<String, String> map = new HashMap<String, String>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_REMARKS + " from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ITEM_ID+ "='" + ItemID + "' AND "+ORDER_BOOKING_REMARKS+"!='' LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//do {
			map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
			//System.out.println("map:"+map.toString());
			//} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("Remarks map:"+map.toString());
		// returning lables
		return map;
	}
	//----------------------------------TODO: Getting Data of Order Subitem LIST Table---------------------------------------
	public List<Map<String, String>> getSubItemList(String itemID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_SUB_ITEM_NAME + "," + ORDER_BOOKING_SUB_ITEM_ID + "," + ORDER_BOOKING_SUB_ITEM_CODE + "," + ORDER_BOOKING_SUB_ITEM_NAME + "," + ORDER_BOOKING_STOCK + "," + ORDER_BOOKING_BARCODE + "," + ORDER_BOOKING_RESERVE_STOCK + "," + ORDER_BOOKING_IN_PRODUCTION + ","+ORDER_BOOKING_IMAGE_STATUS+","+ORDER_BOOKING_IMAGE_URL+"  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' ORDER BY "+ORDER_BOOKING_IMAGE_STATUS+" DESC ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SubItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID)));
				map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE)));
				map.put("SubItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("Barcode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("ReserveStock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK)));
				map.put("InProduction", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION)));
				map.put("ImageStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS)));
				map.put("ImageUrl", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_URL)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}

	//----------------------------------TODO: Getting Data of Order Subitem Quantity List Table---------------------------------------
	public List<Map<String, String>> getSubItemQuantityList(String OrderID, int PostFix, String itemID, String SubItemID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ORDER_ID + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix + "," + ORDER_BOOKING_RATE + PostFix + "," + ORDER_BOOKING_MRP + PostFix + "," + ORDER_BOOKING_DISCOUNT_RATE + PostFix + "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix + "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' AND " + ORDER_BOOKING_SUB_ITEM_ID + "='" + SubItemID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + PostFix)));
				map.put("Ord" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix)));
				map.put("OrdPrevious" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix)));
				map.put("Rate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE + PostFix)));
				map.put("Mrp" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP + PostFix)));
				map.put("DiscountRate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + PostFix)));
				map.put("DisPercentage" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList);
		// returning lables
		return dataList;
	}

	//----------------------------------TODO: Update SubItem Quantity of Order Booking Table---------------------------------------
	public void updateSubItemQty(String OrderID, String ItemID, String SubItemID, String BookedQty, String ExDelDate, int postFix,String Remarks) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix, BookedQty);
		values.put(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + postFix, ExDelDate);
		values.put(ORDER_BOOKING_REMARKS, Remarks);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' AND " + ORDER_BOOKING_SUB_ITEM_ID + "='" + SubItemID + "' ", null);
		db.close();
	}
	//----------------------------------TODO: Getting Booked subitem details of Order Booking Table---------------------------------------
	public List<Map<String, String>> getAllOrderSubItemDetails() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		//TODO: Partial Query
		String PartialQry = "";
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
			PartialQry += (i == 0 ? "" : " OR ") + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " != " + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i;
		}
		// Select All Query
		String selectQuery = "SELECT DISTINCT * from " + ORDER_BOOKING_TABLE + " where " + PartialQry;
		//System.out.println("Query:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("SubItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_ID)));
				map.put("SubItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_NAME)));
				map.put("SubItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_SUB_ITEM_CODE)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				String OrderID = "", Ords = "", Rate = "", Mrp = "", ExDelDates = "",DiscountRate = "",DisPercentage = "";
				for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
					OrderID += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) + "|");
					Ords += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) + "|");
					Rate += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE+i))==null ? "0" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE+i))+"|");
					Mrp += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP+i))==null ? "0" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP+i))+"|");
					DiscountRate += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE+i))+"|");
					DisPercentage += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE+i))+"|");
					ExDelDates += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) + "|");
				}
				map.put("OrderID", OrderID.substring(0, OrderID.length() - 1));
				map.put("BookQty", Ords.substring(0, Ords.length() - 1));
				map.put("Rate", Rate.substring(0,Rate.length()-1));
				map.put("DiscountRate", DiscountRate.substring(0,DiscountRate.length()-1));
				map.put("DisPercentage", DisPercentage.substring(0,DisPercentage.length()-1));
				map.put("ExpectedDate", ExDelDates.substring(0, ExDelDates.length() - 1));
				dataList.add(map);
				//map.put("Remark", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARK)));
				//map.put("FlagLocalStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS)));

			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
			// returning lables
		}
		return dataList;
	}


	//TODO: -------------------------------------Without SubItem Methods--------------------------------------------------

	//----------------------------------TODO: Getting  Without SubItem Details of Order Booking Table---------------------------------------
	public Map<String,String> getWithoutSubItemDetails() {
		Map<String,String> map = new HashMap<>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ITEM_ID + "," + ORDER_BOOKING_ITEM_CODE + "," + ORDER_BOOKING_ITEM_NAME + "," + ORDER_BOOKING_GROUP_ID + "," + ORDER_BOOKING_GROUP + "," + ORDER_BOOKING_MAIN_GROUP + "," + ORDER_BOOKING_SUB_GROUP_NAME + "," + ORDER_BOOKING_BARCODE + ","+ORDER_BOOKING_IMAGE_STATUS+","+ORDER_BOOKING_IMAGE_URL+","+ORDER_BOOKING_REMARKS+" from " + ORDER_BOOKING_TABLE;
        System.out.println("Query:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//do {
				map = new HashMap<>();
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("MainGroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MAIN_GROUP)));
				map.put("Barcode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_BARCODE)));
				map.put("ImageStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_STATUS)));
				map.put("ImageUrl", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IMAGE_URL)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
			//} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return map;
	}
	//----------------------------------TODO: Getting Data of Order Without Subitem LIST Table---------------------------------------
	public List<Map<String, String>> getWithoutSubItemList(String itemID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ITEM_NAME + "," + ORDER_BOOKING_ITEM_ID + "," + ORDER_BOOKING_ITEM_CODE + "," + ORDER_BOOKING_ITEM_NAME + "," + ORDER_BOOKING_STOCK + "," + ORDER_BOOKING_RESERVE_STOCK + "," + ORDER_BOOKING_IN_PRODUCTION + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "'";// ORDER BY "+ORDER_BOOKING_SUB_ITEM_CODE+" ASC ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("Stock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_STOCK)));
				map.put("ReserveStock", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RESERVE_STOCK)));
				map.put("InProduction", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_IN_PRODUCTION)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		// returning lables
		return dataList;
	}
	//----------------------------------TODO: Getting Data of Order  Without Subitem Quantity List Table---------------------------------------
	public List<Map<String, String>> getWithoutSubItemQuantityList(String OrderID, int PostFix, String itemID) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		// Select All Query
		String selectQuery = "SELECT DISTINCT " + ORDER_BOOKING_ORDER_ID + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix + "," + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix + "," + ORDER_BOOKING_RATE + PostFix + "," + ORDER_BOOKING_MRP + PostFix + "," + ORDER_BOOKING_DISCOUNT_RATE + PostFix + "," + ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix + "," + ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix + "  from " + ORDER_BOOKING_TABLE + " where " + ORDER_BOOKING_ORDER_ID + PostFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + itemID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("OrderID" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + PostFix)));
				map.put("Ord" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + PostFix)));
				map.put("OrdPrevious" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + PostFix)));
				map.put("Rate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_RATE + PostFix)));
				map.put("Mrp" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_MRP + PostFix)));
				map.put("DiscountRate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_RATE + PostFix)));
				map.put("DisPercentage" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_DISCOUNT_PERCENTAGE + PostFix)));
				map.put("ExpectedDate" + PostFix, cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + PostFix)));
				dataList.add(map);
			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
		}
		//System.out.println("DataList"+PostFix+":"+dataList);
		// returning lables
		return dataList;
	}
	//----------------------------------TODO: Update  Without SubItem Quantity of Order Booking Table---------------------------------------
	public void updateWithoutSubItemQty(String OrderID, String ItemID, String BookedQty,String ExDelDate,int postFix,String Remarks){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORDER_BOOKING_ORDER_BOOKED_QTY+postFix, BookedQty);
		values.put(ORDER_BOOKING_EXPECTED_DELIVERY_DATE+postFix, ExDelDate);
		values.put(ORDER_BOOKING_REMARKS, Remarks);
		db.update(ORDER_BOOKING_TABLE, values, ORDER_BOOKING_ORDER_ID+postFix+"='"+OrderID+"' AND "+ORDER_BOOKING_ITEM_ID+"='"+ItemID+"' ", null);
		db.close();
	}
	//----------------------------------TODO: Getting Booked without subitem details of Order Booking Table---------------------------------------
	public List<Map<String, String>> getAllOrderWithoutSubItemDetails() {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		//TODO: Partial Query
		String PartialQry = "";
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
			PartialQry += (i == 0 ? "" : " OR ") + ORDER_BOOKING_ORDER_BOOKED_QTY + i + " != " + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i;
		}
		// Select All Query
		String selectQuery = "SELECT DISTINCT * from " + ORDER_BOOKING_TABLE + " where " + PartialQry;
		//System.out.println("Query:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ItemID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_ID)));
				map.put("ItemName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_NAME)));
				map.put("ItemCode", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ITEM_CODE)));
				map.put("GroupID", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP_ID)));
				map.put("GroupName", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_GROUP)));
				map.put("Remarks", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARKS)));
				String OrderID = "", Ords = "", OrdsPrevious = "", ExDelDates = "";
				for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
					OrderID += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_ID + i)) + "|");
					Ords += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + i)) + "|");
					//OrdsPrevious += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS+i))==null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS+i))+"|");
					ExDelDates += (cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) == null ? "" : cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_EXPECTED_DELIVERY_DATE + i)) + "|");
				}
				map.put("OrderID", OrderID.substring(0, OrderID.length() - 1));
				map.put("BookQty", Ords.substring(0, Ords.length() - 1));
				//map.put("OrdsPrevious", OrdsPrevious.substring(0,OrdsPrevious.length()-1));
				map.put("ExpectedDate", ExDelDates.substring(0, ExDelDates.length() - 1));
				dataList.add(map);
				//map.put("Remark", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_REMARK)));
				//map.put("FlagLocalStatus", cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_FLAG_LOCAL_STATUS)));

			} while (cursor.moveToNext());
			// closing connection
			cursor.close();
			db.close();
			// returning lables
		}
		return dataList;
	}

	public int getSumAllQuantity() {
		int qtySum = 0;
		for (int i = 0; i < StaticValues.MultiOrderSize; i++) {
			// Select All Query
			String selectQuery = "SELECT " + ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i +"  from " + ORDER_BOOKING_TABLE;
			//System.out.println("Qry:"+selectQuery);
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					qtySum += cursor.getInt(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i));
					//System.out.println("Qty: "+cursor.getInt(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY_PREVIOUS + i)));
				} while (cursor.moveToNext());
				// closing connection
				cursor.close();
				db.close();
			}
		}
		//System.out.println("QtySum:"+qtySum);
		// returning lables
		return qtySum;
	}

	//TODO: Adavance Booking dialog
	public int AdavanceBookingCondition(String OrderID, String ItemID, String ColorID, String SizeID, int BookedQty, int postFix) {
		int status = 0;
		// Select All Query
		String selectQuery = "SELECT " + ORDER_BOOKING_ORDER_BOOKED_QTY + postFix +"  from " + ORDER_BOOKING_TABLE + " WHERE "+ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' AND " + ORDER_BOOKING_COLOR_ID + "='" + ColorID + "' AND " + ORDER_BOOKING_SIZE_ID + "='" + SizeID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int DBBookedQty = cursor.getInt(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix));
			if (DBBookedQty == BookedQty){
				status = 1;
			}else{
				status = 0;
			}
			// closing connection
			cursor.close();
			db.close();
		}
		return status;
	}
	public int AdavanceBookingConditionSubItem(String OrderID, String ItemID, String SubItemID,String BookedQty, int postFix) {
		int status = 0;
		// Select All Query
		String selectQuery = "SELECT " + ORDER_BOOKING_ORDER_BOOKED_QTY + postFix +"  from " + ORDER_BOOKING_TABLE + " WHERE "+ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' AND " + ORDER_BOOKING_SUB_ITEM_ID + "='" + SubItemID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String DBBookedQty = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix));
			if (DBBookedQty.equals(BookedQty)){
				status = 1;
			}else{
				status = 0;
			}
			// closing connection
			cursor.close();
			db.close();
		}
		return status;
	}
	public int AdavanceBookingConditionItemOnly(String OrderID, String ItemID,String BookedQty, int postFix) {
		int status = 0;
		// Select All Query
		String selectQuery = "SELECT " + ORDER_BOOKING_ORDER_BOOKED_QTY + postFix +"  from " + ORDER_BOOKING_TABLE + " WHERE "+ORDER_BOOKING_ORDER_ID + postFix + "='" + OrderID + "' AND " + ORDER_BOOKING_ITEM_ID + "='" + ItemID + "' ";
		//System.out.println("Qry:"+selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String DBBookedQty = cursor.getString(cursor.getColumnIndex(ORDER_BOOKING_ORDER_BOOKED_QTY + postFix));
			if (DBBookedQty.equals(BookedQty)){
				status = 1;
			}else{
				status = 0;
			}
			// closing connection
			cursor.close();
			db.close();
		}
		return status;
	}
}