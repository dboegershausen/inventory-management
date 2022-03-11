package br.com.diogob.configs.security;

import br.com.diogob.inventory.configs.security.JwtTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    @InjectMocks
    JwtTokenUtil jwtTokenUtil;

    @Mock
    UserDetails userDetails;

    @Test
    void should_generate_valid_token() {
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", "secret");
        when(userDetails.getUsername()).thenReturn("username");
        String token = jwtTokenUtil.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtTokenUtil.validateToken(token, userDetails));
        assertNotNull(jwtTokenUtil.getExpirationDateFromToken(token));
        assertNotNull(jwtTokenUtil.getClaimFromToken(token, claims -> claims));
        assertEquals("username", jwtTokenUtil.getUsernameFromToken(token));
    }

}
