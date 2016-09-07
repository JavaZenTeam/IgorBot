package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.SongRepository;
import ru.javazen.telegram.bot.service.TelegramBotService;

import static ru.javazen.telegram.bot.service.MessageHelper.answer;

public class SongSinger implements UpdateHandler{

    @Autowired
    private TelegramBotService botService;

    private SongRepository repository;
    private SongRepository.SongLine lastSongLine;

    public SongSinger(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean handle(Update update) {
        String string = update.getMessage().getText();
        if (string == null) return false;

        SongRepository.SongLine songLine = findLine(string);

        if (songLine == null || songLine.getNextLine() == null) return false;

        sendSongLine(update, songLine.getNextLine());
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

    private void sendSongLine(Update update, SongRepository.SongLine songLine) {
        if (songLine.getNextLine() != null)
            lastSongLine = songLine;

        botService.sendMessage(answer(update.getMessage(), songLine.getString()));
    }
}
