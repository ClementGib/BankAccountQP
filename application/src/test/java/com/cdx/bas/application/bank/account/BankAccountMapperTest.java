package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.customer.CustomerEntity;
import com.cdx.bas.application.bank.customer.CustomerRepository;
import com.cdx.bas.application.bank.transaction.TransactionEntity;
import com.cdx.bas.application.mapper.DtoEntityMapper;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.ERROR;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.CREDIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BankAccountMapperTest {

    @Inject
    BankAccountMapper bankAccountMapper;

    @InjectMock
    DtoEntityMapper<Customer, CustomerEntity> customerMapper;

    @InjectMock
    DtoEntityMapper<Transaction, TransactionEntity> transactionMapper;

    @InjectMock
    CustomerRepository customerRepository;
    
    @Test
    public void toDto_shouldReturnNullDto_whenEntityIsNull() {
        BankAccount dto = bankAccountMapper.toDto(null);

        assertThat(dto).isNull();

        verifyNoInteractions(customerMapper);
    }

    @Test
    public void toEntity_shouldReturnNullEntity_whenDtoIsNull() {
        // Act
        BankAccountEntity entity = bankAccountMapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
        verifyNoInteractions(customerMapper);
    }

    @Test
    public void toDto_shouldReturnNullDto_whenEntityHasEmptyObject() {
        // Act
        BankAccount dto = bankAccountMapper.toDto(new BankAccountEntity());

        // Assert
        assertThat(dto).isNull();
        verifyNoInteractions(customerMapper);
    }

    @Test
    public void toDto_shouldMapAccountTypeOnly_whenEntityHasAccount_withOnlyAccountType() {
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
        public void toEntity_shouldMapNullValues_whenDtoHasAccount_withOnlyAccountTypeAndId() {
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
    public void toDto_shouldMapEveryFieldsOfDto_whenEntityHasValues() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(10L);
        entity.setType(AccountType.CHECKING);
        entity.setBalance(new BigDecimal("1000"));
        Set<CustomerEntity> customers = new HashSet<>();
        CustomerEntity customerEntity = createCustomerEntityUtils();
        customers.add(customerEntity);
        entity.setCustomers(customers);
        Set<TransactionEntity> transactionEntities = new HashSet<>();
        TransactionEntity transactionEntity1 = createTransactionEntity(2000L, timestamp);
        transactionEntities.add(transactionEntity1);
        TransactionEntity transactionEntity2 = createTransactionEntity(5000L, timestamp);
        transactionEntities.add(transactionEntity2);
        entity.setIssuedTransactions(transactionEntities);

        Transaction transaction1 = Transaction.builder()
                .id(2L)
                .type(CREDIT)
                .emitterAccountId(2000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(ERROR)
                .date(timestamp)
                .label("transaction test")
                .build();
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .type(CREDIT)
                .emitterAccountId(5000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(ERROR)
                .date(timestamp)
                .label("transaction test")
                .build();
        when(transactionMapper.toDto(transactionEntity1)).thenReturn(transaction1);
        when(transactionMapper.toDto(transactionEntity2)).thenReturn(transaction2);

        // Act
        BankAccount dto = bankAccountMapper.toDto(entity);

        // Assert
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(dto.getBalance()).usingRecursiveComparison().isEqualTo(new Money(new BigDecimal("1000")));
        assertThat(dto.getCustomersId()).hasSize(1);
        assertThat(dto.getCustomersId().iterator().next()).isEqualTo(99L);
        assertThat(dto.getIssuedTransactions()).hasSize(2);
        assertThat(dto.getIssuedTransactions()).contains(transaction1);
        assertThat(dto.getIssuedTransactions()).contains(transaction2);

        verify(transactionMapper).toDto(transactionEntity1);
        verify(transactionMapper).toDto(transactionEntity2);
        verifyNoMoreInteractions(customerMapper, transactionMapper);
    }

    @Test
    public void toEntity_shouldThrowNoSuchElementException_whenCustomerIsNotFound() {
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
        Transaction transaction1 = Transaction.builder()
                .id(2L)
                .emitterAccountId(2000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal(100))
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.ERROR)
                .date(timestamp)
                .label("transaction test")
                .metadata(Map.of("amount_before", "0", "amount_after", "350"))
                .build();
        transactions.add(transaction1);
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .emitterAccountId(5000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal(100))
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.ERROR)
                .date(timestamp)
                .label("transaction test")
                .metadata(Map.of("amount_before", "0", "amount_after", "350"))
                .build();
        transactions.add(transaction2);
        dto.setIssuedTransactions(transactions);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

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
    public void toEntity_shouldMapEveryFieldsOfEntity_whenDtoHasValues() {
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
        Transaction transaction1 = Transaction.builder()
                .id(2L)
                .emitterAccountId(2000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal(100))
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.ERROR)
                .date(timestamp)
                .label("transaction test")
                .metadata(Map.of("amount_before", "0", "amount_after", "350"))
                .build();
        transactions.add(transaction1);
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .emitterAccountId(5000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal(100))
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.ERROR)
                .date(timestamp)
                .label("transaction test")
                .metadata(Map.of("amount_before", "0", "amount_after", "350"))
                .build();
        transactions.add(transaction2);
        dto.setIssuedTransactions(transactions);

        CustomerEntity customerEntity = createCustomerEntityUtils();
        when(customerRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);
        TransactionEntity transactionEntity1 = createTransactionEntity(2000L, timestamp);
        when(transactionMapper.toEntity(transaction1)).thenReturn(transactionEntity1);
        TransactionEntity transactionEntity2 = createTransactionEntity(5000L, timestamp);
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

    private TransactionEntity createTransactionEntity(long id, Instant instantDate) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setId(id);
        transactionEntity.setEmitterBankAccountEntity(null);
        transactionEntity.setReceiverBankAccountEntity(null);
        transactionEntity.setAmount(new BigDecimal("100"));
        transactionEntity.setType(TransactionType.CREDIT);
        transactionEntity.setStatus(TransactionStatus.ERROR);
        transactionEntity.setDate(instantDate);
        transactionEntity.setLabel("transaction test");
        return transactionEntity;
    }
}
