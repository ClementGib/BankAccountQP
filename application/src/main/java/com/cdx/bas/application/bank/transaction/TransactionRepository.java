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
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.CommonMessages.ID_DETAIL;

/***
 * persistence implementation for Transaction entities
 * 
 * @author Clément Gibert
 *
 */

@RequestScoped
public class TransactionRepository implements TransactionPersistencePort, PanacheRepositoryBase<TransactionEntity, Long> {
    
    private static final Logger logger = Logger.getLogger(TransactionRepository.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    TransactionMapper transactionMapper;

    @Override
    public Optional<Transaction> findById(long id) {
        return findByIdOptional(id).map(transactionMapper::toDto);
    }

    @Override
    public Set<Transaction> getAll() {
        return findAll(Sort.by("status")).stream()
                .map(transactionEntity -> transactionMapper.toDto(transactionEntity))
                .collect(Collectors.toSet());
    }


    @Override
    @Transactional
    public Set<Transaction> findAllByStatus(TransactionStatus transactionStatus) {
        return findAll(Sort.by("status")).stream()
                .filter(transaction -> transaction.getStatus().equals(transactionStatus))
                .map(transaction -> transactionMapper.toDto(transaction))
                .collect(Collectors.toSet());
    }

    @Override
    public Queue<Transaction> findUnprocessedTransactions() {
        return find("#TransactionEntity.findUnprocessed",
                Parameters.with("status", TransactionStatus.UNPROCESSED).map())
                .list()
                .stream().map(transactionMapper::toDto)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    @Override
    public void create(Transaction transaction) {
        entityManager.persist(transactionMapper.toEntity(transaction));
        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, CREATION_ACTION, SUCCESS_STATUS, List.of(ID_DETAIL + transaction.getId())));

    }

    @Override
    public Transaction update(Transaction transaction) {
        entityManager.merge(transactionMapper.toEntity(transaction));
        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, UPDATE_ACTION, SUCCESS_STATUS, List.of(ID_DETAIL + transaction.getId())));
        return transaction;
    }

    @Override
    public Optional<Transaction> deleteById(long id) {
        Optional<TransactionEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            TransactionEntity entity = entityOptional.get();
            delete(entity);
            logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, DELETION_ACTION, SUCCESS_STATUS, List.of(ID_DETAIL + id)));
            return Optional.of(transactionMapper.toDto(entity));
        }
        return Optional.empty();
    }
}
