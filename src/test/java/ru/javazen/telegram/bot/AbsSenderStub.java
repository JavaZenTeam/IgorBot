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

public class AbsSenderStub extends AbsSender {
    private BotApiMethod apiMethod;

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void executeAsync(Method method, Callback callback) throws TelegramApiException {
        super.executeAsync(method, callback);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        this.apiMethod = method;
        return null;
    }

    @Override
    public Message execute(SendDocument sendDocument) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendPhoto sendPhoto) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendVideo sendVideo) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendVideoNote sendVideoNote) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendSticker sendSticker) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendAudio sendAudio) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendVoice sendVoice) throws TelegramApiException {
        return null;
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(SetStickerSetThumb setStickerSetThumb) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return null;
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return null;
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws TelegramApiException {
        return null;
    }


    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {

    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) throws TelegramApiException {
        return null;
    }



    public BotApiMethod getApiMethod() {
        return apiMethod;
    }
}
