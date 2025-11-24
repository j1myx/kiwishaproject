package com.kiwisha.project.service;

import com.kiwisha.project.dto.FileDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystemException;

@Service
public interface FileStorageService {
    FileDTO storeFile(MultipartFile file) throws FileSystemException;
}
