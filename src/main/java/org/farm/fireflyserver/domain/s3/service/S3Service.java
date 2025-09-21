package org.farm.fireflyserver.domain.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.s3.web.dto.response.PresignedUrlResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    // 프로필 사진 업로드용 presigned url
    public PresignedUrlResponseDTO generateProfilePresignedUrl() {
        String directory = "profile";
        String fileName = "image_" + UUID.randomUUID() + ".png";
        String filePath = directory + "/" + fileName;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 15);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, filePath)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return PresignedUrlResponseDTO.of(url.toString());
    }

}
