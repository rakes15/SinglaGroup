package com.singlagroup.customwidgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;

/**
 * Created by rakes on 28-Sep-17.
 */

public class CustomTextView {

    public static View setTableRow2Columns(Context context,View view, String TextHeader, String TextContent){
        //TODO: Text View Header
        TextView txtHeader= (TextView) view.findViewById(R.id.header);
        txtHeader.setText(TextHeader+":");
        //TODO: Text View Content
        TextView txtContent= (TextView) view.findViewById(R.id.content);
        txtContent.setText(""+(TextContent==null || TextContent.equals("null") ? "" : TextContent));
        //return view
        return view;
    }
}
