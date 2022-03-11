package br.com.diogob.configs.security;

import br.com.diogob.inventory.configs.security.JwtRequestFilter;
import br.com.diogob.inventory.configs.security.JwtTokenUtil;
import br.com.diogob.inventory.services.JwtUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterlTest {

    @InjectMocks
    JwtRequestFilter jwtRequestFilter;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    JwtUserDetailsService jwtUserDetailsService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Test
    void should_do_filter() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzUxMiJ9." +
                "eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY0NzAxODM3NiwiaWF0IjoxNjQ3MDAwMzc2fQ." +
                "umTmLtCNJNVf48-Oj6SW9xNN0u-85MsWTF5Ei9POiwXITyiEiK3wu1rRckmRwwOTLiR2s0RDGWxHDWM6aOc3MA");
        jwtRequestFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

}
