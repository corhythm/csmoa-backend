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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FirebaseStorageManager {

    private final String BUCKET_NAME = "csmoa-38f5b.appspot.com";
    private final String RECIPE = "recipe";
    private final String REVIEW = "review";

    @PostConstruct // Bean이 SpringApplicationContext에 등록될 때 실행
    private void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(BUCKET_NAME)
                    .setProjectId("csmoa-38f5b")
                    .setDatabaseUrl("https://csmoa-38f5b-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 데이터는 소중하니 delete는 굳이 만들지 말자.
    public String saveProfileImage(Long userId, MultipartFile multipartFile) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = userId + "_" + UUID.randomUUID();
        String profile = "profile";
        String filePath = profile + "/" + fileName;
        Blob blob = bucket.create(filePath, multipartFile.getBytes(), multipartFile.getContentType());

        log.info("bucket name = " + bucket.getName());
        log.info("mediaLink = " + blob.getMediaLink());
        log.info("selfLink = " + blob.getSelfLink());
        log.info("blob = " + blob);
        log.info("fileName = " + fileName);

        // "https://firebasestorage.googleapis.com/v0/b/<my bucket name>/o/<fileName>?alt=media"
        String absoluteFilePathUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s%%2F%s?alt=media",
                BUCKET_NAME, profile, fileName);
        log.info(absoluteFilePathUrl);

        return absoluteFilePathUrl;
    }

    public List<String> saveReviewImages(long userId, List<MultipartFile> reviewImages) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        List<String> reviewImageUrls = new ArrayList();

        for (MultipartFile reviewImage : reviewImages) {
            String fileName = userId + "_" + UUID.randomUUID();
            String recipe = "review";
            String filePath = recipe + "/" + fileName;
            // save
            bucket.create(filePath, reviewImage.getBytes(), reviewImage.getContentType());
            String absoluteFilePathUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s%%2F%s?alt=media",
                    BUCKET_NAME, recipe, fileName);
            reviewImageUrls.add(absoluteFilePathUrl);
        }

        return reviewImageUrls;
    }
}
