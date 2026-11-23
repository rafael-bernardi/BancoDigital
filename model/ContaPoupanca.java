package BancoDigital.model;

import BancoDigital.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ContaPoupanca extends Conta {
    public ContaPoupanca(String numeroConta, String titular) {
        super(numeroConta, titular);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValor(valor);
        if (saldo < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente na Conta Poupança.");
        }
        saldo = emCentavos(saldo - valor);
        historico.add(new Transacao("SAQUE", -valor));
    }

    public void aplicarRendimento(double taxaPercentual) {
        if (!Double.isFinite(taxaPercentual) || taxaPercentual <= 0) {
            throw new IllegalArgumentException("A taxa de rendimento deve ser positiva.");
        }
        double rendimento = BigDecimal.valueOf(saldo).multiply(BigDecimal.valueOf(taxaPercentual))
                .movePointLeft(2).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (rendimento > 0) {
            validarDeposito(rendimento);
        }
        saldo = emCentavos(saldo + rendimento);
        historico.add(new Transacao("RENDIMENTO", rendimento));
    }

    @Override
    public String getTipo() { return "Poupança"; }
}
