package com.singlagroup.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.HomeAcitvity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.ModulePermissionDetailsDataset;

import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import me.grantland.widget.AutofitTextView;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class RecyclerViewChildCaptionAdapter extends RecyclerView.Adapter<RecyclerViewChildCaptionAdapter.RecyclerViewHolder>{

    private Context context;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private List<Map<String,String>> datasetList;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    public RecyclerViewChildCaptionAdapter(Context context, List<Map<String,String>> datasetList,RecyclerView recyclerView) {
        this.context=context;
        this.datasetList=datasetList;
        this.recyclerView=recyclerView;
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

        holder.txtView.setText(datasetList.get(position).get("Name"));
        holder.cardView.setTag(datasetList.get(position).get("ID"));
        int isModule = Integer.valueOf(datasetList.get(position).get("IsModule"));
        if(isModule==0){
            holder.txtView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.folder,0,0,0);
            holder.imageViewInfo.setVisibility(View.GONE);
        }else{
            holder.txtView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_module,0,0,0);
            holder.imageViewInfo.setVisibility(View.VISIBLE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isModule = Integer.valueOf(datasetList.get(position).get("IsModule"));
                if(isModule == 0) {
                    String ID = view.getTag().toString();
                    List<Map<String,String>> datasetList=DBHandler.getChildCaption(ID);
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setAdapter(new RecyclerViewChildCaptionAdapter(context,datasetList,recyclerView));
                    recyclerView.setLayoutManager(linearLayoutManager);
                }else if(isModule == 1) {
                    String ClassName = datasetList.get(position).get("ContentClass");
                    if (!ClassName.isEmpty() && ClassName!=null) {
                        try {
                            //TODO: Set Bundle
                            Bundle bundle = new Bundle();
                            bundle.putString("Title",datasetList.get(position).get("Name"));
                            bundle.putInt("ViewFlag",Integer.valueOf(datasetList.get(position).get("ViewFlag")));
                            bundle.putInt("EditFlag",Integer.valueOf(datasetList.get(position).get("EditFlag")));
                            bundle.putInt("CreateFlag",Integer.valueOf(datasetList.get(position).get("CreateFlag")));
                            bundle.putInt("RemoveFlag",Integer.valueOf(datasetList.get(position).get("RemoveFlag")));
                            bundle.putInt("PrintFlag",Integer.valueOf(datasetList.get(position).get("PrintFlag")));
                            bundle.putInt("ImportFlag",Integer.valueOf(datasetList.get(position).get("ImportFlag")));
                            bundle.putInt("ExportFlag",Integer.valueOf(datasetList.get(position).get("ExportFlag")));
                            bundle.putInt("Vtype",Integer.valueOf(datasetList.get(position).get("Vtype")));
                            //TODO: Intent the Activities by Class
                            Intent intent = new Intent(context, Class.forName(ClassName));
                            intent.putExtra("PermissionBundle",bundle);
                            context.startActivity(intent);
                            //context.finish();
                        } catch (Exception e) {
                            MessageDialog.MessageDialog(context, "Error", e.toString());
                        }
                    }else{
                        MessageDialog messageDialog = new MessageDialog();
                        messageDialog.MessageDialog(context, "", "", "Comming soon.....");
                    }
                }
            }
        });
        holder.imageViewInfo.setTag(datasetList.get(position).get("ID"));
        holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v,"Comming soon...",Snackbar.LENGTH_LONG).show();
                String CaptionID = v.getTag().toString();
                DialogModulePermissionsDetails(CaptionID);
            }
        });
    }
    @Override
    public int getItemCount() {

        return datasetList==null?0:datasetList.size();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        AutofitTextView autofitTextView;
        CardView cardView;
        ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtView = (TextView) itemView.findViewById(R.id.textView);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.ImageView_Info);
            autofitTextView = (AutofitTextView) itemView.findViewById(R.id.Autofit_TextView);
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

        //TODO: 3rd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout1, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("V-Type:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getVType()== 0 ? "" : dataset.getVType()));
        tableLayout1.addView(v2);

        //TODO: 4th Row
        View v3 = inflater.inflate(R.layout.table_row, tableLayout1, false);
        TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
        txtHeader3.setText("Permissions:");
        tableLayout1.addView(v3);

        int i=0;
        TableRow tableRow = new TableRow(context);
        tableRow.setId(i + 100);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setBackgroundColor(Color.DKGRAY);

        txt = new TextView(context);
        txt.setId(i + 2);
        txt.setText("View");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 3);
        txt.setText("Create");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 4);
        txt.setText("Edit");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 5);
        txt.setText("Delete");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 6);
        txt.setText("Print");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 7);
        txt.setText("Import");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        txt = new TextView(context);
        txt.setId(i + 8);
        txt.setText("Export");
        txt.setTextColor(Color.WHITE);
        txt.setPadding(10, 10, 10, 10);
        tableRow.addView(txt);

        tableLayout2.addView(tableRow);

        //TODO:Set Row data
        int j =0;
        TableRow tableRow1 = new TableRow(context);
        tableRow1.setId(j + 10);
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER);
        tableRow1.setBackgroundColor(Color.TRANSPARENT);

        txt = new TextView(context);
        txt.setId(j+200);
        txt.setText(""+(dataset.getViewFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j+300);
        txt.setText(""+(dataset.getCreateFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 400);
        txt.setText(""+(dataset.getEditFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 500);
        txt.setText(""+(dataset.getRemoveFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 600);
        txt.setText(""+(dataset.getPrintFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 700);
        txt.setText(""+(dataset.getPrintFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        txt = new TextView(context);
        txt.setId(j + 800);
        txt.setText(""+(dataset.getExportFlag() == 0 ? "N" : "Y"));
        txt.setTextColor(Color.BLACK);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setPadding(5, 5, 5, 5);
        tableRow1.addView(txt);

        tableLayout2.addView(tableRow1);
    }
}
