package whatsapp.autoresponder.Extractor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceivedWhatsAppMessage {
    private String converName;
    private MessageType messageType = MessageType.TEXT;
    private byte[] profileImage;
    private String text;
    private long time;
    private boolean isRelevant;
    private String number;
    private boolean isFromGroup;
    String messageId,activityType;

    public enum MessageType {
        TEXT,
        AUDIO,
        IMAGE
    }

    ReceivedWhatsAppMessage(String converName, String number, boolean isFromGroup, String text, long time, byte[] profileImage, boolean isRelevant,String messageId,String activityType) {
        this.converName = converName;
        this.number = number;
        this.isFromGroup = isFromGroup;
        this.text = text;
        this.time = time;
        this.profileImage = profileImage;
        this.isRelevant = isRelevant;
        this.messageId = messageId;
        this.activityType = activityType;
    }

    public String getConverName() {
        return this.converName;
    }

    public void setConverName(String converName) {
        this.converName = converName;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isfromGroup() {
        return this.isFromGroup;
    }

    public void setFromGroup(boolean fromGroup) {
        this.isFromGroup = fromGroup;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getProfileImage() {
        return this.profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public MessageType getType() {
        return this.messageType;
    }

    public void setType(MessageType type) {
        this.messageType = type;
    }

    public String toString() {
        return new SimpleDateFormat("(dd-MM-yyyy HH:mm:ss) ").format(new Date(this.time)) + this.converName + ":" + this.text;
    }

    public boolean isRelevant() {
        return this.isRelevant;
    }

    public void setRelevant(boolean relevant) {
        this.isRelevant = relevant;
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
}
