package com.cdx.bas.client.bank.customer;


import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.customer.CustomerServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

@Path("/customers")
@ApplicationScoped
public class CustomerResource {

    private final CustomerServicePort customerServicePort;

    @Inject
    public CustomerResource(CustomerServicePort customerServicePort) {
        this.customerServicePort = customerServicePort;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Customer> getAll() {
        return customerServicePort.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("id") long id) {
        return customerServicePort.findCustomer(id);
    }
}
