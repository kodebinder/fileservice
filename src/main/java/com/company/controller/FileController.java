//package com.company.controller;
//
//import com.company.service.FileServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//import static java.net.HttpURLConnection.HTTP_OK;
//
//@RestController
//@RequestMapping("/file")
//@Slf4j
//public class FileController {
//
//    private final FileServiceImpl fileServiceImpl;
//
//    public FileController(FileServiceImpl fileServiceImpl) {
//        this.fileServiceImpl = fileServiceImpl;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
//        return new ResponseEntity<>(fileServiceImpl.uploadFile(file), HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<String>> getAllFilenames() {
//        return new ResponseEntity<>(fileServiceImpl.getAllFilenames(), HttpStatus.OK);
//    }
//
//    @GetMapping("/{filename}")
//    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String filename) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", MediaType.ALL_VALUE);
//        headers.add("Content-Disposition", "attachment; filename=" + filename);
//        byte[] data = fileServiceImpl.downloadFile(filename);
//        return ResponseEntity.status(HTTP_OK).contentLength(data.length).headers(headers).body(data);
//    }
//
//    @DeleteMapping("/{filename}")
//    public ResponseEntity<String> deleteFile(@PathVariable("filename") String filename) {
//        return new ResponseEntity<>(fileServiceImpl.deleteFile(filename), HttpStatus.OK);
//    }
//
//}