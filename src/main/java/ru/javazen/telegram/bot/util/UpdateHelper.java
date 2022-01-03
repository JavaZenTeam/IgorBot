package ru.javazen.telegram.bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Optional;

@UtilityClass
public class UpdateHelper {
    public Optional<Chat> tryResolveChat(Update update) {
        return Optional.<Message>empty()
                .or(() -> Optional.ofNullable(update.getMessage()))
                .or(() -> Optional.ofNullable(update.getEditedMessage()))
                .or(() -> Optional.ofNullable(update.getChannelPost()))
                .or(() -> Optional.ofNullable(update.getEditedChannelPost()))
                .or(() -> Optional.ofNullable(update.getCallbackQuery()).map(CallbackQuery::getMessage))
                .map(Message::getChat)
                .or(() -> Optional.ofNullable(update.getChatJoinRequest()).map(ChatJoinRequest::getChat))
                .or(() -> Optional.ofNullable(update.getChatMember()).map(ChatMemberUpdated::getChat))
                .or(() -> Optional.ofNullable(update.getMyChatMember()).map(ChatMemberUpdated::getChat));
    }
}
