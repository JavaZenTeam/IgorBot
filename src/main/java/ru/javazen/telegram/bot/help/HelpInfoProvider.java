package ru.javazen.telegram.bot.help;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.yaml.snakeyaml.Yaml;
import ru.javazen.telegram.bot.util.MessageData;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class HelpInfoProvider {

    private static final String HELP_FILE = "help/help.yml";

    private Help help;

    public MessageData getHelpMessage() {
        StringBuilder responseText = new StringBuilder(help.getWelcome());
        responseText.append("\n");

        List<List<InlineKeyboardButton>> buttonsLines = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Help.Feature feature : help.getFeatures()) {
            responseText.append(" *").append(feature.getTitle()).append("*")
                    .append(" - ").append(feature.getDescription()).append("\n");

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(feature.getTitle());
            button.setCallbackData("help:feature:" + feature.getTitle());
            buttons.add(button);
            if (buttons.size() >= 3) {
                buttonsLines.add(buttons);
                buttons = new ArrayList<>();
            }
        }
        if (buttons.size() > 0) {
            buttonsLines.add(buttons);
        }


        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setKeyboard(buttonsLines);

        return new MessageData(responseText.toString(), inlineKeyboard);
    }

    public MessageData getFeatureInformation(String featureName) {
        for (Help.Feature feature : help.getFeatures()) {
            if (feature.getTitle().equals(featureName)) {
                String text = "`" + feature.getTitle() + "` - " + feature.getDescription();
                text += "\n";
                text += "\n";
                text += feature.getDetails();

                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("<< Вернуться к списку");
                button.setCallbackData("help:list");
                inlineKeyboard.setKeyboard(Collections.singletonList(Collections.singletonList(button)));

                return new MessageData(text, inlineKeyboard);
            }
        }
        return null;
    }

    @PostConstruct
    protected void loadHelp() throws IOException {
        Yaml yaml = new Yaml();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = classloader.getResourceAsStream(HELP_FILE)) {
            help = yaml.loadAs(in, Help.class);

        }
    }
}
