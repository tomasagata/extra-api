package org.mojodojocasahouse.extra.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(){
        super("Invalid Authentication Credentials");
    }
}
