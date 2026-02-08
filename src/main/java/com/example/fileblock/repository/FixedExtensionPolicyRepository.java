package com.example.fileblock.repository;

import com.example.fileblock.entity.FixedExtensionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExtensionPolicyRepository extends JpaRepository<FixedExtensionPolicy, String> {
}
