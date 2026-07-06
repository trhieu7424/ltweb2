package com.ltweb2.shop.repository;

import com.ltweb2.shop.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductVariantId(Long variantId);
}