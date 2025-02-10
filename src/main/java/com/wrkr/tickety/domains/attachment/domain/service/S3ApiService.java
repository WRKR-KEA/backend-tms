package com.wrkr.tickety.domains.attachment.domain.service;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3ApiService {

    private final S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String bucketName;

    /**
     * 단일 파일 업로드 후 URL 반환
     */
    public String uploadCommentFile(MultipartFile file) {
        String objectKey = "attachments/comments/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return s3Client.utilities().getUrl(builder ->
                builder.bucket(bucketName).key(objectKey).build()
            ).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public String uploadGuideFile(MultipartFile file) {
        String objectKey = "attachments/guide/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return s3Client.utilities().getUrl(builder ->
                builder.bucket(bucketName).key(objectKey).build()
            ).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public String uploadMemberProfileImage(MultipartFile file) {
        String objectKey = "attachments/member/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return s3Client.utilities().getUrl(builder ->
                builder.bucket(bucketName).key(objectKey).build()
            ).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    /**
     * S3에서 파일 삭제
     *
     * @param objectKey 삭제할 파일명
     * @return 삭제 성공 여부 (true: 성공, false: 실패)
     */
    public Boolean deleteObject(String objectKey) {
        try {
            String finalObjectKey = objectKey.substring(objectKey.indexOf(bucketName) + bucketName.length() + 1);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(finalObjectKey)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;

        } catch (S3Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteProfileImage(String profileImageUrl) {
        String objectKey = profileImageUrl.substring(profileImageUrl.indexOf(bucketName) + bucketName.length() + 1);
        return deleteObject(objectKey);
    }
}
