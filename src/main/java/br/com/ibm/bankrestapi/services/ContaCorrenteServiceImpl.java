package br.com.ibm.bankrestapi.services;


import br.com.ibm.bankrestapi.GlobalExceptionHandler.ContaInvalidaException;
import br.com.ibm.bankrestapi.GlobalExceptionHandler.SaldoInsuficienteException;
import br.com.ibm.bankrestapi.models.Cliente;
import br.com.ibm.bankrestapi.models.ContaBancaria;
import br.com.ibm.bankrestapi.models.ContaCorrente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class ContaCorrenteServiceImpl implements ContaCorrenteService {
    private final List<ContaCorrente> contasCorrentes;
    private static final AtomicInteger contadorContas = new AtomicInteger(1);


    public ContaCorrenteServiceImpl(List<ContaCorrente> contasCorrentes) {
        this.contasCorrentes = contasCorrentes;
    }


    //Implementação da API Stream!
    @Override
    public ContaCorrente getContaPorNumero(String numeroConta) {
        return contasCorrentes.stream()
                .filter(conta -> conta.getNumeroConta().equals(numeroConta))
                .findFirst()
                .orElse(null);
    }



    @Override
    public void depositar(String numeroConta, double valor) throws ContaInvalidaException {
        ContaCorrente conta = getContaPorNumero(numeroConta);
        if (conta == null) {
            throw new ContaInvalidaException("Conta inválida. Por favor, verifique o número da conta.");}
        conta.depositar(valor);
    }

    @Override
    public void sacar(String numeroConta, double valor) throws ContaInvalidaException, SaldoInsuficienteException {
        ContaCorrente conta = getContaPorNumero(numeroConta);
        if (conta != null) {
            if (conta.getSaldo() >= valor) {
                conta.sacar(valor);
            } else {
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
            }
        } else {
            throw new ContaInvalidaException("Conta inválida. Por favor, verifique o número da conta.");
        }
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, double valor) throws ContaInvalidaException, SaldoInsuficienteException {
        ContaBancaria origem = getContaPorNumero(contaOrigem);
        ContaBancaria destino = getContaPorNumero(contaDestino);

        if (origem == null || destino == null) {
            throw new ContaInvalidaException("Conta de origem ou destino inválida. Por favor, verifique os números das contas.");
        }
        if (origem.getSaldo() >= valor) {
            origem.transferir(origem, destino, valor);
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência.");
        }
    }

    @Override
    public ContaCorrente criarConta(String nome, String cpf) throws ContaInvalidaException {
        if (nome == null || nome.isEmpty() || cpf == null || cpf.isEmpty()) {
            throw new ContaInvalidaException("Nome e CPF são obrigatórios.");
        }

        String numeroConta = gerarNumeroContaUnico();

        Cliente cliente = new Cliente(nome, cpf);

        return new ContaCorrente(numeroConta, 0.0, cliente);
    }
    private String gerarNumeroContaUnico() {
        return String.format("%05d", contadorContas.getAndIncrement());
    }
}


