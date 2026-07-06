package com.ltweb2.shop.controller;

import com.ltweb2.shop.dto.CartRequestDTO;
import com.ltweb2.shop.entity.Cart;
import com.ltweb2.shop.entity.CartItem;
import com.ltweb2.shop.entity.ProductVariant;
import com.ltweb2.shop.entity.User;
import com.ltweb2.shop.repository.CartRepository;
import com.ltweb2.shop.repository.ProductVariantRepository;
import com.ltweb2.shop.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    public CartController(CartRepository cartRepository, ProductVariantRepository variantRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
    }

    // API thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequestDTO request, Principal principal) {
        String email = principal.getName();
        
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy User"));

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Sản phẩm không tồn tại!"));

        // 3. Tìm giỏ hàng của User này 
        Cart cart = cartRepository.findByUserEmail(email).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // 4. Kiểm tra xem sản phẩm này đã có trong giỏ chưa
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);

        return ResponseEntity.ok(Map.of("message", "Đã thêm sản phẩm vào giỏ hàng thành công!"));
    }
}