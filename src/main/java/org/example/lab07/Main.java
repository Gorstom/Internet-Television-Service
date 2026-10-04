package org.example.lab07;

import lombok.RequiredArgsConstructor;
import org.example.lab07.model.PriceList;
import org.example.lab07.model.Subscription;
import org.example.lab07.repository.PriceListRepository;
import org.example.lab07.service.BillingService;
import org.example.lab07.service.ClientService;
import org.example.lab07.service.PaymentService;
import org.example.lab07.service.TimeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;


@SpringBootApplication
@RequiredArgsConstructor
@RestController // żeby wystawić endpoint testowy
public class Main implements CommandLineRunner {
    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }


    private final ClientService clientService;
    private final BillingService billingService;
    private final PriceListRepository priceListRepository;
    private final TimeService timeService;
    private final PaymentService paymentService;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("🟢 Aplikacja Spring Boot została uruchomiona.");

            // Możesz dodać tutaj dodatkową logikę weryfikacyjną
            // Na przykład sprawdzenie, czy niektóre Bean'y są poprawnie utworzone
            String[] beanNames = ctx.getBeanDefinitionNames();
            System.out.println("Liczba załadowanych beanów: " + beanNames.length);
        };
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @PostConstruct
    public void initPriceList() {
        if (priceListRepository.count() == 0) {
            createPriceEntry(Subscription.ServiceType.BASIC, new BigDecimal("99.99"));
            createPriceEntry(Subscription.ServiceType.PREMIUM, new BigDecimal("199.99"));
            createPriceEntry(Subscription.ServiceType.VIP, new BigDecimal("299.99"));
            createPriceEntry(Subscription.ServiceType.SPORTS, new BigDecimal("249.99"));
            createPriceEntry(Subscription.ServiceType.MOVIES, new BigDecimal("179.99"));
            System.out.println("Zainicjalizowano cennik startowy");
        }
    }

    private void createPriceEntry(Subscription.ServiceType type, BigDecimal price) {
        PriceList entry = new PriceList();
        entry.setServiceType(type);
        entry.setPrice(price);
        entry.setStartDate(LocalDate.now());
        priceListRepository.save(entry);
    }

    @Override
    public void run(String... args) {
        // Menu uruchamiane w osobnym wątku, żeby nie blokować tomcata
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    System.out.println("\n==== MENU GŁÓWNE ====");
                    System.out.println("1. Dodaj klienta");
                    System.out.println("2. Dodaj abonament");
                    System.out.println("3. Generuj faktury");
                    System.out.println("4. Pokaż faktury");
                    System.out.println("5. Przesuń czas");
                    System.out.println("6. Dodaj cennik");
                    System.out.println("7. Sprawdź zaległości");
                    System.out.println("8. Spłać fakturę");
                    System.out.println("9. Usuń fakturę");
                    System.out.println("10. Usuń klienta");
                    System.out.println("11. Usuń zakupiony abonament");
                    System.out.println("12. Pokaż klientów");
                    System.out.println("13. Pokaż zakupione abonamenty");
                    System.out.println("14. Wyjście");

                    System.out.print("Wybierz opcję: ");
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1 -> handleClientCreation(scanner);
                        case 2 -> handleSubscriptionCreation(scanner);
                        case 3 -> billingService.generateMonthlyInvoices();
                        case 4 -> billingService.displayAllInvoices();
                        case 5 -> handleTimeAdvance(scanner);
                        case 6 -> handlePriceListCreation(scanner);
                        case 7 -> billingService.checkOverdueInvoices();
                        case 8 -> paymentService.registerPayment();
                        case 9 -> handleInvoiceDeletion(scanner);
                        case 10 -> handleClientDeletion(scanner);
                        case 11 -> handleSubscriptionDeletion(scanner);
                        case 12 -> clientService.displayAllClients();
                        case 13 -> clientService.displayAllSubscriptions();
                        case 14 -> {
                            System.out.println("Koniec programu");
                            System.exit(0);
                        }
                        default -> System.out.println("Nieprawidłowa opcja!");
                    }

                } catch (NumberFormatException e) {
                    System.err.println("Błędny format liczby!");
                } catch (Exception e) {
                    System.err.println("Błąd: " + e.getMessage());
                }
            }
        }).start();
    }


    private void handleClientCreation(Scanner scanner) {
        System.out.print("Podaj imię: ");
        String firstName = scanner.nextLine();
        System.out.print("Podaj nazwisko: ");
        String lastName = scanner.nextLine();
        System.out.print("Podaj numer klienta: ");
        String clientNumber = scanner.nextLine();
        clientService.createClient(firstName, lastName, clientNumber);
    }

    private void handleSubscriptionCreation(Scanner scanner) {
        System.out.print("Podaj ID klienta: ");
        Long clientId = Long.parseLong(scanner.nextLine());
        System.out.println("Dostępne usługi: BASIC, PREMIUM, VIP, SPORTS, MOVIES");
        System.out.print("Wybierz typ usługi: ");
        String serviceType = scanner.nextLine().toUpperCase();
        clientService.createSubscription(clientId, Subscription.ServiceType.valueOf(serviceType));
    }

    private void handleTimeAdvance(Scanner scanner) {
        System.out.print("Podaj liczbę dni do przesunięcia: ");
        int days = Integer.parseInt(scanner.nextLine());
        timeService.advanceDays(days);
        System.out.println("Aktualna data systemowa: " + timeService.getCurrentDate());
    }

    private void handlePriceListCreation(Scanner scanner) {
        System.out.println("Dostępne usługi: BASIC, PREMIUM, VIP, SPORTS, MOVIES");
        System.out.print("Wybierz typ usługi: ");
        String serviceType = scanner.nextLine().toUpperCase();
        System.out.print("Podaj cenę: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());
        createPriceEntry(Subscription.ServiceType.valueOf(serviceType), price);
        System.out.println("Dodano nowy wpis do cennika!");
    }

    private void handleInvoiceDeletion(Scanner scanner) {
        System.out.print("Podaj ID faktury do usunięcia: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            billingService.deleteInvoice(id);
            System.out.println("Usunięto fakturę o ID " + id);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleClientDeletion(Scanner scanner) {
        System.out.print("Podaj ID klienta do usunięcia: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            clientService.deleteClient(id);
            System.out.println("Usunięto klienta o ID " + id);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleSubscriptionDeletion(Scanner scanner) {
        System.out.print("Podaj ID abonamentu do usunięcia: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            clientService.deleteSubscription(id);
            System.out.println("Usunięto abonament o ID " + id);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
