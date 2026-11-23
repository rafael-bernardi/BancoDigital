package BancoDigital.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Transacao {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private final LocalDateTime dataHora;
    private final String tipo;
    private final double valor;

    public Transacao(String tipo, double valor) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return String.format(BRASIL, "%s | %s | R$ %.2f", dataHora.format(FORMATO), tipo, valor);
    }
}
