package com.facture.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User extends Person{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numRooms=0;
    private int numPeople=0;
    private double houseArea=0.0;
    private boolean hasAC=false;
    private boolean hasTV=false;

    @Column(name = "ave_monthly_income", nullable = true)
    private double aveMonthlyIncome = 0.0;//error without the default value
    private boolean flat=false;
    private int numChildren=0;
    private boolean urban=false;


    public User(String name, String email, String password) {
        this.setFullname(name);
        this.setEmail(email);
        this.setPassword(password);
    }

    private Double userConsumption = 0.0;

}
