package inventory.analysis.catalogue.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.singlagroup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import inventory.analysis.catalogue.dataset.RecyclerGroupDataset;
import orderbooking.StaticValues;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerGroupIconAdapter extends RecyclerView.Adapter<RecyclerGroupIconAdapter.RecyclerGroupHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerGroupDataset> mapList;
    int height,width;
    public RecyclerGroupIconAdapter(Context context, List<RecyclerGroupDataset> mapList) {
        this.context=context;
        this.mapList=mapList;
        this.inflater = LayoutInflater.from(context);
        width= StaticValues.mViewWidth;
        height=StaticValues.mViewHeight;
    }
    @Override
    public RecyclerGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_design, parent, false);
        RecyclerGroupHolder viewHolder=new RecyclerGroupHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerGroupHolder holder, final int position) {

        final int pos=position;
        holder.txtView.setText(mapList.get(position).getGroupName());
        String ImageUrl = (mapList.get(position).getGroupImage().isEmpty() ? "temp": mapList.get(position).getGroupImage());
        Picasso.with(context).load(ImageUrl).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        holder.cardView.setTag(mapList.get(position).getGroupID());
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String ID=v.getTag().toString();
//                Intent intent=new Intent(context, ItemListActivity.class);
//                intent.putExtra("GroupID",ID);
//                context.startActivity(intent);
//                //((Activity)context).finish();
//            }
//        });
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public class RecyclerGroupHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        ImageView imageView;
        CardView cardView;

        public RecyclerGroupHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
