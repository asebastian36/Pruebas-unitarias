package org.asebastian36.junit5app.ejemplos.exceptions;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String msg) {
        super(msg);
    }
}
