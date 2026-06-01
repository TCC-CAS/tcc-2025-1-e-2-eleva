package com.paradacerta.api.service;

import com.paradacerta.api.model.SessaoEstacionamento;
import com.paradacerta.api.model.SessaoStatus;

import java.time.LocalDateTime;

final class ReservaTempoUtils {
    static final long MINUTOS_ANTECIPACAO_BASE_CONFIRMADA = 20L;
    static final long MINUTOS_TOLERANCIA_NO_SHOW_ADMIN = 60L;

    private ReservaTempoUtils() {
    }

    static LocalDateTime inicioDeUsoParaCalculo(SessaoEstacionamento sessao) {
        LocalDateTime previsto = sessao.getInicioReservaPrevisto();
        LocalDateTime confirmacao = sessao.getDataHoraConfirmacao();

        if (previsto != null) {
            if (confirmacaoAntecipadaVinteMinutosOuMais(previsto, confirmacao)) {
                return confirmacao;
            }
            return previsto;
        }
        if (confirmacao != null) {
            return confirmacao;
        }
        return sessao.getHoraEntrada();
    }

    static boolean confirmacaoAntecipadaVinteMinutosOuMais(
            LocalDateTime inicioReservaPrevisto,
            LocalDateTime dataHoraConfirmacao
    ) {
        if (inicioReservaPrevisto == null || dataHoraConfirmacao == null) {
            return false;
        }
        LocalDateTime limite = inicioReservaPrevisto.minusMinutes(MINUTOS_ANTECIPACAO_BASE_CONFIRMADA);
        return !dataHoraConfirmacao.isAfter(limite);
    }

    static LocalDateTime limiteNoShowAdmin(SessaoEstacionamento sessao) {
        LocalDateTime previsto = sessao.getInicioReservaPrevisto();
        return previsto != null ? previsto.plusMinutes(MINUTOS_TOLERANCIA_NO_SHOW_ADMIN) : null;
    }

    static boolean podeFinalizarNoShowAdmin(SessaoEstacionamento sessao, LocalDateTime agora) {
        if (sessao == null || agora == null) {
            return false;
        }
        if (!Boolean.TRUE.equals(sessao.getReservado())) {
            return false;
        }
        if (sessao.getStatus() != SessaoStatus.AGUARDANDO_CONFIRMACAO) {
            return false;
        }
        LocalDateTime limite = limiteNoShowAdmin(sessao);
        return limite != null && !agora.isBefore(limite);
    }
}
