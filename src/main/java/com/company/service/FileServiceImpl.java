//package com.company.service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ListObjectsV2Result;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
//import com.amazonaws.services.s3.model.S3ObjectSummary;
//import com.amazonaws.util.IOUtils;
//import com.company.utility.FileUtility;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class FileServiceImpl implements FileService {
//
//    @Value("${cloud.aws.s3.bucket.name}")
//    private String bucketName;
//    private final AmazonS3 amazonS3;
//
//    public FileServiceImpl(AmazonS3 amazonS3) {
//        this.amazonS3 = amazonS3;
//    }
//
//    @Override
//    public String uploadFile(MultipartFile file) {
//        String originalFilename = file.getOriginalFilename();
//        int count = 0;
//        int maxTries = 3;
//        while (true) {
//            try {
//                File fileObj = FileUtility.convertMultiPartFileToFile(file);
//                amazonS3.putObject(bucketName, originalFilename, fileObj);
//                LOGGER.info("File : {} uploaded successfully!!", originalFilename);
//                return "File : " + originalFilename + " uploaded successfully!!";
//            } catch (IOException e) {
//                if (++count == maxTries) {
//                    LOGGER.error("Runtime exception has occurred after attempting maximum number of tries for filename: {}", originalFilename);
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
//
//    @Override
//    public byte[] downloadFile(String filename) {
//        S3Object s3Object = amazonS3.getObject(bucketName, filename);
//        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
//        try {
//            return IOUtils.toByteArray(s3ObjectInputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public String deleteFile(String filename) {
//        amazonS3.deleteObject(bucketName, filename);
//        LOGGER.info("deleted file: {}", filename);
//        return "Deleted file : " + filename;
//    }
//
//    @Override
//    public List<String> getAllFilenames() {
//        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);
//        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
//    }
//
//}