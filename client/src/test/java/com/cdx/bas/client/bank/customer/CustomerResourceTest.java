package com.cdx.bas.client.bank.customer;

import com.cdx.bas.domain.bank.customer.Customer;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.cdx.bas.domain.bank.customer.gender.Gender.FEMALE;
import static com.cdx.bas.domain.bank.customer.gender.Gender.MALE;
import static com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class CustomerResourceTest {

    @Inject
    CustomerResource customerResource;

    @Test
    void getAll_shouldReturnAllCustomers() {
        Set<Customer> expectedCustomers = Set.of(
                new Customer(1L, "John", "Doe", MALE, SINGLE, LocalDate.of(1980, 1, 1), "US", "200 Central Park West, NY 10024", "New York", "johndoe@bas.com", "+1 212-769-5100", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")),
                new Customer(2L, "Anne", "Jean", FEMALE, MARRIED, LocalDate.of(1993, 7, 11), "FR", "2 rue du chateau", "Marseille", "annej@bas.com", "+36 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "false")),
                new Customer(3L, "Paul", "Jean", MALE, MARRIED, LocalDate.of(1992, 4, 11), "FR", "2 rue du chateau", "Marseille", "paulj@bas.com", "+36 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "email", "annual_salary", "52000", "newsletter", "false")),
                new Customer(4L, "Sophie", "Dupon", FEMALE, WIDOWED, LocalDate.of(1977, 7, 14), "FR", "10 rue du louvre", "Paris", "Sodup@bas.com", "+33 6 50 60 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")),
                new Customer(5L, "Andre", "Martin", MALE, DIVORCED, LocalDate.of(1989, 7, 22), "FR", "16 boulevard victor hugo", "Nîmes", "andre.martin@bas.com", "+33 6 50 44 12 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "52000", "newsletter", "true")),
                new Customer(6L, "Juan", "Pedros", MALE, SINGLE, LocalDate.of(1975, 12, 17), "ES", "Place de las Delicias", "Sevilla", "juanito@bas.com", "+34 9 20 55 62 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "200000", "newsletter", "false"))
        );

        Set<Customer> actualCustomers = customerResource.getAll();
        assertThat(actualCustomers)
                .usingRecursiveComparison()
                .ignoringFields("accounts")
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    void getCustomer_shouldReturnCustomer() {
        Customer expectedCustomer = new Customer(6L, "Juan", "Pedros", MALE, SINGLE, LocalDate.of(1975, 12, 17), "ES", "Place de las Delicias", "Sevilla", "juanito@bas.com", "+34 9 20 55 62 05", Collections.emptyList(), Map.of("contact_preferences", "phone", "annual_salary", "200000", "newsletter", "false"));

        Customer actualCustomer = customerResource.getCustomer(6L);
        assertThat(actualCustomer)
                .usingRecursiveComparison()
                .ignoringFields("accounts")
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomer);
    }
}