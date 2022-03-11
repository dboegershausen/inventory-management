package br.com.diogob.inventory.controllers;

import br.com.diogob.inventory.configs.security.JwtTokenUtil;
import br.com.diogob.inventory.models.JwtRequest;
import br.com.diogob.inventory.models.JwtResponse;
import br.com.diogob.inventory.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/authenticate")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Invalid credentials."));
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(token));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}