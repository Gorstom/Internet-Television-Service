package org.example.lab07.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dueDate;
    private BigDecimal amount;

    @Setter
    @Getter
    private BigDecimal remainingAmount;

    private boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Override
    public String toString() {
        return String.format("Faktura #%d | Kwota: %.2f PLN | Termin: %s | Status: %s",
                id, amount, dueDate, isPaid ? "OPŁACONA" : "NIEOBECNA");
    }
}