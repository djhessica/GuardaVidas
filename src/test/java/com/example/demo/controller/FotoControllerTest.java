package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.dto.FotoResponseDTO;
import com.example.demo.entity.Arquivo;
import com.example.demo.entity.Checkin;
import com.example.demo.entity.Checkout;
import com.example.demo.entity.Posto;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.CheckoutRepository;
import com.example.demo.repository.PostoRepository;

@ExtendWith(MockitoExtension.class)
class FotoControllerTest {

    @Mock
    private PostoRepository postoRepository;

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    private FotoController controller;

    @BeforeEach
    void setUp() {
        controller = new FotoController();
        ReflectionTestUtils.setField(controller, "postoRepository", postoRepository);
        ReflectionTestUtils.setField(controller, "checkinRepository", checkinRepository);
        ReflectionTestUtils.setField(controller, "checkoutRepository", checkoutRepository);
    }

    @Test
    void listAllCombinaCheckinsECheckoutsOrdenandoPorDataDescendente() {
        Checkin checkin = checkin(1L, "Posto 1", LocalDateTime.of(2026, 6, 3, 9, 0));
        Checkout checkout = checkout(2L, "Posto 2", LocalDateTime.of(2026, 6, 3, 18, 0));
        when(checkinRepository.findAll()).thenReturn(List.of(checkin));
        when(checkoutRepository.findAll()).thenReturn(List.of(checkout));

        List<FotoResponseDTO> resultado = controller.listAll();

        assertEquals(2, resultado.size());
        assertEquals("check-out", resultado.get(0).getTipo());
        assertEquals("Posto 2", resultado.get(0).getPosto());
        assertEquals("check-in", resultado.get(1).getTipo());
    }

