package com.ltweb2.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltweb2.shop.dto.ProductResponseDTO;
import com.ltweb2.shop.entity.Product;
import com.ltweb2.shop.entity.ProductVariant;
import com.ltweb2.shop.repository.ProductRepository;
import com.ltweb2.shop.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductController(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

@GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductResponseDTO> responseDTOs = products.stream().map(product -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setCategoryName(product.getCategory().getName());
            dto.setCategoryId(product.getCategory().getId());
            dto.setMainImage(product.getMainImage());
            dto.setDeleted(product.isDeleted());
            
            // CÁCH AN TOÀN NHẤT: Sao chép thủ công các Variant để ngắt vòng lặp
            if (product.getVariants() != null) {
                List<ProductVariant> safeVariants = product.getVariants().stream().map(v -> {
                    ProductVariant safeV = new ProductVariant();
                    safeV.setId(v.getId());
                    safeV.setColor(v.getColor());
                    safeV.setRam(v.getRam());
                    safeV.setRom(v.getRom());
                    safeV.setPrice(v.getPrice());
                    safeV.setSku(v.getSku());
                    // Tuyệt đối KHÔNG gán safeV.setProduct(product) ở đây
                    return safeV;
                }).collect(Collectors.toList());
                
                dto.setVariants(safeVariants);
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }
    
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("data") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        try {
            // 1. Chuyển chuỗi JSON thành Object 
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);

            // 2. KHẮC PHỤC LỖI Ở ĐÂY: Liên kết các phiên bản (Con) với sản phẩm (Cha)
            if (product.getVariants() != null) {
                for (ProductVariant variant : product.getVariants()) {
                    variant.setProduct(product); // Trỏ Con về Cha
                }
            }

            // 3. Xử lý lưu ảnh nếu có
            if (file != null && !file.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(file);
                product.setMainImage(imageUrl);
            }

            // 4. Lưu vào DB (Lúc này Spring Boot đã biết lưu product_id cho variant)
            Product savedProduct = productRepository.save(product);

            return ResponseEntity.ok(Map.of(
                "message", "Thêm sản phẩm thành công",
                "productId", savedProduct.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("data") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        try {
            // 1. Tìm sản phẩm cũ trong DB
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

            // 2. Chuyển JSON mới thành Object
            ObjectMapper objectMapper = new ObjectMapper();
            Product updatedData = objectMapper.readValue(productJson, Product.class);

            // 3. Cập nhật các thông tin cơ bản
            existingProduct.setName(updatedData.getName());
            existingProduct.setDescription(updatedData.getDescription());
            existingProduct.setCategory(updatedData.getCategory());

            // 4. Cập nhật các phiên bản (Variants)
            if (updatedData.getVariants() != null) {
                // Trỏ lại các biến thể mới về sản phẩm Cha
                updatedData.getVariants().forEach(v -> v.setProduct(existingProduct));
                // Xóa list cũ, nạp list mới vào để Hibernate tự động đồng bộ (nhờ orphanRemoval=true)
                existingProduct.getVariants().clear();
                existingProduct.getVariants().addAll(updatedData.getVariants());
            }

            // 5. Nếu có chọn ảnh mới thì lưu ảnh mới, không thì giữ nguyên ảnh cũ
            if (file != null && !file.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(file);
                existingProduct.setMainImage(imageUrl);
            }

            // 6. Lưu xuống DB
            productRepository.save(existingProduct);

            return ResponseEntity.ok(Map.of("message", "Cập nhật sản phẩm thành công"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
            try {
                Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
                
                product.setDeleted(true); // Chỉ đổi trạng thái, KHÔNG dùng deleteById
                productRepository.save(product);
                
                return ResponseEntity.ok(Map.of("message", "Đã chuyển vào thùng rác"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreProduct(@PathVariable Long id) {
        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            
            product.setDeleted(false); // Khôi phục trạng thái
            productRepository.save(product);
            
            return ResponseEntity.ok(Map.of("message", "Khôi phục thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }    
}