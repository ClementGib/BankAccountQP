package com.cdx.bas.application.bank.customer;


import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import com.cdx.bas.domain.message.MessageFormatter;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cdx.bas.domain.message.CommonMessages.*;

/***
 * persistence implementation for Customer entities
 *
 * @author Clément Gibert
 *
 */
@RequestScoped
public class CustomerRepository implements CustomerPersistencePort, PanacheRepositoryBase<CustomerEntity, Long> {

    private static final Logger logger = Logger.getLogger(CustomerRepository.class);


    private final CustomerMapper customerMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public CustomerRepository(CustomerMapper customerMapper, EntityManager entityManager) {
        this.customerMapper = customerMapper;
        this.entityManager = entityManager;
    }

    @Override
    public Set<Customer> getAll() {
        return findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Customer> findById(long id) {
        return findByIdOptional(id).map(customerMapper::toDto);
    }

    @Override
    public Customer create(Customer customer) {
        persist(customerMapper.toEntity(customer));
        logger.debug(MessageFormatter.format(CUSTOMER_CONTEXT, CREATION_ACTION, SUCCESS_STATUS, List.of(CUSTOMER_ID_DETAIL + customer.getId())));
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        Customer updatedCustomer = customerMapper.toDto(entityManager.merge(customerMapper.toEntity(customer)));
        logger.debug(MessageFormatter.format(CUSTOMER_CONTEXT, UPDATE_ACTION, SUCCESS_STATUS, List.of(CUSTOMER_ID_DETAIL + customer.getId())));
        return updatedCustomer;
    }

    @Override
    public Optional<Customer> deleteById(long id) {
        Optional<CustomerEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            CustomerEntity entity = entityOptional.get();
            delete(entity);
            logger.debug(MessageFormatter.format(CUSTOMER_CONTEXT, DELETION_ACTION, SUCCESS_STATUS, List.of(CUSTOMER_ID_DETAIL + id)));
            return Optional.of(customerMapper.toDto(entity));
        }
        return Optional.empty();
    }

}
