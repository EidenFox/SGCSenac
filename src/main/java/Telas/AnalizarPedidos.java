package Telas;

import DAO.PedidoDao;
import Model.ItemPedido;
import Model.Pedido;
import Model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class AnalizarPedidos extends JFrame {
    private JPanel panel1;
    private JButton BtPesquisar;
    private JButton BtAprovar;
    private JButton BtRejeitar;
    private JButton BtEditar;
    private JTextField TfPesquisa;
    private JList<Pedido> listPedidos;
    private JButton BtSair;
    private JRadioButton pendentesRadioButton;
    private JRadioButton aprovadosRadioButton;
    private JRadioButton rejeitadosRadioButton;
    private JButton BtDetalhes;
    private JButton gerarRelatorioButton;

    private Usuario usuarioLogado;
    private DefaultListModel<Pedido> modeloLista;

    public AnalizarPedidos(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(panel1);
        setTitle("Analisar Pedidos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(BtDetalhes);

        modeloLista = new DefaultListModel<>();
        listPedidos.setModel(modeloLista);

        BtAprovar.setEnabled(false);
        BtRejeitar.setEnabled(false);
        BtEditar.setEnabled(false);
        gerarRelatorioButton.setEnabled(false);

        listPedidos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // selecionar varios pedidos na tabela


        listPedidos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int qtdSelecionados = listPedidos.getSelectedIndices().length;

                    boolean unico = (qtdSelecionados == 1);
                    boolean multiplos = (qtdSelecionados >= 1);

                    BtAprovar.setEnabled(unico);
                    BtRejeitar.setEnabled(unico);
                    BtEditar.setEnabled(unico);
                    BtDetalhes.setEnabled(unico);
                    gerarRelatorioButton.setEnabled(multiplos);
                }
            }
        });

        listPedidos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // click duplo no pedido
                    abrirDetalhes();
                }
            }
        });


        ActionListener radioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarLista();
            }
        };
        pendentesRadioButton.addActionListener(radioListener);
        aprovadosRadioButton.addActionListener(radioListener);
        rejeitadosRadioButton.addActionListener(radioListener);

        BtPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarLista();
            }
        });

        BtSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuInicial(usuarioLogado);
                dispose();
            }
        });

        BtDetalhes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDetalhes();
            }
        });

        BtEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pedido pedidoSelecionado = listPedidos.getSelectedValue();
                if (pedidoSelecionado != null) {
                    new RealizarPedido(usuarioLogado, Optional.of(pedidoSelecionado));
                    dispose();
                }
            }
        });

        BtAprovar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pedido pedidoSelecionado = listPedidos.getSelectedValue();

                if (pedidoSelecionado != null) {
                    int confirmacao = JOptionPane.showConfirmDialog(panel1,
                            "Tem a certeza que deseja aprovar o pedido #" + pedidoSelecionado.getIdPedido() + "?",
                            "Confirmar Aprovação",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (confirmacao == JOptionPane.YES_OPTION) {
                        PedidoDao dao = new PedidoDao();

                        boolean sucesso = dao.processarPedido(pedidoSelecionado.getIdPedido(), 1, usuarioLogado.getIdUsuario());

                        if (sucesso) {
                            JOptionPane.showMessageDialog(panel1, "Pedido aprovado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            carregarLista();
                        } else {
                            JOptionPane.showMessageDialog(panel1, "Falha ao aprovar o pedido na base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        BtRejeitar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pedido pedidoSelecionado = listPedidos.getSelectedValue();

                if (pedidoSelecionado != null) {
                    // Cria uma área de texto
                    JTextArea txtMotivo = new JTextArea(5, 30);
                    txtMotivo.setLineWrap(true);
                    txtMotivo.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(txtMotivo);

                    int confirmacao = JOptionPane.showConfirmDialog(panel1,
                            scrollPane,
                            "Motivo da recusa (Pedido #" + pedidoSelecionado.getIdPedido() + "):",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (confirmacao == JOptionPane.OK_OPTION) {
                        String motivo = txtMotivo.getText().trim();

                        if (motivo.isEmpty()) {
                            JOptionPane.showMessageDialog(panel1, "É obrigatório informar um motivo para rejeitar o pedido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                            return; // Interrompe a execução para não salvar uma recusa vazia
                        }

                        PedidoDao dao = new PedidoDao();
                        boolean sucesso = dao.processarPedido(pedidoSelecionado.getIdPedido(), 2, usuarioLogado.getIdUsuario(), motivo);

                        if (sucesso) {
                            JOptionPane.showMessageDialog(panel1, "Pedido rejeitado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            carregarLista();
                        } else {
                            JOptionPane.showMessageDialog(panel1, "Falha ao rejeitar o pedido na base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        gerarRelatorioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Pedido> selecionados = listPedidos.getSelectedValuesList();
                if (!selecionados.isEmpty()) {
                    new RelatorioPedidos(usuarioLogado, selecionados);
                }
            }
        });

        pendentesRadioButton.setSelected(true);
        carregarLista();

        setVisible(true);
    }

    private void carregarLista() {
        int statusBusca = 0;
        if (aprovadosRadioButton.isSelected()) {
            statusBusca = 1;
        } else if (rejeitadosRadioButton.isSelected()) {
            statusBusca = 2;
        }

        String filtroNome = TfPesquisa.getText().trim();

        PedidoDao dao = new PedidoDao();
        List<Pedido> pedidos = dao.listarPedidosParaAnalise(statusBusca, filtroNome);

        modeloLista.clear();
        for (Pedido p : pedidos) {
            modeloLista.addElement(p);
        }

        BtAprovar.setEnabled(false);
        BtRejeitar.setEnabled(false);
        BtEditar.setEnabled(false);
        gerarRelatorioButton.setEnabled(false);
    }


    private void abrirDetalhes() {
        Pedido pedido = listPedidos.getSelectedValue();
        if (pedido != null) {
            PedidoDao dao = new PedidoDao();
            List<ItemPedido> itens = dao.buscarItensPorPedido(pedido.getIdPedido());

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("PEDIDO ID: ").append(pedido.getIdPedido()).append("\n");
            detalhes.append("Solicitante: ").append(pedido.getUsuario().getNomeUsuario()).append("\n\n");

            detalhes.append("--- ITENS SOLICITADOS ---\n");
            for (ItemPedido item : itens) {
                detalhes.append("• ").append(item.getProduto().getNome())
                        .append(" | Qtd: ").append(item.getQuantidade());

                if (item.getObservacao() != null && !item.getObservacao().trim().isEmpty()) {
                    detalhes.append("\n  Obs: ").append(item.getObservacao());
                }
                detalhes.append("\n");
            }

            detalhes.append("\nVALOR TOTAL: R$ ").append(pedido.getValorTotal()).append("\n");

            JTextArea textArea = new JTextArea(detalhes.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Revisão do Pedido", JOptionPane.INFORMATION_MESSAGE);
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
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BtPesquisar = new JButton();
        BtPesquisar.setText("Pesquisar");
        panel3.add(BtPesquisar, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TfPesquisa = new JTextField();
        TfPesquisa.setToolTipText("Insira o ID do pedido");
        panel3.add(TfPesquisa, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BtAprovar = new JButton();
        BtAprovar.setText("Aprovar");
        panel4.add(BtAprovar, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        BtRejeitar = new JButton();
        BtRejeitar.setText("Rejeitar");
        panel4.add(BtRejeitar, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BtEditar = new JButton();
        BtEditar.setText("Editar");
        panel4.add(BtEditar, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BtSair = new JButton();
        BtSair.setText("Sair");
        panel4.add(BtSair, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BtDetalhes = new JButton();
        BtDetalhes.setText("Detalhes");
        panel4.add(BtDetalhes, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gerarRelatorioButton = new JButton();
        gerarRelatorioButton.setText("Gerar Relatório");
        panel4.add(gerarRelatorioButton, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listPedidos = new JList();
        scrollPane1.setViewportView(listPedidos);
        final JToolBar toolBar1 = new JToolBar();
        panel1.add(toolBar1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        pendentesRadioButton = new JRadioButton();
        pendentesRadioButton.setSelected(true);
        pendentesRadioButton.setText("Pendentes");
        toolBar1.add(pendentesRadioButton);
        aprovadosRadioButton = new JRadioButton();
        aprovadosRadioButton.setText("Aprovados");
        toolBar1.add(aprovadosRadioButton);
        rejeitadosRadioButton = new JRadioButton();
        rejeitadosRadioButton.setSelected(false);
        rejeitadosRadioButton.setText("Rejeitados");
        toolBar1.add(rejeitadosRadioButton);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(pendentesRadioButton);
        buttonGroup.add(aprovadosRadioButton);
        buttonGroup.add(rejeitadosRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}