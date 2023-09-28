package org.mojodojocasahouse.extra.exception;

public class ExistingUserEmailException extends RuntimeException{

    public ExistingUserEmailException() {
        super("User email already registered");
    }

}
