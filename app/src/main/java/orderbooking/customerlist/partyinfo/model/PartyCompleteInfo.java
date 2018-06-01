package orderbooking.customerlist.partyinfo.model;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class PartyCompleteInfo {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("msg")
@Expose
private String msg;
@SerializedName("Result")
@Expose
private Result result;

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public String getMsg() {
return msg;
}

public void setMsg(String msg) {
this.msg = msg;
}

public Result getResult() {
return result;
}

public void setResult(Result result) {
this.result = result;
}

    public class Result implements Serializable {

        @SerializedName("BasicInfo")
        @Expose
        private BasicInfo basicInfo;
        @SerializedName("AccountInfo")
        @Expose
        private List<AccountInfo> accountInfo = null;
        @SerializedName("SalesInfo")
        @Expose
        private SalesInfo salesInfo;
        @SerializedName("PendingOrderInfo")
        @Expose
        private List<PendingOrderInfo> pendingOrderInfo = null;
        @SerializedName("AppUseInfo")
        @Expose
        private List<Object> appUseInfo = null;
        @SerializedName("PartyAttechment")
        @Expose
        private List<PartyAttechment> partyAttechment = null;

        public BasicInfo getBasicInfo() {
            return basicInfo;
        }

        public void setBasicInfo(BasicInfo basicInfo) {
            this.basicInfo = basicInfo;
        }

        public List<AccountInfo> getAccountInfo() {
            return accountInfo;
        }

        public void setAccountInfo(List<AccountInfo> accountInfo) {
            this.accountInfo = accountInfo;
        }

        public SalesInfo getSalesInfo() {
            return salesInfo;
        }

        public void setSalesInfo(SalesInfo salesInfo) {
            this.salesInfo = salesInfo;
        }

        public List<PendingOrderInfo> getPendingOrderInfo() {
            return pendingOrderInfo;
        }

        public void setPendingOrderInfo(List<PendingOrderInfo> pendingOrderInfo) {
            this.pendingOrderInfo = pendingOrderInfo;
        }

        public List<Object> getAppUseInfo() {
            return appUseInfo;
        }

        public void setAppUseInfo(List<Object> appUseInfo) {
            this.appUseInfo = appUseInfo;
        }

        public List<PartyAttechment> getPartyAttechment() {
            return partyAttechment;
        }

        public void setPartyAttechment(List<PartyAttechment> partyAttechment) {
            this.partyAttechment = partyAttechment;
        }
    }
        public class BasicInfo implements Serializable{

        @SerializedName("PartyInfo")
        @Expose
        private List<PartyInfo> partyInfo = null;
        @SerializedName("ContactInfo")
        @Expose
        private List<ContactInfo> contactInfo = null;

        public List<PartyInfo> getPartyInfo() {
            return partyInfo;
        }

        public void setPartyInfo(List<PartyInfo> partyInfo) {
            this.partyInfo = partyInfo;
        }

        public List<ContactInfo> getContactInfo() {
            return contactInfo;
        }

        public void setContactInfo(List<ContactInfo> contactInfo) {
            this.contactInfo = contactInfo;
        }

    }
            public class PartyInfo implements Serializable{
        @SerializedName("GSTIN")
        @Expose
        private String gSTIN;
        @SerializedName("IDName")
        @Expose
        private String iDName;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("B2BDeviceCount")
        @Expose
        private Integer b2BDeviceCount;
        @SerializedName("B2BDeviceRejectCount")
        @Expose
        private Integer b2BDeviceRejectCount;
        @SerializedName("B2BDeviceCountPending")
        @Expose
        private Integer b2BDeviceCountPending;
        @SerializedName("B2BDeviceStatus")
        @Expose
        private Integer b2BDeviceStatus;
        @SerializedName("WorkingSince")
        @Expose
        private String workingSince;
        @SerializedName("PartyID")
        @Expose
        private String partyID;
        @SerializedName("PartyName")
        @Expose
        private String partyName;
        @SerializedName("agent")
        @Expose
        private String agent;
        @SerializedName("TypeName")
        @Expose
        private String typeName;
        @SerializedName("CellNo")
        @Expose
        private String cellNo;
        @SerializedName("PhoneNo")
        @Expose
        private String phoneNo;
        @SerializedName("Address1")
        @Expose
        private String address1;
        @SerializedName("Address2")
        @Expose
        private String address2;
        @SerializedName("Address3")
        @Expose
        private String address3;
        @SerializedName("City")
        @Expose
        private String city;
        @SerializedName("State")
        @Expose
        private String state;
        @SerializedName("Country")
        @Expose
        private String country;
        @SerializedName("Pincode")
        @Expose
        private String pincode;
        @SerializedName("SubPartyName")
        @Expose
        private String subPartyName;
        @SerializedName("SubPartyID")
        @Expose
        private String subPartyID;
        @SerializedName("SubPartyCellNo")
        @Expose
        private String subPartyCellNo;
        @SerializedName("SubPartyPhoneNo")
        @Expose
        private String subPartyPhoneNo;
        @SerializedName("SubPartyApplicable")
        @Expose
        private Integer subPartyApplicable;
        @SerializedName("Remark")
        @Expose
        private String remark;
        public String getGSTIN() {
            return gSTIN;
        }

        public void setGSTIN(String gSTIN) {
            this.gSTIN = gSTIN;
        }

        public String getIDName() {
            return iDName;
        }

        public void setIDName(String iDName) {
            this.iDName = iDName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getB2BDeviceCount() {
            return b2BDeviceCount;
        }

        public void setB2BDeviceCount(Integer b2BDeviceCount) {
            this.b2BDeviceCount = b2BDeviceCount;
        }

        public Integer getB2BDeviceRejectCount() {
            return b2BDeviceRejectCount;
        }

        public void setB2BDeviceRejectCount(Integer b2BDeviceRejectCount) {
            this.b2BDeviceRejectCount = b2BDeviceRejectCount;
        }

        public Integer getB2BDeviceCountPending() {
            return b2BDeviceCountPending;
        }

        public void setB2BDeviceCountPending(Integer b2BDeviceCountPending) {
            this.b2BDeviceCountPending = b2BDeviceCountPending;
        }

        public Integer getB2BDeviceStatus() {
            return b2BDeviceStatus;
        }

        public void setB2BDeviceStatus(Integer b2BDeviceStatus) {
            this.b2BDeviceStatus = b2BDeviceStatus;
        }

        public String getWorkingSince() {
            return workingSince;
        }

        public void setWorkingSince(String workingSince) {
            this.workingSince = workingSince;
        }

        public String getPartyID() {
            return partyID;
        }

        public void setPartyID(String partyID) {
            this.partyID = partyID;
        }

        public String getPartyName() {
            return partyName;
        }

        public void setPartyName(String partyName) {
            this.partyName = partyName;
        }

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getCellNo() {
            return cellNo;
        }

        public void setCellNo(String cellNo) {
            this.cellNo = cellNo;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3) {
            this.address3 = address3;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public Integer getSubPartyApplicable() {
            return subPartyApplicable;
        }

        public void setSubPartyApplicable(Integer subPartyApplicable) {
            this.subPartyApplicable = subPartyApplicable;
        }
        public String getSubPartyName() {
            return subPartyName;
        }

        public void setSubPartyName(String subPartyName) {
            this.subPartyName = subPartyName;
        }

        public String getSubPartyID() {
            return subPartyID;
        }
        public void setSubPartyID(String subPartyID) {
            this.subPartyID = subPartyID;
        }

        public String getSubPartyCellNo() {
            return subPartyCellNo;
        }
        public void setSubPartyCellNo(String subPartyCellNo) {
            this.subPartyCellNo = subPartyCellNo;
        }

        public String getSubPartyPhoneNo() {
            return subPartyPhoneNo;
        }
        public void setSubPartyPhoneNo(String subPartyPhoneNo) {
            this.subPartyPhoneNo = subPartyPhoneNo;
        }

        public String getRemark() {
            return remark;
        }
        public void setRemark(String remark) {
            this.remark = remark;
        }

    }
            public class ContactInfo implements Serializable{

                @SerializedName("ID")
                @Expose
                private String iD;
                @SerializedName("Name")
                @Expose
                private String name;
                @SerializedName("Designation")
                @Expose
                private String designation;
                @SerializedName("Address1")
                @Expose
                private String address1;
                @SerializedName("Address2")
                @Expose
                private String address2;
                @SerializedName("Address3")
                @Expose
                private String address3;
                @SerializedName("CellNo")
                @Expose
                private String cellNo;
                @SerializedName("PhoneNo")
                @Expose
                private String phoneNo;
                @SerializedName("Email")
                @Expose
                private String email;
                @SerializedName("IsDefault")
                @Expose
                private Integer isDefault;

                public String getID() {
                    return iD;
                }

                public void setID(String iD) {
                    this.iD = iD;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getDesignation() {
                    return designation;
                }

                public void setDesignation(String designation) {
                    this.designation = designation;
                }

                public String getAddress1() {
                    return address1;
                }

                public void setAddress1(String address1) {
                    this.address1 = address1;
                }

                public String getAddress2() {
                    return address2;
                }

                public void setAddress2(String address2) {
                    this.address2 = address2;
                }

                public String getAddress3() {
                    return address3;
                }

                public void setAddress3(String address3) {
                    this.address3 = address3;
                }

                public String getCellNo() {
                    return cellNo;
                }

                public void setCellNo(String cellNo) {
                    this.cellNo = cellNo;
                }

                public String getPhoneNo() {
                    return phoneNo;
                }

                public void setPhoneNo(String phoneNo) {
                    this.phoneNo = phoneNo;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public Integer getIsDefault() {
                    return isDefault;
                }

                public void setIsDefault(Integer isDefault) {
                    this.isDefault = isDefault;
                }

            }
        public class AccountInfo implements Serializable{

        @SerializedName("PartyID")
        @Expose
        private String partyID;
        @SerializedName("PartyName")
        @Expose
        private String partyName;
        @SerializedName("CreditLimit")
        @Expose
        private Integer creditLimit;
        @SerializedName("CreditDays")
        @Expose
        private Integer creditDays;
        @SerializedName("DiscountPolicy")
        @Expose
        private String discountPolicy;
        @SerializedName("OverDueAmount")
        @Expose
        private Integer overDueAmount;
        @SerializedName("AvgOverDueDays")
        @Expose
        private Integer avgOverDueDays;
        @SerializedName("DueAmount")
        @Expose
        private Integer dueAmount;
        @SerializedName("AvgDueDays")
        @Expose
        private Integer avgDueDays;
        @SerializedName("FncrAvgDays0")
        @Expose
        private Integer fncrAvgDays0;
        @SerializedName("FncrAvgDays1")
        @Expose
        private Integer fncrAvgDays1;
        @SerializedName("FncrAvgDays2")
        @Expose
        private Integer fncrAvgDays2;
        @SerializedName("DiscountFnYr0")
        @Expose
        private Float discountFnYr0;
        @SerializedName("DiscountFnYr1")
        @Expose
        private Float discountFnYr1;
        @SerializedName("DiscountFnYr2")
        @Expose
        private Float discountFnYr2;

        public String getPartyID() {
            return partyID;
        }

        public void setPartyID(String partyID) {
            this.partyID = partyID;
        }

        public String getPartyName() {
            return partyName;
        }

        public void setPartyName(String partyName) {
            this.partyName = partyName;
        }

        public Integer getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(Integer creditLimit) {
            this.creditLimit = creditLimit;
        }

        public Integer getCreditDays() {
            return creditDays;
        }

        public void setCreditDays(Integer creditDays) {
            this.creditDays = creditDays;
        }

        public String getDiscountPolicy() {
            return discountPolicy;
        }

        public void setDiscountPolicy(String discountPolicy) {
            this.discountPolicy = discountPolicy;
        }

        public Integer getOverDueAmount() {
            return overDueAmount;
        }

        public void setOverDueAmount(Integer overDueAmount) {
            this.overDueAmount = overDueAmount;
        }

        public Integer getAvgOverDueDays() {
            return avgOverDueDays;
        }

        public void setAvgOverDueDays(Integer avgOverDueDays) {
            this.avgOverDueDays = avgOverDueDays;
        }

        public Integer getDueAmount() {
            return dueAmount;
        }

        public void setDueAmount(Integer dueAmount) {
            this.dueAmount = dueAmount;
        }

        public Integer getAvgDueDays() {
            return avgDueDays;
        }

        public void setAvgDueDays(Integer avgDueDays) {
            this.avgDueDays = avgDueDays;
        }

        public Integer getFncrAvgDays0() {
            return fncrAvgDays0;
        }

        public void setFncrAvgDays0(Integer fncrAvgDays0) {
            this.fncrAvgDays0 = fncrAvgDays0;
        }

        public Integer getFncrAvgDays1() {
            return fncrAvgDays1;
        }

        public void setFncrAvgDays1(Integer fncrAvgDays1) {
            this.fncrAvgDays1 = fncrAvgDays1;
        }

        public Integer getFncrAvgDays2() {
            return fncrAvgDays2;
        }

        public void setFncrAvgDays2(Integer fncrAvgDays2) {
            this.fncrAvgDays2 = fncrAvgDays2;
        }

        public Float getDiscountFnYr0() {
            return discountFnYr0;
        }

        public void setDiscountFnYr0(Float discountFnYr0) {
            this.discountFnYr0 = discountFnYr0;
        }

        public Float getDiscountFnYr1() {
            return discountFnYr1;
        }

        public void setDiscountFnYr1(Float discountFnYr1) {
            this.discountFnYr1 = discountFnYr1;
        }

        public Float getDiscountFnYr2() {
            return discountFnYr2;
        }

        public void setDiscountFnYr2(Float discountFnYr2) {
            this.discountFnYr2 = discountFnYr2;
        }
    }
        public class SalesInfo implements Serializable{

        @SerializedName("Summary")
        @Expose
        private List<Summary> summary = null;
        @SerializedName("GroupWise")
        @Expose
        private List<GroupWise> groupWise = null;
        @SerializedName("PartyMonthWiseSales")
        @Expose
        private List<PartyMonthWiseSale> partyMonthWiseSales = null;
        @SerializedName("ShowroomWiseSales")
        @Expose
        private List<ShowroomWiseSale> showroomWiseSales = null;
        @SerializedName("ApplicationWiseSales")
        @Expose
        private List<ApplicationWiseSale> applicationWiseSales = null;
        @SerializedName("FairDeliveryParcent")
        @Expose
        private List<FairDeliveryParcent> fairDeliveryParcent = null;

        public List<Summary> getSummary() {
            return summary;
        }

        public void setSummary(List<Summary> summary) {
            this.summary = summary;
        }

        public List<GroupWise> getGroupWise() {
            return groupWise;
        }

        public void setGroupWise(List<GroupWise> groupWise) {
            this.groupWise = groupWise;
        }

        public List<PartyMonthWiseSale> getPartyMonthWiseSales() {
            return partyMonthWiseSales;
        }

        public void setPartyMonthWiseSales(List<PartyMonthWiseSale> partyMonthWiseSales) {
            this.partyMonthWiseSales = partyMonthWiseSales;
        }

        public List<ShowroomWiseSale> getShowroomWiseSales() {
            return showroomWiseSales;
        }

        public void setShowroomWiseSales(List<ShowroomWiseSale> showroomWiseSales) {
            this.showroomWiseSales = showroomWiseSales;
        }

        public List<ApplicationWiseSale> getApplicationWiseSales() {
            return applicationWiseSales;
        }

        public void setApplicationWiseSales(List<ApplicationWiseSale> applicationWiseSales) {
            this.applicationWiseSales = applicationWiseSales;
        }

        public List<FairDeliveryParcent> getFairDeliveryParcent() {
            return fairDeliveryParcent;
        }

        public void setFairDeliveryParcent(List<FairDeliveryParcent> fairDeliveryParcent) {
            this.fairDeliveryParcent = fairDeliveryParcent;
        }

    }
            public class Summary implements Serializable {

                @SerializedName("SalesShare0")
                @Expose
                private Double salesShare0;
                @SerializedName("PartyAvgInv0")
                @Expose
                private Integer partyAvgInv0;
                @SerializedName("NoOfInv0")
                @Expose
                private Integer noOfInv0;
                @SerializedName("SaleReturn0")
                @Expose
                private Double saleReturn0;
                @SerializedName("SalesShare1")
                @Expose
                private Double salesShare1;
                @SerializedName("PartyAvgInv1")
                @Expose
                private Integer partyAvgInv1;
                @SerializedName("NoOfInv1")
                @Expose
                private Integer noOfInv1;
                @SerializedName("SaleReturn1")
                @Expose
                private Double saleReturn1;
                @SerializedName("SalesShare2")
                @Expose
                private Double salesShare2;
                @SerializedName("PartyAvgInv2")
                @Expose
                private Integer partyAvgInv2;
                @SerializedName("NoOfInv2")
                @Expose
                private Integer noOfInv2;
                @SerializedName("SaleReturn2")
                @Expose
                private Double saleReturn2;
                @SerializedName("PartySalesYearsWise0")
                @Expose
                private Double partySalesYearsWise0;
                @SerializedName("PartySalesYearsWise1")
                @Expose
                private Double partySalesYearsWise1;
                @SerializedName("PartySalesYearsWise2")
                @Expose
                private Double partySalesYearsWise2;

                public Double getSalesShare0() {
                    return salesShare0;
                }

                public void setSalesShare0(Double salesShare0) {
                    this.salesShare0 = salesShare0;
                }

                public Integer getPartyAvgInv0() {
                    return partyAvgInv0;
                }

                public void setPartyAvgInv0(Integer partyAvgInv0) {
                    this.partyAvgInv0 = partyAvgInv0;
                }

                public Integer getNoOfInv0() {
                    return noOfInv0;
                }

                public void setNoOfInv0(Integer noOfInv0) {
                    this.noOfInv0 = noOfInv0;
                }

                public Double getSaleReturn0() {
                    return saleReturn0;
                }

                public void setSaleReturn0(Double saleReturn0) {
                    this.saleReturn0 = saleReturn0;
                }

                public Double getSalesShare1() {
                    return salesShare1;
                }

                public void setSalesShare1(Double salesShare1) {
                    this.salesShare1 = salesShare1;
                }

                public Integer getPartyAvgInv1() {
                    return partyAvgInv1;
                }

                public void setPartyAvgInv1(Integer partyAvgInv1) {
                    this.partyAvgInv1 = partyAvgInv1;
                }

                public Integer getNoOfInv1() {
                    return noOfInv1;
                }

                public void setNoOfInv1(Integer noOfInv1) {
                    this.noOfInv1 = noOfInv1;
                }

                public Double getSaleReturn1() {
                    return saleReturn1;
                }

                public void setSaleReturn1(Double saleReturn1) {
                    this.saleReturn1 = saleReturn1;
                }

                public Double getSalesShare2() {
                    return salesShare2;
                }

                public void setSalesShare2(Double salesShare2) {
                    this.salesShare2 = salesShare2;
                }

                public Integer getPartyAvgInv2() {
                    return partyAvgInv2;
                }

                public void setPartyAvgInv2(Integer partyAvgInv2) {
                    this.partyAvgInv2 = partyAvgInv2;
                }

                public Integer getNoOfInv2() {
                    return noOfInv2;
                }

                public void setNoOfInv2(Integer noOfInv2) {
                    this.noOfInv2 = noOfInv2;
                }

                public Double getSaleReturn2() {
                    return saleReturn2;
                }

                public void setSaleReturn2(Double saleReturn2) {
                    this.saleReturn2 = saleReturn2;
                }

                public Double getPartySalesYearsWise0() {
                    return partySalesYearsWise0;
                }

                public void setPartySalesYearsWise0(Double partySalesYearsWise0) {
                    this.partySalesYearsWise0 = partySalesYearsWise0;
                }

                public Double getPartySalesYearsWise1() {
                    return partySalesYearsWise1;
                }

                public void setPartySalesYearsWise1(Double partySalesYearsWise1) {
                    this.partySalesYearsWise1 = partySalesYearsWise1;
                }

                public Double getPartySalesYearsWise2() {
                    return partySalesYearsWise2;
                }

                public void setPartySalesYearsWise2(Double partySalesYearsWise2) {
                    this.partySalesYearsWise2 = partySalesYearsWise2;
                }

            }
            public class ApplicationWiseSale implements Serializable {

                @SerializedName("YearSales")
                @Expose
                private Integer yearSales;
                @SerializedName("MonthNm")
                @Expose
                private String monthNm;
                @SerializedName("SalesMonth")
                @Expose
                private Integer salesMonth;
                @SerializedName("SalesQty")
                @Expose
                private Integer salesQty;
                @SerializedName("SalesAmount")
                @Expose
                private Integer salesAmount;
                @SerializedName("DivisionID")
                @Expose
                private String divisionID;
                @SerializedName("AppType")
                @Expose
                private Integer appType;
                @SerializedName("ApplicationType")
                @Expose
                private String applicationType;

                public Integer getYearSales() {
                    return yearSales;
                }

                public void setYearSales(Integer yearSales) {
                    this.yearSales = yearSales;
                }

                public String getMonthNm() {
                    return monthNm;
                }

                public void setMonthNm(String monthNm) {
                    this.monthNm = monthNm;
                }

                public Integer getSalesMonth() {
                    return salesMonth;
                }

                public void setSalesMonth(Integer salesMonth) {
                    this.salesMonth = salesMonth;
                }

                public Integer getSalesQty() {
                    return salesQty;
                }

                public void setSalesQty(Integer salesQty) {
                    this.salesQty = salesQty;
                }

                public Integer getSalesAmount() {
                    return salesAmount;
                }

                public void setSalesAmount(Integer salesAmount) {
                    this.salesAmount = salesAmount;
                }

                public String getDivisionID() {
                    return divisionID;
                }

                public void setDivisionID(String divisionID) {
                    this.divisionID = divisionID;
                }

                public Integer getAppType() {
                    return appType;
                }

                public void setAppType(Integer appType) {
                    this.appType = appType;
                }

                public String getApplicationType() {
                    return applicationType;
                }

                public void setApplicationType(String applicationType) {
                    this.applicationType = applicationType;
                }

            }
            public class FairDeliveryParcent implements Serializable {

                @SerializedName("YearSales")
                @Expose
                private Integer yearSales;
                @SerializedName("FairIDDeliveryPercent")
                @Expose
                private Double fairIDDeliveryPercent;

                public Integer getYearSales() {
                    return yearSales;
                }

                public void setYearSales(Integer yearSales) {
                    this.yearSales = yearSales;
                }

                public Double getFairIDDeliveryPercent() {
                    return fairIDDeliveryPercent;
                }

                public void setFairIDDeliveryPercent(Double fairIDDeliveryPercent) {
                    this.fairIDDeliveryPercent = fairIDDeliveryPercent;
                }

            }
            public class GroupWise implements Serializable{

            @SerializedName("GroupName")
            @Expose
            private String groupName;
            @SerializedName("GroupID")
            @Expose
            private String groupID;
            @SerializedName("SaleAmtGroupWise0")
            @Expose
            private Double saleAmtGroupWise0;
            @SerializedName("SaleQtyGroupWise0")
            @Expose
            private Double saleQtyGroupWise0;
            @SerializedName("SaleAmtGroupWise1")
            @Expose
            private Double saleAmtGroupWise1;
            @SerializedName("SaleQtyGroupWise1")
            @Expose
            private Double saleQtyGroupWise1;
            @SerializedName("SaleAmtGroupWise2")
            @Expose
            private Double saleAmtGroupWise2;
            @SerializedName("SaleQtyGroupWise2")
            @Expose
            private Double saleQtyGroupWise2;

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public String getGroupID() {
                return groupID;
            }

            public void setGroupID(String groupID) {
                this.groupID = groupID;
            }

            public Double getSaleAmtGroupWise0() {
                return saleAmtGroupWise0;
            }

            public void setSaleAmtGroupWise0(Double saleAmtGroupWise0) {
                this.saleAmtGroupWise0 = saleAmtGroupWise0;
            }

            public Double getSaleQtyGroupWise0() {
                return saleQtyGroupWise0;
            }

            public void setSaleQtyGroupWise0(Double saleQtyGroupWise0) {
                this.saleQtyGroupWise0 = saleQtyGroupWise0;
            }

            public Double getSaleAmtGroupWise1() {
                return saleAmtGroupWise1;
            }

            public void setSaleAmtGroupWise1(Double saleAmtGroupWise1) {
                this.saleAmtGroupWise1 = saleAmtGroupWise1;
            }

            public Double getSaleQtyGroupWise1() {
                return saleQtyGroupWise1;
            }

            public void setSaleQtyGroupWise1(Double saleQtyGroupWise1) {
                this.saleQtyGroupWise1 = saleQtyGroupWise1;
            }

            public Double getSaleAmtGroupWise2() {
                return saleAmtGroupWise2;
            }

            public void setSaleAmtGroupWise2(Double saleAmtGroupWise2) {
                this.saleAmtGroupWise2 = saleAmtGroupWise2;
            }

            public Double getSaleQtyGroupWise2() {
                return saleQtyGroupWise2;
            }

            public void setSaleQtyGroupWise2(Double saleQtyGroupWise2) {
                this.saleQtyGroupWise2 = saleQtyGroupWise2;
            }

    }
            public class PartyMonthWiseSale implements Serializable{

                @SerializedName("MonthNm")
                @Expose
                private String monthNm;
                @SerializedName("monthSales")
                @Expose
                private Integer monthSales;
                @SerializedName("YearSeles")
                @Expose
                private Integer yearSeles;
                @SerializedName("PartySalesAmt0")
                @Expose
                private Integer partySalesAmt0;
                @SerializedName("PartySalesQty0")
                @Expose
                private Integer partySalesQty0;

                public String getMonthNm() {
                    return monthNm;
                }

                public void setMonthNm(String monthNm) {
                    this.monthNm = monthNm;
                }

                public Integer getMonthSales() {
                    return monthSales;
                }

                public void setMonthSales(Integer monthSales) {
                    this.monthSales = monthSales;
                }

                public Integer getYearSeles() {
                    return yearSeles;
                }

                public void setYearSeles(Integer yearSeles) {
                    this.yearSeles = yearSeles;
                }

                public Integer getPartySalesAmt0() {
                    return partySalesAmt0;
                }

                public void setPartySalesAmt0(Integer partySalesAmt0) {
                    this.partySalesAmt0 = partySalesAmt0;
                }

                public Integer getPartySalesQty0() {
                    return partySalesQty0;
                }

                public void setPartySalesQty0(Integer partySalesQty0) {
                    this.partySalesQty0 = partySalesQty0;
                }

            }
            public class ShowroomWiseSale implements Serializable {

                @SerializedName("YearSales")
                @Expose
                private Integer yearSales;
                @SerializedName("MonthNm")
                @Expose
                private String monthNm;
                @SerializedName("SalesMonth")
                @Expose
                private Integer salesMonth;
                @SerializedName("SalesQty")
                @Expose
                private Integer salesQty;
                @SerializedName("SalesAmount")
                @Expose
                private Integer salesAmount;
                @SerializedName("SG_ShowroomID")
                @Expose
                private String sGShowroomID;
                @SerializedName("Showroom")
                @Expose
                private String showroom;

                public Integer getYearSales() {
                    return yearSales;
                }

                public void setYearSales(Integer yearSales) {
                    this.yearSales = yearSales;
                }

                public String getMonthNm() {
                    return monthNm;
                }

                public void setMonthNm(String monthNm) {
                    this.monthNm = monthNm;
                }

                public Integer getSalesMonth() {
                    return salesMonth;
                }

                public void setSalesMonth(Integer salesMonth) {
                    this.salesMonth = salesMonth;
                }

                public Integer getSalesQty() {
                    return salesQty;
                }

                public void setSalesQty(Integer salesQty) {
                    this.salesQty = salesQty;
                }

                public Integer getSalesAmount() {
                    return salesAmount;
                }

                public void setSalesAmount(Integer salesAmount) {
                    this.salesAmount = salesAmount;
                }

                public String getSGShowroomID() {
                    return sGShowroomID;
                }

                public void setSGShowroomID(String sGShowroomID) {
                    this.sGShowroomID = sGShowroomID;
                }

                public String getShowroom() {
                    return showroom;
                }

                public void setShowroom(String showroom) {
                    this.showroom = showroom;
                }

            }
        public class PendingOrderInfo implements Serializable {

        @SerializedName("CombinedVno")
        @Expose
        private String combinedVno;
        @SerializedName("VDate")
        @Expose
        private String vDate;
        @SerializedName("Showroom")
        @Expose
        private String showroom;
        @SerializedName("OrderQty")
        @Expose
        private Integer orderQty;
        @SerializedName("PendingQty")
        @Expose
        private Integer pendingQty;
        @SerializedName("PendingPercent")
        @Expose
        private Double pendingPercent;

        public String getCombinedVno() {
            return combinedVno;
        }

        public void setCombinedVno(String combinedVno) {
            this.combinedVno = combinedVno;
        }

        public String getVDate() {
            return vDate;
        }

        public void setVDate(String vDate) {
            this.vDate = vDate;
        }

        public String getShowroom() {
            return showroom;
        }

        public void setShowroom(String showroom) {
            this.showroom = showroom;
        }

        public Integer getOrderQty() {
            return orderQty;
        }

        public void setOrderQty(Integer orderQty) {
            this.orderQty = orderQty;
        }

        public Integer getPendingQty() {
            return pendingQty;
        }

        public void setPendingQty(Integer pendingQty) {
            this.pendingQty = pendingQty;
        }

        public Double getPendingPercent() {
            return pendingPercent;
        }

        public void setPendingPercent(Double pendingPercent) {
            this.pendingPercent = pendingPercent;
        }

    }
        public class PartyAttechment implements Serializable {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("FileName")
        @Expose
        private String fileName;
        @SerializedName("Description")
        @Expose
        private String description;
        @SerializedName("Filepath")
        @Expose
        private String filepath;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

    }
}
