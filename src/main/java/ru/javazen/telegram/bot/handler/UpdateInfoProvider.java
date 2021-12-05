package ru.javazen.telegram.bot.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class UpdateInfoProvider implements TextMessageHandler {

    private ObjectMapper mapper;

    private Supplier<String> invalidPathMessageSupplier;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        try {
            Message reply = message.getReplyToMessage();
            Object requestedEntity = reply == null ? message : reply;

            String[] args = text.split(" ");
            if (args.length > 1) requestedEntity = resolveEntity(requestedEntity, args[1]);

            String answer = mapper.writeValueAsString(requestedEntity);
            sender.execute(SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("```\n" + answer + "```")
                    .parseMode(ParseMode.MARKDOWN).build());
            return true;
        } catch (IllegalArgumentException e) {
            sender.execute(new SendMessage(message.getChatId().toString(), invalidPathMessageSupplier.get()));
            return true;
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setInvalidPathMessage(String invalidPathMessage) {
        setInvalidPathMessageSupplier(() -> invalidPathMessage);
    }

    public void setInvalidPathMessageSupplier(Supplier<String> invalidPathMessageSupplier) {
        this.invalidPathMessageSupplier = invalidPathMessageSupplier;
    }

    private Object resolveEntity(Object entity, String path) throws InvocationTargetException, IllegalAccessException {
        int dotIndex = path.indexOf('.');
        String pathNode = dotIndex == -1
                ? path
                : path.substring(0, dotIndex);

        if (entity == null) throw new IllegalArgumentException("can't take property " + pathNode + "from null");
        PropertyDescriptor property = BeanUtils.getPropertyDescriptor(entity.getClass(), pathNode);
        if (property == null) throw new IllegalArgumentException("unknown property " + pathNode);
        Object result = property.getReadMethod().invoke(entity);

        return dotIndex == -1
                ? result
                : resolveEntity(result, path.substring(dotIndex + 1));
    }
}
