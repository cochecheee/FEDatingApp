package vn.iotstar.dating_fe.entities;

import java.util.Date;

public class Message {

    private Long id;

    private Long fromUser;
    private Long toUser;
    private String messageContent;
    private Date sentAt;
    private boolean liked;

    public Message(Long id, Long fromUser, Long toUser, String messageContent, Date sentAt, boolean liked) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageContent = messageContent;
        this.sentAt = sentAt;
        this.liked = liked;
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUser() {
        return toUser;
    }

    public void setToUser(Long toUser) {
        this.toUser = toUser;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
