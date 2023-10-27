package com.sazer.commonClass;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String senderId; // 消息发送方ID
    private String receiverId; // 消息接收方ID

    private String content; // 消息内容, 当消息类型为MSG_SEND_FILE时该字段表示文件名称

    private MessageType msgType; // 消息类型，根据消息类型做不同处理

    // 以下属性仅在msgType为MSG_SEND_FILE时有效
    private byte[] fileBuffer; // 要传输的文件数据


    public Message() {
    }

    public Message(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }
    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getFileBuffer() {
        return fileBuffer;
    }

    public void setFileBuffer(byte[] fileBuffer) {
        this.fileBuffer = fileBuffer;
    }
}
