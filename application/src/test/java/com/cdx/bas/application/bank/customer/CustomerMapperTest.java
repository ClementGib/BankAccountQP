package com.cdx.bas.application.bank.customer;

import com.cdx.bas.application.bank.account.BankAccountEntity;
import com.cdx.bas.application.bank.account.BankAccountMapper;
import com.cdx.bas.application.bank.transaction.TransactionEntity;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class CustomerMapperTest {

    @InjectMock
    BankAccountMapper bankAccountMapper;

    @Inject
    CustomerMapper customerMapper;
    
    @Test
    void toDto_shouldReturnNullDto_whenEntityIsNull() {
        // Act
        Customer dto = customerMapper.toDto(null);

        // Assert
        assertThat(dto).isNull();
        verifyNoInteractions(bankAccountMapper);
    }

    @Test
    void toEntity_shouldReturnNullEntity_whenDtoIsNull() {
        // Act
        CustomerEntity entity = customerMapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
        verifyNoInteractions(bankAccountMapper);
    }
    
	@Test
	void toDto_shouldMapNullValues_whenEntityValuesNotDefined() {
        // Act
        Customer dto = customerMapper.toDto(new CustomerEntity());

        // Assert
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getGender()).isNull();
        assertThat(dto.getBirthdate()).isNull();
        assertThat(dto.getBirthdate()).isNull();
        assertThat(dto.getCountry()).isNull();
        assertThat(dto.getMaritalStatus()).isNull();
        assertThat(dto.getAddress()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPhoneNumber()).isNull();
        assertThat(dto.getAccounts()).isEmpty();
        assertThat(dto.getMetadata()).isEmpty();

        verifyNoInteractions(bankAccountMapper);
    }
    
    @Test
    void toEntity_shouldMapNullValues_whenDtoValuesNotDefined() {
        // Act
        CustomerEntity entity = customerMapper.toEntity(new Customer());

        // Assert
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isNull();
        assertThat(entity.getLastName()).isNull();
        assertThat(entity.getGender()).isNull();
        assertThat(entity.getBirthdate()).isNull();
        assertThat(entity.getBirthdate()).isNull();
        assertThat(entity.getCountry()).isNull();
        assertThat(entity.getMaritalStatus()).isNull();
        assertThat(entity.getAddress()).isNull();
        assertThat(entity.getCity()).isNull();
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getPhoneNumber()).isNull();
        assertThat(entity.getAccounts()).isEmpty();
        assertThat(entity.getMetadata()).isNull();
        verifyNoInteractions(bankAccountMapper);
    }
    
	@Test
	void toDto_shouldMapDtoValues_whenEntityHasValues() {
        // Arrange
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);
        entity.setFirstName("Paul");
        entity.setLastName("Martin");
        entity.setGender(Gender.MALE);
        entity.setMaritalStatus(MaritalStatus.SINGLE);
        entity.setBirthdate(LocalDate.of(1995,Month.MAY,3));
        entity.setCountry("FR");
        entity.setAddress("100 avenue de la république");
        entity.setCity("Paris");
        entity.setEmail("jean.dupont@yahoo.fr");
        entity.setPhoneNumber("+33642645678");
        
        Instant instantDate = Instant.now();
        BankAccountEntity accountEntity1 = createBankAccountEntity(10L, instantDate);
        BankAccountEntity accountEntity2 = createBankAccountEntity(11L, instantDate);
        List<BankAccountEntity> accounts = new ArrayList<>();
        accounts.add(accountEntity1);
        accounts.add(accountEntity2);
        entity.setAccounts(accounts);
        
        String strMetadata = "{\"contact_preferences\" : \"email\", \"annual_salary\" : \"52000\"}";
        entity.setMetadata(strMetadata);
        
        BankAccount account1 = createBankAccount(10L, instantDate);
        BankAccount account2 = createBankAccount(11L, instantDate);
        when(bankAccountMapper.toDto(accountEntity1)).thenReturn(account1);
        when(bankAccountMapper.toDto(accountEntity2)).thenReturn(account2);

        // Act
        Customer dto = customerMapper.toDto(entity);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).hasToString("Paul");
        assertThat(dto.getLastName()).hasToString("Martin");
        assertThat(dto.getGender()).isEqualTo(Gender.MALE);
        assertThat(dto.getBirthdate()).isBefore(LocalDate.now());
        assertThat(dto.getBirthdate().toString()).hasToString("1995-05-03");
        assertThat(dto.getCountry()).isEqualTo("FR");
        assertThat(dto.getMaritalStatus()).isEqualTo(MaritalStatus.SINGLE);
        assertThat(dto.getAddress()).hasToString("100 avenue de la république");
        assertThat(dto.getCity()).hasToString("Paris");
        assertThat(dto.getEmail()).hasToString("jean.dupont@yahoo.fr");
        assertThat(dto.getPhoneNumber()).hasToString("+33642645678");
        assertThat(dto.getAccounts()).hasSize(2);
        Set<BankAccount> accountsToCompare = Stream.of(account1, account2).collect(Collectors.toSet());
        assertThat(dto.getAccounts()).containsExactlyInAnyOrderElementsOf(accountsToCompare);
        assertThat(dto.getMetadata()).containsEntry("contact_preferences", "email");
        assertThat(dto.getMetadata()).containsEntry("annual_salary","52000");
        verify(bankAccountMapper).toDto(accountEntity1);
        verify(bankAccountMapper).toDto(accountEntity2);
        verifyNoMoreInteractions(bankAccountMapper);
    }
    
    @Test
    void toEntity_shouldMapEntityValues_whenDtoHasValues() {
        // Arrange
        Customer model = new Customer();
        model.setId(1L);
        model.setFirstName("Paul");
        model.setLastName("Martin");
        model.setGender(Gender.MALE);
        model.setMaritalStatus(MaritalStatus.SINGLE);
        model.setBirthdate(LocalDate.of(1995,Month.MAY,3));
        model.setCountry("FR");
        model.setAddress("100 avenue de la république");
        model.setCity("Paris");
        model.setEmail("jean.dupont@yahoo.fr");
        model.setPhoneNumber("+33642645678");
        
        Instant instantDate = Instant.now();
        BankAccount account1 = createBankAccount(10L, instantDate);
        BankAccount account2 = createBankAccount(11L, instantDate);
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        model.setAccounts(accounts);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("contact_preferences", "email");
        metadata.put("annual_salary", "48000");
        model.setMetadata(metadata);
        
        BankAccountEntity accountEntity1 = createBankAccountEntity(10L, instantDate);
        BankAccountEntity accountEntity2 = createBankAccountEntity(11L, instantDate);
        when(bankAccountMapper.toEntity(account1)).thenReturn(accountEntity1);
        when(bankAccountMapper.toEntity(account2)).thenReturn(accountEntity2);

        // Act
        CustomerEntity entity = customerMapper.toEntity(model);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFirstName()).hasToString("Paul");
        assertThat(entity.getLastName()).hasToString("Martin");
        assertThat(entity.getGender()).isEqualTo(Gender.MALE);
        assertThat(entity.getBirthdate()).isBefore(LocalDate.now());
        assertThat(entity.getBirthdate().toString()).hasToString("1995-05-03");
        assertThat(entity.getCountry()).isEqualTo("FR");
        assertThat(entity.getMaritalStatus()).isEqualTo(MaritalStatus.SINGLE);
        assertThat(entity.getAddress()).hasToString("100 avenue de la république");
        assertThat(entity.getCity()).hasToString("Paris");
        assertThat(entity.getEmail()).hasToString("jean.dupont@yahoo.fr");
        assertThat(entity.getPhoneNumber()).hasToString("+33642645678");
        assertThat(entity.getAccounts()).hasSize(2);
        Set<BankAccountEntity> accountsToCompare = Stream.of(accountEntity1, accountEntity2).collect(Collectors.toSet());
        assertThat(entity.getAccounts()).containsExactlyInAnyOrderElementsOf(accountsToCompare);
        assertThat(entity.getMetadata()).hasToString("{\"contact_preferences\":\"email\",\"annual_salary\":\"48000\"}");
        verify(bankAccountMapper).toEntity(account1);
        verify(bankAccountMapper).toEntity(account2);
        verifyNoMoreInteractions(bankAccountMapper);
    }
    
    private BankAccount createBankAccount(long accountId, Instant timestamp) {
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(accountId);
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(new Money(new BigDecimal("100")));
        Set<Long> customersId = new HashSet<>();
        customersId.add(99L);
        bankAccount.setCustomersId(customersId);
        Set<Transaction> transactions = new HashSet<>();
        Transaction transaction1 = new Transaction();
        transaction1.setId(100L);
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setEmitterAccountId(accountId);
        transaction1.setReceiverAccountId(77L);
        transaction1.setAmount(new BigDecimal("100"));
        transaction1.setCurrency("EUR");
        transaction1.setStatus(TransactionStatus.ERROR);
        transaction1.setDate(timestamp);
        transaction1.setLabel("transaction test");
        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId(100L);
        transaction2.setType(TransactionType.CREDIT);
        transaction2.setEmitterAccountId(accountId);
        transaction2.setReceiverAccountId(77L);
        transaction2.setAmount(new BigDecimal("100"));
        transaction2.setCurrency("EUR");
        transaction2.setStatus(TransactionStatus.ERROR);
        transaction2.setDate(timestamp);
        transaction2.setLabel("transaction test");
        transactions.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setId(100L);
        transaction3.setType(TransactionType.CREDIT);
        transaction3.setEmitterAccountId(accountId);
        transaction3.setReceiverAccountId(77L);
        transaction3.setAmount(new BigDecimal("100"));
        transaction3.setCurrency("EUR");
        transaction3.setStatus(TransactionStatus.ERROR);
        transaction3.setDate(timestamp);
        transaction3.setLabel("transaction test");
        transactions.add(transaction3);
        bankAccount.setIssuedTransactions(transactions);
        return bankAccount;
    }
    
    private BankAccountEntity createBankAccountEntity(long id, Instant instantDate) {
        BankAccountEntity bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setId(id);
        bankAccountEntity.setType(AccountType.CHECKING);
        bankAccountEntity.setBalance(new BigDecimal("100"));
        Set<CustomerEntity> customersId = new HashSet<>();
        customersId.add(new CustomerEntity());
        bankAccountEntity.setCustomers(customersId);
        Set<TransactionEntity> transactionEntities = new HashSet<>();
        transactionEntities.add(createTransactionEntity(99L, instantDate));
        transactionEntities.add(createTransactionEntity(100L, instantDate));
        bankAccountEntity.setIssuedTransactions(transactionEntities);
        return bankAccountEntity;
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
