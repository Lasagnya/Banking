package com.project.banking.repository;

import com.project.banking.domain.ClientInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionCallbackRepository extends JpaRepository<ClientInformation, Integer> {
	Optional<ClientInformation> findByTransactionId(int id);
}
