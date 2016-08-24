package ru.javazen.telegram.bot.service;

import java.util.List;

public interface SongRepository {
    SongLine saveSong(List<String> song);
    SongLine findSong(String string);
    SongLine findSong(SongLine songLine, String string);

    class SongLine {
        private String string;
        private SongLine nextLine;

        public SongLine(String string, SongLine nextLine) {
            this.string = string;
            this.nextLine = nextLine;
        }

        public String getString() {
            return string;
        }

        public SongLine getNextLine() {
            return nextLine;
        }
    }
}
