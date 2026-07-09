package Telas;

import DAO.UsuarioDao;
import Model.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MudarSenha extends JFrame {
    private JPanel main;
    private JButton mudarSenhaButton;
    private JPasswordField passwordField1; // Campo: Senha Antiga
    private JPasswordField passwordField2; // Campo: Senha Nova

    private Usuario usuarioLogado;

    public MudarSenha(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setContentPane(main);
        setTitle("Trocar Senha");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getRootPane().setDefaultButton(mudarSenhaButton);

        mudarSenhaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String senhaAntiga = new String(passwordField1.getPassword());
                String senhaNova = new String(passwordField2.getPassword());

                if (senhaAntiga.trim().isEmpty() || senhaNova.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(main, "Por favor, preencha ambos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UsuarioDao dao = new UsuarioDao();
                int resultado = dao.alterarSenha(senhaNova, senhaAntiga, usuarioLogado.getIdUsuario());

                if (resultado == 1) {
                    JOptionPane.showMessageDialog(main, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else if (resultado == -1) {
                    JOptionPane.showMessageDialog(main, "Limite de tentativas excedido. Por segurança, aguarde 30 minutos antes de tentar novamente.", "Bloqueio de Segurança", JOptionPane.ERROR_MESSAGE);
                } else if (resultado == 0) {
                    JOptionPane.showMessageDialog(main, "A senha antiga está incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(main, "Ocorreu um erro interno na comunicação com a base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}