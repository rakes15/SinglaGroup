package report.forceclose.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.GlobleValues;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.forceclose.ForceCloseActivity;
import report.forceclose.model.GroupWise;
import report.forceclose.model.ItemDetails;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import stockcheck.StockCheckActivity;
import uploadimagesfiles.responsedatasets.ResponseItemInfoDataset;

/**
 * Created by Rakesh on 03-Aug-17.
 */
public class ItemDetailsAdapter extends RecyclerView.Adapter<ItemDetailsAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<ItemDetails> datasetList,filterDatasetList;
    private int flag,urgencyLevel = 0;
    private DBSqlLiteForceClose DBHandler;
    private static String TAG = ItemDetailsAdapter.class.getSimpleName();
    public ItemDetailsAdapter(Context context, List<ItemDetails> datasetList,int flag) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<ItemDetails>();
        this.filterDatasetList.addAll(this.datasetList);
        this.flag = flag;
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteForceClose(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_table_layout_horizontal_scroll, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final ItemDetails dataset = filterDatasetList.get(position);
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.tableLayout2,holder.tableLayout3);
        //TODO: set TableLayout3
        setTableLayout3(dataset,holder.tableLayout3);
        //TODO: Click Events method
        CLickEvents(dataset,holder.tableLayout,holder.tableLayout2,holder.tableLayout3);

    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterDatasetList.get(position);
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterDatasetList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterDatasetList.addAll(datasetList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (ItemDetails item : datasetList) {
                        if(item.getItemCode()!=null && item.getItemName()!=null && item.getSubItemName()!=null && item.getGroupName()!=null && item.getMainGroup()!=null && item.getPartyName()!=null && item.getOrderNo()!=null && item.getOrderDate()!=null )
                            if (item.getItemCode().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubItemName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getMainGroup().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderDate().toLowerCase().contains(text.toLowerCase()) ) {
                                // Adding Matched items
                                filterDatasetList.add(item);
                            }
                    }
                }

                // Set on UI Thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }
    //TODO RecyclerViewHolder
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        HorizontalScrollView scrollView;
        TableLayout tableLayout,tableLayout2,tableLayout3;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            scrollView = (HorizontalScrollView) itemView.findViewById(R.id.view_horizontal);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
        }
    }
    //TODO: Click Events
    private void CLickEvents(ItemDetails itemDetails,TableLayout tableLayout,TableLayout tableLayout2,TableLayout tableLayout3){
        //TODO: set Tag
        tableLayout.setTag(itemDetails);
        tableLayout2.setTag(itemDetails);
        //TODO: On Click
        tableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tableLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //TODO: On Long Click
        tableLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ItemDetails dataset = (ItemDetails)view.getTag();
                //TODO: Force Close Flag 1
                if (GlobleValues.multi_action_flag == 1){
                    if (flag == 0) {
                        AlertDialogMethod(dataset, "", "");
                    } else if (flag == 1) {
                        AlertDialogMethod(dataset, "", dataset.getOrderID());
                    } else if (flag == 2) {
                        AlertDialogMethod(dataset, dataset.getPartyID(), "");
                    }
                }
                //TODO: Expected delivery date Flag 2
                else if (GlobleValues.multi_action_flag == 2) {
                    DialogExpectedDeliveryDatetimeUpdate(dataset);
                }
                return false;
            }
        });
        tableLayout2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ItemDetails dataset = (ItemDetails)view.getTag();
                //TODO: Force Close Flag 1
                if (GlobleValues.multi_action_flag == 1){
                    if (flag == 0) {
                        AlertDialogMethod(dataset, "", "");
                    } else if (flag == 1) {
                        AlertDialogMethod(dataset, "", dataset.getOrderID());
                    } else if (flag == 2) {
                        AlertDialogMethod(dataset, dataset.getPartyID(), "");
                    }
                }
                //TODO: Expected delivery date Flag 2
                else if (GlobleValues.multi_action_flag == 2) {
                    DialogExpectedDeliveryDatetimeUpdate(dataset);
                }
                return false;
            }
        });
    }
    //TODO: Display TableLayout
    private void setTableLayout(ItemDetails dataset, TableLayout tableLayout, TableLayout tableLayout2, TableLayout tableLayout3){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Item Code:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getItemCode()==null || dataset.getItemCode().equals("null") ?"":dataset.getItemCode()));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Item Name:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getItemName()==null || dataset.getItemName().equals("null") ?"":dataset.getItemName()));
        tableLayout.addView(v2);

        //TODO: Set Layout Conditions
        if (dataset.getMDApplicable() == 1){
            //TODO: Call MD Details Grid
            SetLayoutMDDetailsGrid(dataset,tableLayout2);
            if (GlobleValues.multi_action_flag == 0){
                SetLayoutRate(dataset, tableLayout3);
            }
        }else {
            if (dataset.getSubItemApplicable() == 1){
                //TODO: Call Sub Item Details Grid
                SetLayoutSubItemDetailsGrid(dataset,tableLayout2);
            }else {
                //TODO: Call Item Only Details Grid
                SetLayoutItemOnlyDetailsGrid(dataset,tableLayout2);
            }
        }
    }
    //TODO: Display TableLayout3
    private void setTableLayout3(ItemDetails dataset, TableLayout tableLayout3){

        //TODO: TableLayout set
        tableLayout3.removeAllViewsInLayout();
        tableLayout3.removeAllViews();
        //TODO: View Image
        View v = inflater.inflate(R.layout.table_row_button, tableLayout3, false);
        Button btnView= (Button) v.findViewById(R.id.Button_View_Report);
        btnView.setText("View");
        btnView.setTag(dataset);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetails dataset = (ItemDetails) view.getTag();
                if (dataset!=null && !dataset.getItemCode().isEmpty()){
                    //MessageDialog.MessageDialog(context,"","Comming soon...");
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj= new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if(str!=null)
                            CallRetrofitGetItemInfo(str[3], str[0], str[4],str[5],str[14],dataset.getItemCode());
                    }else{
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            }
        });
        //TODO: Stock Check
        Button btnStockCheck = (Button) v.findViewById(R.id.button_ok);
        btnStockCheck.setText("Stock Check");
        btnStockCheck.setTag(dataset);
        btnStockCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetails dataset = (ItemDetails) view.getTag();
                if (dataset!=null && !dataset.getItemCode().isEmpty()){
                    DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
                    Map<String,String> map = DBInfo.getModulePermissionByVtype(11);
                    if (map != null && !map.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("ItemCode",dataset.getItemCode());
                        bundle.putString("Title",map.get("Name"));
                        bundle.putInt("ViewFlag",Integer.valueOf(map.get("ViewFlag")));
                        bundle.putInt("EditFlag",Integer.valueOf(map.get("EditFlag")));
                        bundle.putInt("CreateFlag",Integer.valueOf(map.get("CreateFlag")));
                        bundle.putInt("RemoveFlag",Integer.valueOf(map.get("RemoveFlag")));
                        bundle.putInt("PrintFlag",Integer.valueOf(map.get("PrintFlag")));
                        bundle.putInt("ImportFlag",Integer.valueOf(map.get("ImportFlag")));
                        bundle.putInt("ExportFlag",Integer.valueOf(map.get("ExportFlag")));
                        bundle.putInt("Vtype",Integer.valueOf(map.get("Vtype")));
                        System.out.println("bundle print:"+bundle.toString());
                        Intent in = new Intent(context, StockCheckActivity.class);
                        in.putExtra("PermissionBundle", bundle);
                        context.startActivity(in);
                    }
                }
            }
        });
        tableLayout3.addView(v);
    }
    //TODO: MD Item Details Grid
    private void SetLayoutMDDetailsGrid(ItemDetails dataset, TableLayout tableLayout){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: Declaration
        int i=0;
        TableRow tableRow;
        TextView txt;
        List<Map<String,String>> ColorList = new ArrayList<>();
        List<Map<String,String>> SizeList = new ArrayList<>();
        //TODO: Condition of Group Wise, Party Wise and Order Wise
        if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            ColorList = DBHandler.getColorListByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
            SizeList = DBHandler.getSizeListByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
        }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            ColorList = DBHandler.getColorListByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
            SizeList = DBHandler.getSizeListByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
        }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            ColorList = DBHandler.getColorListByGroupWise(dataset.getItemID(), dataset.getGroupID());
            SizeList = DBHandler.getSizeListByGroupWise(dataset.getItemID(), dataset.getGroupID());
        }
        float[][] totalRow=new  float[SizeList.size()+1][3];
        float[] totalColumn=new  float[3];
        //TODO: Set Table Row and TextView Grid
        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Color");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        for (int j = 0; j<SizeList.size()+1; j++) {
            txt = new TextView(context);
            txt.setId(j + 10);
            if (j<SizeList.size()){
                txt.setText("" + SizeList.get(j).get("SizeName"));
            }else{
                txt.setText("Total");
            }
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }
        tableLayout.addView(tableRow);
        //TODO:Set Row and Column grid data
        int k = 0;
        for (int j=0; j<ColorList.size(); j++) {
            String ColorID = ColorList.get(j).get("ColorID");
            totalColumn[0]=0;
            totalColumn[1]=0;
            totalColumn[2]=0;

            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText(ColorList.get(j).get("ColorName")+" ("+ ColorList.get(j).get("ColorFamily")+")");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            for (k = 0; k < SizeList.size(); k++) {
                String SizeID = SizeList.get(k).get("SizeID");

                Map<String,Float> Qty = new HashMap<>();
                if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
                    Qty = DBHandler.getPendingOrderStockQtyByPartyWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
                }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
                    Qty = DBHandler.getPendingOrderStockQtyByOrderWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID(),dataset.getOrderID());
                }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
                    Qty = DBHandler.getPendingOrderStockQtyByGroupWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID());
                }
                float pQty = 0,oQty = 0,sQty = 0;
                if (!Qty.isEmpty() && Qty!=null){
                    pQty = Qty.get("PendingQty");
                    oQty = Qty.get("OrderQty");
                    sQty = Qty.get("StockQty");
                    totalColumn[0]+=pQty;
                    totalColumn[1]+=oQty;
                    totalColumn[2]+=sQty;
                }else{
                    pQty = 0;
                    oQty = 0;
                    sQty = 0;
                }
                txt = new TextView(context);
                txt.setId(j + 300);
                //txt.setText(pQty+" | "+oQty+" | "+sQty);
                txt.setText(Html.fromHtml(MessageDialog.getColoredSpanned(""+pQty,"#990033")+"/"+MessageDialog.getColoredSpanned(""+oQty,"#2879aa")+"/"+MessageDialog.getColoredSpanned(""+sQty,"#606060")));
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow1.addView(txt);

                totalRow[k][0]+=pQty;
                totalRow[k][1]+=oQty;
                totalRow[k][2]+=sQty;
            }
            totalRow[k][0]+=totalColumn[0];
            totalRow[k][1]+=totalColumn[1];
            totalRow[k][2]+=totalColumn[2];

            txt = new TextView(context);
            txt.setId(i + 130);
            txt.setText(Html.fromHtml(MessageDialog.getColoredSpanned(""+totalColumn[0],"#990033")+"/"+MessageDialog.getColoredSpanned(""+totalColumn[1],"#2879aa")+"/"+MessageDialog.getColoredSpanned(""+totalColumn[2],"#606060")));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        tableRow = new TableRow(context);
        tableRow.setId(i + 120);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.LEFT);
        tableRow.setBackgroundColor(Color.TRANSPARENT);

        txt = new TextView(context);
        txt.setId(i + 10);
        txt.setText("Grand Total");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        for(int x=0;x<SizeList.size()+1;x++)
        {
            txt = new TextView(context);
            txt.setId(i + 10);
            txt.setText(Html.fromHtml(MessageDialog.getColoredSpanned(""+totalRow[x][0],"#990033")+"/"+MessageDialog.getColoredSpanned(""+totalRow[x][1],"#2879aa")+"/"+MessageDialog.getColoredSpanned(""+totalRow[x][2],"#606060")));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }
        tableLayout.addView(tableRow);

    }
    //TODO: Sub Item Details Grid
    private void SetLayoutSubItemDetailsGrid(ItemDetails dataset, TableLayout tableLayout){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: Declaration
        int i=0;
        TableRow tableRow;
        TextView txt;
        List<Map<String,String>> SubItemList = new ArrayList<>();
        //TODO: Condition of Group Wise, Party Wise and Order Wise
        if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            SubItemList = DBHandler.getSubItemDetailsByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
        }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            SubItemList = DBHandler.getSubItemDetailsByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
        }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null){
            SubItemList = DBHandler.getSubItemDetailsByGroupWise(dataset.getItemID(), dataset.getGroupID());
        }
        //TODO: Set Table Row and TextView Grid
        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("SubItem");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Pending/Order/Stock");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout.addView(tableRow);

        tableRow = new TableRow(context);
        tableRow.setId(i + 113);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.TRANSPARENT);

        for (int j = 0; j<SubItemList.size(); j++) {
            String SubItemID = SubItemList.get(j).get("SubItemID");
            txt = new TextView(context);
            txt.setId(j + 20);
            txt.setText("" + SubItemList.get(j).get("SubItemName"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);

            Map<String,Float> Qty = new HashMap<>();
            if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
                Qty = DBHandler.getSubItemStockDetailsByPartyWise(dataset.getItemID(),SubItemID,dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
            }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
                Qty = DBHandler.getSubItemStockDetailsByOrderWise(dataset.getItemID(),SubItemID,dataset.getGroupID(),dataset.getOrderID());
            }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null){
                Qty = DBHandler.getSubItemStockDetailsByGroupWise(dataset.getItemID(),SubItemID,dataset.getGroupID());
            }
            float pQty = 0,oQty = 0,sQty = 0;
            if (!Qty.isEmpty() && Qty!=null){
                pQty = Qty.get("PendingQty");
                oQty = Qty.get("OrderQty");
                sQty = Qty.get("StockQty");
            }else{
                pQty = 0;
                oQty = 0;
                sQty = 0;
            }
            txt = new TextView(context);
            txt.setId(j + 300);
            //txt.setText(pQty+" | "+oQty+" | "+sQty);
            txt.setText(Html.fromHtml(MessageDialog.getColoredSpanned(""+pQty,"#990033")+"/"+MessageDialog.getColoredSpanned(""+oQty,"#2879aa")+"/"+MessageDialog.getColoredSpanned(""+sQty,"#606060")));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }
        tableLayout.addView(tableRow);

    }
    //TODO: Item Only Details Grid
    private void SetLayoutItemOnlyDetailsGrid(ItemDetails dataset, TableLayout tableLayout){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        Map<String,Float> Qty = new HashMap<>();
        if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            Qty = DBHandler.getWithoutSubItemDetailsByPartyWise(dataset.getItemID(),dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
        }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            Qty = DBHandler.getWithoutSubItemDetailsByOrderWise(dataset.getItemID(),dataset.getGroupID(),dataset.getOrderID());
        }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null){
            Qty = DBHandler.getWithoutSubItemDetailsByGroupWise(dataset.getItemID(),dataset.getGroupID());
        }
        float pQty = 0,oQty = 0,sQty = 0;
        if (!Qty.isEmpty() && Qty!=null){
            pQty = Qty.get("PendingQty");
            oQty = Qty.get("OrderQty");
            sQty = Qty.get("StockQty");
        }else{
            pQty = 0;
            oQty = 0;
            sQty = 0;
        }
        //TODO: 1nd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Pending/Order/Stock:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(Html.fromHtml(MessageDialog.getColoredSpanned(""+pQty,"#990033")+"/"+MessageDialog.getColoredSpanned(""+oQty,"#2879aa")+"/"+MessageDialog.getColoredSpanned(""+sQty,"#606060")));
        tableLayout.addView(v2);
    }
    //TODO: Item Only Details Grid
    private void SetLayoutRate(ItemDetails dataset, TableLayout tableLayout){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        //TODO: Declaration
        int i=0;
        TableRow tableRow;
        TextView txt;
        List<Map<String,String>> RateList = new ArrayList<>();
        //TODO: Condition of Group Wise, Party Wise and Order Wise
        if (!dataset.getPartyID().isEmpty() && dataset.getPartyID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            RateList = DBHandler.getPriceListByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID(),dataset.getSubPartyID(),dataset.getRefName());
        }else if (!dataset.getOrderID().isEmpty() && dataset.getOrderID()!=null && !dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            RateList = DBHandler.getPriceListByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
        }else if (!dataset.getGroupID().isEmpty() && dataset.getGroupID()!=null) {
            RateList = DBHandler.getPriceListByGroupWise(dataset.getItemID(), dataset.getGroupID());
        }
        if (!RateList.isEmpty()) {
            tableRow = new TableRow(context);
            tableRow.setId(i + 110);
            tableRow.setPadding(10, 10, 10, 10);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setBackgroundColor(Color.DKGRAY);

            txt = new TextView(context);
            txt.setId(i + 3);
            txt.setText("Rate");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
            txt = new TextView(context);

            txt.setId(i + 3);
            txt.setText("Qty");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);

            txt = new TextView(context);
            txt.setId(i + 3);
            txt.setText("Total");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);

            tableLayout.addView(tableRow);

            for (int j = 0; j<RateList.size(); j++) {
                tableRow = new TableRow(context);
                tableRow.setId(i + 120);
                tableRow.setPadding(10, 10, 10, 10);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setGravity(Gravity.LEFT);
                tableRow.setBackgroundColor(Color.TRANSPARENT);

                txt = new TextView(context);
                txt.setId(j + 10);
                txt.setText("" + RateList.get(j).get("Rate"));
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow.addView(txt);

                txt = new TextView(context);
                txt.setId(j + 10);
                txt.setText("" + RateList.get(j).get("Qty"));
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow.addView(txt);

                txt = new TextView(context);
                txt.setId(j + 10);
                txt.setText("" + RateList.get(j).get("TotalAmt"));
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow.addView(txt);

                tableLayout.addView(tableRow);
            }
        }
    }


    private void AlertDialogMethod(final ItemDetails itemDetails, final String partyID, final String orderID){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.cardview_message_with_edittext, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView txtTitle = (TextView) dialogView.findViewById(R.id.textView_message);
        final TextInputLayout edtReason = (TextInputLayout) dialogView.findViewById(R.id.editText_remarks);
        TextView txtNo = (TextView) dialogView.findViewById(R.id.textView_No);
        TextView txtYes = (TextView) dialogView.findViewById(R.id.textView_Yes);
        txtTitle.setText("Are you sure? You want to Force Close.");
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtReason.setError(null);
                String Reason = edtReason.getEditText().getText().toString().trim();
                boolean cancel=false;
                View focusView=null;
                if (TextUtils.isEmpty(Reason)){
                    edtReason.setError("Reason cannot be blank");
                    focusView = edtReason;
                    cancel = true;
                }
                if (cancel){
                    if (focusView!=null)
                        focusView.requestFocus();
                }else {
                    if (Reason.length()>4){
                        String status = NetworkUtils.getConnectivityStatusString(context);
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj = new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if (str != null)
                                if (StaticValues.removeFlag == 1) {
                                    if (itemDetails!=null){
                                        String VType = itemDetails.getVType();
                                        CallVolleyForceClose(str[3], str[4], str[0], str[5],str[14],str[15],"","",itemDetails.getItemID(), DateFormatsMethods.getDateTime(),DateFormatsMethods.getDateTime(),VType,Reason,"0");
                                        alertDialog.dismiss();
                                    }else if (itemDetails!=null && !orderID.isEmpty()){
                                        String VType = itemDetails.getVType();
                                        CallVolleyForceClose(str[3], str[4], str[0], str[5],str[14],str[15],"",orderID,itemDetails.getItemID(), DateFormatsMethods.getDateTime(),DateFormatsMethods.getDateTime(),VType,Reason,"1");
                                        alertDialog.dismiss();
                                    }else if (itemDetails!=null && !partyID.isEmpty()){
                                        String VType = itemDetails.getVType();
                                        CallVolleyForceClose(str[3], str[4], str[0], str[5],str[14],str[15],partyID,"",itemDetails.getItemID(), DateFormatsMethods.getDateTime(),DateFormatsMethods.getDateTime(),VType,Reason,"2");
                                        alertDialog.dismiss();
                                    }
                                }else {
                                    MessageDialog.MessageDialog(context,"Permission denied","You don't have delete permission to delete this module.");
                                }
                        } else {
                            MessageDialog.MessageDialog(context,"",""+status);
                        }
                    }else {
                        edtReason.setError("Please enter *Reason minimum 4 Characters");
                    }
                }
            }
        });
        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    public void CallVolleyForceClose(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String PartyID,final String OrderID,final String ItemID,final String FromDt,final String ToDt,final String VType,final String Remarks,final String Type){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderForceClosed", new Response.Listener<String>()
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
                        MessageDialog.MessageDialog(context,"",Msg);
                        DBHandler.ForceCloseByType(Integer.valueOf(Type),"",ItemID,PartyID,OrderID);
                        //LoadRecyclerView(GroupID,PartyID,OrderID,0);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("PartyID", PartyID);
                params.put("OrderID", OrderID);
                params.put("ItemID", ItemID);
                params.put("FromDt", FromDt);
                params.put("ToDt", ToDt);
                params.put("VType", VType);
                params.put("Remarks", Remarks);
                params.put("Type", Type);
                Log.i(TAG,"Force Close parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void DialogExpectedDeliveryDatetimeUpdate(final ItemDetails itemDetails){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_expected_delivery_date);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final EditText editTextDelDate = (EditText) dialog.findViewById(R.id.ex_del_date);
        final EditText editTextDelTime = (EditText) dialog.findViewById(R.id.ex_del_time);
        LinearLayout layoutUrgency = (LinearLayout) dialog.findViewById(R.id.Linear_Urgency);
        LinearLayout layoutRemarks = (LinearLayout) dialog.findViewById(R.id.Linear_Remarks);
        Spinner spnUrgencyLevel = (Spinner) dialog.findViewById(R.id.spinner_Urgency_Level);
        final EditText edtRemarks = (EditText) dialog.findViewById(R.id.editText_remarks);
        Button btnUpdate = (Button) dialog.findViewById(R.id.button_Update);
        Button btnCancel = (Button) dialog.findViewById(R.id.button_Cancel);
        //TODO: Linear Layout
        layoutUrgency.setVisibility(View.VISIBLE);
        layoutRemarks.setVisibility(View.VISIBLE);
        //TODO: Edit text
        editTextDelDate.setInputType(InputType.TYPE_NULL);
        editTextDelTime.setInputType(InputType.TYPE_NULL);
        editTextDelDate.setText(""+ DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
        editTextDelTime.setText(""+DateFormatsMethods.getDateTime().substring(11,16));
        editTextDelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String ExDelDate = DateFormatsMethods.PastDateNotSelect(formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year);
                        editTextDelDate.setText(ExDelDate);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select the date");
                datePicker.show();
            }
        });
        editTextDelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        try{
                            DecimalFormat formatter = new DecimalFormat("00");
                            String ExDelTime = formatter.format(hourOfDay)+":"+formatter.format(minute);
                            editTextDelTime.setText(""+ExDelTime);
                        }catch (Exception e) {
                            // TODO: handle exception
                            Log.e("ERRor", ""+e.toString());
                        }
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));
                timePicker.setTitle("Select the Time");
                timePicker.show();
            }
        });
        //TODO: Spinner Urgency
        spnUrgencyLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                urgencyLevel = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //TODO: Btn Click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ExDate = editTextDelDate.getText().toString();
                String ExTime = editTextDelTime.getText().toString();
                String Remarks = edtRemarks.getText().toString().toString();
                if (!ExDate.isEmpty() && !ExTime.isEmpty() && urgencyLevel!=0) {
                    String ExDateTime = DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDate) + " " + ExTime + ":00";
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.editFlag == 1) {
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], itemDetails.getOrderID(),itemDetails.getGroupID(),itemDetails.getItemID(), ExDateTime,""+itemDetails.getVType(),Remarks,"0",""+urgencyLevel, CommanStatic.LogIN_UserName);
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                            }
                    }else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }else {
                    if (urgencyLevel == 0){MessageDialog.MessageDialog(context,"Alert","Please select urgency level");}
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID, final String ItemID,final String ExpectedDatetime, final String VType, final String Remarks, final String Type, final String UrgencyLevel,final String UserName){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ExpDelDtUpdate", new Response.Listener<String>()
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
                        MessageDialog.MessageDialog(context,"",Msg);
                        DBHandler.UpdateExpDelDate(ExpectedDatetime,OrderID,GroupID,ItemID);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());

                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("OrderID", OrderID);
                params.put("GroupID", GroupID);
                params.put("ItemID", ItemID);
                params.put("ExpDelDt", ExpectedDatetime);
                params.put("VType", VType);
                params.put("Remarks", Remarks);
                params.put("Type", Type);
                params.put("UrgLab", UrgencyLevel);
                params.put("UserName", UserName);
                Log.d(TAG,"Update Expected Del datetime parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallRetrofitGetItemInfo( String DeviceID, String SessionID, String UserID,String DivisionID,String CompanyID,final String ItemCode){
        showDialog();
        final ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("ItemCode", ItemCode);
        Log.d(TAG,"Parameters:"+params.toString());
        Call<ResponseItemInfoDataset> call = apiService.getItemInfoWithImage(params);
        call.enqueue(new Callback<ResponseItemInfoDataset>() {
            @Override
            public void onResponse(Call<ResponseItemInfoDataset> call, retrofit2.Response<ResponseItemInfoDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            Map<String,String> map = response.body().getResult();
                            if (map!=null && map.get("ImageStatus").equals("1") && !map.get("ItemImage").isEmpty()) {
                                DialogViewImage(context, map.get("ItemImage"));
                            }else {
                                MessageDialog.MessageDialog(context,"","Image not available");
                            }
                        } else {
                            MessageDialog.MessageDialog(context,"",msg);
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"Server response",""+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG," Exception:"+e.getMessage());
                    MessageDialog.MessageDialog(context,"Item image API",e.toString());
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<ResponseItemInfoDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog.MessageDialog(context,"Failure","Item image API"+t.toString());
                hideDialog();
            }
        });
    }
    private void DialogViewImage(final Context mContext,String Url) {
        final Dialog dialog = new Dialog(new ContextThemeWrapper(mContext, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_view_image);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        ImageView imageView = (ImageView) dialog.findViewById(R.id.Image_View);
        WebView webView = (WebView) dialog.findViewById(R.id.web_view);
        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(Url).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null ) {
            progressDialog.dismiss();
        }
    }
}
