package com.universite.inscriptions.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "creneaux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jour; // Ex: LUNDI, MARDI...
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}