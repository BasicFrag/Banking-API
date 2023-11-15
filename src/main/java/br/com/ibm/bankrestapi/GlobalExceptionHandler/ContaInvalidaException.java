package br.com.ibm.bankrestapi.GlobalExceptionHandler;

public class ContaInvalidaException extends Exception{
    public ContaInvalidaException(String mensagem) {super(mensagem);}
}

