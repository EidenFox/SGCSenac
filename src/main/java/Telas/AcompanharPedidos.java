package Telas;

import DAO.PedidoDao;
import Model.ItemPedido;
import Model.Pedido;
import Model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AcompanharPedidos extends JFrame {
    private JPanel panel1;
    private JTextField TfConsultarID;
    private JButton BtPesquisar;
    private JList<Pedido> listPedidos;
    private JButton BtSair;
    private JButton BtDetalhes;

    private Usuario usuarioLogado;
    private DefaultListModel<Pedido> modeloLista;

    public AcompanharPedidos(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(panel1);
        setTitle("Acompanhar Pedidos");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloLista = new DefaultListModel<>();
        listPedidos.setModel(modeloLista);
        listPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        BtDetalhes.setEnabled(false);

        carregarLista("");

        listPedidos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    BtDetalhes.setEnabled(!listPedidos.isSelectionEmpty());
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

        BtPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarLista(TfConsultarID.getText());
            }
        });

        BtDetalhes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDetalhes();
            }
        });

        BtSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuInicial(usuarioLogado);
                dispose();
            }
        });

        setVisible(true);
    }

    private void carregarLista(String filtroID) {
        PedidoDao dao = new PedidoDao();
        List<Pedido> pedidos = dao.listarPedidosAcompanhamento(usuarioLogado, filtroID);

        modeloLista.clear();
        for (Pedido p : pedidos) {
            modeloLista.addElement(p);
        }

        BtDetalhes.setEnabled(false);
    }

    private void abrirDetalhes() {
        Pedido pedido = listPedidos.getSelectedValue();
        if (pedido != null) {
            PedidoDao dao = new PedidoDao();
            List<ItemPedido> itens = dao.buscarItensPorPedido(pedido.getIdPedido());

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("PEDIDO ID: ").append(pedido.getIdPedido()).append("\n");
            detalhes.append("Solicitante: ").append(pedido.getUsuario().getNomeUsuario()).append("\n");

            String statusStr = pedido.getStatusPedido() == 0 ? "Pendente" : (pedido.getStatusPedido() == 1 ? "Aprovado" : "Recusado");
            detalhes.append("Status Atual: ").append(statusStr).append("\n\n");

            detalhes.append("--- ITENS DO PEDIDO ---\n");
            for (ItemPedido item : itens) {
                detalhes.append("• ").append(item.getProduto().getNome())
                        .append(" | Qtd: ").append(item.getQuantidade())
                        .append(" | Preço Un: R$").append(item.getPrecoUnitario());
                if (item.getObservacao() != null && !item.getObservacao().trim().isEmpty()) {
                    detalhes.append(" | Obs: ").append(item.getObservacao());
                }
                detalhes.append("\n");
            }

            detalhes.append("\nVALOR TOTAL DO PEDIDO: R$").append(pedido.getValorTotal()).append("\n");

            if (pedido.getStatusPedido() == 2 && pedido.getMotivoRecusa() != null) {
                detalhes.append("\nMOTIVO DA RECUSA:\n").append(pedido.getMotivoRecusa()).append("\n");
            }

            JTextArea textArea = new JTextArea(detalhes.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(450, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Detalhes do Pedido", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}