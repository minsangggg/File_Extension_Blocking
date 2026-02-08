package com.example.fileblock.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class FixedExtensionsProperties {
    private List<String> fixedExtensions = new ArrayList<>();

    public List<String> getFixedExtensions() {
        return fixedExtensions;
    }

    public void setFixedExtensions(List<String> fixedExtensions) {
        this.fixedExtensions = fixedExtensions;
    }
}
