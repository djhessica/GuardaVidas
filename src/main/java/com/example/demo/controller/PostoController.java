package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PostoDTO;
import com.example.demo.service.PostoService;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController
@RequestMapping("/postos")

public class PostoController extends BaseController<PostoDTO>{

    public PostoController(PostoService service){
        super(service);
    } {

}
}
 



