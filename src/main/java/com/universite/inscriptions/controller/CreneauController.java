package com.universite.inscriptions.controller;

import com.universite.inscriptions.dto.request.CreneauRequest;
import com.universite.inscriptions.dto.response.CreneauResponse;
import com.universite.inscriptions.service.CreneauService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creneaux")
@RequiredArgsConstructor
public class CreneauController {

    private final CreneauService creneauService;

    @PostMapping
    public ResponseEntity<CreneauResponse> creerCreneau(@Valid @RequestBody CreneauRequest request) {
        CreneauResponse response = creneauService.creerCreneau(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreneauResponse> obtenirCreneauParId(@PathVariable Long id) {
        return ResponseEntity.ok(creneauService.obtenirCreneauParId(id));
    }

    @GetMapping
    public ResponseEntity<List<CreneauResponse>> listerTousLesCreneaux() {
        return ResponseEntity.ok(creneauService.listerTousLesCreneaux());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCreneau(@PathVariable Long id) {
        creneauService.supprimerCreneau(id);
        return ResponseEntity.noContent().build();
    }
}
