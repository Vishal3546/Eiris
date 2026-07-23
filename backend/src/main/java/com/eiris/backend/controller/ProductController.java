package com.eiris.backend.controller;

import com.eiris.backend.dto.request.CreateProductRequest;
import com.eiris.backend.dto.request.UpdateProductRequest;
import com.eiris.backend.dto.response.ProductResponse;
import com.eiris.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;
    private final com.eiris.backend.service.S3StorageService s3StorageService;

    public ProductController(ProductService productService, com.eiris.backend.service.S3StorageService s3StorageService) {
        this.productService = productService;
        this.s3StorageService = s3StorageService;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<java.util.Map<String, String>> uploadImage(@RequestParam("image") org.springframework.web.multipart.MultipartFile image) {
        try {
            String url = s3StorageService.uploadImage(image);
            return ResponseEntity.ok(java.util.Map.of("url", url));
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "Failed to upload image"));
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
