package com.singlagroup.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.AppPrinterService;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.RecommandedAppsDataset;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerUserInfo;

/**
 * Created by Rakesh on 04-March-17.
 */
public class RecyclerViewPrinterServicesAdapter extends RecyclerView.Adapter<RecyclerViewPrinterServicesAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<RecommandedAppsDataset> datasetList;
    public RecyclerViewPrinterServicesAdapter(Context context, List<RecommandedAppsDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_recyclerview_recommanded_apps, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        holder.txtView.setText(datasetList.get(position).getName());
        String Mandatory = (datasetList.get(position).getIsMandatory() == 1)?"Mandatory":"Optional";
        holder.txtViewSubTitle.setText(Mandatory);
        holder.cardView.setTag(datasetList.get(position).getURL());
        String Icon = datasetList.get(position).getIcon().replaceAll("\\s","%20");
        Picasso.with(context).load(Icon).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).into(holder.imageViewIcon);
        //Log.d("Tag",""+Icon);
        AppPrinterService obj = new AppPrinterService();
        int status = obj.AppPrinterServiceCheck(context,datasetList.get(position).getPackageName(),datasetList.get(position).getURL());
        if(status == 1){
            holder.imageViewDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_downloaded));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageDialog.MessageDialog(context,"",datasetList.get(position).getName()+" app is already installed.");
                }
            });
        }else{
            holder.imageViewDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_download));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Redirect on Playstore Via Url
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(datasetList.get(position).getURL()));
                    context.startActivity(intent);
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        return datasetList==null?0:datasetList.size();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewSubTitle;
        CardView cardView;
        ImageView imageViewDownload,imageViewIcon;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtView = (TextView) itemView.findViewById(R.id.textView);
            txtViewSubTitle = (TextView) itemView.findViewById(R.id.textView_subTitle);
            imageViewDownload = (ImageView) itemView.findViewById(R.id.ImageView_Download);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.ImageView_Icon);
        }
    }
}
