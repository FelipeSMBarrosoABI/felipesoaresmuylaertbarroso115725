package br.com.seplag.musicapi.infrastructure.storage.s3;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioClientFactory {

    private final MinioClient minioClient;

    public MinioClient getClient() {
        return minioClient;
    }
}
