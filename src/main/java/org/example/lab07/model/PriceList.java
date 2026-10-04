package org.example.lab07.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class PriceList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Subscription.ServiceType serviceType;

    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
}