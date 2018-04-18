package ru.javazen.telegram.bot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.javazen.telegram.bot.file.FileService;
import ru.javazen.telegram.bot.model.File;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileServiceController {

    private final FileService fileService;

    public FileServiceController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(path = "/{id}/download", method = RequestMethod.GET)
    public ResponseEntity download(@PathVariable("id") Long id) {
        if (!fileService.exist(id)) {
            return ResponseEntity.notFound().build();
        }

        File file = fileService.findById(id);

        return ResponseEntity.ok()
                .contentLength(file.getSize())
                .contentType(MediaType.parseMediaType(file.getType()))
                .body(file.getBytes());
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Long> upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setSize(file.getSize());
        fileMetadata.setType(file.getContentType());

        fileMetadata.setBytes(file.getBytes());
        fileService.saveFile(fileMetadata);

        return ResponseEntity.ok(fileMetadata.getId());
    }
}
