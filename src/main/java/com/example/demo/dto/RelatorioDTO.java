package com.example.demo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "O ID do posto é obrigatório")
    private Long posto;

    @NotNull(message = "As lesões da manhã são obrigatórias")
    private Integer lesoesManha;

    @NotNull(message = "As lesões da tarde são obrigatórias")
    private Integer lesoesTarde;

    @NotNull(message = "As prevenções da manhã são obrigatórias")
    private Integer prevencoesManha;

    @NotNull(message = "As prevenções da tarde são obrigatórias")
    private Integer prevencoesTarde;

    @JsonProperty(value = "data", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
