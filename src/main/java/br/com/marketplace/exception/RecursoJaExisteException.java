package br.com.marketplace.exception;

public class RecursoJaExisteException extends RuntimeException {

    public RecursoJaExisteException(String message) {
      super(message);
    }
}
