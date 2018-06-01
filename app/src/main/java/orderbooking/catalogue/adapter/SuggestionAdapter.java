package orderbooking.catalogue.adapter;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.singlagroup.R;

/**
 * Created by Rakesh on 06-Sep-16.
 */

public class SuggestionAdapter extends CursorAdapter {
    public SuggestionAdapter(Context context, Cursor cursor)     {
        super(context, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_suggestion_item, parent, false);
        return view;
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor){
        // reference
        LinearLayout root = (LinearLayout) view;
        root.setBackgroundColor(ContextCompat.getColor(context, R.color.White));
        TextView titleTextView = (TextView) root.findViewById(R.id.search_suggestion_item_title);
        TextView subtitleTextView = (TextView) root.findViewById(R.id.search_suggestion_item_subtitle);

        // content
        final int index0 = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY);
        final int index1 = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
        final int index2 = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
        titleTextView.setText(cursor.getString(index0)+" "+cursor.getString(index1));
        subtitleTextView.setText(cursor.getString(index2));
    }
    public void refill(Context context, Cursor cursor){
        changeCursor(cursor);
    }
}