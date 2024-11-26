package com.cdx.bas.application.bank.customer;

import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.cdx.bas.domain.bank.customer.gender.Gender.FEMALE;
import static com.cdx.bas.domain.bank.customer.gender.Gender.MALE;
import static com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerRepositoryTest {

    public static final long ID_SEQUENCE_START = 20L;
    @Inject
    CustomerPersistencePort customerRepository;

    @Test
    @Transactional
    @Order(1)
    void getAll_shouldReturnAllCustomers() {
        Set<Customer> expectedCustomers = Set.of(
                new Customer(1L, "John", "Doe", MALE, SINGLE, LocalDate.of(1980, 1, 1), "US", "200 Central Park West, NY 10024", "New York", "johndoe@bas.com", "+1 212-769-5100", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")),
                new Customer(2L, "Anne", "Jean", FEMALE, MARRIED, LocalDate.of(1993, 7, 11), "FR", "2 rue du chateau", "Marseille", "annej@bas.com", "+36 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")),
                new Customer(3L, "Paul", "Jean", MALE, MARRIED, LocalDate.of(1992, 4, 11), "FR", "2 rue du chateau", "Marseille", "paulj@bas.com", "+36 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "email", "annual_salary", "52000", "newsletter", "false")),
                new Customer(4L, "Sophie", "Dupon", FEMALE, WIDOWED, LocalDate.of(1977, 7, 14), "FR", "10 rue du louvre", "Paris", "Sodup@bas.com", "+33 6 50 60 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")),
                new Customer(5L, "Andre", "Martin", MALE, DIVORCED, LocalDate.of(1989, 7, 22), "FR", "16 boulevard victor hugo", "Nîmes", "andre.martin@bas.com", "+33 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")),
                new Customer(6L, "Juan", "Pedros", MALE, SINGLE, LocalDate.of(1975, 12, 17), "ES", "Place de las Delicias", "Sevilla", "juanito@bas.com", "+34 9 20 55 62 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "200000", "newsletter", "false"))
        );

        Set<Customer> actualCustomers = customerRepository.getAll();
        assertThat(actualCustomers)
                .usingRecursiveComparison()
                .ignoringFields("accounts")
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Transactional
    @Order(2)
    void findById_shouldReturnCustomer_whenIdExists() {
        // Act
        Optional<Customer> customer = customerRepository.findById(1L);

        // Assert
        assertThat(customer).isPresent();
        assertThat(customer.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @Transactional
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Act
        Optional<Customer> customer = customerRepository.findById(999L);

        // Assert
        assertThat(customer).isNotPresent();
    }

    @Test
    @Transactional
    @Order(3)
    void create_shouldPersistNewCustomer() {
        // Arrange
        Customer newCustomer = new Customer(null, "Paul", "Smith", MALE, SINGLE, LocalDate.of(1990, 5, 20), "UK", "10 Downing St", "London", "paul.smith@bas.com", "+44 20 7946 0958", Collections.emptyList(), Map.of("contact_preferences", "email"));

        // Act
        Customer createdCustomer = customerRepository.create(newCustomer);

        // Assert
        assertThat(createdCustomer).isEqualTo(newCustomer);
        Optional<Customer> customer = customerRepository.findById(ID_SEQUENCE_START);
        assertThat(customer).isPresent();
        Customer expectedCustomer = new Customer(ID_SEQUENCE_START, "Paul", "Smith", MALE, SINGLE,
                LocalDate.of(1990, 5, 20), "UK", "10 Downing St", "London", "paul.smith@bas.com", "+44 20 7946 0958",
                Collections.emptyList(), Map.of("contact_preferences", "email"));
        assertThat(customer.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedCustomer);
    }

    @Test
    @Transactional
    @Order(4)
    void update_shouldModifyExistingCustomer() {
        // Arrange
        Optional<Customer> existingCustomer = customerRepository.findById(ID_SEQUENCE_START);
        assertThat(existingCustomer).isPresent();
        Customer customerToUpdate = existingCustomer.get();
        customerToUpdate.setFirstName("Johnny");

        // Act
        Customer updatedCustomer = customerRepository.update(customerToUpdate);

        // Assert
        Optional<Customer> persistedCustomer = customerRepository.findById(updatedCustomer.getId());
        assertThat(persistedCustomer).isPresent();
        assertThat(persistedCustomer.get().getFirstName()).isEqualTo("Johnny");
    }


    @Test
    @Transactional
    @Order(5)
    void deleteById_shouldRemoveCustomer_whenIdExists() {
        // Arrange
        assertThat(customerRepository.findById(ID_SEQUENCE_START)).isPresent();

        // Act
        Optional<Customer> deletedCustomer = customerRepository.deleteById(ID_SEQUENCE_START);

        // Assert
        assertThat(deletedCustomer).isPresent();
        assertThat(deletedCustomer.get().getFirstName()).isEqualTo("Johnny");
        assertThat(customerRepository.findById(ID_SEQUENCE_START)).isNotPresent();
    }

    @Test
    @Transactional
    void deleteById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Act
        Optional<Customer> deletedCustomer = customerRepository.deleteById(999L);

        // Assert
        assertThat(deletedCustomer).isEmpty();
    }
}