package org.farm.fireflyserver.domain.s3.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.s3.service.S3Service;
import org.farm.fireflyserver.domain.s3.web.dto.response.PresignedUrlResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "S3", description = "S3 관련 API")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "presigned URL 발급", description = "담당자 이미지 업로드용 S3 presigned URL 발급 API. presigned URL 받아서 PUT 요청으로 해당 이미지 업로드 가능")
    @PostMapping("/presigned-url")
    public BaseResponse<?> getPresignedUrl() {
        PresignedUrlResponseDTO presignedUrl = s3Service.generateProfilePresignedUrl();
        return BaseResponse.of(SuccessCode.OK, presignedUrl);
    }
}
