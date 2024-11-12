package com.cdx.bas.application.bank.customer;

import com.cdx.bas.application.bank.account.BankAccountEntity;
import com.cdx.bas.application.bank.customer.gender.GenderConverter;
import com.cdx.bas.application.bank.customer.maritalstatus.MaritalStatusConverter;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(schema = "basapp", name = "customers", uniqueConstraints = @UniqueConstraint(columnNames = "customer_id"))
public class CustomerEntity extends PanacheEntityBase {

    @Id
    @Column(name = "customer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_customer_id_seq_gen")
    @SequenceGenerator(name = "customers_customer_id_seq_gen", sequenceName = "customers_customer_id_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "gender", nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(name = "marital_status", nullable = false)
    @Convert(converter = MaritalStatusConverter.class)
    private MaritalStatus maritalStatus;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthdate;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "bank_accounts_customers", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<BankAccountEntity> accounts = new ArrayList<>();

    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String metadata;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerEntity that = (CustomerEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && gender == that.gender
                && maritalStatus == that.maritalStatus
                && Objects.equals(birthdate, that.birthdate)
                && Objects.equals(country, that.country)
                && Objects.equals(address, that.address)
                && Objects.equals(city, that.city)
                && Objects.equals(email, that.email)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(accounts, that.accounts)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, gender, maritalStatus, birthdate, country, address, city, email, phoneNumber, metadata);
    }
}
