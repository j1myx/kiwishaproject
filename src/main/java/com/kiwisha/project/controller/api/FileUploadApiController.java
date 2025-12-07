package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.FileDTO;
import com.kiwisha.project.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadApiController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileDTO>> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files) throws FileSystemException {
        List<FileDTO> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            FileDTO filedto = fileStorageService.storeFile(file);
            uploadedFiles.add(filedto);
        }
        return ResponseEntity.ok(uploadedFiles);
    }
}
