package ru.javazen.telegram.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public Message execute(SendDocument sendDocument) throws TelegramApiException {
        Message response = sender.execute(sendDocument);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendPhoto sendPhoto) throws TelegramApiException {
        Message response = sender.execute(sendPhoto);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendVideo sendVideo) throws TelegramApiException {
        Message response = sender.execute(sendVideo);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendVideoNote sendVideoNote) throws TelegramApiException {
        Message response = sender.execute(sendVideoNote);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendSticker sendSticker) throws TelegramApiException {
        Message response = sender.execute(sendSticker);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendAudio sendAudio) throws TelegramApiException {
        Message response = sender.execute(sendAudio);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public Message execute(SendVoice sendVoice) throws TelegramApiException {
        Message response = sender.execute(sendVoice);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        List<Message> response = sender.execute(sendMediaGroup);
        if (response != null && !response.isEmpty()) sentMessage = response.get(0);
        return response;
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return sender.execute(setChatPhoto);
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return sender.execute(addStickerToSet);
    }

    @Override
    public Boolean execute(SetStickerSetThumb setStickerSetThumb) throws TelegramApiException {
        return sender.execute(setStickerSetThumb);
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return sender.execute(createNewStickerSet);
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return sender.execute(uploadStickerFile);
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException {
        return sender.execute(editMessageMedia);
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws TelegramApiException {
        Message response = sender.execute(sendAnimation);
        if (response != null) sentMessage = response;
        return response;
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethodAsync");
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support sendApiMethod");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendDocument sendDocument) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideo sendVideo) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideoNote sendVideoNote) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAudio sendAudio) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVoice sendVoice) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<List<Message>> executeAsync(SendMediaGroup sendMediaGroup) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetChatPhoto setChatPhoto) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(AddStickerToSet addStickerToSet) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumb setStickerSetThumb) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet createNewStickerSet) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<File> executeAsync(UploadStickerFile uploadStickerFile) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Serializable> executeAsync(EditMessageMedia editMessageMedia) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAnimation sendAnimation) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(Method method) {
        throw new UnsupportedOperationException("BotUsageLogWrapper does't support executeAsync");
    }

    public Message getSentMessage() {
        return this.sentMessage;
    }
}
