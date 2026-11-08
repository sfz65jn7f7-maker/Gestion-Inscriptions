package com.universite.inscriptions.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class ModuleResponse {
    private Long id;
    private String code;
    private String nom;
    private int nombrePlaces;
    private long placesRestantes; // Très pratique pour l'API !
    private String semestre;
    private String nomEnseignant;
    private Set<String> codesPrerequis;
}