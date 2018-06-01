package inventory.analysis.catalogue;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionMenu;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.ConditionLibrary;
import com.singlagroup.customwidgets.MessageDialog;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerAllGroups;
import DatabaseController.DatabaseSqlLiteHandlerFilter;
import info.hoang8f.android.segmented.SegmentedGroup;
import inventory.analysis.catalogue.adapter.FilterCheckBoxListAdapter;
import inventory.analysis.catalogue.adapter.FilterTabListAdapter;
import inventory.analysis.catalogue.adapter.RecyclerItemGridAdapter;
import inventory.analysis.catalogue.adapter.RecyclerItemGridColorAdapter;
import inventory.analysis.catalogue.dataset.RecyclerGroupDataset;
import inventory.analysis.catalogue.filterdataset.ResponseFilter;
import inventory.analysis.catalogue.responsedataset.ResponseFilterDataset;
import inventory.analysis.catalogue.responsedataset.ResponseFilterSubDataset;
import inventory.analysis.catalogue.responseitemlist.ResponseItemList;
import inventory.analysis.catalogue.responseitemlistcolor.ItemList;
import inventory.analysis.catalogue.responseitemlistcolor.Result;
import inventory.analysis.catalogue.wishlist.GroupWishListActivity;
import orderbooking.StaticValues;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 21-Feb-17.
 */
