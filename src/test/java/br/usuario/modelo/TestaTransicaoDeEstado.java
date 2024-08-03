package br.usuario.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestaTransicaoDeEstado {
    @Test
    public void TempoMaximoTransicaoEstadoCronometradaTest() {
        var deltaMillissegundos = 500;
        var tempoMillissegundo = 30000;
        var tempoMillissegundoMaximoAceito = tempoMillissegundo + deltaMillissegundos;


        var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");

        RegraUsuarioService.ativar(usuario, adminitrador);
        RegraUsuarioService.advertir(usuario, adminitrador);

        try {
            RegraUsuarioService.advertir(usuario, adminitrador);
            Thread.sleep(tempoMillissegundoMaximoAceito);
            var estado = usuario.getNomeEstado();

            assertEquals(estado, Ativo.class.getSimpleName(), "O estado foi alterado dentro do tempo máximo");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        RegraUsuarioService.advertir(usuario, adminitrador);
        var estado = usuario.getNomeEstado();
        assertEquals(estado, BanidoDefinitivo.class.getSimpleName(), "O estado foi alterado para BanidoDefinitivo");
    }

    @Test
    public void TempoMinimoTransicaoEstadoCronometradaTest() {
        var deltaMillissegundos = 500;
        var tempoMillissegundo = 30000;
        var tempoMillissegundoMinimoAceito = tempoMillissegundo - deltaMillissegundos;


        var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");

        RegraUsuarioService.ativar(usuario, adminitrador);
        RegraUsuarioService.advertir(usuario, adminitrador);

        try {
            RegraUsuarioService.advertir(usuario, adminitrador);
            Thread.sleep(tempoMillissegundoMinimoAceito);
            var estado = usuario.getNomeEstado();

            assertEquals(estado, BanidoTemporario.class.getSimpleName(), "O estado não foi alterado antes do tempo minimo");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
