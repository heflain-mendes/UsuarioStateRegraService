package br.usuario.modelo;

import br.usuario.modelo.vaidacoes.ValidacoesDiretor;

public class RegraUsuarioService {
    public static void ativar(Usuario usuario, Usuario administrador) {
        ValidacoesDiretor.validar(usuario, administrador);
        usuario.ativar();
    }

    public static void desativar(Usuario usuario, Usuario administrador) {
        ValidacoesDiretor.validar(usuario, administrador);
        usuario.desativar();
    }

    public static void advertir(Usuario usuario, Usuario administrador) {
        ValidacoesDiretor.validar(usuario, administrador);
        usuario.advertir();
    }
}
