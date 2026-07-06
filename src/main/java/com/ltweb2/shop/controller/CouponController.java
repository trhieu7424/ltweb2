package com.ltweb2.shop.controller;

import com.ltweb2.shop.dto.CouponRequestDTO;
import com.ltweb2.shop.entity.Coupon;
import com.ltweb2.shop.repository.CouponRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponRepository couponRepository;

    public CouponController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody CouponRequestDTO request) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(request.getCode());

        // 1. Kiểm tra mã có tồn tại không
        if (couponOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã giảm giá không tồn tại!"));
        }

        Coupon coupon = couponOpt.get();

        // 2. Kiểm tra trạng thái kích hoạt
        if (!coupon.getIsActive()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã giảm giá này đã bị vô hiệu hóa!"));
        }

        // 3. Kiểm tra hạn sử dụng
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã giảm giá đã hết hạn sử dụng!"));
        }

        // 4. Kiểm tra số lượng
        if (coupon.getUsedCount() >= coupon.getQuantity()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã giảm giá đã hết lượt sử dụng!"));
        }

        // 5. Kiểm tra điều kiện giá trị đơn hàng
        if (request.getOrderTotalValue().compareTo(coupon.getMinOrderValue()) < 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã này!",
                "minRequired", coupon.getMinOrderValue()
            ));
        }

        // Nếu qua hết các bài test trên, mã hợp lệ!
        return ResponseEntity.ok(Map.of(
            "status", "Hợp lệ",
            "message", "Áp dụng mã giảm giá thành công!",
            "discountAmount", coupon.getDiscountAmount()
        ));
    }
}