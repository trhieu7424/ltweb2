package com.ltweb2.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // Mã nhập vào (VD: GIAM20K, FREESHIP)

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount; // Số tiền được giảm

    @Column(name = "min_order_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderValue; // Điều kiện: Đơn hàng tối thiểu bao nhiêu thì được áp dụng

    @Column(nullable = false)
    private Integer quantity; // Số lượng mã phát hành

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0; // Số lượng mã đã được sử dụng

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate; // Ngày hết hạn

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}