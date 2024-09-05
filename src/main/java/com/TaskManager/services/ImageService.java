package com.TaskManager.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ImageService {

    private String uploadFileToFirebase(InputStream inputStream, String fileName) throws IOException {
        BlobId blobId = BlobId.of("task-manager-1eddc.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

        InputStream credentialsStream = ImageService.class.getClassLoader().getResourceAsStream("firebase-config.json");
        Credentials credentials = GoogleCredentials.fromStream(credentialsStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        storage.create(blobInfo, inputStream);

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/task-manager-1eddc.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            String URL;
            try (InputStream inputStream = multipartFile.getInputStream()) {
                URL = this.uploadFileToFirebase(inputStream, fileName);
            }

            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, something went wrong";
        }
    }
}
