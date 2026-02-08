package com.example.fileblock.web;

import com.example.fileblock.service.ExtensionService;
import com.example.fileblock.web.dto.CustomCreateRequest;
import com.example.fileblock.web.dto.CustomExtensionDto;
import com.example.fileblock.web.dto.FixedExtensionDto;
import com.example.fileblock.web.dto.FixedUpdateRequest;
import com.example.fileblock.web.exception.BadRequestException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extensions")
public class ExtensionController {
    private final ExtensionService extensionService;

    public ExtensionController(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @GetMapping("/fixed")
    public List<FixedExtensionDto> getFixed() {
        return extensionService.getFixedExtensions();
    }

    @PutMapping("/fixed/{ext}")
    public FixedExtensionDto updateFixed(@PathVariable String ext, @RequestBody FixedUpdateRequest request) {
        if (request == null || request.blocked() == null) {
            throw new BadRequestException("BLOCKED_REQUIRED", "blocked is required.");
        }
        return extensionService.updateFixed(ext, request.blocked());
    }

    @GetMapping("/custom")
    public List<CustomExtensionDto> getCustom() {
        return extensionService.getCustomExtensions();
    }

    @PostMapping("/custom")
    public ResponseEntity<CustomExtensionDto> addCustom(@RequestBody CustomCreateRequest request) {
        if (request == null || request.ext() == null) {
            throw new BadRequestException("EXTENSION_REQUIRED", "Extension is required.");
        }
        CustomExtensionDto created = extensionService.addCustomExtension(request.ext());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<Void> deleteCustom(@PathVariable Long id) {
        extensionService.deleteCustomExtension(id);
        return ResponseEntity.noContent().build();
    }
}
