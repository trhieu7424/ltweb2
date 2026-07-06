package com.ltweb2.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String size;

    // Quan hệ 1-1 với Inventory (Mỗi biến thể có một bản ghi kho riêng)
    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;

    @Column(length = 50)
    private String color; 

    @Column(length = 20)
    private String ram; // Ví dụ: 8GB, 12GB

    @Column(length = 20)
    private String rom;
}