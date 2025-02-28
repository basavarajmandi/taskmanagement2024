package com.suktha.controllers.auth;

import com.suktha.dtos.AuthenticationRequest;
import com.suktha.dtos.AuthenticationResponse;
import com.suktha.dtos.SignupRequest;
import com.suktha.dtos.UserDTO;
import com.suktha.entity.User;
import com.suktha.repositories.UserRepository;
import com.suktha.services.auth.AuthService;
import com.suktha.services.jwt.UserService;
import com.suktha.utiles.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    //chanage this method
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail()))
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("user alredy exist with the provided email");
        UserDTO userDto = authService.signup(signupRequest);
        if (userDto == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
//    UserDTO userDto = authService.signup(signupRequest);
//        if (userDto == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        //Authenticate user with provided credentials
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("username or password is incorrrect");
        }
        //Load user details by email
        final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser = userRepository.findFirstByEmail(authenticationRequest.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        AuthenticationResponse response = new AuthenticationResponse();
        // Generate JWT token if user is found then going
        if (optionalUser.isPresent()) {
            // Prepare response with JWT and user details
            response.setJwt(jwtToken);
            response.setUserRole(optionalUser.get().getUserRole());
            response.setUserId(optionalUser.get().getId());
        }
        return response;
    }
}
