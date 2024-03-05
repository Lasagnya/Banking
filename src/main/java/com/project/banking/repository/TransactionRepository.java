package com.project.banking.repository;

import com.project.banking.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	@Query("select t from Transaction t " +
			"where " +
			"((t.sendingBank=1 and t.sendingAccount=?1) " +
			"or " +
			"(t.receivingBank=1 and t.receivingAccount=?1)) " +
			"and t.time>?2" +
			"order by t.time desc")
	List<Transaction> getTransactionsByAccountForPeriod(int accountId, Date date);
}