public class ItemListActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private ActionBar actionBar;
    private CloseOrBookDataset closeOrBookDataset;
    RecyclerView gridViewItemList;
    LinearLayout linearLayoutFilter;
    ProgressBar progressBar;
    FloatingActionMenu fabMenu;
    TextView txtViewGroupName,txtViewTotalStyle,txtFilter,txtSort;
    DatabaseSqlLiteHandlerFilter DBHandler;
    int StockType = 1,StockAvailability = 1, ImageAvailable = 0, SummaryDataFlag = 1;
    String SortType = "",SortDays = "",RangeFromDays = "",RangeToDays = "",RangeDaysType = "";
    int PriceOrder=0,StockOrder=0,TotalStyle=0;
    int flag=0,v=0,filterLoadStatus=0,searchFlag=0,exchangeFlag=0;
    int rangeSeekBarFlag = 0;
    double MinRate=0,MaxRate=0,MinStock=0,MaxStock=0;
    double MinRateSeek=0,MaxRateSeek=0,MinStockSeek=0,MaxStockSeek=0;
    double minSeekPrice=0,maxSeekPrice=0,minSeekStock=0,maxSeekStock=0;
    double TotalStock=0,TotalItemValue=0;
    List<inventory.analysis.catalogue.responseitemlist.ItemList> DatasetList;
    List<ItemList> DatasetListColor;
    ListView listViewTab,listViewCheckBox;
    String[] strFilter=new String[14];
    public static int start=1,end=30;
    GridLayoutManager gridLayoutManager;
    RecyclerItemGridAdapter recyclerItemGridAdapter;
    RecyclerItemGridColorAdapter recyclerItemGridColorAdapter;
    Dialog dialog;
    private static String TAG = ItemListActivity.class.getSimpleName();
    String Keyword="",GroupID="",Group="",SubGroupID="",SubGroup="",PartyID="",SubPartyID="",RefName="",ItemListApi="ItemList";
    String PriceListID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_itemlist);
        MainGroupOrGroupActivity.deleteCache(getApplicationContext());
        StaticValues.SingleMultiViewFlag=0;
        ItemListApi=StaticValues.ItemListApi;
        DBHandler=new DatabaseSqlLiteHandlerFilter(getApplicationContext());
        DBHandler.FilterTableDelete();
        DatasetList = new ArrayList<>();
        DatasetListColor = new ArrayList<>();
        Initialization();
        GetDataByIntentMethod();
        CallApiMethod();
        handleIntent(this.getIntent());
    }
    private void Initialization() {
        this.context = ItemListActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        gridViewItemList = (RecyclerView) findViewById(R.id.recyclerGrid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Circle);
        txtViewGroupName = (TextView) findViewById(R.id.textView_GroupName);
        txtViewGroupName.setTextSize(10);
        txtViewTotalStyle = (TextView) findViewById(R.id.textView_TotalStyle);
        txtFilter = (TextView) findViewById(R.id.text_Filter);
        txtFilter.setVisibility(View.VISIBLE);
        txtSort = (TextView) findViewById(R.id.text_Sort);
        txtSort.setVisibility(View.VISIBLE);
        fabMenu = (FloatingActionMenu) findViewById(R.id.menu_fab);
        linearLayoutFilter = (LinearLayout) findViewById(R.id.linearLayout1);
        //fabMenu.setVisibility(View.VISIBLE);
    }
    private void GetDataByIntentMethod(){
        try {
            GroupID = getIntent().getExtras().getString("GroupID", "");
            Group = getIntent().getExtras().getString("Group", "");
            RecyclerGroupDataset dataset = (RecyclerGroupDataset)getIntent().getExtras().get("SubGroupDataset");
            closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("PartyDetails");
            if (dataset!=null){
                SubGroupID = dataset.getGroupID();
                SubGroup = dataset.getGroupName();
                if (closeOrBookDataset != null) {
                    PartyID = closeOrBookDataset.getPartyID();
                    String Party = "";
                    if (!closeOrBookDataset.getSubParty().isEmpty() && closeOrBookDataset.getSubParty()!=null) {
                        Party = closeOrBookDataset.getSubParty()+"(" + closeOrBookDataset.getPartyName()+")";
                    }else if (!closeOrBookDataset.getRefName().isEmpty() && closeOrBookDataset.getRefName()!=null) {
                        Party = closeOrBookDataset.getRefName()+"(" + closeOrBookDataset.getPartyName()+")";
                    }else {
                        Party = closeOrBookDataset.getPartyName();
                    }
                    setActionBarHeader(Party,dataset.getGroupName()+" ( "+Group+" )");
                } else {
                    setActionBarHeader(getResources().getString(R.string.app_name),dataset.getGroupName()+" ( "+Group+" )");
                }
            }else{
                //txtViewGroupName.setText(""+Group);
                //actionBar.setSubtitle(""+Group);
                if (closeOrBookDataset != null) {
                    PartyID = closeOrBookDataset.getPartyID();
                    String Party = "";
                    if (!closeOrBookDataset.getSubParty().isEmpty() && closeOrBookDataset.getSubParty()!=null) {
                        Party = closeOrBookDataset.getSubParty()+"(" + closeOrBookDataset.getPartyName()+")";
                    }else if (!closeOrBookDataset.getRefName().isEmpty() && closeOrBookDataset.getRefName()!=null) {
                        Party = closeOrBookDataset.getRefName()+"(" + closeOrBookDataset.getPartyName()+")";
                    }else {
                        Party = closeOrBookDataset.getPartyName();
                    }
                    setActionBarHeader(Party,Group);
                    //setActionBarHeader(closeOrBookDataset.getPartyName(),Group);
                } else {
                    setActionBarHeader(getResources().getString(R.string.app_name),Group);
                }
            }
            //TODO: Pre-Filter
            Map<String,String> preFilter =  (Map<String,String>) getIntent().getExtras().get("PreFilter");

            if(preFilter!=null){
                strFilter = new String[14];
                for(int i=0; i < strFilter.length; i++){
                    if(i == 11) {
                        //TODO: Color
                        String attr = "";
                        if (!preFilter.get("Color").isEmpty()) {
                            if (preFilter.get("Color").contains(",")) {
                                String[] str = preFilter.get("Color").split(",");
                                attr = "";
                                if (str.length > 0) {
                                    for (int j = 0; j < str.length; j++) {
                                        attr += "'" + str[j] + "',";
                                    }

                                }
                            } else {
                                attr += "'" + preFilter.get("Color") + "'";
                            }
                        }
                        strFilter[11] = attr;
                    }else if(i == 12) {
                        //TODO: Size
                        String attr = "";
                        if (!preFilter.get("Size").isEmpty()) {
                            if (preFilter.get("Size").contains(",")) {
                                String[] str = preFilter.get("Size").split(",");
                                attr = "";
                                if (str.length > 0) {
                                    for (int j = 0; j < str.length; j++) {
                                        attr += "'" + str[j] + "',";
                                    }

                                }
                            } else {
                                attr += "'" + preFilter.get("Size") + "',";
                            }
                        }
                        strFilter[12] = attr;
                    }else if(i >= 0 && i < 10) {
                        //TODO: Attr 1 to 10
                        if (preFilter.get("Attr" + (i + 1)) != null) {
                            String attr = "";
                            if (!preFilter.get("Attr" +(i + 1)).isEmpty()){

                                if(preFilter.get("Attr" + (i + 1)).contains(",")) {
                                    String[] str = preFilter.get("Attr" + (i + 1)).split(",");
                                    if (str.length > 0) {
                                        for (int j = 0; j < str.length; j++) {
                                            attr += "'" + str[j] + "',";
                                        }
                                    }
                                }else{
                                    attr += "'" + preFilter.get("Attr" +(i + 1)) + "',";
                                }
                            }
                            strFilter[i] = attr;
                        } else {
                            strFilter[i] = "";
                        }
                    }
                    Log.e(TAG,"Attr"+i+":"+strFilter[i]);
                }
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Intent Exception",""+e.toString());
        }
    }
    private void setActionBarHeader(String Header,String SubHeader){
        try{
            LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.action_bar_header, null);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView_Back);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Activity Intent to Flag wise
                    finish();
                }
            });
            TextView textView = (TextView) v.findViewById(R.id.text_view_header);
            textView.setSelected(true);
            textView.setWidth(StaticValues.sViewWidth-300);
            textView.setText("" + Header);
            TextView textViewSubHeader = (TextView) v.findViewById(R.id.text_view_sub_header);
            textViewSubHeader.setSelected(true);
            textViewSubHeader.setWidth(StaticValues.sViewWidth-300);
            textViewSubHeader.setText("" + SubHeader);

            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(v);
        }catch (Exception e){
            MessageDialog.MessageDialog(context, "Exception", "Intent:"+e.toString());
        }
    }
    private void CallApiMethod(){
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {

            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                //TODO: CallRetrofit Item Catalogue
                CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                //TODO: CallRetrofit Filtr
                CallVolleyFilter(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID,"","","","");
            }
        }else{
            Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //TODO: CallRetrofit Item List
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                        //TODO: CallRetrofit Filtr
                        CallVolleyFilter(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID,"","","","");
                    }
                }else{
                    Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    //TODO: FabButton onClick Method
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_Filter:
                // TODO: Filter
                DatabaseSqlLiteHandlerFilter DBFilter=new DatabaseSqlLiteHandlerFilter(ItemListActivity.this);
                List<Map<String,String>> mapList = DBFilter.getFilter();
                if(!mapList.isEmpty()) {
                    DialogFilter();
                }else {
                    Toast.makeText(getApplicationContext(),"Please wait...",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.text_Sort:
                // TODO: Sort
                if (ItemListApi.equals("ItemList")){
                    //if(!DatasetList.isEmpty())
                        DialogSort();
                }else{
                    //if(!DatasetListColor.isEmpty())
                        DialogSort();
                }
                break;
            default:
                break;
        }
    }
    //TODO:Asyc MainGroup
    private void CallRetrofit(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID, final String BranchID, String GroupID, String SubGroupID, String PriceListID, String start, String end, String PriceOrder, String minRate, String maxRate, String StockOrder, String minSt, String maxSt, String[] Attr, String SummaryDataFlg, String SearchQuery,String StockCheckFlag,String ImageCheckFlag,String StockTypeFlag,String SortDays,String SortType,String RangeFromDays,String RangeToDays,String RangeDaysType){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("CompanyID", CompanyID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("BranchID", BranchID);
        params.put("GroupID", GroupID);
        params.put("SubGroupID", SubGroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("PriceSort", PriceOrder);
        params.put("minRate", minRate);
        params.put("maxRate", maxRate);
        params.put("StockSort", StockOrder);
        params.put("minStock", minSt);
        params.put("maxStock", maxSt);
        String Attr1=(Attr[0]==null || Attr[0].isEmpty())?"":Attr[0].substring(0,Attr[0].length()-1);
        params.put("Attr1", Attr1);
        String Attr2=(Attr[1]==null || Attr[1].isEmpty())?"":Attr[1].substring(0,Attr[1].length()-1);
        params.put("Attr2", Attr2);
        String Attr3=(Attr[2]==null || Attr[2].isEmpty())?"":Attr[2].substring(0,Attr[2].length()-1);
        params.put("Attr3", Attr3);
        String Attr4=(Attr[3]==null || Attr[3].isEmpty())?"":Attr[3].substring(0,Attr[3].length()-1);
        params.put("Attr4", Attr4);
        String Attr5=(Attr[4]==null || Attr[4].isEmpty())?"":Attr[4].substring(0,Attr[4].length()-1);
        params.put("Attr5", Attr5);
        String Attr6=(Attr[5]==null || Attr[5].isEmpty())?"":Attr[5].substring(0,Attr[5].length()-1);
        params.put("Attr6", Attr6);
        String Attr7=(Attr[6]==null || Attr[6].isEmpty())?"":Attr[6].substring(0,Attr[6].length()-1);
        params.put("Attr7", Attr7);
        String Attr8=(Attr[7]==null || Attr[7].isEmpty())?"":Attr[7].substring(0,Attr[7].length()-1);
        params.put("Attr8", Attr8);
        String Attr9=(Attr[8]==null || Attr[8].isEmpty())?"":Attr[8].substring(0,Attr[8].length()-1);
        params.put("Attr9", Attr9);
        String Attr10=(Attr[9]==null || Attr[9].isEmpty())?"":Attr[9].substring(0,Attr[9].length()-1);
        params.put("Attr10", Attr10);
        String RateIn=(Attr[10]==null || Attr[10].isEmpty())?"":Attr[10].substring(0,Attr[10].length()-1);
        params.put("RateIn", RateIn);
        String CF=(Attr[11]==null || Attr[11].isEmpty())?"":Attr[11].substring(0,Attr[11].length()-1);
        params.put("CF", CF);
        String SZ=(Attr[12]==null || Attr[12].isEmpty())?"":Attr[12].substring(0,Attr[12].length()-1);
        params.put("SZ", SZ);
        String DiscIn=(Attr[13]==null || Attr[13].isEmpty())?"":Attr[13].substring(0,Attr[13].length()-1);
        params.put("DiscIn", DiscIn);
        params.put("SummaryDataFlg", SummaryDataFlg); //TODO: Summary Data Flag :- Total Style, Min-Max Rate Or Stock ,filter
        params.put("Search", SearchQuery);
        params.put("StockCheckFlag", StockCheckFlag);
        params.put("ImageCheckFlag", ImageCheckFlag);
        params.put("StockTypeFlag", StockTypeFlag); //TODO: 0-DefaultStock, 1-SalebleStock,2-ReserveStock,3-Rejection/ScrapStock,4-production/Purchase,5-Pending Delivery
        params.put("SortDays", SortDays);
        params.put("SortType", SortType);
        params.put("RangeFromDays", RangeFromDays);
        params.put("RangeToDays", RangeToDays);
        params.put("RangeDaysType", RangeDaysType);
        Log.d(TAG,"Item Catalogue Parameters:"+params.toString());
        if (ItemListApi.equals("ItemList") && !ItemListApi.isEmpty()){
            Call<ResponseItemList> call = apiService.getInventoryItemList(params);
            call.enqueue(new Callback<ResponseItemList>() {
                @Override
                public void onResponse(Call<ResponseItemList> call, retrofit2.Response<ResponseItemList> response) {
                    try {
                        TotalStyle = 0;
                        TotalStock = 0;
                        TotalItemValue = 0;
                        DatasetList = new ArrayList<>();
                        DatasetListColor = new ArrayList<>();
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                inventory.analysis.catalogue.responseitemlist.Result result = response.body().getResult();
                                TotalStyle = result.getTotalStyle();
                                DatasetList = result.getItemList();
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                                if (rangeSeekBarFlag == 0) {
                                    MinRate = result.getMinRate();
                                    MaxRate = result.getMaxRate();
                                    MinStock = result.getMinStock();
                                    MaxStock = result.getMaxStock();
                                }else{
                                    MinStockSeek = result.getMinRate();
                                    MaxRateSeek = result.getMaxRate();
                                    MinStockSeek = result.getMinStock();
                                    MaxStockSeek = result.getMaxStock();
                                }
                                TotalStock = result.getTotalStock();
                                TotalItemValue = result.getTotalItemValue();
                                txtViewGroupName.setText("Q:"+ ConditionLibrary.ConvertDoubleToString(TotalStock)+"  V:₹"+Math.round(TotalItemValue));
                            }  else {
                                TotalStyle = 0;
                                TotalStock = 0;
                                TotalItemValue = 0;
                                txtViewGroupName.setText("Q:"+ ConditionLibrary.ConvertDoubleToString(TotalStock)+"  V:₹"+Math.round(TotalItemValue));
                                txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                                Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                            }
                            //Snackbar.make(gridViewItemList,""+msg,Snackbar.LENGTH_LONG).show();
                        }else {
                            TotalStyle = 0;
                            TotalStock = 0;
                            TotalItemValue = 0;
                            txtViewGroupName.setText("Q:"+ ConditionLibrary.ConvertDoubleToString(TotalStock)+"  V:₹"+Math.round(TotalItemValue));
                            txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                            ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                            Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Log.e(TAG,"ItemList Exception:"+e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        } else {
            Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call = apiService.getInventoryItemListColor(params);
            call.enqueue(new Callback<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList>() {
                @Override
                public void onResponse(Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call, retrofit2.Response<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> response) {
                    try {
                        TotalStyle = 0;
                        DatasetList = new ArrayList<>();
                        DatasetListColor = new ArrayList<>();
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                Result result = response.body().getResult();
                                TotalStyle = result.getTotalStyle();
                                DatasetListColor = result.getItemList();
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                                MinRate = result.getMinRate();
                                MaxRate = result.getMaxRate();
                                MinStock = result.getMinStock();
                                MaxStock = result.getMaxStock();
                            } else {
                                TotalStyle = 0;
                                txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                                Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                            }
                        }else {
                            TotalStyle = 0;
                            txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                            ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                            Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Log.e(TAG,"ItemList Exception:"+e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }
    }
    private void CallVolleyFilter(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID, final String BranchID, final String GroupID, final String SubGroupID, final String PartyID, final String SubPartyID, final String RefName, final String PriceListID){
        //showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"GetFilterDetails", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        if (filterLoadStatus == 0) {
                            StaticValues.FilterCaptionSequenceInventory = new String[14];
                            StaticValues.FilterAttributeSequenceInventory = new String[14];
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            List<Map<String, String>> mapListFilter = new ArrayList<>();
                            for (int i=0; i<jsonArrayResult.length(); i++){
                                String Cap = (jsonArrayResult.getJSONObject(i).optString("Cap")== null || jsonArrayResult.getJSONObject(i).optString("Cap").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Cap"));
                                String Seq = (jsonArrayResult.getJSONObject(i).optString("Seq")== null || jsonArrayResult.getJSONObject(i).optString("Seq").equals("null") ? "0" : jsonArrayResult.getJSONObject(i).optString("Seq"));
                                StaticValues.FilterCaptionSequenceInventory[i] = Cap;
                                StaticValues.FilterAttributeSequenceInventory[i] = Seq;

                                JSONArray jsonArrayAttribute = jsonArrayResult.getJSONObject(i).getJSONArray("Attribute");
                                if (jsonArrayAttribute!= null && jsonArrayAttribute.length()>0) {
                                    for (int j = 0; j < jsonArrayAttribute.length(); j++) {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("ID", (jsonArrayAttribute.getJSONObject(j).optString("ID")==null ? "" : jsonArrayAttribute.getJSONObject(j).optString("ID")));
                                        map.put("Name", (jsonArrayAttribute.getJSONObject(j).optString("Name")==null ? "" : jsonArrayAttribute.getJSONObject(j).optString("Name")));
                                        map.put("Seq", (jsonArrayAttribute.getJSONObject(j).optString("Seq")==null ? "0" : jsonArrayAttribute.getJSONObject(j).optString("Seq")));
                                        mapListFilter.add(map);
                                    }
                                }
//                                    }else{
//                                        Map<String, String> map = new HashMap<>();
//                                        map.put("ID", "00000000-0000-0000-0000-000000000000");
//                                        map.put("Name", "");
//                                        map.put("Seq", String.valueOf(result.get(i).getSeq()));
//                                        mapListFilter.add(map);
//                                    }

                            }
                            System.out.println("Filter MapList:"+mapListFilter.toString());
                            DatabaseSqlLiteHandlerFilter DBFilter = new DatabaseSqlLiteHandlerFilter(context);
                            DBFilter.FilterTableDelete();
                            DBFilter.insertFilterTable(mapListFilter);

                            filterLoadStatus = 1;
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                //hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                //hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                params.put("BranchID", BranchID);
                params.put("GroupID", GroupID);
                params.put("SubGroupID", SubGroupID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("PriceListID", PriceListID);
                Log.d(TAG,"Get Filter Details Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallRetrofitOnScroll(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID, final String BranchID, String GroupID, String SubGroupID, String PriceListID, String start, String end, String PriceOrder, String minRate, String maxRate, String StockOrder, String minSt, String maxSt, String[] Attr, String SummaryDataFlg, String SearchQuery,String StockCheckFlag,String ImageCheckFlag,String StockTypeFlag,String SortDays,String SortType,String RangeFromDays,String RangeToDays,String RangeDaysType){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("CompanyID", CompanyID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("BranchID", BranchID);
        params.put("GroupID", GroupID);
        params.put("SubGroupID", SubGroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("PriceSort", PriceOrder);
        params.put("minRate", minRate);
        params.put("maxRate", maxRate);
        params.put("StockSort", StockOrder);
        params.put("minStock", minSt);
        params.put("maxStock", maxSt);
        String Attr1=(Attr[0]==null || Attr[0].isEmpty())?"":Attr[0].substring(0,Attr[0].length()-1);
        params.put("Attr1", Attr1);
        String Attr2=(Attr[1]==null || Attr[1].isEmpty())?"":Attr[1].substring(0,Attr[1].length()-1);
        params.put("Attr2", Attr2);
        String Attr3=(Attr[2]==null || Attr[2].isEmpty())?"":Attr[2].substring(0,Attr[2].length()-1);
        params.put("Attr3", Attr3);
        String Attr4=(Attr[3]==null || Attr[3].isEmpty())?"":Attr[3].substring(0,Attr[3].length()-1);
        params.put("Attr4", Attr4);
        String Attr5=(Attr[4]==null || Attr[4].isEmpty())?"":Attr[4].substring(0,Attr[4].length()-1);
        params.put("Attr5", Attr5);
        String Attr6=(Attr[5]==null || Attr[5].isEmpty())?"":Attr[5].substring(0,Attr[5].length()-1);
        params.put("Attr6", Attr6);
        String Attr7=(Attr[6]==null || Attr[6].isEmpty())?"":Attr[6].substring(0,Attr[6].length()-1);
        params.put("Attr7", Attr7);
        String Attr8=(Attr[7]==null || Attr[7].isEmpty())?"":Attr[7].substring(0,Attr[7].length()-1);
        params.put("Attr8", Attr8);
        String Attr9=(Attr[8]==null || Attr[8].isEmpty())?"":Attr[8].substring(0,Attr[8].length()-1);
        params.put("Attr9", Attr9);
        String Attr10=(Attr[9]==null || Attr[9].isEmpty())?"":Attr[9].substring(0,Attr[9].length()-1);
        params.put("Attr10", Attr10);
        String RateIn=(Attr[10]==null || Attr[10].isEmpty())?"":Attr[10].substring(0,Attr[10].length()-1);
        params.put("RateIn", RateIn);
        String CF=(Attr[11]==null || Attr[11].isEmpty())?"":Attr[11].substring(0,Attr[11].length()-1);
        params.put("CF", CF);
        String SZ=(Attr[12]==null || Attr[12].isEmpty())?"":Attr[12].substring(0,Attr[12].length()-1);
        params.put("SZ", SZ);
        String DiscIn=(Attr[13]==null || Attr[13].isEmpty())?"":Attr[13].substring(0,Attr[13].length()-1);
        params.put("DiscIn", DiscIn);
        params.put("SummaryDataFlg", SummaryDataFlg); //TODO: Summary Data Flag :- Total Style, Min-Max Rate Or Stock ,filter
        params.put("Search", SearchQuery);
        params.put("StockCheckFlag", StockCheckFlag);
        params.put("ImageCheckFlag", ImageCheckFlag);
        params.put("StockTypeFlag", StockTypeFlag); //TODO: 0-DefaultStock, 1-SalebleStock,2-ReserveStock,3-Rejection/ScrapStock,4-production/Purchase,5-Pending Delivery
        params.put("SortDays", SortDays);
        params.put("SortType", SortType);
        params.put("RangeFromDays", RangeFromDays);
        params.put("RangeToDays", RangeToDays);
        params.put("RangeDaysType", RangeDaysType);
        Log.d(TAG,"Item Catalogue Parameters:"+params.toString());
        if (ItemListApi.equals("ItemList") && !ItemListApi.isEmpty()){
            Call<ResponseItemList> call = apiService.getInventoryItemList(params);
            call.enqueue(new Callback<ResponseItemList>() {
                @Override
                public void onResponse(Call<ResponseItemList> call, retrofit2.Response<ResponseItemList> response) {
                    try {
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                inventory.analysis.catalogue.responseitemlist.Result result = response.body().getResult();
                                DatasetList.addAll(result.getItemList());
                                int curSize = recyclerItemGridAdapter.getItemCount();
                                recyclerItemGridAdapter.notifyItemRangeInserted(curSize, DatasetList.size() - 1);
                            } else {
                                Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                            }
                        }else {
                            //ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                            //Snackbar.make(gridViewItemList,""+response.message()+" Response:"+response.code(),Snackbar.LENGTH_LONG).show();

                        }
                    }catch (Exception e){
                        Log.e(TAG,"ItemList Exception:"+e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    Toast.makeText(getApplicationContext(),"Item List Scroll Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        } else {
            Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call = apiService.getInventoryItemListColor(params);
            call.enqueue(new Callback<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList>() {
                @Override
                public void onResponse(Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call, retrofit2.Response<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> response) {
                    try {
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                Result result = response.body().getResult();
                                DatasetListColor.addAll(result.getItemList());
                                int curSize = recyclerItemGridColorAdapter.getItemCount();
                                recyclerItemGridColorAdapter.notifyItemRangeInserted(curSize, DatasetListColor.size() - 1);
                                //Log.e(TAG,"ItemList :"+result.getItemList());
                            } else {
                                Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                            }
                        }else {
                            //ItemViewChanger(DatasetList,DatasetListColor, TotalStyle);
                            //Snackbar.make(gridViewItemList,""+response.message()+" Response:"+response.code(),Snackbar.LENGTH_LONG).show();

                        }
                    }catch (Exception e){
                        Log.e(TAG,"ItemList Exception:"+e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    //Toast.makeText(getApplicationContext(),"Item List Scroll Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }
    }
    private void showpDialog() {
//        WhorlView whorlView = (WhorlView) this.findViewById(R.id.whorl2);
//        whorlView.setVisibility(View.VISIBLE);
//        whorlView.start();
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepDialog() {
//        WhorlView whorlView = (WhorlView) this.findViewById(R.id.whorl2);
//        whorlView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onResume(){
        super.onResume();
        actionBar.invalidateOptionsMenu();
    }
    @Override
    public void onPause(){
        super.onPause();
        actionBar.invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem menuItem = menu.findItem(R.id.action_account);
        menuItem.setVisible(false);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        MenuItemCompat.setOnActionExpandListener(searchItem, new SearchViewExpandListener(this));
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setQueryHint("Search...");
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText){
                // this is your adapter that will be filtered
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query){
                // this is your adapter that will be filtered
                //recyclerItemGridAdapter.getFilter().filter(query);
                StaticValues.SearchQuery="";
                StaticValues.SearchQuery=query;
                searchFlag=1;
                start = 1;
                end = 30;
                //TODO: CallRetrofit Item List
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    //CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                    CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, "0", StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                StaticValues.SearchQuery="";
                searchFlag=0;
                start = 1;
                end = 30;
                //TODO: CallRetrofit Item List
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null) {
                    CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, "0", StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                }
                return false;
            }
        });
        MenuItem box = menu.findItem(R.id.action_box);
        MainGroupOrGroupActivity.iconBox = (LayerDrawable) box.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(ItemListActivity.this, MainGroupOrGroupActivity.iconBox, StaticValues.totalBoxCount);

        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        MainGroupOrGroupActivity.iconWishlist = (LayerDrawable) wishlist.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(ItemListActivity.this, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
        return super.onCreateOptionsMenu(menu);
    }
    //TODO: Searching
    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
                 break;
            case R.id.action_search:
                 break;
            case R.id.action_single_view:
                DialogCustomViewSettings();
                 break;
            case R.id.action_wishlist:
                Intent intent = new Intent(context, GroupWishListActivity.class);
                startActivity(intent);
                 break;
            case R.id.action_box:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    intent = new Intent(context, OrderViewOrPushActivity.class);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select ruuning Customer first");
                }
                 //CustomDialogBox(1,"My Box", 1);
                 break;
//            case R.id.action_account:
//                DialogLoginCatalogue(ItemListActivity.this);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //TODO: Item list view changer
    private void ItemViewChanger(final List<inventory.analysis.catalogue.responseitemlist.ItemList> list, final List<ItemList> listColor, final int totalstyle){
        invalidateOptionsMenu();
        gridViewItemList.removeAllViewsInLayout();
        gridViewItemList.removeAllViews();
        final Context context=ItemListActivity.this;
        if (ItemListApi.equals("ItemList")) {
            recyclerItemGridAdapter=new RecyclerItemGridAdapter(context,list,flag,closeOrBookDataset,StockType);
            gridViewItemList.setAdapter(recyclerItemGridAdapter);
        }else{
            recyclerItemGridColorAdapter=new RecyclerItemGridColorAdapter(context,listColor,flag,closeOrBookDataset);
            gridViewItemList.setAdapter(recyclerItemGridColorAdapter);
        }
        if(flag==0){
            gridLayoutManager=new GridLayoutManager(context,2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridViewItemList.setLayoutManager(gridLayoutManager);
            gridViewItemList.setHasFixedSize(true);
            gridViewItemList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int mLastFirstVisibleItem = 0;
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(searchFlag==0){
                        String totalStyle="";
                        if (ItemListApi.equals("ItemList")) {
                            totalStyle = (list.size() == 0) ? "0 Style" : totalstyle + " Styles";
                        }else{
                            totalStyle = (listColor.size() == 0) ? "0 Style" : totalstyle + " Styles";
                        }
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" + totalStyle);
                    }else if(searchFlag==1){
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" +gridLayoutManager.getItemCount()+ " Styles");
                    }
                    final int currentFirstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                    if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                        actionBar.hide();
                    } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                        actionBar.show();
                    }
                    this.mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            });
            gridViewItemList.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    //Toast.makeText(context,"totalItemsCount:"+totalItemsCount,Toast.LENGTH_SHORT).show();
                    start = end + 1;
                    end = end + 30;
                    //TODO: onScroll Execute filter
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            SummaryDataFlag = 0;
//                            CallRetrofitOnScroll(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", StaticValues.SearchQuery,"0",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                            CallRetrofitOnScroll(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                        }
                    } else {
                        Snackbar.make(gridViewItemList, status + "", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        else if(flag==1) {
            gridLayoutManager = new GridLayoutManager(context, 1);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridViewItemList.setLayoutManager(gridLayoutManager);
            gridViewItemList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(searchFlag==0){
                        String totalStyle="";
                        if (ItemListApi.equals("ItemList")) {
                            totalStyle = (list.size() == 0) ? "0 Style" : totalstyle + " Styles";
                        }else{
                            totalStyle = (listColor.size() == 0) ? "0 Style" : totalstyle + " Styles";
                        }
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" + totalStyle);
                    }else if(searchFlag==1){
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" +gridLayoutManager.getItemCount()+ " Styles");
                    }
                }
            });
            gridViewItemList.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    final String GroupID = getIntent().getExtras().getString("GroupID");
                    start = end + 1;
                    end = end + 30;
                    //TODO: AsyncItemScroll Execute filter
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            SummaryDataFlag = 0;
//                            CallRetrofitOnScroll(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", StaticValues.SearchQuery,"0",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                            CallRetrofitOnScroll(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                        }
                    }else{
                        Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
//        if(list.isEmpty()){
//            txtViewTotalStyle.setText("");
//            Snackbar.make(gridViewItemList,"No result found"+Keyword,Snackbar.LENGTH_LONG).show();
//        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent=new Intent(ItemListActivity.this, MainGroupOrGroupActivity.class);
//            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void DialogCustomViewSettings(){
        final Dialog dialog=new Dialog(ItemListActivity.this);
        dialog.setContentView(R.layout.dialog_view_settings);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle("Item list view settings");
        dialog.show();
        RadioGroup radioGroupSingleOrMultiView = (RadioGroup) dialog.findViewById(R.id.Radio_Group_SingleOrMultiWise);
        RadioGroup radioGroupItemOrColorWise = (RadioGroup) dialog.findViewById(R.id.Radio_Group_ItemOrColorWise);
        RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.Radio_Button1);
        RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.Radio_Button2);
        RadioButton radioButton3 = (RadioButton) dialog.findViewById(R.id.Radio_Button3);
        RadioButton radioButton4 = (RadioButton) dialog.findViewById(R.id.Radio_Button4);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        if(flag == 0) {
            radioButton4.setChecked(true);
        }else if (flag == 1){
            radioButton3.setChecked(true);
        }
        if (ItemListApi.equals("ItemList")){
            radioButton1.setChecked(true);
        }else {
            radioButton2.setChecked(true);
        }
        radioGroupItemOrColorWise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId==R.id.Radio_Button1){
                    ItemListApi = "ItemList";
                    exchangeFlag = 1;
                }
                else if(checkedId==R.id.Radio_Button2){
                    ItemListApi = "ItemListColorWise";
                    exchangeFlag = 1;
                }
            }
        });
        radioGroupSingleOrMultiView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId==R.id.Radio_Button3){
                    flag = 1;
                }
                else if(checkedId==R.id.Radio_Button4){
                    flag = 0;
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exchangeFlag == 1) {
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        //TODO: CallRetrofit Item List
                        start=1;
                        end=30;
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            //CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                            SummaryDataFlag = 1;
                            CallRetrofitOnScroll(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), "0", "0", String.valueOf(StockOrder),  "0", "0", strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                            dialog.dismiss();
                            exchangeFlag = 0;
                        }
                    } else {
                        Snackbar.make(gridViewItemList, status + "", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    ItemViewChanger(DatasetList,DatasetListColor,TotalStyle);
                    dialog.dismiss();
                }
            }
        });
    }
    private void DialogSort(){
        final Dialog dialog=new Dialog(context,R.style.FullHeightDialog);//new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_sort_for_inventory);
        dialog.show();
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //TODO: Declaration
        final TextView txtViewClear=(TextView) dialog.findViewById(R.id.textView_Clear);
        Button btnOk=(Button) dialog.findViewById(R.id.btn_yes);
        //TODO: Stock
        final Spinner spnStockType = (Spinner) dialog.findViewById(R.id.spinner_StockType);
        final RadioGroup radioGroupStockAvailability=(RadioGroup) dialog.findViewById(R.id.Radio_Group_Stock_Availability);
//        final EditText edtFromRangeStock = (EditText) dialog.findViewById(R.id.editText_Stock_From_Range);
//        final EditText edtToRangeStock = (EditText) dialog.findViewById(R.id.editText_Stock_To_Range);
//        final TextView txtViewStockMin=(TextView) dialog.findViewById(R.id.textView_Stock_Min);
//        final TextView txtViewStockMax=(TextView) dialog.findViewById(R.id.textView_Stock_Max);
        final RangeSeekBar rangeSeekBarStockRange=(RangeSeekBar) dialog.findViewById(R.id.seekbar_Stock);
        final TextView txtViewMinStock=(TextView) dialog.findViewById(R.id.textView_minimumRangeStock);
        final TextView txtViewMaxStock=(TextView) dialog.findViewById(R.id.textView_maximumRangeStock);
        final SegmentedGroup radioGroupStockOrder=(SegmentedGroup) dialog.findViewById(R.id.radioGroup_Stock_Order);
        final RadioGroup radioGroupImageAvailable=(RadioGroup) dialog.findViewById(R.id.Radio_Group_Image_Available);
        //TODO: Price
        final RangeSeekBar rangeSeekBarPriceRange=(RangeSeekBar) dialog.findViewById(R.id.seekbar_Price);
        final TextView txtViewMin=(TextView) dialog.findViewById(R.id.textView_minimumRangePrice);
        final TextView txtViewMax=(TextView) dialog.findViewById(R.id.textView_maximumRangePrice);
        final SegmentedGroup radioGroupPriceOrder=(SegmentedGroup) dialog.findViewById(R.id.radioGroup_Price_Order);
//        final EditText edtFromRangePrice = (EditText) dialog.findViewById(R.id.editText_Price_From_Range);
//        final EditText edtToRangePrice = (EditText) dialog.findViewById(R.id.editText_Price_To_Range);
//        final TextView txtViewPriceMin=(TextView) dialog.findViewById(R.id.textView_Price_Min);
//        final TextView txtViewPriceMax=(TextView) dialog.findViewById(R.id.textView_Price_Max);

        //TODO: Sort Days
        final Spinner spnSortDays = (Spinner) dialog.findViewById(R.id.spinner_SortDays);
        final SegmentedGroup radioGroupDaysType=(SegmentedGroup) dialog.findViewById(R.id.Radio_Group_DaysType);
        //TODO: Range Days
        final Spinner spnRangeDays = (Spinner) dialog.findViewById(R.id.spinner_RangeDays);
//        final EditText edtFromRangeDays = (EditText) dialog.findViewById(R.id.editText_Range_From_Days);
//        final EditText edtToRangeDays = (EditText) dialog.findViewById(R.id.editText_Range_To_Days);
        final LinearLayout LinearLayoutFromRangeDays = (LinearLayout) dialog.findViewById(R.id.LinearLayout_Range_From_Days);
        final LinearLayout LinearLayoutToRangeDays = (LinearLayout) dialog.findViewById(R.id.LinearLayout_Range_To_Days);
        //TODO: Spinner Stock Type
        spnStockType.setSelection(StockType);
        spnStockType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                StockType = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Stock Availability
        final RadioButton radioBtnSAYes=(RadioButton) dialog.findViewById(R.id.Radio_Button_Stock_Availability_Yes);
        final RadioButton radioBtnSANo=(RadioButton) dialog.findViewById(R.id.Radio_Button_Stock_Availability_No);
        final RadioButton radioBtnSAAll=(RadioButton) dialog.findViewById(R.id.Radio_Button_Stock_Availability_All);
        if (StockAvailability == 0){
            radioBtnSAAll.setChecked(true);
        }else if (StockAvailability == 1){
            radioBtnSAYes.setChecked(true);
        }if (StockAvailability == 2){
            radioBtnSANo.setChecked(true);
        }
        radioGroupStockAvailability.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.Radio_Button_Stock_Availability_Yes){
                    StockAvailability = 1;
                }else if(checkedId == R.id.Radio_Button_Stock_Availability_No){
                    StockAvailability = 2;
                }else if(checkedId == R.id.Radio_Button_Stock_Availability_All){
                    StockAvailability = 0;
                }
            }
        });
        //TODO: Stock Range
//        minSeekStock = (minSeekStock == 0 ? MinStock : minSeekStock);
//        maxSeekStock = (maxSeekStock == 0 ? MaxStock : maxSeekStock);
//        txtViewStockMin.setText("From :\nMin "+MinStock);
//        txtViewStockMax.setText("To :\nMax "+MaxStock);
//        edtFromRangeStock.setText(""+minSeekStock);
//        edtToRangeStock.setText(""+maxSeekStock);
        double minStockSort = (MinStock >= MinStockSeek ? (MinStockSeek == 0 && MaxStockSeek == 0 ? MinStock : MinStockSeek) : MinStock);
        double maxStockSort = (MaxStock >= MaxStockSeek ? MaxStock : (MinStockSeek == 0 && MaxStockSeek == 0 ? MaxStock : MaxStockSeek));
        minSeekStock=((minSeekStock==0)?MinStock:minSeekStock);
        maxSeekStock=((maxSeekStock==0)?MaxStock:maxSeekStock);
        txtViewMinStock.setText(""+minStockSort);
        txtViewMaxStock.setText(""+maxStockSort);
        rangeSeekBarStockRange.setRangeValues(minStockSort, maxStockSort); // if we want to set progrmmatically set range of seekbar
        rangeSeekBarStockRange.setSelectedMinValue(minSeekStock);
        rangeSeekBarStockRange.setSelectedMaxValue(maxSeekStock);
        rangeSeekBarStockRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                minSeekStock = minValue;
                maxSeekStock = maxValue;
            }
        });
        //TODO: Stock Order
        final RadioButton radioBtnSOLowHigh=(RadioButton) dialog.findViewById(R.id.radioButton_Stock_LowToHigh);
        final RadioButton radioBtnSOHighLow=(RadioButton) dialog.findViewById(R.id.radioButton_Stock_HighToLow);
        if (StockOrder == 1) {
            if (SortType.equals("0")) {
                radioBtnSOLowHigh.setChecked(true);
            } else if (SortType.equals("1")) {
                radioBtnSOHighLow.setChecked(true);
            }
        }
        radioGroupStockOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.radioButton_Stock_LowToHigh){
                    SortType = "0";
                    StockOrder = 1;
                }else if(checkedId == R.id.radioButton_Stock_HighToLow){
                    SortType = "1";
                    StockOrder = 1;
                }
            }
        });
        //TODO: Image Available
        final RadioButton radioBtnIAYes=(RadioButton) dialog.findViewById(R.id.Radio_Button_ImageView_Available_Yes);
        final RadioButton radioBtnIANo=(RadioButton) dialog.findViewById(R.id.Radio_Button_ImageView_Available_No);
        final RadioButton radioBtnIAAll=(RadioButton) dialog.findViewById(R.id.Radio_Button_ImageView_Available_All);
        if (ImageAvailable == 0){
            radioBtnIAAll.setChecked(true);
        }else if (ImageAvailable == 1){
            radioBtnIAYes.setChecked(true);
        }if (ImageAvailable == 2){
            radioBtnIANo.setChecked(true);
        }
        radioGroupImageAvailable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.Radio_Button_ImageView_Available_Yes){
                    ImageAvailable = 1;
                }else if(checkedId == R.id.Radio_Button_ImageView_Available_No){
                    ImageAvailable = 2;
                }else if(checkedId == R.id.Radio_Button_ImageView_Available_All){
                    ImageAvailable = 0;
                }
            }
        });
        //TODO: Price Range
