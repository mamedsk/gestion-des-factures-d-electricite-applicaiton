package com.facture.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "bills")
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;


    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    private LocalDate paymentdate;

    @Column(nullable = false)
    private boolean paid = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    private double UserConsumption;

    // Constructor for initializing Bill with given parameters
    public Bill(double amount, LocalDate dueDate, User user) {
        this.amount = amount;
        this.dueDate = dueDate;
        this.user = user;
        this.paid = false; // Defaulting to unpaid when a new bill is created
    }
}
