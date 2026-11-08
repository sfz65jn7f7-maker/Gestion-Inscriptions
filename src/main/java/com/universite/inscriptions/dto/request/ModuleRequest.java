package com.universite.inscriptions.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class ModuleRequest {
    @NotBlank(message = "Le code du module est obligatoire")
    private String code;

    @NotBlank(message = "Le nom du module est obligatoire")
    private String nom;

    @Min(value = 1, message = "Le nombre de places doit être supérieur à 0")
    private int nombrePlaces;

    private String semestre;

    private Long enseignantId; // ID de l'enseignant responsable

    private Set<Long> prerequisIds; // IDs des modules requis
}