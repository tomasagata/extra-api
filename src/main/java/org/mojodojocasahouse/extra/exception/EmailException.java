package org.mojodojocasahouse.extra.exception;

public class EmailException extends RuntimeException{
    public EmailException(String message){
        super(message);
    }

    public EmailException() {
        super("An error occurred while sending password recovery email. Try again later");
    }
}
