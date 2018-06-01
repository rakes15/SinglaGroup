package com.singlagroup.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseLogInUserSubDataset {

    @SerializedName("UserDataRights")
    private List<Map<String,String>> UserDataRights;
    @SerializedName("APIURL")
    private String APIURL;
    @SerializedName("SessionID")
    private String SessionID;
    @SerializedName("CaptionRights")
    private List<Map<String,String>> CaptionRights;
    @SerializedName("BasicInfo")
    private Map<String,String> BasicInfo;

    public List<Map<String,String>> getUserDataRights() {
        return UserDataRights;
    }
    public void setUserDataRights(List<Map<String,String>> UserDataRights) {
        this.UserDataRights = UserDataRights;
    }

    public String getAPIURL() {
        return APIURL;
    }
    public void setAPIURL(String APIURL) {
        this.APIURL = APIURL;
    }

    public String getSessionID() {
        return SessionID;
    }
    public void setSessionID(String SessionID) {
        this.SessionID = SessionID;
    }

    public List<Map<String,String>> getCaptionRights() {
        return CaptionRights;
    }
    public void setCaptionRights(List<Map<String,String>> CaptionRights) {
        this.CaptionRights = CaptionRights;
    }

    public Map<String,String> getBasicInfo() {
        return BasicInfo;
    }
    public void setBasicInfo(Map<String,String> BasicInfo) {
        this.BasicInfo = BasicInfo;
    }
}
