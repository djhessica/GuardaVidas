package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArquivoDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "O nome deve ser preenchido.")
    private String nome;

    @NotBlank(message = "O tipo deve ser preenchido.")
    private String tipo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tamanho;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String caminho;

}