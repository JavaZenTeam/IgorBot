package ru.javazen.telegram.bot.service.impl;

import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.javazen.telegram.bot.client.FileServiceClient;
import ru.javazen.telegram.bot.model.UserVoiceMessage;
import ru.javazen.telegram.bot.model.VoiceMessageEntity;
import ru.javazen.telegram.bot.model.VoiceMessagePK;
import ru.javazen.telegram.bot.repository.UserVoiceRepository;
import ru.javazen.telegram.bot.repository.VoiceMessageRepository;
import ru.javazen.telegram.bot.service.VoiceService;

import java.util.Optional;

@Slf4j
public class VoiceServiceImpl implements VoiceService {

    private AmazonPolly amazonClient;
    private FileServiceClient fileServiceClient;
    private VoiceMessageRepository voiceMessageRepository;
    private UserVoiceRepository userVoiceRepository;


    public VoiceServiceImpl(FileServiceClient fileServiceClient, AmazonPolly amazonPolly, VoiceMessageRepository voiceMessageRepository) {
        this.fileServiceClient = fileServiceClient;
        this.amazonClient = amazonPolly;
        this.voiceMessageRepository = voiceMessageRepository;
    }

    @Override
    public String getAsFileLink(String text, Integer userId, VoiceId voiceId) {
        VoiceMessageEntity message = getVoiceMessageByText(text, voiceId);
        increaseUsageCount(message, userId);
        return message.getFileId();
    }

    @SneakyThrows
    @Override
    public byte[] synthesize(String text, VoiceId voiceId) {
        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Ogg_vorbis)
                .withVoiceId(voiceId)
                .withText(text);
        byte[] result = new byte[0];

        SynthesizeSpeechResult synthesizeSpeechResult = amazonClient.synthesizeSpeech(synthesizeSpeechRequest);
        return IOUtils.toByteArray(synthesizeSpeechResult.getAudioStream());
    }

    private VoiceMessageEntity getVoiceMessageByText(String text, VoiceId voiceId) {
        Optional<VoiceMessageEntity> voiceMessageOptional = voiceMessageRepository.findById(text.trim().toLowerCase());
        if (!voiceMessageOptional.isPresent()) {
            VoiceMessageEntity message = new VoiceMessageEntity();
            String fileId = fileServiceClient.uploadFile(synthesize(text, voiceId), text);
            message.setFileId(fileId);
            message.setText(text);
            return voiceMessageRepository.save(message);
        }
        return voiceMessageOptional.get();
    }

    private UserVoiceMessage increaseUsageCount(VoiceMessageEntity message, Integer userId) {
        Optional<UserVoiceMessage> userVoiceMessageOptional = userVoiceRepository.findById(new VoiceMessagePK(userId, message.getText()));
        UserVoiceMessage userVoiceMessage;
        if (userVoiceMessageOptional.isPresent()) {
            userVoiceMessage = userVoiceMessageOptional.get();
            userVoiceMessage.setCount(userVoiceMessage.getCount() + 1);
            return userVoiceRepository.save(userVoiceMessage);
        }
        userVoiceMessage = new UserVoiceMessage();
        userVoiceMessage.setCount(1);
        userVoiceMessage.setMessagePK(new VoiceMessagePK(userId, message.getText()));
        return userVoiceRepository.save(userVoiceMessage);
    }
}
