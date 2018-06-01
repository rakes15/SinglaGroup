package com.singlagroup.customwidgets;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by rakes on 18-Oct-17.
 */

public class CustomEditText {

    private static String blockCharacterSet = "~#^|$%&*!'"; //Special characters to block

    public static InputFilter SetFilter(){
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        return filter;
    }
}
