package com.singlagroup.datasets;
import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class AppCheckDataset implements Serializable {
    private static final long serialVersionUID = 1L;
    private int RequestStatus;
    private int AllowUser;
    private int AutoLogIN;
    private String DeviceID;
    private String DefaultUser;
    private String Password;
    private String APIUrl;
    private String AppDownloadLink;
    private String Msg;
    public AppCheckDataset(int RequestStatus, int AllowUser, int AutoLogIN, String DeviceID, String DefaultUser, String Password, String APIUrl, String AppDownloadLink, String Msg) {

        this.RequestStatus = RequestStatus;
        this.AllowUser = AllowUser;
        this.AutoLogIN = AutoLogIN;
        this.DeviceID = DeviceID;
        this.DefaultUser = DefaultUser;
        this.Password = Password;
        this.APIUrl = APIUrl;
        this.AppDownloadLink = AppDownloadLink;
        this.Msg = Msg;
    }
    public int getRequestStatus() {
        return RequestStatus;
    }
    public void setRequestStatus(int RequestStatus) {
        this.RequestStatus = RequestStatus;
    }

    public int getAllowUser() {
        return AllowUser;
    }
    public void setAllowUser(int AllowUser) {
        this.AllowUser = AllowUser;
    }

    public int getAutoLogIN() {
        return AutoLogIN;
    }
    public void setAutoLogIN(int AutoLogIN) {
        this.AutoLogIN = AutoLogIN;
    }

    public String getDeviceID() {
        return DeviceID;
    }
    public void setDeviceID(String DeviceID) {
        this.DeviceID = DeviceID;
    }

    public String getDefaultUser() {
        return DefaultUser;
    }
    public void setDefaultUser(String DefaultUser) {
        this.DefaultUser = DefaultUser;
    }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getAPIUrl() {
        return APIUrl;
    }
    public void setAPIUrl(String APIUrl) {
        this.APIUrl = APIUrl;
    }

    public String getAppDownloadLink() {
        return AppDownloadLink;
    }
    public void setAppDownloadLink(String AppDownloadLink) {
        this.AppDownloadLink = AppDownloadLink;
    }

    public String getMsg() {
        return Msg;
    }
    public void setMsg(String Msg) {
        this.Msg = Msg;
    }
}
