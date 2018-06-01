package uploadimagesfiles.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.PDFThumbnail;

import java.io.File;
import java.util.List;

import uploadimagesfiles.voucherdocupload.datasets.FileImageDataset;

/**
 * Created by Rakesh on 25-Oct-16.
 */
public class RecyclerViewFileImageWithDescriptionAdapter extends RecyclerView.Adapter<RecyclerViewFileImageWithDescriptionAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    public static List<FileImageDataset> mapList;
    public RecyclerViewFileImageWithDescriptionAdapter(Context context, List<FileImageDataset> mapList) {
        this.context=context;
        this.mapList=mapList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_image_with_decription, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        File file=new File(mapList.get(position).getPath());
        String Name = file.getName();
        String Path = file.getAbsolutePath();
        holder.txtFileName.setText("File Name: "+Name);
        if ((Name.contains(".jpg")) || (Name.contains(".jpeg")) || (Name.contains(".png")) || (Name.contains(".gif"))){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap.setDensity(200);
            Bitmap bm1 = Bitmap.createScaledBitmap (bitmap, 200, 350, false);
            holder.imageView.setImageBitmap(bm1);
        }else if (Name.contains(".pdf")){
            holder.imageView.setImageBitmap(PDFThumbnail.GenerateImageFromPdf(context,Path));
        }else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        holder.txtDescription.setText("Description: "+mapList.get(position).getDescription());
        holder.txtAddDescription.setTag(mapList.get(position).getDescription()+"/"+position);
        holder.txtAddDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] tag = view.getTag().toString().split("/");
                String Description = tag[0];
                int pos = Integer.valueOf(tag[1]);
                DialogAskDescriptions(Description,pos);
            }
        });
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

        TextView txtFileName,txtAddDescription,txtDescription;
        CardView cardView;
        ImageView imageView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtFileName = (TextView) itemView.findViewById(R.id.textView_Title);
            txtAddDescription = (TextView) itemView.findViewById(R.id.textView_Add_Description);
            txtDescription = (TextView) itemView.findViewById(R.id.textView_Description);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_Icon);
        }
    }

    private void DialogAskDescriptions(String Description, final int position){//final String Path, final String DeviceID, final String SessionID, final String UserID, final String GodownID, final String DivisionID,final String CompanyID, final String BranchID , final String VType, final String VHeading , final String VID , final String Attachment,final String Type){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_name_remaks);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final EditText edtDescription=(EditText)dialog.findViewById(R.id.editTxt_name);
        final EditText edtRemarks=(EditText)dialog.findViewById(R.id.editTxt_Remarks);
        edtRemarks.setVisibility(View.GONE);
        edtDescription.setHint("Description");
        edtDescription.setText(""+Description);
        Button submit=(Button)dialog.findViewById(R.id.button_Approve);
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Des = edtDescription.getText().toString().trim();
                String Remarks = "";//edtRemarks.getText().toString().trim();
                if (!Des.isEmpty() && Des !=null) {
                    mapList.get(position).setDescription(""+Des);
                    mapList.get(position).setDescFlag(true);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    MessageDialog.MessageDialog(context, "", "Description is mandatory!!!");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
