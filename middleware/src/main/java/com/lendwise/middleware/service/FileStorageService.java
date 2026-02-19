package com.lendwise.middleware.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/gif",
            "image/webp",
            "application/pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".png", ".jpg", ".jpeg", ".gif", ".webp", ".pdf"
    );

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("File upload directory initialized at: {}", uploadPath);
        } catch (IOException e) {
            log.error("Failed to initialize upload directory: {}", uploadDir, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) {

        log.info("File upload request received. Original filename: {}, Size: {} bytes",
                file.getOriginalFilename(),
                file.getSize());

        if (file.isEmpty()) {
            log.warn("Upload attempt with empty file.");
            throw new RuntimeException("File cannot be empty");
        }

        validateFile(file);

        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            if (originalFileName.contains("..")) {
                log.warn("Invalid path sequence detected in filename: {}", originalFileName);
                throw new RuntimeException("Invalid file path");
            }

            String extension = getFileExtension(originalFileName);

            String newFileName = UUID.randomUUID() + extension;

            Path targetLocation = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File successfully stored as: {}", newFileName);

            return newFileName;

        } catch (IOException ex) {
            log.error("File storage failed for file: {}", file.getOriginalFilename(), ex);
            throw new RuntimeException("Could not store file", ex);
        }
    }

    private void validateFile(MultipartFile file) {

        String contentType = file.getContentType();
        String extension = getFileExtension(file.getOriginalFilename());

        log.debug("Validating file. Content-Type: {}, Extension: {}", contentType, extension);

        // Primary validation: MIME type
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Rejected file due to invalid content type: {}", contentType);
            throw new RuntimeException("Only image and PDF files are allowed");
        }

        // Secondary validation: Extension
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            log.warn("Rejected file due to invalid extension: {}", extension);
            throw new RuntimeException("Invalid file extension");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return null;

        int index = fileName.lastIndexOf(".");
        if (index == -1) return null;

        return fileName.substring(index);
    }

    public Path loadFile(String fileName) {
        return uploadPath.resolve(fileName).normalize();
    }
}
