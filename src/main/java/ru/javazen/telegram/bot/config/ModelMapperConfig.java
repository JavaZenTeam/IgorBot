package ru.javazen.telegram.bot.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.User;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {
    @Bean
    public PropertyMap<Message, MessageEntity> messageEntityPropertyMap() {
        return new PropertyMap<Message, MessageEntity>() {
            @Override
            protected void configure() {
                map(source, destination.getMessagePK());
                using(textConverter).map(source, destination.getText());
                using(textLengthConverter).map(source, destination.getTextLength());
                using(scoreConverter).map(source, destination.getScore());
                using(dateConverter).map(source.getDate(), destination.getDate());
                map(source.getChat(), destination.getChat());
                map(source.getFrom(), destination.getUser());
                map(source.getForwardFrom(), destination.getForwardFrom());
                using(fileTypeConverter).map(source, destination.getFileType());
                using(fileIdConverter).map(source, destination.getFileId());
                using(wordsConverter).map(source.getText(), destination.getWords());
            }
        };
    }

    @Bean
    public PropertyMap<Chat, ChatEntity> chatEntityPropertyMap() {
        return new PropertyMap<Chat, ChatEntity>() {
            @Override
            protected void configure() {
                map().setChatId(source.getId());
                map().setTitle(source.getTitle());
                map().setUsername(source.getUserName());
            }
        };
    }

    @Bean
    public PropertyMap<User, UserEntity> userEntityPropertyMap() {
        return new PropertyMap<User, UserEntity>() {
            @Override
            protected void configure() {
                map().setUserId(source.getId());
                map().setUsername(source.getUserName());
                map().setFirstName(source.getFirstName());
                map().setLastName(source.getLastName());
            }
        };
    }

    @Bean
    public PropertyMap<Message, MessagePK> messagePkPropertyMap() {
        return new PropertyMap<Message, MessagePK>() {
            @Override
            protected void configure() {
                map().setChatId(source.getChatId());
                map().setMessageId(source.getMessageId());
            }
        };
    }

    private Converter<Message, String> textConverter = ctx -> MessageHelper.getActualText(ctx.getSource());

    private Converter<Message, Integer> textLengthConverter = ctx -> {
        String text = MessageHelper.getActualText(ctx.getSource());
        return text != null ? text.length() : 0;
    };

    private Converter<Message, Float> scoreConverter = ctx -> {
        String text = MessageHelper.getActualText(ctx.getSource());
        return text != null ? text.length() : (float) Math.sqrt(30);
    };

    private Converter<Integer, Date> dateConverter = ctx -> new Date(1000L * ctx.getSource());

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\P{L}*(^|\\s+|$)\\P{L}*", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern FILTER_PATTERN = Pattern.compile("^[-\\p{L}]+$", Pattern.UNICODE_CHARACTER_CLASS);

    private Converter<String, List<String>> wordsConverter = ctx ->
            ctx.getSource() == null
                    ? Collections.emptyList()
                    : SPLIT_PATTERN.splitAsStream(ctx.getSource())
                            .filter(FILTER_PATTERN.asPredicate())
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());

    private Converter<Message, FileType> fileTypeConverter = ctx -> {
        if (ctx.getSource().getPhoto() != null) return FileType.PHOTO;
        if (ctx.getSource().getAudio() != null) return FileType.AUDIO;
        if (ctx.getSource().getDocument() != null) return FileType.DOCUMENT;
        if (ctx.getSource().getVideo() != null) return FileType.VIDEO;
        if (ctx.getSource().getVoice() != null) return FileType.VOICE;
        if (ctx.getSource().getVideoNote() != null) return FileType.VIDEO_NOTE;
        if (ctx.getSource().getSticker() != null) return FileType.STICKER;

        return null;
    };

    private Converter<Message, String> fileIdConverter = ctx -> {
        if (ctx.getSource().getPhoto() != null)
            return ctx.getSource().getPhoto().stream()
                    .map(PhotoSize::getFileId)
                    .collect(Collectors.joining(","));
        if (ctx.getSource().getAudio() != null) return ctx.getSource().getAudio().getFileId();
        if (ctx.getSource().getDocument() != null) return ctx.getSource().getDocument().getFileId();
        if (ctx.getSource().getVideo() != null) return ctx.getSource().getVideo().getFileId();
        if (ctx.getSource().getVoice() != null) return ctx.getSource().getVoice().getFileId();
        if (ctx.getSource().getVideoNote() != null) return ctx.getSource().getVideoNote().getFileId();
        if (ctx.getSource().getSticker() != null) return ctx.getSource().getSticker().getFileId();

        return null;
    };

    @Bean
    public ModelMapper modelMapper(List<PropertyMap> propertyMaps) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);
        propertyMaps.forEach(mapper::addMappings);
        return mapper;
    }
}
