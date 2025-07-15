package br.com.cotiinformatica.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    @PostMapping("/criar")
    public ResponseEntity<?> criar() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar() {
        return ResponseEntity.ok().build();
    }
}

