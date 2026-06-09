package bank.core;

import bank.state.AccountState;

public class Account {

    // ===== Identity (هوية الحساب) =====
    private final String id;
    private final String ownerName;

    // ===== Mutable State (حالة قابلة للتغيير – فقط عبر النظام) =====
    private long balance;
    private AccountState state;

    // ===== Constructor (باني) =====
    public Account(final String id, final String ownerName, final long initialBalance) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.state = AccountState.ACTIVE; // الحساب يبدأ نشط
    }

    // ===== Getters (قراءة فقط) =====
    public String getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public AccountState getState() {
        return state;
    }

    // ===== Package-Private Mutators (تغيير داخلي فقط) =====
    // تستخدم فقط من Bank (النظام)

    void setBalance(long balance) {
        this.balance = balance;
    }

    void setState(AccountState state) {
        this.state = state;
    }
}