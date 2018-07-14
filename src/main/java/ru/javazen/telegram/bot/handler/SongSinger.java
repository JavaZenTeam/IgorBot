package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;
import ru.javazen.telegram.bot.service.SongRepository;
import ru.javazen.telegram.bot.util.MessageHelper;

@Handling(UpdateType.MESSAGE)
public class SongSinger implements UpdateHandler{

    private SongRepository repository;
    private SongRepository.SongLine lastSongLine;

    public SongSinger(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String string = update.getMessage().getText();
        if (string == null) return false;

        SongRepository.SongLine songLine = findLine(string);

        if (songLine == null || songLine.getNextLine() == null) return false;

        sendSongLine(update, songLine.getNextLine(), sender);
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

    private void sendSongLine(Update update, SongRepository.SongLine songLine, AbsSender sender) throws TelegramApiException {
        if (songLine.getNextLine() != null)
            lastSongLine = songLine;

        sender.execute(MessageHelper.answer(update.getMessage(), songLine.getString()));
    }
}
