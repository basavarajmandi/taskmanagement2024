package com.suktha.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}
