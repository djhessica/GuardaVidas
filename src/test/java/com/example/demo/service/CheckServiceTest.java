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

import com.example.demo.dto.CheckinDTO;
import com.example.demo.dto.CheckinResponseDTO;
import com.example.demo.entity.Arquivo;
import com.example.demo.entity.Checkin;
import com.example.demo.entity.Posto;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.PostoRepository;

@ExtendWith(MockitoExtension.class)
class CheckServiceTest {

    @Mock
    private PostoRepository postoRepository;

    @Mock
    private ArquivoService arquivoService;

    @Mock
    private CheckinRepository checkinRepository;

    private CheckService service;

    @BeforeEach
    void setUp() {
        service = new CheckService();
        ReflectionTestUtils.setField(service, "postoRepository", postoRepository);
        ReflectionTestUtils.setField(service, "arquivoService", arquivoService);
        ReflectionTestUtils.setField(service, "checkinRepository", checkinRepository);
    }

    @Test
    void checkinEntityBuscaPostoFazUploadESalva() {
        CheckinDTO dto = dto();
        Posto posto = posto();
        Arquivo arquivo = arquivo();
        when(postoRepository.findById(4L)).thenReturn(Optional.of(posto));
        when(arquivoService.upload(dto.getFoto())).thenReturn(arquivo);
        when(checkinRepository.save(any(Checkin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckinResponseDTO resultado = service.checkin(dto);

        assertEquals("Posto 4", resultado.getPosto());
    }

    @Test
    void checkinRetornaPostoEHorarioDoRegistroSalvo() {
        CheckinDTO dto = dto();
        Posto posto = posto();
        Arquivo arquivo = arquivo();
        LocalDateTime horario = LocalDateTime.of(2026, 6, 3, 10, 30);
        when(postoRepository.findById(4L)).thenReturn(Optional.of(posto));
        when(arquivoService.upload(dto.getFoto())).thenReturn(arquivo);
        when(checkinRepository.save(any(Checkin.class))).thenAnswer(invocation -> {
            Checkin checkin = invocation.getArgument(0);
            checkin.setCreatedAt(horario);
            return checkin;
        });

        CheckinResponseDTO resultado = service.checkin(dto);

        assertEquals("Posto 4", resultado.getPosto());
        assertEquals(horario, resultado.getHorario());
    }

    private CheckinDTO dto() {
        CheckinDTO dto = new CheckinDTO();
        dto.setPostoId(4L);
        dto.setFoto(new MockMultipartFile("foto", "foto.jpg", "image/jpeg", "conteudo".getBytes()));
        return dto;
    }

    private Posto posto() {
        Posto posto = new Posto();
        posto.setNome("Posto 4");
        posto.setDescricao("Descricao");
        posto.setId(4L);
        return posto;
    }

    private Arquivo arquivo() {
        Arquivo arquivo = new Arquivo();
        arquivo.setTipo("image/jpeg");
        arquivo.setNome("foto.jpg");
        arquivo.setTamanho(8L);
        arquivo.setCaminho("foto.jpg");
        return arquivo;
    }
}
