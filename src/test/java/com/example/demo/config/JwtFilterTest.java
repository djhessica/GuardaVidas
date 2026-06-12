package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void autenticaUsuarioQuandoTokenBearerValido() throws Exception {
        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUsername("token")).thenReturn("user@email.com");
        when(jwtUtil.extractRole("token")).thenReturn("ADMIN");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        new JwtFilter(jwtUtil).doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals("user@email.com", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals("ROLE_ADMIN", SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void naoAutenticaQuandoHeaderAusenteOuTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        new JwtFilter(jwtUtil).doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValidoSemRoleCriaAutenticacaoSemAutoridades() throws Exception {
        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUsername("token")).thenReturn("user@email.com");
        when(jwtUtil.extractRole("token")).thenReturn(null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        new JwtFilter(jwtUtil).doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals(0, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }
}
