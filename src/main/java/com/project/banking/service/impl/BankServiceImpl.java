package com.project.banking.service.impl;

import com.project.banking.domain.Bank;
import com.project.banking.repository.BankRepository;
import com.project.banking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService {
	private final BankRepository bankRepository;

	@Autowired
	public BankServiceImpl(BankRepository bankRepository) {
		this.bankRepository = bankRepository;
	}

	@Override
	public List<Bank> findAll() {
		return bankRepository.findAll();
	}

	@Override
	public Optional<Bank> findById(int id) {
		return bankRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		bankRepository.deleteById(id);
	}

	@Override
	public void update(Bank updatedBank) {
		bankRepository.save(updatedBank);
	}

	@Override
	public void save(Bank bank) {
		bankRepository.save(bank);
	}
}
