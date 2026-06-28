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


    public ItemsCadastro(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(panel1);
        /* INICIO DE FUNÇÃO DE [Configuração de Spinner]; esta função define um modelo numérico com limite mínimo (0), bloqueando a digitação de valores negativos pelo utilizador */
        SpinnerNumberModel modeloQuantidade = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);   // Parâmetros: Valor inicial, Valor mínimo, Valor máximo, Incremento
        SQuantidade.setModel(modeloQuantidade);

        setTitle("Cadastrar itens");
        setSize(640, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        comboBoxCategoria();


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
                Produto produto = new Produto(nome, descricao, quantidade, preco, categoriaSelecionada, unidade);

                // Cadastrar Produto
                ProdutoDao produtoDao = new ProdutoDao();
                boolean sucesso = produtoDao.cadastrarProduto(produto);

                if (sucesso){
                    JOptionPane.showMessageDialog(panel1, "Produto cadastrado com Sucesso!");
                    new MenuInicial(usuarioLogado);
                    dispose();
                } else {
                  JOptionPane.showMessageDialog(panel1, "Erro ao cadastrar o produto!");
                }


            }
        });
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
