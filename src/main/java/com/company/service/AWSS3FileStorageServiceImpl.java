package com.company.service;

import com.amazonaws.services.s3.AmazonS3;
import com.company.configuration.AwsProperties;
import com.company.domain.AWSS3Object;
import com.company.utility.FileUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;


@RequiredArgsConstructor
@Slf4j
@Service
public class AWSS3FileStorageServiceImpl implements AWSS3FileStorageService {

    private final S3AsyncClient s3AsyncClient;
    private final AwsProperties s3ConfigProperties;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @Override
    public Mono<String> uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int maxTries = 3;

        return Mono.fromCallable(() -> {
            int count = 0;
            while (true) {
                try {
                    File fileObj = FileUtility.convertMultiPartFileToFile(file);
                    amazonS3.putObject(bucketName, originalFilename, fileObj);
                    LOGGER.info("File: {} uploaded successfully!!", originalFilename);
                    return "File: " + originalFilename + " uploaded successfully!!";
                } catch (IOException e) {
                    if (++count == maxTries) {
                        LOGGER.error("Runtime exception has occurred after attempting maximum number of tries for filename: {}", originalFilename);
                        throw new RuntimeException(e);
                    }
                }
            }
        }).onErrorResume(IOException.class, e -> {
            LOGGER.error("IOException occurred while uploading file: {}", originalFilename);
            return Mono.error(e);
        }).retry(maxTries);
    }

    @Override
    public Flux<AWSS3Object> getObjects() {
        LOGGER.info("Listing objects in S3 bucket: {}", s3ConfigProperties.getS3BucketName());
        return Flux.from(s3AsyncClient.listObjectsV2Paginator(ListObjectsV2Request.builder()
                        .bucket(s3ConfigProperties.getS3BucketName())
                        .build()))
                .flatMap(response -> Flux.fromIterable(response.contents()))
                .map(s3Object -> new AWSS3Object(s3Object.key(), s3Object.lastModified(),s3Object.eTag(), s3Object.size()));
    }

    @Override
    public Mono<Void> deleteObject(@NotNull String objectKey) {
        LOGGER.info("Delete Object with key: {}", objectKey);
        return Mono.just(DeleteObjectRequest.builder().bucket(s3ConfigProperties.getS3BucketName()).key(objectKey).build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .then();
    }

    @Override
    public Mono<byte[]> getByteObject(@NotNull String key) {
        LOGGER.debug("Fetching object as byte array from S3 bucket: {}, key: {}", s3ConfigProperties.getS3BucketName(), key);
        return Mono.just(GetObjectRequest.builder().bucket(s3ConfigProperties.getS3BucketName()).key(key).build())
                .map(it -> s3AsyncClient.getObject(it, AsyncResponseTransformer.toBytes()))
                .flatMap(Mono::fromFuture)
                .map(BytesWrapper::asByteArray);
    }

}
