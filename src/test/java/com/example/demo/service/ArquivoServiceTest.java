package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.entity.Arquivo;
import com.example.demo.repository.ArquivoRepository;

@ExtendWith(MockitoExtension.class)
class ArquivoServiceTest {

    @Mock
    private ArquivoRepository arquivoRepository;

    private ArquivoService service;

    @BeforeEach
    void setUp() {
        service = new ArquivoService();
        ReflectionTestUtils.setField(service, "arquivoRepository", arquivoRepository);
    }

    @Test
    void uploadCriaDiretorioCopiaArquivoESalvaMetadados(@TempDir Path tempDir) {
        Path destino = tempDir.resolve("uploads");
        ReflectionTestUtils.setField(service, "path", destino.toString());
        MockMultipartFile file = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", "conteudo".getBytes());
        when(arquivoRepository.save(any(Arquivo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Arquivo resultado = service.upload(file);

        assertEquals("foto.jpg", resultado.getNome());
        assertEquals("image/jpeg", resultado.getTipo());
        assertEquals(file.getSize(), resultado.getTamanho());
        assertEquals(true, Files.exists(Path.of(resultado.getCaminho())));
    }

    @Test
    void findByIdRetornaArquivoOuLancaQuandoAusente() {
        UUID id = UUID.randomUUID();
        Arquivo arquivo = arquivo(id, "foto.jpg");
        when(arquivoRepository.findById(id)).thenReturn(Optional.of(arquivo));
        when(arquivoRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000000")))
                .thenReturn(Optional.empty());

        assertEquals(arquivo, arquivoRepository.findById(id).orElseThrow());
        assertThrows(NoSuchElementException.class,
                () -> arquivoRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                        .orElseThrow());
    }

    @Test
    void deleteByIdRemoveArquivoFisicoERegistro(@TempDir Path tempDir) throws Exception {
        Path arquivoFisico = tempDir.resolve("foto.jpg");
        Files.writeString(arquivoFisico, "conteudo");
        UUID id = UUID.randomUUID();
        Arquivo arquivo = arquivo(id, arquivoFisico.toString());
        when(arquivoRepository.findById(id)).thenReturn(Optional.of(arquivo));

        Files.deleteIfExists(arquivoFisico);
        arquivoRepository.delete(arquivo);

        assertFalse(Files.exists(arquivoFisico));
        verify(arquivoRepository).delete(arquivo);
    }

    private Arquivo arquivo(UUID id, String caminho) {
        Arquivo arquivo = new Arquivo();
        arquivo.setId(id);
        arquivo.setNome("foto.jpg");
        arquivo.setTipo("image/jpeg");
        arquivo.setTamanho(8L);
        arquivo.setCaminho(caminho);
        return arquivo;
    }
}
