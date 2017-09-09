package ru.javazen.telegram.bot.handler;

import ru.javazen.telegram.bot.BotMethodExecutor;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.service.SongRepository;
import ru.javazen.telegram.bot.util.MessageHelper;

public class SongSinger implements UpdateHandler{

    private SongRepository repository;
    private SongRepository.SongLine lastSongLine;

    public SongSinger(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean handle(Update update, BotMethodExecutor executor) {
        String string = update.getMessage().getText();
        if (string == null) return false;

        SongRepository.SongLine songLine = findLine(string);

        if (songLine == null || songLine.getNextLine() == null) return false;

        sendSongLine(update, songLine.getNextLine(), executor);
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

    private void sendSongLine(Update update, SongRepository.SongLine songLine, BotMethodExecutor executor) {
        if (songLine.getNextLine() != null)
            lastSongLine = songLine;

        executor.execute(MessageHelper.answer(update.getMessage(), songLine.getString()), Void.class);
    }
}
