package org.example.lab07.controller;

import org.example.lab07.model.Client;
import org.example.lab07.model.Subscription;
import org.example.lab07.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<String> createClient(@RequestBody Map<String, String> request) {
        try {
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String clientNumber = request.get("clientNumber");

            clientService.createClient(firstName, lastName, clientNumber);
            return ResponseEntity.ok("Klient został utworzony");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Błąd: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.ok("Klient o ID " + id + " został usunięty");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Błąd: " + e.getMessage());
        }
    }

    @PostMapping("/{clientId}/subscriptions")
    public ResponseEntity<String> createSubscription(
            @PathVariable Long clientId,
            @RequestBody Map<String, String> request) {
        try {
            String serviceType = request.get("type");
            Subscription.ServiceType type = Subscription.ServiceType.valueOf(serviceType.toUpperCase());

            clientService.createSubscription(clientId, type);
            return ResponseEntity.ok("Subskrypcja została utworzona");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Błąd: " + e.getMessage());
        }
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<String> getAllSubscriptions() {
        try {
            clientService.displayAllSubscriptions();
            return ResponseEntity.ok("Lista subskrypcji wyświetlona w konsoli");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd: " + e.getMessage());
        }
    }

    @DeleteMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<String> deleteSubscription(@PathVariable Long subscriptionId) {
        try {
            clientService.deleteSubscription(subscriptionId);
            return ResponseEntity.ok("Subskrypcja o ID " + subscriptionId + " została usunięta");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Błąd: " + e.getMessage());
        }
    }
}