package org.example.lab07.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}