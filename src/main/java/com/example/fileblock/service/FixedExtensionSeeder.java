package com.example.fileblock.service;

import com.example.fileblock.config.FixedExtensionsProperties;
import com.example.fileblock.entity.FixedExtensionPolicy;
import com.example.fileblock.repository.FixedExtensionPolicyRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FixedExtensionSeeder implements ApplicationRunner {
    private final FixedExtensionPolicyRepository fixedRepository;
    private final FixedExtensionsProperties properties;

    public FixedExtensionSeeder(
            FixedExtensionPolicyRepository fixedRepository,
            FixedExtensionsProperties properties
    ) {
        this.fixedRepository = fixedRepository;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> fixed = properties.getFixedExtensions();
        for (String raw : fixed) {
            String ext = normalize(raw);
            fixedRepository.findById(ext)
                    .orElseGet(() -> fixedRepository.save(new FixedExtensionPolicy(ext, false)));
        }
    }

    private String normalize(String input) {
        String ext = input == null ? "" : input.trim().toLowerCase();
        while (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        if (ext.isBlank()) {
            throw new IllegalStateException("Invalid fixed extension in config.");
        }
        return ext;
    }
}
