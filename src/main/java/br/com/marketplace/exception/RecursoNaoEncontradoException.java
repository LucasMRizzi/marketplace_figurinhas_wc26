package br.com.marketplace.exception;

public class RecursoNaoEncontradoException extends RuntimeException{

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
