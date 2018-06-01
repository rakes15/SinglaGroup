package orderbooking.customerlist.partyinfo.model;

public class PartyInfo {

        private Integer b2BDeviceCount;
        private Integer b2BDeviceStatus;
        private String workingSince;
        private String partyID;
        private String partyName;
        private String agent;
        private String typeName;
        private String cellNo;
        private String phoneNo;
        private String address1;
        private String address2;
        private String address3;
        private String city;
        private String state;
        private String country;
        private String pincode;
        private Integer subPartyApplicable;
        private String remark;
        
        public PartyInfo(Integer b2BDeviceCount,Integer b2BDeviceStatus,String workingSince,String partyID,String partyName,String agent,String typeName,String cellNo,String phoneNo,String address1,String address2,String address3,String city,String state,String country,String pincode,Integer subPartyApplicable,String remark){
            this.b2BDeviceCount = b2BDeviceCount;
            this.b2BDeviceStatus = b2BDeviceStatus;
            this.workingSince = workingSince;
            this.partyID = partyID;
            this.partyName = partyName;
            this.agent = agent;
            this.typeName = typeName;
            this.cellNo = cellNo;
            this.phoneNo = phoneNo;
            this.address1 = address1;
            this.address2 = address2;
            this.address3 = address3;
            this.city = city;
            this.state = state;
            this.country = country;
            this.pincode = pincode;
            this.subPartyApplicable = subPartyApplicable;
            this.remark = remark;
        }

        public Integer getB2BDeviceCount() {
            return b2BDeviceCount;
        }
        public void setB2BDeviceCount(Integer b2BDeviceCount) {
            this.b2BDeviceCount = b2BDeviceCount;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }


    }