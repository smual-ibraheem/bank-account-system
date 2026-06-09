package bank.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void createAccount_shouldCreateActiveAccountWithInitialBalance() {
        Bank bank = new Bank(1_000_000, 5_000_000);

        Account account = bank.createAccount("Ahmad", 10_000);

        assertNotNull(account.getId());
        assertEquals("Ahmad", account.getOwnerName());
        assertEquals(10_000, account.getBalance());
    }

    @Test
    void deposit_shouldIncreaseAccountBalance() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account account = bank.createAccount("Sara", 10_000);

        Transaction transaction = bank.deposit(account.getId(), 5_000);

        assertEquals(Transaction.TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(15_000, account.getBalance());
    }

    @Test
    void deposit_shouldRejectInvalidAmount() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account account = bank.createAccount("Sara", 10_000);

        Transaction transaction = bank.deposit(account.getId(), 0);

        assertEquals(Transaction.TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(Transaction.RejectionReason.INVALID_AMOUNT, transaction.getRejectionReason());
        assertEquals(10_000, account.getBalance());
    }

    @Test
    void withdraw_shouldDecreaseAccountBalance() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account account = bank.createAccount("Ahmad", 10_000);

        Transaction transaction = bank.withdraw(account.getId(), 3_000);

        assertEquals(Transaction.TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(7_000, account.getBalance());
    }

    @Test
    void withdraw_shouldRejectInsufficientFunds() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account account = bank.createAccount("Ahmad", 2_000);

        Transaction transaction = bank.withdraw(account.getId(), 5_000);

        assertEquals(Transaction.TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(Transaction.RejectionReason.INSUFFICIENT_FUNDS, transaction.getRejectionReason());
        assertEquals(2_000, account.getBalance());
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccounts() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account from = bank.createAccount("Ahmad", 10_000);
        Account to = bank.createAccount("Sara", 2_000);

        Transaction transaction = bank.transfer(from.getId(), to.getId(), 4_000);

        assertEquals(Transaction.TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(6_000, from.getBalance());
        assertEquals(6_000, to.getBalance());
    }

    @Test
    void transfer_shouldRejectSameAccountTransfer() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account account = bank.createAccount("Ahmad", 10_000);

        Transaction transaction = bank.transfer(account.getId(), account.getId(), 1_000);

        assertEquals(Transaction.TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(Transaction.RejectionReason.SAME_ACCOUNT_TRANSFER, transaction.getRejectionReason());
        assertEquals(10_000, account.getBalance());
    }

    @Test
    void transfer_shouldRejectWhenToAccountDoesNotExist() {
        Bank bank = new Bank(1_000_000, 5_000_000);
        Account from = bank.createAccount("Ahmad", 10_000);

        Transaction transaction = bank.transfer(from.getId(), "missing-account-id", 1_000);

        assertEquals(Transaction.TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(Transaction.RejectionReason.ACCOUNT_NOT_FOUND, transaction.getRejectionReason());
        assertEquals(10_000, from.getBalance());
    }

    @Test
    void withdraw_shouldRejectWhenDailyLimitExceeded() {
        Bank bank = new Bank(1_000_000, 5_000);
        Account account = bank.createAccount("Ahmad", 20_000);

        Transaction first = bank.withdraw(account.getId(), 4_000);
        Transaction second = bank.withdraw(account.getId(), 2_000);

        assertEquals(Transaction.TransactionStatus.SUCCESS, first.getStatus());
        assertEquals(Transaction.TransactionStatus.REJECTED, second.getStatus());
        assertEquals(Transaction.RejectionReason.DAILY_LIMIT_EXCEEDED, second.getRejectionReason());
        assertEquals(16_000, account.getBalance());
    }

    @Test
    void deposit_shouldRejectWhenMaxTransactionExceeded() {
        Bank bank = new Bank(1_000, 5_000);
        Account account = bank.createAccount("Sara", 10_000);

        Transaction transaction = bank.deposit(account.getId(), 2_000);

        assertEquals(Transaction.TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(Transaction.RejectionReason.MAX_TX_EXCEEDED, transaction.getRejectionReason());
        assertEquals(10_000, account.getBalance());
    }

    @Test
    void createAccount_shouldRejectNegativeInitialBalance() {
        Bank bank = new Bank(1_000_000, 5_000_000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bank.createAccount("Ahmad", -1_000)
        );

        assertEquals("Initial balance cannot be negative", exception.getMessage());
    }

    @Test
    void createAccount_shouldRejectEmptyOwnerName() {
        Bank bank = new Bank(1_000_000, 5_000_000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bank.createAccount("   ", 10_000)
        );

        assertEquals("Owner name cannot be empty", exception.getMessage());
    }
}