package Telas;

import DAO.UsuarioDao;
import Model.Usuario;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class Login extends JFrame {
    private JPanel panel1;
    private JButton EntrarBT;
    private JLabel usuarioCracha;
    private JTextField usuarioTF;
    private JLabel senhaLabel;
    private JPasswordField senhaTF;


    public Login() {
        setContentPane(panel1);
        setTitle("SGC Senac - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
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

                    if (usuarioEncontrado.getEstado() == 0) {
                        JOptionPane.showMessageDialog(this, "Usuário BANIDO! faz o ticket pai");
                        return;
                    }

                    Usuario usuarioValidado = usuarioEncontrado;
                    JOptionPane.showMessageDialog(this, "Bem vindo a bordo, " + usuarioValidado.getNomeUsuario() + "!");
                    new MenuInicial(usuarioValidado);
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Pare de tentar invedir contas!!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Quem é voce?");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Crachá deve conter apenas Numeros");
        }
    }




    public static void main(String[] args) {
        new Login();
    }
}