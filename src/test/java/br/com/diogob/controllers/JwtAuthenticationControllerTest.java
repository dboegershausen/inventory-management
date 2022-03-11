package br.com.diogob.controllers;

import br.com.diogob.inventory.configs.security.JwtTokenUtil;
import br.com.diogob.inventory.controllers.JwtAuthenticationController;
import br.com.diogob.inventory.models.JwtRequest;
import br.com.diogob.inventory.services.JwtUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationControllerTest {

    @InjectMocks
    JwtAuthenticationController jwtAuthenticationController;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    JwtUserDetailsService jwtUserDetailsService;

    @Mock
    UserDetails userDetails;

    @Test
    void should_authenticate() {
        var jwtRequest = new JwtRequest("username", "password");
        var authentication = new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword());
        when(jwtUserDetailsService.loadUserByUsername(jwtRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("TOKEN");

        var jwtResponse = jwtAuthenticationController.createAuthenticationToken(jwtRequest);

        assertNotNull(jwtResponse.getBody());
        assertEquals(HttpStatus.OK, jwtResponse.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(authentication);
        verify(jwtUserDetailsService, times(1)).loadUserByUsername(jwtRequest.getUsername());
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void should_not_authenticate() {
        var jwtRequest = new JwtRequest("username", "passwordErrado");
        var authentication = new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword());
        when(authenticationManager.authenticate(authentication)).thenThrow(BadCredentialsException.class);

        var jwtResponse = jwtAuthenticationController.createAuthenticationToken(jwtRequest);

        assertEquals(HttpStatus.BAD_REQUEST, jwtResponse.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(authentication);
    }

}
