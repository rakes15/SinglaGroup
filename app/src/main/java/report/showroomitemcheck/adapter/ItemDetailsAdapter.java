package report.showroomitemcheck.adapter;

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
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import report.DatabaseSqlite.DBSqlLiteHandlerShowroomItemCheck;
import report.showroomitemcheck.model.ItemDetails;

/**
 * Created by Rakesh on 03-Aug-17.
 */
public class ItemDetailsAdapter extends RecyclerView.Adapter<ItemDetailsAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<ItemDetails> datasetList,filterDatasetList;
    private DBSqlLiteHandlerShowroomItemCheck DBHandler;
    private static String TAG = ItemDetailsAdapter.class.getSimpleName();
    public ItemDetailsAdapter(Context context, List<ItemDetails> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteHandlerShowroomItemCheck(context);
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
                        if(item.getItemCode()!=null && item.getItemName()!=null && item.getSubItemName()!=null && item.getGroupName()!=null && item.getMainGroup()!=null)
                            if (item.getItemCode().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubItemName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getGroupName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getMainGroup().toLowerCase().contains(text.toLowerCase()) ) {
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
        //TODO: Item Code
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Sub Group",(dataset.getSubGroupName()==null || dataset.getSubGroupName().equals("null") ?"":dataset.getSubGroupName())));

        //TODO: Item Code
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Item Code",(dataset.getItemCode()==null || dataset.getItemCode().equals("null") ?"":dataset.getItemCode())));

        //TODO: Item Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Item Name",(dataset.getItemName()==null || dataset.getItemName().equals("null") ?"":dataset.getItemName())));

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
                SetLayoutItemOnlyDetailsGrid(dataset,tableLayout);
            }
        }
    }
    //TODO: MD Item Details Grid
    private void SetLayoutMDDetailsGrid(ItemDetails dataset,TableLayout tableLayout){
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
        ColorList = DBHandler.getShowroomColorName(dataset.getItemID(), dataset.getGroupID(),dataset.getMainGroupID());
        //TODO: Set Table Row and TextView Grid
        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
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

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Color Stock");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Stock");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout.addView(tableRow);

        for (int j=0; j<ColorList.size(); j++) {
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText(ColorList.get(j).get("ColorName"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText(ColorList.get(j).get("ColorSizeStock"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText(ColorList.get(j).get("ItemStock"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
    }
    //TODO: Sub Item Details Grid
    private void SetLayoutSubItemDetailsGrid(ItemDetails dataset,TableLayout tableLayout){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: Declaration
        int i=0;
        TableRow tableRow;
        TextView txt;
        List<Map<String,String>> SubItemList = new ArrayList<>();
        //TODO: Condition of Group Wise, Party Wise and Order Wise
         SubItemList = DBHandler.getShowroomSubItem(dataset.getItemID(), dataset.getGroupID(),dataset.getMainGroupID());
        //TODO: Set Table Row and TextView Grid
        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
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

//        txt = new TextView(context);
//        txt.setId(i + 4);
//        txt.setText("SI Stock");
//        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
//        txt.setTextColor(Color.WHITE);
//        txt.setGravity(Gravity.CENTER_HORIZONTAL);
//        txt.setPadding(10, 10, 10, 10);
//        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 5);
        txt.setText("Stock");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout.addView(tableRow);

        for (int j = 0; j<SubItemList.size(); j++) {
            tableRow = new TableRow(context);
            tableRow.setId(j + 10);
            tableRow.setPadding(10, 10, 10, 10);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setBackgroundColor(Color.TRANSPARENT);

            //String SubItemID = SubItemList.get(j).get("SubItemID");
            txt = new TextView(context);
            txt.setId(j + 1);
            txt.setText("" + SubItemList.get(j).get("SubItem"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);

//            txt = new TextView(context);
//            txt.setId(j + 5);
//            txt.setText("" + SubItemList.get(j).get("SubItemStock"));
//            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
//            txt.setTextColor(Color.BLACK);
//            txt.setGravity(Gravity.CENTER_HORIZONTAL);
//            txt.setPadding(10, 10, 10, 10);
//            tableRow.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 10);
            txt.setText("" + SubItemList.get(j).get("ItemStock"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);

            tableLayout.addView(tableRow);
        }
    }
    //TODO: Item Only Details Grid
    private void SetLayoutItemOnlyDetailsGrid(ItemDetails dataset,TableLayout tableLayout){
//        //TODO: TableLayout set
//        tableLayout.removeAllViewsInLayout();
//        tableLayout.removeAllViews();

        Map<String,String> Qty = DBHandler.getShowroomItemOnly(dataset.getItemID(),dataset.getGroupID(),dataset.getMainGroup());
        //TODO: 1nd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Stock:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(Qty.get("ItemStock")+"");
        tableLayout.addView(v2);
    }
}