//        minSeekPrice = (minSeekPrice == 0 ? MinRate : minSeekPrice);
//        maxSeekPrice = (maxSeekPrice == 0 ? MaxRate : maxSeekPrice);
//        txtViewPriceMin.setText("From :\nMin "+MinRate);
//        txtViewPriceMax.setText("To :\nMax "+MaxRate);
//        edtFromRangePrice.setText(""+minSeekPrice);
//        edtToRangePrice.setText(""+maxSeekPrice);
        double minRateSort = (MinRate >= MinRateSeek ?  (MinRateSeek == 0 && MaxRateSeek == 0 ? MinRate : MinRateSeek): MinRate);
        double maxRateSort = (MaxRate >= MaxRateSeek ? MaxRate : (MinRateSeek == 0 && MaxRateSeek == 0 ? MaxRate : MaxRateSeek));
        minSeekPrice=((minSeekPrice==0)?MinRate:minSeekPrice);
        maxSeekPrice=((maxSeekPrice==0)?MaxRate:maxSeekPrice);
        txtViewMin.setText(""+minRateSort);
        txtViewMax.setText(""+maxRateSort);
        rangeSeekBarPriceRange.setRangeValues(minRateSort , maxRateSort); // if we want to set progrmmatically set range of seekbar
        rangeSeekBarPriceRange.setSelectedMinValue(minSeekPrice);
        rangeSeekBarPriceRange.setSelectedMaxValue(maxSeekPrice);
        rangeSeekBarPriceRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                minSeekPrice = minValue;
                maxSeekPrice = maxValue;
            }
        });
        //TODO: Price Order
        final RadioButton radioBtnPOLowHigh=(RadioButton) dialog.findViewById(R.id.radioButton_Price_LowToHigh);
        final RadioButton radioBtnPOHighLow=(RadioButton) dialog.findViewById(R.id.radioButton_Price_HighToLow);
        if (PriceOrder == 1) {
            if (SortType.equals("0")) {
                radioBtnPOLowHigh.setChecked(true);
            } else if (SortType.equals("1")) {
                radioBtnPOHighLow.setChecked(true);
            }
        }
        radioGroupPriceOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.radioButton_Price_LowToHigh){
                    SortType = "0";
                    PriceOrder = 1;
                }else if(checkedId == R.id.radioButton_Price_HighToLow){
                    SortType = "1";
                    PriceOrder = 1;
                }
            }
        });
        //TODO: Spinner Sort Days
        int SortDay = (SortDays.isEmpty() ? 0 : Integer.valueOf(SortDays));
        spnSortDays.setSelection(SortDay);
        spnSortDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                if (position == 0){
                    SortDays = String.valueOf(position);
                }else {
                    SortDays = String.valueOf(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Sort Order
        final RadioButton radioBtnSortTypeAsc=(RadioButton) dialog.findViewById(R.id.radioButton_DaysType_LowToHigh);
        final RadioButton radioBtnSortTypeDesc=(RadioButton) dialog.findViewById(R.id.radioButton_DaysType_HighToLow);
        if (SortDay>0) {
            if (SortType.equals("0")) {
                radioBtnSortTypeAsc.setChecked(true);
            } else if (SortType.equals("1")) {
                radioBtnSortTypeDesc.setChecked(true);
            }
        }
        radioGroupDaysType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.radioButton_DaysType_LowToHigh){
                    SortType = "0";
                }else if(checkedId == R.id.radioButton_DaysType_HighToLow){
                    SortType = "1";
                }
            }
        });
        //TODO: Spinner Range Days
        spnRangeDays.setSelection((RangeDaysType.isEmpty()? 0 :Integer.valueOf(RangeDaysType)));
        spnRangeDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                if (position == 0){
                    RangeDaysType = String.valueOf(position);
                }else {
                    RangeDaysType = String.valueOf(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Edit Text Range Days
        final EditText editTextFromDays = new EditText(context);
        editTextFromDays.setHint("Days");
        editTextFromDays.setSelectAllOnFocus(true);
        editTextFromDays.setEms(10);
        editTextFromDays.setText(""+RangeFromDays);
        editTextFromDays.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        editTextFromDays.setPadding(10, 10, 10, 10);
        editTextFromDays.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextFromDays.setBackground(context.getResources().getDrawable(R.drawable.edittext_border));
        LinearLayoutFromRangeDays.addView(editTextFromDays);

        final EditText editTextToDays = new EditText(context);
        editTextToDays.setHint("Days");
        editTextToDays.setEms(10);
        editTextToDays.setSelectAllOnFocus(true);
        editTextToDays.setText(""+RangeToDays);
        editTextToDays.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        editTextToDays.setPadding(10, 10, 10, 10);
        editTextToDays.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextToDays.setBackground(context.getResources().getDrawable(R.drawable.edittext_border));
        LinearLayoutToRangeDays.addView(editTextToDays);
//        edtFromRangeDays.setText(""+RangeFromDays);
//        edtToRangeDays.setText(""+RangeToDays);
        editTextFromDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String ToRange = editTextToDays.getText().toString();
                if (!ToRange.isEmpty() && !editable.toString().isEmpty()){
                    int fRange = Integer.valueOf(editable.toString());
                    int tRange = Integer.valueOf(ToRange);
                    if (fRange > tRange)
                    Toast.makeText(context, "From Days is not greater than To Days", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editTextToDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String FromRange = editTextFromDays.getText().toString();
                if (!FromRange.isEmpty() && !editable.toString().isEmpty()){
                    int fRange = Integer.valueOf(FromRange);
                    int tRange = Integer.valueOf(editable.toString());
                    if (fRange > tRange)
                        Toast.makeText(context, "To Days is not less than From Days", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO: Clear Sorting
        txtViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Stock Type Clear
                spnStockType.setSelection(1);
                StockType = 1;
                //TODO: Stock Availability Clear
                radioBtnSAYes.setChecked(true);
                StockAvailability = 1;
                //TODO: Stock Range Clear
//                edtFromRangeStock.setText(""+MinStock);
//                edtToRangeStock.setText(""+MaxStock);
                minSeekStock=0;
                maxSeekStock=0;
                minSeekStock=((minSeekStock==0)?MinStock:minSeekStock);
                maxSeekStock=((maxSeekStock==0)?MaxStock:maxSeekStock);
                txtViewMinStock.setText(""+MinStock);
                txtViewMaxStock.setText(""+MaxStock);
                rangeSeekBarStockRange.setRangeValues(MinStock, MaxStock); // if we want to set progrmmatically set range of seekbar
                rangeSeekBarStockRange.setSelectedMinValue(minSeekStock);
                rangeSeekBarStockRange.setSelectedMaxValue(maxSeekStock);
                //TODO: Stock Order Clear
                radioBtnSOLowHigh.setChecked(false);
                radioBtnSOHighLow.setChecked(false);
                StockOrder = 0;
                //TODO: Image Available Clear
                radioBtnIAAll.setChecked(true);
                ImageAvailable = 0;
                //TODO: Price Range Clear
//                edtFromRangePrice.setText(""+MinRate);
//                edtToRangePrice.setText(""+MaxRate);
                minSeekPrice=0;
                maxSeekPrice=0;
                minSeekPrice=((minSeekPrice==0)?MinRate:minSeekPrice);
                maxSeekPrice=((maxSeekPrice==0)?MaxRate:maxSeekPrice);
                txtViewMin.setText(""+MinRate);
                txtViewMax.setText(""+MaxRate);
                rangeSeekBarPriceRange.setRangeValues(MinRate , MaxRate); // if we want to set progrmmatically set range of seekbar
                rangeSeekBarPriceRange.setSelectedMinValue(minSeekPrice);
                rangeSeekBarPriceRange.setSelectedMaxValue(maxSeekPrice);
                //TODO: Price Order Clear
                radioBtnPOLowHigh.setChecked(false);
                radioBtnPOHighLow.setChecked(false);
                PriceOrder = 0;
                //TODO: Range Seekbar Flag
                rangeSeekBarFlag = 0;
                //TODO: Sort Days
                SortDays = "";
                //TODO: Sort Type
                SortType = "";
                //TODO: Range Days Type
                RangeDaysType = "";
                //TODO: Range Days
                RangeFromDays = "";
                RangeToDays = "";
                editTextFromDays.setText("");
                editTextToDays.setText("");
            }
        });
        //TODO: Ok Button
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=1;
                end=30;
                Keyword=" for your Sort & Filter";
                //TODO: AsyncItemList Execute SortBy Price
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        DatasetList = new ArrayList<>();
//                        minSeekStock = Double.parseDouble((edtFromRangeStock.getText().toString().isEmpty() ? MinStock+"" : edtFromRangeStock.getText().toString()));
//                        maxSeekStock = Double.parseDouble((edtToRangeStock.getText().toString().isEmpty() ? MaxStock+"" : edtToRangeStock.getText().toString()));
                        if (minSeekStock == MinStock && maxSeekStock == MaxStock){
                            minSeekStock = 0;
                            maxSeekStock = 0;
                        }
//                        minSeekPrice = Double.parseDouble((edtFromRangePrice.getText().toString().isEmpty() ? MinRate+"" : edtFromRangePrice.getText().toString()));
//                        maxSeekPrice = Double.parseDouble((edtToRangePrice.getText().toString().isEmpty() ? MaxRate+"" : edtToRangePrice.getText().toString()));
                        if (minSeekPrice == MinRate && maxSeekPrice == MaxRate){
                            minSeekPrice = 0;
                            maxSeekPrice = 0;
                        }
                        SummaryDataFlag = 1;

                        RangeFromDays = editTextFromDays.getText().toString();
                        RangeToDays = editTextToDays.getText().toString();

                        if (RangeFromDays.isEmpty() && RangeToDays.isEmpty()){
                            CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID, SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery, String.valueOf(StockAvailability), String.valueOf(ImageAvailable), String.valueOf(StockType), SortDays, SortType, RangeFromDays, RangeToDays, RangeDaysType);
                            dialog.dismiss();
                            fabMenu.close(true);
                        }else if (!RangeFromDays.isEmpty() && RangeToDays.isEmpty()){
                            MessageDialog.MessageDialog(context,"","To Days cann't be blank");
                        }else if (RangeFromDays.isEmpty() && !RangeToDays.isEmpty()){
                            MessageDialog.MessageDialog(context,"","From Days cann't be blank");
                        }else if (!RangeFromDays.isEmpty() && !RangeToDays.isEmpty()){
                            if (Integer.valueOf(RangeFromDays) < Integer.valueOf(RangeToDays)) {
                                //MessageDialog.MessageDialog(context,"","Min Stock:"+minSeekStock+" Max Stock:"+maxSeekStock+"\nMin Price:"+minSeekPrice+" Max Price:"+maxSeekPrice);
                                //CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", StaticValues.SearchQuery,"1",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                                CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID, SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder), String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, String.valueOf(SummaryDataFlag), StaticValues.SearchQuery, String.valueOf(StockAvailability), String.valueOf(ImageAvailable), String.valueOf(StockType), SortDays, SortType, RangeFromDays, RangeToDays, RangeDaysType);
                                dialog.dismiss();
                                fabMenu.close(true);

                            }else{
                                MessageDialog.MessageDialog(context,"","From Days is not greater than To Days");
                            }
                        }
                        //TODO: Range Seekbar Flag
                        rangeSeekBarFlag = 1;
                    }
                }else{
                    Snackbar.make(txtViewClear,""+status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
//        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//                // TODO Auto-generated method stub
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.dismiss();
//                    fabMenu.close(true);
//                }
//                return true;
//            }
//        });
    }
    private void DialogFilter(){
        final Dialog dialog=new Dialog(context,R.style.FullHeightDialog);//final Dialog dialog=new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_filter);
        dialog.show();
        ImageView imageViewClose=(ImageView) dialog.findViewById(R.id.imageView_close);
        final TextView txtViewApply=(TextView) dialog.findViewById(R.id.textView_Apply);
        TextView txtViewclear=(TextView) dialog.findViewById(R.id.textView_Clear);
        listViewTab=(ListView) dialog.findViewById(R.id.listViewTab);
        listViewCheckBox=(ListView) dialog.findViewById(R.id.listViewCheckBox);
        txtViewApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Keyword=" for your Sort & Filter";
                strFilter=new String[14];
                strFilter=DBHandler.ApplyFilterInventory(14);
                final String GroupID = getIntent().getExtras().getString("GroupID");
                start=1;
                end=30;
                //TODO: AsyncItemList Execute filter
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        DatasetList = new ArrayList<>();
                        if (minSeekStock == MinStock && maxSeekStock == MaxStock){
                            minSeekStock = 0;
                            maxSeekStock = 0;
                        }
                        if (minSeekPrice == MinRate && maxSeekPrice == MaxRate){
                            minSeekPrice = 0;
                            maxSeekPrice = 0;
                        }
                        SummaryDataFlag = 1;
                        //CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", StaticValues.SearchQuery,"1",String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType));
                        CallRetrofit(str[3], str[0], str[14], str[4], str[5], str[15], GroupID,SubGroupID, PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(PriceOrder),String.valueOf(minSeekPrice), String.valueOf(maxSeekPrice), String.valueOf(StockOrder), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, String.valueOf(SummaryDataFlag) , StaticValues.SearchQuery,String.valueOf(StockAvailability),String.valueOf(ImageAvailable),String.valueOf(StockType),SortDays,SortType,RangeFromDays,RangeToDays,RangeDaysType);
                    }
                }else{
                    Snackbar.make(txtViewApply,""+status,Snackbar.LENGTH_LONG).show();
                }
                dialog.dismiss();
                fabMenu.close(true);
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHandler.RestoreFilterFlag();
                dialog.dismiss();
                fabMenu.close(true);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    DBHandler.RestoreFilterFlag();
                    dialog.dismiss();
                    fabMenu.close(true);
                }
                return true;
            }
        });
        txtViewclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=1;
                end=30;
                DBHandler.UpdateFlagClear(0);
                filterMethod();
                strFilter=null;
            }
        });
        filterMethod();
    }
    //TODO: Filter Method
    private void filterMethod(){
        final List<Map<String,String>> CaptionWithSeqInventory=DBHandler.CaptionWithSeqInventory();
        FilterTabListAdapter filterTabListAdapter=new FilterTabListAdapter(ItemListActivity.this,CaptionWithSeqInventory);
        listViewTab.setAdapter(filterTabListAdapter);
        List<Map<String,String>> mapList=DBHandler.getColor(1);
        FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(ItemListActivity.this, mapList);
        listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
        listViewTab.setItemChecked(0, true);
        listViewTab.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewTab.setSelector(R.color.colorPrimary);
        listViewTab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<Map<String,String>> mapList=DBHandler.getColor(Integer.valueOf(CaptionWithSeqInventory.get(position).get("Seq")));
                FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(ItemListActivity.this, mapList);
                listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
            }
        });
    }
}
