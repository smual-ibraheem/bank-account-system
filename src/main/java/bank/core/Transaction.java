package bank.core;

import java.time.LocalDateTime;

/**
 * Transaction (عملية)
 * -------------------
 * يمثل سجل (Record) لعملية حصلت في النظام.
 *<p>
 * - Immutable (غير قابل للتغيير)
 * - لا يحتوي أي منطق أعمال (Business Logic)
 * - يُستخدم لأغراض التتبع (Audit) والحسابات الزمنية (Daily Limit)
 */
public class Transaction {

    // ===== Identity & Core Data =====
    private final String id;                       // معرّف العملية
    private final TransactionType type;            // نوع العملية
    private final long amount;                     // المبلغ
    private final LocalDateTime timestamp;         // وقت وتاريخ العملية

    // ===== Result =====
    private final TransactionStatus status;         // نتيجة العملية
    private final RejectionReason rejectionReason;  // سبب الرفض (إن وجد)

    // ===== Related Accounts =====
    private final String fromAccountId;             // الحساب المصدر
    private final String toAccountId;               // الحساب الهدف

    /**
     * Constructor
     * كل القيم تُحدَّد عند الإنشاء ولا تتغير لاحقًا.
     */
    public Transaction(
            String id,
            TransactionType type,
            long amount,
            LocalDateTime timestamp,
            TransactionStatus status,
            RejectionReason rejectionReason,
            String fromAccountId,
            String toAccountId
    ) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    // ===== Getters (Read-Only Access) =====

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    // ===== Enums =====

    /**
     * TransactionType (نوع العملية)
     */
    public enum TransactionType {
        DEPOSIT,    // إيداع
        WITHDRAW,   // سحب
        TRANSFER    // تحويل
    }

    /**
     * TransactionStatus (نتيجة العملية)
     */
    public enum TransactionStatus {
        SUCCESS,    // نجحت
        REJECTED    // مرفوضة
    }

    /**
     * RejectionReason (سبب الرفض)
     * يُستخدم فقط إذا كانت الحالة REJECTED
     */
    public enum RejectionReason {
        INVALID_AMOUNT,
        ACCOUNT_NOT_FOUND,
        ACCOUNT_CLOSED,
        ACCOUNT_FROZEN,
        INSUFFICIENT_FUNDS,
        SAME_ACCOUNT_TRANSFER,
        MAX_TX_EXCEEDED,
        DAILY_LIMIT_EXCEEDED
    }
}
