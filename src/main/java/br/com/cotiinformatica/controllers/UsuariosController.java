package br.com.cotiinformatica.controllers;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequest;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponse;
import br.com.cotiinformatica.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    public UsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/criar")
    public ResponseEntity<CriarUsuarioResponse> criar(@RequestBody @Valid CriarUsuarioRequest request) {
        var response = usuarioService.criarUsuario(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<AutenticarUsuarioResponse> autenticar(@RequestBody @Valid AutenticarUsuarioRequest request) {
        var response = usuarioService.autenticarUsuario(request);
        return ResponseEntity.ok().body(response);
    }
}

