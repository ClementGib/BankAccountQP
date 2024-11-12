package com.cdx.bas.application.bank.customer;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerException;
import com.cdx.bas.domain.bank.customer.CustomerServicePort;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.h2.H2DatabaseTestResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {

    @Inject
    CustomerServicePort customerService;

    @Test
    @Order(1)
    void shouldGetAllCustomers_whenRepositoryFoundCustomers() {
        // Arrange
        Set<Customer> expectedCustomers = Set.of(
                new Customer(
                        1L,
                        "John",
                        "Doe",
                        Gender.MALE,
                        MaritalStatus.SINGLE,
                        LocalDate.parse("1980-01-01"),
                        "US",
                        "200 Central Park West, NY 10024",
                        "New York",
                        "johndoe@bas.com",
                        "+1 212-769-5100",
                        null,
                        Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")
                ),
                new Customer(
                        2L,
                        "Anne",
                        "Jean",
                        Gender.FEMALE,
                        MaritalStatus.MARRIED,
                        LocalDate.parse("1993-07-11"),
                        "FR",
                        "2 rue du chateau",
                        "Marseille",
                        "annej@bas.com",
                        "+36 6 50 44 12 05",
                        null,
                        Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")
                ),
                new Customer(
                        3L,
                        "Paul",
                        "Jean",
                        Gender.MALE,
                        MaritalStatus.MARRIED,
                        LocalDate.parse("1992-04-11"),
                        "FR",
                        "2 rue du chateau",
                        "Marseille",
                        "paulj@bas.com",
                        "+36 6 50 44 12 05",
                        null,
                        Map.of("contact_preferences", "email", "annual_salary", "52000", "newsletter", "false")
                ),
                new Customer(
                        4L,
                        "Sophie",
                        "Dupon",
                        Gender.FEMALE,
                        MaritalStatus.WIDOWED,
                        LocalDate.parse("1977-07-14"),
                        "FR",
                        "10 rue du louvre",
                        "Paris",
                        "Sodup@bas.com",
                        "+33 6 50 60 12 05",
                        null,
                        Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")
                ),
                new Customer(
                        5L,
                        "Andre",
                        "Martin",
                        Gender.MALE,
                        MaritalStatus.DIVORCED,
                        LocalDate.parse("1989-07-22"),
                        "FR",
                        "16 boulevard victor hugo",
                        "Nîmes",
                        "andre.martin@bas.com",
                        "+33 6 50 44 12 05",
                        null,
                        Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")
                ),
                new Customer(
                        6L,
                        "Juan",
                        "Pedros",
                        Gender.MALE,
                        MaritalStatus.SINGLE,
                        LocalDate.parse("1975-12-17"),
                        "ES",
                        "Place de las Delicias",
                        "Sevilla",
                        "juanito@bas.com",
                        "+34 9 20 55 62 05",
                        null,
                        Map.of("contact_preferences", "phone", "annual_salary", "200000", "newsletter", "false")
                )
        );

        // Act
        Set<Customer> actualCustomers = customerService.getAll();

        // Assert
        assertThat(actualCustomers).usingRecursiveComparison()
                .ignoringFields("accounts")
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Order(2)
    void shouldFindCustomer_whenCustomerExists() {
        // Arrange
        long customerId = 1L;
        Customer expectedCustomer = new Customer(
                1L,
                "John",
                "Doe",
                Gender.MALE,
                MaritalStatus.SINGLE,
                LocalDate.parse("1980-01-01"),
                "US",
                "200 Central Park West, NY 10024",
                "New York",
                "johndoe@bas.com",
                "+1 212-769-5100",
                null,
                Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")
        );

        // Act
        Customer actualCustomer = customerService.findCustomer(customerId);

        // Assert
        assertThat(actualCustomer)
                .usingRecursiveComparison()
                .ignoringFields("accounts")
                .isEqualTo(expectedCustomer);
    }


    @Test
    @Order(3)
    void shouldThrowCustomerException_whenCustomerDoesNotExist() {
        // Arrange
        long customerId = 99L;

        // Act & Assert
        assertThatThrownBy(() -> customerService.findCustomer(customerId))
                .isInstanceOf(CustomerException.class)
                .hasMessageContaining("Customer: searching not found\n" +
                        "Customer id:" + customerId);
    }
}
