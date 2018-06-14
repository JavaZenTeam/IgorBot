package ru.javazen.telegram.bot.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SongRepositoryImpl implements SongRepository {
    private Map<String, SongLine> songLineMap = new HashMap<>();
    private Function<String, String> encoder;

    public SongRepositoryImpl(){
        this(s -> s);
    }

    public SongRepositoryImpl(Function<String, String> encoder) {
        this.encoder = encoder;
    }

    public SongRepositoryImpl(Function<String, String> encoder, Collection<List<String>> songs){
        this(encoder);
        songs.forEach(this::saveSong);
    }

    @Override
    public SongLine saveSong(List<String> song) {
        if (song == null || song.isEmpty()) return null;
        SongLine firstSongLine = generateSongLine(song);
        SongLine songLine = firstSongLine;

        while (songLine != null) {
            String encodedString = encoder.apply(songLine.getString());
            songLineMap.putIfAbsent(encodedString, songLine);
            songLine = songLine.getNextLine();
        }
        return firstSongLine;
    }

    private SongLine generateSongLine(List<String> song){
        if (song == null || song.isEmpty()) return null;

        String string = song.get(0);
        SongLine nextLine = generateSongLine(song.subList(1, song.size()));
        return new SongLine(string, nextLine);
    }

    @Override
    public SongLine findSong(String string) {
        String encodedString = encoder.apply(string);
        return songLineMap.get(encodedString);
    }

    @Override
    public SongLine findSong(SongLine songLine, String string) {
        string = encoder.apply(string);
        SongRepository.SongLine current = songLine;
        for (int i = 0; i < 4; i++ ){
            if ((current = current.getNextLine()) == null) {
                break;
            }
            if (encoder.apply(current.getString()).equals(string)){
                return current;
            }
        }

        return null;
    }
}
