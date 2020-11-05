package ru.javazen.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.service.ChatConfigService;

import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserTimezoneHandler implements TextMessageHandler {

    private static final String PATTERN = "таймзона ([+-]?[0-9][0-9]?)(:([0-9][0-9]?))?";
    private static final String TIMEZONE_OFFSET_CONFIG_KEY = "TIMEZONE_OFFSET";

    private final ChatConfigService chatConfigService;

    public UserTimezoneHandler(ChatConfigService chatConfigService) {
        this.chatConfigService = chatConfigService;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Pattern activationPattern = Pattern.compile(PATTERN,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

        Matcher activation = activationPattern.matcher(text);

        if (activation.matches()) {
            String timezone = activation.group(1);
            int minutes = 0;

            String second = activation.group(3);
            if (second != null) {
                minutes = Integer.parseInt(second);
            }

            int hours = Integer.parseInt(timezone);
            if (hours >= -18 && hours <= 18 && minutes == 0
                    || hours >= -17 && hours <= 17 && minutes > 0 && minutes <= 59) {

                int hm = Math.abs(hours);
                String hf = hm < 10 ? "0" + hm : "" + hm;
                hf = hours < 0 ? "-" + hf : "+" + hf;

                String mf = minutes < 10 ? "0" + minutes : "" + minutes;

                ZoneOffset zoneOffset = ZoneOffset.of(hf + ":" + mf);

                chatConfigService.setProperty(
                        message.getFrom().getId(),
                        TIMEZONE_OFFSET_CONFIG_KEY,
                        zoneOffset.getId());

                return true;
            }
        }

        return false;
    }
}
