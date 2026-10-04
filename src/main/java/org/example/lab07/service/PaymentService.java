package org.example.lab07.service;

import lombok.RequiredArgsConstructor;
import org.example.lab07.model.Invoice;
import org.example.lab07.model.Payment;
import org.example.lab07.repository.InvoiceRepository;
import org.example.lab07.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final Scanner scanner = new Scanner(System.in);

    public void registerPayment() {
        try {
            System.out.print("Podaj ID faktury: ");
            Long invoiceId = Long.parseLong(scanner.nextLine());

            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Faktura o ID " + invoiceId + " nie istnieje"));

            if (invoice.getRemainingAmount() == null) {
                invoice.setRemainingAmount(invoice.getAmount());
            }


            if (invoice.isPaid()) {
                System.out.println("Faktura została już opłacona.");
                return;
            }

            System.out.printf("Kwota całkowita: %s | Pozostało do zapłaty: %s%n",
                    invoice.getAmount(),
                    invoice.getRemainingAmount()
            );

            System.out.print("Podaj kwotę: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Kwota musi być większa niż zero.");
                return;
            }

            if (amount.compareTo(invoice.getRemainingAmount()) > 0) {
                System.out.printf("Kwota przekracza pozostałą należność! Można zapłacić maksymalnie: %s%n", invoice.getRemainingAmount());
                return;
            }

            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDate.now());
            payment.setInvoice(invoice);

            BigDecimal newRemaining = invoice.getRemainingAmount().subtract(amount);
            invoice.setRemainingAmount(newRemaining);

            if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
                invoice.setPaid(true);
                System.out.println("Faktura została w pełni opłacona!");
            } else {
                System.out.printf("Częściowa płatność zarejestrowana! Pozostało do zapłaty: %s%n", newRemaining);
            }
            paymentRepository.save(payment);
            invoiceRepository.save(invoice);

        } catch (NumberFormatException e) {
            System.err.println("Nieprawidłowy format liczby");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}