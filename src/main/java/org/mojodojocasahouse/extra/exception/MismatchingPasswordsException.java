package org.mojodojocasahouse.extra.exception;

public class MismatchingPasswordsException extends RuntimeException{
    public MismatchingPasswordsException() {
        super("Passwords must match");
    }
}
