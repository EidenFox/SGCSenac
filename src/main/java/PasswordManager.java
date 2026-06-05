import DAO.UsuarioDao;
import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Scanner;

public class PasswordManager {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String storedHash) {
        return BCrypt.checkpw(plainPassword, storedHash);
    }

    public static void main(String[] args) {
        UsuarioDao usuarioDao = new UsuarioDao();
        Scanner scan = new Scanner(System.in);
        int op, id;

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

                    System.out.print("Digite o Nome do Usuário: ");
                    novoUsuario.setNome(scan.nextLine());
                    System.out.print("Digite a Senha: ");
                    String senhaCadastro = scan.nextLine();

                    novoUsuario.setSenha(hashPassword(senhaCadastro));

                    if (usuarioDao.inserir(novoUsuario)) {
                        System.out.println("Usuário cadastrado com sucesso!");
                    }else{
                        System.out.println("Erro ao cadastrar Usuário");
                    }
                    break;

                case 2:
                    System.out.println("\n--- Editar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja Editar: ");
                    id = scan.nextInt();
                    scan.nextLine();

                    Usuario usuarioAtt = new Usuario();
                    usuarioAtt.setId(id);

                    System.out.print("Digite o novo Nome: ");
                    usuarioAtt.setNome(scan.nextLine());

                    System.out.print("Digite a nova Senha: ");
                    String senhaNova = scan.nextLine();

                    usuarioAtt.setSenha(hashPassword(senhaNova));

                    if (usuarioDao.atualizar(usuarioAtt)) {
                        System.out.println("Usuário atualizado com sucesso!");
                    } else {
                        System.out.println("Erro ao atualizar o usuário.");
                    }
                    break;

                case 3:
                    System.out.println("\n--- Listar Usuários ---");
                    System.out.println("[1] Listar Usuários Ativos");
                    System.out.println("[2] Listar Usuários Inativos");
                    System.out.println("[3] Listar Todos os Usuários");
                    int op2 = scan.nextInt();

                    List<Usuario> lista = null;
                    if (op2 == 1) lista = usuarioDao.listar2(1);
                    if (op2 == 2) lista = usuarioDao.listar2(0);
                    if (op2 == 3) lista = usuarioDao.listarUsuarios();
                    if (lista == null){
                        System.out.println("Nenhum usuário Encontrado.");
                        break;
                    }
                    for (Usuario u : lista) {
                        System.out.println("ID: " + u.getId() + " - Nome: " + u.getNome() + " - Estado: " + u.getState());
                    }
                    break;

                case 4:
                    System.out.println("\n--- Inativar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja excluir: ");
                    id = scan.nextInt();

                    if (usuarioDao.changeState(0, id)) {
                        System.out.println("Usuário desativado com sucesso!");
                    }
                    break;

                case 5:
                    System.out.println("\n--- Reativar Usuário ---");
                    System.out.print("Digite o ID do usuário que deseja excluir: ");
                    id = scan.nextInt();

                    if (usuarioDao.changeState(1, id)) {
                        System.out.println("Usuário Ativo com sucesso!");
                    }
                    break;

                case 6:
                    System.out.println("\n--- Fazer Login ---");
                    scan.nextLine();
                    System.out.print("Digite o Nome do Usuário: ");
                    String nomeLogin = scan.nextLine();

                    Usuario usuarioEncontrado = usuarioDao.buscarPorNome(nomeLogin);

                    if (usuarioEncontrado != null) {
                        System.out.print("Digite a Senha: ");
                        String senhaLogin = scan.nextLine();

                        if (checkPassword(senhaLogin, usuarioEncontrado.getSenha())) {
                            System.out.println("Login bem-sucedido!");
                        } else {
                            System.out.println("Senha incorreta.");
                        }
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