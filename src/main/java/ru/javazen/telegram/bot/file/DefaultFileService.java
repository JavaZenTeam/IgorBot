package ru.javazen.telegram.bot.file;

import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.file.provider.FileRepository;
import ru.javazen.telegram.bot.model.File;
import ru.javazen.telegram.bot.repository.FileMetadataRepository;

@Service
public class DefaultFileService implements FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileRepository fileRepository;

    public DefaultFileService(FileMetadataRepository fileMetadataRepository,
                              FileRepository fileProvider) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileRepository = fileProvider;
    }

    @Override
    public File findById(Long id) {
        File file = fileMetadataRepository.findOne(id);
        if (fileRepository.exist(id)) {
            file.setBytes(fileRepository.find(id));
        }
        return file;
    }

    @Override
    public File findMetadataById(Long id) {
        return fileMetadataRepository.findOne(id);
    }

    @Override
    public Long saveFile(File file) {
        File saved = fileMetadataRepository.save(file);
        if (file.getBytes() != null) {
            fileRepository.save(saved.getId(), file.getBytes());
        }
        return saved.getId();
    }

    @Override
    public void deleteFile(Long id) {
        fileRepository.delete(id);
        fileMetadataRepository.delete(id);
    }

    @Override
    public boolean exist(Long id) {
        return fileMetadataRepository.exists(id) && fileRepository.exist(id);
    }
}
