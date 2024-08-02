package br.usuario.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TesteBasico {
    @Test
    public void testar(){
        var usuario = new Usuario("Abel", TipoUsuario.NORMAL, "123");
        var adm = new Usuario("Heflain", TipoUsuario.ADMINISTRADOR, "123");

        var exceptionCaptured = assertThrows(IllegalStateException.class, ()->{
            RegraUsuarioService.advertir(usuario, adm);
        });

        assertTrue(exceptionCaptured.getMessage().contains("Usuário novo não pode ser advertido"));
    }
}
