package com.universite.inscriptions.dto.response;

import com.universite.inscriptions.enums.StatutInscription;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class InscriptionResponse {
    private Long id;
    private LocalDate dateInscription;
    private StatutInscription statut;
    private String nomEtudiant;
    private String prenomEtudiant;
    private String codeModule;
    private String nomModule;
}