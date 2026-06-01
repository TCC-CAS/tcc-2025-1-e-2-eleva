package com.paradacerta.api.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VagasReservaUtilsTest {

    @Test
    void disponibilidadeDeReservaAcompanhaMenorValorEntreLimiteRestanteEVagasLivres() {
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(10, 0, 3)).isEqualTo(3);
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(10, 4, 3)).isEqualTo(3);
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(10, 8, 6)).isEqualTo(2);
    }

    @Test
    void disponibilidadeNaoFicaNegativa() {
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(5, 7, 10)).isZero();
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(null, null, null)).isZero();
        assertThat(VagasReservaUtils.calcularDisponiveisParaReserva(5, 1, -2)).isZero();
    }
}
