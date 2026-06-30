package Telas;

import DAO.CategoriaDao;
import DAO.ProdutoDao;
import Model.Categoria;
import Model.Produto;
import Model.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ItemsCadastro extends JFrame {
    private JPanel panel1;
    private JLabel Title;
    private JLabel CategoriaLabel;
    private JLabel QuantidadeLabel;
    private JLabel UnidadeLabel;
    private JButton Cancelar;
    private JButton CadastrarButton;
    private JTextField TFdescricao;
    private JLabel DescricaoLabel;
    private JTextField TfNome;
    private JLabel NomeLabel;
    private JTextField TfPreco;
    private JLabel PrecoLabel;
    private JComboBox CbCategoria;
    private JSpinner SQuantidade;
    private JComboBox CbUnidades;
    private Usuario usuarioLogado;
    private Optional<Produto> produto;

    public ItemsCadastro(Usuario usuarioLogado, Optional<Produto> produto) {
        this.usuarioLogado = usuarioLogado;
        this.produto = produto;

        setContentPane(panel1);
        /* INICIO DE FUNÇÃO DE [Configuração de Spinner]; esta função define um modelo numérico com limite mínimo (0), bloqueando a digitação de valores negativos pelo utilizador */
        SpinnerNumberModel modeloQuantidade = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);   // Parâmetros: Valor inicial, Valor mínimo, Valor máximo, Incremento
        SQuantidade.setModel(modeloQuantidade);

        setTitle(produto.isPresent() ? "Editar Item" : "Cadastrar Item");
        setSize(640, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        comboBoxCategoria();
        preencherDados();

        Cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuInicial(usuarioLogado);
                dispose();
            }
        });

        CadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Nome
                String nome = TfNome.getText().trim();

                // Categoria
                Categoria categoriaSelecionada = (Categoria) CbCategoria.getSelectedItem();
                if (categoriaSelecionada == null) {
                    JOptionPane.showMessageDialog(panel1, "Selecione uma categoria válida para o produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Descrição
                String descricao = TFdescricao.getText().trim();

                // Quantidade
                int quantidade = (Integer) SQuantidade.getValue();

                // Preço
                String textoPreco = TfPreco.getText().trim().replace(",", ".");
                BigDecimal preco = null;

                if (!textoPreco.isEmpty()) {
                    try {
                        preco = new BigDecimal(textoPreco);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel1, "Introduza um valor numérico válido para o preço ou deixe o campo em branco.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Unidade:
                String unidade = "";
                if (CbUnidades.getSelectedItem() != null) {
                    unidade = CbUnidades.getSelectedItem().toString().trim().toLowerCase();
                }

                // Criação do Produto
                Produto produtoPreenchido = new Produto(nome, descricao, quantidade, preco, categoriaSelecionada, unidade);

                ProdutoDao produtoDao = new ProdutoDao();
                boolean sucesso;

                // Se for edição
                if (produto.isPresent()) {
                    produtoPreenchido.setIdProduto(produto.get().getIdProduto());
                    sucesso = produtoDao.editarProduto(produtoPreenchido, usuarioLogado);
                } else {
                    sucesso = produtoDao.cadastrarProduto(produtoPreenchido);
                }

                if (sucesso) {
                    JOptionPane.showMessageDialog(panel1, produto.isPresent() ? "Produto editado com Sucesso!" : "Produto cadastrado com Sucesso!");
                    new MenuInicial(usuarioLogado);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(panel1, "Erro ao processar a requisição no banco de dados!");
                }


            }
        });
    }

    private void preencherDados() {
        if (produto.isPresent()) {
            Produto p = produto.get();

            Title.setText("Editar Produto");
            CadastrarButton.setText("Editar");

            TfNome.setText(p.getNome());
            TFdescricao.setText(p.getDescricao());
            SQuantidade.setValue(p.getQuantidade());

            if (p.getPreco() != null) {
                // Converte de volta para string substituindo ponto por vírgula para manter o padrão PT-BR na tela
                TfPreco.setText(p.getPreco().toString().replace(".", ","));
            }

            // Encontrar a categoria correta no ComboBox comparando os IDs
            if (p.getCategoria() != null) {
                for (int i = 0; i < CbCategoria.getItemCount(); i++) {
                    Categoria catBox = (Categoria) CbCategoria.getItemAt(i);
                    if (catBox.getIdCategoria().equals(p.getCategoria().getIdCategoria())) {
                        CbCategoria.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Encontrar a unidade correta no ComboBox
            if (p.getUnidade() != null) {
                for (int i = 0; i < CbUnidades.getItemCount(); i++) {
                    String unBox = CbUnidades.getItemAt(i).toString();
                    if (unBox.equalsIgnoreCase(p.getUnidade())) {
                        CbUnidades.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }


    private void comboBoxCategoria(){
        CategoriaDao categoriaDao = new CategoriaDao();
        List<Categoria> lista = categoriaDao.listarCategorias();

        CbCategoria.removeAllItems();
        for (Categoria c : lista){
            CbCategoria.addItem(c);
        }
    }
}
