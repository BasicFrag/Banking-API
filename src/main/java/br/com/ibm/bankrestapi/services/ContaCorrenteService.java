package br.com.ibm.bankrestapi.services;

import br.com.ibm.bankrestapi.GlobalExceptionHandler.ContaInvalidaException;
import br.com.ibm.bankrestapi.GlobalExceptionHandler.SaldoInsuficienteException;
import br.com.ibm.bankrestapi.models.ContaCorrente;

import java.util.List;

public interface ContaCorrenteService {
    ContaCorrente getContaPorNumero(String numeroConta);
    void depositar(String numeroConta, double valor) throws ContaInvalidaException;
    void sacar(String numeroConta, double valor) throws ContaInvalidaException, SaldoInsuficienteException;
    void transferir(String contaOrigem, String contaDestino, double valor) throws ContaInvalidaException, SaldoInsuficienteException;
    ContaCorrente criarConta(String nome, String cpf) throws ContaInvalidaException;

}
