package br.usuario.modelo.vaidacoes;

import br.usuario.modelo.Usuario;

public interface IValidacoesStrategy {
    public void validar(Usuario usuario, Usuario administrador);
}
