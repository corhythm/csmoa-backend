package com.dnr2144.csmoa.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FirebaseStorageManager {

    private final String MY_BUCKET_NAME = "csmoa-38f5b.appspot.com";

    @PostConstruct // Bean이 SpringApplicationContext에 등록될 때 실행
    private void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(MY_BUCKET_NAME)
                    .setDatabaseUrl("https://csmoa-38f5b-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String save(Long userId, MultipartFile multipartFile) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = userId + "_" + UUID.randomUUID();
        Blob blob = bucket.create(fileName, multipartFile.getBytes(), multipartFile.getContentType());

        log.info("bucket name = " + bucket.getName());
        log.info("mediaLink = " + blob.getMediaLink());
        log.info("selfLink = " + blob.getSelfLink());
        log.info("blob = " + blob.toString());
        log.info("fileName = " + fileName);

        // "https://firebasestorage.googleapis.com/v0/b/<bucket name>/o/<fileName>?alt=media"
        String downloadUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", MY_BUCKET_NAME, fileName);;
        log.info(downloadUrl);

        return downloadUrl;
    }

}
