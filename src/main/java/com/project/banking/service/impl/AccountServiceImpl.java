package com.project.banking.service.impl;

import com.project.banking.enumeration.Period;
import com.project.banking.model.database.Account;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.repository.AccountRepository;
import com.project.banking.service.AccountService;
import com.project.banking.util.DocumentsFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final DocumentsFunctionality documentsFunctionality;
	/** мэп, сопоставляющий id аккаунта с ReentrantLock */
	private final ConcurrentMap<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();
	/** мэп, сопоставляющий id банка с ReentrantLock */
	private final ConcurrentMap<Integer, ReentrantLock> bankLocks = new ConcurrentHashMap<>();

	@Autowired
	public AccountServiceImpl(AccountRepository accountRepository, @Lazy DocumentsFunctionality documentsFunctionality) {
		this.accountRepository = accountRepository;
		this.documentsFunctionality = documentsFunctionality;
	}

	@Override
	@Transactional
	public void payIn(final TransactionDb transaction) {
		findById(transaction.getReceivingBank()).ifPresent(account -> account.setBalance(account.getBalance() + transaction.getAmount()));
	}

	@Override
	@Transactional
	public void withdrawal(TransactionDb transaction) {
		findById(transaction.getSendingBank()).ifPresent(account -> account.setBalance(account.getBalance() - transaction.getAmount()));
	}

	@Override
	@Transactional
	public void transfer(TransactionDb transaction) {
		if (transaction.getReceivingBank() != 1) {
			ReentrantLock accountLock = accountLocks.computeIfAbsent(transaction.getSendingAccount(), k -> new ReentrantLock());
			ReentrantLock bankLock = bankLocks.computeIfAbsent(transaction.getReceivingBank(), k -> new ReentrantLock());
			takeLocks(accountLock, bankLock);
			try {
				payIn(transaction);
				withdrawal(transaction);
			} finally {
				accountLock.unlock();
				bankLock.unlock();
			}
		} else {
			payIn(transaction);
			withdrawal(transaction);
		}
	}

	private void takeLocks(ReentrantLock lock1, ReentrantLock lock2) {
		boolean firstLockTaken = false;
		boolean secondLockTaken = false;
		while (true) {
			try {
				firstLockTaken = lock1.tryLock();
				secondLockTaken = lock2.tryLock();
			} finally {
				if (firstLockTaken && secondLockTaken)
					return;
				if (firstLockTaken)
					lock1.unlock();
				if (secondLockTaken)
					lock2.unlock();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Optional<Account> findById(int id) {
		return accountRepository.findById(id);
	}

	@Override
	public List<Account> findByBank(int id) {
		return accountRepository.findByBankId(id);
	}

	@Override
	public boolean thisBank(int bankId, int accountId) {
		return findById(accountId).filter(value -> value.getBank().getId() == bankId).isPresent();
	}

	@Override
	public List<Account> findByUser(int id) {
		return accountRepository.findByUserId(id);
	}

	@Override
	public void save(Account account) {
		accountRepository.save(account);
	}

	@Override
	public void excerpt(Account account, Period period) {
		documentsFunctionality.excerpt(account, period);
	}

	@Override
	public void excerptInPDF(Account account, Period period) {
		documentsFunctionality.excerptInPDF(account,period);
	}
}
