package com.singlagroup.datasets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rakesh on 27-Feb-16.
 */
public class CompanyDataset implements Serializable {
    private static final long serialVersionUID = 1L;

    String ID,CompanyName,DBName,Alias;
    ArrayList<DivisionEmpDataset> DivisionInfo;
    public CompanyDataset(String ID, String CompanyName ,String DBName,String Alias) {
        this.ID = ID;
        this.CompanyName = CompanyName;
        this.DBName = DBName;
        this.Alias = Alias;
        this.DivisionInfo = DivisionInfo;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCompanyName() {
        return CompanyName;
    }
    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }

    public String getDBName() {
        return DBName;
    }
    public void setDBName(String DBName) {
        this.DBName = DBName;
    }

    public String getAlias() {
        return Alias;
    }
    public void setAlias(String Alias) {
        this.Alias = Alias;
    }

    public ArrayList<DivisionEmpDataset> getDivisionInfo() {
        return DivisionInfo;
    }
    public void setDivisionInfo(ArrayList<DivisionEmpDataset> DivisionInfo) {
        this.DivisionInfo = DivisionInfo;
    }

}
