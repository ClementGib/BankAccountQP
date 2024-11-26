package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.customer.CustomerEntity;
import com.cdx.bas.application.bank.customer.CustomerMapper;
import com.cdx.bas.application.bank.customer.CustomerRepository;
import com.cdx.bas.application.bank.transaction.TransactionEntity;
import com.cdx.bas.application.bank.transaction.TransactionMapper;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.CREDIT;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountMapperTest {

    @Mock
    CustomerMapper customerMapper;

    @Mock
    TransactionMapper transactionMapper;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    BankAccountMapper bankAccountMapper;

    @Test
    void toDto_shouldReturnNullDto_whenEntityIsNull() {
        BankAccount dto = bankAccountMapper.toDto(null);

        assertThat(dto).isNull();

        verifyNoInteractions(customerMapper);
    }

    @Test
    void toEntity_shouldReturnNullEntity_whenDtoIsNull() {
        // Act
        BankAccountEntity entity = bankAccountMapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
        verifyNoInteractions(customerMapper);
    }

    @Test
    void toDto_shouldReturnNullDto_whenEntityHasEmptyObject() {
        // Act
        BankAccount dto = bankAccountMapper.toDto(new BankAccountEntity());

        // Assert
        assertThat(dto).isNull();
        verifyNoInteractions(customerMapper);
    }

    @Test
    void toDto_shouldMapAccountTypeOnly_whenEntityHasAccount_withOnlyAccountType() {
        // Arrange
        BankAccountEntity entity = new BankAccountEntity();
        entity.setType(AccountType.CHECKING);

        // Act
        BankAccount dto = bankAccountMapper.toDto(entity);

        // Assert
        assertThat(dto.getId()).isNull();
        assertThat(dto.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(dto.getBalance()).usingRecursiveComparison().isEqualTo(new Money(null));
        assertThat(dto.getCustomersId()).isEmpty();
        assertThat(dto.getIssuedTransactions()).isEmpty();
        verifyNoInteractions(customerMapper);
    }

    @Test
    void toEntity_shouldMapNullValues_whenDtoHasAccount_withOnlyAccountTypeAndId() {
        // Arrange
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(1L);

        // Act
        BankAccountEntity entity = bankAccountMapper.toEntity(bankAccount);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(entity.getBalance()).isNull();
        assertThat(entity.getCustomers()).isEmpty();
        assertThat(entity.getIssuedTransactions()).isEmpty();
        verifyNoInteractions(customerMapper);
    }

    @Test
    void toDto_shouldMapEveryFieldsOfDto_whenEntityHasValues() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(10L);
        entity.setType(AccountType.SAVING);
        entity.setBalance(new BigDecimal("1000"));

        Set<CustomerEntity> customers = new HashSet<>();
        CustomerEntity customerEntity = createCustomerEntityUtils();
        customers.add(customerEntity);
        entity.setCustomers(customers);

        Set<TransactionEntity> issuedTransactionEntities = new HashSet<>();
        TransactionEntity issuedTransactionEntity1 = getTransactionEntity(1L, timestamp);
        TransactionEntity issuedTransactionEntity2 = getTransactionEntity(2L, timestamp);
        issuedTransactionEntities.add(issuedTransactionEntity1);
        issuedTransactionEntities.add(issuedTransactionEntity2);
        entity.setIssuedTransactions(issuedTransactionEntities);

        Set<TransactionEntity> incomingTransactionEntities = new HashSet<>();
        TransactionEntity incomingTransactionEntity = getTransactionEntity(3L, timestamp);
        incomingTransactionEntities.add(incomingTransactionEntity);
        entity.setIncomingTransactions(incomingTransactionEntities);

        Transaction issuedTransaction1 = getTransaction(1L, 10L, 15L, timestamp);
        Transaction issuedTransaction2 = getTransaction(2L, 10L, 20L, timestamp);
        Transaction incomingTransaction = getTransaction(3L, 30L, 10L, timestamp);

        when(transactionMapper.toDto(issuedTransactionEntity1)).thenReturn(issuedTransaction1);
        when(transactionMapper.toDto(issuedTransactionEntity2)).thenReturn(issuedTransaction2);
        when(transactionMapper.toDto(incomingTransactionEntity)).thenReturn(incomingTransaction);

        BankAccount expectedBankAccount = new SavingBankAccount();
        expectedBankAccount.setId(10L);
        expectedBankAccount.setType(AccountType.SAVING);
        expectedBankAccount.setBalance(new Money(new BigDecimal("1000")));
        expectedBankAccount.setCustomersId(Set.of(99L));
        expectedBankAccount.setIssuedTransactions(Set.of(issuedTransaction1, issuedTransaction2));
        expectedBankAccount.setIncomingTransactions(Set.of(incomingTransaction));

        // Act
        BankAccount dto = bankAccountMapper.toDto(entity);

        // Assert
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(expectedBankAccount);

        verify(transactionMapper).toDto(issuedTransactionEntity1);
        verify(transactionMapper).toDto(issuedTransactionEntity2);
        verify(transactionMapper).toDto(incomingTransactionEntity);
        verifyNoMoreInteractions(customerMapper, transactionMapper);
    }

    private static TransactionEntity getTransactionEntity(long id, Instant timestamp) {
        TransactionEntity issuedTransactionEntity2 = new TransactionEntity();
        issuedTransactionEntity2.setId(id);
        issuedTransactionEntity2.setEmitterBankAccountEntity(null);
        issuedTransactionEntity2.setReceiverBankAccountEntity(null);
        issuedTransactionEntity2.setAmount(new BigDecimal("100"));
        issuedTransactionEntity2.setType(TransactionType.CREDIT);
        issuedTransactionEntity2.setStatus(TransactionStatus.ERROR);
        issuedTransactionEntity2.setDate(timestamp);
        issuedTransactionEntity2.setLabel("transaction test");
        return issuedTransactionEntity2;
    }

    private static Transaction getTransaction(long id, long emitterAccountId, long receiverAccountId, Instant timestamp) {
        Transaction incomingTransaction = new Transaction();
        incomingTransaction.setId(id);
        incomingTransaction.setType(CREDIT);
        incomingTransaction.setEmitterAccountId(emitterAccountId);
        incomingTransaction.setReceiverAccountId(receiverAccountId);
        incomingTransaction.setAmount(new BigDecimal("100"));
        incomingTransaction.setCurrency("EUR");
        incomingTransaction.setStatus(ERROR);
        incomingTransaction.setDate(timestamp);
        incomingTransaction.setLabel("transaction test");
        return incomingTransaction;
    }


    @Test
    void toEntity_shouldThrowNoSuchElementException_whenCustomerIsNotFound() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount dto = new CheckingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.CHECKING);
        dto.setBalance(new Money(new BigDecimal("1000")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomerUtils();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        Set<Transaction> transactions = new HashSet<>();
        Transaction transaction1 = new Transaction();
        transaction1.setId(2L);
        transaction1.setEmitterAccountId(2000L);
        transaction1.setReceiverAccountId(77L);
        transaction1.setAmount(new BigDecimal(100));
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setStatus(TransactionStatus.ERROR);
        transaction1.setDate(timestamp);
        transaction1.setLabel("transaction test");
        transaction1.setMetadata(Map.of("amount_before", "0", "amount_after", "350"));
        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setEmitterAccountId(5000L);
        transaction2.setReceiverAccountId(77L);
        transaction2.setAmount(new BigDecimal(100));
        transaction2.setType(TransactionType.CREDIT);
        transaction2.setStatus(TransactionStatus.ERROR);
        transaction2.setDate(timestamp);
        transaction2.setLabel("transaction test");
        transaction2.setMetadata(Map.of("amount_before", "0", "amount_after", "350"));
        transactions.add(transaction2);
        dto.setIssuedTransactions(transactions);

        try {
            // Act
            bankAccountMapper.toEntity(dto);
            fail();
        } catch (NoSuchElementException exception) {
            // Assert
            assertThat(exception.getMessage()).hasToString("Customer entity not found for id: 99");
        }

        verify(customerRepository).findByIdOptional(customer.getId());
        verifyNoInteractions(customerMapper, transactionMapper);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void toEntity_shouldMapEveryFieldsOfEntity_whenDtoHasValues() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount dto = new CheckingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.CHECKING);
        dto.setBalance(new Money(new BigDecimal("1000")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomerUtils();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        Set<Transaction> transactions = new HashSet<>();
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setEmitterAccountId(2000L);
        transaction1.setReceiverAccountId(77L);
        transaction1.setAmount(new BigDecimal(100));
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setStatus(TransactionStatus.ERROR);
        transaction1.setDate(timestamp);
        transaction1.setLabel("transaction test");
        transaction1.setMetadata(Map.of("amount_before", "0", "amount_after", "350"));
        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setEmitterAccountId(5000L);
        transaction2.setReceiverAccountId(77L);
        transaction2.setAmount(new BigDecimal(100));
        transaction2.setType(TransactionType.CREDIT);
        transaction2.setStatus(TransactionStatus.ERROR);
        transaction2.setDate(timestamp);
        transaction2.setLabel("transaction test");
        transaction2.setMetadata(Map.of("amount_before", "0", "amount_after", "350"));
        transactions.add(transaction2);
        dto.setIssuedTransactions(transactions);

        CustomerEntity customerEntity = createCustomerEntityUtils();
        when(customerRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(customerEntity));
        TransactionEntity transactionEntity1 = getTransactionEntity(1L, timestamp);
        when(transactionMapper.toEntity(transaction1)).thenReturn(transactionEntity1);
        TransactionEntity transactionEntity2 = getTransactionEntity(2L, timestamp);
        when(transactionMapper.toEntity(transaction2)).thenReturn(transactionEntity2);

        // Act
        BankAccountEntity entity = bankAccountMapper.toEntity(dto);

        // Assert
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(entity.getBalance()).usingRecursiveComparison().isEqualTo(new BigDecimal("1000"));
        assertThat(entity.getCustomers()).hasSize(1);
        assertThat(entity.getCustomers().iterator().next()).isEqualTo(customerEntity);
        assertThat(entity.getIssuedTransactions()).hasSize(2);
        assertThat(entity.getIssuedTransactions()).contains(transactionEntity1);
        assertThat(entity.getIssuedTransactions()).contains(transactionEntity2);

        verify(customerRepository).findByIdOptional(customer.getId());
        verify(transactionMapper).toEntity(transaction1);
        verify(transactionMapper).toEntity(transaction2);
        verifyNoMoreInteractions(customerMapper, transactionMapper, customerRepository);
    }

    private Customer createCustomerUtils() {
        Customer customer = new Customer();
        customer.setId(99L);
        customer.setFirstName("Paul");
        customer.setLastName("Martin");
        customer.setGender(Gender.MALE);
        customer.setMaritalStatus(MaritalStatus.SINGLE);
        customer.setBirthdate(LocalDate.of(1995, Month.MAY, 3));
        customer.setCountry("FR");
        customer.setAddress("100 avenue de la république");
        customer.setCity("Paris");
        customer.setEmail("jean.dupont@yahoo.fr");
        customer.setPhoneNumber("+33642645678");
        return customer;
    }

    private CustomerEntity createCustomerEntityUtils() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(99L);
        customerEntity.setFirstName("Paul");
        customerEntity.setLastName("Martin");
        customerEntity.setGender(Gender.MALE);
        customerEntity.setMaritalStatus(MaritalStatus.SINGLE);
        customerEntity.setBirthdate(LocalDate.of(1995, Month.MAY, 3));
        customerEntity.setCountry("FR");
        customerEntity.setAddress("100 avenue de la république");
        customerEntity.setCity("Paris");
        customerEntity.setEmail("jean.dupont@yahoo.fr");
        customerEntity.setPhoneNumber("+33642645678");
        return customerEntity;
    }

}
