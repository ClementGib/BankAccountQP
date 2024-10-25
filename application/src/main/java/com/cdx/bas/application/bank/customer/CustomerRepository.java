package com.cdx.bas.application.bank.customer;


import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jboss.logging.Logger;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Inject
    public CustomerRepository(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
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
        logger.info("Customer " + customer.getId() + " created");
        return customer;
	}

	@Override
	public Customer update(Customer customer) {
	    persist(customerMapper.toEntity(customer));
        logger.info("Customer " + customer.getId() + " updated");
        return customer;
	}

	@Override
	public Optional<Customer> deleteById(long id) {
        Optional<CustomerEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            CustomerEntity entity = entityOptional.get();
            delete(entity);
            logger.info("Customer " + entity.getId() + " deleted");
            return Optional.of(customerMapper.toDto(entity));
        }
        return Optional.empty();
	}

}
