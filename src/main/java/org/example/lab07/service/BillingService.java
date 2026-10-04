package org.example.lab07.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lab07.model.*;
import org.example.lab07.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PriceListRepository priceListRepository;
    private final TimeService timeService;
    private static final Logger logger = LogManager.getLogger(BillingService.class);

    @Transactional
    public void generateMonthlyInvoices() {
        LocalDate currentDate = timeService.getCurrentDate();
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        if(subscriptions.isEmpty()) {
            logger.warn("Brak aktywnych abonamentów do fakturowania");
            return;
        }

        subscriptions.forEach(sub -> {
            PriceList price = priceListRepository.findFirstByServiceTypeAndStartDateLessThanEqualOrderByStartDateDesc(
                    sub.getType(), currentDate);

            if(price == null) {
                logger.error("Brak cennika dla usługi: {}", sub.getType());
                return;
            }

            Invoice invoice = new Invoice();
            invoice.setDueDate(currentDate.plusDays(14));
            invoice.setAmount(price.getPrice());
            invoice.setRemainingAmount(price.getPrice());
            invoice.setSubscription(sub);
            invoiceRepository.save(invoice);

            sub.setCurrentPrice(price.getPrice());
            subscriptionRepository.save(sub);

            logger.info("Wygenerowano fakturę dla abonamentu ID: {}", sub.getId());
        });
    }

    @Transactional(readOnly = true)
    public void displayAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        if (invoices.isEmpty()) {
            System.out.println("\nBrak faktur w systemie");
            return;
        }
        invoices.forEach(inv -> System.out.printf(
                "Faktura #%d – Kwota: %s, Pozostało: %s, Termin: %s, Status: %s%n",
                inv.getId(),
                inv.getAmount(),
                inv.getRemainingAmount(),
                inv.getDueDate(),
                inv.isPaid() ? "OPŁACONA" : "DO SPŁATY"
        ));
    }


    @Transactional
    public void checkOverdueInvoices() {
        List<Invoice> overdue = invoiceRepository.findByIsPaidFalseAndDueDateBefore(timeService.getCurrentDate());
        overdue.forEach(invoice -> {
            logger.warn("MONIT: Faktura {} zaległa od {} dni",
                    invoice.getId(),
                    timeService.getCurrentDate().getDayOfYear() - invoice.getDueDate().getDayOfYear()
            );
        });
    }

    @Transactional
    public void deleteInvoice(Long invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw new RuntimeException("Faktura o ID " + invoiceId + " nie istnieje");
        }
        invoiceRepository.deleteById(invoiceId);
    }
}