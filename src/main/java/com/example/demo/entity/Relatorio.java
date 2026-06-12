package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relatorios")
@EqualsAndHashCode(callSuper = false)
public class Relatorio extends BaseEntity {

    @Column(nullable = false)
    private Long posto;

    @Column(nullable = false)
    private Integer lesoesManha;

    @Column(nullable = false)
    private Integer lesoesTarde;

    @Column(nullable = false)
    private Integer prevencoesManha;

    @Column(nullable = false)
    private Integer prevencoesTarde;
}
