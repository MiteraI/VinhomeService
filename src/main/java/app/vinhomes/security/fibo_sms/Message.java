package app.vinhomes.security.fibo_sms;

public class Message {
    private String senderName;
    private String phoneNumber;
    private String smsMessage;
    private String smsGUID;

    public Message(String senderName, String phoneNumber, String smsMessage, String smsGUID) {
        this.senderName = senderName;
        this.phoneNumber = phoneNumber;
        this.smsMessage = smsMessage;
        this.smsGUID = smsGUID;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the smsMessage
     */
    public String getSmsMessage() {
        return smsMessage;
    }

    /**
     * @param smsMessage the smsMessage to set
     */
    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    /**
     * @return the smsGUID
     */
    public String getSmsGUID() {
        return smsGUID;
    }

    /**
     * @param smsGUID the smsGUID to set
     */
    public void setSmsGUID(String smsGUID) {
        this.smsGUID = smsGUID;
    }
}
