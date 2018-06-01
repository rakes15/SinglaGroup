package com.singlagroup.customwidgets;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import com.singlagroup.R;
import java.lang.reflect.Field;

/**
 * Created by Rakesh on 18-Jan-17.
 */

public class CursorColor {

    public static void  CursorColor(EditText editText){
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, R.drawable.cursor);
        } catch (Exception ignored) {
            Log.e("CusrsorColor",""+ignored.toString());
        }
    }
}
