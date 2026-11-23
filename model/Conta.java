package BancoDigital.model;

import BancoDigital.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public abstract class Conta {
    protected final String numeroConta;
    protected final String titular;
    protected double saldo;
    protected final List<Transacao> historico;

    protected Conta(String numeroConta, String titular) {
        if (numeroConta == null || !numeroConta.matches("[0-9]+(-[0-9]+)?")) {
            throw new IllegalArgumentException("Use um número de conta como 1001 ou 1001-2.");
        }
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do titular.");
        }
        this.numeroConta = numeroConta;
        this.titular = titular.strip();
        this.saldo = 0.0;
        this.historico = new ArrayList<>();
    }

    protected static void validarValor(double valor) {
        if (!Double.isFinite(valor) || valor <= 0
                || BigDecimal.valueOf(valor).stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Informe um valor positivo com até duas casas decimais.");
        }
    }

    protected static double emCentavos(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public final void validarDeposito(double valor) {
        validarValor(valor);
        // Mantém o saldo na faixa em que double consegue distinguir cada centavo.
        if (valor > 1_000_000_000_000.0 || saldo + valor > 1_000_000_000_000.0) {
            throw new IllegalArgumentException("Valor acima do limite de saldo do simulador.");
        }
    }

    public final void depositar(double valor) {
        validarDeposito(valor);
        saldo = emCentavos(saldo + valor);
        historico.add(new Transacao("DEPÓSITO", valor));
    }

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract String getTipo();

    public final String gerarExtrato() {
        StringBuilder texto = new StringBuilder();
        texto.append("Extrato da conta ").append(numeroConta).append(" | ").append(getTipo()).append('\n');
        texto.append("Titular: ").append(titular).append('\n');
        if (historico.isEmpty()) {
            texto.append("Nenhuma movimentação.\n");
        }
        for (Transacao transacao : historico) {
            texto.append(transacao).append('\n');
        }
        return texto.append(String.format(Transacao.BRASIL, "Saldo atual: R$ %.2f", saldo)).toString();
    }

    public final void imprimirExtrato() {
        System.out.println(gerarExtrato());
    }

    public String getNumeroConta() { return numeroConta; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }
    public List<Transacao> getHistorico() { return List.copyOf(historico); }
}
