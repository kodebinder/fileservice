package com.company.controller;

import com.company.domain.SuccessResponse;
import com.company.service.AWSS3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/objects")
@Validated
public class AWSS3Controller {

    private final AWSS3FileStorageService fileStorageService;

    @PostMapping
    public Mono<SuccessResponse> uploadObject(@RequestParam(value = "file") MultipartFile file) {
        return fileStorageService.uploadFile(file)
                .map(fileResponse -> new SuccessResponse(fileResponse, "Upload successfully"));
    }

    @GetMapping(path = "/{fileKey}")
    public Mono<SuccessResponse> downloadObject(@PathVariable("fileKey") String fileKey) {
        return fileStorageService.getByteObject(fileKey)
                .map(objectKey -> new SuccessResponse(objectKey, "Object byte response"));
    }

    @DeleteMapping(path = "/{objectKey}")
    public Mono<SuccessResponse> deleteObject(@PathVariable("objectKey") String objectKey) {
        return fileStorageService.deleteObject(objectKey)
                .map(resp -> new SuccessResponse(null, MessageFormat.format("Object with key: {0} deleted successfully", objectKey)));
    }

    @GetMapping
    public Flux<SuccessResponse> getObject() {
        return fileStorageService.getObjects()
                .map(objectKey -> new SuccessResponse(objectKey, "Result found"));
    }
}
