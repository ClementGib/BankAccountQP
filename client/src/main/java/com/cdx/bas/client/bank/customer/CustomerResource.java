package com.cdx.bas.client.bank.customer;


import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerPersistencePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;
import java.util.Set;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

@Path("/customers")
@ApplicationScoped
public class CustomerResource {

    @Inject
    CustomerPersistencePort customerPersistencePort;

    @GET
    @Transactional(value = REQUIRED)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Customer> getAll() {
        //TODO use service
        return customerPersistencePort.getAll();
    }

    @GET
    @Path("/{id}")
    @Transactional(value = REQUIRED)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Customer> getCustomer(@PathParam("id") long id) {
        //TODO use service
        return customerPersistencePort.findById(id);
    }
}
