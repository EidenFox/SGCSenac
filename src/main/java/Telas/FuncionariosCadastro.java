package Telas;

import DAO.UsuarioDao;
import Model.Usuario;

import javax.swing.*;
import java.util.Optional;

public class FuncionariosCadastro extends JFrame {
    private JPanel main;
    private JButton CadastrarBT;
    private JButton EditarBT;
    private JButton ConsultarBT;
    private JButton ExcluirBT;
    private JLabel identificacaoLabel;
    private JLabel cadUsuarioLabel;
    private JLabel emailLabel;
    private JLabel telefoneLabel;
    private JLabel cargoLabel;
    private JLabel estadoLabel;
    private JLabel cadSenhaLabel;
    private JLabel TitleLabel;
    private JTextField TFnumeroId;
    private JTextField TFnomeUsuario;
    private JTextField TFemail;
    private JTextField TFtelefone;
    private JTextField TFcargo;
    private JTextField TFsenha;
    private JTextField TFestado;
    private JPanel panel1;
    private JLabel usuarioCracha;
    private JTextField usuarioTF;
    private JLabel senhaLabel;
    private JPasswordField senhaTF;


    public FuncionariosCadastro() {
        setContentPane(main);

        CadastrarBT.addActionListener(e -> cadastrarFuncionario());
        EditarBT.addActionListener(e -> editarFuncionario());
        ConsultarBT.addActionListener(e -> consultarFuncionario());
        ExcluirBT.addActionListener(e -> excluirFuncionario());
    }

    private void cadastrarFuncionario() {

        try {

            Usuario usuario = new Usuario();

            usuario.setNumIdentificacao(Integer.parseInt(TFnumeroId.getText()));
            usuario.setNomeUsuario(TFnomeUsuario.getText());
            usuario.setEmail(TFemail.getText());
            usuario.setTelefone(TFtelefone.getText());
            usuario.setCargo(Integer.parseInt(TFcargo.getText()));
            usuario.setSenha(TFsenha.getText());
            usuario.setEstado(Integer.parseInt(TFestado.getText()));

            UsuarioDao dao = new UsuarioDao();

            boolean cadastro = dao.cadastrarUsuario(usuario);

            if (cadastro) {

                JOptionPane.showMessageDialog(this,
                        "Funcionário cadastrado com sucesso!");

                limparCampos();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Não foi possível cadastrar o funcionário.");

            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this,
                    "Número de identificação, cargo ou estado inválidos.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage());

        }
    }

    private void editarFuncionario() {

        try {

            int cracha = Integer.parseInt(TFnumeroId.getText());

            UsuarioDao dao = new UsuarioDao();

            Optional<Usuario> usuarioEncontrado = dao.buscarPorCracha(cracha);

            if (usuarioEncontrado.isPresent()) {

                Usuario usuario = usuarioEncontrado.get();

                usuario.setNomeUsuario(TFnomeUsuario.getText());
                usuario.setEmail(TFemail.getText());
                usuario.setTelefone(TFtelefone.getText());
                usuario.setCargo(Integer.parseInt(TFcargo.getText()));

                // Altere este valor para o ID do usuário logado no sistema
                Long usuarioLogado = usuario.getIdUsuario();

                boolean editou = dao.editarUsuario(usuario, usuarioLogado);

                if (editou) {

                    JOptionPane.showMessageDialog(this,
                            "Funcionário editado com sucesso!");

                    limparCampos();

                } else {

                    JOptionPane.showMessageDialog(this,
                            "Não foi possível editar o funcionário.");

                }

            } else {

                JOptionPane.showMessageDialog(this,
                        "Funcionário não encontrado.");

            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this,
                    "Número de identificação ou cargo inválido.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage());

        }
    }

    private void consultarFuncionario() {

        try {

            int cracha = Integer.parseInt(TFnumeroId.getText());

            UsuarioDao dao = new UsuarioDao();

            Optional<Usuario> usuarioEncontrado = dao.buscarPorCracha(cracha);

            if (usuarioEncontrado.isPresent()) {

                Usuario usuario = usuarioEncontrado.get();

                TFnomeUsuario.setText(usuario.getNomeUsuario());
                TFemail.setText(usuario.getEmail());
                TFtelefone.setText(usuario.getTelefone());
                TFcargo.setText(String.valueOf(usuario.getCargo()));
                TFsenha.setText(usuario.getSenha());
                TFestado.setText(String.valueOf(usuario.getEstado()));

                JOptionPane.showMessageDialog(this,
                        "Funcionário encontrado!");

            } else {

                JOptionPane.showMessageDialog(this,
                        "Funcionário não encontrado.");

            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this,
                    "Número de identificação inválido.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage());

        }
    }

    private void excluirFuncionario() {

        try {

            int cracha = Integer.parseInt(TFnumeroId.getText());

            UsuarioDao dao = new UsuarioDao();

            Optional<Usuario> usuarioEncontrado = dao.buscarPorCracha(cracha);

            if (usuarioEncontrado.isPresent()) {

                Usuario usuario = usuarioEncontrado.get();

                // ID do usuário que está realizando a ação
                Long usuarioLogado = usuario.getIdUsuario();

                // 0 = Desativado
                boolean desativou = dao.changeState(0, usuario.getIdUsuario(), usuarioLogado);

                if (desativou) {

                    JOptionPane.showMessageDialog(this,
                            "Funcionário desativado com sucesso!");

                    limparCampos();

                } else {

                    JOptionPane.showMessageDialog(this,
                            "Não foi possível desativar o funcionário.");

                }

            } else {

                JOptionPane.showMessageDialog(this,
                        "Funcionário não encontrado.");

            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this,
                    "Número de identificação inválido.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Erro ao desativar: " + ex.getMessage());

        }
    }


    private void limparCampos() {
        TFnumeroId.setText("");
        TFnomeUsuario.setText("");
        TFemail.setText("");
        TFtelefone.setText("");
        TFcargo.setText("");
        TFsenha.setText("");
        TFestado.setText("");
    }
}
