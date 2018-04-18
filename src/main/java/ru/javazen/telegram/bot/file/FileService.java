package ru.javazen.telegram.bot.file;

import ru.javazen.telegram.bot.model.File;

public interface FileService {

    /**
     * Returns file by id
     * @param id id of file
     * @return file
     */
    File findById(Long id);

    /**
     * Returns file metadata by id without bytes
     * @param id id of file
     * @return file without bytes
     */
    File findMetadataById(Long id);

    /**
     * Save new file
     * @param file a new file
     * @return id of new file
     */
    Long saveFile(File file);

    /**
     * Deleted file
     * @param id id of file
     */
    void deleteFile(Long id);

    /**
     * Check is file metadata exist
     * @param id id of metadata
     * @return true or false
     */
    boolean exist(Long id);
}
