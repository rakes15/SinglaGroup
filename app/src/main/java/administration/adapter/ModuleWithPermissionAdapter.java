package administration.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import administration.database.DatabaseSqlLiteHandlerVoucherAuthorization;
import administration.datasets.Module;
import administration.datasets.Module;
import orderbooking.StaticValues;
import services.NetworkUtils;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class ModuleWithPermissionAdapter extends RecyclerView.Adapter<ModuleWithPermissionAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog,pDialog;
    private LayoutInflater inflater;
    private List<Module> datasetList,filterDatasetList;
    private static String TAG = ModuleWithPermissionAdapter.class.getSimpleName();
    public ModuleWithPermissionAdapter(Context context, List<Module> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
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

        final Module dataset = filterDatasetList.get(position);
        holder.cardView.setTag(dataset);
        //TODO: call TableLayout method
        setTableLayout(dataset,position,holder.tableLayout,holder.tableLayout2,holder.tableLayout3);
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
                    for (Module item : datasetList) {
                        if(item.getCaption()!=null && item.getVType()!=null)
                        if (item.getCaption().toLowerCase().contains(text.toLowerCase()) ||
                            item.getVType().toString().toLowerCase().contains(text.toLowerCase()) ) {
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
        TableLayout tableLayout,tableLayout2,tableLayout3;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(final Module dataset, final int pos, final TableLayout tableLayout, final TableLayout tableLayout2, TableLayout tableLayout3){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
       //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row_single_column, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.content);
        txtHeader.setText(""+dataset.getCaption());
        tableLayout.addView(v);
        //TODO: TableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //View view = inflater.inflate(R.layout.table_row_edittext, tableLayout2, false);
    }

    private void showDialog() {
        if(pDialog!=null) {
            pDialog.show();
        }
    }
    private void hideDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
        }
    }
}
