package com.suktha.dtos;

import com.suktha.enums.UserRole;
import lombok.*;

@NoArgsConstructor
@ToString
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}
