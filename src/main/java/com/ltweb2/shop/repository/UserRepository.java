package com.ltweb2.shop.repository;

import com.ltweb2.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Hàm này giúp Spring tự động tạo câu lệnh SQL tìm User theo email
    Optional<User> findByEmail(String email);
}