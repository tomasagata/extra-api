package org.mojodojocasahouse.extra.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException() {
        super("Budget not found");
    }
}
