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
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.List;

public class BotUsageLogWrapper extends AbsSender {
    private AbsSender sender;
    private Message sentMessage;

    public BotUsageLogWrapper(AbsSender sender) {
        this.sender = sender;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        T response = sender.execute(method);
        if (response instanceof Message) sentMessage = (Message) response;
        return response;
    }

    @Override
    public Message sendDocument(SendDocument sendDocument) throws TelegramApiException {
        Message response = sender.sendDocument(sendDocument);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendPhoto(SendPhoto sendPhoto) throws TelegramApiException {
        Message response = sender.sendPhoto(sendPhoto);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendVideo(SendVideo sendVideo) throws TelegramApiException {
        Message response = sender.sendVideo(sendVideo);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendVideoNote(SendVideoNote sendVideoNote) throws TelegramApiException {
        Message response = sender.sendVideoNote(sendVideoNote);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendSticker(SendSticker sendSticker) throws TelegramApiException {
        Message response = sender.sendSticker(sendSticker);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendAudio(SendAudio sendAudio) throws TelegramApiException {
        Message response = sender.sendAudio(sendAudio);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message sendVoice(SendVoice sendVoice) throws TelegramApiException {
        Message response = sender.sendVoice(sendVoice);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public List<Message> sendMediaGroup(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        List<Message> response = sender.sendMediaGroup(sendMediaGroup);
        if (response != null && !response.isEmpty()) sentMessage = response.get(0);
        return response;
    }

    @Override
    public Boolean setChatPhoto(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return sender.setChatPhoto(setChatPhoto);
    }

    @Override
    public Boolean addStickerToSet(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return sender.addStickerToSet(addStickerToSet);
    }

    @Override
    public Boolean createNewStickerSet(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return sender.createNewStickerSet(createNewStickerSet);
    }

    @Override
    public File uploadStickerFile(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return sender.uploadStickerFile(uploadStickerFile);
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethodAsync");
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethod");
    }

    public Message getSentMessage() {
        return this.sentMessage;
    }
}
