package br.usuario.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestaTransicaoDeEstado {
    @Test
    public  void testeDeTempoMaximoAceitoParaATransicaoDeBanidoTemporarioParaAtivo() throws Exception{
        //region Definindo o tempo máximo de atraso aceito
        var atrasoMaximoAceitoEmMillissegundos = 500;
        var tempoDeBanimentoEmMillissegundo = 30000;
        var tempoMillissegundoMaximoAceito =
                tempoDeBanimentoEmMillissegundo + atrasoMaximoAceitoEmMillissegundos;
        //endregion

        //region Criando o usuário e o adminitrador
        var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
        //endregion

        //region Verificando estado inicial do usuário
        var estado = usuario.getNomeEstado();
        assertEquals(estado, Novo.class.getSimpleName(),
                "O estado do usuário não foi iniciado como Novo");
        //endregion

        //region Verificando estado inicial do administrado
        estado = adminitrador.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do administrado não foi iniciado como Ativo");
        //endregion

        //region Teste de ativação do usuário
        RegraUsuarioService.ativar(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do usuário não foi Ativado");
        //endregion

        //region Teste de desativação antes da aplicação da primeira advertência
        RegraUsuarioService.desativar(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Desativado.class.getSimpleName(),
                "O estado do usuário não foi Desativado");
        //endregion

        //region Teste de ativação após desativação
        RegraUsuarioService.ativar(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do usuário não foi Ativado na segunda ativação");
        //endregion

        //region Advertindo o usuário duas vezes
        RegraUsuarioService.advertir(usuario, adminitrador);
        RegraUsuarioService.advertir(usuario, adminitrador);
        //endregion

        //region Teste de ativação até do tempo máximo de atrazo aceito
        Thread.sleep(tempoMillissegundoMaximoAceito);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do usuário não foi Ativado até o tempo máximo");
        //endregion

        //region Teste de transição para o estado BanidoDefinitivo
        RegraUsuarioService.advertir(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, BanidoDefinitivo.class.getSimpleName(),
                "O estado do usuário não foi alterado para BanidoDefinitivo");
        //endregion
    }

    @Test
    public void testeDeTempoMiminoAceitoParaATransicaoDeBanidoTemporarioParaAtivo() throws Exception{
        //region Definindo o tempo máximo de atrazo aceito
        var atrazoMinimoAceitoEmMillissegundos = 500;
        var tempoDeBanimentoEmMillissegundo = 30000;
        var tempoMillissegundoMinimoAceito =
                tempoDeBanimentoEmMillissegundo - atrazoMinimoAceitoEmMillissegundos;
        //endregion

        //region Criando o usuário e o administrador e ativando o usuário
        var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
        RegraUsuarioService.ativar(usuario, adminitrador);
        //endregion

        //region Testando a quantidade de advertências = 0
        var numAdvertencias = usuario.getNumeroDeAdvertencias();
        var numAdvertenciasEsperado = 0;
        assertEquals(numAdvertenciasEsperado, numAdvertencias,
                "O número de advertências não corresponde ao esperado que é igual a 0");
        //endregion

        
        //region Testando a quantidade de advertências = 1
        RegraUsuarioService.advertir(usuario, adminitrador);
        
        numAdvertencias = usuario.getNumeroDeAdvertencias();
        numAdvertenciasEsperado = 1;
        assertEquals(numAdvertenciasEsperado, numAdvertencias,
                "O número de advertências não corresponde ao esperado que é igual a 2");
        //endregion
                
        //region Teste de banido temporariamente até o tempo minímo aceito
        RegraUsuarioService.advertir(usuario, adminitrador);

        Thread.sleep(tempoMillissegundoMinimoAceito);

        var estado = usuario.getNomeEstado();
        assertEquals(estado, BanidoTemporario.class.getSimpleName(),
                "O estado do usuário foi alterado antes do tempo minimo");
        //endregion

        //region Testando a quantidade de advertências = 2
        numAdvertencias = usuario.getNumeroDeAdvertencias();
        numAdvertenciasEsperado = 2;
        assertEquals(numAdvertenciasEsperado, numAdvertencias,
                "O número de advertências não corresponde ao esperado que é igual a 2");
        //endregion

        //region Esperando um segundo para a transição de estado ocorrer
        Thread.sleep(1000);
        //endregion

        //region Teste de ativação do usuário
        estado = usuario.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do usuário não foi alterado para Ativado");
        //endregion

        //region Teste de desativação do usuário
        RegraUsuarioService.desativar(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Desativado.class.getSimpleName(),
                "O estado do usuário não foi alterado para Desativado");
        //endregion

        //region Teste de ativação do usuário
        RegraUsuarioService.ativar(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, Ativo.class.getSimpleName(),
                "O estado do usuário não foi alterado para Ativado");
        //endregion

        //region Teste de transição para o estado BanidoDefinitivo
        RegraUsuarioService.advertir(usuario, adminitrador);
        estado = usuario.getNomeEstado();
        assertEquals(estado, BanidoDefinitivo.class.getSimpleName(),
                "O estado do usuário não foi alterado para BanidoDefinitivo");
        //endregion

        //region Testando a quantidade de advertências = 3
        numAdvertencias = usuario.getNumeroDeAdvertencias();
        numAdvertenciasEsperado = 3;
        assertEquals(numAdvertenciasEsperado, numAdvertencias,
                "O número de advertências não corresponde ao esperado que é igual a 3");
        //endregion
    }

    @Test
    public void TestesDeAutorizacoesParaAtivarDesativarEAdvertir() throws Exception{
        //region Criando os administradores e usuário
        var usuario1 = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var usuario2 = new Usuario("usuario", TipoUsuario.NORMAL, "123");
        var adminitrador1 = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
        //endregion

        //region Teste de tentativa de ativação de um administrado por meio de um usuário
        var error = assertThrows(SecurityException.class, () -> {
            RegraUsuarioService.ativar(adminitrador1, usuario1);
        });

        assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
        //endregion

        //region Teste de tentativa de ativação de um usuário por meio de outro usuário
        error = assertThrows(SecurityException.class, () -> {
            RegraUsuarioService.ativar(usuario1, usuario2);
        });

        assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
        //endregion

        //region Teste de tentativa de advertir um usuário por meio de outro usuário
        error = assertThrows(SecurityException.class, () -> {
            RegraUsuarioService.advertir(usuario1, usuario2);
        });

        assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
        //endregion

        //region Teste da possibilidade do administrador pode se auto ativar
        var error2 = assertThrows(Exception.class, () -> {
            RegraUsuarioService.ativar(adminitrador1, adminitrador1);
        }, "O Adminstrador não pode se auto-ativar");
        //endregion
    }
}
