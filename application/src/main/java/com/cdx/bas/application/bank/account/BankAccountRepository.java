package com.cdx.bas.application.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import com.cdx.bas.domain.message.MessageFormatter;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;

/***
 * persistence implementation for BankAccount entities
 *
 * @author Clément Gibert
 *
 */
@ApplicationScoped
public class BankAccountRepository implements BankAccountPersistencePort, PanacheRepositoryBase<BankAccountEntity, Long> {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountRepository.class);
    public static final String ID_FIELD = "id";

    BankAccountMapper bankAccountMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public BankAccountRepository(BankAccountMapper bankAccountMapper,
                                 EntityManager entityManager) {
        this.bankAccountMapper = bankAccountMapper;
        this.entityManager = entityManager;
    }

    @Override
    public List<BankAccount> getAll() {
        return findAll(Sort.by(ID_FIELD)).stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Override
    public Optional<BankAccount> findById(long id) {
        return findByIdOptional(id).map(bankAccountMapper::toDto);
    }

    @Override
    public BankAccount create(BankAccount bankAccount) {
        BankAccountEntity entity = bankAccountMapper.toEntity(bankAccount);
        persist(entity);
        logger.debug(MessageFormatter.format(BANK_ACCOUNT_CONTEXT, CREATION_ACTION, SUCCESS_STATUS));
        return bankAccount;
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        try {
            bankAccount = bankAccountMapper.toDto(entityManager.merge(bankAccountMapper.toEntity(bankAccount)));
            logger.debug(MessageFormatter.format(BANK_ACCOUNT_CONTEXT, UPDATE_ACTION, SUCCESS_STATUS));
            return bankAccount;
        } catch (UnsupportedOperationException exception) {
            throw new BankAccountException(MessageFormatter.format(BANK_ACCOUNT_CONTEXT, UPDATE_ACTION, FAILED_STATUS));
        }
    }

    @Override
    public Optional<BankAccount> deleteById(long id) {
        Optional<BankAccountEntity> entityOptional = findByIdOptional(id);
        if (entityOptional.isPresent()) {
            BankAccountEntity entity = entityOptional.get();
            delete(entity);
            logger.debug(MessageFormatter.format(BANK_ACCOUNT_CONTEXT, DELETION_ACTION, SUCCESS_STATUS,
                    List.of(BANK_ACCOUNT_ID_DETAIL + id)));
            return Optional.of(bankAccountMapper.toDto(entity));
        }
        return Optional.empty();
    }
}
