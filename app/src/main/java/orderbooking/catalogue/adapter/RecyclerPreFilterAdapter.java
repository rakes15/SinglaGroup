package orderbooking.catalogue.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.singlagroup.R;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerPreFilterAdapter extends RecyclerView.Adapter<RecyclerPreFilterAdapter.RecyclerPreFilterHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Map<String,String>> mapList;
    public RecyclerPreFilterAdapter(Context context, List<Map<String,String>> mapList) {
        this.context=context;
        this.mapList=mapList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerPreFilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_pre_filter, parent, false);
        RecyclerPreFilterHolder viewHolder=new RecyclerPreFilterHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerPreFilterHolder holder, int position) {

        //final int pos=position;
        holder.txtViewTitle.setText(""+mapList.get(position).get("Name"));
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mapList.get(position);
    }
    public class RecyclerPreFilterHolder extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        public RecyclerPreFilterHolder(View itemView) {
            super(itemView);
            txtViewTitle = (TextView) itemView.findViewById(R.id.text_View_title);
        }
    }

}
