package com.facture.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Person  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullname;
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    private String number;
    @Column(nullable = false, unique = true)
    private String cin;
    private String address;
    private LocalDate dateNaissance;



}
