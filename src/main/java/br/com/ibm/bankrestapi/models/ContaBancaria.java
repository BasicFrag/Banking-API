package br.com.ibm.bankrestapi.models;

import br.com.ibm.bankrestapi.GlobalExceptionHandler.SaldoInsuficienteException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaBancaria {
    private String numeroConta;
    private Double saldo;
    private Cliente titular;
    private static final double TAXA_DE_TRANSFERENCIA = 0.001;

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque inválido.");
        }
        if (saldo >= valor) {
            saldo -= valor;
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
    }

    public void depositar(double valor) {
        this.saldo += valor;
        System.out.printf("Valor de %.2f depositado na conta de número %s \n", valor, this.numeroConta);
    }

    public void transferir(ContaBancaria origem, ContaBancaria destino, double valor) {
        try {
            if (origem.getSaldo() < valor) {
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência.");
            }
            origem.setSaldo(origem.getSaldo() - valor - (valor * TAXA_DE_TRANSFERENCIA ));
            destino.setSaldo(destino.getSaldo() + valor);
        } catch (SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
    }
}
