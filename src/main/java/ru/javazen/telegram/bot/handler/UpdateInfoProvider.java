package ru.javazen.telegram.bot.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

@Handling(UpdateType.MESSAGE)
public class UpdateInfoProvider implements UpdateHandler {

    private ObjectMapper mapper;

    private Supplier<String> invalidPathMessageSupplier;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        try {
            Message replyToMessage = update.getMessage().getReplyToMessage();
            Object requestedEntity = replyToMessage == null ? update : replyToMessage;

            String[] args = MessageHelper.getActualText(update.getMessage()).split(" ");
            if (args.length > 1) requestedEntity = resolveEntity(requestedEntity, args[1]);

            String answer = mapper.writeValueAsString(requestedEntity);
            SendMessage message = MessageHelper.answer(update.getMessage(), "```\n" + answer + "```");
            message.setParseMode("MARKDOWN");
            sender.execute(message);
            return true;
        } catch (IllegalArgumentException e) {
            SendMessage message = MessageHelper.answer(update.getMessage(), invalidPathMessageSupplier.get());
            sender.execute(message);
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
