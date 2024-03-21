package com.gestion.exception;

public class BibliotecaNotFoundException extends RuntimeException{
    public BibliotecaNotFoundException(String message) {
        super(message);
    }
}
