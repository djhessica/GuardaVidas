package com.example.demo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "arquivos")
@EqualsAndHashCode(callSuper = false)
public class Arquivo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Long tamanho;

    @Column(nullable = false, length = 500)
    private String caminho;

}
