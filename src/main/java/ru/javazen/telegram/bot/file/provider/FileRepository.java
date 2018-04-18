package ru.javazen.telegram.bot.file.provider;

public interface FileRepository {

    byte[] find(Long id);

    void save(Long id, byte[] bytes);

    void delete(Long id);

    boolean exist(Long id);
}
