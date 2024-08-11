package br.usuario.modelo.vaidacoes;

import br.usuario.modelo.Usuario;

public class ValidaAdministradorDiferenteDeUsuario implements  IValidacoesStrategy{
    @Override
    public void validar(Usuario usuario, Usuario administrador) {
        if(administrador  == usuario){
            throw new SecurityException("O administrador não pode aplicar uma ação a si mesmo");
        }
    }
}
