package com.example.fileblock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "custom_blocked_extension")
public class CustomBlockedExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String ext;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CustomBlockedExtension() {
    }

    public CustomBlockedExtension(String ext) {
        this.ext = ext;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getExt() {
        return ext;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
