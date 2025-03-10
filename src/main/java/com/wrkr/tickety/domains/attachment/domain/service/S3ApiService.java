package com.wrkr.tickety.domains.attachment.domain.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    private static final String BUCKET_PREFIX = "v1/1693f30f6d0848e895e9bb87002e8f62/";

    private final S3Client s3Client;
    private final Environment env;
    private final int WIDTH = 128;
    private final int HEIGHT = 128;
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

            String encodedUrl = s3Client.utilities().getUrl(builder ->
                    builder.bucket(bucketName).key(objectKey).build()
                                                           ).toString();

            encodedUrl = addUrlPrefix(encodedUrl);

            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
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

            String encodedUrl = s3Client.utilities().getUrl(builder ->
                    builder.bucket(bucketName).key(objectKey).build()
                                                           ).toString();

            encodedUrl = addUrlPrefix(encodedUrl);

            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public String uploadMemberProfileImage(MultipartFile file) {
        try {
            byte[] resizedImageBytes = resizeImage(file);

            String originalFileName = file.getOriginalFilename();
            String newFileName = null;
            if (originalFileName != null) {
                newFileName = originalFileName.replaceAll("\\.[^.]+$", ".png");
            }
            String objectKey = "attachments/member/" + UUID.randomUUID() + "-" + newFileName;

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType("image/png")
                    .build(),
                RequestBody.fromBytes(resizedImageBytes)
                              );

            String encodedUrl = s3Client.utilities().getUrl(builder ->
                    builder.bucket(bucketName).key(objectKey).build()
                                                           ).toString();

            encodedUrl = addUrlPrefix(encodedUrl);

            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
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

    /**
     * 운영 환경일 경우 bucketName 앞에 Prefix 붙여주기
     *
     * @param encodedUrl
     * @return
     */
    private String addUrlPrefix(String encodedUrl) {
        if (env.getActiveProfiles()[0].equals("prod")) {
            int bucketIndex = encodedUrl.indexOf(bucketName);
            if (bucketIndex != -1) {
                encodedUrl =
                    encodedUrl.substring(0, bucketIndex) +
                        BUCKET_PREFIX +
                        encodedUrl.substring(bucketIndex);
            }
        }
        return encodedUrl;
    }

    private byte[] resizeImage(MultipartFile multipartFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int squareSize = Math.min(originalWidth, originalHeight);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
            .sourceRegion(Positions.CENTER, squareSize, squareSize)
            .size(WIDTH, HEIGHT)
            .outputFormat("png")
            .outputQuality(1.0)
            .toOutputStream(baos);

        return baos.toByteArray();
    }
}
