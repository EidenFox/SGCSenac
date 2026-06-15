package Telas;

import DAO.UsuarioDao;
import Model.Usuario;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JPanel panel1;
    private JButton EntrarBT;
    private JLabel usuarioCracha;
    private JTextField usuarioTF;
    private JLabel senhaLabel;
    private JPasswordField senhaTF;

    public static Usuario usuarioLogado;

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
            Usuario usuarioEncontrado = dao.buscarPorCracha(cracha);

            if (usuarioEncontrado != null) {

                if (dao.checarSenha(senhaDigitada, usuarioEncontrado.getSenha())) {

                    if (usuarioEncontrado.getEstado() == 0) {
                        JOptionPane.showMessageDialog(this, "Usuário BANIDO! faz o ticket pai");
                        return;
                    }

                    usuarioLogado = usuarioEncontrado;
                    JOptionPane.showMessageDialog(this, "Bem vindo a bordo, " + usuarioLogado.getNomeUsuario() + "!");

                    if (usuarioLogado.getCargo() == 0) {
                        System.out.println("Limpa os pé antes de entrar: ADMINISTRADOR");
                        // TODO: Chamar a Tela Principal com TODAS as funções liberadas

                    } else if (usuarioLogado.getCargo() == 1) {
                        System.out.println("Limpa os pé antes de entrar: USUÁRIO");
                        // TODO: Chamar a Tela Principal APENAS com a tela de Gerar Solicitação

                    }
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Pare de tentar invedir contas indian!!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Quem é voce?");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Só hablo em numeros nego");
        }
    }




    public static void main(String[] args) {
        new Login();
    }
}