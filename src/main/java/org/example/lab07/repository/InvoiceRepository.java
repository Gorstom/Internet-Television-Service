package org.example.lab07.repository;

import org.example.lab07.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findBySubscriptionId(Long subscriptionId);

    List<Invoice> findByIsPaidFalseAndDueDateBefore(LocalDate date);

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.subscription.client.id = :clientId AND i.isPaid = false")
    BigDecimal sumUnpaidAmountByClientId(Long clientId);
}
