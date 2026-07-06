package com.ltweb2.shop.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CouponRequestDTO {
    private String code;
    private BigDecimal orderTotalValue; 
}