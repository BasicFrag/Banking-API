package br.com.ibm.bankrestapi;

import br.com.ibm.bankrestapi.GlobalExceptionHandler.ContaInvalidaException;
import br.com.ibm.bankrestapi.GlobalExceptionHandler.SaldoInsuficienteException;
import br.com.ibm.bankrestapi.models.ContaBancaria;
import br.com.ibm.bankrestapi.models.ContaCorrente;
import br.com.ibm.bankrestapi.services.ContaCorrenteServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/v1/bank")
@Tag(name="bank")

@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_PLAIN)
public class BankResource {
    List<ContaCorrente> contasCorrentes = new ArrayList<>();
    ContaCorrenteServiceImpl contaCorrenteService = new ContaCorrenteServiceImpl(contasCorrentes);
@GET
@Path("/contas")
@Operation(description = "Busca todas as contas e seus titulares", summary = "Busca as contas cadastradas")
@APIResponses({
        @APIResponse(responseCode = "200", description = "Contas cadastradas retribuÍdas"),
        @APIResponse(responseCode = "204", description = "Nenhuma conta cadastrada para retribuir")
})
public Response listarContas() {
    List <String> todasAsContas = new ArrayList<>();
    String conta;
    for (ContaCorrente contaCorrente: contasCorrentes) {
        conta= "Nome do Cliente: " + contaCorrente.getTitular().getNome() +
                " CPF: " + contaCorrente.getTitular().getCpf() +
                " Número da Conta: " + contaCorrente.getNumeroConta() +
                " Saldo: " + contaCorrente.getSaldo() + "\n";
        todasAsContas.add(conta);
    }
    if (todasAsContas.isEmpty()) {
        return Response.status(Response.Status.NO_CONTENT).entity(todasAsContas).build();
    }
    return Response.status(Response.Status.OK).entity(todasAsContas).build();
}

    @POST
    @Path("/contas")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Cadastra uma nova conta na API", summary = "Cria uma nova conta")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "CREATED; Conta cadastrada com sucesso"),
            @APIResponse(responseCode = "400", description = "BAD REQUEST;Paramêtros 'nome' ou 'cpf' vazios dentro do corpo (Body) da requisição"),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response cadastroConta(@FormParam("nome") String nome, @FormParam("cpf") String cpf) {
        try {
            ContaCorrente contaCorrente = contaCorrenteService.criarConta(nome, cpf);
            contasCorrentes.add(contaCorrente);
            return Response.status(Response.Status.CREATED)
                    .entity(String.format("Número da Conta criada: %s; Nome do Cliente: %s; CPF do Cliente:  %s",
                            contaCorrente.getNumeroConta(), contaCorrente.getTitular().getNome(), contaCorrente.getTitular().getCpf()))
                    .build();
        } catch (ContaInvalidaException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Conta inválida. Por favor, verifique os campos digitados").build();
        }
    }

