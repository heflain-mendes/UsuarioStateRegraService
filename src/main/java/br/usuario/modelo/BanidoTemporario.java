package br.usuario.modelo;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class BanidoTemporario implements Estado {
    private final Duration tempoTotalBanimento;
    //region Versão com o problema
//    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    public BanidoTemporario(final Usuario usuario) {
    //endregion

    //region Versão corrigida
    private final ScheduledExecutorService scheduler;


    public BanidoTemporario(final Usuario usuario) {
        this.scheduler = Executors.newScheduledThreadPool(1);
    //endregion
        this.tempoTotalBanimento = Duration.ofSeconds(30);
        usuario.setTempoBanimento(tempoTotalBanimento);

        scheduler.scheduleAtFixedRate(new Runnable() {
            private long tempoRestante = tempoTotalBanimento.getSeconds();

            @Override
            public void run() {
                if (tempoRestante > 0) {
                    usuario.setTempoBanimento(Duration.ofSeconds(--tempoRestante));
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (usuario.getNumeroDeAdvertencias() > 2) {
                    usuario.setEstado(new BanidoDefinitivo());
                } else {
                    usuario.setEstado(new Ativo());
                }
                scheduler.shutdown();
            }
        }, tempoTotalBanimento.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void ativar(Usuario usuario) {
        throw new IllegalStateException("Usuário ainda está banido temporariamente");
    }

    @Override
    public void desativar(Usuario usuario) {
        throw new IllegalStateException("Usuário banido temporariamente não pode ser desativado");
    }

    @Override
    public void advertir(Usuario usuario) {
        throw new IllegalStateException("Usuário banido temporariamente não pode ser advertido");
    }
}
