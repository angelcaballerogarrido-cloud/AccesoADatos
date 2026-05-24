package com.persistencia.practica1.controllers;

import com.persistencia.practica1.entities.User;
import com.persistencia.practica1.security.UserDetailsImpl;
import com.persistencia.practica1.security.UserDetailsServiceImpl;
import com.persistencia.practica1.services.UserService;
import com.persistencia.practica1.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    // DTOs para requests y responses
    public static class LoginRequest {
        @Valid
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
        public Set<String> roles;
    }

    public static class AuthResponse {
        public String token;
        public String type = "Bearer";
        public String username;
        public String email;
        public Set<String> roles;

        public AuthResponse(String token, String username, String email, Set<String> roles) {
            this.token = token;
            this.username = username;
            this.email = email;
            this.roles = roles;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String token = jwtTokenUtil.generateToken(userDetails.getUsername(), roles);

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    userDetails.getUsername(),
                    userDetails.getUser().getEmail(),
                    roles
            ));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Protección extra: Si nos mandan nulo O nos mandan un array vacío [], forzamos a que sean ROLE_USER
            Set<String> roles = (registerRequest.roles != null && !registerRequest.roles.isEmpty()) 
                                ? registerRequest.roles 
                                : Set.of("ROLE_USER");

            User user = userService.createUser(
                    registerRequest.username,
                    registerRequest.password,
                    registerRequest.email,
                    roles
            );

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequest.username, registerRequest.password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Set<String> userRoles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());

            String token = jwtTokenUtil.generateToken(registerRequest.username, userRoles);

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    userRoles
            ));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        // CORRECCIÓN CRÍTICA: authentication.getPrincipal() devuelve un objeto UserDetails, no un String.
        // Usamos authentication.getName() que extrae el String de forma segura.
        String username = authentication.getName();
        
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);

        Set<String> userRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("password", userDetails.getPassword());
        userInfo.put("roles", userRoles);

        return ResponseEntity.ok(userInfo);
    }
}
