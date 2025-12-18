package org.pavelryzhov.lab3.pptxcleaner.controller;

import lombok.RequiredArgsConstructor;
import org.pavelryzhov.lab3.pptxcleaner.entity.UserStat;
import org.pavelryzhov.lab3.pptxcleaner.entity.WatermarkPreset;
import org.pavelryzhov.lab3.pptxcleaner.repository.UserStatRepository;
import org.pavelryzhov.lab3.pptxcleaner.repository.WatermarkPresetRepository;
import org.pavelryzhov.lab3.pptxcleaner.service.PptxCleanerService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://127.0.0.1:8080" })
public class MainController {
    private final PptxCleanerService cleanerService;
    private final UserStatRepository userStatRepository;
    private final WatermarkPresetRepository presetRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username) {
        if (username == null || username.trim().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username cannot be empty"));
        }
        var existingUser = userStatRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "status", "exists",
                    "message", "Welcome back, " + username + "!",
                    "user", existingUser.get()
            ));
        }
        var newUser = userStatRepository.save(new UserStat(username, 0));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "created",
                "message", "Welcome to the club, " + username + "!",
                "user", newUser
        ));
    }

    @GetMapping("/stats")
    public List<UserStat> getLeaderboard() {
        return userStatRepository.findAll(Sort.by(Sort.Direction.DESC, "totalSlidesCleaned"));
    }

    @PostMapping("/presets")
    public ResponseEntity<?> createPreset(
            @RequestParam String username,
            @RequestBody WatermarkPreset preset
    ) {
        var userOptional = userStatRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found. Please login first.");
        }
        var colorToSave = preset.getColor() == null ? "#000000" : preset.getColor();
        var newPreset = new WatermarkPreset(
                preset.getTitle(),
                preset.getReplacementText(),
                colorToSave,
                userOptional.get()
        );
        return ResponseEntity.ok(presetRepository.save(newPreset));
    }

    @GetMapping("/presets")
    public List<WatermarkPreset> getAllPresets(@RequestParam String username) {
        return presetRepository.findByUser_Username(username);
    }

    @PostMapping("/clean")
    public ResponseEntity<Resource> cleanPresentation(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "customText", required = false) String customText,
            @RequestParam(value = "color", required = false) String color
    ) throws IOException {
        var result = cleanerService.cleanPresentation(file.getInputStream(), customText, color);
        if (result.cleanedCount() > 0) {
            updateUserStat(username, result.cleanedCount());
        }
        var resource = new ByteArrayResource(result.data());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cleaned_" + file.getOriginalFilename())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                .contentLength(result.data().length)
                .body(resource);
    }

    @DeleteMapping("/presets/{id}")
    public ResponseEntity<?> deletePreset(@PathVariable Long id) {
        if (presetRepository.existsById(id)) {
            presetRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private void updateUserStat(String username, int count) {
        var userStat = userStatRepository.findByUsername(username).orElse(new UserStat(username, 0));
        userStat.setTotalSlidesCleaned(userStat.getTotalSlidesCleaned() + count);
        userStatRepository.save(userStat);
    }
}
