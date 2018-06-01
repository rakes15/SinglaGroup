package com.singlagroup.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.singlagroup.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> arrayList;

        public CustomSpinnerAdapter(Context context,ArrayList<String> arrayList) {
            this.arrayList=arrayList;
            this.activity = context;
        }
        public int getCount()
        {
            return arrayList.size();
        }

        public Object getItem(int i)
        {
            return arrayList.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }

        @Override
        public boolean isEnabled(int position){
            if(position == 0)
            {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            }
            else
            {
                return true;
            }
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(14, 14, 14, 14);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(arrayList.get(position));
            if(position == 0){
                // Set the hint text color gray
                txt.setTextColor(Color.GRAY);
            }
            else {
                txt.setTextColor(Color.BLACK);
            }
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(activity);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(14, 14, 14, 14);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(arrayList.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

    }