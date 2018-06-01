package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 10-March-17.
 */
public class DeviceInfoDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String MacID,SerialNo,IMEINo,ModelNo,Version,FCM_ID,AndroidDUID;

    public DeviceInfoDataset(String MacID,String SerialNo,String IMEINo,String ModelNo,String Version,String FCM_ID,String AndroidDUID) {

        this.MacID = MacID;
        this.SerialNo = SerialNo;
        this.IMEINo = IMEINo;
        this.ModelNo = ModelNo;
        this.Version = Version;
        this.FCM_ID = FCM_ID;
        this.AndroidDUID = AndroidDUID;
    }
    public String getMacID() {
        return MacID;
    }
    public void setMacID(String MacID) {
        this.MacID = MacID;
    }

    public String getSerialNo() {
        return SerialNo;
    }
    public void setSerialNo(String SerialNo) {
        this.SerialNo = SerialNo;
    }

    public String getIMEINo() {
        return IMEINo;
    }
    public void setIMEINo(String IMEINo) {
        this.IMEINo = IMEINo;
    }

    public String getModelNo() {
        return ModelNo;
    }
    public void setModelNo(String ModelNo) {
        this.ModelNo = ModelNo;
    }

    public String getVersion() {
        return Version;
    }
    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getFCM_ID() {
        return FCM_ID;
    }
    public void setFCM_ID(String FCM_ID) {
        this.FCM_ID = FCM_ID;
    }

    public String getAndroidDUID() {
        return AndroidDUID;
    }
    public void setAndroidDUID(String AndroidDUID) {
        this.AndroidDUID = AndroidDUID;
    }
}
