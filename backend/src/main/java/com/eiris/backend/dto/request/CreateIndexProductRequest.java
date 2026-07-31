package com.eiris.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateIndexProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;



    private String imageUrl;
    
    private String details;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }



    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
