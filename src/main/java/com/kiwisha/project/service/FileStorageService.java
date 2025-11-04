package com.kiwisha.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystemException;

@Service
public interface FileStorageService {
    String storeFile(MultipartFile file) throws FileSystemException;
}
