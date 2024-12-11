package com.suktha.services.auth;

import com.suktha.dtos.SignupRequest;
import com.suktha.dtos.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {


   UserDTO signup(SignupRequest signupRequest);
    boolean hasUserWithEmail(String email);
}
