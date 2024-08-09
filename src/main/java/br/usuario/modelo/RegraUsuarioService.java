package br.usuario.modelo;

public class RegraUsuarioService {
    public static void ativar(Usuario usuario, Usuario administrador) {
        ativarAdministrador(usuario, administrador);
        validarAdministrador(administrador);
        usuario.ativar();
    }

    public static void desativar(Usuario usuario, Usuario administrador) {
        ativarAdministrador(usuario, administrador);
        validarAdministrador(administrador);
        usuario.desativar();
    }

    public static void advertir(Usuario usuario, Usuario administrador) {
        ativarAdministrador(usuario, administrador);
        validarAdministrador(administrador);
        usuario.advertir();
    }

    private static void validarAdministrador(Usuario administrador) {
        if (administrador.getTipo() != TipoUsuario.ADMINISTRADOR) {
            throw new SecurityException("Ação permitida apenas para administradores");
        }
    }

    private static void ativarAdministrador(Usuario administrador1, Usuario administrador2){
        if(administrador1 == administrador2){
            throw new SecurityException("O administrador não pode se auto-ativar");
        }
    }
}
