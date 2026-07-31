package com.eiris.backend.dto.response;

import com.eiris.backend.entity.IndexProduct;
import java.time.ZonedDateTime;
import java.util.UUID;

public class IndexProductResponse {

    private UUID id;
    private String name;
    private String category;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String details;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public IndexProductResponse() {}

    public IndexProductResponse(IndexProduct product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
        this.details = product.getDetails();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
