package ru.javazen.telegram.bot.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class MessageEntity {
    @EmbeddedId
    private MessagePK messagePK;

    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ChatEntity chat;

    @JoinColumn(name="user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity user;

    @Column(length = 4096)
    private String text;

    @Column
    private Date date;

    @Column(length = 4096)
    private String fileId;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @JoinColumn(name="forward_user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity forwardFrom;


    public MessagePK getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(MessagePK messagePK) {
        this.messagePK = messagePK;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public UserEntity getForwardFrom() {
        return forwardFrom;
    }

    public void setForwardFrom(UserEntity forwardFrom) {
        this.forwardFrom = forwardFrom;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "messagePK=" + messagePK +
                ", chat=" + chat +
                ", user=" + user +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", fileId='" + fileId + '\'' +
                ", fileType=" + fileType +
                ", forwardFrom=" + forwardFrom +
                '}';
    }
}
