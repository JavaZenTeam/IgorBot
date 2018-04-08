package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    public String toString() {
        return "Subscription{" +
                "subscriptionPK=" + subscriptionPK +
                ", userId=" + userId +
                ", trigger='" + trigger + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
