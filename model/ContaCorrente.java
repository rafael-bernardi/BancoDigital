package BancoDigital.model;

import BancoDigital.exception.SaldoInsuficienteException;

public final class ContaCorrente extends Conta {
    private final double limiteChequeEspecial = 500.0;
    private final double taxaSaque = 2.50;

    public ContaCorrente(String numeroConta, String titular) {
        super(numeroConta, titular);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValor(valor);
        double total = emCentavos(valor + taxaSaque);
        if (emCentavos(saldo + limiteChequeEspecial) < total) {
            throw new SaldoInsuficienteException("Saldo e limite insuficientes.");
        }
        saldo = emCentavos(saldo - total);
        historico.add(new Transacao("SAQUE", -valor));
        historico.add(new Transacao("TARIFA DE SAQUE", -taxaSaque));
    }

    @Override
    public String getTipo() { return "Corrente"; }
}
