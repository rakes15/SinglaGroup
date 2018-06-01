package orderbooking.customerlist.partyinfo.adapter;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.singlagroup.R;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import orderbooking.customerlist.partyinfo.model.ChildInfo;
import orderbooking.customerlist.partyinfo.model.GroupInfo;


/**
 * Created by Gourav on 08-03-2016.
 */
public class CustomAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<GroupInfo> deptList;

    public CustomAdapter(Context context, ArrayList<GroupInfo> deptList) {
        this.context = context;
        this.deptList = deptList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,View view, ViewGroup parent) {

        ChildInfo detailInfo = (ChildInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_view_holder, null);
        }
        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.table_Layout);
        setTableLayout(tableLayout,detailInfo);
        return view;
    }
    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return deptList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return deptList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,ViewGroup parent) {

        GroupInfo headerInfo = (GroupInfo) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.cardview_header, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.textView_Header);
        heading.setTextColor(context.getResources().getColor(R.color.Maroon));
        heading.setPadding(15,15,15,15);
        heading.setText(headerInfo.getName().trim());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setTableLayout(TableLayout tableLayout,ChildInfo detailInfo){
        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();
        //TODO: 1nd Row
        View view = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader = (TextView) view.findViewById(R.id.header);
        TextView txtContent = (TextView) view.findViewById(R.id.content);
        if (detailInfo.getName().contains(":")){
            String[] str = detailInfo.getName().split(":");
            String Header = str[0]+" :";
            String Content = (str.length>1?(str[1]==null || str[1].equals("null")?"":str[1]):"");
            String Link = (str.length>2?(str[2]==null || str[2].equals("null")?"":str[2]):"");
            String Link2 = (str.length>3?(str[3]==null || str[3].equals("null")?"":str[3]):"");
            String Clink = Link+":"+Link2;
            txtHeader.setText(Header);
            txtContent.setText(Content);
            txtContent.setTag(Clink+"");
            txtContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String FilePath = v.getTag().toString();
                    DialogViewImage(context,FilePath);
                }
            });
        }else{
            txtHeader.setText(""+detailInfo.getName());
            txtHeader.setTextColor(context.getResources().getColor(R.color.Brown));
            txtContent.setText("");
        }
        tableLayout.addView(view);
    }
    private void DialogViewImage(final Context context,String Url) {
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_view_image);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        ImageView imageView = (ImageView) dialog.findViewById(R.id.Image_View);
        WebView webView = (WebView) dialog.findViewById(R.id.web_view);
        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.getSettings().setBuiltInZoomControls(true);
        webView .loadUrl(Url);
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimeType,long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition,mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(context, "Downloading File",Toast.LENGTH_LONG).show();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}