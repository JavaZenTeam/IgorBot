package ru.javazen.telegram.bot.web.statistic.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Controller
public class ChatStatController {
    private DefaultAbsSender bot;

    @Autowired
    public ChatStatController(DefaultAbsSender bot) {
        this.bot = bot;
    }

    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatIdStr, Model model) throws TelegramApiException {
        Long chatId = Long.valueOf(chatIdStr);
        Chat chat = bot.execute(new GetChat(chatId));
        model.addAttribute("chat", chat);
        return "chat";
    }
}
