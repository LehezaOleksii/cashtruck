package com.projects.oleksii.leheza.cashtruck.exception;

public class DomainException extends RuntimeException{
    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
