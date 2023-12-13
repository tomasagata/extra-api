package org.mojodojocasahouse.extra.exception;

public class ConflictingBudgetException extends RuntimeException {
    public ConflictingBudgetException() {
        super("A budget for that category already exists within the given dates and categories");
    }
}
