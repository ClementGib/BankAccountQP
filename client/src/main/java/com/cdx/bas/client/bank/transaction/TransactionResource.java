package com.cdx.bas.client.bank.transaction;

import com.cdx.bas.domain.DomainException;
import com.cdx.bas.domain.bank.transaction.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

@Path("/transactions")
@ApplicationScoped
public class TransactionResource implements TransactionControllerPort {

    private static final Logger logger = LoggerFactory.getLogger(TransactionResource.class);

    @Inject
    public TransactionResource(TransactionServicePort transactionServicePort) {
        this.transactionServicePort = transactionServicePort;
    }

    TransactionServicePort transactionServicePort;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Set<Transaction> getAll() {
        return transactionServicePort.getAll();
    }

    @GET
    @Path("/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Set<Transaction> getAllByStatus(@PathParam("status") String status) {
        try {
            return transactionServicePort.findAllByStatus(status);
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.warn("Error: " + illegalArgumentException.getCause());
            return Collections.emptySet();
        }
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Transaction findById(@PathParam("id") long id) {
        return transactionServicePort.findTransaction(id);
    }

    @POST
    @Path("/digital")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add transaction", description = "Returns acceptance information about the added transaction")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "New transaction accepted"),
            @APIResponse(responseCode = "400", description = "Transaction invalid check error details"),
            @APIResponse(responseCode = "500", description = "Unexpected error happened")
    })
    @Override
    public Response addDigitalTransaction(NewDigitalTransaction newTransaction) {
        try {
            transactionServicePort.createDigitalTransaction(newTransaction);
            return Response.status(Response.Status.ACCEPTED).entity("Digital transaction accepted").build();
        } catch (DomainException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error happened").build();
        }
    }

    @POST
    @Path("/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add withdraw transaction", description = "Returns acceptance information about the added transaction")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "New transaction accepted"),
            @APIResponse(responseCode = "400", description = "Transaction invalid check error details"),
            @APIResponse(responseCode = "500", description = "Unexpected error happened")
    })
    @Override
    public Response withdraw(NewCashTransaction newWithdrawTransaction) {
        try {
            transactionServicePort.withdraw(newWithdrawTransaction);
            return Response.status(Response.Status.ACCEPTED).entity("Withdraw transaction accepted").build();
        } catch (DomainException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (Exception exception) {
            logger.error("Unexpected error happened:&", exception.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error happened: ").build();
        }
    }

    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add deposit transaction", description = "Returns acceptance information about the added transaction")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "New transaction accepted"),
            @APIResponse(responseCode = "400", description = "Transaction invalid check error details"),
            @APIResponse(responseCode = "500", description = "Unexpected error happened")
    })
    @Override
    public Response deposit(NewCashTransaction newDepositTransaction) {
        try {
            transactionServicePort.deposit(newDepositTransaction);
            return Response.status(Response.Status.ACCEPTED).entity("Deposit transaction accepted").build();
        } catch (DomainException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error happened").build();
        }
    }
}