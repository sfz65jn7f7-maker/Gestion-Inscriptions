package com.universite.inscriptions.controller;

import com.universite.inscriptions.dto.request.EnseignantRequest;
import com.universite.inscriptions.dto.response.EnseignantResponse;
import com.universite.inscriptions.service.EnseignantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
public class EnseignantController {

    private final EnseignantService enseignantService;

    @PostMapping
    public ResponseEntity<EnseignantResponse> creerEnseignant(@Valid @RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.creerEnseignant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnseignantResponse> obtenirEnseignantParId(@PathVariable Long id) {
        return ResponseEntity.ok(enseignantService.obtenirEnseignantParId(id));
    }

    @GetMapping
    public ResponseEntity<List<EnseignantResponse>> listerTousLesEnseignants() {
        return ResponseEntity.ok(enseignantService.listerTousLesEnseignants());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEnseignant(@PathVariable Long id) {
        enseignantService.supprimerEnseignant(id);
        return ResponseEntity.noContent().build();
    }
}
