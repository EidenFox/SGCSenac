package Telas;

import DAO.PedidoDao;
import Model.Pedido;
import Model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

        listPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
                    new RealizarPedido(usuarioLogado, java.util.Optional.of(pedidoSelecionado));
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
            java.util.List<Model.ItemPedido> itens = dao.buscarItensPorPedido(pedido.getIdPedido());

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("PEDIDO ID: ").append(pedido.getIdPedido()).append("\n");
            detalhes.append("Solicitante: ").append(pedido.getUsuario().getNomeUsuario()).append("\n\n");

            detalhes.append("--- ITENS SOLICITADOS ---\n");
            for (Model.ItemPedido item : itens) {
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
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Revisão do Pedido", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}