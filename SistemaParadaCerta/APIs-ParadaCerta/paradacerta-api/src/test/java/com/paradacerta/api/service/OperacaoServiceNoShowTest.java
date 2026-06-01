package com.paradacerta.api.service;

import com.paradacerta.api.exception.ConflictException;
import com.paradacerta.api.model.ApiResponse;
import com.paradacerta.api.model.SessaoEstacionamento;
import com.paradacerta.api.model.SessaoStatus;
import com.paradacerta.api.repository.AdmEstacionamentoRepository;
import com.paradacerta.api.repository.ClienteRepository;
import com.paradacerta.api.repository.EstacionamentoRepository;
import com.paradacerta.api.repository.SessaoRepository;
import com.paradacerta.api.repository.VagasEstacionamentoRepository;
import com.paradacerta.api.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperacaoServiceNoShowTest {
    private static final ZoneId ZONE_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final SessaoRepository sessaoRepository = mock(SessaoRepository.class);
    private final OperacaoService service = new OperacaoService(
            sessaoRepository,
            mock(EstacionamentoRepository.class),
            mock(VagasEstacionamentoRepository.class),
            mock(ClienteRepository.class),
            mock(VeiculoRepository.class),
            mock(AdmEstacionamentoRepository.class),
            mock(PlanoService.class),
            mock(EmailService.class)
    );

    @Test
    void bloqueiaFinalizacaoNoShowAntesDeUmaHoraDoHorarioPrevisto() {
        SessaoEstacionamento sessao = reservaAguardando(LocalDateTime.now(ZONE_SAO_PAULO).minusMinutes(50));
        when(sessaoRepository.findById(10L)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.encerrarSessao(10L, BigDecimal.ZERO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("prazo de chegada");

        verify(sessaoRepository, never()).save(sessao);
    }

    @Test
    void finalizaNoShowDepoisDeUmaHoraMantendoValorAntecipado() {
        SessaoEstacionamento sessao = reservaAguardando(LocalDateTime.now(ZONE_SAO_PAULO).minusMinutes(70));
        when(sessaoRepository.findById(10L)).thenReturn(Optional.of(sessao));

        ApiResponse resposta = service.encerrarSessao(10L, BigDecimal.TEN);

        assertThat(resposta.isSucesso()).isTrue();
        assertThat(sessao.getStatus()).isEqualTo(SessaoStatus.ENCERRADA);
        assertThat(sessao.getHoraSaida()).isNotNull();
        assertThat(sessao.getHoraPagamento()).isNotNull();
        assertThat(sessao.getValorPago()).isEqualByComparingTo("25.00");
        assertThat(sessao.getValorFinalCalculado()).isEqualByComparingTo("25.00");
        assertThat(sessao.getValorRestanteCobrado()).isEqualByComparingTo("0.00");
        verify(sessaoRepository).save(sessao);
    }

    private static SessaoEstacionamento reservaAguardando(LocalDateTime inicioReservaPrevisto) {
        SessaoEstacionamento sessao = new SessaoEstacionamento();
        sessao.setId(10L);
        sessao.setReservado(true);
        sessao.setStatus(SessaoStatus.AGUARDANDO_CONFIRMACAO);
        sessao.setInicioReservaPrevisto(inicioReservaPrevisto);
        sessao.setValorPago(new BigDecimal("25.00"));
        return sessao;
    }
}
