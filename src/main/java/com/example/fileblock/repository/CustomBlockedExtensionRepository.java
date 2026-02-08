package com.example.fileblock.repository;

import com.example.fileblock.entity.CustomBlockedExtension;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomBlockedExtensionRepository extends JpaRepository<CustomBlockedExtension, Long> {
    boolean existsByExt(String ext);
    List<CustomBlockedExtension> findAllByOrderByCreatedAtAsc();
}
