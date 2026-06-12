package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.entity.Usuario;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void createDefineNivelPadraoECodificaSenha() {
        UsuarioDTO dto = new UsuarioDTO(null, "user@email.com", "123456", null);
        when(passwordEncoder.encode("123456")).thenReturn("senha-codificada");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        UsuarioDTO resultado = service.create(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals(NivelAcesso.USUARIO, captor.getValue().getNivelAcesso());
        assertEquals("senha-codificada", captor.getValue().getSenha());
        assertEquals(1L, resultado.getId());
        assertEquals(NivelAcesso.USUARIO, resultado.getNivelAcesso());
    }

    @Test
    void updateMantemNivelInformadoENaoCodificaSenhaEmBranco() {
        UsuarioDTO dto = new UsuarioDTO(null, "admin@email.com", " ", NivelAcesso.ADMIN);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO resultado = service.update(8L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        verify(passwordEncoder, never()).encode(any());
        assertEquals(8L, captor.getValue().getId());
        assertEquals(NivelAcesso.ADMIN, resultado.getNivelAcesso());
    }
}
