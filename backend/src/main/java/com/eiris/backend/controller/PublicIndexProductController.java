package com.eiris.backend.controller;

import com.eiris.backend.dto.response.IndexProductResponse;
import com.eiris.backend.service.IndexProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/index-products")
public class PublicIndexProductController {

    private final IndexProductService indexProductService;

    public PublicIndexProductController(IndexProductService indexProductService) {
        this.indexProductService = indexProductService;
    }

    @GetMapping
    public ResponseEntity<List<IndexProductResponse>> getProducts(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(indexProductService.getProductsByCategory(category));
        }
        return ResponseEntity.ok(indexProductService.getAllProducts());
    }

    @GetMapping("/latest-per-category")
    public ResponseEntity<List<IndexProductResponse>> getLatestPerCategory() {
        return ResponseEntity.ok(indexProductService.getLatestProductsPerCategory());
    }
}
