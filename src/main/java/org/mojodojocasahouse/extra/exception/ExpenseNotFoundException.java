package org.mojodojocasahouse.extra.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException() {
        super("Expense not found");
    }

}
