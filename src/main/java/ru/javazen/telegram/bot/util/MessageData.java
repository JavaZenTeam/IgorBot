package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageData {
    private String messageText;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
}
