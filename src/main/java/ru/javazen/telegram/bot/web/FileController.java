package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class FileController {
    private DefaultAbsSender bot;
    private HttpClient telegramHttpClient;

    @GetMapping("/file/{fileId}")
    public void download(@PathVariable("fileId") String fileId, HttpServletResponse servletResponse) throws TelegramApiException, IOException {
        GetFile method = new GetFile();
        method.setFileId(fileId);
        File file = bot.execute(method);
        String fileUrl = file.getFileUrl(bot.getBotToken());

        HttpResponse response = telegramHttpClient.execute(new HttpGet(fileUrl));

        IOUtils.copy(response.getEntity().getContent(), servletResponse.getOutputStream());
    }
}
