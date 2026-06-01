package com.paradacerta.api.service;

final class VagasReservaUtils {

    private VagasReservaUtils() {
    }

    static int calcularDisponiveisParaReserva(Integer reservaveis, Integer reservadas, Integer disponiveis) {
        int limiteReservas = Math.max(0, valor(reservaveis) - valor(reservadas));
        int vagasLivres = Math.max(0, valor(disponiveis));
        return Math.min(limiteReservas, vagasLivres);
    }

    private static int valor(Integer numero) {
        return numero == null ? 0 : numero;
    }
}
