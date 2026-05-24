package com.persistencia.practica1.controllers;

import com.persistencia.practica1.dtos.AuthRequest;
import com.persistencia.practica1.dtos.AuthResponse;
import com.persistencia.practica1.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

// PASO 6: Clase REST Controller con endpoints de seguridad
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // Ruta a la que atacarás desde POSTMAN con tu usuario y contraseña
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // Spring Security comprueba que "admin" y "admin123" son correctos
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Acceso Denegado: Usuario o contraseña incorrectos");
        }

        // Si son correctos, cogemos el usuario y ordenamos a JwtTokenUtil que fabrique el Token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Devolvemos el JSON: { "token": "eyJhGci..." }
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
