package br.com.seplag.musicapi.infrastructure.storage;

import br.com.seplag.musicapi.infrastructure.storage.exceptions.StorageException;
import br.com.seplag.musicapi.infrastructure.storage.s3.PresignedUrlService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresignedUrlServiceTest {

    @Mock
    private MinioClient minioClient;

    private PresignedUrlService presignedUrlService;

    @BeforeEach
    void setUp() {
        presignedUrlService = new PresignedUrlService(minioClient);
        ReflectionTestUtils.setField(presignedUrlService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(presignedUrlService, "presignExpirationSeconds", 3600);
    }

    @Test
    void generatePresignedUrl_WithValidObjectKey_ReturnsUrl() throws Exception {
        String expectedUrl = "http://minio:9000/test-bucket/covers/album-1.jpg?X-Amz-Signature=abc123";
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn(expectedUrl);

        String result = presignedUrlService.generatePresignedUrl("covers/album-1.jpg");

        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void generatePresignedUrl_WithMinioException_ThrowsStorageException() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("MinIO connection failed"));

        assertThatThrownBy(() -> presignedUrlService.generatePresignedUrl("covers/album-1.jpg"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to generate presigned URL");
    }

    @Test
    void generatePresignedUrl_WithDifferentObjectKeys_ReturnsCorrectUrls() throws Exception {
        String url1 = "http://minio:9000/test-bucket/covers/album-1.jpg?signature=1";
        String url2 = "http://minio:9000/test-bucket/covers/album-2.jpg?signature=2";

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(url1, url2);

        String result1 = presignedUrlService.generatePresignedUrl("covers/album-1.jpg");
        String result2 = presignedUrlService.generatePresignedUrl("covers/album-2.jpg");

        assertThat(result1).isEqualTo(url1);
        assertThat(result2).isEqualTo(url2);
    }
}
