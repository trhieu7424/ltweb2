package com.ltweb2.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy token từ request
            String jwt = getJwtFromRequest(request);

            // Nếu có token và token hợp lệ
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Lấy email từ token
                String email = tokenProvider.getEmailFromToken(jwt);

                // Lấy thông tin user từ database
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Đưa thông tin vào Security Context (Báo cho Spring biết user này đã đăng nhập hợp lệ)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            System.out.println(">>> [BẢO MẬT] Không thể thiết lập xác thực người dùng: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Hàm bóc tách JWT từ Header (Bearer Token)
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ 7 ký tự "Bearer " để lấy đúng chuỗi token
        }
        return null;
    }
}