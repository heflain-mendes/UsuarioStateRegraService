package br.usuario.modelo.vaidacoes;

import br.usuario.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ValidacoesDiretor {
    private static List<IValidacoesStrategy> validacoes;

    static {
        validacoes = new ArrayList<>();

        validacoes.add(new ValidaAdministradorAtivo());
        validacoes.add(new ValidaAdministradorDiferenteDeUsuario());
    }

    public static  void validar(Usuario usuario, Usuario administrado){
        validacoes.forEach(item -> item.validar(usuario, administrado));
    }
}
