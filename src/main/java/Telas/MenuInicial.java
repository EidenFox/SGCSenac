package Telas;
import Model.Usuario;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

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
    private JTextPane TaCreditos;
    private Usuario usuarioLogado;


    public MenuInicial(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(panel1);
        setTitle("Menu Principal");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        TitleLabel.setText("Bem vindo " + usuarioLogado.getNomeUsuario());
        habilitarBotoes();
        setVisible(true);

        TaCreditos.setContentType("text/html");
        TaCreditos.setEditable(false);
        TaCreditos.setOpaque(false);

        String textoSobre =
                "<html><body style='font-family: Consolas; font-size: 11px; text-align: center; color: #555555;'>" +
                "Sistema de Gestão (SGC) desenvolvido por: <br>" +
                "<a href=\"https://github.com/EidenFox\">Daniel Rocha</a> <br> " +
                "<a href=\"https://github.com/JovemPadrawn\">Nickolas Anderson</a> <br> " +
                "<a href=\"https://github.com/1tsc0sta\">Matheus Costa</a> <br> " +
                "<a href=\"https://github.com/kauan-cotes\">Kauan Cotes</a> <br> " +
                "<a href=\"https://github.com/EdwardLeywin\">Eduardo Nunes</a>" +
                "<a href=\"https://github.com/AnaaRosa\">Ana Camila</a>" +
                "</body></html>";

        TaCreditos.setText(textoSobre);

        TaCreditos.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException | URISyntaxException ex) {
                            System.out.println("Erro ao tentar abrir o link: " + ex.getMessage());
                        }
                    }
                }
            }
        });


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


        BtNovoPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RealizarPedido(usuarioLogado, Optional.empty());
                dispose();
            }
        });
        BtAcompPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AcompanharPedidos(usuarioLogado);
                dispose();
            }
        });
        BtAnalisaPedidos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AnalizarPedidos(usuarioLogado);
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
