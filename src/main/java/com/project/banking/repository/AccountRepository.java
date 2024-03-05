package com.project.banking.repository;

import com.project.banking.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findByBankId(int id);
	List<Account> findByUserId(int id);

	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Query("update Account a set a.balance=a.balance + (a.balance * ?1) where a.isPercents=true and a.bank.id=1")
	void chargePercents(Double percent);
}
