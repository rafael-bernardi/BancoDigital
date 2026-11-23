package BancoDigital.service;

import BancoDigital.exception.SaldoInsuficienteException;
import BancoDigital.model.Conta;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BancoService {
    private final Map<String, Conta> contas = new HashMap<>();

    public void cadastrarConta(Conta conta) {
        if (contas.putIfAbsent(conta.getNumeroConta(), conta) != null) {
            throw new IllegalArgumentException("Já existe uma conta com esse número.");
        }
    }

    public Conta buscarConta(String numeroConta) {
        Conta conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada.");
        }
        return conta;
    }

    public void transferir(String numOrigem, String numDestino, double valor)
            throws SaldoInsuficienteException {
        Conta origem = buscarConta(numOrigem);
        Conta destino = buscarConta(numDestino);
        if (origem == destino) {
            throw new IllegalArgumentException("Escolha contas diferentes para a transferência.");
        }
        destino.validarDeposito(valor);
        origem.sacar(valor);
        destino.depositar(valor);
    }

    public Path exportarExtratoTxt(String numeroConta) throws IOException {
        Conta conta = buscarConta(numeroConta);
        Path arquivo = Path.of("extrato_" + conta.getNumeroConta() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo.toFile(), StandardCharsets.UTF_8))) {
            writer.write(conta.gerarExtrato());
            writer.newLine();
        }
        return arquivo;
    }

    public void listarTodasContas() {
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
        }
        contas.values().stream().sorted(Comparator.comparing(Conta::getNumeroConta))
                .forEach(conta -> System.out.printf(Locale.forLanguageTag("pt-BR"),
                        "%s | %s | %s | R$ %.2f%n", conta.getNumeroConta(), conta.getTipo(),
                        conta.getTitular(), conta.getSaldo()));
    }
}
