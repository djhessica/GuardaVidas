package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.PostoDTO;
import com.example.demo.entity.Posto;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.PostoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwt;

    private String token;

    @Autowired
    private PostoRepository pr;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.objectMapper = new ObjectMapper();

        this.token = jwt.generateToken("tantofazcomotantofez@admin.com", NivelAcesso.ADMIN.toString());

        //pr.deleteAll();

    }

   @Test
    @DisplayName("Deve buscar por id")
    void buscarPorId() throws Exception {
        Posto posto = new Posto();

        posto.setNome("Posto para buscar por ID");
        posto.setDescricao("Posto buscavel");

        posto = pr.save(posto);

        mockMvc.perform(
                get("/postos/" + posto.getId())
            .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Posto para buscar por ID"));
    }

    @Test
    @DisplayName("Deve excluir por id")
    void excluirPorId() throws Exception {
        Posto posto = new Posto();

        posto.setNome("Posto para excluir");
        posto.setDescricao("Posto para exclusão");

        posto = pr.save(posto);

        mockMvc.perform(
                delete("/postos/" + posto.getId())
            .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertTrue(pr.findAll().isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar um posto existente")
    void atualizarPosto() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto original");
        posto.setDescricao("Descrição original");
        posto = pr.save(posto);

        PostoDTO postoDTO = new PostoDTO();
        postoDTO.setNome("Posto atualizado");
        postoDTO.setDescricao("Descrição atualizada");

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                put("/postos/" + posto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Posto atualizado"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"));
    }

    @Test
    @DisplayName("Deve retornar bad request ao atualizar com dados inválidos")
    void alterarPostoInvalido() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto incial");
        posto.setDescricao("Descrição inicial");
        posto = pr.save(posto);

        PostoDTO postoDTO = new PostoDTO();
        postoDTO.setNome(null);
        postoDTO.setDescricao(null);

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                put("/postos/" + posto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve criar um posto com sucesso")
    void criarPosto() throws Exception {
        PostoDTO postoDTO = new PostoDTO();

        postoDTO.setNome("Posto 12");
        postoDTO.setDescricao("Descrição do posto 12");

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                post("/postos").contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Posto 12"))
                .andExpect(jsonPath("$.id").exists());

    }

    @Test
    @DisplayName("Deve listar postos ativos")
    void listaPosto() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto para lista");
        posto.setDescricao("Descrição lista");
        pr.save(posto);

        mockMvc.perform(get("/postos")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].nome").value("Posto para lista"));
    }

     @Test
    @DisplayName("Deve dar badRequest")
    void criaPosto() throws Exception {
        PostoDTO postoDTO = new PostoDTO();

        postoDTO.setNome(null);
        postoDTO.setDescricao(null);

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                post("/postos").contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }


 @Test
    @DisplayName("Deve dar badRequest")
    void alterarPosto() throws Exception {
    

        mockMvc.perform(
                post("/postos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

}
