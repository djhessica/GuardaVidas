package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.annotations.Public;
import com.example.demo.dto.CheckoutDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.service.CheckoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/check")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping("/out")
    @Public
    public CheckoutResponseDTO checkout(@ModelAttribute @Valid CheckoutDTO dto) {
        return checkoutService.checkout(dto);
    }

}
