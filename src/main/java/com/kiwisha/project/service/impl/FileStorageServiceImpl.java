package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.FileDTO;
import com.kiwisha.project.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(@Value("${app.upload.dir}") String uploadDir) throws FileSystemException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            throw new FileSystemException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    @Override
    public FileDTO storeFile(MultipartFile file) throws FileSystemException {
        String fileName = UUID.randomUUID() + "." + file.getOriginalFilename();
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileSystemException("Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return FileDTO.builder().ruta(fileName).nombre(file.getOriginalFilename()).build();
        } catch (IOException ex) {
            throw new FileSystemException("Could not store file " + fileName + ". Please try again!");
        }
    }
}
