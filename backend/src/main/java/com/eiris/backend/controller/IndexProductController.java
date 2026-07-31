package com.eiris.backend.controller;

import com.eiris.backend.dto.request.CreateIndexProductRequest;
import com.eiris.backend.dto.request.UpdateIndexProductRequest;
import com.eiris.backend.dto.response.IndexProductResponse;
import com.eiris.backend.service.IndexProductService;
import com.eiris.backend.service.S3StorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/index-products")
public class IndexProductController {

    private final IndexProductService indexProductService;
    private final S3StorageService s3StorageService;

    public IndexProductController(IndexProductService indexProductService, S3StorageService s3StorageService) {
        this.indexProductService = indexProductService;
        this.s3StorageService = s3StorageService;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String url = s3StorageService.uploadImage(image, "index_products");
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload image"));
        }
    }

    @PostMapping
    public ResponseEntity<IndexProductResponse> createProduct(@Valid @RequestBody CreateIndexProductRequest request) {
        return ResponseEntity.ok(indexProductService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<IndexProductResponse>> getAllProducts() {
        return ResponseEntity.ok(indexProductService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndexProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(indexProductService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndexProductResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateIndexProductRequest request) {
        return ResponseEntity.ok(indexProductService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        indexProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
