package com.example.demo.dto;

import java.time.LocalDateTime;


import lombok.Data;


@Data


public class CheckoutResponseDTO {

    private String posto;
    private String tipo;
    private LocalDateTime data;
    private Long postoId;

}

