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

        SongRepository.SongLine songLine = findLine(string);

        if (songLine == null || songLine.getNextLine() == null) return false;

        sendSongLine(update, bot, songLine.getNextLine());
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

    private void sendSongLine(Update update, Bot bot, SongRepository.SongLine songLine) {
        if (songLine.getNextLine() != null)
            lastSongLine = songLine;

        bot.getService().sendMessage(answer(update.getMessage(), songLine.getString()));
    }
}
