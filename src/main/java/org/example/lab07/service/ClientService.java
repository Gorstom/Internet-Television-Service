package org.example.lab07.service;

import lombok.RequiredArgsConstructor;
import org.example.lab07.model.Client;
import org.example.lab07.model.PriceList;
import org.example.lab07.model.Subscription;
import org.example.lab07.repository.ClientRepository;
import org.example.lab07.repository.PriceListRepository;
import org.example.lab07.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PriceListRepository priceListRepository;   // dodaj repozytorium cennika
    private final TimeService timeService;                   // potrzeba aktualnej daty

    @Transactional
    public void createClient(String firstName, String lastName, String clientNumber) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setClientNumber(clientNumber);
        client = clientRepository.save(client); // Zwróć zapisanego klienta
        System.out.println("Dodano klienta ID: " + client.getId());
    }

    @Transactional
    public void createSubscription(Long clientId, Subscription.ServiceType type) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Klient nie istnieje"));

        // 1. Pobierz ostatni wpis z cennika dla danego typu usługi
        PriceList priceEntry = priceListRepository
                .findFirstByServiceTypeAndStartDateLessThanEqualOrderByStartDateDesc(
                        type, timeService.getCurrentDate());
        if (priceEntry == null) {
            throw new RuntimeException("Brak cennika dla usługi " + type);
        }

        Subscription subscription = new Subscription();
        subscription.setType(type);
        subscription.setClient(client);
        subscription.setCurrentPrice(priceEntry.getPrice());

        subscriptionRepository.save(subscription);
        System.out.printf("Dodano abonament: %s, cena: %s%n", type, priceEntry.getPrice());
    }

    @Transactional
    public void deleteSubscription(Long subscriptionId) {
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new RuntimeException("Abonament o ID " + subscriptionId + " nie istnieje");
        }
        subscriptionRepository.deleteById(subscriptionId);
    }

    @Transactional
    public void deleteClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Klient o ID " + clientId + " nie istnieje");
        }
        clientRepository.deleteById(clientId);
    }

    @Transactional(readOnly = true)
    public void displayAllClients() {
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            System.out.println("Brak klientów w systemie.");
            return;
        }
        System.out.println("\n=== Lista klientów ===");
        clients.forEach(c ->
                System.out.printf("ID: %d | %s %s | Nr klienta: %s%n",
                        c.getId(), c.getFirstName(), c.getLastName(), c.getClientNumber())
        );
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }


    @Transactional(readOnly = true)
    public void displayAllSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findAll();
        if (subs.isEmpty()) {
            System.out.println("Brak abonamentów w systemie.");
            return;
        }
        System.out.println("\n=== Lista abonamentów ===");
        subs.forEach(s ->
                System.out.printf("ID: %d | Klient ID: %d | Usługa: %s | Cena bieżąca: %s%n",
                        s.getId(),
                        s.getClient().getId(),
                        s.getType(),
                        s.getCurrentPrice() != null ? s.getCurrentPrice() : "(brak)")
        );
    }
}
