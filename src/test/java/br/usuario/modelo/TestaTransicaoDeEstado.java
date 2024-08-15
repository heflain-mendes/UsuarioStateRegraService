package br.usuario.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestaTransicaoDeEstado {

        private final Usuario ADMINISTRADOR;
        private final Usuario NORMAL;

        public TestaTransicaoDeEstado() {
                ADMINISTRADOR = new Usuario("ADMINISTRADOR", TipoUsuario.ADMINISTRADOR, "ADMINISTRADOR");
                NORMAL = new Usuario("NORMAL", TipoUsuario.NORMAL, "NORMAL");
        }

        @Test
        public void testeDeTempoMaximoAceitoParaATransicaoDeBanidoTemporarioParaAtivo() throws Exception {
                // region Definindo o tempo máximo de atraso aceito
                var atrasoMaximoAceitoEmMillissegundos = 500;
                var tempoDeBanimentoEmMillissegundo = 30000;
                var tempoMillissegundoMaximoAceito = tempoDeBanimentoEmMillissegundo
                                + atrasoMaximoAceitoEmMillissegundos;
                // endregion

                // region Criando o usuário e o adminitrador
                var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
                var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
                // endregion

                // region Verificando estado inicial do usuário
                var estado = usuario.getNomeEstado();
                assertEquals(estado, Novo.class.getSimpleName(),
                                "O estado do usuário não foi iniciado como Novo");
                // endregion

                // region Verificando estado inicial do administrado
                estado = adminitrador.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do administrado não foi iniciado como Ativo");
                // endregion

                // region Teste de ativação do usuário
                RegraUsuarioService.ativar(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do usuário não foi Ativado");
                // endregion

                // region Teste de desativação antes da aplicação da primeira advertência
                RegraUsuarioService.desativar(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Desativado.class.getSimpleName(),
                                "O estado do usuário não foi Desativado");
                // endregion

                // region Teste de ativação após desativação
                RegraUsuarioService.ativar(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do usuário não foi Ativado na segunda ativação");
                // endregion

                // region Advertindo o usuário duas vezes
                RegraUsuarioService.advertir(usuario, adminitrador);
                RegraUsuarioService.advertir(usuario, adminitrador);
                // endregion

                // region Teste de ativação até do tempo máximo de atrazo aceito
                Thread.sleep(tempoMillissegundoMaximoAceito);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do usuário não foi Ativado até o tempo máximo");
                // endregion

                // region Teste de transição para o estado BanidoDefinitivo
                RegraUsuarioService.advertir(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, BanidoDefinitivo.class.getSimpleName(),
                                "O estado do usuário não foi alterado para BanidoDefinitivo");
                // endregion
        }

        @Test
        public void testeDeTempoMiminoAceitoParaATransicaoDeBanidoTemporarioParaAtivo() throws Exception {
                // region Definindo o tempo máximo de atrazo aceito
                var atrazoMinimoAceitoEmMillissegundos = 500;
                var tempoDeBanimentoEmMillissegundo = 30000;
                var tempoMillissegundoMinimoAceito = tempoDeBanimentoEmMillissegundo
                                - atrazoMinimoAceitoEmMillissegundos;
                // endregion

                // region Criando o usuário e o administrador e ativando o usuário
                var usuario = new Usuario("usuario", TipoUsuario.NORMAL, "123");
                var adminitrador = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
                RegraUsuarioService.ativar(usuario, adminitrador);
                // endregion

                // region Testando a quantidade de advertências = 0
                var numAdvertencias = usuario.getNumeroDeAdvertencias();
                var numAdvertenciasEsperado = 0;
                assertEquals(numAdvertenciasEsperado, numAdvertencias,
                                "O número de advertências não corresponde ao esperado que é igual a 0");
                // endregion

                // region Teste de banido temporariamente até o tempo minímo aceito
                RegraUsuarioService.advertir(usuario, adminitrador);
                RegraUsuarioService.advertir(usuario, adminitrador);

                Thread.sleep(tempoMillissegundoMinimoAceito);

                var estado = usuario.getNomeEstado();
                assertEquals(estado, BanidoTemporario.class.getSimpleName(),
                                "O estado do usuário foi alterado antes do tempo minimo");
                // endregion

                // region Testando a quantidade de advertências = 2
                numAdvertencias = usuario.getNumeroDeAdvertencias();
                numAdvertenciasEsperado = 2;
                assertEquals(numAdvertenciasEsperado, numAdvertencias,
                                "O número de advertências não corresponde ao esperado que é igual a 2");
                // endregion

                // region Esperando um segundo para a transição de estado ocorrer
                Thread.sleep(1000);
                // endregion

                // region Teste de ativação do usuário
                estado = usuario.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do usuário não foi alterado para Ativado");
                // endregion

                // region Teste de desativação do usuário
                RegraUsuarioService.desativar(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Desativado.class.getSimpleName(),
                                "O estado do usuário não foi alterado para Desativado");
                // endregion

                // region Teste de ativação do usuário
                RegraUsuarioService.ativar(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, Ativo.class.getSimpleName(),
                                "O estado do usuário não foi alterado para Ativado");
                // endregion

                // region Teste de transição para o estado BanidoDefinitivo
                RegraUsuarioService.advertir(usuario, adminitrador);
                estado = usuario.getNomeEstado();
                assertEquals(estado, BanidoDefinitivo.class.getSimpleName(),
                                "O estado do usuário não foi alterado para BanidoDefinitivo");
                // endregion

                // region Testando a quantidade de advertências = 3
                numAdvertencias = usuario.getNumeroDeAdvertencias();
                numAdvertenciasEsperado = 3;
                assertEquals(numAdvertenciasEsperado, numAdvertencias,
                                "O número de advertências não corresponde ao esperado que é igual a 3");
                // endregion
        }

        @Test
        public void testesDeAutorizacoesParaAtivarDesativarEAdvertir() throws Exception {
                // region Criando os administradores e usuário
                var usuario1 = new Usuario("usuario", TipoUsuario.NORMAL, "123");
                var usuario2 = new Usuario("usuario", TipoUsuario.NORMAL, "123");
                var adminitrador1 = new Usuario("adminitrador", TipoUsuario.ADMINISTRADOR, "123");
                // endregion

                // region Teste de tentativa de ativação de um administrado por meio de um
                // usuário
                var error = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.ativar(adminitrador1, usuario1);
                });

                assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
                // endregion

                // region Teste de tentativa de ativação de um usuário por meio de outro usuário
                error = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.ativar(usuario1, usuario2);
                });

                assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
                // endregion

                // region Teste de tentativa de advertir um usuário por meio de outro usuário
                error = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.advertir(usuario1, usuario2);
                });

                assertEquals(error.getMessage(), "Ação permitida apenas para administradores");
                // endregion

                // region Teste da possibilidade do administrador pode se auto ativar
                var error2 = assertThrows(Exception.class, () -> {
                        RegraUsuarioService.ativar(adminitrador1, adminitrador1);
                }, "O Adminstrador não pode se auto-ativar");
                // endregion
        }

        // Teste adicionado por Rafael e Gabriel
        @Test
        public void explorarUsuarioNovo() {
                // Criação de usuários
                String senha = "123";
                Usuario usuario = new Usuario("User", TipoUsuario.NORMAL, senha);
                Usuario usuarioAdmin = new Usuario("Admin", TipoUsuario.ADMINISTRADOR, senha);

                // Tentar desativar usuário novo
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.desativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar desativar um usuário novo");

                assertEquals(exception.getMessage(), "Usuário novo não pode ser desativado",
                                "A mensagem de exceção para desativação deve ser correta");

                // Tentar advertir usuário novo
                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.advertir(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar advertir um usuário novo");

                assertEquals(exception.getMessage(), "Usuário novo não pode ser advertido",
                                "A mensagem de exceção para advertência deve ser correta");

                // Tentar ativar usuário novo
                RegraUsuarioService.ativar(usuario, usuarioAdmin);
                assertEquals(Ativo.class.getSimpleName(), usuario.getNomeEstado(),
                                "Deve ser possível ativar um usuário novo");

        }

        // Teste adicionado por Rafael e Gabriel
        @Test
        public void explorarUsuarioDesativado() {
                // Criação de usuários
                String senha = "123";
                Usuario usuario = new Usuario("User", TipoUsuario.NORMAL, senha);
                Usuario usuarioAdmin = new Usuario("Admin", TipoUsuario.ADMINISTRADOR, senha);

                RegraUsuarioService.ativar(usuario, usuarioAdmin);
                RegraUsuarioService.desativar(usuario, usuarioAdmin);

                // Explorar advertir usuário desativado
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.advertir(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar advertir um usuário que está desativado");
                assertEquals(exception.getMessage(),
                                "Usuário desativado não pode ser advertido", "A mensagem de exceção deve ser correta");

                // Explorar desativar usuário desativado
                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.desativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar desativar um usuário que já está desativado");

                assertEquals(exception.getMessage(), "O usuário já está desativado",
                                "A mensagem de exceção deve ser correta");

                // Explorar ativar usuário desativado
                RegraUsuarioService.ativar(usuario, usuarioAdmin);
                assertEquals(Ativo.class.getSimpleName(), usuario.getNomeEstado(),
                                "Deve ser possível ativar um usuário desativado");
        }

        // Teste adicionado por Gabriel e Rafael
        @Test
        public void explorarBanimentoTemporario() throws InterruptedException {
                // Criação de usuários
                String senha = "123";
                Usuario usuario = new Usuario("User", TipoUsuario.NORMAL, senha);
                Usuario usuarioAdmin = new Usuario("Admin", TipoUsuario.ADMINISTRADOR, senha);

                RegraUsuarioService.ativar(usuario, usuarioAdmin);

                // Explorar primeira advertencia
                RegraUsuarioService.advertir(usuario, usuarioAdmin);
                assertEquals(1, usuario.getNumeroDeAdvertencias(),
                                "O número de advertências deve ser 1 após a primeira advertência");
                assertEquals(Ativo.class.getSimpleName(), usuario.getNomeEstado(),
                                "O usuário deve permanecer no estado 'Ativo' após a primeira advertência");

                // Explorar segunda advertência
                RegraUsuarioService.advertir(usuario, usuarioAdmin);
                assertEquals(2, usuario.getNumeroDeAdvertencias(),
                                "O número de advertências deve ser 2 após a segunda advertência");
                assertEquals(BanidoTemporario.class.getSimpleName(), usuario.getNomeEstado(),
                                "O usuário deve estar banido temporariamente após duas advertências");

                // Explorar advertir usuário banido temporariamente
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.advertir(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar advertir um usuário que está banido temporariamente");
                assertEquals(exception.getMessage(),
                                "Usuário banido temporariamente não pode ser advertido",
                                "A mensagem de exceção deve ser correta");

                // Explorar desativar usuário banido temporariamente
                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.desativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar desativar um usuário que está banido temporariamente");
                assertEquals(exception.getMessage(),
                                "Usuário banido temporariamente não pode ser desativado",
                                "A mensagem de exceção deve ser correta");

                // Explorar ativar usuário banido temporariamente
                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.ativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar ativar um usuário que está banido temporariamente");
                assertEquals(exception.getMessage(),
                                "Usuário ainda está banido temporariamente", "A mensagem de exceção deve ser correta");

        }

        // Teste adicionado por Rafael e Gabriel
        @Test
        public void explorarBanimentoPermanente() {
                // Criação de usuários
                String senha = "123";
                Usuario usuario = new Usuario("User", TipoUsuario.NORMAL, senha);
                Usuario usuarioAdmin = new Usuario("Admin", TipoUsuario.ADMINISTRADOR, senha);

                // Explorar banimento permanente na terceira advertência
                RegraUsuarioService.ativar(usuario, usuarioAdmin);
                usuario.setNumeroDeAdvertencias(2);

                RegraUsuarioService.advertir(usuario, usuarioAdmin);
                assertEquals(BanidoDefinitivo.class.getSimpleName(), usuario.getNomeEstado(),
                                "Usuário deve ser banido permanentemente quando for advertido pela terceira vez");

                // Explorar troca de estados de um usuário banido permanentemente
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.desativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar desativar um usuário banido");
                assertEquals(exception.getMessage(),
                                "Usuário banido definitivamente não pode ser desativado",
                                "A mensagem de exceção deve ser correta");

                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.ativar(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar ativar um usuário banido");
                assertEquals(exception.getMessage(),
                                "Usuário banido definitivamente não pode ser ativado",
                                "A mensagem de exceção deve ser correta");

                exception = assertThrows(IllegalStateException.class, () -> {
                        RegraUsuarioService.advertir(usuario, usuarioAdmin);
                }, "A mensagem de exceção deve ser lançada ao tentar advertir um usuário banido");
                assertEquals(exception.getMessage(),
                                "Usuário banido definitivamente não pode ser advertido",
                                "A mensagem de exceção deve ser correta");
        }

        // Teste adicionado por Gabriel e Rafael
        @Test
        public void explorarPermissaoAdmin() throws InterruptedException {
                // Criação de usuários
                String senha = "123";
                Usuario usuario = new Usuario("User", TipoUsuario.NORMAL, senha);
                Usuario usuarioAdmin = new Usuario("Admin", TipoUsuario.ADMINISTRADOR, senha);

                // Explorar permissões do administrador para alterar status de um usuário normal
                RegraUsuarioService.ativar(usuario, usuarioAdmin);
                assertEquals(Ativo.class.getSimpleName(), usuario.getNomeEstado(),
                                "Um administrador deve poder ativar um usuário novo");

                RegraUsuarioService.advertir(usuario, usuarioAdmin);
                assertEquals(1, usuario.getNumeroDeAdvertencias(),
                                "Um administrador deve poder advertir um usuário ativo");

                RegraUsuarioService.desativar(usuario, usuarioAdmin);
                assertEquals(Desativado.class.getSimpleName(), usuario.getNomeEstado(),
                                "Um administrador deve poder desativar um usuário ativo");

                // Explorar permissões do administrador para alterar status de um outro
                // administrador
                Usuario administrador2 = new Usuario(usuarioAdmin.getNome(), TipoUsuario.ADMINISTRADOR, "123");

                RegraUsuarioService.advertir(administrador2, usuarioAdmin);
                assertEquals(1, administrador2.getNumeroDeAdvertencias(),
                                "Um administrador deve poder advertir um administrador ativo");

                RegraUsuarioService.desativar(administrador2, usuarioAdmin);
                assertEquals(Desativado.class.getSimpleName(), administrador2.getNomeEstado(),
                                "Um administrador deve poder desativar um administrador ativo");

                // Explorar se usuário normal não consegue alterar permissões
                Usuario usuarioNormal = new Usuario(usuario.getNome(), TipoUsuario.NORMAL, "123");
                SecurityException exception = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.ativar(usuario, usuarioNormal);
                }, "A mensagem de exceção deve ser lançada quando um usuário não tem permissões para ativar outro");
                assertEquals(exception.getMessage(),
                                "Ação permitida apenas para administradores", "A mensagem de exceção deve ser correta");

                usuario.setEstado(new Ativo());
                exception = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.advertir(usuario, usuarioNormal);
                }, "A mensagem de exceção deve ser lançada quando um usuário não tem permissões para advertir outro");
                assertEquals(exception.getMessage(),
                                "Ação permitida apenas para administradores", "A mensagem de exceção deve ser correta");

                exception = assertThrows(SecurityException.class, () -> {
                        RegraUsuarioService.desativar(usuario, usuarioNormal);
                }, "A mensagem de exceção deve ser lançada quando um usuário não tem permissões para desativar outro");
                assertEquals(exception.getMessage(),
                                "Ação permitida apenas para administradores", "A mensagem de exceção deve ser correta");

                // Explorar administrador banido definitivamente
                RegraUsuarioService.advertir(usuarioAdmin, administrador2);
                RegraUsuarioService.advertir(usuarioAdmin, administrador2);

                Thread.sleep(31000);

                RegraUsuarioService.advertir(usuarioAdmin, administrador2);
                RegraUsuarioService.desativar(usuario, administrador2);

                RegraUsuarioService.ativar(usuario, usuarioAdmin);

                assertEquals(Ativo.class.getSimpleName(), usuario.getNomeEstado(),
                                "Um administrador banido permanentemente não deve poder alterar status de um usuário");
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/casos_de_teste_estado.csv", numLinesToSkip = 1, delimiter = ';')
        void testesUnitarios(
                        String codigoTeste,
                        String usuarioDestinoStatus,
                        String acaoRealizda,
                        String usuarioRealizador,
                        String resultadoEsperado) {
                var usuario = getUsuario(usuarioDestinoStatus);
                var usuarioAcao = "A".equalsIgnoreCase(usuarioRealizador) ? ADMINISTRADOR : NORMAL;
                if ("ativar".equalsIgnoreCase(acaoRealizda) || "reativar".equalsIgnoreCase(acaoRealizda)) {
                        if ("ERRO".equalsIgnoreCase(resultadoEsperado)) {
                                assertThrows(Exception.class, () -> {
                                        RegraUsuarioService.ativar(usuario, usuarioAcao);
                                }, codigoTeste);
                        } else {
                                RegraUsuarioService.ativar(usuario, usuarioAcao);
                                assertEquals(resultadoEsperado.toLowerCase(), usuario.getNomeEstado().toLowerCase());
                        }
                } else if ("desativar".equalsIgnoreCase(acaoRealizda)) {
                        if ("ERRO".equalsIgnoreCase(resultadoEsperado)) {
                                assertThrows(Exception.class, () -> {
                                        RegraUsuarioService.desativar(usuario, usuarioAcao);
                                }, codigoTeste);
                        } else {
                                RegraUsuarioService.desativar(usuario, usuarioAcao);
                                assertEquals(resultadoEsperado.toLowerCase(), usuario.getNomeEstado().toLowerCase());
                        }
                } else if ("advertir".equalsIgnoreCase(acaoRealizda)) {
                        if ("ERRO".equalsIgnoreCase(resultadoEsperado)) {
                                assertThrows(Exception.class, () -> {
                                        RegraUsuarioService.advertir(usuario, usuarioAcao);
                                }, codigoTeste);
                        } else {
                                RegraUsuarioService.advertir(usuario, usuarioAcao);
                                assertEquals(resultadoEsperado.toLowerCase(), usuario.getNomeEstado().toLowerCase());
                        }
                }

        }

        private Usuario getUsuario(String status) {
                if ("NOVO".equalsIgnoreCase(status)) {
                        return gerarUsuarioNovo();
                } else if ("ATIVO".equalsIgnoreCase(status)) {
                        return gerarUsuarioAtivo();
                } else if ("DESATIVADO".equalsIgnoreCase(status)) {
                        return gerarUsuarioDesativado();
                } else if ("BANIDODEFINITIVO".equalsIgnoreCase(status)) {
                        return gerarUsuarioBanidoDefinitivamente();
                } else if ("BANIDOTEMPORARIO".equalsIgnoreCase(status)) {
                        return gerarUsuarioBanidoTemporariamente();
                } else {
                        throw new IllegalStateException("STATUS não definido");
                }
        }

        private Usuario gerarUsuarioNovo() {
                return new Usuario("GERADO", TipoUsuario.NORMAL, "GERADO");
        }

        private Usuario gerarUsuarioAtivo() {
                var gerado = gerarUsuarioNovo();
                RegraUsuarioService.ativar(gerado, ADMINISTRADOR);
                return gerado;
        }

        private Usuario gerarUsuarioDesativado() {
                var gerado = gerarUsuarioAtivo();
                RegraUsuarioService.desativar(gerado, ADMINISTRADOR);
                return gerado;
        }

        private Usuario gerarUsuarioBanidoTemporariamente() {
                var gerado = gerarUsuarioAtivo();
                RegraUsuarioService.advertir(gerado, ADMINISTRADOR);
                RegraUsuarioService.advertir(gerado, ADMINISTRADOR);
                return gerado;
        }

        private Usuario gerarUsuarioBanidoDefinitivamente() {
                var gerado = gerarUsuarioAtivo();

                RegraUsuarioService.advertir(gerado, ADMINISTRADOR);
                RegraUsuarioService.advertir(gerado, ADMINISTRADOR);
                System.out.println(LocalDateTime.now());
                try {
                        /* TimeUnit.SECONDS.sleep( 31 ); */
                        TimeUnit.MILLISECONDS.sleep(30001);
                } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                }
                System.out.println(LocalDateTime.now());
                RegraUsuarioService.advertir(gerado, ADMINISTRADOR);
                return gerado;
        }

    //Teste adicionado por Álvaro e Mário
    @Test
    public void testeDiagramaEstadoCompleto() throws InterruptedException{
        //criação de usuários
        Usuario usuarioAdmin = new Usuario("usuário Normal", TipoUsuario.ADMINISTRADOR, "alvaro");
        Usuario usuarioNormal = new Usuario("usuário Admin", TipoUsuario.NORMAL, "mario");
        
        //Ativar usuario normal
        RegraUsuarioService.ativar(usuarioNormal, usuarioAdmin);
        assertEquals(Ativo.class.getSimpleName(), usuarioNormal.getNomeEstado(), "Estado não condiz com o esperado:"+Ativo.class.getSimpleName());
        
        //verificar quantidade advertências de um usuário ativo 
        assertTrue( usuarioNormal.getNumeroDeAdvertencias() == 0 , "O número de advertências para um usuário recém ativo deveria ser zero");
        
        //Desativar usuario normal
        RegraUsuarioService.desativar(usuarioNormal, usuarioAdmin);
        
        assertEquals(Desativado.class.getSimpleName(), usuarioNormal.getNomeEstado(), "Estado não condiz com o esperado"+Desativado.class.getSimpleName());
        
        //validar "travas" quando o usuário está dessativado
        RuntimeException excecaoNegativaAdvertirDesativado = assertThrows(RuntimeException.class, () -> {
            RegraUsuarioService.advertir(usuarioNormal, usuarioAdmin);
        });
        
        assertEquals("Usuário desativado não pode ser advertido", excecaoNegativaAdvertirDesativado.getMessage(), "A mensagem de falha não corresponde com o esperado: Usuário desativado não pode ser advertido");
        
        RuntimeException excecaoNegativaDesativarDesativado = assertThrows(RuntimeException.class, () -> {
            RegraUsuarioService.desativar(usuarioNormal, usuarioAdmin);
        });

        assertEquals("O usuário já está desativado", excecaoNegativaDesativarDesativado.getMessage(), "A mensagem de falha não corresponde com o esperado: O usuário já está desativado");
        
        //Reativar ususário desativado
        
        RegraUsuarioService.ativar(usuarioNormal, usuarioAdmin);
        assertEquals(Ativo.class.getSimpleName(), usuarioNormal.getNomeEstado(), "O usuário não retornou ao estado de ativo após a desativação");
        
        //advertir usuário ativo e banir temporariamente
        RegraUsuarioService.advertir(usuarioNormal, usuarioAdmin);
        assertTrue( usuarioNormal.getNumeroDeAdvertencias() == 1 , "O número de advertências está incorreto");
        
        RegraUsuarioService.advertir(usuarioNormal, usuarioAdmin);
        
        assertEquals(BanidoTemporario.class.getSimpleName(), usuarioNormal.getNomeEstado(), "O usuário não alterou estado de ativo para BanidoTemporario");
        
        assertTrue( usuarioNormal.getNumeroDeAdvertencias() == 2 , "O número de advertências não foi incrementado");
        
        Thread.sleep(30500);   
        
        assertEquals(usuarioNormal.getNomeEstado(), Ativo.class.getSimpleName(), "O estado do usuário não retornou para ativo após o tempo banimento temporário");
        
        // verificar números de advertências quando muda para o estado desativado
        RegraUsuarioService.desativar(usuarioNormal, usuarioAdmin);
        assertTrue( usuarioNormal.getNumeroDeAdvertencias() == 2 , "O número de advertências não pode ser alterado quando muda para o estado desativado");
        
        RegraUsuarioService.ativar(usuarioNormal, usuarioAdmin);
        
        
        //Realizar Banimento Definitivo e validar as travas do sistema
        RegraUsuarioService.advertir(usuarioNormal, usuarioAdmin);
         
        assertTrue( usuarioNormal.getNumeroDeAdvertencias() == 3 , "O número de advertências não foi incrementado Para o Banimento definitivo");
         
        assertEquals(usuarioNormal.getNomeEstado(), BanidoDefinitivo.class.getSimpleName(), "O estado do não foi alterado para: "+BanidoDefinitivo.class.getSimpleName());
        
        RuntimeException excecaoDesativarBanidoFefinitivo = assertThrows(RuntimeException.class, () -> {
            RegraUsuarioService.desativar(usuarioNormal, usuarioAdmin);
        });
        
        assertEquals("Usuário banido definitivamente não pode ser desativado", excecaoDesativarBanidoFefinitivo.getMessage(), "A mensagem de falha não corresponde com o esperado: Usuário banido definitivamente não pode ser desativado");

        RuntimeException excecaoAtivarBanidoDefinitivo = assertThrows(RuntimeException.class, () -> {
            RegraUsuarioService.ativar(usuarioNormal, usuarioAdmin);
        });
        
        assertEquals("Usuário banido definitivamente não pode ser ativado", excecaoAtivarBanidoDefinitivo.getMessage(), "A mensagem de falha não corresponde com o esperado: Usuário banido definitivamente não pode ser ativado");

        RuntimeException excecaoAdvertirBanidoDefinitivo = assertThrows(RuntimeException.class, () -> {
            RegraUsuarioService.advertir(usuarioNormal, usuarioAdmin);
        });
        
        assertEquals("Usuário banido definitivamente não pode ser advertido", excecaoAdvertirBanidoDefinitivo.getMessage(), "A mensagem de falha não corresponde com o esperado: Usuário banido definitivamente não pode ser advertido");
        
    }

    //Teste adicionado por Arthur e Daniel
    @Test
    public void TesteCriarUsuarioInvalido()
    {   
        IllegalArgumentException excecaoNome = assertThrows(IllegalArgumentException.class, () -> {
                Usuario u = new Usuario(null, TipoUsuario.NORMAL, "1234");
            });
        assertEquals("O nome do usuário não pode ser vazio ou nulo", excecaoNome.getMessage());
        
        IllegalArgumentException excecaoSenha = assertThrows(IllegalArgumentException.class, () -> {
                Usuario u = new Usuario("Tigas", TipoUsuario.NORMAL, null);
            });
        assertEquals("A senha do usuário não pode ser vazia ou nula", excecaoSenha.getMessage());
    }
}
