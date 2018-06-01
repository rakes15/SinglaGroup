package report.itemsrndetails.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ConditionLibrary;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import report.pendingdispatch.model.OrderWise;

/**
 * Created by Rakesh on 05-Jan-18.
 */
public class ItemSRNDetailsAdapter extends RecyclerView.Adapter<ItemSRNDetailsAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<Map<String,String>> datasetList,filterDatasetList;
    private int Flag;
    private static String TAG = ItemSRNDetailsAdapter.class.getSimpleName();
    public ItemSRNDetailsAdapter(Context context, List<Map<String,String>> datasetList,int Flag) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<Map<String,String>>();
        this.filterDatasetList.addAll(this.datasetList);
        this.Flag = Flag;
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final Map<String,String> dataset = filterDatasetList.get(position);
        if (Flag == 0) {
            //TODO: call TableLayout method
            setTableLayout(dataset, position, holder.tableLayout, holder.tableLayout2);
        }else if (Flag == 1) {
            //TODO: call TableLayout method
            setTableLayoutMoreDetails(dataset, position, holder.tableLayout, holder.tableLayout2);
        }else if (Flag == 2) {
            //TODO: call TableLayout method
            setTableLayoutMoreDetails(dataset, position, holder.tableLayout, holder.tableLayout2);
        }
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
                    for (Map<String,String> item : datasetList) {
//                        if(item.getGroupName()!=null && item.getMainGroup()!=null)
//                            if (item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
//                                    item.getMainGroup().toLowerCase().contains(text.toLowerCase()) ) {
//                                // Adding Matched items
//                                filterDatasetList.add(item);
//                            }
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
        TableLayout tableLayout,tableLayout2;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
        }
    }
    //TODO: Display TableLayout Flag 0
    private void setTableLayout(Map<String,String> map,int position, TableLayout tableLayout, TableLayout tableLayout2) {
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        int s=position+1;
        String Date=map.get("VDate");
        String VType=map.get("VType");
        String Days=DateFormatsMethods.DaysHoursMinutesCount(Date+" 00:00:00");
        //TODO: Header
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, s+". "+map.get("Header"), ""));
        //TODO: Maker's /Vender's Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Maker's/Vender's Name", map.get("PartyName")+"("+map.get("PartyCode")+")"));
        //TODO: Voucher No
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Voucher No", map.get("VNo")));
        //TODO: Voucher Date
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Voucher Date", DateFormatsMethods.DateFormat_DD_MM_YYYY(Date)));
        //TODO: Pending Since
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Since (in Days)", ""+Days));
        //TODO: Receipt Qty
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Receipt Qty", map.get("StockQty")));
        //TODO: Cost of Making/Purchase Rate
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Cost of Making\n/Purchase Rate", "Rs."+map.get("MakerCharge")));

        if(VType.equals("76")) {
            //TODO: Cost of Making/Purchase Rate
            String  cop = (map.get("ItemUnitRate") == null) && (map.get("ItemUnitRate").isEmpty())  ? "" : ConditionLibrary.ConvertRoundOff2Decimal(map.get("ItemUnitRate"));
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Cost of Production", "Rs."+cop));

            //TODO: table 2
            tableLayout2.removeAllViewsInLayout();
            tableLayout2.removeAllViews();
            //TODO: 1th Row
            View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_single_column, tableLayout2, false);

            TextView txtContent1= (TextView) vt1.findViewById(R.id.content);
            txtContent1.setText("Click to More Details");
            txtContent1.setGravity(Gravity.CENTER_HORIZONTAL);
            txtContent1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableLayout2.addView(vt1);
        }

    }
    //TODO: Display TableLayout Flag 1
    private void setTableLayoutMoreDetails(Map<String,String> map,int position, TableLayout tableLayout, TableLayout tableLayout2) {
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        int s=position+1;
        String Date=map.get("VDate");
        String VType=map.get("VType");
        String Days=DateFormatsMethods.DaysHoursMinutesCount(Date+" 00:00:00");
        //TODO: Item  Name and Code
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Item Name(Code)", map.get("ItemName")+"("+map.get("ItemCode")+")"));
        //TODO: Maker's /Vender's Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Vender's Name", map.get("PartyName")+"("+map.get("PartyCode")+")"));
        //TODO: Voucher No
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Voucher No", map.get("VNo")));
        //TODO: Voucher Date
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Voucher Date", DateFormatsMethods.DateFormat_DD_MM_YYYY(Date)));
        //TODO: Stock Qty
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Stock Qty", map.get("StockQty")));
        //TODO: Rate
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Rate", "Rs."+map.get("ItemUnitRate")));
        //TODO: Pending Since
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Days", ""+Days));

    }
    //TODO: Display TableLayout
    private void setTableLayout(Map<String,String> map,int position,TextView txtSerNo,LinearLayout lLPurchase,ScrollView scrollView,TableLayout tableLayout){

        String Date=map.get("VDate");
        String VType=map.get("VType");
        String Days=DateFormatsMethods.DaysHoursMinutesCount(Date);

        int s=position+1;
        txtSerNo.setText(s+". "+map.get("Header"));

        TextView txtVender=new TextView(context);
        txtVender.setText("Maker's/Vender's Name: "+map.get("PartyName")+"("+map.get("PartyCode")+")");
        txtVender.setTextColor(Color.BLUE);
        lLPurchase.addView(txtVender);

        TextView txtVNo=new TextView(context);
        txtVNo.setText("Voucher No: "+map.get("VNo"));
        txtVNo.setTextColor(Color.RED);
        lLPurchase.addView(txtVNo);

        TextView txtDate=new TextView(context);
        txtDate.setText("Voucher Date: "+Date.substring(0, 10)+" Since(in Day) :"+Days);
        txtDate.setTextColor(Color.DKGRAY);
        lLPurchase.addView(txtDate);

        TextView txtQty=new TextView(context);
        txtQty.setText("Receipt Qty: "+map.get("StockQty"));
        txtQty.setTextColor(Color.MAGENTA);
        lLPurchase.addView(txtQty);

        TextView txtRate=new TextView(context);
        txtRate.setText("Cost of Making/Purchase Rate: Rs."+map.get("MakerCharge"));
        txtRate.setTextColor(Color.BLUE);
        lLPurchase.addView(txtRate);

        if(VType.equals("76"))
        {
            TextView txtUnit=new TextView(context);
            txtUnit.setText("Cost of Production: Rs."+map.get("ItemUnitRate"));
            txtUnit.setTextColor(Color.BLACK);
            lLPurchase.addView(txtUnit);
            //Start Child Process
            scrollView.setVisibility(View.VISIBLE);
            tableLayout.removeAllViews();
            String VID=datasetList.get(position).get("VID");
            String VDetailID=datasetList.get(position).get("VDetailID");
            String StockQty=datasetList.get(position).get("StockQty");
            //Fetch Data through the server
            String COMANDOSQL="EXEC GetItemSRNDet '','',1,'"+VID+"','"+VDetailID+"',null,null ";
            //Connection connect=SqlConnection.CONN(SqlConnection.user2, SqlConnection.password2, SqlConnection.DB2, SqlConnection.IP2);
            ResultSet rs=null;//SqlConnection.QuerySQL(COMANDOSQL, connect);
            try
            {
                if(!rs.wasNull() && rs!=null)
                {
                    int i=0;
                    while(rs.next())
                    {
                        TableRow tableRow=new TableRow(context);
                        tableRow.setId(i+89);
                        tableRow.setBackgroundColor(Color.WHITE);
                        TableRow.LayoutParams rowSpanLayout = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        rowSpanLayout.span = 6;

                        TextView label_Header = new TextView(context);
                        label_Header.setId(i+21+9);
                        label_Header.setText(rs.getString("ItemName")+"("+rs.getString("ItemCode")+")");
                        label_Header.setTextColor(Color.BLUE);
                        label_Header.setPadding(5, 5, 5, 5);
                        tableRow.addView(label_Header,rowSpanLayout);// add the column to the table row here
                        tableLayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

//				    		tableRow=new TableRow(context);
//    						tableRow.setId(i+29);
//    						tableRow.setBackgroundColor(Color.WHITE);
//    						rowSpanLayout = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//    						rowSpanLayout.span = 6;
//
//			                label_Header = new TextView(context);
//			                label_Header.setId(219+i);
//			                label_Header.setText("Maker's Name: "+rs.getString("PartyName")+"("+rs.getString("PartyCode")+")"+"\nAverage: "+rs.getFloat("StockQty")/Float.valueOf(StockQty));
//			                label_Header.setTextColor(Color.BLACK);
//			                label_Header.setPadding(5, 5, 5, 5);
//				    		tableRow.addView(label_Header,rowSpanLayout);// add the column to the table row here
//				    		tableLayout.addView(tableRow, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));

                        TableRow tableRow2=new TableRow(context);
                        tableRow2.setId(i+122);
                        tableRow2.setBackgroundColor(Color.DKGRAY);
                        tableRow2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        TextView label_item = new TextView(context);
                        label_item.setId(i+981);
                        label_item.setText("Vender's Name");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        label_item = new TextView(context);
                        label_item.setId(i+4045);
                        label_item.setText("Voucher No");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        label_item = new TextView(context);
                        label_item.setId(i+4129);
                        label_item.setText("Voucher Date");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        label_item = new TextView(context);
                        label_item.setId(i+411);
                        label_item.setText("Stock Qty");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        label_item = new TextView(context);
                        label_item.setId(i+49);
                        label_item.setText("Rate");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        label_item = new TextView(context);
                        label_item.setId(i+3099);
                        label_item.setText("Days");
                        label_item.setTextColor(Color.WHITE);
                        label_item.setPadding(5, 5, 5, 5);
                        tableRow2.addView(label_item);// add the column to the table row here

                        tableLayout.addView(tableRow2, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        String COMANDOSQL2="EXEC GetItemSRNDet '','',2,null,null,'"+rs.getString("ItemID")+"',null ";
                        ResultSet rs2=null;///SqlConnection.QuerySQL(COMANDOSQL2, connect);
                        try
                        {
                            if(!rs2.wasNull() && rs2!=null)
                            {
                                int j=0;
                                while(rs2.next())
                                {
                                    TableRow tr_Item = new TableRow(context);
                                    tr_Item.setId(j+1076);
                                    tr_Item.setBackgroundColor(Color.LTGRAY);
                                    tr_Item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tr_Item.removeAllViews();

                                    label_item = new TextView(context);
                                    label_item.setId(j+34);
                                    label_item.setText(rs2.getString("PartyName")+"("+rs2.getString("PartyCode")+")");
                                    label_item.setTextColor(Color.BLUE);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    label_item = new TextView(context);
                                    label_item.setId(j+809);
                                    label_item.setText(rs2.getString("VNo"));
                                    label_item.setTextColor(Color.RED);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    label_item = new TextView(context);
                                    label_item.setId(j+11);
                                    label_item.setText(rs2.getString("VDate").substring(0,10));
                                    label_item.setTextColor(Color.BLUE);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    label_item = new TextView(context);
                                    label_item.setId(j+343);
                                    label_item.setText(rs2.getString("StockQty"));
                                    label_item.setTextColor(Color.RED);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    label_item = new TextView(context);
                                    label_item.setId(j+309);
                                    label_item.setText(""+rs2.getFloat("MakerCharge"));
                                    label_item.setTextColor(Color.MAGENTA);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    String Days2=DateFormatsMethods.DaysHoursMinutesCount(rs2.getString("VDate"));
                                    label_item = new TextView(context);
                                    label_item.setId(j+379);
                                    label_item.setText(Days2);
                                    label_item.setTextColor(Color.MAGENTA);
                                    label_item.setPadding(5, 5, 5, 5);
                                    tr_Item.addView(label_item);// add the column to the table row here

                                    tableLayout.addView(tr_Item, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    j++;
                                }
                            }
                        }
                        catch (Exception e) {
                            Log.e("ERRor", ""+e.getMessage());
                            //Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        i++;
                    }
                }
            }
            catch (Exception e) {
                Log.e("ERRor", ""+e.getMessage());
            }
        }
    }
    private void SetTableLayout2(List<Map<String,String>> dataList){
        TableLayout tableLayout=null;// = (TableLayout) rowView.findViewById(R.id.tableLayout_Consumption);
        int i=0;
        TableRow tr_Item = new TableRow(context);
        tr_Item.setId(i+176);
        tr_Item.setBackgroundColor(Color.DKGRAY);
        tr_Item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView label_item = new TextView(context);
        label_item.setId(i+409);
        label_item.setText("Vender's Name");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        label_item = new TextView(context);
        label_item.setId(i+4045);
        label_item.setText("Voucher No");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        label_item = new TextView(context);
        label_item.setId(i+429);
        label_item.setText("Voucher Date");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        label_item = new TextView(context);
        label_item.setId(i+4011);
        label_item.setText("Stock Qty");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        label_item = new TextView(context);
        label_item.setId(i+309);
        label_item.setText("Rate");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        label_item = new TextView(context);
        label_item.setId(i+3099);
        label_item.setText("Days");
        label_item.setTextColor(Color.WHITE);
        label_item.setPadding(5, 5, 5, 5);
        tr_Item.addView(label_item);// add the column to the table row here

        tableLayout.addView(tr_Item, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for(i=0;i<dataList.size();i++)
        {
            tr_Item = new TableRow(context);
            tr_Item.setId(i+1076);
            tr_Item.setBackgroundColor(Color.LTGRAY);
            tr_Item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            label_item = new TextView(context);
            label_item.setId(i+34);
            label_item.setWidth(150);
            label_item.setText(dataList.get(i).get("PartyName")+"("+dataList.get(i).get("PartyCode")+")");
            label_item.setTextColor(Color.BLUE);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here

            label_item = new TextView(context);
            label_item.setId(i+309);
            label_item.setText(dataList.get(i).get("VNo"));
            label_item.setTextColor(Color.RED);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here

            label_item = new TextView(context);
            label_item.setId(i+11);
            label_item.setText(dataList.get(i).get("VDate").substring(0,10));
            label_item.setTextColor(Color.BLUE);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here


            label_item = new TextView(context);
            label_item.setId(i+343);
            label_item.setText(dataList.get(i).get("StockQty"));
            label_item.setTextColor(Color.RED);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here

            label_item = new TextView(context);
            label_item.setId(i+309);
            label_item.setText(dataList.get(i).get("MakerCharge"));
            label_item.setTextColor(Color.MAGENTA);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here

            String Days= DateFormatsMethods.DaysHoursMinutesCount(dataList.get(i).get("VDate"));
            label_item = new TextView(context);
            label_item.setId(i+309);
            label_item.setText(Days);
            label_item.setTextColor(Color.MAGENTA);
            label_item.setPadding(5, 5, 5, 5);
            tr_Item.addView(label_item);// add the column to the table row here

            tableLayout.addView(tr_Item, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
