package com.universite.inscriptions.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscriptionRequest {
    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'ID du module est obligatoire")
    private Long moduleId;
}