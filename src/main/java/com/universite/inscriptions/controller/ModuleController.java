package com.universite.inscriptions.controller;

import com.universite.inscriptions.dto.request.ModuleRequest;
import com.universite.inscriptions.dto.response.ModuleResponse;
import com.universite.inscriptions.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponse> creerModule(@Valid @RequestBody ModuleRequest request) {
        ModuleResponse response = moduleService.creerModule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> obtenirModuleParId(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.obtenirModuleParId(id));
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> listerTousLesModules() {
        return ResponseEntity.ok(moduleService.listerTousLesModules());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerModule(@PathVariable Long id) {
        moduleService.supprimerModule(id);
        return ResponseEntity.noContent().build();
    }
}