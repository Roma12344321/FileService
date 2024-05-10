package com.martynov.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        return fileService.getFile(filename);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        fileService.deleteFile(filename);
        return ResponseEntity.ok("success");
    }

    @PostMapping
    public String saveFile(@RequestBody MultipartFile file) {
        return fileService.saveFile(file);
    }

    @ExceptionHandler(FileException.class)
    public Map<String, String> error(FileException e) {
        return Map.of("error", e.getMessage());
    }
}
