package com.vkostylev;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BankTransaction {
    public enum operationType {DEBIT, CREDIT;};

    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private operationType operationType;
    private String accountId;
    private String transactionDescription;

    public String toString(){
        return operationType + " операция от " + date + ": " + amount + ". Назначение: " + transactionDescription + ". Счет №" + accountId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setOperationType(BankTransaction.operationType operationType) {
        this.operationType = operationType;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }
}
