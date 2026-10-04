package org.example.lab07.repository;

import org.example.lab07.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByClientNumber(String clientNumber);

    boolean existsByClientNumber(String clientNumber);
}
