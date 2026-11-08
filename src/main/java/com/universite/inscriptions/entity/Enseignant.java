package com.universite.inscriptions.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "enseignants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Enseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String specialite;

    @OneToMany(mappedBy = "enseignant")
    private List<Module> modules;
}