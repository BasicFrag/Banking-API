package br.com.ibm.bankrestapi.GlobalExceptionHandler;

public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
