package com.suktha.services.auth;
import com.suktha.dtos.SignupRequest;
import com.suktha.dtos.UserDTO;
import com.suktha.entity.User;
import com.suktha.enums.UserRole;
import com.suktha.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void createAnAdminAccount() {

        Optional<User> optionalUser = userRepository.findByUserRole(UserRole.ADMIN);
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setName("Admin");
            user.setEmail("admin@test.com");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setUserRole(UserRole.ADMIN);
            userRepository.save(user);
            log.info("Admin account is created successfully");
        } else {
            log.info("Admin account is already exist!");
        }
    }

    @Override
    public UserDTO signup(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setUserRole(UserRole.EMPLOYEE);
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        return userRepository.save(user).getUserDTO();

    }

    //i hange Boolean to boolean
    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }
}
