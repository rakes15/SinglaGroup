package com.singlagroup.datasets;

import java.io.Serializable;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ModulePermissionDetailsDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String UserID,UserName;
    int VType,IsModule,ViewFlag,EditFlag,CreateFlag,RemoveFlag,ImportFlag,ExportFlag,PrintFlag;

    public ModulePermissionDetailsDataset(String UserID, String UserName, int VType,int IsModule,int ViewFlag,int EditFlag,int CreateFlag,int RemoveFlag,int ImportFlag,int ExportFlag,int PrintFlag) {

        this.UserID = UserID;
        this.UserName = UserName;
        this.VType = VType;
        this.IsModule = IsModule;
        this.ViewFlag = ViewFlag;
        this.EditFlag = EditFlag;
        this.CreateFlag = CreateFlag;
        this.RemoveFlag = RemoveFlag;
        this.ImportFlag = ImportFlag;
        this.ExportFlag = ExportFlag;
        this.PrintFlag = PrintFlag;
    }
    public String getUserID() {
        return UserID;
    }
    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public int getVType() {
        return VType;
    }
    public void setVType(int VType) {
        this.VType = VType;
    }

    public int getIsModule() {
        return IsModule;
    }
    public void setIsModule(int IsModule) {
        this.IsModule = IsModule;
    }

    public int getViewFlag() {
        return ViewFlag;
    }
    public void setViewFlag(int ViewFlag) {
        this.ViewFlag = ViewFlag;
    }

    public int getEditFlag() {
        return EditFlag;
    }
    public void setEditFlag(int EditFlag) {
        this.EditFlag = EditFlag;
    }

    public int getCreateFlag() {
        return CreateFlag;
    }
    public void setCreateFlag(int CreateFlag) {
        this.CreateFlag = CreateFlag;
    }

    public int getRemoveFlag() {
        return RemoveFlag;
    }
    public void setRemoveFlag(int RemoveFlag) {
        this.RemoveFlag = RemoveFlag;
    }

    public int getImportFlag() {
        return ImportFlag;
    }
    public void setImportFlag(int ImportFlag) {
        this.ImportFlag = ImportFlag;
    }

    public int getExportFlag() {
        return ExportFlag;
    }
    public void setExportFlag(int ExportFlag) {
        this.ExportFlag = ExportFlag;
    }

    public int getPrintFlag() {
        return PrintFlag;
    }
    public void setPrintFlag(int PrintFlag) {
        this.PrintFlag = PrintFlag;
    }


}
