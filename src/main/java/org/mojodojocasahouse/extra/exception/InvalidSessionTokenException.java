package org.mojodojocasahouse.extra.exception;

public class InvalidSessionTokenException extends RuntimeException{

    public InvalidSessionTokenException(){
        super("Session is invalid or expired");
    }

}
