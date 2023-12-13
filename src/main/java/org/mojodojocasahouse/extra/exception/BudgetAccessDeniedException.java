package org.mojodojocasahouse.extra.exception;

public class BudgetAccessDeniedException extends RuntimeException {
    public BudgetAccessDeniedException() {
        super("You are not the owner of the budget you are trying to access");
    }
}
