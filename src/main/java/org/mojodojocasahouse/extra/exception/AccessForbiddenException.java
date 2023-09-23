package org.mojodojocasahouse.extra.exception;

public class AccessForbiddenException extends RuntimeException{
    public AccessForbiddenException(){
        super("Not enough privileges to perform solicited action");
    }
}
