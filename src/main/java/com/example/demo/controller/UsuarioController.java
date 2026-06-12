package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CadastroUsuarioDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<UsuarioDTO> {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService service) {
        super(service);
        this.usuarioService = service;
    }

    @PostMapping("/cadastrar-cpf")
    public CadastroUsuarioDTO cadastrarPorCpf(@RequestBody @Valid CadastroUsuarioDTO dto) {
        return usuarioService.criarPorCpf(dto);
    }
}
