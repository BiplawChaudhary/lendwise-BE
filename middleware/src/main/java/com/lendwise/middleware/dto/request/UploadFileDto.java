package com.lendwise.middleware.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileDto {
    private MultipartFile uploadedFile;
//    private String fileName;
//    private String fileSize;

}
