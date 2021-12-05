package ru.javazen.telegram.bot.help;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.MessageHandler;
import ru.javazen.telegram.bot.util.MessageData;

@RequiredArgsConstructor
@Component("helpMainPostCommandHandler")
public class HelpMainPostCommandHandler implements MessageHandler {

    private final HelpInfoProvider helpInfoProvider;

    @Override
    public boolean handle(Message message, AbsSender sender) throws TelegramApiException {
        MessageData helpMessage = helpInfoProvider.getHelpMessage();

        SendMessage response = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(helpMessage.getMessageText())
                .parseMode(ParseMode.MARKDOWN)
                .build();
        response.setReplyMarkup(helpMessage.getInlineKeyboardMarkup());

        sender.execute(response);

        return true;
    }
}
