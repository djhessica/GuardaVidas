package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.dto.CheckoutDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.entity.Arquivo;
import com.example.demo.entity.Checkout;
import com.example.demo.entity.Posto;
import com.example.demo.repository.CheckoutRepository;
import com.example.demo.repository.PostoRepository;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private PostoRepository postoRepository;

    @Mock
    private ArquivoService arquivoService;

    @Mock
    private CheckoutRepository checkoutRepository;

    private CheckoutService service;

    @BeforeEach
    void setUp() {
        service = new CheckoutService(postoRepository, arquivoService, checkoutRepository);
        ReflectionTestUtils.setField(service, "postoRepository", postoRepository);
        ReflectionTestUtils.setField(service, "arquivoService", arquivoService);
        ReflectionTestUtils.setField(service, "checkoutRepository", checkoutRepository);
    }

    @Test
    void checkoutEntityBuscaPostoFazUploadESalva() {
        CheckoutDTO dto = dto();
        Posto posto = posto();
        Arquivo arquivo = arquivo();
        when(postoRepository.findById(9L)).thenReturn(Optional.of(posto));
        when(arquivoService.upload(dto.getFoto())).thenReturn(arquivo);
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckoutResponseDTO resultado = service.checkout(dto);

        assertEquals("Posto 9", resultado.getPosto());
        assertEquals(9L, resultado.getPostoId());
        assertEquals("image/png", resultado.getTipo());
    }

    @Test
    void checkoutRetornaDadosDoRegistroSalvo() {
        CheckoutDTO dto = dto();
        Posto posto = posto();
        Arquivo arquivo = arquivo();
        LocalDateTime data = LocalDateTime.of(2026, 6, 3, 18, 0);
        when(postoRepository.findById(9L)).thenReturn(Optional.of(posto));
        when(arquivoService.upload(dto.getFoto())).thenReturn(arquivo);
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setCreatedAt(data);
            return checkout;
        });

        CheckoutResponseDTO resultado = service.checkout(dto);

        assertEquals("Posto 9", resultado.getPosto());
        assertEquals(9L, resultado.getPostoId());
        assertEquals("image/png", resultado.getTipo());
        assertEquals(data, resultado.getData());
    }

    private CheckoutDTO dto() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setPostoId(9L);
        dto.setFoto(new MockMultipartFile("foto", "foto.png", "image/png", "conteudo".getBytes()));
        return dto;
    }

    private Posto posto() {
        Posto posto = new Posto();
        ReflectionTestUtils.setField(posto, "nome", "Posto 9");
        ReflectionTestUtils.setField(posto, "descricao", "Descricao");
        ReflectionTestUtils.setField(posto, "id", 9L);
        return posto;
    }

    private Arquivo arquivo() {
        Arquivo arquivo = new Arquivo();
        arquivo.setTipo("image/png");
        arquivo.setNome("foto.png");
        arquivo.setTamanho(8L);
        arquivo.setCaminho("foto.png");
        return arquivo;
    }
}
