package com.eiris.backend.service;

import com.eiris.backend.dto.request.CreateIndexProductRequest;
import com.eiris.backend.dto.request.UpdateIndexProductRequest;
import com.eiris.backend.dto.response.IndexProductResponse;
import com.eiris.backend.entity.IndexProduct;
import com.eiris.backend.repository.IndexProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IndexProductService {

    private final IndexProductRepository indexProductRepository;
    private final S3StorageService s3StorageService;

    public IndexProductService(IndexProductRepository indexProductRepository, S3StorageService s3StorageService) {
        this.indexProductRepository = indexProductRepository;
        this.s3StorageService = s3StorageService;
    }

    public IndexProductResponse createProduct(CreateIndexProductRequest request) {
        IndexProduct product = new IndexProduct();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setDetails(request.getDetails());

        IndexProduct saved = indexProductRepository.save(product);
        return new IndexProductResponse(saved);
    }

    public List<IndexProductResponse> getAllProducts() {
        return indexProductRepository.findAll().stream()
                .map(IndexProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<IndexProductResponse> getProductsByCategory(String category) {
        return indexProductRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(IndexProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<IndexProductResponse> getLatestProductsPerCategory() {
        return indexProductRepository.findLatestProductsPerCategory().stream()
                .map(IndexProductResponse::new)
                .collect(Collectors.toList());
    }

    public IndexProductResponse getProductById(UUID id) {
        IndexProduct product = indexProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new IndexProductResponse(product);
    }

    public IndexProductResponse updateProduct(UUID id, UpdateIndexProductRequest request) {
        IndexProduct product = indexProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getDetails() != null) product.setDetails(request.getDetails());

        // If a new image is provided, delete the old one
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            if (product.getImageUrl() != null && !product.getImageUrl().equals(request.getImageUrl())) {
                s3StorageService.deleteImage(product.getImageUrl());
            }
            product.setImageUrl(request.getImageUrl());
        }

        IndexProduct saved = indexProductRepository.save(product);
        return new IndexProductResponse(saved);
    }

    public void deleteProduct(UUID id) {
        IndexProduct product = indexProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Delete the image from S3
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            s3StorageService.deleteImage(product.getImageUrl());
        }

        indexProductRepository.delete(product);
    }
}
