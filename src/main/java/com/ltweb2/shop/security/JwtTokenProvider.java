package com.ltweb2.shop.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Khóa bí mật dùng để ký Token (Bắt buộc phải dài ít nhất 32 ký tự)
    private final String JWT_SECRET = "DayLaMotChuoiBaoMatCucKyDaiDeMaHoaJWTChoDuAnThuongMaiDienTuCuaBan";
    
    // Thời gian sống của Token: 7 ngày (tính bằng milliseconds)
    private final long JWT_EXPIRATION = 604800000L;

    // Hàm tạo Key bảo mật từ chuỗi Secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // Hàm tạo ra chuỗi JWT từ Email
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(email) // Lưu email vào trong payload của Token
                .setIssuedAt(now) // Thời gian tạo
                .setExpiration(expiryDate) // Thời gian hết hạn
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Thuật toán mã hóa
                .compact(); // Đóng gói thành chuỗi String
    }

    // Hàm lấy email người dùng từ chuỗi Token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Hàm kiểm tra Token có hợp lệ hay không
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            System.out.println(">>> [BẢO MẬT] Lỗi JWT: " + ex.getMessage());
            return false;
        }
    }
}