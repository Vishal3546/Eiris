package com.eiris.backend.service;

import com.eiris.backend.dto.request.CreateProductRequest;
import com.eiris.backend.dto.request.UpdateProductRequest;
import com.eiris.backend.dto.response.ProductResponse;
import com.eiris.backend.entity.Product;
import com.eiris.backend.mapper.ProductMapper;
import com.eiris.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final S3StorageService s3StorageService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, S3StorageService s3StorageService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.s3StorageService = s3StorageService;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            product.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getDetails() != null) {
            product.setDetails(request.getDetails());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            // Delete old image if it's changing
            if (product.getImageUrl() != null && !product.getImageUrl().equals(request.getImageUrl())) {
                s3StorageService.deleteImage(product.getImageUrl());
            }
            product.setImageUrl(request.getImageUrl());
        }
        
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        if (product.getImageUrl() != null) {
            s3StorageService.deleteImage(product.getImageUrl());
        }
        productRepository.deleteById(id);
    }
}
