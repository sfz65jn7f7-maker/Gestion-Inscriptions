package com.universite.inscriptions.controller;

import com.universite.inscriptions.dto.request.EtudiantRequest;
import com.universite.inscriptions.dto.response.EtudiantResponse;
import com.universite.inscriptions.service.EtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;

    @PostMapping
    public ResponseEntity<EtudiantResponse> creerEtudiant(@Valid @RequestBody EtudiantRequest request) {
        EtudiantResponse response = etudiantService.creerEtudiant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantResponse> obtenirEtudiantParId(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.obtenirEtudiantParId(id));
    }

    @GetMapping
    public ResponseEntity<List<EtudiantResponse>> listerTousLesEtudiants() {
        return ResponseEntity.ok(etudiantService.listerTousLesEtudiants());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEtudiant(@PathVariable Long id) {
        etudiantService.supprimerEtudiant(id);
        return ResponseEntity.noContent().build();
    }
}