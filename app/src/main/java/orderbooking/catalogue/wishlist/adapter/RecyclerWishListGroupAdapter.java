package orderbooking.catalogue.wishlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.singlagroup.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import orderbooking.StaticValues;
import orderbooking.catalogue.wishlist.GroupWishListActivity;
import orderbooking.catalogue.wishlist.WishListActivity;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistDataset;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerWishListGroupAdapter extends RecyclerView.Adapter<RecyclerWishListGroupAdapter.RecyclerWishListGroupHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerWishlistGroupDataset> mapList;
    List<RecyclerWishlistDataset> list;
    private CloseOrBookDataset closeOrBookDataset;
    android.view.Display display;
    int height,width;
    DatabaseSqlLiteHandlerWishlist DBHandler;
    public RecyclerWishListGroupAdapter(Context context, List<RecyclerWishlistGroupDataset> mapList, CloseOrBookDataset closeOrBookDataset) {
        this.context=context;
        this.mapList=mapList;
        this.closeOrBookDataset=closeOrBookDataset;
        DBHandler=new DatabaseSqlLiteHandlerWishlist(context);
        this.inflater = LayoutInflater.from(context);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        width= StaticValues.mViewWidth;
        height= StaticValues.mViewHeight;
    }
    @Override
    public RecyclerWishListGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_design, parent, false);
        RecyclerWishListGroupHolder viewHolder=new RecyclerWishListGroupHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerWishListGroupHolder holder, final int position) {

        final int pos=position;
        holder.txtView.setText(""+mapList.get(position).getGroupName()+"\n("+mapList.get(position).getMainGroup()+")");
        Picasso.with(context).load(mapList.get(position).getGroupImage()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        holder.cardView.setTag(mapList.get(position).getGroupID());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID=v.getTag().toString();
                GroupWishListActivity.groupflag = 1;
                GroupWishListActivity.PreGroupID = ID;
                Intent intent = new Intent(context,WishListActivity.class);
                intent.putExtra("Key",closeOrBookDataset);
                context.startActivity(intent);
                //list=DBHandler.getWishlistItemsGroupBy(ID);
                //ItemWishList(list);
                //if(BoxActivity.actionBar!=null){BoxActivity.actionBar.invalidateOptionsMenu();}
            }
        });

    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public class RecyclerWishListGroupHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        ImageView imageView;
        CardView cardView;

        public RecyclerWishListGroupHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
