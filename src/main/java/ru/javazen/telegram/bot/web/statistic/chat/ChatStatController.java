package ru.javazen.telegram.bot.web.statistic.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.repository.ChatEntityRepository;

@Controller
public class ChatStatController {
    private ChatEntityRepository chatEntityRepository;

    @Autowired
    public ChatStatController(ChatEntityRepository chatEntityRepository) {
        this.chatEntityRepository = chatEntityRepository;
    }

    @GetMapping("/chat/{chatId}")
    public String getChatView(@PathVariable("chatId") String chatId, Model model){
        ChatEntity chat = chatEntityRepository.findById(Long.valueOf(chatId)).orElse(null);
        model.addAttribute("chat", chat);
        return "chat";
    }
}
