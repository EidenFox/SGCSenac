import DAO.UsuarioDao;
import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainExemplo {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }


    static void main() {
        UsuarioDao usuarioDao = new UsuarioDao();
        Scanner scan = new Scanner(System.in);
        int op;
        long id;


        do {
            System.out.println("--------MENU--------");
            System.out.println("[1] Cadastrar Usuário");
            System.out.println("[2] Editar Usuário");
            System.out.println("[3] Listar Usuários");
            System.out.println("[4] Inativar Usuário");
            System.out.println("[5] Reativar Usuário");
            System.out.println("[6] Fazer Login");
            System.out.println("[0] Sair");
            op = scan.nextInt();

            switch (op) {
                case 1:
                    Usuario novoUsuario = new Usuario();
                    System.out.println("\n--- Cadastrar Novo Usuário ---");
                    scan.nextLine();

                    System.out.println("Digite seu Numero de Identificação (Crachá): ");
                    novoUsuario.setNumIdentificacao(scan.nextInt());
                    scan.nextLine();

                    System.out.print("Digite seu Nome (max 45 caracteres): ");
                    novoUsuario.setNomeUsuario(scan.nextLine());
                    System.out.println("Digite seu Email de login: ");
                    novoUsuario.setEmail(scan.nextLine());

                    //Cadastro da senha
                    System.out.print("Digite sua Senha: ");
                    String senhaCadastro = scan.nextLine();
                    novoUsuario.setSenha(hashPassword(senhaCadastro));

                    novoUsuario.setCargo(1); //Novo usuario sempre cargo 1, pode ser alterado para 0 (administrador) depois

                    if (usuarioDao.cadastrarUsuario(novoUsuario)) {
                        System.out.println("Usuário cadastrado com sucesso!");
                    }else{
                        System.out.println("Erro ao cadastrar Usuário");
                    }
                    break;

                case 2:
                    System.out.println("\n--- Editar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja Editar: ");
                    id = scan.nextLong();
                    scan.nextLine();

                    Usuario usuarioAtt = new Usuario();
                    usuarioAtt.setIdUsuario(id);

                    System.out.println("Digite o Numero de Identificação (Crachá): ");
                    usuarioAtt.setNumIdentificacao(scan.nextInt());
                    scan.nextLine();

                    System.out.print("Digite o Nome (max 45 caracteres): ");
                    usuarioAtt.setNomeUsuario(scan.nextLine());
                    System.out.println("Digite o Email de login: ");
                    usuarioAtt.setEmail(scan.nextLine());
                    System.out.println("Selecione o cargo: [0]Administrador | [1]Usuario");
                    usuarioAtt.setCargo(scan.nextInt());


                    if (usuarioDao.editarUsuario(usuarioAtt, -1L)) { // no update PRECISA do ID da pessoa que está logada fazendo a alteração por conta do log
                        System.out.println("Usuário atualizado com sucesso!");
                    } else {
                        System.out.println("Erro ao editarUsuario o usuário.");
                    }
                    break;

                case 3:
                    System.out.println("\n--- Listar Usuários ---");
                    System.out.println("[1] Listar Usuários Ativos");
                    System.out.println("[2] Listar Usuários Inativos");
                    System.out.println("[3] Listar Todos os Usuários");
                    int op2 = scan.nextInt();

                    List<Usuario> lista = null;
                    if (op2 == 1) lista = usuarioDao.listarEspecial(1);
                    if (op2 == 2) lista = usuarioDao.listarEspecial(0);
                    if (op2 == 3) lista = usuarioDao.listarUsuarios();
                    if (lista == null){
                        System.out.println("Nenhum usuário Encontrado.");
                        break;
                    }
                    for (Usuario u : lista) {
                        System.out.println("ID: " + u.getIdUsuario() + " - Crachá: " + u.getNumIdentificacao() + " - Email: " + u.getEmail() + " - Nome: " + u.getNomeUsuario() + " - Estado: " + u.getEstado());
                    }
                    break;

                case 4:
                    System.out.println("\n--- Inativar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja Inativar: ");
                    id = scan.nextLong();

                    if (usuarioDao.changeState(0, id, -1L)) {    // no update PRECISA do ID da pessoa que está logada fazendo a alteração por conta do log
                        System.out.println("Usuário Inativado com sucesso!");
                    }
                    break;

                case 5:
                    System.out.println("\n--- Reativar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja Ativar: ");
                    id = scan.nextLong();

                    if (usuarioDao.changeState(1, id, -1L)) {    // no update PRECISA do ID da pessoa que está logada fazendo a alteração por conta do log
                        System.out.println("Usuário Ativo com sucesso!");
                    }
                    break;

                case 6:
                    System.out.println("\n--- Fazer Login ---");
                    scan.nextLine();
                    System.out.print("Digite o Email do Usuário: ");
                    String emailLogin = scan.nextLine();

                    Optional<Usuario> usuarioOpt = usuarioDao.buscarPorEmail(emailLogin);

                    if (usuarioOpt.isPresent()) {
                        Usuario usuarioEncontrado = usuarioOpt.get();
                        System.out.print("Digite a Senha: ");
                        String senhaLogin = scan.nextLine();

                        if (usuarioDao.checarSenha(senhaLogin, usuarioEncontrado.getSenha())) System.out.println("Login bem-sucedido!");
                        else System.out.println("Senha incorreta.");

                    } else {
                        System.out.println("Usuário não encontrado.");
                    }
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção Inválida! Tente Novamente");
                    break;
            }

        } while (op != 0);

        scan.close();
    }
}