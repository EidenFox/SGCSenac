package Telas;

import DAO.PedidoDao;
import DAO.ProdutoDao;
import Model.ItemPedido;
import Model.Pedido;
import Model.Produto;
import Model.Usuario;

import java.awt.*;
import java.util.Locale;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyleContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RealizarPedido extends JFrame {

    private JPanel main;
    private JTextField TfObservacao;
    private JButton BtEnviar;
    private JButton BtCancelar;
    private JButton BtExcluir;
    private JLabel TitleLabel;
    private JButton adicionarButton;
    private JTable JtItens;
    private JTable JtItensAdicionados;
    private JSpinner spinner1;
    private JTextField TfPesquisa;
    private JButton procurarButton;
    private JFormattedTextField formattedTFData;
    private Optional<Pedido> pedidoExistente;

    private Usuario usuarioLogado;
    private DefaultTableModel modeloItens;
    private TableRowSorter<DefaultTableModel> sorterItens;
    private DefaultTableModel modeloItensAdicionados;

    public RealizarPedido(Usuario usuarioLogado, Optional<Pedido> pedidoExistente) {
        this.usuarioLogado = usuarioLogado;
        this.pedidoExistente = pedidoExistente;

        setContentPane(main);
        setTitle(pedidoExistente.isPresent() ? "Editar Pedido #" + pedidoExistente.get().getIdPedido() : "Novo Pedido");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(BtEnviar);

        SpinnerNumberModel modeloQuantidade = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        spinner1.setModel(modeloQuantidade);

        adicionarButton.setEnabled(false);
        BtExcluir.setEnabled(false);

        configurarTabelaItens();
        carregarTabelaItens();
        configurarTabelaAdicionados();

        if (pedidoExistente.isPresent()) {
            carregarItensEdicao();
        }

        JtItens.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    adicionarButton.setEnabled(JtItens.getSelectedRow() != -1);
                }
            }
        });

        /* INICIO DE FUNÇÃO DE [Adicionar Item ao Pedido]; esta função captura os dados da tabela superior convertendo o índice visual para o índice do modelo (evitando erros com o filtro de pesquisa), combina com os campos de entrada e insere na tabela inferior */
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = JtItens.getSelectedRow();
                if (linhaSelecionada != -1) {
                    int linhaModel = JtItens.convertRowIndexToModel(linhaSelecionada);

                    Object id = modeloItens.getValueAt(linhaModel, 0);
                    Object nome = modeloItens.getValueAt(linhaModel, 1);
                    int quantidade = (Integer) spinner1.getValue();
                    String observacao = TfObservacao.getText().trim();

                    Object[] novaLinha = {id, nome, quantidade, observacao};
                    modeloItensAdicionados.addRow(novaLinha);

                    spinner1.setValue(1);
                    TfObservacao.setText("");
                    JtItens.clearSelection();
                }
            }
        });

        /* INICIO DE FUNÇÃO DE [Ouvinte de Seleção da Tabela Inferior]; esta função ativa o botão de exclusão apenas quando uma linha da lista de itens adicionados é selecionada */
        JtItensAdicionados.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    BtExcluir.setEnabled(JtItensAdicionados.getSelectedRow() != -1);
                }
            }
        });

        /* INICIO DE FUNÇÃO DE [Remover Item do Pedido]; esta função exclui a linha selecionada da tabela inferior e desativa o botão novamente */
        BtExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = JtItensAdicionados.getSelectedRow();
                if (linhaSelecionada != -1) {
                    modeloItensAdicionados.removeRow(linhaSelecionada);
                    BtExcluir.setEnabled(false);
                }
            }
        });

        procurarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoBusca = TfPesquisa.getText().trim();
                if (textoBusca.isEmpty()) {
                    sorterItens.setRowFilter(null);
                } else {
                    sorterItens.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusca, 1));
                }
            }
        });

        BtCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuInicial(usuarioLogado);
                dispose();
            }
        });

        setVisible(true);
        BtEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modeloItensAdicionados.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(main, "Adicione pelo menos um item ao pedido antes de enviar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ProdutoDao produtoDao = new ProdutoDao();
                Pedido pedidoParaSalvar;

                if (pedidoExistente.isPresent()) {
                    pedidoParaSalvar = pedidoExistente.get();
                    pedidoParaSalvar.getItens().clear();
                } else {
                    pedidoParaSalvar = new Pedido(0, usuarioLogado);
                }

                for (int i = 0; i < modeloItensAdicionados.getRowCount(); i++) {
                    Long idProduto = (Long) modeloItensAdicionados.getValueAt(i, 0);
                    int quantidade = (Integer) modeloItensAdicionados.getValueAt(i, 2);
                    String observacao = (String) modeloItensAdicionados.getValueAt(i, 3);

                    Produto produto = produtoDao.buscarPorId(idProduto);

                    ItemPedido item = new ItemPedido();
                    item.setProduto(produto);
                    item.setQuantidade(quantidade);
                    item.setPrecoUnitario(produto.getPreco());
                    item.setObservacao(observacao);

                    pedidoParaSalvar.adicionarItem(item);
                }

                PedidoDao pedidoDao = new PedidoDao();
                boolean sucesso;

                if (pedidoExistente.isPresent()) {
                    sucesso = pedidoDao.atualizarSolicitacao(pedidoParaSalvar);
                } else {
                    sucesso = pedidoDao.gerarSolicitacao(pedidoParaSalvar);
                }

                if (sucesso) {
                    JOptionPane.showMessageDialog(main, pedidoExistente.isPresent() ? "Pedido atualizado com sucesso!" : "Pedido gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    new MenuInicial(usuarioLogado);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(main, "Erro ao processar o pedido. Verifique a comunicação com a base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    private void configurarTabelaItens() {
        String[] colunas = {"ID", "Nome", "Descrição", "Preço", "Estoque"};
        modeloItens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JtItens.setModel(modeloItens);

        sorterItens = new TableRowSorter<>(modeloItens);
        JtItens.setRowSorter(sorterItens);
    }

    private void configurarTabelaAdicionados() {
        String[] colunas = {"ID", "Nome", "Quantidade", "Observação"};
        modeloItensAdicionados = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JtItensAdicionados.setModel(modeloItensAdicionados);
    }

    private void carregarTabelaItens() {
        ProdutoDao dao = new ProdutoDao();
        List<Produto> produtos = dao.listarProdutos();

        modeloItens.setRowCount(0);
        for (Produto p : produtos) {
            Object[] linha = {
                    p.getIdProduto(),
                    p.getNome(),
                    p.getDescricao(),
                    p.getPreco(),
                    p.getQuantidade()
            };
            modeloItens.addRow(linha);
        }
    }

    private void carregarItensEdicao() {
        PedidoDao dao = new PedidoDao();
        List<ItemPedido> itens = dao.buscarItensPorPedido(pedidoExistente.get().getIdPedido());

        for (ItemPedido item : itens) {
            Object[] linha = {
                    item.getProduto().getIdProduto(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getObservacao()
            };
            modeloItensAdicionados.addRow(linha);
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
        main.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        TitleLabel = new JLabel();
        Font TitleLabelFont = this.$$$getFont$$$("Arial", Font.BOLD, 28, TitleLabel.getFont());
        if (TitleLabelFont != null) TitleLabel.setFont(TitleLabelFont);
        TitleLabel.setText("Novo Pedido");
        panel1.add(TitleLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        main.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BtEnviar = new JButton();
        BtEnviar.setText("Enviar");
        panel2.add(BtEnviar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BtCancelar = new JButton();
        BtCancelar.setText("Cancelar");
        panel2.add(BtCancelar, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Quantidade: *");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, 26, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Item: *");
        panel3.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, -1, 22, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Observação:");
        panel3.add(label3, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TfObservacao = new JTextField();
        panel3.add(TfObservacao, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), new Dimension(150, 50), new Dimension(-1, 100), 0, false));
        adicionarButton = new JButton();
        adicionarButton.setText("Adicionar");
        panel3.add(adicionarButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        JtItens = new JTable();
        scrollPane1.setViewportView(JtItens);
        spinner1 = new JSpinner();
        panel3.add(spinner1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TfPesquisa = new JTextField();
        panel3.add(TfPesquisa, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        procurarButton = new JButton();
        procurarButton.setText("Procurar");
        panel3.add(procurarButton, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Procurar Item:");
        panel3.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel4.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        JtItensAdicionados = new JTable();
        scrollPane2.setViewportView(JtItensAdicionados);
        BtExcluir = new JButton();
        BtExcluir.setEnabled(false);
        BtExcluir.setText("Remover");
        panel4.add(BtExcluir, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$("Arial", Font.BOLD, 28, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Itens do Pedido");
        panel4.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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