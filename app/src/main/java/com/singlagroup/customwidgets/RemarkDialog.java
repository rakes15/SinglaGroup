package com.singlagroup.customwidgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.singlagroup.R;

/**
 * Created by Rakesh on 03-Oct-16.
 */

public class RemarkDialog {

    public void MessageDialog(Context context,String Title,String Event,String Mesaage){
        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cardview_message_box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle(Title);
        TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
        TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
        Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
        txtViewMessageTitle.setText(Event);
        txtViewMessage.setText(Mesaage);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public static  void MessageDialog(Context context,String Title,String Mesaage){
        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cardview_message_box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
        TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
        Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
        txtViewMessageTitle.setText(Title);
        txtViewMessage.setText(Mesaage);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
