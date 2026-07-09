package Telas;

import DAO.UsuarioDao;
import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main = new JPanel();
        main.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        TitleLabel = new JLabel();
        Font TitleLabelFont = this.$$$getFont$$$("Arial Black", Font.PLAIN, 22, TitleLabel.getFont());
        if (TitleLabelFont != null) TitleLabel.setFont(TitleLabelFont);
        TitleLabel.setText("Cadastro de usuário");
        panel2.add(TitleLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BTCancelar = new JButton();
        BTCancelar.setText("Cancelar");
        panel4.add(BTCancelar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BTCadastrar = new JButton();
        BTCadastrar.setText("Cadastrar");
        panel4.add(BTCadastrar, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        identificacaoLabel = new JLabel();
        Font identificacaoLabelFont = this.$$$getFont$$$("Arial Rounded MT Bold", Font.PLAIN, 14, identificacaoLabel.getFont());
        if (identificacaoLabelFont != null) identificacaoLabel.setFont(identificacaoLabelFont);
        identificacaoLabel.setText("Número do Crachá");
        panel5.add(identificacaoLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TFnumeroId = new JTextField();
        panel5.add(TFnumeroId, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        TFnomeUsuario = new JTextField();
        panel5.add(TFnomeUsuario, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cadUsuarioLabel = new JLabel();
        Font cadUsuarioLabelFont = this.$$$getFont$$$("Arial Rounded MT Bold", Font.PLAIN, 14, cadUsuarioLabel.getFont());
        if (cadUsuarioLabelFont != null) cadUsuarioLabel.setFont(cadUsuarioLabelFont);
        cadUsuarioLabel.setText("Nome completo: ");
        panel5.add(cadUsuarioLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TFemail = new JTextField();
        panel5.add(TFemail, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        emailLabel = new JLabel();
        Font emailLabelFont = this.$$$getFont$$$("Arial Rounded MT Bold", Font.PLAIN, 14, emailLabel.getFont());
        if (emailLabelFont != null) emailLabel.setFont(emailLabelFont);
        emailLabel.setText("E-mail:");
        panel5.add(emailLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TFtelefone = new JTextField();
        panel5.add(TFtelefone, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        telefoneLabel = new JLabel();
        Font telefoneLabelFont = this.$$$getFont$$$("Arial Rounded MT Bold", Font.PLAIN, 14, telefoneLabel.getFont());
        if (telefoneLabelFont != null) telefoneLabel.setFont(telefoneLabelFont);
        telefoneLabel.setText("Telefone:");
        panel5.add(telefoneLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TFsenha = new JPasswordField();
        panel5.add(TFsenha, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cadSenhaLabel = new JLabel();
        Font cadSenhaLabelFont = this.$$$getFont$$$("Arial Rounded MT Bold", Font.PLAIN, 14, cadSenhaLabel.getFont());
        if (cadSenhaLabelFont != null) cadSenhaLabel.setFont(cadSenhaLabelFont);
        cadSenhaLabel.setText("Senha:");
        panel5.add(cadSenhaLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cargoLabel = new JLabel();
        cargoLabel.setText("Cargo:");
        cargoLabel.setVisible(false);
        panel5.add(cargoLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CBCargo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        CBCargo.setModel(defaultComboBoxModel1);
        CBCargo.setVisible(false);
        panel5.add(CBCargo, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        estadoLabel = new JLabel();
        estadoLabel.setText("Estado:");
        estadoLabel.setVisible(false);
        panel5.add(estadoLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        comboBox1.setModel(defaultComboBoxModel2);
        comboBox1.setVisible(false);
        panel5.add(comboBox1, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }
}