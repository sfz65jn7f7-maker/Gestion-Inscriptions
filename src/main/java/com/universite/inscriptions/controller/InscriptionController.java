package com.universite.inscriptions.controller;

import com.universite.inscriptions.dto.request.InscriptionRequest;
import com.universite.inscriptions.dto.response.InscriptionResponse;
import com.universite.inscriptions.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @PostMapping
    public ResponseEntity<InscriptionResponse> inscrireEtudiant(@Valid @RequestBody InscriptionRequest request) {
        InscriptionResponse response = inscriptionService.inscrireEtudiant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<InscriptionResponse>> getInscriptionsParEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsParEtudiant(etudiantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerInscription(@PathVariable Long id) {
        inscriptionService.supprimerInscription(id);
        return ResponseEntity.noContent().build();
    }
}