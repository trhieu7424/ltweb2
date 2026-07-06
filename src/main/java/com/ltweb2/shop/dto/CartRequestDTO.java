package com.ltweb2.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDTO {
    private Long variantId; 
    private Integer quantity; 
}