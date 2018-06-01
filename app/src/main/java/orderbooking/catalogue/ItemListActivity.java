package orderbooking.catalogue;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionMenu;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.codehaus.jackson.node.BooleanNode;
import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerAddToBox;
import DatabaseController.DatabaseSqlLiteHandlerAllGroups;
import DatabaseController.DatabaseSqlLiteHandlerFilter;
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import info.hoang8f.android.segmented.SegmentedGroup;
import orderbooking.StaticValues;
import orderbooking.catalogue.adapter.FilterCheckBoxListAdapter;
import orderbooking.catalogue.adapter.FilterTabListAdapter;
import orderbooking.catalogue.adapter.RecyclerItemDataset;
import orderbooking.catalogue.adapter.RecyclerItemGridAdapter;
import orderbooking.catalogue.adapter.RecyclerItemGridColorAdapter;
import orderbooking.catalogue.addtobox.adapter.RecyclerBoxGroupAdapter;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxGroupDataset;
import orderbooking.catalogue.addtobox.dataset.ResponseBoxListDataset;
import orderbooking.catalogue.responsedataset.ResponseFilterDataset;
import orderbooking.catalogue.responsedataset.ResponseFilterSubDataset;
import orderbooking.catalogue.responsedataset.ResponseItemListMainDataset;
import orderbooking.catalogue.responsedataset.ResponseItemListSubDataset;
import orderbooking.catalogue.responseitemlist.ItemList;
import orderbooking.catalogue.responseitemlist.ResponseItemList;
import orderbooking.catalogue.responseitemlist.Result;
import orderbooking.catalogue.wishlist.GroupWishListActivity;
import orderbooking.catalogue.wishlist.adapter.RecyclerWishListGroupAdapter;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;
import orderbooking.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
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
    ProgressBar progressBar;
    FloatingActionMenu fabMenu;
    TextView txtViewGroupName,txtViewTotalStyle,txtFilter,txtSort;
    DatabaseSqlLiteHandlerFilter DBHandler;
    int priceStockFlag=0,priceFlag=0,stockFlag=0;
    int flag=0,v=0,filterLoadStatus=0,searchFlag=0,exchangeFlag=0;
    int MinRate=0,MaxRate=0,MinStock=0,MaxStock=0;
    int minSeekVal=0,maxSeekVal=0,minSeekStock=0,maxSeekStock=0;
    public static int listSize = 0;
    List<ItemList> DatasetList;
    List<Boolean> booleanList;
    List<orderbooking.catalogue.responseitemlistcolor.ItemList> DatasetListColor;
    int TotalStyle=0;
    ListView listViewTab,listViewCheckBox;
    String[] strFilter=new String[12];
    private int lastItem=0;
    public static int start=1,end=30;
    GridLayoutManager gridLayoutManager;
    RecyclerItemGridAdapter recyclerItemGridAdapter;
    RecyclerItemGridColorAdapter recyclerItemGridColorAdapter;
    LinearLayout linearLayoutPlaceOrderHeader;
    Dialog dialog;
    private static String TAG = ItemListActivity.class.getSimpleName();
    String Keyword="",GroupID="",PartyID="",SubPartyID="",RefName="",ItemListApi="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_itemlist);
        CatalogueActivity.deleteCache(getApplicationContext());
        StaticValues.SingleMultiViewFlag=0;
        ItemListApi=StaticValues.ItemListApi;
        DBHandler=new DatabaseSqlLiteHandlerFilter(getApplicationContext());
        DBHandler.FilterTableDelete();
        DatasetList = new ArrayList<>();
        DatasetListColor = new ArrayList<>();
        booleanList = new ArrayList<>();
        listSize = 0;
        Initialization();
        GetDataByIntentMethod();
        CallApiMethod();
        handleIntent(this.getIntent());
        DatabaseSqlLiteHandlerAllGroups DBGroup=new DatabaseSqlLiteHandlerAllGroups(getApplicationContext());
        String str=DBGroup.getGroupName(GroupID);
        txtViewGroupName.setText(""+str);
    }
    private void Initialization() {
        this.context = ItemListActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        gridViewItemList = (RecyclerView) findViewById(R.id.recyclerGrid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Circle);
        txtViewGroupName = (TextView) findViewById(R.id.textView_GroupName);
        txtViewTotalStyle = (TextView) findViewById(R.id.textView_TotalStyle);
        txtFilter = (TextView) findViewById(R.id.text_Filter);
        txtFilter.setVisibility(View.VISIBLE);
        txtSort = (TextView) findViewById(R.id.text_Sort);
        txtSort.setVisibility(View.VISIBLE);
        fabMenu = (FloatingActionMenu) findViewById(R.id.menu_fab);
        //fabMenu.setVisibility(View.VISIBLE);
    }
    private void GetDataByIntentMethod(){
        try {
            GroupID = getIntent().getExtras().getString("GroupID", "");
            closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("PartyDetails");

            if (closeOrBookDataset != null) {
                PartyID = closeOrBookDataset.getPartyID();
                CommanStatic.PriceListID = closeOrBookDataset.getPricelistID();
                //SubPartyID = selectCustomerForOrderDataset.getPartyID();
                //RefName = selectCustomerForOrderDataset.getPartyID();
                actionBar.setTitle(closeOrBookDataset.getPartyName());
            } else {
                MessageDialog.MessageDialog(context, "Intent", "Get intent is null");
            }
            //TODO: Pre-Filter
            Map<String,String> preFilter =  (Map<String,String>) getIntent().getExtras().get("PreFilter");

            if(preFilter!=null){
                strFilter = new String[12];
                for(int i=0; i < strFilter.length; i++){
                    if(i == 0) {
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
                        strFilter[0] = attr;
                    }else if(i == 1) {
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
                        strFilter[1] = attr;
                    }else if(i > 1) {
                        //TODO: Attr 1 to 10
                        if (preFilter.get("Attr" + (i - 1)) != null) {
                            String attr = "";
                            if (!preFilter.get("Attr" +(i - 1)).isEmpty()){

                                if(preFilter.get("Attr" + (i - 1)).contains(",")) {
                                    String[] str = preFilter.get("Attr" + (i - 1)).split(",");
                                    if (str.length > 0) {
                                        for (int j = 0; j < str.length; j++) {
                                            attr += "'" + str[j] + "',";
                                        }
                                    }
                                }else{
                                    attr += "'" + preFilter.get("Attr" +(i - 1)) + "',";
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
    private void CallApiMethod(){
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //TODO: CallRetrofit Item List
            if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1");
            }
            //TODO: CallRetrofit Filtr
            if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofitFilter(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID);
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
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1");
                    }
                    //TODO: CallRetrofit Filtr
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        CallRetrofitFilter(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID);
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
                    if(!DatasetList.isEmpty())
                        DialogSort();
                }else{
                    if(!DatasetListColor.isEmpty())
                        DialogSort();
                }
                break;
            default:
                break;
        }
    }
    //TODO:Asyc MainGroup
    private void CallRetrofit(String AKey, String Password, String GroupID, String PriceListID, String start, String end, String priceFlag, String minRate, String maxRate, String stockFlag, String minSt, String maxSt, String[] Attr, String TSFlg, final String SFFlg, String SearchQuery,String SliderDataFlag){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("GroupID", GroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("PriceSort", priceFlag);
        params.put("minRate", minRate);
        params.put("maxRate", maxRate);
        params.put("StockSort", stockFlag);
        params.put("minStock", minSt);
        params.put("maxStock", maxSt);
        String CF=(Attr[0]==null || Attr[0].isEmpty())?"":Attr[0].substring(0,Attr[0].length()-1);
        params.put("CF", CF);
        String SZ=(Attr[1]==null || Attr[1].isEmpty())?"":Attr[1].substring(0,Attr[1].length()-1);
        params.put("SZ", SZ);
        String Attr2=(Attr[2]==null || Attr[2].isEmpty())?"":Attr[2].substring(0,Attr[2].length()-1);
        params.put("Attr1", Attr2);
        String Attr3=(Attr[3]==null || Attr[3].isEmpty())?"":Attr[3].substring(0,Attr[3].length()-1);
        params.put("Attr2", Attr3);
        String Attr4=(Attr[4]==null || Attr[4].isEmpty())?"":Attr[4].substring(0,Attr[4].length()-1);
        params.put("Attr3", Attr4);
        String Attr5=(Attr[5]==null || Attr[5].isEmpty())?"":Attr[5].substring(0,Attr[5].length()-1);
        params.put("Attr4", Attr5);
        String Attr6=(Attr[6]==null || Attr[6].isEmpty())?"":Attr[6].substring(0,Attr[6].length()-1);
        params.put("Attr5", Attr6);
        String Attr7=(Attr[7]==null || Attr[7].isEmpty())?"":Attr[7].substring(0,Attr[7].length()-1);
        params.put("Attr6", Attr7);
        String Attr8=(Attr[8]==null || Attr[8].isEmpty())?"":Attr[8].substring(0,Attr[8].length()-1);
        params.put("Attr7", Attr8);
        String Attr9=(Attr[9]==null || Attr[9].isEmpty())?"":Attr[9].substring(0,Attr[9].length()-1);
        params.put("Attr8", Attr9);
        String Attr10=(Attr[10]==null || Attr[10].isEmpty())?"":Attr[10].substring(0,Attr[10].length()-1);
        params.put("Attr9", Attr10);
        String Attr11=(Attr[11]==null || Attr[11].isEmpty())?"":Attr[11].substring(0,Attr[11].length()-1);
        params.put("Attr10", Attr11);
        params.put("TSFlg", TSFlg);
        params.put("SFFlg", SFFlg);
        params.put("Search", SearchQuery);
        params.put("SliderDataFlag", SliderDataFlag);
        Log.d(TAG,"ItemList Parameters:"+params.toString());
        if (ItemListApi.equals("ItemList") && !ItemListApi.isEmpty()){
            Call<ResponseItemList> call = apiService.getItemList(params);
            call.enqueue(new Callback<ResponseItemList>() {
                @Override
                public void onResponse(Call<ResponseItemList> call, retrofit2.Response<ResponseItemList> response) {
                    try {
                        TotalStyle = 0;
                        DatasetList = new ArrayList<>();
                        DatasetListColor = new ArrayList<>();
                        booleanList = new ArrayList<>();
                        listSize = 0;
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                Result result = response.body().getResult();
                                TotalStyle = result.getTotalStyle();
                                DatasetList = result.getItemList();
                                for(int i=0; i<result.getItemList().size(); i++){
                                    booleanList.add(false);
                                }
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
                                MinRate = result.getMinRate();
                                MaxRate = result.getMaxRate();
                                MinStock = result.getMinStock();
                                MaxStock = result.getMaxStock();
                            }  else {
                                TotalStyle = 0;
                                txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
                                Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                            }
                        }else {
                            TotalStyle = 0;
                            txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                            ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
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
            Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call = apiService.getItemListColor(params);
            call.enqueue(new Callback<orderbooking.catalogue.responseitemlistcolor.ResponseItemList>() {
                @Override
                public void onResponse(Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call, retrofit2.Response<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> response) {
                    try {
                        TotalStyle = 0;
                        DatasetList = new ArrayList<>();
                        DatasetListColor = new ArrayList<>();
                        booleanList = new ArrayList<>();
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                orderbooking.catalogue.responseitemlistcolor.Result result = response.body().getResult();
                                TotalStyle = result.getTotalStyle();
                                DatasetListColor = result.getItemList();
                                for(int i=0; i<result.getItemList().size(); i++){
                                    booleanList.add(false);
                                }
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
                                MinRate = result.getMinRate();
                                MaxRate = result.getMaxRate();
                                MinStock = result.getMinStock();
                                MaxStock = result.getMaxStock();
                            } else {
                                TotalStyle = 0;
                                txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                                ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
                                Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                            }
                        }else {
                            TotalStyle = 0;
                            txtViewTotalStyle.setText(TotalStyle+"/" + TotalStyle);
                            ItemViewChanger(DatasetList,DatasetListColor, TotalStyle,booleanList);
                            Snackbar.make(gridViewItemList,"No Result Found !!!",Snackbar.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Log.e(TAG,"ItemList Exception:"+e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }
    }
    private void CallRetrofitFilter(String AKey, String Password, String GroupID, String PriceListID){
        //showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("GroupID", GroupID);
        params.put("PriceListID", PriceListID);
        Call<ResponseFilterDataset> call = apiService.getFilter(params);
        call.enqueue(new Callback<ResponseFilterDataset>() {
            @Override
            public void onResponse(Call<ResponseFilterDataset> call, retrofit2.Response<ResponseFilterDataset> response) {
                try {
                    if(response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            ResponseFilterSubDataset subDataset = response.body().getResult();
                            if (filterLoadStatus == 0) {
                                List<Map<String, String>> mapListFilter = subDataset.getFilter();
                                DatabaseSqlLiteHandlerFilter DBFilter = new DatabaseSqlLiteHandlerFilter(getApplicationContext());
                                DBFilter.FilterTableDelete();
                                DBFilter.insertFilterTable(mapListFilter);
                                List<Map<String, String>> mapListFilterCaption = subDataset.getFilterCaption();
                                StaticValues.FilterCaptionSequence = new String[12];
                                StaticValues.FilterAttributeSequence = new String[12];
                                for (int i = 0; i < mapListFilterCaption.size(); i++) {
                                    if (mapListFilterCaption.get(i).get("Name") != null) {
                                        StaticValues.FilterCaptionSequence[i] = mapListFilterCaption.get(i).get("Name");
                                        StaticValues.FilterAttributeSequence[i] = mapListFilterCaption.get(i).get("Seq");
                                    }
                                }
                                filterLoadStatus = 1;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        //ItemViewChanger(DatasetList, TotalStyle);
                        Snackbar.make(gridViewItemList,""+response.message()+" Response:"+response.code(),Snackbar.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e(TAG,"Filter Exception:"+e.getMessage());
                }
                //hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseFilterDataset> call, Throwable t) {
                Log.e(TAG,"Filter Failure:"+t.toString());
                Toast.makeText(getApplicationContext(),"Filter Failure",Toast.LENGTH_LONG).show();
                //hidepDialog();
            }
        });
    }
    private void CallRetrofitOnScroll(String AKey,String Password,String GroupID,String PriceListID,String start,String end,String priceFlag,String minRate,String maxRate,String stockFlag,String minSt,String maxSt,String[] Attr,String TSFlg,String SFFlg,String SearchQuery,String SliderDataFlag){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("GroupID", GroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("PriceSort", priceFlag);
        params.put("minRate", minRate);
        params.put("maxRate", maxRate);
        params.put("StockSort", stockFlag);
        params.put("minStock", minSt);
        params.put("maxStock", maxSt);
        String CF=(Attr[0]==null || Attr[0].isEmpty())?"":Attr[0].substring(0,Attr[0].length()-1);
        params.put("CF", CF);
        String SZ=(Attr[1]==null || Attr[1].isEmpty())?"":Attr[1].substring(0,Attr[1].length()-1);
        params.put("SZ", SZ);
        String Attr2=(Attr[2]==null || Attr[2].isEmpty())?"":Attr[2].substring(0,Attr[2].length()-1);
        params.put("Attr1", Attr2);
        String Attr3=(Attr[3]==null || Attr[3].isEmpty())?"":Attr[3].substring(0,Attr[3].length()-1);
        params.put("Attr2", Attr3);
        String Attr4=(Attr[4]==null || Attr[4].isEmpty())?"":Attr[4].substring(0,Attr[4].length()-1);
        params.put("Attr3", Attr4);
        String Attr5=(Attr[5]==null || Attr[5].isEmpty())?"":Attr[5].substring(0,Attr[5].length()-1);
        params.put("Attr4", Attr5);
        String Attr6=(Attr[6]==null || Attr[6].isEmpty())?"":Attr[6].substring(0,Attr[6].length()-1);
        params.put("Attr5", Attr6);
        String Attr7=(Attr[7]==null || Attr[7].isEmpty())?"":Attr[7].substring(0,Attr[7].length()-1);
        params.put("Attr6", Attr7);
        String Attr8=(Attr[8]==null || Attr[8].isEmpty())?"":Attr[8].substring(0,Attr[8].length()-1);
        params.put("Attr7", Attr8);
        String Attr9=(Attr[9]==null || Attr[9].isEmpty())?"":Attr[9].substring(0,Attr[9].length()-1);
        params.put("Attr8", Attr9);
        String Attr10=(Attr[10]==null || Attr[10].isEmpty())?"":Attr[10].substring(0,Attr[10].length()-1);
        params.put("Attr9", Attr10);
        String Attr11=(Attr[11]==null || Attr[11].isEmpty())?"":Attr[11].substring(0,Attr[11].length()-1);
        params.put("Attr10", Attr11);
        params.put("TSFlg", TSFlg);
        params.put("SFFlg", SFFlg);
        params.put("Search", SearchQuery);
        params.put("SliderDataFlag", SliderDataFlag);
        Log.d(TAG,"ItemList Scroll Parameters:"+params.toString());
        if (ItemListApi.equals("ItemList") && !ItemListApi.isEmpty()){
            Call<ResponseItemList> call = apiService.getItemList(params);
            call.enqueue(new Callback<ResponseItemList>() {
                @Override
                public void onResponse(Call<ResponseItemList> call, retrofit2.Response<ResponseItemList> response) {
                    try {
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                Result result = response.body().getResult();
                                DatasetList.addAll(result.getItemList());
                                List<Boolean> booleanList1 = new ArrayList<>();
                                for(int i=0; i<result.getItemList().size(); i++){
                                    booleanList1.add(false);
                                }
                                booleanList.addAll(booleanList1);
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
                    Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        } else {
            Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call = apiService.getItemListColor(params);
            call.enqueue(new Callback<orderbooking.catalogue.responseitemlistcolor.ResponseItemList>() {
                @Override
                public void onResponse(Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call, retrofit2.Response<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> response) {
                    try {
                        if (response.isSuccessful()) {
                            int status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (status == 1) {
                                orderbooking.catalogue.responseitemlistcolor.Result result = response.body().getResult();
                                DatasetListColor.addAll(result.getItemList());
                                listSize = DatasetListColor.size();
                                List<Boolean> booleanList1 = new ArrayList<>();
                                for(int i=0; i<result.getItemList().size(); i++){
                                    booleanList1.add(false);
                                }
                                booleanList.addAll(booleanList1);
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
                public void onFailure(Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> call, Throwable t) {
                    Log.e(TAG,"Failure:"+t.toString());
                    Toast.makeText(getApplicationContext(),"Item List Scroll Failure",Toast.LENGTH_LONG).show();
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
        invalidateOptionsMenu();
    }
    @Override
    public void onPause(){
        super.onPause();
        invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem menuItem = menu.findItem(R.id.action_account);
        menuItem.setVisible(false);
        MenuItem menuWhatsApp = menu.findItem(R.id.action_whatsapp);
        if (StaticValues.exportFlag ==1) {
            menuWhatsApp.setVisible(true);
        }
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
                String GroupID = getIntent().getExtras().getString("GroupID");
                searchFlag=1;
                start = 1;
                end = 30;
                //TODO: CallRetrofit Item List
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    Log.d(TAG,"Search:"+StaticValues.SearchQuery);
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1");
                    //MakePostItemList(ApiClient.BASE_URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", CommanStatic.SearchQuery);
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
                final String GroupID = getIntent().getExtras().getString("GroupID");
                //TODO: CallRetrofit Item List
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1");
                    //MakePostItemList(ApiClient.BASE_URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", CommanStatic.SearchQuery);
                }
                return false;
            }
        });
        MenuItem box = menu.findItem(R.id.action_box);
        CatalogueActivity.iconBox = (LayerDrawable) box.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(ItemListActivity.this, CatalogueActivity.iconBox, StaticValues.totalBoxCount);

        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        CatalogueActivity.iconWishlist = (LayerDrawable) wishlist.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(ItemListActivity.this, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
        return super.onCreateOptionsMenu(menu);
    }
    //TODO:Searching
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
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    //StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, GroupWishListActivity.class);
                    intent.putExtra("Key",closeOrBookDataset);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select Running Customer first");
                }
                 break;
            case R.id.action_box:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, OrderViewOrPushActivity.class);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select ruuning Customer first");
                }
                 //CustomDialogBox(1,"My Box", 1);
                 break;
            case R.id.action_whatsapp:
                //TODO: Share on Whats App
                ShareOnWhatsApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void ItemViewChanger(final List<ItemList> list, final List<orderbooking.catalogue.responseitemlistcolor.ItemList> listColor, final int totalstyle, final List<Boolean> checkedList){
        invalidateOptionsMenu();
        gridViewItemList.removeAllViewsInLayout();
        gridViewItemList.removeAllViews();
        Context context=ItemListActivity.this;
        if (ItemListApi.equals("ItemList")) {
            recyclerItemGridAdapter=new RecyclerItemGridAdapter(context,list,flag,closeOrBookDataset,checkedList);
            gridViewItemList.setAdapter(recyclerItemGridAdapter);
        }else{
            recyclerItemGridColorAdapter=new RecyclerItemGridColorAdapter(context,listColor,flag,closeOrBookDataset,checkedList);
            gridViewItemList.setAdapter(recyclerItemGridColorAdapter);
        }
        if(flag==0){
            gridLayoutManager=new GridLayoutManager(context,2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridViewItemList.setLayoutManager(gridLayoutManager);
            gridViewItemList.setHasFixedSize(true);
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
                    //TODO: onScroll Execute filter
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            CallRetrofitOnScroll(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", StaticValues.SearchQuery,"0");
                            //MakePostItemListOnScroll(CommanStatic.URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", CommanStatic.SearchQuery);
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
                        if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            CallRetrofitOnScroll(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", StaticValues.SearchQuery,"0");
                            //MakePostItemListOnScroll(CommanStatic.URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "0", "0", CommanStatic.SearchQuery);
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
                        if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            String GroupID = getIntent().getExtras().getString("GroupID");
                            start=1;
                            end=30;
                            CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), "0", "0", String.valueOf(stockFlag), "0", "0", strFilter, "1", "0", StaticValues.SearchQuery,"1");
                            dialog.dismiss();
                            exchangeFlag = 0;
                        }
                    } else {
                        Snackbar.make(gridViewItemList, status + "", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    ItemViewChanger(DatasetList,DatasetListColor,TotalStyle,booleanList);
                    dialog.dismiss();
                }
            }
        });
    }
    private void DialogSort(){
        final Dialog dialog=new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_sort);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setTitle("Sort by price or stock");
        dialog.show();
        final SegmentedGroup radioGroup=(SegmentedGroup) dialog.findViewById(R.id.radioGroup);
        final RangeSeekBar rangeSeekBar=(RangeSeekBar) dialog.findViewById(R.id.seekbar);
        final TextView txtViewClear=(TextView) dialog.findViewById(R.id.textView_Clear);
        final TextView txtViewMin=(TextView) dialog.findViewById(R.id.textView_minimumRange);
        final TextView txtViewMax=(TextView) dialog.findViewById(R.id.textView_maximumRange);
        final RangeSeekBar rangeSeekBarStock=(RangeSeekBar) dialog.findViewById(R.id.seekbar_Stock);
        final TextView txtViewMinStock=(TextView) dialog.findViewById(R.id.textView_minimumRangeStock);
        final TextView txtViewMaxStock=(TextView) dialog.findViewById(R.id.textView_maximumRangeStock);
        Button btnYes=(Button) dialog.findViewById(R.id.btn_yes);
        Button btnNo=(Button) dialog.findViewById(R.id.btn_no);
        txtViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioBtn=(RadioButton) dialog.findViewById(R.id.radioButton_LowToHigh);
                radioBtn.setChecked(false);
                RadioButton radioBtn1=(RadioButton) dialog.findViewById(R.id.radioButton_HighToLow);
                radioBtn1.setChecked(false);
                RadioButton radioBtnSt=(RadioButton) dialog.findViewById(R.id.radioButton_LowToHighStock);
                radioBtnSt.setChecked(false);
                RadioButton radioBtnSt1=(RadioButton) dialog.findViewById(R.id.radioButton_HighToLowStock);
                radioBtnSt1.setChecked(false);
                priceFlag=0;
                minSeekVal=0;
                maxSeekVal=0;
                stockFlag=0;
                minSeekStock=0;
                maxSeekStock=0;
                minSeekVal=((minSeekVal==0)?MinRate:minSeekVal);
                maxSeekVal=((maxSeekVal==0)?MaxRate:maxSeekVal);
                minSeekStock=((minSeekStock==0)?MinStock:minSeekStock);
                maxSeekStock=((maxSeekStock==0)?MaxStock:maxSeekStock);
                txtViewMin.setText(""+MinRate);
                txtViewMax.setText(""+MaxRate);
                txtViewMinStock.setText(""+MinStock);
                txtViewMaxStock.setText(""+MaxStock);
                rangeSeekBar.setRangeValues(MinRate, MaxRate); // if we want to set progrmmatically set range of seekbar
                rangeSeekBar.setSelectedMinValue(minSeekVal);
                rangeSeekBar.setSelectedMaxValue(maxSeekVal);
                rangeSeekBarStock.setRangeValues(MinStock, MaxStock); // if we want to set progrmmatically set range of seekbar
                rangeSeekBarStock.setSelectedMinValue(minSeekStock);
                rangeSeekBarStock.setSelectedMaxValue(maxSeekStock);
            }
        });
        final RadioButton radioBtn1=(RadioButton) dialog.findViewById(R.id.radioButton_LowToHigh);
        final RadioButton radioBtn2=(RadioButton) dialog.findViewById(R.id.radioButton_HighToLow);
        final RadioButton radioBtn3=(RadioButton) dialog.findViewById(R.id.radioButton_LowToHighStock);
        final RadioButton radioBtn4=(RadioButton) dialog.findViewById(R.id.radioButton_HighToLowStock);
        if(priceStockFlag==1){
            radioBtn1.setChecked(true);
        } else if(priceStockFlag==2){
            radioBtn2.setChecked(true);
        } else if(priceStockFlag==3){
            radioBtn3.setChecked(true);
        } else if(priceStockFlag==4){
            radioBtn4.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == R.id.radioButton_LowToHigh){
                    priceStockFlag=1;
                }else if(checkedId == R.id.radioButton_HighToLow){
                    priceStockFlag=2;
                }else if(checkedId == R.id.radioButton_LowToHighStock){
                    priceStockFlag=3;
                }else if(checkedId == R.id.radioButton_HighToLowStock){
                    priceStockFlag=4;
                }
            }
        });
        minSeekVal=((minSeekVal==0)?MinRate:minSeekVal);
        maxSeekVal=((maxSeekVal==0)?MaxRate:maxSeekVal);
        minSeekStock=((minSeekStock==0)?MinStock:minSeekStock);
        maxSeekStock=((maxSeekStock==0)?MaxStock:maxSeekStock);
        txtViewMin.setText(""+MinRate);
        txtViewMax.setText(""+MaxRate);
        txtViewMinStock.setText(""+MinStock);
        txtViewMaxStock.setText(""+MaxStock);
        rangeSeekBar.setRangeValues(MinRate, MaxRate); // if we want to set progrmmatically set range of seekbar
        rangeSeekBar.setSelectedMinValue(minSeekVal);
        rangeSeekBar.setSelectedMaxValue(maxSeekVal);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Log.e("value", minValue + "  " + maxValue);
                minSeekVal = minValue;
                maxSeekVal = maxValue;
            }
        });
        rangeSeekBarStock.setRangeValues(MinStock, MaxStock); // if we want to set progrmmatically set range of seekbar
        rangeSeekBarStock.setSelectedMinValue(minSeekStock);
        rangeSeekBarStock.setSelectedMaxValue(maxSeekStock);
        rangeSeekBarStock.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Log.e("value", minValue + "  " + maxValue);
                minSeekStock = minValue;
                maxSeekStock = maxValue;
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(priceStockFlag == 1){
                    priceFlag = 1;
                    stockFlag = 0;
                }else if(priceStockFlag == 2){
                    priceFlag = 2;
                    stockFlag = 0;
                }else if(priceStockFlag == 3){
                    priceFlag = 0;
                    stockFlag = 1;
                }else if(priceStockFlag == 4){
                    priceFlag = 0;
                    stockFlag = 2;
                }
                final String GroupID = getIntent().getExtras().getString("GroupID");
                start=1;
                end=30;
                Keyword=" for your Sort & Filter";
                //TODO: AsyncItemList Execute SortBy Price
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        //Log.e(TAG,CommanStatic.DeviceID+"\n"+CommanStatic.DevicePassword+"\n"+GroupID+"\n"+CommanStatic.PriceListID+"\n"+ String.valueOf(start)+"\n"+ String.valueOf(end)+"\n"+ String.valueOf(priceFlag)+"\n"+ String.valueOf(minSeekVal)+"\n"+ String.valueOf(maxSeekVal)+"\n"+ String.valueOf(stockFlag)+"\n"+ String.valueOf(minSeekStock)+"\n"+ String.valueOf(maxSeekStock)+"\n"+ strFilter+"\n"+ "0"+"\n"+ "0"+"\n"+ CommanStatic.SearchQuery);
                        DatasetList = new ArrayList<>();
                        DatasetListColor = new ArrayList<>();
                        listSize = DatasetList.size();
                        listSize = DatasetListColor.size();
                        //DatasetListColor = new ArrayList<itemlist.responseitemlistcolor.ItemList>();
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", StaticValues.SearchQuery,"1");
                        //MakePostItemList(CommanStatic.URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", CommanStatic.SearchQuery);
                    }
                }else{
                    Snackbar.make(txtViewClear,""+status,Snackbar.LENGTH_LONG).show();
                }
                dialog.dismiss();
                fabMenu.close(true);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fabMenu.close(true);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    fabMenu.close(true);
                }
                return true;
            }
        });
    }
    private void DialogFilter(){
        final Dialog dialog=new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
                strFilter=new String[12];
                strFilter=DBHandler.ApplyFilter(12);
                final String GroupID = getIntent().getExtras().getString("GroupID");
                start=1;
                end=30;
                //TODO: AsyncItemList Execute filter
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", StaticValues.SearchQuery,"1");
                        //MakePostItemList(ApiClient.BASE_URL+"ItemList",CommanStatic.DeviceID, CommanStatic.DevicePassword, GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), String.valueOf(priceFlag), String.valueOf(minSeekVal), String.valueOf(maxSeekVal), String.valueOf(stockFlag), String.valueOf(minSeekStock), String.valueOf(maxSeekStock), strFilter, "1", "0", CommanStatic.SearchQuery);
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
        final List<Map<String,String>> CaptionWithSeq=DBHandler.CaptionWithSeq();
        FilterTabListAdapter filterTabListAdapter=new FilterTabListAdapter(ItemListActivity.this,CaptionWithSeq);
        listViewTab.setAdapter(filterTabListAdapter);
        List<Map<String,String>> mapList=DBHandler.getColor(0);
        FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(ItemListActivity.this, mapList);
        listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
        listViewTab.setItemChecked(0, true);
        listViewTab.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewTab.setSelector(R.color.colorPrimary);
        listViewTab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<Map<String,String>> mapList=DBHandler.getColor(Integer.valueOf(CaptionWithSeq.get(position).get("Seq")));
                FilterCheckBoxListAdapter filterCheckBoxListAdapter = new FilterCheckBoxListAdapter(ItemListActivity.this, mapList);
                listViewCheckBox.setAdapter(filterCheckBoxListAdapter);
            }
        });
    }
    private void DialogLoginCatalogue(Context context){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_catalogue_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final LinearLayout linearLayoutSignIn = (LinearLayout)  dialog.findViewById(R.id.Linear_SignIn);
        final LinearLayout linearLayoutSignOut = (LinearLayout)  dialog.findViewById(R.id.Linear_SignOut);
        TextView textViewUserName = (TextView) dialog.findViewById(R.id.text_UserName);
        TextView textViewNewCustomer = (TextView) dialog.findViewById(R.id.text_new_customer);
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
        CommonSearchableSpinner spnRunningCustomer = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_running_customer);
        final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        textViewUserName.setText("Rakesh Sharma");
        if(CatalogueActivity.loginFlag == 0){
            linearLayoutSignOut.setVisibility(View.GONE);
            linearLayoutSignIn.setVisibility(View.VISIBLE);
            btnApply.setText("Sign in");
        }else if(CatalogueActivity.loginFlag == 1){
            linearLayoutSignOut.setVisibility(View.VISIBLE);
            linearLayoutSignIn.setVisibility(View.GONE);
            btnApply.setText("Sign out");
        }
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CatalogueActivity.loginFlag==0) {
                    CatalogueActivity.loginFlag = 1;
                    linearLayoutSignOut.setVisibility(View.VISIBLE);
                    linearLayoutSignIn.setVisibility(View.GONE);
                    btnApply.setText("Sign out");
                }else if (CatalogueActivity.loginFlag==1){
                    CatalogueActivity.loginFlag = 0;
                    linearLayoutSignOut.setVisibility(View.GONE);
                    linearLayoutSignIn.setVisibility(View.VISIBLE);
                    btnApply.setText("Sign in");
                }
            }
        });
        LoginActivity obj=new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(ItemListActivity.this);
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //CallRetrofitVoucherList(progressBar, spnVoucherType, textView, str[3], str[0], str[4], str[6], str[5]);
        }else{
            MessageDialog.MessageDialog(ItemListActivity.this,"",status);
        }
    }
    private void ShareOnWhatsApp(){
        if (!RecyclerItemGridAdapter.checkedList.isEmpty()) {
            //MessageDialog.MessageDialog(context,"",""+RecyclerItemGridAdapter.checkedList.toString());
            ArrayList<Uri> uriArrayList = new ArrayList<>();
            //ArrayList<String> stringArrayList = new ArrayList<>();
            String Caption = "";
            for (int i = 0; i < RecyclerItemGridAdapter.checkedList.size(); i++) {
                File myDir = new File(RecyclerItemGridAdapter.checkedList.get(i).get("ItemImage"));
                if (myDir.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", myDir);
                        uriArrayList.add(fileUri);
                    } else {
                        uriArrayList.add(Uri.fromFile(myDir));
                    }
                    Caption += (i + 1) + " DN:" + RecyclerItemGridAdapter.checkedList.get(i).get("ItemCode") + "\t R:₹" + RecyclerItemGridAdapter.checkedList.get(i).get("Rate") + "\t S:" + RecyclerItemGridAdapter.checkedList.get(i).get("Stock") + "\n";
                }
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, Caption);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriArrayList);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);
        }else if (!RecyclerItemGridColorAdapter.checkedListColor.isEmpty()) {
            //MessageDialog.MessageDialog(context,"",""+RecyclerItemGridAdapter.checkedList.toString());
            ArrayList<Uri> uriArrayList = new ArrayList<>();
            //ArrayList<String> stringArrayList = new ArrayList<>();
            String Caption = "";
            for (int i = 0; i < RecyclerItemGridColorAdapter.checkedListColor.size(); i++) {
                File myDir = new File(RecyclerItemGridColorAdapter.checkedListColor.get(i).get("ItemImage"));
                if (myDir.exists())
                    uriArrayList.add(Uri.fromFile(myDir));
                    //stringArrayList.add("Item Code: "+RecyclerItemGridColorAdapter.checkedListColor.get(i).get("ItemCode")+"\nRate: "+RecyclerItemGridColorAdapter.checkedListColor.get(i).get("Rate")+"\nStock: "+RecyclerItemGridColorAdapter.checkedListColor.get(i).get("Stock"));
                    Caption += (i+1)+" DN:"+RecyclerItemGridAdapter.checkedList.get(i).get("ItemCode")+"\t R:₹"+RecyclerItemGridAdapter.checkedList.get(i).get("Rate")+"\t S:"+RecyclerItemGridAdapter.checkedList.get(i).get("Stock")+"\n";
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, Caption);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriArrayList);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);
        }else {
            MessageDialog.MessageDialog(context,"","Please check atleast 1 item");
        }
    }
    public void writeToParcel(Parcel dest, String str) {
        dest.writeString(str);
    }
}
