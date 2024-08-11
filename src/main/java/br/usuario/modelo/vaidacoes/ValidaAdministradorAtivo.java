package br.usuario.modelo.vaidacoes;

import br.usuario.modelo.TipoUsuario;
import br.usuario.modelo.Usuario;

public class ValidaAdministradorAtivo implements IValidacoesStrategy {
    @Override
    public void validar(Usuario usuario, Usuario administrador) {
        if (administrador.getTipo() != TipoUsuario.ADMINISTRADOR) {
            throw new SecurityException("Ação permitida apenas para administradores");
        }
    }
}
