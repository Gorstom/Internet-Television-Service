package org.example.lab07.repository;

import org.example.lab07.model.PriceList;
import org.example.lab07.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    PriceList findFirstByServiceTypeAndStartDateLessThanEqualOrderByStartDateDesc(
            Subscription.ServiceType serviceType, LocalDate date);
}

