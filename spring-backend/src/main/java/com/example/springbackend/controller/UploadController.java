package com.example.springbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    @Value("${file.upload-dir:../uploads}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0)
            ext = filename.substring(i);
        String newName = UUID.randomUUID().toString() + ext;
        Path target = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(target);
        Path filePath = target.resolve(newName);
        Files.copy(file.getInputStream(), filePath);
        Map<String, String> resp = new HashMap<>();
        resp.put("url", "/uploads/" + newName);
        return ResponseEntity.ok(resp);
    }
}
