package com.singlagroup;

/**
 * Created by rakes on 03-Feb-18.
 */

public class GlobleValues {

    public static int multi_action_flag = 0; //TODO: 0 is nothing 1 is force Close 2 is Expected Delivery Date

    public static int MOBILE = 0;
    public static int EMAIL = 1;
    public static int PAN_NO = 2;
    public static int GSTIN = 3;
    public static int PINCODE = 4;
    // TODO: Mobile No etc. Validate
    public static String ValidateByRegex(String str,int RegexType){
        String result = "";
        if(RegexType == MOBILE) { // Mobile No
            if(str.matches("^[6-9][0-9]{9}$")){
                result = "";
            }else{
                result = "Mobile number is not valid";
            }
        }else if(RegexType == EMAIL) { // Email
            if(str.matches("^([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)$")){
                result = "";
            }else{
                result = "Email is not valid";
            }
        }else if(RegexType == PAN_NO) { // Pan No
            if(str.matches("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}$")){
                result = "";
            }else{
                result = "PAN number is not valid";
            }
        }else if(RegexType == GSTIN) { // GSTIN
            if(str.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")){
                result = "";
            }else{
                result = "GST Number is not valid";
            }
        }else if(RegexType == PINCODE) { // PinCode
            if(str.matches("^[1-9][0-9]{5}$")){
                result = "";
            }else{
                result = "Pincode is not valid";
            }
        }
        return result;
    }
}