    @POST
    @Path("/contas/{numeroConta}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Deposita um valor em uma conta cadastrada na API", summary = "Deposita um valor na conta")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK: Valor depósitado com sucesso"),
            @APIResponse(responseCode = "400", description = "BAD REQUEST; Paramêtro 'numeroConta' na URL (Path Parameter) inválido ou conta com esse número não existem na API"),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response deposito(@PathParam("numeroConta") String numeroConta, @FormParam("valor") double valor) {
        try {
            contaCorrenteService.depositar(numeroConta, valor);
            return Response.status(Response.Status.OK).entity("Depósito realizado na conta: " + numeroConta).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(e.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu um erro ao depositar" + valor)
                    .build();
        }

    }

    @GET
    @Path("/contas/{numeroConta}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Retorna o saldo em uma conta cadastrada na API", summary = "Mostra o saldo na conta")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK; Saldo atual na conta retribuído com sucesso"),
            @APIResponse(responseCode = "404", description = "NOT FOUND; Paramêtro 'numeroConta' na URL (Path Parameter) inválido ou conta com esse número não existe na API"),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response saldo(@PathParam("numeroConta") String numeroConta) {
        ContaCorrente conta = contaCorrenteService.getContaPorNumero(numeroConta);
        if (conta != null) {
            return Response.status(Response.Status.OK).entity("Saldo atual da conta " +
                    conta.getNumeroConta() + ": " + conta.getSaldo()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).
                    entity("Conta " + numeroConta + " não encontrada").
                    build();
        }


    }

    @PUT
    @Path("/contas/{numeroConta}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Realiza um saque de um valor em uma conta cadastrada na API", summary = "Saca um valor na conta")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK; Saque relizado com sucesso"),
            @APIResponse(responseCode = "404", description = "NOT FOUND; Paramêtro 'numeroConta' na URL (Path Parameter) inválido ou conta com esse número não existe na API"),
            @APIResponse(responseCode = "400", description = "BAD REQUEST; Paramêtro 'valor' no corpo (Body) da requisição inválido ou conta tem o saldo insuficiente para realizar a operação."),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response saque(@PathParam("numeroConta") String numeroConta, @FormParam("valor") double valor) {
        try {
            contaCorrenteService.sacar(numeroConta, valor);
            return Response.status(Response.Status.OK).entity("Saque de " + valor + " realizado na conta "
                            + numeroConta + " com sucesso! \n" + "Saldo atual: " + contaCorrenteService.getContaPorNumero(numeroConta).getSaldo())
                    .build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SaldoInsuficienteException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PATCH
    @Path("/contas/{contaOrigem}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Transfere um valor em uma conta cadastrada para outra conta cadastrada na API", summary = "Transfere um valor em uma conta para outra conta")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK; Valor depósitado com sucesso"),
            @APIResponse(responseCode = "404", description = "NOT FOUND; Paramêtro 'contaDestino' na query ou 'contaOrigem' na URL (Path Parameter) inválido ou contas com esses números não existe na API"),
            @APIResponse(responseCode = "400", description = "BAD REQUEST; Paramêtro 'valor' no corpo (Body) da requisição inválido ou conta tem o saldo insuficiente para realizar a operação"),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response transferencia(
            @PathParam("contaOrigem") String contaOrigem,
            @QueryParam("contaDestino") String contaDestino,
            @FormParam("valor") double valor
    ) {
        try {
            contaCorrenteService.transferir(contaOrigem, contaDestino, valor);
            return Response.status(Response.Status.OK).entity(String.format("Tranferência de: %.2f da conta %s para conta %s realizada com sucesso!.",valor,contaOrigem,contaDestino))
                    .build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao realizar transferência, tente novamente").build();
        }
    }

    @DELETE
    @Path("/contas/{numeroConta}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Remove uma conta cadastrada na API", summary = "Remove uma conta")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK; Conta removida com sucesso"),
            @APIResponse(responseCode = "404", description = "NOT FOUND; Paramêtro 'numeroConta' na URL (Path Parameter) inválido ou conta com esse número não existe na API"),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR; Erro interno na API")
    })
    public Response deleteConta(@PathParam("numeroConta") String numeroConta) throws ContaInvalidaException {
            ContaCorrente contaCorrente = contaCorrenteService.getContaPorNumero(numeroConta);
        if (contaCorrente != null) {
            //Remove conta do ArrayList
            contasCorrentes.remove(contaCorrente);
            return Response.status(Response.Status.OK)
                    .entity(String.format("Número da Conta deletada: %s; Nome do Cliente: %s; CPF do Cliente:  %s",
                            contaCorrente.getNumeroConta(), contaCorrente.getTitular().getNome(), contaCorrente.getTitular().getCpf()))
                    .build();
        } else if (contaCorrente == null)  {
            ContaInvalidaException ex = new ContaInvalidaException("Conta inválida ou não encontrada!");
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu ao deletar conta, tente novamente.").build();
        }
    }

}
