package org.mojodojocasahouse.extra.exception;

public class SessionAlreadyRevokedException extends RuntimeException {
    public SessionAlreadyRevokedException(){
        super("Session is already revoked");
    }
}
