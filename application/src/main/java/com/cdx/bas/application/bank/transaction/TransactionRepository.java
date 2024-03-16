package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional
    public Optional<Transaction> findById(long id) {
        return findByIdOptional(id).map(transactionMapper::toDto);
    }

    @Override
    @Transactional
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
    @Transactional
    public Queue<Transaction> findUnprocessedTransactions() {
        return find("#TransactionEntity.findUnprocessed",
                Parameters.with("status", TransactionStatus.UNPROCESSED).map())
                .list()
                .stream().map(transactionMapper::toDto)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    @Override
    @Transactional
    public void create(Transaction transaction) {
        entityManager.persist(transactionMapper.toEntity(transaction));
        logger.info("Transaction from " + transaction.getEmitterAccountId() + " to " + transaction.getReceiverAccountId() + " created");
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction) {
        entityManager.merge(transactionMapper.toEntity(transaction));
        logger.info("Transaction " + transaction.getId() + " updated");
        return transaction;
    }

    @Override
    @Transactional
    public Optional<Transaction> deleteById(long id) {
        Optional<TransactionEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            TransactionEntity entity = entityOptional.get();
            delete(entity);
            logger.info("Transaction " + entity.getId() + " deleted");
            return Optional.of(transactionMapper.toDto(entity));
        }
        return Optional.empty();
    }
}
