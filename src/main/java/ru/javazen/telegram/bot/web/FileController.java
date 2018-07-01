package ru.javazen.telegram.bot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;

@Controller
public class FileController {
    private DefaultAbsSender bot;

    @Autowired
    public FileController(DefaultAbsSender bot) {
        this.bot = bot;
    }

    @GetMapping("/file/{fileId}")
    public String download(@PathVariable("fileId") String fileId) throws TelegramApiException, IOException {
        GetFile method = new GetFile();
        method.setFileId(fileId);
        File file = bot.execute(method);
        String fileUrl = file.getFileUrl(bot.getBotToken());

        return "redirect:" + fileUrl;
    }
}
