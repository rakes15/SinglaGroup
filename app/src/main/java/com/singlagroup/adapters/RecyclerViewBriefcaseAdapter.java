package com.singlagroup.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.BriefcaseSettingActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.ModulePermissionDetailsDataset;

import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerUserInfo;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class RecyclerViewBriefcaseAdapter extends RecyclerView.Adapter<RecyclerViewBriefcaseAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<Map<String,String>> datasetList;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    public RecyclerViewBriefcaseAdapter(Context context, List<Map<String,String>> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.inflater = LayoutInflater.from(context);
        this.DBHandler=new DatabaseSqlLiteHandlerUserInfo(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_recyclerview, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        holder.txtView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_module,0,0,0);
        holder.txtView.setText(datasetList.get(position).get("Name")+"-"+datasetList.get(position).get("Vtype"));
        holder.cardView.setTag(datasetList.get(position).get("ID"));
        int briefcaseCheckBox = Integer.valueOf(datasetList.get(position).get("CheckBox"));
        if(briefcaseCheckBox == 0){
            holder.checkBox.setVisibility(View.GONE);
        }else if (briefcaseCheckBox == 1){
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.checkBox.setTag(datasetList.get(position).get("Vtype"));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                String VType = v.getTag().toString();
                if(cb.isChecked()){
                    BriefcaseSettingActivity.BriefcaseChecked.add(Integer.valueOf(VType));
                }else{
                    BriefcaseSettingActivity.BriefcaseChecked.remove(Integer.valueOf(VType));
                }
            }
        });
    }
    @Override
    public int getItemCount() {

        return datasetList==null?0:datasetList.size();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        CardView cardView;
        ImageView imageViewInfo;
        CheckBox checkBox;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtView = (TextView) itemView.findViewById(R.id.textView);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.ImageView_Info);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_briefcase);
        }
    }

    private void DialogModulePermissionsDetails(String CaptionID){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_module_permission_details);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        TableLayout tableLayout1 = (TableLayout) dialog.findViewById(R.id.table_Layout1);
        TableLayout tableLayout2 = (TableLayout) dialog.findViewById(R.id.table_Layout2);
        Button btnOk = (Button) dialog.findViewById(R.id.button_ok);
        setTableLayout(DBHandler.getModulePermissionDetails(CaptionID), tableLayout1, tableLayout2);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    //TODO: Display TableLayout
    private void setTableLayout(ModulePermissionDetailsDataset dataset, TableLayout tableLayout1, TableLayout tableLayout2){

        //TODO: TableLayout set
        tableLayout1.removeAllViewsInLayout();
        tableLayout1.removeAllViews();
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout1, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("User ID:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getUserID()==null?"":dataset.getUserID()));
        tableLayout1.addView(v);

        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout1, false);
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("User Name:");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getUserName()==null?"":dataset.getUserName()));
        tableLayout1.addView(v1);


        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 1);
        txt.setText("VType");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("View");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Edit");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 4);
        txt.setText("Create");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 5);
        txt.setText("Remove");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 6);
        txt.setText("Import");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 7);
        txt.setText("Export");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 8);
        txt.setText("Print");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout2.addView(tableRow);

        //TODO:Set Row data
        int j =0;
        TableRow tableRow1 = new TableRow(context);
        tableRow1.setId(j + 10);
        tableRow1.setBackgroundColor(Color.TRANSPARENT);

        txt = new TextView(context);
        txt.setId(j+100);
        txt.setText(""+dataset.getVType());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j+200);
        txt.setText(""+dataset.getViewFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j+300);
        txt.setText(""+dataset.getEditFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 400);
        txt.setText(""+dataset.getCreateFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 500);
        txt.setText(""+dataset.getRemoveFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 600);
        txt.setText(""+dataset.getImportFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 700);
        txt.setText(""+dataset.getExportFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 800);
        txt.setText(""+dataset.getPrintFlag());
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        tableLayout2.addView(tableRow1);
    }
}
