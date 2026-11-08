package com.universite.inscriptions.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class CreneauResponse {
    private Long id;
    private String jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
}
