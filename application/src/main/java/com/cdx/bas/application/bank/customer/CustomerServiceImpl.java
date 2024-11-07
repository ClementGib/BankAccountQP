package com.cdx.bas.application.bank.customer;

import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerException;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import com.cdx.bas.domain.bank.customer.CustomerServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.CommonMessages.ID_DETAIL;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerServicePort {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

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
                .orElseThrow(() -> new CustomerException(MessageFormatter.format(CUSTOMER_CONTEXT, SEARCHING_ACTION, NOT_FOUND_CAUSE, List.of(ID_DETAIL + customerId))));
    }
}
