package Telas;

import DAO.ProdutoDao;
import Model.Produto;
import Model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class ProdutosLista extends JFrame {
    private JPanel panel1;
    private JList listProdutosCadastrados;
    private JTextField TfProdutoID;
    private JButton BtPesquisa;
    private JButton BtSair;
    private JLabel TitleLabel;
    private JButton editarButton;
    private Usuario usuarioLogado;
    private DefaultListModel<Produto> modeloLista;

    public ProdutosLista(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setContentPane(panel1);
        setTitle("Consultar Produtos");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /* INICIO DE FUNÇÃO DE [Configuração de Componentes e Lista]; esta função inicializa o modelo da JList e garante que o botão de edição inicie bloqueado, carregando a lista completa de produtos na sequência */
        modeloLista = new DefaultListModel<>();
        listProdutosCadastrados.setModel(modeloLista);
        editarButton.setEnabled(false);
        carregarLista("");

        setVisible(true);

        listProdutosCadastrados.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if(usuarioLogado.getCargo() == 0){
                        editarButton.setEnabled(!listProdutosCadastrados.isSelectionEmpty());
                    }
                }
            }
        });

        BtPesquisa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarLista(TfProdutoID.getText().trim());
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Produto produtoSelecionado = (Produto) listProdutosCadastrados.getSelectedValue();
                if (produtoSelecionado != null) {
                    new ItemsCadastro(usuarioLogado, Optional.of(produtoSelecionado));
                    dispose();
                }
            }
        });

        BtSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuInicial(usuarioLogado);
                dispose();
            }
        });
    }

    private void carregarLista(String idFiltro) {
        ProdutoDao dao = new ProdutoDao();
        modeloLista.clear();
        editarButton.setEnabled(false);

        try {
            if (idFiltro.isEmpty()) {
                List<Produto> lista = dao.listarProdutos();
                for (Produto p : lista) {
                    modeloLista.addElement(p);
                }
            } else {
                Long idBusca = Long.parseLong(idFiltro);
                Produto p = dao.buscarPorId(idBusca);
                if (p != null) {
                    modeloLista.addElement(p);
                } else {
                    JOptionPane.showMessageDialog(panel1, "Nenhum produto encontrado.", "Busca Vazia", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel1, "O ID deve conter apenas números.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
    }
}