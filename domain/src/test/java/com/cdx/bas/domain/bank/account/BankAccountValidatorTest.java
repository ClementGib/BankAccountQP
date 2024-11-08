package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.account.validation.BankAccountValidator;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class BankAccountValidatorTest {

    @Inject
    BankAccountValidator bankAccountValidator;
    
    @Test
    void validateBankAccount_shouldDoNothing_whenCheckingBankAccountIsValid(){
        BankAccount dto = new CheckingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.CHECKING);
        dto.setBalance(new Money(new BigDecimal("1000")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        bankAccountValidator.validateBankAccount(dto);
    }
    
    @Test
    void validateBankAccount_shouldDoNothing_whenSavingBankAccountIsValid(){
        BankAccount dto = new SavingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.SAVING);
        dto.setBalance(new Money(new BigDecimal("1000")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        bankAccountValidator.validateBankAccount(dto);
    }
    
    @Test
    void validateBankAccount_shouldDoNothing_whenMMABankAccountIsValid(){
        BankAccount dto = new MMABankAccount();
        dto.setId(10L);
        dto.setType(AccountType.MMA);
        dto.setBalance(new Money(new BigDecimal("1000")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        bankAccountValidator.validateBankAccount(dto);
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenCheckingBankAccountFieldsAreInvalid(){
        BankAccount dto = new CheckingBankAccount();
        dto.setId(null);
        dto.setType(null);
        dto.setBalance(null);
        dto.setCustomersId( null);
        dto.setIssuedTransactions(null);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage().split("\n")).hasSize(7);
        }
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenCheckingBankAccountAmountIsInvalid(){
        BankAccount dto = new CheckingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.CHECKING);
        dto.setBalance(new Money(new BigDecimal("100001")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage()).hasToString("balance amount must be between -600 and 100000.\n");
        }
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenSavingBankAccountFieldsAreInvalid(){
        BankAccount dto = new SavingBankAccount();
        dto.setId(null);
        dto.setType(null);
        dto.setBalance(null);
        dto.setCustomersId( null);
        dto.setIssuedTransactions(null);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage().split("\n")).hasSize(7);
        }
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenSavingBankAccountAmountIsInvalid(){
        BankAccount dto = new SavingBankAccount();
        dto.setId(10L);
        dto.setType(AccountType.SAVING);
        dto.setBalance(new Money(new BigDecimal("22951")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage()).hasToString("balance amount must be between 1 and 22950.\n");
        }
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenMMABankAccountFieldsAreInvalid(){
        BankAccount dto = new MMABankAccount();
        dto.setId(null);
        dto.setType(null);
        dto.setBalance(null);
        dto.setCustomersId( null);
        dto.setIssuedTransactions(null);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage().split("\n")).hasSize(7);
        }
    }
    
    @Test
    void validateBankAccount_shouldThrowBankAccountException_whenMMABankAccountAmountIsInvalid(){
        BankAccount dto = new MMABankAccount();
        dto.setId(10L);
        dto.setType(AccountType.MMA);
        dto.setBalance(new Money(new BigDecimal("250001")));
        Set<Long> customers = new HashSet<>();
        Customer customer = createCustomer();
        customers.add(customer.getId());
        dto.setCustomersId(customers);
        
        try {
            bankAccountValidator.validateBankAccount(dto);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage()).hasToString("balance amount must be between 1000 and 250000.\n");
        }
    }
    
    private Customer createCustomer() {
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
}