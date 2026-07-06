package com.ltweb2.shop.repository;

import com.ltweb2.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserEmail(String email);
}