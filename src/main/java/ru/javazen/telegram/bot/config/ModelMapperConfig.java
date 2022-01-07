package ru.javazen.telegram.bot.config;

import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.CustomConditions;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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
                using(fileUniqueIdConverter).map(source, destination.getFileUniqueId());
                when(CustomConditions.isNotEmpty())
                        .map(source.getNewChatMembers(), destination.getMembers());
                when(Conditions.isNotNull())
                        .map(source.getLeftChatMember(), destination.getMember());
                using(eventTypeConverter).map(source, destination.getEventType());
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
                using(chatTypeConverter).map(source.getType(), destination.getType());
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

    private final Converter<Message, String> textConverter = ctx ->
            ofNullable(MessageHelper.getActualText(ctx.getSource()))
                    .or(() -> ofNullable(ctx.getSource().getPinnedMessage()).map(MessageHelper::getActualText))
                    .orElse(null);

    private final Converter<Message, Integer> textLengthConverter = ctx -> {
        String text = MessageHelper.getActualText(ctx.getSource());
        return text != null ? text.length() : 0;
    };

    private final Converter<Message, Double> scoreConverter = ctx -> {
        String text = MessageHelper.getActualText(ctx.getSource());
        int length = text != null ? text.length() : 30;
        return Math.sqrt(length);
    };

    private final Converter<Integer, Date> dateConverter = ctx -> new Date(1000L * ctx.getSource());

    private final Converter<Message, FileType> fileTypeConverter = ctx ->
            ofNullable(resolveFileType(ctx.getSource()))
                    .or(() -> ofNullable(ctx.getSource().getPinnedMessage()).map(this::resolveFileType))
                    .orElse(null);

    private FileType resolveFileType(Message message) {
        if (message.getPhoto() != null) return FileType.PHOTO;
        if (message.getAudio() != null) return FileType.AUDIO;
        if (message.getDocument() != null) return FileType.DOCUMENT;
        if (message.getVideo() != null) return FileType.VIDEO;
        if (message.getVoice() != null) return FileType.VOICE;
        if (message.getVideoNote() != null) return FileType.VIDEO_NOTE;
        if (message.getSticker() != null) return FileType.STICKER;
        if (message.getAnimation() != null) return FileType.ANIMATION;

        return null;
    }

    private final Converter<Message, String> fileIdConverter = ctx ->
            ofNullable(resolveFileId(ctx.getSource()))
                    .or(() -> ofNullable(ctx.getSource().getPinnedMessage()).map(this::resolveFileId))
                    .orElse(null);

    private String resolveFileId(Message message) {
        if (message.getPhoto() != null)
            return message.getPhoto().stream()
                    .map(PhotoSize::getFileId)
                    .collect(Collectors.joining(","));
        if (message.getAudio() != null) return message.getAudio().getFileId();
        if (message.getDocument() != null) return message.getDocument().getFileId();
        if (message.getVideo() != null) return message.getVideo().getFileId();
        if (message.getVoice() != null) return message.getVoice().getFileId();
        if (message.getVideoNote() != null) return message.getVideoNote().getFileId();
        if (message.getSticker() != null) return message.getSticker().getFileId();
        if (message.getAnimation() != null) return message.getAnimation().getFileId();

        return null;
    }

    private final Converter<Message, String> fileUniqueIdConverter = ctx ->
            ofNullable(resolveFileUniqueId(ctx.getSource()))
                    .or(() -> ofNullable(ctx.getSource().getPinnedMessage()).map(this::resolveFileUniqueId))
                    .orElse(null);

    private String resolveFileUniqueId(Message message) {
        if (message.getPhoto() != null)
            return message.getPhoto().stream()
                    .map(PhotoSize::getFileId)
                    .collect(Collectors.joining(","));
        if (message.getAudio() != null) return message.getAudio().getFileUniqueId();
        if (message.getDocument() != null) return message.getDocument().getFileUniqueId();
        if (message.getVideo() != null) return message.getVideo().getFileUniqueId();
        if (message.getVoice() != null) return message.getVoice().getFileUniqueId();
        if (message.getVideoNote() != null) return message.getVideoNote().getFileUniqueId();
        if (message.getSticker() != null) return message.getSticker().getFileUniqueId();
        if (message.getAnimation() != null) return message.getAnimation().getFileUniqueId();

        return null;
    }

    private final Converter<Message, EventType> eventTypeConverter = ctx -> {
        Message message = ctx.getSource();
        if (message.getPinnedMessage() != null) return EventType.PINNED_MESSAGE;
        if (message.getNewChatTitle() != null) return EventType.NEW_TITLE;
        if (!CollectionUtils.isEmpty(message.getNewChatPhoto())) return EventType.NEW_PHOTO;
        if (Boolean.TRUE.equals(message.getDeleteChatPhoto())) return EventType.DELETED_PHOTO;
        if (!CollectionUtils.isEmpty(message.getNewChatMembers())) return EventType.NEW_MEMBERS;
        if (message.getLeftChatMember() != null) return EventType.LEFT_MEMBER;
        return null;
    };

    private final Converter<String, ChatType> chatTypeConverter = ctx -> Optional
            .ofNullable(EnumUtils.getEnumIgnoreCase(ChatType.class, ctx.getSource()))
            .orElse(ChatType.UNKNOWN);

    @Bean
    public ModelMapper modelMapper(List<PropertyMap<?, ?>> propertyMaps) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);
        propertyMaps.forEach(mapper::addMappings);
        return mapper;
    }
}
