package ru.javazen.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javazen.telegram.bot.model.File;

public interface FileMetadataRepository extends CrudRepository<File, Long> {

    File findByName(String name);
}
