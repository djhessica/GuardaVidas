package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class FotoResponseDTO {

    private UUID id;
    private Long postoId;
    private String posto;
    private String tipo;
    private String url;
    private LocalDateTime data;
}
