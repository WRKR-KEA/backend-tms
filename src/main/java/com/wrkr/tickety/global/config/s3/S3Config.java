package com.wrkr.tickety.global.config.s3;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${s3.endpoint}")
    private String s3Endpoint;
    @Value("${s3.credentials.accessKey}")
    private String accessKey;
    @Value("${s3.credentials.secretKey}")
    private String secretKey;
    @Value("${s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {

        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .endpointOverride(URI.create(s3Endpoint))
            .forcePathStyle(true)
            .region(Region.of(region))
            .build();
    }
}