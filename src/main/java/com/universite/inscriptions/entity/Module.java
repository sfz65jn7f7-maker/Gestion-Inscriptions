package com.universite.inscriptions.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "modules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Ex: INF301

    private String nom;

    @Column(nullable = false)
    private int nombrePlaces; // Capacité maximale

    private String semestre; // Ex: S1, S2

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;

    // Relation auto-référencée : un module peut exiger d'autres modules en prérequis
    @ManyToMany
    @JoinTable(
            name = "module_prerequis",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequis_id")
    )
    @Builder.Default
    private Set<Module> prerequis = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Creneau> creneaux;
}