package whatsapp.autoresponder.Model;

import java.io.Serializable;

/**
 * Created by iqor on 1/24/2018.
 */

public class SchedulerModel implements Serializable {
    String phnNumber, conversationName, text, time, isRelevant, isFromGroup, fileUrl, fileName;
    String profileImage;
    String messageType;
    boolean forServer;
    String messageId,activityType,deviceId;
    String image;
    String id;
    String updateTime;
    String flag;

    private static final long serialVersionUID = 1L;

    public String getPhnNumber() {
        return phnNumber;
    }

    public void setPhnNumber(String phnNumber) {
        this.phnNumber = phnNumber;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsRelevant() {
        return isRelevant;
    }

    public void setIsRelevant(String isRelevant) {
        this.isRelevant = isRelevant;
    }

    public String getIsFromGroup() {
        return isFromGroup;
    }

    public void setIsFromGroup(String isFromGroup) {
        this.isFromGroup = isFromGroup;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isForServer() {
        return forServer;
    }

    public void setForServer(boolean forServer) {
        this.forServer = forServer;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
