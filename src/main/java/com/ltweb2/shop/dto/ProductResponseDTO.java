package com.ltweb2.shop.dto;

import com.ltweb2.shop.entity.ProductVariant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Long categoryId; 
    private String mainImage; 
    private boolean deleted;  
    private List<ProductVariant> variants; 
}