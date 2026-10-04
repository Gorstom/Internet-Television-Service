package org.example.lab07.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SubAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String password;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription; // Relacja zwrotna
}