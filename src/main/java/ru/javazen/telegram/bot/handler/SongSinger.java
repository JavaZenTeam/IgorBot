package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.service.SongRepository;

public class SongSinger implements TextMessageHandler {

    private SongRepository repository;
    private SongRepository.SongLine lastSongLine;

    public SongSinger(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        SongRepository.SongLine songLine = findLine(text);
        SongRepository.SongLine nextLine = songLine == null ? null : songLine.getNextLine();
        if (nextLine == null) return false;

        if (nextLine.getNextLine() != null)
            lastSongLine = songLine;

        sender.execute(new SendMessage(message.getChatId().toString(), nextLine.getString()));
        return true;
    }

    private SongRepository.SongLine findLine(String string){
        SongRepository.SongLine songLine = null;
        if (lastSongLine != null) {
            songLine = repository.findSong(lastSongLine, string);
        }
        if (songLine == null){
            songLine = repository.findSong(string);
        }
        return songLine;
    }
}
