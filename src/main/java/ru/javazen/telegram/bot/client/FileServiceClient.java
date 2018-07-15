package ru.javazen.telegram.bot.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class FileServiceClient {
    private static final String AUDIO_OGG_TYPE = "audio/ogg";
    private static final String UPLOAD_URI = "/file/upload";
    private static final String DOWNLOAD_URI = "/file/%s/download";

    private final String uploadFileUrl;
    private final String DownloadFileUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public FileServiceClient(String fileServiceUrl) {
        this.uploadFileUrl = fileServiceUrl + UPLOAD_URI;
        this.DownloadFileUrl = fileServiceUrl + DOWNLOAD_URI;
    }

    public String uploadFile(byte[] file, String text) {
        HttpHeaders binaryHeaders = new HttpHeaders();
        binaryHeaders.setContentType(MediaType.valueOf(AUDIO_OGG_TYPE));

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("file", new HttpEntity<>(
                new FileNameAwareByteArrayResource("igor.ogg", file),
                binaryHeaders));
        map.add("description", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> result = restTemplate.exchange(uploadFileUrl, HttpMethod.POST, requestEntity, String.class);

        log.debug("Uploaded file id: {}", result.getBody());
        return String.format(DownloadFileUrl, result.getBody());

    }

    //TODO public FileInfoDto getInfo(int id){}
}
