package ru.javazen.telegram.bot.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Message;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.send.SendMessage;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class UpdateInfoProvider implements UpdateHandler {

    private ObjectMapper mapper;

    private String invalidPathMessage = "invalid path";

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        try {
            Message replyToMessage = update.getMessage().getReplyToMessage();
            Object requestedEntity = replyToMessage == null ? update : replyToMessage;

            String[] args = MessageHelper.getActualText(update.getMessage()).split(" ");
            if (args.length > 1) requestedEntity = resolveEntity(requestedEntity, args[1]);

            String answer = mapper.writeValueAsString(requestedEntity);
            SendMessage message = MessageHelper.answer(update.getMessage(), "```" + answer + "```");
            message.setParseMode("MARKDOWN");
            executor.execute(message, Void.class);
            return true;
        } catch (IllegalArgumentException e) {
            SendMessage message = MessageHelper.answer(update.getMessage(), invalidPathMessage);
            executor.execute(message, Void.class);
            return true;
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setInvalidPathMessage(String InvalidPathMessage) {
        this.invalidPathMessage = InvalidPathMessage;
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
