package br.com.seplag.musicapi.infrastructure.storage.s3;

import br.com.seplag.musicapi.infrastructure.storage.exceptions.StorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private final MinioClient minioClient;

    @Value("${app.minio.bucket}")
    private String bucket;

    @Value("${app.minio.presign-expiration-seconds}")
    private int presignExpirationSeconds;

    public String generatePresignedUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(presignExpirationSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned URL", e);
        }
    }
}
