package com.example.fileblock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "fixed_extension_policy")
public class FixedExtensionPolicy {
    @Id
    @Column(length = 20, nullable = false)
    private String ext;

    @Column(name = "is_blocked", nullable = false)
    private boolean blocked;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FixedExtensionPolicy() {
    }

    public FixedExtensionPolicy(String ext, boolean blocked) {
        this.ext = ext;
        this.blocked = blocked;
    }

    @PrePersist
    @PreUpdate
    public void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    public String getExt() {
        return ext;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
