package com.universite.inscriptions.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EtudiantResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String niveau;
    private String filiere;
}