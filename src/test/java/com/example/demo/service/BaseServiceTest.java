package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.PostoDTO;
import com.example.demo.entity.Posto;
import com.example.demo.repository.PostoRepository;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private PostoRepository repository;

    private TestPostoService service;

    @BeforeEach
    void setUp() {
        service = new TestPostoService(repository);
    }

    @Test
    void createConverteDtoSalvaEntidadeERetornaDto() {
        PostoDTO dto = new PostoDTO();
        dto.setNome("Posto 1");
        dto.setDescricao("Descricao 1");

        when(repository.save(any(Posto.class))).thenAnswer(invocation -> {
            Posto posto = invocation.getArgument(0);
            posto.setId(10L);
            return posto;
        });

        PostoDTO resultado = service.create(dto);

        ArgumentCaptor<Posto> captor = ArgumentCaptor.forClass(Posto.class);
        verify(repository).save(captor.capture());
        assertEquals("Posto 1", captor.getValue().getNome());
        assertEquals("Descricao 1", captor.getValue().getDescricao());
        assertEquals(10L, resultado.getId());
        assertEquals("Posto 1", resultado.getNome());
    }

    @Test
    void updateDefineIdAntesDeSalvar() {
        PostoDTO dto = new PostoDTO();
        dto.setNome("Posto atualizado");
        dto.setDescricao("Descricao atualizada");
        when(repository.save(any(Posto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostoDTO resultado = service.update(7L, dto);

        assertEquals(7L, resultado.getId());
        verify(repository).save(any(Posto.class));
    }

    @Test
    void readPorIdRetornaDtoQuandoEncontrado() {
        Posto posto = new Posto();
        posto.setNome("Posto 2");
        posto.setDescricao("Descricao 2");
        posto.setId(2L);
        when(repository.findById(2L)).thenReturn(Optional.of(posto));

        PostoDTO resultado = service.read(2L);

        assertEquals(2L, resultado.getId());
        assertEquals("Posto 2", resultado.getNome());
    }

    @Test
    void readPorIdLancaQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.read(99L));
    }

    @Test
    void readListaConverteTodasAsEntidades() {
        Posto posto3 = new Posto();
        posto3.setNome("Posto 3");
        posto3.setDescricao("Descricao 3");

        Posto posto4 = new Posto();
        posto4.setNome("Posto 4");
        posto4.setDescricao("Descricao 4");

        when(repository.findAll()).thenReturn(Arrays.asList(posto3, posto4));

        List<PostoDTO> resultado = service.read();

        assertEquals(2, resultado.size());
        assertEquals("Posto 3", resultado.get(0).getNome());
        assertEquals("Posto 4", resultado.get(1).getNome());
    }

    @Test
    void deleteESoftDeleteDelegamParaRepositorio() {
        service.delete(5L);
        service.softDelete(6L);

        verify(repository).deleteById(5L);
        verify(repository).softDeleteById(6L);
    }

    private static class TestPostoService extends BaseService<Posto, PostoDTO> {
        TestPostoService(PostoRepository repository) {
            super(repository);
        }
    }
}
