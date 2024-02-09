package com.project.banking.repository;

import com.project.banking.model.database.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findByBankId(int id);
	List<Account> findByUserId(int id);
}
