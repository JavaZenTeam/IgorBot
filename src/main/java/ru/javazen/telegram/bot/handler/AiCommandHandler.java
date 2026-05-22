package ru.javazen.telegram.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

@Slf4j
@Component
public class AiCommandHandler implements TextMessageHandler {

    private final ChatModel chatModel;

    @Autowired
    public AiCommandHandler(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        String question = text.replaceFirst("^/ai\\s*", "").trim();
        if (question.isEmpty()) {
            sender.execute(new SendMessage(message.getChatId().toString(), "Please provide a question after /ai"));
            return true;
        }

        try {
            String aiResponse = chatModel.call(question);
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), aiResponse);
            sendMessage.setMessageThreadId(message.getMessageThreadId());
            sender.execute(sendMessage);
        } catch (Exception e) {
            log.error("Error calling AI API", e);
            sender.execute(new SendMessage(message.getChatId().toString(), "Error calling AI API: " + e.getMessage()));
        }

        return true;
    }
}
