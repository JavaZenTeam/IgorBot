package ru.javazen.telegram.bot.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.javazen.telegram.bot.model.DictionaryWord;

public interface DictionaryRepository extends JpaRepository<DictionaryWord, String> {
    @Modifying
    @CacheEvict("DictionaryWord")
    @Query(value = "INSERT INTO dictionary_word SELECT word, count(*) FROM message_entity_words GROUP BY word",
            nativeQuery = true)
    void sync();

    @Modifying
    @CacheEvict("DictionaryWord")
    @Query(value = "TRUNCATE dictionary_word", nativeQuery = true)
    void truncate();

    @Cacheable("DictionaryWord")
    @Query("SELECT sum(d.count) from DictionaryWord d")
    Long sumCounts();
}
