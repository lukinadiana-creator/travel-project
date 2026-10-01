package io.github.lukinadiana_creator.travel_planner.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/registration")
    public void createUser(@RequestParam @Email String email, @RequestParam @NotBlank @Size(min = 6, message = "Минимальная длина — 6 символов") String password, HttpServletRequest request) {
        String encodedPassword = passwordEncoder.encode(password);
        userService.createUser(email, encodedPassword);

        autoLogin(email, password, request);
    }

    @GetMapping("/account")
    public UserDto getUser(Authentication authentication) {
        return userService.getUser(authentication.getName());
    }

    @PutMapping("/account")
    public UserDto updateUsername(Authentication authentication, @RequestParam String name) {
        return userService.updateUsername(authentication.getName(), name);
    }

    private void autoLogin(String email, String password, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        userDetails,
                        password,
                        userDetails.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
