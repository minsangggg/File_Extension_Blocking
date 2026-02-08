package com.example.fileblock.service;

import com.example.fileblock.config.FixedExtensionsProperties;
import com.example.fileblock.entity.CustomBlockedExtension;
import com.example.fileblock.entity.FixedExtensionPolicy;
import com.example.fileblock.repository.CustomBlockedExtensionRepository;
import com.example.fileblock.repository.FixedExtensionPolicyRepository;
import com.example.fileblock.web.dto.CustomExtensionDto;
import com.example.fileblock.web.dto.FixedExtensionDto;
import com.example.fileblock.web.exception.BadRequestException;
import com.example.fileblock.web.exception.ConflictException;
import com.example.fileblock.web.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExtensionService {
    private static final int CUSTOM_MAX = 200;
    private static final int EXT_MAX_LENGTH = 20;

    private final FixedExtensionPolicyRepository fixedRepository;
    private final CustomBlockedExtensionRepository customRepository;
    private final FixedExtensionsProperties properties;

    public ExtensionService(
            FixedExtensionPolicyRepository fixedRepository,
            CustomBlockedExtensionRepository customRepository,
            FixedExtensionsProperties properties
    ) {
        this.fixedRepository = fixedRepository;
        this.customRepository = customRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public List<FixedExtensionDto> getFixedExtensions() {
        List<String> ordered = properties.getFixedExtensions().stream()
                .map(this::normalizeForConfig)
                .toList();

        Map<String, FixedExtensionPolicy> map = new HashMap<>();
        fixedRepository.findAllById(ordered).forEach(item -> map.put(item.getExt(), item));

        return ordered.stream()
                .map(ext -> {
                    FixedExtensionPolicy policy = map.get(ext);
                    boolean blocked = policy != null && policy.isBlocked();
                    return new FixedExtensionDto(ext, blocked);
                })
                .toList();
    }

    @Transactional
    public FixedExtensionDto updateFixed(String ext, boolean blocked) {
        String normalized = normalizeForInput(ext);
        FixedExtensionPolicy policy = fixedRepository.findById(normalized)
                .orElseThrow(() -> new NotFoundException("FIXED_NOT_FOUND", "Fixed extension not found."));
        policy.setBlocked(blocked);
        return new FixedExtensionDto(policy.getExt(), policy.isBlocked());
    }

    @Transactional(readOnly = true)
    public List<CustomExtensionDto> getCustomExtensions() {
        return customRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(item -> new CustomExtensionDto(item.getId(), item.getExt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomExtensionDto addCustomExtension(String rawExt) {
        String normalized = normalizeForInput(rawExt);

        if (properties.getFixedExtensions().stream()
                .map(this::normalizeForConfig)
                .anyMatch(ext -> ext.equals(normalized))) {
            throw new ConflictException("EXTENSION_IN_FIXED_LIST", "Extension exists in fixed list.");
        }

        if (customRepository.existsByExt(normalized)) {
            throw new ConflictException("CUSTOM_EXISTS", "Custom extension already exists.");
        }

        long count = customRepository.count();
        if (count >= CUSTOM_MAX) {
            throw new ConflictException("CUSTOM_LIMIT", "Custom extension limit exceeded.");
        }

        CustomBlockedExtension saved = customRepository.save(new CustomBlockedExtension(normalized));
        return new CustomExtensionDto(saved.getId(), saved.getExt());
    }

    @Transactional
    public void deleteCustomExtension(Long id) {
        if (!customRepository.existsById(id)) {
            throw new NotFoundException("CUSTOM_NOT_FOUND", "Custom extension not found.");
        }
        customRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String validateUploadFilename(String filename) {
        String ext = extractExtension(filename);
        FixedExtensionPolicy policy = fixedRepository.findById(ext).orElse(null);
        if (policy != null && policy.isBlocked()) {
            throw new ConflictException("EXTENSION_BLOCKED", "금지된 확장자 입니다.");
        }
        if (customRepository.existsByExt(ext)) {
            throw new ConflictException("EXTENSION_BLOCKED", "금지된 확장자 입니다.");
        }
        return ext;
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new BadRequestException("FILENAME_REQUIRED", "File name is required.");
        }
        String trimmed = filename.trim();
        int lastDot = trimmed.lastIndexOf('.');
        if (lastDot < 0 || lastDot == trimmed.length() - 1) {
            throw new BadRequestException("EXTENSION_REQUIRED", "File extension is required.");
        }
        String ext = trimmed.substring(lastDot + 1);
        return normalizeForInput(ext);
    }

    private String normalizeForInput(String input) {
        if (input == null) {
            throw new BadRequestException("EXTENSION_REQUIRED", "Extension is required.");
        }

        String ext = input.trim().toLowerCase();
        while (ext.startsWith(".")) {
            ext = ext.substring(1);
        }

        if (ext.isBlank()) {
            throw new BadRequestException("EXTENSION_EMPTY", "Extension cannot be empty.");
        }

        if (ext.length() > EXT_MAX_LENGTH) {
            throw new BadRequestException("EXTENSION_TOO_LONG", "Extension is too long.");
        }

        if (!ext.matches("^[a-z0-9]+$")) {
            throw new BadRequestException("EXTENSION_INVALID", "Extension contains invalid characters.");
        }

        return ext;
    }

    private String normalizeForConfig(String input) {
        if (input == null) {
            throw new BadRequestException("FIXED_CONFIG_INVALID", "Fixed extension is invalid.");
        }

        String ext = input.trim().toLowerCase();
        while (ext.startsWith(".")) {
            ext = ext.substring(1);
        }

        if (ext.isBlank()) {
            throw new BadRequestException("FIXED_CONFIG_INVALID", "Fixed extension is invalid.");
        }

        return ext;
    }
}
