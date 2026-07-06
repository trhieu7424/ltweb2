package com.ltweb2.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String fullName;
    private String phone;
}