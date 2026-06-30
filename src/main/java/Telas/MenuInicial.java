package Telas;
import Model.Usuario;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class MenuInicial extends JFrame {
    private JPanel panel1;
    private JButton BtSair;
    private JButton BtCadItem;
    private JButton BtNovoPedido;
    private JButton BtAcompPedido;
    private JButton BtCadFuncionario;
    private JButton BtAnalisaPedidos;
    private JLabel TitleLabel;
    private JButton listarProdutosButton;
    private Usuario usuarioLogado;


    public MenuInicial(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(panel1);
        setTitle("Menu Principal");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        TitleLabel.setText("Bem vindo " + usuarioLogado.getNomeUsuario());
        habilitarBotoes();
        setVisible(true);


        BtSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        BtCadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ItemsCadastro(usuarioLogado, Optional.empty());
                dispose();
            }
        });
        listarProdutosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProdutosLista(usuarioLogado);
                dispose();
            }
        });
    }
        private void habilitarBotoes(){
            if (usuarioLogado.getCargo() != 0) {
                BtCadFuncionario.setVisible(false);
                BtAnalisaPedidos.setVisible(false);

            }
        }






}
