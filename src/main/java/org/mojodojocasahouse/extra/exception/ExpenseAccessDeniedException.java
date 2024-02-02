package org.mojodojocasahouse.extra.exception;

public class ExpenseAccessDeniedException extends RuntimeException {
    public ExpenseAccessDeniedException() {
        super("You are not the owner of the expense you are trying to access");
    }
}
