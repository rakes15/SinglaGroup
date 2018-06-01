package orderbooking.view_order_details.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.DatabaseSqlLiteHandlerAddToBox;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderViewDetails;
import orderbooking.StaticValues;
import orderbooking.catalogue.addtobox.adapter.RecyclerBoxListAdapter;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxListDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.EventItem;
import orderbooking.view_order_details.OrderViewItemByGroupActivity;
import orderbooking.view_order_details.dataset.OrderViewGroupDataset;
import orderbooking.view_order_details.dataset.OrderViewItemByGroupDataset;
import services.NetworkUtils;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class OrderViewGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<OrderViewGroupDataset> mapList;
    android.view.Display display;
    int height,width;
    ProgressDialog spotsDialog;
    DatabaseSqlLiteHandlerOrderViewDetails DBHandler;
    public static List<OrderViewGroupDataset> listGroup = new ArrayList<>();
    private static String TAG = OrderViewGroupAdapter.class.getSimpleName();
    private final int GROUP_LIST = 0;
    public OrderViewGroupAdapter(Context context, List<OrderViewGroupDataset> mapList) {
        this.context=context;
        this.mapList=mapList;
        this.DBHandler = new DatabaseSqlLiteHandlerOrderViewDetails(context);
        this.inflater = LayoutInflater.from(context);
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setCanceledOnTouchOutside(false);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width= StaticValues.mViewWidth;
        height=StaticValues.mViewHeight;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == GROUP_LIST)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_view_group_design, parent, false);
            return new RecyclerBoxGroupHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof RecyclerBoxGroupHolder)
        {
            final RecyclerBoxGroupHolder VHholder = (RecyclerBoxGroupHolder)holder;
            final int pos=position;
            VHholder.txtView.setText(""+mapList.get(position).getGroupName()+"\n("+mapList.get(position).getMainGroup()+")");
//            if (mapList.get(position).getGroupName().equals("Kurta")) {
//                Glide.with(context).load(R.drawable.kurta).asGif().placeholder(R.drawable.placeholder_new).into(VHholder.imageView);
//            }else if (mapList.get(position).getGroupName().equals("Shirts")){
//                //String Url= "http://animationsa2z.com/attachments/Image/clothes/clothes9.gif";
//                Glide.with(context).load(R.drawable.shirt).asGif().placeholder(R.drawable.placeholder_new).into(VHholder.imageView);
//            }else{
                Picasso.with(context).load(mapList.get(position).getGroupImage()).placeholder(R.drawable.placeholder_new).into(VHholder.imageView);
            //}
            VHholder.txtViewTotalStyle.setText("Style: "+mapList.get(position).getTotalStyle());
            VHholder.txtViewTotalQty.setText("Qty: "+mapList.get(position).getTotalQty());
            VHholder.txtViewTotalAmt.setText("Amt: "+mapList.get(position).getTotalAmount());
            VHholder.txtLastBookedDate.setText("Last Booked:");
            VHholder.txtLastBookedBy.setText("Time: "+ DateFormatsMethods.DaysHoursMinutesCount(mapList.get(position).getLastBookDateTime())+"\nBy: "+ mapList.get(position).getFullName()+" ("+mapList.get(position).getUserName()+") "+mapList.get(position).getEmpCVType()+" "+mapList.get(position).getEmpCVName());
            VHholder.cardView.setTag(R.id.dataset,mapList.get(pos));
            VHholder.cardView.setTag(R.id.position,pos);
            VHholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderViewGroupDataset dataset = (OrderViewGroupDataset)v.getTag(R.id.dataset);
                    Intent intent = new Intent(context, OrderViewItemByGroupActivity.class);
                    intent.putExtra("Key",dataset);
                    context.startActivity(intent);
                }
            });
            VHholder.checkBox.setTag(R.id.dataset,mapList.get(pos));
            VHholder.checkBox.setTag(R.id.position,pos);
            VHholder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final OrderViewGroupDataset dataset = (OrderViewGroupDataset) buttonView.getTag(R.id.dataset);
                    final int positionTag = (int) buttonView.getTag(R.id.position);
                    if (isChecked == true){
                        if (listGroup.contains(dataset)) {
                            listGroup.remove(dataset);
                            listGroup.add(dataset);
                        }else{
                            listGroup.add(dataset);
                        }
                    }else{
                        if (listGroup.contains(dataset)){
                            listGroup.remove(dataset);
                        }
                    }
                }
            });
            VHholder.imageViewExDatetime.setTag(R.id.dataset,mapList.get(pos));
            VHholder.imageViewExDatetime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderViewGroupDataset dataset = (OrderViewGroupDataset) v.getTag(R.id.dataset);
                    DialogExpectedDeliveryDatetimeUpdate(dataset);
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return GROUP_LIST;
    }
    @Override
    public int getItemCount() {
        return mapList==null?0:mapList.size();
    }
    public void delete(int postion){
        mapList.remove(postion);
        notifyItemRemoved(postion);
    }
    public class RecyclerBoxGroupHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewTotalStyle,txtViewTotalQty,txtViewTotalAmt,txtLastBookedDate,txtLastBookedBy;
        ImageView imageView,imageViewExDatetime;
        CardView cardView;
        CheckBox checkBox;
        public RecyclerBoxGroupHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            imageViewExDatetime = (ImageView) itemView.findViewById(R.id.imageView_expected_date);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewTotalStyle = (TextView) itemView.findViewById(R.id.textView_TotalStyle);
            txtViewTotalQty = (TextView) itemView.findViewById(R.id.textView_TotalQty);
            txtViewTotalAmt = (TextView) itemView.findViewById(R.id.textView_TotalAmt);
            txtLastBookedDate = (TextView) itemView.findViewById(R.id.textView_lastbookedDate);
            txtLastBookedBy = (TextView) itemView.findViewById(R.id.textView_lastbookedby);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_group);
        }
    }
    private void CallVolleyStyleDelete(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID,final int positionTag){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderItemSubItemColorDel", new Response.Listener<String>()
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
                        //TODO: Delete Group Wise
                        delete(positionTag);
                        MessageDialog.MessageDialog(context,"",Msg);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());

                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hidepDialog();
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
                Log.d(TAG,"delete group wise parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }
    private void DialogExpectedDeliveryDatetimeUpdate(final OrderViewGroupDataset dataset){
        final Dialog dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_expected_delivery_date);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final EditText editTextDelDate = (EditText) dialog.findViewById(R.id.ex_del_date);
        final EditText editTextDelTime = (EditText) dialog.findViewById(R.id.ex_del_time);
        editTextDelDate.setInputType(InputType.TYPE_NULL);
        editTextDelTime.setInputType(InputType.TYPE_NULL);
        editTextDelDate.setText(""+DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
        editTextDelTime.setText(""+DateFormatsMethods.getDateTime().substring(11,16));
        Button btnUpdate = (Button) dialog.findViewById(R.id.button_Update);
        Button btnCancel = (Button) dialog.findViewById(R.id.button_Cancel);
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
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ExDate = editTextDelDate.getText().toString();
                String ExTime = editTextDelTime.getText().toString();
                if (!ExDate.isEmpty() && !ExTime.isEmpty()) {
                    String ExDateTime = DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDate) + " " + ExTime + ":00";
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.editFlag == 1) {
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getOrderID(), dataset.getGroupID(), ExDateTime);
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                            }
                    }else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
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
    private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID,final String ExpectedDatetime){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderGorupSubGroupItemExpectedDateUpdate", new Response.Listener<String>()
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
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());

                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hidepDialog();
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
                params.put("ExpectedDelDate", ExpectedDatetime);
                Log.d(TAG,"Search barcode or style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
