package com.universite.inscriptions.entity;

import com.universite.inscriptions.enums.StatutInscription;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"etudiant_id", "module_id"}) // Empêche un doublon d'inscription pour le même module
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    private StatutInscription statut; // EN_COURS, VALIDE, REFUSE, ANNULE

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}
