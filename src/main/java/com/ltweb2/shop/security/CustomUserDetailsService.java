package com.ltweb2.shop.security;

import com.ltweb2.shop.entity.User;
import com.ltweb2.shop.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(">>> [BẢO MẬT] Đang thử đăng nhập với email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println(">>> [BẢO MẬT] LỖI: Không tìm thấy email " + email + " trong Database!");
                    return new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email);
                });

        System.out.println(">>> [BẢO MẬT] Đã tìm thấy User trong DB: " + user.getFullName() + " | Trạng thái active: " + user.getIsActive());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive() != null ? user.getIsActive() : false, 
                true, true, true,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}