package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.customer.CustomerEntity;
import com.cdx.bas.application.bank.customer.CustomerRepository;
import com.cdx.bas.application.bank.transaction.TransactionMapper;
import com.cdx.bas.application.mapper.DtoEntityMapper;
import com.cdx.bas.application.bank.transaction.TransactionEntity;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.money.Money;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.account.BankAccountFactory;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class BankAccountMapper implements DtoEntityMapper<BankAccount, BankAccountEntity> {

    @Inject
    public BankAccountMapper(CustomerRepository customerRepository,
                             BankAccountRepository bankAccountRepository,
                             TransactionMapper transactionMapper) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionMapper = transactionMapper;
    }

    CustomerRepository customerRepository;
    BankAccountRepository bankAccountRepository;
    TransactionMapper transactionMapper;

    @Override
    public BankAccount toDto(BankAccountEntity entity) {

        if (entity == null || entity.getType() == null) {
            return null;
        }

        BankAccount dto = BankAccountFactory.createBankAccount(entity.getType());
        dto.setId(entity.getId());
        dto.setBalance(new Money(entity.getBalance()));

        dto.setCustomersId(entity.getCustomers().stream()
                .map(CustomerEntity::getId)
                .collect(Collectors.toSet()));

        dto.setIssuedTransactions(entity.getIssuedTransactions().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toSet()));

        return dto;
    }

    @Override
    public BankAccountEntity toEntity(BankAccount dto) {

        if (dto == null || dto.getId() == null) {
            return null;
        }

        BankAccountEntity entity = bankAccountRepository.findByIdOptional(dto.getId()).orElse(new BankAccountEntity());
        entity.setId(dto.getId());
        entity.setType(dto.getType());

        if (dto.getBalance() != null) {
            entity.setBalance(dto.getBalance().getAmount());
        } else {
            entity.setBalance(null);
        }

        entity.setCustomers(dto.getCustomersId().stream()
                .map(customerId -> customerRepository.findByIdOptional(customerId)
                        .orElseThrow(() -> new NoSuchElementException("Customer entity not found for id: " + customerId)))
                .collect(Collectors.toSet()));

        Set<TransactionEntity> newIssuedTransactions = new HashSet<>();
        for (Transaction issuedTransactionDto : dto.getIssuedTransactions()) {
            TransactionEntity newIssuedTransactionEntity = transactionMapper.toEntity(issuedTransactionDto);
            newIssuedTransactions.add(newIssuedTransactionEntity);
        }

        entity.setIssuedTransactions(newIssuedTransactions);
        return entity;
    }
}
