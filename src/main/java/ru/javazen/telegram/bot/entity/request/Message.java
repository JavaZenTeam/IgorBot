package ru.javazen.telegram.bot.entity.request;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    @JsonProperty("message_id")
    private long messageId;

    @JsonProperty("from")
    private User from;

    @JsonProperty("date")
    private long date;

    @JsonProperty("chat")
    private Chat chat;

    @JsonProperty("text")
    private String text;

    @JsonProperty("reply_to_message")
    private Message replyMessage;

    @JsonProperty("pinned_message")
    private Message pinnedMessage;

    @JsonProperty("sticker")
    private Sticker sticker;

    @JsonProperty("caption")
    private String caption;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(Message replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public void setSticker(Sticker sticker) {
        this.sticker = sticker;
    }

    public Message getPinnedMessage() {
        return pinnedMessage;
    }

    public void setPinnedMessage(Message pinnedMessage) {
        this.pinnedMessage = pinnedMessage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", from=" + from +
                ", date=" + date +
                ", chat=" + chat +
                ", text='" + text + '\'' +
                ", replyMessage=" + replyMessage +
                ", pinnedMessage=" + pinnedMessage +
                ", sticker=" + sticker +
                ", caption=" + caption +
                '}';
    }


}