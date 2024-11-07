package com.cdx.bas.application.bank.customer;

import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerException;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import com.cdx.bas.domain.bank.customer.CustomerServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

import static com.cdx.bas.domain.message.CommonMessages.*;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerServicePort {

    CustomerPersistencePort customerPersistencePort;

    @Inject
    public CustomerServiceImpl(CustomerPersistencePort customerPersistencePort) {
        this.customerPersistencePort = customerPersistencePort;
    }

    @Override
    @Transactional
    public Set<Customer> getAll() {
        return customerPersistencePort.getAll();
    }

    @Override
    @Transactional
    public Customer findCustomer(Long customerId) {
        return customerPersistencePort.findById(customerId)
                .orElseThrow(() -> new CustomerException(MessageFormatter.format(CUSTOMER_CONTEXT, SEARCHING_ACTION, NOT_FOUND_CAUSE, List.of(CUSTOMER_ID_DETAIL + customerId))));
    }
}
