package orderbooking.customerlist.partyinfo.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.FiscalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerPartyInfo;
import orderbooking.customerlist.partyinfo.model.PartyCompleteInfo;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class PartyCommonDetailsListAdapter extends RecyclerView.Adapter<PartyCommonDetailsListAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private String Title;
    private Calendar calendar;
    private int CurrentYear;
    private DatabaseSqlLiteHandlerPartyInfo partyInfo;
    private List<PartyCompleteInfo.Summary>  SummaryList,filterSummaryList;
    private List<PartyCompleteInfo.GroupWise>  GroupWiseList,filterGroupWiseList;
    private List<PartyCompleteInfo.PartyMonthWiseSale>  PartyMonthWiseList,filterPartyMonthWiseList;
    private List<Map<String,String>>  ShowroomWiseList,filterShowroomWiseList;
    private List<Map<String,String>>  ApplicationWiseList,filterApplicationWiseList;
    private List<PartyCompleteInfo.FairDeliveryParcent>  FairDeliveryParcentList,filterFairDeliveryParcentList;
    private static String TAG = PartyCommonDetailsListAdapter.class.getSimpleName();
    public static Map<String,String> mapAvgPayDays = new HashMap<>();
    public static Map<String,String> mapDiscount = new HashMap<>();
    public PartyCommonDetailsListAdapter(Context context, String Title , PartyCompleteInfo.SalesInfo SalesInfo) {
        this.context=context;
        this.Title=Title;
        this.partyInfo = new DatabaseSqlLiteHandlerPartyInfo(context);
        calendar = Calendar.getInstance();
        CurrentYear = calendar.get(Calendar.YEAR);
        context.deleteDatabase(DatabaseSqlLiteHandlerPartyInfo.DATABASE_NAME);
        if (Title.equals("Summary")){
            this.SummaryList=SalesInfo.getSummary();
            this.filterSummaryList = new ArrayList<>();
            this.filterSummaryList.addAll(this.SummaryList);
        }else if (Title.equals("Group Wise Sales")){
            this.GroupWiseList=SalesInfo.getGroupWise();
            this.filterGroupWiseList = new ArrayList<>();
            this.filterGroupWiseList.addAll(this.GroupWiseList);
        }else if (Title.equals("Party Month Wise Sales")){
            this.PartyMonthWiseList=SalesInfo.getPartyMonthWiseSales();
            this.filterPartyMonthWiseList = new ArrayList<>();
            this.filterPartyMonthWiseList.addAll(this.PartyMonthWiseList);
        }else if (Title.equals("Showroom Wise Sales")){
            partyInfo.deletePartyInfo();
            List<Map<String,String>> mapList = new ArrayList<>();
            for (int i=0; i<SalesInfo.getShowroomWiseSales().size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Year", String.valueOf(SalesInfo.getShowroomWiseSales().get(i).getYearSales()));
                map.put("MonthName", String.valueOf(SalesInfo.getShowroomWiseSales().get(i).getMonthNm()));
                map.put("Month", String.valueOf(SalesInfo.getShowroomWiseSales().get(i).getSalesMonth()));
                map.put("Quantity", String.valueOf(SalesInfo.getShowroomWiseSales().get(i).getSalesQty()));
                map.put("Amount", String.valueOf(SalesInfo.getShowroomWiseSales().get(i).getSalesAmount()));
                map.put("Common", SalesInfo.getShowroomWiseSales().get(i).getShowroom());
                mapList.add(map);
            }
            partyInfo.insertPartyInfo(mapList);
            this.ShowroomWiseList=partyInfo.getCommonList();
            this.filterShowroomWiseList = new ArrayList<>();
            this.filterShowroomWiseList.addAll(this.ShowroomWiseList);
        }else if (Title.equals("Application Wise Sales")){
            partyInfo.deletePartyInfo();
            List<Map<String,String>> mapList = new ArrayList<>();
            for (int i=0; i<SalesInfo.getApplicationWiseSales().size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("Year", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getYearSales()));
                map.put("MonthName", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getMonthNm()));
                map.put("Month", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getSalesMonth()));
                map.put("Quantity", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getSalesQty()));
                map.put("Amount", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getSalesAmount()));
                map.put("Common", SalesInfo.getApplicationWiseSales().get(i).getApplicationType());
                map.put("CommonType", String.valueOf(SalesInfo.getApplicationWiseSales().get(i).getAppType()));
                mapList.add(map);
            }
            partyInfo.insertPartyInfo(mapList);
            this.ApplicationWiseList=partyInfo.getCommonList();
            this.filterApplicationWiseList = new ArrayList<>();
            this.filterApplicationWiseList.addAll(this.ApplicationWiseList);
        }else if (Title.equals("Fair Delivery Percent")){
            this.FairDeliveryParcentList=SalesInfo.getFairDeliveryParcent();
            this.filterFairDeliveryParcentList = new ArrayList<>();
            this.filterFairDeliveryParcentList.addAll(this.FairDeliveryParcentList);
        }
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_table_layout_horizontal_scroll, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        if (Title.equals("Summary")){
            PartyCompleteInfo.Summary dataset = filterSummaryList.get(position);
            //holder.cardView.setTag(dataset.get());
            //TODO: call TableLayout method
            setTableLayoutSummary(filterSummaryList,holder.tableLayout);
        }else if (Title.equals("Group Wise Sales")){
            PartyCompleteInfo.GroupWise dataset = filterGroupWiseList.get(position);
            holder.cardView.setTag(dataset.getGroupID());
            //TODO: call TableLayout method
            setTableLayoutGroupWise(filterGroupWiseList,holder.tableLayout);
        }else if (Title.equals("Party Month Wise Sales")){
            PartyCompleteInfo.PartyMonthWiseSale dataset = filterPartyMonthWiseList.get(position);
            //holder.cardView.setTag(dataset.get());
            //TODO: call TableLayout method
            setTableLayoutPartyMonthWise(filterPartyMonthWiseList,holder.tableLayout);
        }else if (Title.equals("Showroom Wise Sales")){
            Map<String,String> dataset = filterShowroomWiseList.get(position);
            //holder.cardView.setTag(dataset.get());
            //TODO: call TableLayout method
            setTableLayoutShowroomWise(dataset,holder.tableLayout);
        }else if (Title.equals("Application Wise Sales")){
            Map<String,String> dataset = filterApplicationWiseList.get(position);
            //holder.cardView.setTag(dataset.get());
            //TODO: call TableLayout method
            setTableLayoutApplicationWise(dataset,holder.tableLayout);
        }else if (Title.equals("Fair Delivery Percent")){
            PartyCompleteInfo.FairDeliveryParcent dataset = filterFairDeliveryParcentList.get(position);
            //holder.cardView.setTag(dataset.get());
            //TODO: call TableLayout method
            setTableLayoutFairDeliveryPercent(dataset,holder.tableLayout);
        }

    }
    @Override
    public int getItemCount() {
        int size=0;
        if (Title.equals("Summary")){
            size = 1;
            //size = (null != filterGroupWiseList ? filterGroupWiseList.size() : 0);
        }else if (Title.equals("Group Wise Sales")){
            size = 1;
            //size = (null != filterGroupWiseList ? filterGroupWiseList.size() : 0);
        }else if (Title.equals("Party Month Wise Sales")){
            size = 1;//(null != filterPartyMonthWiseList ? filterPartyMonthWiseList.size() : 0);
        }else if (Title.equals("Showroom Wise Sales")){
            size = (null != filterShowroomWiseList ? filterShowroomWiseList.size() : 0);
        }else if (Title.equals("Application Wise Sales")){
            size = (null != filterApplicationWiseList ? filterApplicationWiseList.size() : 0);
        }else if (Title.equals("Fair Delivery Percent")){
            size = (null != filterFairDeliveryParcentList ? filterFairDeliveryParcentList.size() : 0);
        }
        return size;
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Title.equals("Summary")){
                    // Clear the filter list
                    filterSummaryList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterSummaryList.addAll(SummaryList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (PartyCompleteInfo.Summary item : SummaryList) {
//                            if(item.get()!=null)
//                                if (item.getGroupName().toLowerCase().contains(text.toLowerCase())) {
//                                    // Adding Matched items
//                                    filterGroupWiseList.add(item);
//                                }
                        }
                    }
                }else if (Title.equals("Group Wise Sales")){
                    // Clear the filter list
                    filterGroupWiseList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterGroupWiseList.addAll(GroupWiseList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (PartyCompleteInfo.GroupWise item : GroupWiseList) {
                            if(item.getGroupName()!=null)
                                if (item.getGroupName().toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterGroupWiseList.add(item);
                                }
                        }
                    }
                }else if (Title.equals("Party Month Wise Sales")){
                    // Clear the filter list
                    filterPartyMonthWiseList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {

                        filterPartyMonthWiseList.addAll(PartyMonthWiseList);

                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (PartyCompleteInfo.PartyMonthWiseSale item : PartyMonthWiseList) {
                            if(item.getYearSeles()!=null || item.getMonthSales()!=null || item.getMonthNm()!=null || item.getPartySalesQty0()!=null || item.getPartySalesAmt0()!=null)
                                if (String.valueOf(item.getYearSeles()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getMonthSales()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getMonthNm()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getPartySalesQty0()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getPartySalesAmt0()).toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterPartyMonthWiseList.add(item);
                                }
                        }
                    }
                }else if (Title.equals("Showroom Wise Sales")){
                    // Clear the filter list
                    filterShowroomWiseList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterShowroomWiseList.addAll(ShowroomWiseList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (Map<String,String> item : ShowroomWiseList) {
                            if(item.get("Common")!=null)
                                if (String.valueOf(item.get("Common")).toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterShowroomWiseList.add(item);
                                }
                        }
                    }
                }else if (Title.equals("Application Wise Sales")){
                    // Clear the filter list
                    filterApplicationWiseList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterApplicationWiseList.addAll(ApplicationWiseList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (Map<String,String> item : ApplicationWiseList) {
                            if(item.get("Common")!=null  || item.get("CommonType")!=null)
                                if (String.valueOf(item.get("Common")).toLowerCase().contains(text.toLowerCase()) || String.valueOf(item.get("CommonType")).toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterApplicationWiseList.add(item);
                                }
                        }
                    }
                }else if (Title.equals("Fair Delivery Percent")){
                    // Clear the filter list
                    filterFairDeliveryParcentList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterFairDeliveryParcentList.addAll(FairDeliveryParcentList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (PartyCompleteInfo.FairDeliveryParcent item : FairDeliveryParcentList) {
                            if(item.getFairIDDeliveryPercent()!=null || item.getYearSales()!=null)
                                if (String.valueOf(item.getFairIDDeliveryPercent()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getYearSales()).toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterFairDeliveryParcentList.add(item);
                                }
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
        TableLayout tableLayout;
        //ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            //imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display Group Wise TableLayout
    private void setTableLayoutSummary(List<PartyCompleteInfo.Summary> list, TableLayout tableLayout){

        List<Map<String,String>> mapListSale = new ArrayList<>();
        List<Map<String,String>> mapListShare = new ArrayList<>();
        List<Map<String,String>> mapListAvgInvoiceValue = new ArrayList<>();
        List<Map<String,String>> mapListNoOfInv = new ArrayList<>();
        List<Map<String,String>> mapListReturn = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("Name", "Sales");
            map.put("Sale0", String.valueOf(Math.round(list.get(i).getPartySalesYearsWise0())));
            map.put("Sale1", String.valueOf(Math.round(list.get(i).getPartySalesYearsWise1())));
            map.put("Sale2", String.valueOf(Math.round(list.get(i).getPartySalesYearsWise2())));
            mapListSale.add(map);
            map = new HashMap<String, String>();
            map.put("Name", "Share");
            map.put("Share0", String.valueOf(list.get(i).getSalesShare0()));
            map.put("Share1", String.valueOf(list.get(i).getSalesShare1()));
            map.put("Share2", String.valueOf(list.get(i).getSalesShare2()));
            mapListShare.add(map);
            map = new HashMap<String, String>();
            map.put("Name", "Avg. Invoice Value");
            map.put("AvgInvoiceValue0", String.valueOf(list.get(i).getPartyAvgInv0()));
            map.put("AvgInvoiceValue1", String.valueOf(list.get(i).getPartyAvgInv1()));
            map.put("AvgInvoiceValue2", String.valueOf(list.get(i).getPartyAvgInv2()));
            mapListAvgInvoiceValue.add(map);
            map = new HashMap<String, String>();
            map.put("Name", "No. Of Invoice");
            map.put("NoOfInvoice0", String.valueOf(list.get(i).getNoOfInv0()));
            map.put("NoOfInvoice1", String.valueOf(list.get(i).getNoOfInv1()));
            map.put("NoOfInvoice2", String.valueOf(list.get(i).getNoOfInv2()));
            mapListNoOfInv.add(map);
            map = new HashMap<String, String>();
            map.put("Name", "Return");
            map.put("Return0", String.valueOf(list.get(i).getSaleReturn0()));
            map.put("Return1", String.valueOf(list.get(i).getSaleReturn1()));
            map.put("Return2", String.valueOf(list.get(i).getSaleReturn2()));
            mapListReturn.add(map);
        }
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        TextView txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Year "+ FiscalDate.getFinancialYear(calendar));
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 4);
        txt.setText("Year "+FiscalDate.getPreviousFinancialYear(calendar));
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 5);
        txt.setText("Year "+FiscalDate.getPreviuosToPreviuosFinancialYear(calendar));
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout.addView(tableRow);
        //TODO:Set Row data Sale
        for (int j=0; j<mapListSale.size(); j++) {
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapListSale.get(j).get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("₹" + mapListSale.get(j).get("Sale0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("₹" + mapListSale.get(j).get("Sale1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("₹" + mapListSale.get(j).get("Sale2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data Share
        for (int j=0; j<mapListShare.size(); j++) {
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 20);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapListShare.get(j).get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapListShare.get(j).get("Share0")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapListShare.get(j).get("Share1")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapListShare.get(j).get("Share2")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data AvgInvoiceValue
        for (int j=0; j<mapListAvgInvoiceValue.size(); j++) {
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 30);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapListAvgInvoiceValue.get(j).get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapListAvgInvoiceValue.get(j).get("AvgInvoiceValue0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapListAvgInvoiceValue.get(j).get("AvgInvoiceValue1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapListAvgInvoiceValue.get(j).get("AvgInvoiceValue2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data mapListNoOfInv
        for (int j=0; j<mapListNoOfInv.size(); j++) {
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 30);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapListNoOfInv.get(j).get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapListNoOfInv.get(j).get("NoOfInvoice0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapListNoOfInv.get(j).get("NoOfInvoice1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapListNoOfInv.get(j).get("NoOfInvoice2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data mapListNoOfInv
        for (int j=0; j<mapListReturn.size(); j++) {
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 40);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapListReturn.get(j).get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapListReturn.get(j).get("Return0")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapListReturn.get(j).get("Return1")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapListReturn.get(j).get("Return2")+"%");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data Avg Payment Days
        if (mapAvgPayDays!=null) {
            int j=0;
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 40);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapAvgPayDays.get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapAvgPayDays.get("AvgPayDays0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapAvgPayDays.get("AvgPayDays1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapAvgPayDays.get("AvgPayDays2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
        //TODO:Set Row data Avg Payment Days
        if (mapDiscount!=null) {
            int j=0;
            //String GroupID = list.get(j).get;
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 40);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + mapDiscount.get("Name"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + mapDiscount.get("Discount0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + mapDiscount.get("Discount1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + mapDiscount.get("Discount2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
    }
    //TODO: Display Group Wise TableLayout
    private void setTableLayoutGroupWise(List<PartyCompleteInfo.GroupWise> list, TableLayout tableLayout){

        List<Map<String,String>> mapList = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("GroupID", String.valueOf(list.get(i).getGroupID()));
            map.put("GroupName", String.valueOf(list.get(i).getGroupName()));
            map.put("Amt0", String.valueOf(list.get(i).getSaleAmtGroupWise0()));
            map.put("Amt1", String.valueOf(list.get(i).getSaleAmtGroupWise1()));
            map.put("Amt2", String.valueOf(list.get(i).getSaleAmtGroupWise2()));
            map.put("Qty0", String.valueOf(list.get(i).getSaleQtyGroupWise0()));
            map.put("Qty1", String.valueOf(list.get(i).getSaleQtyGroupWise1()));
            map.put("Qty2", String.valueOf(list.get(i).getSaleQtyGroupWise2()));
            mapList.add(map);
        }
        context.deleteDatabase(DatabaseSqlLiteHandlerPartyInfo.DATABASE_NAME);
        DatabaseSqlLiteHandlerPartyInfo partyInfo = new DatabaseSqlLiteHandlerPartyInfo(context);
        partyInfo.deleteGroupWise();
        partyInfo.insertGroupWise(mapList);
        List<Map<String,String>> groupList = partyInfo.getGroupWise();
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        TextView txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("Group Name");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Year "+ FiscalDate.getFinancialYear(calendar)+" (Qty / Amt)");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 4);
        txt.setText("Year "+FiscalDate.getPreviousFinancialYear(calendar)+" (Qty / Amt)");
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 5);
        txt.setText("Year "+FiscalDate.getPreviuosToPreviuosFinancialYear(calendar)+" (Qty / Amt)");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout.addView(tableRow);
        //TODO:Set Row data
        for (int j=0; j<groupList.size(); j++) {

            String GroupID = groupList.get(j).get("GroupID");
            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText("" + groupList.get(j).get("GroupName"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 300);
            txt.setText("" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Qty0") + " / ₹" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Amt0"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 400);
            txt.setText("" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Qty1") + " / ₹" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Amt1"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            txt = new TextView(context);
            txt.setId(j + 500);
            txt.setText("" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Qty2") + " / ₹" + partyInfo.getQtyAmtByGroup(GroupID).get(0).get("Amt2"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            tableLayout.addView(tableRow1);
        }
    }
    //TODO: Display Party Month Wise TableLayout
    private void setTableLayoutPartyMonthWise(List<PartyCompleteInfo.PartyMonthWiseSale> list, TableLayout tableLayout){

        List<Map<String,String>> mapList = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("Year", String.valueOf(list.get(i).getYearSeles()));
            map.put("MonthName", String.valueOf(list.get(i).getMonthNm()));
            map.put("Month", String.valueOf(list.get(i).getMonthSales()));
            map.put("Quantity", String.valueOf(list.get(i).getPartySalesQty0()));
            map.put("Amount", String.valueOf(list.get(i).getPartySalesAmt0()));
            map.put("Common", "");
            mapList.add(map);
        }
        context.deleteDatabase(DatabaseSqlLiteHandlerPartyInfo.DATABASE_NAME);
        DatabaseSqlLiteHandlerPartyInfo partyInfo = new DatabaseSqlLiteHandlerPartyInfo(context);
        partyInfo.deletePartyInfo();
        partyInfo.insertPartyInfo(mapList);

        List<Map<String,String>> monthList = partyInfo.getMonthList();
        List<Map<String,String>> yearList = partyInfo.getYearList();

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        TextView txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("Month-Year");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        for (int j = 0; j<yearList.size(); j++) {
            txt = new TextView(context);
            txt.setId(j + 10);
            txt.setText(""+yearList.get(j).get("Year"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }

        tableLayout.addView(tableRow);
        //TODO:Set Row data
        for (int j=0; j<monthList.size(); j++) {

            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText(monthList.get(j).get("MonthName")+" ("+ monthList.get(j).get("Month")+")");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            for (int k = 0; k < yearList.size(); k++) {

                Map<String,Integer> qtyAmt = partyInfo.getQtyAmtList(Integer.valueOf(monthList.get(j).get("Month")),Integer.valueOf(yearList.get(k).get("Year")));
                int qty = 0,amt = 0;
                if (!qtyAmt.isEmpty()){
                    qty = qtyAmt.get("Quantity");
                    amt = qtyAmt.get("Amount");
                }else{
                    qty = 0;
                    amt = 0;
                }
                txt = new TextView(context);
                txt.setId(j + 300);
                txt.setText(qty+" / ₹"+amt);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow1.addView(txt);
            }
            tableLayout.addView(tableRow1);
        }
//        //TODO: TableLayout set
//        tableLayout.removeAllViewsInLayout();
//        tableLayout.removeAllViews();
//        //TODO: 1st Row
//        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        TextView txtHeader= (TextView) v.findViewById(R.id.header);
//        txtHeader.setText("Year:");
//
//        TextView txt= (TextView) v.findViewById(R.id.content);
//        txt.setText(""+(dataset.getYearSeles()==null?"":dataset.getYearSeles()));
//        tableLayout.addView(v);
//
//        //TODO: 2nd Row
//        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
//        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
//        txtHeader1.setText("Month:");
//
//        TextView txt1= (TextView) v1.findViewById(R.id.content);
//        txt1.setText(""+(dataset.getMonthSales()==null?"":dataset.getMonthSales()));
//        tableLayout.addView(v1);
//        //TODO: 3rd Row
//        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
//        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
//        txtHeader2.setText("Month Name:");
//
//        TextView txt2= (TextView) v2.findViewById(R.id.content);
//        txt2.setText(""+(dataset.getMonthNm()==null?"":dataset.getMonthNm()));
//        tableLayout.addView(v2);
//        //TODO: 4th Row
//        View v3 = inflater.inflate(R.layout.table_row, tableLayout, false);
//        TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
//        txtHeader3.setText("Quantity:");
//
//        TextView txt3= (TextView) v3.findViewById(R.id.content);
//        txt3.setText(""+(dataset.getPartySalesQty0()==null?"":dataset.getPartySalesQty0()));
//        tableLayout.addView(v3);
//        //TODO: 5th Row
//        View v4 = inflater.inflate(R.layout.table_row, tableLayout, false);
//        TextView txtHeader4= (TextView) v4.findViewById(R.id.header);
//        txtHeader4.setText("Amount:");
//
//        TextView txt4= (TextView) v4.findViewById(R.id.content);
//        txt4.setText(""+(dataset.getPartySalesAmt0()==null?"":"₹"+dataset.getPartySalesAmt0()));
//        tableLayout.addView(v4);
    }
    //TODO: Display Showroom Wise TableLayout
    private void setTableLayoutShowroomWise(Map<String,String> map, TableLayout tableLayout){

        List<Map<String,String>> monthList = partyInfo.getMonthListByCommon(map.get("Common"));
        List<Map<String,String>> yearList = partyInfo.getYearListByCommon(map.get("Common"));

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.TRANSPARENT);

        TextView txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("Showroom : ");
        //txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.BLUE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText(""+map.get("Common"));
        //txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);
        tableLayout.addView(tableRow);

        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Month-Year");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        for (int j = 0; j<yearList.size(); j++) {
            txt = new TextView(context);
            txt.setId(j + 10);
            txt.setText(""+yearList.get(j).get("Year"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }

        tableLayout.addView(tableRow);
        //TODO:Set Row data
        for (int j=0; j<monthList.size(); j++) {

            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText(monthList.get(j).get("MonthName")+" ("+ monthList.get(j).get("Month")+")");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            for (int k = 0; k < yearList.size(); k++) {

                Map<String,Integer> qtyAmt = partyInfo.getQtyAmtListByCommon(Integer.valueOf(monthList.get(j).get("Month")),Integer.valueOf(yearList.get(k).get("Year")),map.get("Common"));
                int qty = 0,amt = 0;
                if (!qtyAmt.isEmpty()){
                    qty = qtyAmt.get("Quantity");
                    amt = qtyAmt.get("Amount");
                }else{
                    qty = 0;
                    amt = 0;
                }
                txt = new TextView(context);
                txt.setId(j + 300);
                txt.setText(qty+" / ₹"+amt);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow1.addView(txt);
            }
            tableLayout.addView(tableRow1);
        }
    }
    //TODO: Display Application Wise TableLayout
    private void setTableLayoutApplicationWise(Map<String,String> map, TableLayout tableLayout){

        List<Map<String,String>> monthList = partyInfo.getMonthListByCommon(map.get("Common"));
        List<Map<String,String>> yearList = partyInfo.getYearListByCommon(map.get("Common"));

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.TRANSPARENT);

        TextView txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("Application / App Type : ");
        //txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.BLUE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText(map.get("Common")+" / "+map.get("CommonType"));
        //txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);
        tableLayout.addView(tableRow);

        tableRow = new TableRow(context);
        tableRow.setId(i + 110);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Month-Year");
        txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        for (int j = 0; j<yearList.size(); j++) {
            txt = new TextView(context);
            txt.setId(j + 10);
            txt.setText(""+yearList.get(j).get("Year"));
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.WHITE);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setPadding(10, 10, 10, 10);
            tableRow.addView(txt);
        }

        tableLayout.addView(tableRow);
        //TODO:Set Row data
        for (int j=0; j<monthList.size(); j++) {

            TableRow tableRow1 = new TableRow(context);
            tableRow1.setId(j + 10);
            tableRow1.setPadding(10, 10, 10, 10);
            tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);
            tableRow1.setBackgroundColor(Color.TRANSPARENT);

            txt = new TextView(context);
            txt.setId(j + 200);
            txt.setText(monthList.get(j).get("MonthName")+" ("+ monthList.get(j).get("Month")+")");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setTextColor(Color.BLACK);
            txt.setGravity(Gravity.LEFT);
            txt.setPadding(10, 10, 10, 10);
            tableRow1.addView(txt);

            for (int k = 0; k < yearList.size(); k++) {

                Map<String,Integer> qtyAmt = partyInfo.getQtyAmtListByCommon(Integer.valueOf(monthList.get(j).get("Month")),Integer.valueOf(yearList.get(k).get("Year")),map.get("Common"));
                int qty = 0,amt = 0;
                if (!qtyAmt.isEmpty()){
                    qty = qtyAmt.get("Quantity");
                    amt = qtyAmt.get("Amount");
                }else{
                    qty = 0;
                    amt = 0;
                }
                txt = new TextView(context);
                txt.setId(j + 300);
                txt.setText(qty+" / ₹"+amt);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setTextColor(Color.BLACK);
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setPadding(10, 10, 10, 10);
                tableRow1.addView(txt);
            }
            tableLayout.addView(tableRow1);
        }
    }
    //TODO: Display Fair Delivery Percent Wise TableLayout
    private void setTableLayoutFairDeliveryPercent(PartyCompleteInfo.FairDeliveryParcent dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Year:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getYearSales()==null?"":dataset.getYearSales()));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Percentage:");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getFairIDDeliveryPercent()==null?"":dataset.getFairIDDeliveryPercent()+"%"));
        tableLayout.addView(v1);
    }
}
