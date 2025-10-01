package com.example.demo.controller;

import com.example.demo.model.dto.CachorroRequestDTO;
import com.example.demo.model.dto.CachorroResponseDTO;
import com.example.demo.service.CachorroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cachorros")
@CrossOrigin(origins = "*")
public class CachorroController {

    @Autowired
    private CachorroService service;

    @GetMapping
    public List<CachorroResponseDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> getById(@PathVariable Long id) {
        try {
            CachorroResponseDTO cachorro = service.findById(id);
            return ResponseEntity.ok(cachorro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CachorroResponseDTO create(@Valid @RequestBody CachorroRequestDTO request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CachorroRequestDTO request) {
        try {
            CachorroResponseDTO updated = service.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/nome")
    public List<CachorroResponseDTO> searchByNome(@RequestParam String nome) {
        return service.findByNome(nome);
    }

    @GetMapping("/search/raca")
    public List<CachorroResponseDTO> searchByRaca(@RequestParam String raca) {
        return service.findByRaca(raca);
    }
}