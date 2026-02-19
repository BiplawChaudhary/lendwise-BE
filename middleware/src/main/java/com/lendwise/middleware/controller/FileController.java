package com.lendwise.middleware.controller;

import com.lendwise.middleware.constants.ApiConstants;
import com.lendwise.middleware.controller.base.BaseController;
import com.lendwise.middleware.dto.request.UploadFileDto;
import com.lendwise.middleware.service.FileStorageService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API_VERSION+ "/files")
public class FileController extends BaseController {

    @Autowired
    private FileStorageService fileStorageService;

    // Upload Endpoint
    @PostMapping(value = "/upload", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestHeader String urn,
            @ModelAttribute UploadFileDto dto) {
        MDC.put("urn", urn);
        String storedFileName = fileStorageService.storeFile(dto.getUploadedFile());

        String fileUrl = "/files/stream/" + storedFileName;

        return createSuccessResponse(
                Map.of("fileUrl", fileUrl),
                "save.success","File"
        );
    }

    // Streaming Endpoint
    @GetMapping("/stream/{fileName}")
    public ResponseEntity<Resource> streamFile(@PathVariable String fileName) {

        try {
            Path filePath = fileStorageService.loadFile(fileName);

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
