package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 10-March-17.
 */
public class RecommandedAppsDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String ID,Name,URL,PackageName,Icon;
    int IsMandatory,AppType;

    public RecommandedAppsDataset(String ID,String Name, String URL, String PackageName, int IsMandatory,int AppType,String Icon) {

        this.ID = ID;
        this.Name = Name;
        this.URL = URL;
        this.PackageName = PackageName;
        this.IsMandatory = IsMandatory;
        this.AppType = AppType;
        this.Icon = Icon;
    }
    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public String getURL() {
        return URL;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getPackageName() {
        return PackageName;
    }
    public void setPackageName(String PackageName) {
        this.PackageName = PackageName;
    }

    public int getIsMandatory() {
        return IsMandatory;
    }
    public void setIsMandatory(int IsMandatory) {
        this.IsMandatory = IsMandatory;
    }

    public int getAppType() {
        return AppType;
    }
    public void setAppType(int AppType) {
        this.AppType = AppType;
    }

    public String getIcon() {
        return Icon;
    }
    public void setIcon(String Icon) {
        this.Icon = Icon;
    }
}
