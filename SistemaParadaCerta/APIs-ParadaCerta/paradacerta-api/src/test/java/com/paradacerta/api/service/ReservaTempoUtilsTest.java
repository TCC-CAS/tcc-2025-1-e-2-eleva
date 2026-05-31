package com.paradacerta.api.service;

import com.paradacerta.api.model.SessaoEstacionamento;
import com.paradacerta.api.model.SessaoStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservaTempoUtilsTest {

    @Test
    void usaConfirmacaoComoBaseQuandoEntradaFoiVinteMinutosOuMaisAntesDoPrevisto() {
        LocalDateTime previsto = LocalDateTime.of(2026, 5, 31, 18, 0);
        LocalDateTime confirmacao = LocalDateTime.of(2026, 5, 31, 17, 40);
        SessaoEstacionamento sessao = reserva(previsto, confirmacao);

        assertThat(ReservaTempoUtils.inicioDeUsoParaCalculo(sessao)).isEqualTo(confirmacao);
    }

    @Test
    void mantemHorarioPrevistoQuandoEntradaAntecipadaFoiMenorQueVinteMinutos() {
        LocalDateTime previsto = LocalDateTime.of(2026, 5, 31, 18, 0);
        LocalDateTime confirmacao = LocalDateTime.of(2026, 5, 31, 17, 41);
        SessaoEstacionamento sessao = reserva(previsto, confirmacao);

        assertThat(ReservaTempoUtils.inicioDeUsoParaCalculo(sessao)).isEqualTo(previsto);
    }

    @Test
    void mantemHorarioPrevistoQuandoMotoristaChegaNoHorarioOuDepois() {
        LocalDateTime previsto = LocalDateTime.of(2026, 5, 31, 18, 0);
        SessaoEstacionamento noHorario = reserva(previsto, previsto);
        SessaoEstacionamento depois = reserva(previsto, previsto.plusMinutes(10));

        assertThat(ReservaTempoUtils.inicioDeUsoParaCalculo(noHorario)).isEqualTo(previsto);
        assertThat(ReservaTempoUtils.inicioDeUsoParaCalculo(depois)).isEqualTo(previsto);
    }

    @Test
    void liberaFinalizacaoNoShowAdminApenasDepoisDeUmaHoraDoHorarioPrevisto() {
        LocalDateTime previsto = LocalDateTime.of(2026, 5, 31, 18, 0);
        SessaoEstacionamento sessao = reserva(previsto, null);
        sessao.setStatus(SessaoStatus.AGUARDANDO_CONFIRMACAO);
        sessao.setReservado(true);

        assertThat(ReservaTempoUtils.podeFinalizarNoShowAdmin(sessao, previsto.plusMinutes(59))).isFalse();
        assertThat(ReservaTempoUtils.podeFinalizarNoShowAdmin(sessao, previsto.plusHours(1))).isTrue();
    }

    private static SessaoEstacionamento reserva(LocalDateTime previsto, LocalDateTime confirmacao) {
        SessaoEstacionamento sessao = new SessaoEstacionamento();
        sessao.setInicioReservaPrevisto(previsto);
        sessao.setDataHoraConfirmacao(confirmacao);
        sessao.setHoraEntrada(previsto.minusMinutes(5));
        return sessao;
    }
}
