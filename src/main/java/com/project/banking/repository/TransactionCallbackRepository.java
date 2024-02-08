package com.project.banking.repository;

import com.project.banking.model.database.TransactionCallbackDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCallbackRepository extends JpaRepository<TransactionCallbackDb, Integer> {
}
