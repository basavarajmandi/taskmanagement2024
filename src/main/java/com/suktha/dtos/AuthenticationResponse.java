package com.suktha.dtos;

import com.suktha.enums.UserRole;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Data
public class AuthenticationResponse {

    private String jwt;
    private Long userId;
    private UserRole userRole;
}
