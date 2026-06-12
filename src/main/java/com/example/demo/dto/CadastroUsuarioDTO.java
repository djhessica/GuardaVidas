package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroUsuarioDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome deve ser preenchido.")
    private String nome;

    @NotBlank(message = "O email deve ser preenchido.")
    @jakarta.validation.constraints.Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "O CPF deve ser preenchido.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
    private String cpf;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String senhaGerada;
}
