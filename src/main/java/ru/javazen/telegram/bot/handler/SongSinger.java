package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.SongRepository;

import static ru.javazen.telegram.bot.service.MessageHelper.answer;

public class SongSinger implements UpdateHandler{
    private SongRepository repository;
    private SongRepository.SongLine lastSongLine;

    public SongSinger(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean handle(Update update, Bot bot) {
        String string = update.getMessage().getText();
        if (string == null) return false;

        if (lastSongLine != null) {
            SongRepository.SongLine nextLine = repository.findSong(lastSongLine, string);
            if (nextLine != null) {
                sendSongLine(update, bot, nextLine.getNextLine());
                return true;
            }
        }

        return tryWithRepo(update, bot, string);
    }

    private boolean tryWithRepo(Update update, Bot bot, String string) {
        SongRepository.SongLine songLine = repository.findSong(string);
        if (songLine == null || songLine.getNextLine() == null)
            return false;

        sendSongLine(update, bot, songLine.getNextLine());
        return true;
    }

    private void sendSongLine(Update update, Bot bot, SongRepository.SongLine songLine) {
        if (songLine.getNextLine() != null)
            lastSongLine = songLine;

        bot.getService().sendMessage(answer(update.getMessage(), songLine.getString()));
    }
}
