package com.TaskManager.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");


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
            String fileType = multipartFile.getContentType();
            if (!allowedTypes.contains(fileType)) {
                return null;
            }

            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            String URL;
            try (InputStream inputStream = multipartFile.getInputStream()) {
                URL = this.uploadFileToFirebase(inputStream, fileName);
            }

            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteImage(String url){
        String[] parts = url.split("[/?]");
        String fileName = parts[parts.length - 2];
        BlobId blobId = BlobId.of("task-manager-1eddc.appspot.com", fileName);
        InputStream credentialsStream = ImageService.class.getClassLoader().getResourceAsStream("firebase-config.json");
        try {
            Credentials credentials = GoogleCredentials.fromStream(credentialsStream);
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            return storage.delete(blobId);
        }
        catch (Exception e){
            return false;
        }
    }

}

