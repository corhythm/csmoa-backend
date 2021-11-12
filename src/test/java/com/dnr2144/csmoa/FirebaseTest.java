package com.dnr2144.csmoa;

import com.google.api.core.ApiFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@Slf4j
@SpringBootTest
public class FirebaseTest {

    private final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/<bucket name>/o/%s?alt=media";

    @Test
    void fireStoreTest() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("users").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            log.info(document.toObject(Member.class).toString());
        }
        documents.forEach(document -> log.info(document.toObject(Member.class).toString()));
    }

    @Test
    String StorageTest() throws IOException {

        File file = new File("src/main/resources/serviceAccountKey.json");
        String fileName = "1_" + UUID.randomUUID();
        BlobId blobId  = BlobId.of("bucket name", "src/main/resources/serviceAccountKey.json");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/serviceAccountKey.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    @Test
    void convertFile(MultipartFile multipartFile) {
        File tempFile = new File("src/main/resources/serviceAccountKey.json");
        log.info(tempFile.getName() + ", " + tempFile.length());
    }
}

@Getter
@ToString
@NoArgsConstructor
class Member {
    private String id;
    private String name;
    private int age;
    private String tel;

    public Member(String id, String name, int age, String tel) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.tel = tel;
    }
}
