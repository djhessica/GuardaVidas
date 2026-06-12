package com.example.demo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarEmail(String destinatario, String titulo, String descricao) {
        // Implementar envio de email com dependências de e-mail quando estiverem configuradas.
        System.out.println("Enviando email para " + destinatario + " com título " + titulo);
    }

    public void enviarEmailFromTemplate(String destinatario, String titulo, String fileName) {
        String filePath = "src/main/resources/templates/email/" + fileName;
        String html;
        try {
            html = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler template de email", e);
        }

        // Implementar envio de email usando o conteúdo do template.
        System.out.println("Enviando email para " + destinatario + " com título " + titulo);
        System.out.println("Conteúdo do template: " + html);
    }
}
