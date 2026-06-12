package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.PostoDTO;
import com.example.demo.entity.Posto;
import com.example.demo.repository.PostoRepository;

@ExtendWith(MockitoExtension.class)
class PostoServiceTest {

    @Mock
    private PostoRepository repository;

    @Test
    void readOrdenaPostosPeloNumeroNoNome() {
        Posto posto10 = new Posto();
        posto10.setNome("Posto 10");
        posto10.setDescricao("Descricao 10");

        Posto semNumero = new Posto();
        semNumero.setNome("Central");
        semNumero.setDescricao("Sem numero");

        Posto nulo = new Posto();
        nulo.setNome(null);
        nulo.setDescricao("Nome nulo");

        Posto posto2 = new Posto();
        posto2.setNome("Posto 2");
        posto2.setDescricao("Descricao 2");
        when(repository.findAll()).thenReturn(List.of(posto10, semNumero, nulo, posto2));

        List<PostoDTO> resultado = new PostoService(repository).read();

        assertEquals("Posto 2", resultado.get(0).getNome());
        assertEquals("Posto 10", resultado.get(1).getNome());
        assertEquals("Central", resultado.get(2).getNome());
        assertEquals(null, resultado.get(3).getNome());
    }
}
