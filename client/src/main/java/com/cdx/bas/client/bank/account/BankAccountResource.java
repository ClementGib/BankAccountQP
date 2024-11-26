package com.cdx.bas.client.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountControllerPort;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/accounts")
@RequestScoped
public class BankAccountResource implements BankAccountControllerPort {

    BankAccountServicePort bankAccountServicePort;

    @Inject
    public BankAccountResource(BankAccountServicePort bankAccountServicePort) {
        this.bankAccountServicePort = bankAccountServicePort;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public List<BankAccount> getAll() {
        return bankAccountServicePort.getAll();
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public BankAccount findById(@PathParam("id") long id) {
        return bankAccountServicePort.findBankAccount(id);
    }
}
