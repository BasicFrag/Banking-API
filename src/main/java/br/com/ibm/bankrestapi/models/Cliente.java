package br.com.ibm.bankrestapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cliente {
    private String nome;
    private String cpf;

}
