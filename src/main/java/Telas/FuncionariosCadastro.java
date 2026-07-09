package Telas;

import DAO.UsuarioDao;
import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class FuncionariosCadastro extends JFrame {
    private JPanel main;
    private JButton BTCancelar;
    private JButton BTCadastrar;
    private JLabel identificacaoLabel;
    private JLabel cadUsuarioLabel;
    private JLabel emailLabel;
    private JLabel telefoneLabel;
    private JLabel cadSenhaLabel;
    private JLabel TitleLabel;
    private JTextField TFnumeroId;
    private JTextField TFnomeUsuario;
    private JTextField TFemail;
    private JTextField TFtelefone;
    private JPasswordField TFsenha;
    private JComboBox<String> CBCargo;
    private JComboBox<String> comboBox1;
    private JLabel cargoLabel;
    private JLabel estadoLabel;

    private Optional<Usuario> usuarioLogado;
    private Optional<Usuario> usuarioEditar;

    public FuncionariosCadastro() {
        this(Optional.empty(), Optional.empty());
    }

    public FuncionariosCadastro(Optional<Usuario> usuarioLogado, Optional<Usuario> usuarioEditar) {
        this.usuarioLogado = usuarioLogado;
        this.usuarioEditar = usuarioEditar;

        setContentPane(main);
        setTitle(usuarioEditar.isPresent() ? "Editar Usuário" : "Cadastro de Usuário");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarInterface();

        BTCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarFuncionario();
            }
        });

        BTCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void configurarInterface() {
        CBCargo.addItem("0 - Administrador");
        CBCargo.addItem("1 - Funcionário");

        comboBox1.addItem("0 - Desativado");
        comboBox1.addItem("1 - Ativo");
        comboBox1.addItem("2 - Pendente");

        if (usuarioEditar.isPresent()) {
            Usuario u = usuarioEditar.get();
            TitleLabel.setText("Editar Usuário");
            BTCadastrar.setText("Salvar Alterações");

            TFnumeroId.setText(String.valueOf(u.getNumIdentificacao()));
            TFnomeUsuario.setText(u.getNomeUsuario());
            TFemail.setText(u.getEmail());
            TFtelefone.setText(u.getTelefone() != null ? u.getTelefone() : "");

            CBCargo.setSelectedIndex(u.getCargo() == 0 ? 0 : 1);
            comboBox1.setSelectedIndex(u.getEstado());

            CBCargo.setVisible(true);
            comboBox1.setVisible(true);
            if (cargoLabel != null) cargoLabel.setVisible(true);
            if (estadoLabel != null) estadoLabel.setVisible(true);
        } else {
            CBCargo.setVisible(false);
            comboBox1.setVisible(false);
            if (cargoLabel != null) cargoLabel.setVisible(false);
            if (estadoLabel != null) estadoLabel.setVisible(false);
        }
    }

    private void salvarFuncionario() {
        try {
            String crachaText = TFnumeroId.getText().trim();
            String nome = TFnomeUsuario.getText().trim();
            String email = TFemail.getText().trim();
            String telefone = TFtelefone.getText().trim();
            String senhaStr = new String(TFsenha.getPassword());

            if (crachaText.isEmpty() || nome.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios (Crachá, Nome e E-mail).", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UsuarioDao dao = new UsuarioDao();

            if (usuarioEditar.isPresent()) {
                Usuario u = usuarioEditar.get();
                u.setNumIdentificacao(Integer.parseInt(crachaText));
                u.setNomeUsuario(nome);
                u.setEmail(email);
                u.setTelefone(telefone);
                u.setCargo(CBCargo.getSelectedIndex());
                int novoEstado = comboBox1.getSelectedIndex();
                Long idAdmin = usuarioLogado.get().getIdUsuario();

                boolean sucesso = dao.editarUsuario(u, idAdmin);

                if (sucesso) {
                    dao.changeState(novoEstado, u.getIdUsuario(), idAdmin);

                    if (!senhaStr.isEmpty()) {
                        String senhaHash = BCrypt.hashpw(senhaStr, BCrypt.gensalt());
                        dao.forcarNovaSenha(senhaHash, u.getIdUsuario(), idAdmin);
                    }

                    JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário na base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                if (senhaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "A senha é obrigatória para novos cadastros.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Usuario usuario = new Usuario();
                usuario.setNumIdentificacao(Integer.parseInt(crachaText));
                usuario.setNomeUsuario(nome);
                usuario.setEmail(email);
                usuario.setTelefone(telefone);
                usuario.setCargo(1);
                usuario.setEstado(2);

                String senhaHash = BCrypt.hashpw(senhaStr, BCrypt.gensalt());
                usuario.setSenha(senhaHash);

                boolean cadastro = dao.cadastrarUsuario(usuario);

                if (cadastro) {
                    JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso! Aguarde a aprovação do administrador.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível cadastrar. Verifique se o crachá ou e-mail já existem no sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "O Número do Crachá deve conter apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao guardar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}