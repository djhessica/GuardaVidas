package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthDTO;
import com.example.demo.entity.Usuario;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController();
        ReflectionTestUtils.setField(controller, "usuarioRepository", usuarioRepository);
        ReflectionTestUtils.setField(controller, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
    }

    @Test
    void loginRetornaTokenQuandoSenhaCodificadaConfere() {
        AuthDTO dto = new AuthDTO("admin@email.com", "123456");
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "email", "admin@email.com");
        ReflectionTestUtils.setField(usuario, "senha", "hash");
        ReflectionTestUtils.setField(usuario, "nivelAcesso", NivelAcesso.ADMIN);
        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("admin@email.com", "ADMIN")).thenReturn("jwt-token");

        ResponseEntity<?> resposta = controller.login(dto);

        assertEquals(200, resposta.getStatusCode().value());
        assertEquals("jwt-token", ((Map<?, ?>) resposta.getBody()).get("token"));
        assertEquals("admin", ((Map<?, ?>) resposta.getBody()).get("tipo"));
    }

    @Test
    void loginAceitaSenhaEmTextoPuroQuandoArmazenadaIgual() {
        AuthDTO dto = new AuthDTO("user@email.com", "123456");
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "email", "user@email.com");
        ReflectionTestUtils.setField(usuario, "senha", "123456");
        ReflectionTestUtils.setField(usuario, "nivelAcesso", NivelAcesso.USUARIO);
        when(usuarioRepository.findByEmail("user@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "123456")).thenReturn(false);
        when(jwtUtil.generateToken("user@email.com", "USUARIO")).thenReturn("jwt-user");

        ResponseEntity<?> resposta = controller.login(dto);

        assertEquals(200, resposta.getStatusCode().value());
        assertEquals("usuario", ((Map<?, ?>) resposta.getBody()).get("tipo"));
    }

    @Test
    void loginRetornaUnauthorizedQuandoUsuarioNaoExisteOuSenhaFalha() {
        AuthDTO dto = new AuthDTO("nobody@email.com", "errada");
        when(usuarioRepository.findByEmail("nobody@email.com")).thenReturn(Optional.empty());

        ResponseEntity<?> resposta = controller.login(dto);

        assertEquals(401, resposta.getStatusCode().value());
        assertTrue(resposta.getBody().toString().contains("Credenciais"));
    }

    @Test
    void pingEValidarNaoLancamExcecao() {
        controller.pong();
    }
}
