package org.farm.fireflyserver.domain.s3.web.dto.response;

public record PresignedUrlResponseDTO(
        String presignedUrl
) {
    public static PresignedUrlResponseDTO of(String presignedUrl) {
        return new PresignedUrlResponseDTO(presignedUrl);
    }
}
