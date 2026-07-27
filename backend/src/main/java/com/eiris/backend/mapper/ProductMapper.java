package com.eiris.backend.mapper;

import com.eiris.backend.dto.request.CreateProductRequest;
import com.eiris.backend.dto.response.ProductResponse;
import com.eiris.backend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        if (request == null) return null;
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return product;
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setPrice(entity.getPrice());
        response.setStock(entity.getStock());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
