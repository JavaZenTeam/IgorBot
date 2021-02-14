package ru.javazen.telegram.bot.service;

import org.junit.Before;
import org.junit.Test;
import ru.javazen.telegram.bot.service.impl.SongRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SongRepositoryImplTest {
    private SongRepository songRepository;

    @Before
    public void setUp() {
        songRepository = new SongRepositoryImpl();
    }

    @Test
    public void saveNullSong() {
        assertNull(songRepository.saveSong(null));
    }

    @Test
    public void saveEmptySong() {
        assertNull(songRepository.saveSong(Collections.emptyList()));
    }

    @Test
    public void saveSong() {
        SongRepository.SongLine line = songRepository.saveSong(asList("1", "2", "3"));
        assertEquals("1", line.getString());
        assertEquals("2", line.getNextLine().getString());
        assertEquals("3", line.getNextLine().getNextLine().getString());
        assertNull(line.getNextLine().getNextLine().getNextLine());
    }


    @Test
    public void saveLongSong() {
        List<String> longSong = IntStream.range(0, 2 << 8)
                .mapToObj(Integer::toHexString)
                .collect(Collectors.toList());
        SongRepository.SongLine line = songRepository.saveSong(longSong);
        for (int i = 0; i < (2 << 8); i++) {
            assertEquals(Integer.toHexString(i), line.getString());
            line = line.getNextLine();
        }
        assertNull(line);
    }

    @Test
    public void findNull() {
        songRepository.saveSong(asList("1", "2", "3"));
        assertNull(songRepository.findSong(null));
    }

    @Test
    public void findLineWhenNoSongs() {
        assertNull(songRepository.findSong("1"));
    }

    @Test
    public void findNotExisting() {
        songRepository.saveSong(asList("1", "2", "3"));
        assertNull(songRepository.findSong("AAA"));
    }

    @Test
    public void findFirstLine() {
        songRepository.saveSong(asList("AAA", "BBB", "CCC"));
        SongRepository.SongLine line = songRepository.findSong("AAA");
        assertEquals("AAA", line.getString());
        assertEquals("BBB", line.getNextLine().getString());
    }

    @Test
    public void findNotFirstLine() {
        songRepository.saveSong(asList("AAA", "BBB", "CCC", "DDD"));
        SongRepository.SongLine line = songRepository.findSong("CCC");
        assertEquals("CCC", line.getString());
        assertEquals("DDD", line.getNextLine().getString());
    }

    @Test
    public void findLastLine() {
        songRepository.saveSong(asList("AAA", "BBB", "CCC"));
        SongRepository.SongLine line = songRepository.findSong("CCC");
        assertEquals("CCC", line.getString());
        assertNull(line.getNextLine());
    }

    @Test
    public void findLineInSeveralSongs() {
        songRepository.saveSong(asList("1AAA", "1BBB", "1CCC"));
        songRepository.saveSong(asList("2AAA", "2BBB", "2CCC"));
        songRepository.saveSong(asList("3AAA", "3BBB", "3CCC"));
        SongRepository.SongLine line = songRepository.findSong("2BBB");
        assertEquals("2BBB", line.getString());
        assertEquals("2CCC", line.getNextLine().getString());
    }

    @Test
    public void findNextLineInSong() {
        songRepository.saveSong(asList("1AAA", "BBB", "1CCC"));
        songRepository.saveSong(asList("2AAA", "BBB", "2CCC"));
        songRepository.saveSong(asList("3AAA", "BBB", "3CCC"));
        SongRepository.SongLine line1 = songRepository.findSong("2AAA");
        SongRepository.SongLine line2 = songRepository.findSong(line1, "BBB");
        assertEquals("BBB", line2.getString());
        assertEquals("2CCC", line2.getNextLine().getString());
    }


    @Test
    public void findDistantNextLineInSong() {
        songRepository.saveSong(asList("1AAA", "1BBB", "1CCC", "DDD", "1EEE"));
        songRepository.saveSong(asList("2AAA", "2BBB", "2CCC", "DDD", "2EEE"));
        songRepository.saveSong(asList("3AAA", "3BBB", "3CCC", "DDD", "3EEE"));
        SongRepository.SongLine line1 = songRepository.findSong("2BBB");
        SongRepository.SongLine line2 = songRepository.findSong(line1, "DDD");
        assertEquals("DDD", line2.getString());
        assertEquals("2EEE", line2.getNextLine().getString());
        assertNull(line2.getNextLine().getNextLine());
    }
}