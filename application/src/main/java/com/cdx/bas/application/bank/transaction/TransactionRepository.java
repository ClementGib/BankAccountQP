package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.message.MessageFormatter;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.cdx.bas.domain.message.CommonMessages.*;

/***
 * persistence implementation for Transaction entities
 *
 * @author Clément Gibert
 *
 */

@RequestScoped
public class TransactionRepository implements TransactionPersistencePort, PanacheRepositoryBase<TransactionEntity, Long> {

    private static final Logger logger = Logger.getLogger(TransactionRepository.class);
    public static final String STATUS = "status";

    TransactionMapper transactionMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public TransactionRepository(TransactionMapper transactionMapper, EntityManager entityManager) {
        this.transactionMapper = transactionMapper;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Transaction> findById(long id) {
        return findByIdOptional(id).map(transactionMapper::toDto);
    }

    @Override
    public Set<Transaction> findTransactionsByEmitterBankAccount(long emitterBankAccountId) {
        List<TransactionEntity> transactionEntities = entityManager
                .createQuery("SELECT t FROM TransactionEntity t WHERE t.emitterBankAccountEntity.id = :id", TransactionEntity.class)
                .setParameter("id", emitterBankAccountId)
                .getResultList();
        return transactionEntities.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Transaction> findTransactionsByReceiverBankAccount(long receiverBankAccountId) {
        List<TransactionEntity> transactionEntities = entityManager
                .createQuery("SELECT t FROM TransactionEntity t WHERE t.receiverBankAccountEntity.id = :id", TransactionEntity.class)
                .setParameter("id", receiverBankAccountId)
                .getResultList();
        return transactionEntities.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Transaction> getAll() {
        return findAll(Sort.by(STATUS)).stream()
                .map(transactionEntity -> transactionMapper.toDto(transactionEntity))
                .collect(Collectors.toSet());
    }


    @Override
    public Set<Transaction> findAllByStatus(TransactionStatus transactionStatus) {
        return findAll(Sort.by(STATUS)).stream()
                .filter(transaction -> transaction.getStatus().equals(transactionStatus))
                .map(transaction -> transactionMapper.toDto(transaction))
                .collect(Collectors.toSet());
    }

    @Override
    public Queue<Transaction> findUnprocessedTransactions() {
        return find("#TransactionEntity.findUnprocessed",
                Parameters.with(STATUS, TransactionStatus.UNPROCESSED).map())
                .list()
                .stream().map(transactionMapper::toDto)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    @Override
    public void create(Transaction transaction) {
        entityManager.persist(transactionMapper.toEntity(transaction));
        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, CREATION_ACTION, SUCCESS_STATUS, List.of(TRANSACTION_ID_DETAIL + transaction.getId())));
    }

    @Override
    public Transaction update(Transaction transaction) {
        TransactionEntity entity = transactionMapper.toEntity(transaction);
        TransactionEntity merge = entityManager.merge(entity);
        Transaction updatedTransaction = transactionMapper.toDto(merge);
        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, UPDATE_ACTION, SUCCESS_STATUS, List.of(TRANSACTION_ID_DETAIL + transaction.getId())));
        return updatedTransaction;
    }



    @Override
    public Optional<Transaction> deleteById(long id) {
        Optional<TransactionEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            TransactionEntity entity = entityOptional.get();
            delete(entity);
            logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, DELETION_ACTION, SUCCESS_STATUS, List.of(TRANSACTION_ID_DETAIL + id)));
            return Optional.of(transactionMapper.toDto(entity));
        }
        return Optional.empty();
    }
}