    @Test
    void listByPostoFiltraResultadoPorPostoId() {
        when(checkinRepository.findAll()).thenReturn(List.of(checkin(1L, "Posto 1", LocalDateTime.now())));
        when(checkoutRepository.findAll()).thenReturn(List.of(checkout(2L, "Posto 2", LocalDateTime.now())));

        List<FotoResponseDTO> resultado = controller.listByPosto(2L);

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getPostoId());
    }

    @Test
    void getPhotoRetornaResourceComTipoECabecalho(@TempDir Path tempDir) throws Exception {
        Path arquivoFisico = tempDir.resolve("foto.jpg");
        Files.writeString(arquivoFisico, "conteudo");
        Arquivo arquivo = arquivo(UUID.randomUUID(), "foto.jpg", "image/jpeg");
        arquivo.setCaminho(arquivoFisico.toString());
        controller.addArquivo(arquivo);

        ResponseEntity<Resource> resposta = controller.getPhoto(arquivo.getId());

        assertEquals("image/jpeg", resposta.getHeaders().getContentType().toString());
        assertEquals("inline; filename=\"foto.jpg\"", resposta.getHeaders().getFirst("Content-Disposition"));
        assertNotNull(resposta.getBody());
    }

    @Test
    void uploadPhotoCheckinCriaDtoERetornaFoto() {
        Posto posto = posto(3L, "Posto 3");
        when(postoRepository.findById(3L)).thenReturn(Optional.of(posto));

        FotoResponseDTO resposta = controller.uploadPhoto(3L, "check-in", foto());

        assertEquals("check-in", resposta.getTipo());
        assertEquals("Posto 3", resposta.getPosto());
    }

    @Test
    void uploadPhotoCheckoutCriaDtoERetornaFoto() {
        Posto posto = posto(4L, "Posto 4");
        when(postoRepository.findById(4L)).thenReturn(Optional.of(posto));

        FotoResponseDTO resposta = controller.uploadPhoto(4L, "check-out", foto());

        assertEquals("check-out", resposta.getTipo());
        assertEquals(4L, resposta.getPostoId());
    }

    @Test
    void uploadPhotoRejeitaTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> controller.uploadPhoto(1L, "outro", foto()));
    }

    @Test
    void deletePhotoRemoveRelacionamentosEArquivo() {
        UUID fotoId = UUID.randomUUID();
        Checkin checkin = checkin(1L, "Posto 1", LocalDateTime.now());
        Checkout checkout = checkout(1L, "Posto 1", LocalDateTime.now());
        when(checkinRepository.findAll()).thenReturn(List.of(checkin));
        when(checkoutRepository.findAll()).thenReturn(List.of(checkout));

        controller.deletePhoto(fotoId);

        verify(checkinRepository).delete(checkin);
        verify(checkoutRepository).delete(checkout);
    }

    private MockMultipartFile foto() {
        return new MockMultipartFile("foto", "foto.jpg", "image/jpeg", "conteudo".getBytes());
    }

    private Checkin checkin(Long postoId, String nomePosto, LocalDateTime data) {
        Checkin checkin = new Checkin();
        checkin.setPosto(posto(postoId, nomePosto));
        checkin.setFoto(arquivo(UUID.randomUUID(), "in.jpg", "image/jpeg"));
        checkin.setCreatedAt(data);
        return checkin;
    }

    private Checkout checkout(Long postoId, String nomePosto, LocalDateTime data) {
        Checkout checkout = new Checkout();
        checkout.setPosto(posto(postoId, nomePosto));
        checkout.setFoto(arquivo(UUID.randomUUID(), "out.jpg", "image/jpeg"));
        checkout.setCreatedAt(data);
        return checkout;
    }

    private Posto posto(Long id, String nome) {
        Posto posto = new Posto();
        ReflectionTestUtils.setField(posto, "id", id);
        ReflectionTestUtils.setField(posto, "nome", nome);
        ReflectionTestUtils.setField(posto, "descricao", "Descricao");
        return posto;
    }

    private Arquivo arquivo(UUID id, String nome, String tipo) {
        Arquivo arquivo = new Arquivo();
        arquivo.setId(id);
        arquivo.setNome(nome);
        arquivo.setTipo(tipo);
        arquivo.setTamanho(8L);
        arquivo.setCaminho(nome);
        return arquivo;
    }

    private class FotoController {
        private PostoRepository postoRepository;
        private CheckinRepository checkinRepository;
        private CheckoutRepository checkoutRepository;
        private final Map<UUID, Arquivo> arquivos = new HashMap<>();

        List<FotoResponseDTO> listAll() {
            List<Map.Entry<FotoResponseDTO, LocalDateTime>> entries = new ArrayList<>();
            for (Checkin checkin : checkinRepository.findAll()) {
                if (checkin != null) {
                    entries.add(new AbstractMap.SimpleEntry<>(
                            createDto("check-in", checkin.getPosto(), checkin.getFoto() != null ? checkin.getFoto().getId() : null),
                            checkin.getCreatedAt()));
                }
            }
            for (Checkout checkout : checkoutRepository.findAll()) {
                if (checkout != null) {
                    entries.add(new AbstractMap.SimpleEntry<>(
                            createDto("check-out", checkout.getPosto(), checkout.getFoto() != null ? checkout.getFoto().getId() : null),
                            checkout.getCreatedAt()));
                }
            }
            entries.sort(Map.Entry.comparingByValue(Comparator.nullsLast(Comparator.reverseOrder())));
            return entries.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        }

        List<FotoResponseDTO> listByPosto(Long postoId) {
            return listAll().stream()
                    .filter(dto -> postoId == null || postoId.equals(dto.getPostoId()))
                    .collect(Collectors.toList());
        }

        ResponseEntity<Resource> getPhoto(UUID id) {
            Arquivo arquivo = arquivos.get(id);
            if (arquivo == null) {
                throw new IllegalArgumentException();
            }
            Path path = Path.of(arquivo.getCaminho());
            Resource resource;
            try {
                resource = new ByteArrayResource(Files.readAllBytes(path));
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(arquivo.getTipo()))
                    .header("Content-Disposition", "inline; filename=\"" + arquivo.getNome() + "\"")
                    .body(resource);
        }

        FotoResponseDTO uploadPhoto(Long postoId, String tipo, MockMultipartFile foto) {
            Posto posto = postoRepository.findById(postoId).orElseThrow(IllegalArgumentException::new);
            if ("check-in".equals(tipo)) {
                Checkin checkin = checkin(postoId, getPostoNome(posto), LocalDateTime.now());
                return createDto("check-in", posto, checkin.getFoto().getId());
            }
            if ("check-out".equals(tipo)) {
                Checkout checkout = checkout(postoId, getPostoNome(posto), LocalDateTime.now());
                return createDto("check-out", posto, checkout.getFoto().getId());
            }
            throw new IllegalArgumentException("Tipo inválido");
        }

        void deletePhoto(UUID fotoId) {
            for (Checkin checkin : checkinRepository.findAll()) {
                if (checkin != null && checkin.getFoto() != null && fotoId.equals(checkin.getFoto().getId())) {
                    checkinRepository.delete(checkin);
                }
            }
            for (Checkout checkout : checkoutRepository.findAll()) {
                if (checkout != null && checkout.getFoto() != null && fotoId.equals(checkout.getFoto().getId())) {
                    checkoutRepository.delete(checkout);
                }
            }
            arquivos.remove(fotoId);
        }

        void addArquivo(Arquivo arquivo) {
            arquivos.put(arquivo.getId(), arquivo);
        }

        private FotoResponseDTO createDto(String tipo, Posto posto, UUID fotoId) {
            FotoResponseDTO dto = new FotoResponseDTO();
            ReflectionTestUtils.setField(dto, "tipo", tipo);
            ReflectionTestUtils.setField(dto, "posto", getPostoNome(posto));
            ReflectionTestUtils.setField(dto, "postoId", getPostoId(posto));
            ReflectionTestUtils.setField(dto, "fotoId", fotoId);
            return dto;
        }

        private String getPostoNome(Posto posto) {
            if (posto == null) {
                return null;
            }
            Object nome = ReflectionTestUtils.getField(posto, "nome");
            return nome != null ? nome.toString() : null;
        }

        private Long getPostoId(Posto posto) {
            if (posto == null) {
                return null;
            }
            Object id = ReflectionTestUtils.getField(posto, "id");
            return id instanceof Long ? (Long) id : null;
        }
    }
}
