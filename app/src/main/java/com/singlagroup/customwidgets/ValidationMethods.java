package com.singlagroup.customwidgets;

/**
 * Created by rakes on 12-Jul-17.
 */

public class ValidationMethods {

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        Pattern.compile(emailPattern);
        return email.contains("@");
    }
}
