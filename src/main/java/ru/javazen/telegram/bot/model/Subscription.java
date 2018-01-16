package ru.javazen.telegram.bot.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
public class Subscription {
    @EmbeddedId
    private MessagePK subscriptionPK;

    @Column
    private Integer userId;

    @Column(nullable = false)
    private String trigger;

    @Column(nullable = false)
    private String response;

    public MessagePK getSubscriptionPK() {
        return subscriptionPK;
    }

    public void setSubscriptionPK(MessagePK subscriptionPK) {
        this.subscriptionPK = subscriptionPK;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return Objects.equals(subscriptionPK, that.subscriptionPK);
    }

    @Override
    public int hashCode() {
        return subscriptionPK != null ? subscriptionPK.hashCode() : 0;
    }
}
