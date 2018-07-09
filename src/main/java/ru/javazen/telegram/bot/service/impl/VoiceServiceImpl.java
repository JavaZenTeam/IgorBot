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
import ru.javazen.telegram.bot.service.VoiceService;

@Slf4j
public class VoiceServiceImpl implements VoiceService {

    private AmazonPolly amazonClient;
    private FileServiceClient fileServiceClient;

    public VoiceServiceImpl(FileServiceClient fileServiceClient, AmazonPolly amazonPolly) {
        this.fileServiceClient = fileServiceClient;
        this.amazonClient = amazonPolly;
    }

    @Override
    public String getAsFileLink(String text, VoiceId voiceId) {
        //TODO caching
        return fileServiceClient.uploadFile(synthesize(text, voiceId), text);
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
}
