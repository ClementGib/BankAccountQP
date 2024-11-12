package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BankAccountFactoryTest {

    @Test
    void createBankAccount_shouldReturnNull_whenAccountTypeIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> BankAccountFactory.createBankAccount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unexpected account type null");
    }

    @Test
    void createBankAccount_shouldReturnCheckingBankAccountInstance_whenAccountTypeIsChecking() {
        // Act
        BankAccount bankAccount = BankAccountFactory.createBankAccount(AccountType.CHECKING);

        // Assert
        assertThat(bankAccount).isInstanceOf(CheckingBankAccount.class);
    }

    @Test
    void createBankAccount_shouldReturnSavingBankAccountInstance_whenAccountTypeIsSaving() {
        // Act
        BankAccount bankAccount = BankAccountFactory.createBankAccount(AccountType.SAVING);

        // Assert
        assertThat(bankAccount).isInstanceOf(SavingBankAccount.class);
    }

    @Test
    void createBankAccount_shouldReturnMMABankAccountInstance_whenAccountTypeIsMMA() {
        // Act
        BankAccount bankAccount = BankAccountFactory.createBankAccount(AccountType.MMA);

        // Assert
        assertThat(bankAccount).isInstanceOf(MMABankAccount.class);
    }

    @Test
    void createBankAccount_shouldThrowException_whenAccountTypeIsUnknown() {
        // Act & Assert
        assertThatThrownBy(() -> BankAccountFactory.createBankAccount(AccountType.OTHER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unexpected account type OTHER");
    }

    @Test
    void constructor_shouldThrowException_whenInvoked() throws NoSuchMethodException {
        // Arrange
        Constructor<BankAccountFactory> constructor = BankAccountFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act & Assert
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .getCause()
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Utility class");
    }
}