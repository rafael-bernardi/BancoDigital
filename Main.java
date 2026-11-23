package BancoDigital;

import BancoDigital.exception.SaldoInsuficienteException;
import BancoDigital.model.Conta;
import BancoDigital.model.ContaCorrente;
import BancoDigital.model.ContaPoupanca;
import BancoDigital.service.BancoService;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final String MENU = """
            BancoDigital | Menu de operações
             1. Abrir conta corrente         6. Consultar extrato
             2. Abrir conta poupança         7. Exportar extrato (.txt)
             3. Depositar                    8. Aplicar rendimento
             4. Sacar                        9. Listar todas as contas
             5. Transferir entre contas     10. Sair
            """;

    public static void main(String[] args) {
        BancoService banco = new BancoService();
        try (Scanner entrada = new Scanner(System.in)) {
            while (true) {
                System.out.println(MENU);
                String opcao = ler(entrada, "Opção");
                try {
                    switch (opcao) {
                        case "1", "2" -> {
                            String numero = ler(entrada, "Número da conta");
                            String titular = ler(entrada, "Titular");
                            Conta nova = opcao.equals("1") ? new ContaCorrente(numero, titular)
                                    : new ContaPoupanca(numero, titular);
                            banco.cadastrarConta(nova);
                            System.out.println("Conta aberta: " + numero + " | " + nova.getTipo());
                        }
                        case "3" -> {
                            Conta conta = selecionarConta(entrada, banco);
                            conta.depositar(lerDecimal(entrada, "Valor do depósito (R$)"));
                            System.out.println("Depósito realizado.");
                        }
                        case "4" -> {
                            Conta conta = selecionarConta(entrada, banco);
                            conta.sacar(lerDecimal(entrada, "Valor do saque (R$)"));
                            System.out.println("Saque realizado.");
                        }
                        case "5" -> {
                            String origem = ler(entrada, "Conta de origem");
                            String destino = ler(entrada, "Conta de destino");
                            banco.transferir(origem, destino, lerDecimal(entrada, "Valor da transferência (R$)"));
                            System.out.println("Transferência realizada.");
                        }
                        case "6" -> selecionarConta(entrada, banco).imprimirExtrato();
                        case "7" -> System.out.println("Extrato salvo em "
                                + banco.exportarExtratoTxt(ler(entrada, "Número da conta")));
                        case "8" -> {
                            Conta conta = selecionarConta(entrada, banco);
                            if (conta instanceof ContaPoupanca poupanca) {
                                poupanca.aplicarRendimento(lerDecimal(entrada, "Taxa de rendimento (%)"));
                                System.out.println("Rendimento aplicado.");
                            } else {
                                System.out.println("O rendimento se aplica apenas à poupança.");
                            }
                        }
                        case "9" -> banco.listarTodasContas();
                        case "10" -> {
                            System.out.println("Sessão encerrada.");
                            return;
                        }
                        default -> System.out.println("Escolha uma opção de 1 a 10.");
                    }
                } catch (SaldoInsuficienteException e) {
                    System.out.println("Operação recusada: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println("Falha ao salvar o extrato: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (NoSuchElementException e) {
            System.out.println("Entrada encerrada.");
        }
    }

    private static Conta selecionarConta(Scanner entrada, BancoService banco) {
        return banco.buscarConta(ler(entrada, "Número da conta"));
    }

    private static String ler(Scanner entrada, String pergunta) {
        System.out.print(pergunta + ": ");
        System.out.flush();
        return entrada.nextLine().strip();
    }

    private static double lerDecimal(Scanner entrada, String pergunta) {
        String texto = ler(entrada, pergunta);
        if (!texto.matches("[0-9]+([.,][0-9]+)?")) {
            throw new IllegalArgumentException("Informe um número decimal, por exemplo 50,25.");
        }
        return Double.parseDouble(texto.replace(',', '.'));
    }
}
