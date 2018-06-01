package report.godownwiseorderitem.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import report.DatabaseSqlite.DBSqlLiteSalesReportWithOrderType;
import report.godownwiseorderitem.model.ItemDetails;

/**
 * Created by Rakesh on 03-Aug-17.
 */
public class ItemDetailsAdapter extends RecyclerView.Adapter<ItemDetailsAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<ItemDetails> datasetList,filterDatasetList;
    private DBSqlLiteSalesReportWithOrderType DBHandler;
    private static String TAG = ItemDetailsAdapter.class.getSimpleName();
    public ItemDetailsAdapter(Context context, List<ItemDetails> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<ItemDetails>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteSalesReportWithOrderType(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final ItemDetails dataset = filterDatasetList.get(position);
        holder.cardView.setTag(dataset);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.tableLayout2);
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
        TableLayout tableLayout,tableLayout2;
        ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(ItemDetails dataset, TableLayout tableLayout, TableLayout tableLayout2){

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
        }else {
            if (dataset.getSubItemApplicable() == 1){
                //TODO: Call Sub Item Details Grid
                SetLayoutSubItemDetailsGrid(dataset,tableLayout2);
            }else {
                //TODO: Call Item Only Details Grid
                SetLayoutItemOnlyDetailsGrid(dataset,tableLayout2);
            }
        }
        //TODO: TableLayout2 set
//        tableLayout2.removeAllViewsInLayout();
//        tableLayout2.removeAllViews();
//        //TODO: 1th Row
//        View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayout, false);
//
//        TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
//        //txtContent1.setText("Pending Items : "+dataset.getPendingItems());
//
//        TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
//        //txtContent2.setText("Pending Qty: "+dataset.getPendingQty());
////
////        TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
////        txtContent3.setText("Total Amt: ₹"+dataset.getTotalAmount());
//        tableLayout2.addView(vt1);
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
        if (!dataset.getPartyID().isEmpty() || dataset.getPartyID()!=null) {
            ColorList = DBHandler.getColorListByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID());
            SizeList = DBHandler.getSizeListByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID());
        }else if (!dataset.getOrderID().isEmpty() || dataset.getOrderID()!=null) {
            ColorList = DBHandler.getColorListByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
            SizeList = DBHandler.getSizeListByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
        }else {
            ColorList = DBHandler.getColorListByGroupWise(dataset.getItemID(), dataset.getGroupID());
            SizeList = DBHandler.getSizeListByGroupWise(dataset.getItemID(), dataset.getGroupID());
        }
        int[][] totalRow=new  int[SizeList.size()+1][3];
        int[] totalColumn=new  int[3];
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

                Map<String,Integer> Qty = new HashMap<>();
                if (!dataset.getPartyID().isEmpty() || dataset.getPartyID()!=null) {
                    Qty = DBHandler.getPendingOrderStockQtyByPartyWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID(),dataset.getPartyID());
                }else if (!dataset.getOrderID().isEmpty() || dataset.getOrderID()!=null) {
                    Qty = DBHandler.getPendingOrderStockQtyByOrderWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID(),dataset.getOrderID());
                }else {
                    Qty = DBHandler.getPendingOrderStockQtyByGroupWise(SizeID,ColorID,dataset.getItemID(),dataset.getGroupID());
                }
                int pQty = 0,oQty = 0,sQty = 0;
                if (!Qty.isEmpty() || Qty!=null){
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
        if (!dataset.getPartyID().isEmpty() || dataset.getPartyID()!=null) {
            SubItemList = DBHandler.getSubItemDetailsByPartyWise(dataset.getItemID(), dataset.getGroupID(),dataset.getPartyID());
        }else if (!dataset.getOrderID().isEmpty() || dataset.getOrderID()!=null) {
            SubItemList = DBHandler.getSubItemDetailsByOrderWise(dataset.getItemID(), dataset.getGroupID(),dataset.getOrderID());
        }else {
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

            Map<String,Integer> Qty = new HashMap<>();
            if (!dataset.getPartyID().isEmpty() || dataset.getPartyID()!=null) {
                Qty = DBHandler.getSubItemStockDetailsByPartyWise(dataset.getItemID(),SubItemID,dataset.getGroupID(),dataset.getPartyID());
            }else if (!dataset.getOrderID().isEmpty() || dataset.getOrderID()!=null) {
                Qty = DBHandler.getSubItemStockDetailsByOrderWise(dataset.getItemID(),SubItemID,dataset.getGroupID(),dataset.getOrderID());
            }else {
                Qty = DBHandler.getSubItemStockDetailsByGroupWise(dataset.getItemID(),SubItemID,dataset.getGroupID());
            }
            int pQty = 0,oQty = 0,sQty = 0;
            if (!Qty.isEmpty() || Qty!=null){
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

        Map<String,Integer> Qty = new HashMap<>();
        if (!dataset.getPartyID().isEmpty() || dataset.getPartyID()!=null) {
            Qty = DBHandler.getWithoutSubItemDetailsByPartyWise(dataset.getItemID(),dataset.getGroupID(),dataset.getPartyID());
        }else if (!dataset.getOrderID().isEmpty() || dataset.getOrderID()!=null) {
            Qty = DBHandler.getWithoutSubItemDetailsByOrderWise(dataset.getItemID(),dataset.getGroupID(),dataset.getOrderID());
        }else {
            Qty = DBHandler.getWithoutSubItemDetailsByGroupWise(dataset.getItemID(),dataset.getGroupID());
        }
        int pQty = 0,oQty = 0,sQty = 0;
        if (!Qty.isEmpty() || Qty!=null){
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
