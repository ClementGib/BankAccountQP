package com.cdx.bas.application.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cdx.bas.domain.text.MessageConstants.BANK_ACCOUNT_START;
import static jakarta.transaction.Transactional.TxType.REQUIRED;

/***
 * persistence implementation for BankAccount entities
 * 
 * @author Clément Gibert
 *
 */
@ApplicationScoped
public class BankAccountRepository implements BankAccountPersistencePort, PanacheRepositoryBase<BankAccountEntity, Long> {
    
    private static final Logger logger = LoggerFactory.getLogger(BankAccountRepository.class);

    BankAccountMapper bankAccountMapper;

    TransactionManager transactionManager;

    EntityManager entityManager;

    @Inject
    public BankAccountRepository(BankAccountMapper bankAccountMapper,
                                 TransactionManager transactionManager,
                                 EntityManager entityManager) {
        this.bankAccountMapper = bankAccountMapper;
        this.transactionManager = transactionManager;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<BankAccount> getAll() {
        return findAll(Sort.by("id")).stream()
                .map(bankAccountEntity -> bankAccountMapper.toDto(bankAccountEntity))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<BankAccount> findById(long id) {
        return findByIdOptional(id).map(bankAccountMapper::toDto);
    }
    
    @Override
    @Transactional
    public BankAccount create(BankAccount bankAccount) {
        entityManager.persist(bankAccountMapper.toEntity(bankAccount));
        logger.info(BANK_ACCOUNT_START + bankAccount.getId() + " created");
        return bankAccount;
    }

    @Override
    @Transactional
    public BankAccount update(BankAccount bankAccount) {
        try {
            BankAccountEntity entity = bankAccountMapper.toEntity(bankAccount);
            getEntityManager();
            BankAccountEntity merge = entityManager.merge(entity);
            bankAccount = bankAccountMapper.toDto(merge);
            logger.info(BANK_ACCOUNT_START + bankAccount.getId() + " updated");
            return bankAccount;
        } catch (UnsupportedOperationException exception) {
            throw new BankAccountException("invalid", exception);
        }
    }
    
    @Override
    @Transactional
    public Optional<BankAccount> deleteById(long id) {
        Optional<BankAccountEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            BankAccountEntity entity = entityOptional.get();
            delete(entity);
            logger.info("BankAccount " + entity.getId() + " deleted");
            return Optional.of(bankAccountMapper.toDto(entity));
        }
        return Optional.empty();
    }
}
