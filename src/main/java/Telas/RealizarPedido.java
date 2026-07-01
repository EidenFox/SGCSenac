package Telas;

import DAO.PedidoDao;
import DAO.ProdutoDao;
import Model.ItemPedido;
import Model.Pedido;
import Model.Produto;
import Model.Usuario;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
}