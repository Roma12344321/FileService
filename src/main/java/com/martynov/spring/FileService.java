package com.martynov.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    @Value("${pathDir}")
    private String pathDir;

    public ResponseEntity<Resource> getFile(String filename) {
        try {
            Path filePath = Paths.get(pathDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileException("File is empty");
        }
        try {
            String uploadDir = pathDir;
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "-" + (originalFilename != null ? originalFilename : "file");
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/file/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException e) {
            throw new FileException("file can not be saved");
        }
    }

    public void deleteFile(String filename) {
        Path path = Paths.get(pathDir).resolve(filename).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileException("File can not be deleted");
        }
    }
}

