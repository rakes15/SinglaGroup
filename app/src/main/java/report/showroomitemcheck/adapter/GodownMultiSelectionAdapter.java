package report.showroomitemcheck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.singlagroup.R;
import java.util.ArrayList;
import java.util.List;

import report.showroomitemcheck.model.GodownCheckBox;

public class GodownMultiSelectionAdapter extends ArrayAdapter<GodownCheckBox> {
    private Context mContext;
    private List<GodownCheckBox> listState;
    private boolean isFromView = false;
    private boolean[] checked;

    public GodownMultiSelectionAdapter(Context context, int resource, List<GodownCheckBox> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = objects;
        checked= new boolean[listState.size()];
        for(int i=0; i<checked.length; i++){
            checked[i]=false;
        }
    }
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mCheckBox.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        //isFromView = true;
        //listState.get(position).isSelected() = checked[position];
        holder.mCheckBox.setChecked(checked[position]);
        //isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(listState.get(position).getID()+"/"+position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[position]=isChecked;
                String[] getTag = buttonView.getTag().toString().split("/");
                String ID = getTag[0];
                int pos = Integer.valueOf(getTag[1]);
                for(int z=0;z<listState.size();z++) {
                    if (checked[z] == true) {
                        listState.get(z).setSelected(true);
                    }else {
                        listState.get(z).setSelected(false);
                    }
                }
                //Toast.makeText(mContext,"ID:"+ID+"\nPos:"+Pos,Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}