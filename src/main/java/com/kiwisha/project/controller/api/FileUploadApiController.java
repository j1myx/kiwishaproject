package com.kiwisha.project.controller.api;

import com.kiwisha.project.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadApiController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws FileSystemException {
        List<String> uploadedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = fileStorageService.storeFile(file);
            uploadedFileNames.add(fileName);
        }
        return ResponseEntity.ok(uploadedFileNames);
    }
}
