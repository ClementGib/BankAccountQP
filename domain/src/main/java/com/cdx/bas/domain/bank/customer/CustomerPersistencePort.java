package com.cdx.bas.domain.bank.customer;

import java.util.Optional;
import java.util.Set;

public interface CustomerPersistencePort {

    /**
     * find every Customers
     * @return all Customer
     */
    public Set<Customer> getAll();

    /**
     * find every Customers from id collection
     *
     * @param customersId
     * @return all Customer
     */
    public Set<Customer> findAllById(Set<Long> customersId);
    
    /**
     * find Customer from its id
     * 
     * @param id of Customer
     * @return <Optional>Customer if id corresponding or not to a Customer
     */
    public Optional<Customer> findById(long id);
    
    /**
     * create the current Customer
     * 
     * @param customer to create
     * @return created Customer
     */
    public Customer create(Customer customer);
    
    /**
     * update the current Customer
     * 
     * @param customer to update
     * @return updated Customer
     */
    public Customer update(Customer customer);
    
    /**
     * delete Customer from its id
     * 
     * @param id of Customer
     * @return Customer if id corresponding or not to a Customer
     */
    public Optional<Customer> deleteById(long id);
}
