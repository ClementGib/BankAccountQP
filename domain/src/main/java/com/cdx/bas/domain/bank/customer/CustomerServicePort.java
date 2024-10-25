package com.cdx.bas.domain.bank.customer;

import java.util.Set;

public interface CustomerServicePort {


    /**
     * find all customers
     *
     * @return List with all Customer
     */
    Set<Customer> getAll();

    /**
     * find customer id
     *
     * @param customerId
     * @return customer found
     */
    Customer findCustomer(Long customerId);

}
