package com.company.service;

import com.company.domain.AWSS3Object;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AWSS3FileStorageService {
    Mono<byte[]> getByteObject(@NotNull String key);

    Mono<Void> deleteObject(@NotNull String objectKey);

    Flux<AWSS3Object> getObjects();

    Mono<String> uploadFile(MultipartFile file);
}
