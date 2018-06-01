package orderbooking.catalogue.addtobox.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.singlagroup.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;
import DatabaseController.DatabaseSqlLiteHandlerAddToBox;
import orderbooking.StaticValues;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxGroupDataset;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxListDataset;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerBoxGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerBoxGroupDataset> mapList;
    private List<Map<String, String>> mapListHeader;
    private RecyclerView recyclerView;
    private LinearLayout linearLayoutPlaceOrderHeader;
    android.view.Display display;
    int height,width;
    DatabaseSqlLiteHandlerAddToBox DBHandler;
    private final int PLACE_HOLDER=0,GROUP_LIST = 0;
    public RecyclerBoxGroupAdapter(Context context, List<RecyclerBoxGroupDataset> mapList, RecyclerView recyclerView,LinearLayout linearLayoutPlaceOrderHeader) {
        this.context=context;
        this.linearLayoutPlaceOrderHeader=linearLayoutPlaceOrderHeader;
        this.mapList=mapList;
        this.recyclerView=recyclerView;
        linearLayoutPlaceOrderHeader.setVisibility(View.VISIBLE);
        DBHandler=new DatabaseSqlLiteHandlerAddToBox(context);
        this.inflater = LayoutInflater.from(context);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width= StaticValues.mViewWidth;
        height=StaticValues.mViewHeight;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == GROUP_LIST)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_box_group_design, parent, false);
            return new RecyclerBoxGroupHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        if(holder instanceof RecyclerPlaceOrderHolder)
//        {
//            RecyclerPlaceOrderHolder VHholder = (RecyclerPlaceOrderHolder)holder;
//            final int pos=position;
//            VHholder.txtViewHeader.setText("My Box");
//            VHholder.txtViewTotalStyle.setText(""+mapListHeader.ge);
//            VHholder.txtViewTotalQty.setText("My Box");
//            VHholder.txtViewTotalAmt.setText("My Box");
//            VHholder.txtViewPlaceOrder.setText("My Box");
//            VHholder.imageViewClose.setTag(mapList.get(position).getGroupID());
//            VHholder.imageViewBack.setTag(mapList.get(position).getGroupID());
//            VHholder.imageViewBack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String ID = v.getTag().toString();
//                }
//            });
//            VHholder.imageViewClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String ID = v.getTag().toString();
//                }
//            });
//        }
        if(holder instanceof RecyclerBoxGroupHolder)
        {
            RecyclerBoxGroupHolder VHholder = (RecyclerBoxGroupHolder)holder;
            final int pos=position;
            VHholder.txtView.setText(""+mapList.get(position).getGroupName()+"\n("+mapList.get(position).getMainGroup()+")");
            Picasso.with(context).load(mapList.get(position).getGroupImage()).placeholder(R.drawable.placeholder_new).into(VHholder.imageView);
            VHholder.cardView.setTag(mapList.get(position).getGroupID());
            VHholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ID = v.getTag().toString();
                    List<RecyclerBoxListDataset> list = DBHandler.getBoxItemsGroupBy(ID);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                    gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    RecyclerBoxListAdapter adapter = new RecyclerBoxListAdapter(context, list,linearLayoutPlaceOrderHeader);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(gridLayoutManager);
                }
            });
            List<Map<String,String>> list=DBHandler.getTotalQtyWithTotalItemByGroup(mapList.get(position).getGroupID());
            if(!list.isEmpty()){
                VHholder.txtViewTotalStyle.setText("Total Style: "+list.get(0).get("TotalItem"));
                VHholder.txtViewTotalQty.setText("Total Qty: "+list.get(0).get("TotalQty"));
                VHholder.txtViewTotalAmt.setText("Total Amt: "+list.get(0).get("TotalAmt"));
            }
            else{
                VHholder.txtViewTotalStyle.setVisibility(View.GONE);
                VHholder.txtViewTotalQty.setVisibility(View.GONE);
                VHholder.txtViewTotalAmt.setVisibility(View.GONE);
            }
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
//    private boolean isPositionHeader (int position) {
//        return position == 0;
//    }
    public class RecyclerPlaceOrderHolder extends RecyclerView.ViewHolder {

        TextView txtViewHeader,txtViewTotalStyle,txtViewTotalQty,txtViewTotalAmt,txtViewPlaceOrder;
        ImageView imageViewClose,imageViewBack;

        public RecyclerPlaceOrderHolder(View itemView) {
            super(itemView);
            //imageViewClose = (ImageView) itemView.findViewById(R.id.imageView_Close);
            imageViewBack = (ImageView) itemView.findViewById(R.id.imageView_Back);
//            txtViewHeader = (TextView) itemView.findViewById(R.id.txtViewHeader);
//            txtViewTotalStyle = (TextView) itemView.findViewById(R.id.txtViewTotalItems);
//            txtViewTotalQty = (TextView) itemView.findViewById(R.id.txtViewTotalQty);
//            txtViewTotalAmt = (TextView) itemView.findViewById(R.id.txtViewTotalAmt);
            //txtViewPlaceOrder = (TextView) itemView.findViewById(R.id.txtView_PlaceOrder);
        }
    }
    public class RecyclerBoxGroupHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewTotalStyle,txtViewTotalQty,txtViewTotalAmt;
        ImageView imageView;
        CardView cardView;

        public RecyclerBoxGroupHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewTotalStyle = (TextView) itemView.findViewById(R.id.textView_TotalStyle);
            txtViewTotalQty = (TextView) itemView.findViewById(R.id.textView_TotalQty);
            txtViewTotalAmt = (TextView) itemView.findViewById(R.id.textView_TotalAmt);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
