package ru.javazen.telegram.bot;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.updateshandlers.SentCallback;
import ru.javazen.telegram.bot.model.BotUsageLog;
import ru.javazen.telegram.bot.model.MessagePK;
import ru.javazen.telegram.bot.repository.BotUsageLogRepository;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.io.Serializable;
import java.util.List;

public class BotUsageLogWrapper extends AbsSender {
    private TelegramLongPollingBot target;
    private String moduleName;
    private Message sourceMessage;
    private BotUsageLogRepository botUsageLogRepository;


    private void logBotUsage(Message targetMessage) {
        BotUsageLog botUsageLog = new BotUsageLog();
        botUsageLog.setMessagePK(new MessagePK(targetMessage.getChatId(), targetMessage.getMessageId()));
        botUsageLog.setSourceMessageId(sourceMessage.getMessageId());
        botUsageLog.setModuleName(moduleName);
        botUsageLog.setText(MessageHelper.getActualText(targetMessage));
        botUsageLogRepository.save(botUsageLog);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        T response = target.execute(method);
        if (response instanceof Message) logBotUsage((Message) response);
        return response;
    }

    @Override
    public Message sendDocument(SendDocument sendDocument) throws TelegramApiException {
        Message response = target.sendDocument(sendDocument);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendPhoto(SendPhoto sendPhoto) throws TelegramApiException {
        Message response = target.sendPhoto(sendPhoto);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendVideo(SendVideo sendVideo) throws TelegramApiException {
        Message response = target.sendVideo(sendVideo);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendVideoNote(SendVideoNote sendVideoNote) throws TelegramApiException {
        Message response = target.sendVideoNote(sendVideoNote);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendSticker(SendSticker sendSticker) throws TelegramApiException {
        Message response = target.sendSticker(sendSticker);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendAudio(SendAudio sendAudio) throws TelegramApiException {
        Message response = target.sendAudio(sendAudio);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public Message sendVoice(SendVoice sendVoice) throws TelegramApiException {
        Message response = target.sendVoice(sendVoice);
        if (response != null) logBotUsage(response);
        return response;
    }

    @Override
    public List<Message> sendMediaGroup(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        List<Message> response = target.sendMediaGroup(sendMediaGroup);
        if (response != null && !response.isEmpty()) logBotUsage(response.get(0));
        return response;
    }

    @Override
    public Boolean setChatPhoto(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return target.setChatPhoto(setChatPhoto);
    }

    @Override
    public Boolean addStickerToSet(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return target.addStickerToSet(addStickerToSet);
    }

    @Override
    public Boolean createNewStickerSet(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return target.createNewStickerSet(createNewStickerSet);
    }

    @Override
    public File uploadStickerFile(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return target.uploadStickerFile(uploadStickerFile);
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethodAsync");
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) throws TelegramApiException {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethod");
    }

    void setTarget(TelegramLongPollingBot target) {
        this.target = target;
    }

    void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    void setSourceMessage(Message sourceMessage) {
        this.sourceMessage = sourceMessage;
    }

    void setBotUsageLogRepository(BotUsageLogRepository botUsageLogRepository) {
        this.botUsageLogRepository = botUsageLogRepository;
    }
}
