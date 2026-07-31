package com.eiris.backend.dto.request;

import jakarta.validation.constraints.Min;

public class UpdateIndexProductRequest {

    private String name;
    private String category;

    @Min(value = 0, message = "Price must be positive")
    private Double price;

    @Min(value = 0, message = "Stock must be positive")
    private Integer stock;

    private String imageUrl;
    
    private String details;

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
}
