package com.ltweb2.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequestDTO {
    private String status; // "SHIPPING", "DELIVERED", "CANCELLED"
}