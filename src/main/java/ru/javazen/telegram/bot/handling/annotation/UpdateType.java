package ru.javazen.telegram.bot.handling.annotation;

public enum UpdateType {
    MESSAGE,
    EDITED_MESSAGE,
    INLINE_QUERY,
    CALLBACK_QUERY,
    CHANNEL_POST,
    CHOSEN_INLINE_QUERY,
    EDITED_CHANNEL_POST,
    PRE_CHECKOUT_QUERY,
    SHIPPING_QUERY
}