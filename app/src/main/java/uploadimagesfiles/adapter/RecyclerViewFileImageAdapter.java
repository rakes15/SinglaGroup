package uploadimagesfiles.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.singlagroup.BuildConfig;
import com.singlagroup.R;
import com.singlagroup.customwidgets.BitmapScaler;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.PDFThumbnail;

import java.io.File;
import java.util.List;

import uploadimagesfiles.FileImageUplodingAcitvity;

/**
 * Created by Rakesh on 25-Oct-16.
 */
public class RecyclerViewFileImageAdapter extends RecyclerView.Adapter<RecyclerViewFileImageAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<String> mapList;
    public RecyclerViewFileImageAdapter(Context context, List<String> mapList) {
        this.context=context;
        this.mapList=mapList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_image, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        String Path = mapList.get(position);
        File file;
        if (Path.contains("Cam/")){
            file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp/Cam/"+(new File(mapList.get(position)).getName()));
        }else {
            file = new File(Path);
        }
        String Name = file.getName();
        holder.txtView.setText(Name);
        if (Name.toLowerCase().contains(".jpg") || Name.toLowerCase().contains(".jpeg") || Name.toLowerCase().contains(".png")){
            try {
//                Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                //Log.d("FilePath","Path:"+file.getAbsolutePath().toString());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                //Log.e("Error", "Bitmap:" + bitmap.toString());
                bitmap.setDensity(200);
                Bitmap bm1 = Bitmap.createScaledBitmap (bitmap, 200, 350, false);
                //Bitmap bm1 = BitmapScaler.scaleToFitWidth(bitmap, 200);
                holder.imageView.setImageBitmap(bm1);
            }catch (Exception e){
                MessageDialog.MessageDialog(context,"Error","Exception: "+e.toString());
            }
        }else if (Name.contains(".pdf")){
            holder.imageView.setImageBitmap(PDFThumbnail.GenerateImageFromPdf(context,Path));
        }else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public void delete(int postion){
        mapList.remove(postion);
        notifyItemRemoved(postion);
        notifyDataSetChanged();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtStatus;
        CardView cardView;
        ImageView imageView;
        ProgressBar progressBar;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtView = (TextView) itemView.findViewById(R.id.textView_Title);
            txtStatus = (TextView) itemView.findViewById(R.id.textView_Status);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_Icon);
            progressBar = (ProgressBar) itemView.findViewById(R.id.ProgressBar1);
        }
    }

    //TODO : Multiple Thread Execute
    public class MultiAsyncTask extends AsyncTask<Void, Integer, Void> {

        ProgressBar myProgressBar;

        public MultiAsyncTask(ProgressBar target) {
            myProgressBar = target;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < 100; i++) {
                publishProgress(i);
                SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            myProgressBar.setProgress(values[0]);
        }
    }
    private void StartAsyncTaskInParallel(MultiAsyncTask task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
