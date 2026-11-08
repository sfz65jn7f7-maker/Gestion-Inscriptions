package com.universite.inscriptions.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreneauRequest {
    @NotBlank(message = "Le jour est obligatoire")
    private String jour;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotBlank(message = "La salle est obligatoire")
    private String salle;

    @NotNull(message = "L'ID du module est obligatoire")
    private Long moduleId;
}
