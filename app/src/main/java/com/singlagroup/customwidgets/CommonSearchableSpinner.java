package com.singlagroup.customwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.singlagroup.R;
import com.singlagroup.adapters.CityFilterableAdapter;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.datasets.CityDataset;

import java.util.ArrayList;
import java.util.Map;

public class CommonSearchableSpinner extends AppCompatSpinner implements View.OnTouchListener,CommonSearchSpinnerDialog.SearchableItem {

    public static final int NO_ITEM_SELECTED = -1;
    private Context _context;
    private ArrayList<Map<String,String>> _items;
    private CommonSearchSpinnerDialog _searchableListDialog;
    private boolean _isDirty;
    private CommonSearchSpinnerFilterableAdapter _arrayAdapter;
    private String _strHintText;
    private boolean _isFromInit;

    public CommonSearchableSpinner(Context context) {
        super(context);
        this._context = context;
        init();
    }

    public CommonSearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SearchableSpinner_hintText) {
                _strHintText = a.getString(attr);
            }
        }
        a.recycle();
        init();
    }

    public CommonSearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        init();
    }

    private void init() {
        _items = new ArrayList<Map<String,String>>();
        _searchableListDialog = CommonSearchSpinnerDialog.newInstance(_items);
        _searchableListDialog.setOnSearchableItemClickListener(this);
        setOnTouchListener(this);

        _arrayAdapter = (CommonSearchSpinnerFilterableAdapter) getAdapter();
        if (!TextUtils.isEmpty(_strHintText)) {
            CommonSearchSpinnerFilterableAdapter adapter=new CommonSearchSpinnerFilterableAdapter(_context,_items,_items);
            adapter.getFilter().filter(_strHintText);
            _isFromInit = true;
            setAdapter(adapter);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (null != _arrayAdapter) {

                // Refresh content #6
                // Change Start
                // Description: The items were only set initially, not reloading the data in the
                // spinner every time it is loaded with items in the adapter.
                _items.clear();
                for (int i = 0; i < _arrayAdapter.getCount(); i++) {
                    Map<String,String> temp=(Map<String,String>)_arrayAdapter.getItem(i);
                    _items.add(temp);
                }
                // Change end.

                _searchableListDialog.show(scanForActivity(_context).getFragmentManager(), "TAG");
            }
        }
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

        if (!_isFromInit) {
            _arrayAdapter = (CommonSearchSpinnerFilterableAdapter) adapter;
            if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                CommonSearchSpinnerFilterableAdapter cityadapter=new CommonSearchSpinnerFilterableAdapter(_context,_items,_items);
                cityadapter.getFilter().filter(_strHintText);
                super.setAdapter(cityadapter);
            } else {
                super.setAdapter(adapter);
            }

        } else {
            _isFromInit = false;
            super.setAdapter(adapter);
        }
    }

    @Override
    public void onSearchableItemClicked(Object item, int position) {
        setSelection(_items.indexOf(item));

        if (!_isDirty) {
            _isDirty = true;
            setAdapter(_arrayAdapter);
            setSelection(_items.indexOf(item));
        }
    }

    public void setTitle(String strTitle) {
        _searchableListDialog.setTitle(strTitle);
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _searchableListDialog.setPositiveButton(strPositiveButtonText);
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _searchableListDialog.setPositiveButton(strPositiveButtonText, onClickListener);
    }

    public void setOnSearchTextChangedListener(CommonSearchSpinnerDialog.OnSearchTextChanged onSearchTextChanged) {
        _searchableListDialog.setOnSearchTextChangedListener(onSearchTextChanged);
    }

    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    @Override
    public int getSelectedItemPosition() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return NO_ITEM_SELECTED;
        } else {
            return super.getSelectedItemPosition();
        }
    }

    @Override
    public Object getSelectedItem() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return null;
        } else {
            return super.getSelectedItem();
        }
    }
}