package org.pavelryzhov.lab3.pptxcleaner.repository;

import org.pavelryzhov.lab3.pptxcleaner.entity.WatermarkPreset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatermarkPresetRepository extends JpaRepository<WatermarkPreset, Long> {
    List<WatermarkPreset> findByUser_Username(String username);
}
