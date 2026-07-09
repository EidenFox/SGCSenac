package Telas;

import DAO.UsuarioDao;
import Model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class Login extends JFrame {
    private JPanel panel1;
    private JButton EntrarBT;
    private JLabel usuarioCracha;
    private JTextField usuarioTF;
    private JLabel senhaLabel;
    private JPasswordField senhaTF;
    private JLabel cadastrarLabel;


    public Login() {
        setContentPane(panel1);
        setTitle("SGC Senac - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(EntrarBT);

        cadastrarLabel.setText("<html><u>Não tem uma conta ainda? Cadastrar!</u></html>");
        cadastrarLabel.setForeground(Color.BLUE);
        cadastrarLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cadastrarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new FuncionariosCadastro();
            }
        });

        setVisible(true);

        EntrarBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });
    }

    private void fazerLogin() {
        try {
            int cracha = Integer.parseInt(usuarioTF.getText());
            String senhaDigitada = new String(senhaTF.getPassword());

            UsuarioDao dao = new UsuarioDao();
            Optional<Usuario> usuarioOpt = dao.buscarPorCracha(cracha);

            if (usuarioOpt.isPresent()) {
                Usuario usuarioEncontrado = usuarioOpt.get();

                if (dao.checarSenha(senhaDigitada, usuarioEncontrado.getSenha())) {
                    if (usuarioEncontrado.getEstado() != 1) {
                        JOptionPane.showMessageDialog(this, "Usuário atualmente desativado, entre em contato com a gerência!");
                        return;
                    }

                    Usuario usuarioValidado = usuarioEncontrado;
                    JOptionPane.showMessageDialog(this, "Bem vindo, " + usuarioValidado.getNomeUsuario() + "!");
                    new MenuInicial(usuarioValidado);
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos!!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos!!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Crachá deve conter apenas Numeros");
        }
    }




    public static void main(String[] args) {
        new Login();
    }
}