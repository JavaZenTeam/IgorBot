package ru.javazen.telegram.bot.client;

import org.springframework.core.io.ByteArrayResource;

public class FileNameAwareByteArrayResource extends ByteArrayResource {

    private String fileName;

    public FileNameAwareByteArrayResource(String fileName, byte[] byteArray) {
        super(byteArray);
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return fileName;
    }
}