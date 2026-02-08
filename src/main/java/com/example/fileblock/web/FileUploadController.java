package com.example.fileblock.web;

import com.example.fileblock.service.ExtensionService;
import com.example.fileblock.web.dto.UploadResponse;
import com.example.fileblock.web.exception.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    private final ExtensionService extensionService;

    public FileUploadController(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("FILE_REQUIRED", "File is required.");
        }
        String ext = extensionService.validateUploadFilename(file.getOriginalFilename());
        return new UploadResponse("업로드 가능: ." + ext, ext);
    }
}
