package org.example.lab07.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ServiceType type;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Invoice> invoices = new ArrayList<>();

    private BigDecimal currentPrice;

    public enum ServiceType {
        BASIC, PREMIUM, VIP, SPORTS, MOVIES
    }

}