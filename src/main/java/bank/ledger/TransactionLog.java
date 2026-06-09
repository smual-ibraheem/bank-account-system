package bank.ledger;

import bank.core.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionLog (سجل العمليات)
 * ------------------------------
 * مسؤول عن:
 * - تخزين كل العمليات (الناجحة والمرفوضة)
 * - توفير بيانات للـ Audit (التتبع)
 * - دعم حساب Daily Limit (الحد اليومي)
 * <p>
 * لا يحتوي أي منطق قرار (Business Logic)
 */
public class TransactionLog {

    // قائمة بكل العمليات التي حصلت في النظام
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * append (إضافة عملية)
     * يتم تسجيل العملية كما هي، بدون تعديل أو تحقق.
     */
    public void append(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * getDailyTotalForAccount (إجمالي اليوم لحساب معيّن)
     * <p>
     * يحسب مجموع:
     * - WITHDRAW (سحب)
     * - TRANSFER الصادر
     * <p>
     * بشرط:
     * - العملية SUCCESS (ناجحة)
     * - نفس اليوم (System day)
     */
    public long getDailyTotalForAccount(String accountId, LocalDate day) {
        long total = 0;

        for (Transaction tx : transactions) {

            // نتجاهل أي عملية مرفوضة
            if (tx.getStatus() != Transaction.TransactionStatus.SUCCESS) {
                continue;
            }

            // نتجاهل العمليات من أيام أخرى
            if (!tx.getTimestamp().toLocalDate().equals(day)) {
                continue;
            }

            // نحسب السحب للحساب نفسه
            // بعد الحساب ننتقل مباشرة للعملية التالية (لا داعي لفحص شروط أخرى)
            if (tx.getType() == Transaction.TransactionType.WITHDRAW
                    && accountId.equals(tx.getFromAccountId())) {
                total += tx.getAmount();
                continue;
            }

            // نحسب التحويل الصادر فقط
            // هذا آخر شرط داخل الحلقة، لذلك لا نحتاج continue
            if (tx.getType() == Transaction.TransactionType.TRANSFER
                    && accountId.equals(tx.getFromAccountId())) {
                total += tx.getAmount();
            }
        }
        return total;
    }

    public List<Transaction> getRecentTransactionsForAccount(String accountId, int limit) {

        // Guard clause: إذا limit غير صالح، نرجّع قائمة فاضية مباشرة
        if (limit <= 0) {
            return List.of();
        }

        // قائمة لتجميع العمليات المتعلقة بالحساب
        List<Transaction> result = new ArrayList<>();

        // نبدأ من آخر عملية (الأحدث) ونمشي للخلف
        for (int i = transactions.size() - 1;
             i >= 0 && result.size() < limit;
             i--) {

            // جلب العملية الحالية من السجل
            Transaction tx = transactions.get(i);

            // نتحقق إذا الحساب مشارك بالعملية (مرسل أو مستقبل)
            boolean involved =
                    accountId.equals(tx.getFromAccountId()) ||
                            accountId.equals(tx.getToAccountId());

            // إذا العملية تخص الحساب، نضيفها للنتائج
            if (involved) {
                result.add(tx);
            }
        }

        // نرجّع آخر العمليات (من الأحدث للأقدم)
        return result;
    }
}
