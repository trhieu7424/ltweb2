package com.ltweb2.shop.controller;

import com.ltweb2.shop.dto.OrderRequestDTO;
import com.ltweb2.shop.dto.OrderStatusRequestDTO;
import com.ltweb2.shop.entity.*;
import com.ltweb2.shop.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final CouponRepository couponRepository;

    // Đã sửa lỗi thiếu CouponRepository ở hàm khởi tạo này
    public OrderController(UserRepository userRepository, CartRepository cartRepository, 
                           OrderRepository orderRepository, InventoryRepository inventoryRepository,
                           CouponRepository couponRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.couponRepository = couponRepository;
    }

    @PostMapping("/place")
    @Transactional 
    public ResponseEntity<?> placeOrder(@RequestBody(required = false) OrderRequestDTO request, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy User"));

        // 1. Lấy giỏ hàng của User
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Giỏ hàng trống!"));

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Giỏ hàng của bạn đang trống, không thể đặt hàng!"));
        }

        // 2. Khởi tạo Đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        // 3. Duyệt qua từng sản phẩm trong giỏ để kiểm tra kho và tính tiền
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            
            // Kiểm tra tồn kho
            Inventory inventory = inventoryRepository.findByProductVariantId(variant.getId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy thông tin kho của sản phẩm " + variant.getSku()));

            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getSku() + " không đủ số lượng trong kho!");
            }

            // Trừ tồn kho
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);

            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(variant.getPrice());

            order.getItems().add(orderItem);

            // Cộng dồn tổng tiền (Số lượng * Giá)
            BigDecimal itemTotal = variant.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalOrderPrice = totalOrderPrice.add(itemTotal);
        }

        // --- BẮT ĐẦU XỬ LÝ MÃ GIẢM GIÁ ---
        if (request != null && request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(request.getCouponCode());
            
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                // Kiểm tra các điều kiện cơ bản
                if (coupon.getIsActive() && 
                    coupon.getExpiryDate().isAfter(LocalDateTime.now()) && 
                    coupon.getUsedCount() < coupon.getQuantity() &&
                    totalOrderPrice.compareTo(coupon.getMinOrderValue()) >= 0) {
                    
                    // Trừ tiền hóa đơn
                    totalOrderPrice = totalOrderPrice.subtract(coupon.getDiscountAmount());
                    // Đảm bảo tổng tiền không bị âm
                    if (totalOrderPrice.compareTo(BigDecimal.ZERO) < 0) {
                        totalOrderPrice = BigDecimal.ZERO;
                    }
                    
                    // Tăng số lượt sử dụng của mã
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                    couponRepository.save(coupon);
                } else {
                    throw new RuntimeException("Mã giảm giá không hợp lệ hoặc không đủ điều kiện áp dụng!");
                }
            }
        }
        // --- KẾT THÚC XỬ LÝ MÃ GIẢM GIÁ ---

        order.setTotalPrice(totalOrderPrice);
        
        // 4. Lưu đơn hàng
        orderRepository.save(order);

        // 5. Làm sạch giỏ hàng sau khi đặt thành công
        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(Map.of(
            "message", "Đặt hàng thành công!",
            "orderId", order.getId(),
            "finalPrice", order.getTotalPrice()
        ));
    }

    // Dành cho Admin/Staff cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // CHỐT CHẶN BẢO MẬT: Chỉ 2 role này mới được vào
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusRequestDTO request) {
        
        // 1. Tìm đơn hàng theo ID truyền trên URL
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn hàng mã số " + id));

        // 2. Cập nhật trạng thái
        order.setStatus(request.getStatus().toUpperCase());
        orderRepository.save(order);

        // 3. Trả về thông báo
        return ResponseEntity.ok(Map.of(
            "message", "Cập nhật trạng thái đơn hàng thành công!",
            "orderId", order.getId(),
            "newStatus", order.getStatus()
        ));
    }
}