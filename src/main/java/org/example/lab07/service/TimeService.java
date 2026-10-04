package org.example.lab07.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TimeService {
    private LocalDate simulatedDate = LocalDate.now();

    public LocalDate getCurrentDate() {
        return simulatedDate;
    }

    public void advanceDays(int days) {
        simulatedDate = simulatedDate.plusDays(days);
    }
}