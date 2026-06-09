package bank;

import bank.core.Account;
import bank.core.Bank;
import bank.core.Transaction;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // إعدادات البنك (قيم منطقية للتجربة في الكونسول)
        long maxTransactionAmount = 1_000_000;
        long dailyLimit = 5_000_000;

        Bank bank = new Bank(maxTransactionAmount, dailyLimit);
        Scanner scanner = new Scanner(System.in);

        // Seed بسيط لتسهيل التجربة (اختياري)
        bank.createAccount("Ahmad", 10_000);
        bank.createAccount("Sara", 25_000);

        boolean running = true;

        while (running) {

            printMenu();

            int choice = readIntOrRetry(scanner, "Choose option: ");
            if (choice == -1) {
                // إدخال غير رقمي → نعيد الحلقة
                continue;
            }

            switch (choice) {
                case 1 -> printAccounts(bank.listAccounts());
                case 2 -> createAccount(bank, scanner);
                case 3 -> showAccountDetails(bank, scanner);
                case 4 -> deposit(bank, scanner);
                case 5 -> withdraw(bank, scanner);
                case 6 -> transfer(bank, scanner);
                case 7 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    // =========================
    // Menu (UI only)
    // =========================
    private static void printMenu() {
        System.out.println("\n=========== BANK SYSTEM ===========");
        System.out.println("1) List Accounts");
        System.out.println("2) Create Account");
        System.out.println("3) Account Details");
        System.out.println("4) Deposit");
        System.out.println("5) Withdraw");
        System.out.println("6) Transfer");
        System.out.println("7) Exit");
        System.out.println("----------------------------------");
    }

    // =========================
    // List Accounts (UI only)
    // =========================
    private static void printAccounts(List<Account> accounts) {

        System.out.println("\n--- Accounts List ---");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Account account : accounts) {
            System.out.printf(
                    "ID: %s | Owner: %s | Balance: %d | State: %s%n",
                    account.getId(),
                    account.getOwnerName(),
                    account.getBalance(),
                    account.getState()
            );
        }
    }

    // =========================
    // Create Account (UI only)
    // =========================
    private static void createAccount(Bank bank, Scanner scanner) {

        System.out.print("Enter owner name: ");
        String ownerName = scanner.nextLine().trim();

        // الرصيد الابتدائي اختياري (Enter = 0)
        Long initialBalance = readLongOrNull(scanner, "Enter initial balance (Enter for 0): ", true);
        if (initialBalance == null) {
            System.out.println("Invalid amount. Initial balance set to 0.");
            initialBalance = 0L;
        }

        try {
            // Bank يحمي النظام من الداخل
            Account account = bank.createAccount(ownerName, initialBalance);

            System.out.println("\nAccount created successfully.");
            System.out.println("Account ID: " + account.getId());
            System.out.println("Owner     : " + account.getOwnerName());
            System.out.println("Balance   : " + account.getBalance());

        } catch (IllegalArgumentException e) {
            System.out.println("Account creation failed: " + e.getMessage());
        }
    }

    // =========================
    // Account Details (UI only)
    // =========================
    private static void showAccountDetails(Bank bank, Scanner scanner) {

        // عرض الحسابات أولًا لتسهيل اختيار UUID
        printAccounts(bank.listAccounts());

        System.out.print("Enter account ID: ");
        String accountId = scanner.nextLine().trim();

        if (accountId.isEmpty()) {
            System.out.println("Account ID cannot be empty.");
            return;
        }

        Account account = bank.getAccount(accountId);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        int limit = 5; // افتراضي
        Integer userLimit = readIntWithDefault(scanner,
                "How many recent transactions to show? (Enter for 5): ",
                5
        );

        // قيد بسيط لمنع spam على الكونسول
        if (userLimit < 1 || userLimit > 50) {
            System.out.println("Limit must be between 1 and 50. Using 5.");
            limit = 5;
        } else {
            limit = userLimit;
        }

        System.out.println("\n--- Account Details ---");
        System.out.println("ID      : " + account.getId());
        System.out.println("Owner   : " + account.getOwnerName());
        System.out.println("Balance : " + account.getBalance());
        System.out.println("State   : " + account.getState());

        List<Transaction> recent = bank.getRecentTransactions(accountId, limit);

        System.out.println("\n--- Recent Transactions (max " + limit + ") ---");
        if (recent.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction tx : recent) {
            String line = String.format(
                    "%s | %s | Amount: %d | Status: %s",
                    tx.getTimestamp(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getStatus()
            );

            // لو مرفوضة نعرض سبب الرفض
            if (tx.getStatus() == Transaction.TransactionStatus.REJECTED) {
                line += " | Reason: " + tx.getRejectionReason();
            }

            // لو تحويل نعرض من/إلى (لتكون واضحة)
            if (tx.getType() == Transaction.TransactionType.TRANSFER) {
                line += String.format(
                        " | From: %s -> To: %s",
                        tx.getFromAccountId(),
                        tx.getToAccountId()
                );
            }

            System.out.println(line);
        }
    }

    // =========================
    // Deposit / Withdraw / Transfer (UI only)
    // =========================
    private static void deposit(Bank bank, Scanner scanner) {

        printAccounts(bank.listAccounts());

        System.out.print("Enter account ID: ");
        String accountId = scanner.nextLine().trim();

        if (accountId.isEmpty()) {
            System.out.println("Account ID cannot be empty.");
            return;
        }

        Long amount = readLongOrNull(scanner, "Enter amount to deposit: ", false);
        if (amount == null) {
            System.out.println("Invalid amount.");
            return;
        }

        // مهم: منع 0 أو قيمة سالبة (UX check)
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        Transaction tx = bank.deposit(accountId, amount);
        printTransactionResult(tx);
    }

    private static void withdraw(Bank bank, Scanner scanner) {

        printAccounts(bank.listAccounts());

        System.out.print("Enter account ID: ");
        String accountId = scanner.nextLine().trim();

        if (accountId.isEmpty()) {
            System.out.println("Account ID cannot be empty.");
            return;
        }

        Long amount = readLongOrNull(scanner, "Enter amount to withdraw: ", false);
        if (amount == null) {
            System.out.println("Invalid amount.");
            return;
        }

        // مهم: منع 0 أو قيمة سالبة (UX check)
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        Transaction tx = bank.withdraw(accountId, amount);
        printTransactionResult(tx);
    }

    private static void transfer(Bank bank, Scanner scanner) {

        printAccounts(bank.listAccounts());

        System.out.print("Enter FROM account ID: ");
        String fromId = scanner.nextLine().trim();
        if (fromId.isEmpty()) {
            System.out.println("From account ID cannot be empty.");
            return;
        }

        System.out.print("Enter TO account ID: ");
        String toId = scanner.nextLine().trim();
        if (toId.isEmpty()) {
            System.out.println("To account ID cannot be empty.");
            return;
        }

        // تحقق UX بسيط (بدون business rules)
        if (fromId.equals(toId)) {
            System.out.println("Cannot transfer to the same account.");
            return;
        }

        Long amount = readLongOrNull(scanner, "Enter amount to transfer: ", false);
        if (amount == null) {
            System.out.println("Invalid amount.");
            return;
        }

        // مهم: منع 0 أو قيمة سالبة (UX check)
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        Transaction tx = bank.transfer(fromId, toId, amount);
        printTransactionResult(tx);
    }

    // =========================
    // Transaction Result (UI only)
    // =========================
    private static void printTransactionResult(Transaction tx) {

        System.out.println("\n--- Transaction Result ---");

        // معلومات أساسية (مفيدة دائمًا)
        System.out.println("Transaction ID: " + tx.getId());
        System.out.println("Type          : " + tx.getType());
        System.out.println("Amount        : " + tx.getAmount());
        System.out.println("Status        : " + tx.getStatus());

        // سبب الرفض عند الحاجة
        if (tx.getStatus() == Transaction.TransactionStatus.REJECTED) {
            System.out.println("Rejection     : " + tx.getRejectionReason());
        }

        // تفاصيل التحويل فقط إذا كانت العملية Transfer
        if (tx.getType() == Transaction.TransactionType.TRANSFER) {
            System.out.println("From Account  : " + tx.getFromAccountId());
            System.out.println("To Account    : " + tx.getToAccountId());
        }
    }

    // =========================
    // Small Input Helpers (UI only)
    // =========================

    // قراءة رقم خيار المينو: يرجّع -1 إذا الإدخال غير صالح
    private static int readIntOrRetry(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid option. Please enter a number.");
            return -1;
        }
    }

    // قراءة رقم مع Default (Enter -> default)
    private static Integer readIntWithDefault(Scanner scanner, String prompt, int defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // نخليها default بدل ما نعلّق المستخدم
            System.out.println("Invalid number. Using " + defaultValue + ".");
            return defaultValue;
        }
    }

    // قراءة long: إذا allowEmpty=true و المستخدم ضغط Enter -> ترجع 0
    // إذا الإدخال غير رقمي -> ترجع null (لتقرر الرسالة في المكان المناسب)
    private static Long readLongOrNull(Scanner scanner, String prompt, boolean allowEmpty) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (allowEmpty && input.isEmpty()) {
            return 0L;
        }

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}