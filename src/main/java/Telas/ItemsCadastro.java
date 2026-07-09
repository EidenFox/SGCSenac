package Telas;

import DAO.CategoriaDao;
import DAO.ProdutoDao;
import Model.Categoria;
import Model.Produto;
import Model.Usuario;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
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
        getRootPane().setDefaultButton(CadastrarButton);
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


    private void comboBoxCategoria() {
        CategoriaDao categoriaDao = new CategoriaDao();
        List<Categoria> lista = categoriaDao.listarCategorias();

        CbCategoria.removeAllItems();
        for (Categoria c : lista) {
            CbCategoria.addItem(c);
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
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Title = new JLabel();
        Font TitleFont = this.$$$getFont$$$("Arial Black", Font.PLAIN, 18, Title.getFont());
        if (TitleFont != null) Title.setFont(TitleFont);
        Title.setText("Cadastro de Produtos");
        panel2.add(Title, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 6, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        CategoriaLabel = new JLabel();
        CategoriaLabel.setText("Categoria:");
        panel2.add(CategoriaLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        QuantidadeLabel = new JLabel();
        QuantidadeLabel.setText("Quantidade:");
        panel2.add(QuantidadeLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        UnidadeLabel = new JLabel();
        UnidadeLabel.setText("Unidades:");
        panel2.add(UnidadeLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        DescricaoLabel = new JLabel();
        DescricaoLabel.setText("Descrição:");
        panel2.add(DescricaoLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TFdescricao = new JTextField();
        panel2.add(TFdescricao, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        NomeLabel = new JLabel();
        NomeLabel.setText("Nome do produto:");
        panel2.add(NomeLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TfNome = new JTextField();
        panel2.add(TfNome, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrecoLabel = new JLabel();
        PrecoLabel.setText("Preço (opcional):");
        panel2.add(PrecoLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TfPreco = new JTextField();
        panel2.add(TfPreco, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        CbCategoria = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        CbCategoria.setModel(defaultComboBoxModel1);
        panel2.add(CbCategoria, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SQuantidade = new JSpinner();
        SQuantidade.setAutoscrolls(false);
        SQuantidade.setInheritsPopupMenu(false);
        SQuantidade.putClientProperty("html.disable", Boolean.FALSE);
        panel2.add(SQuantidade, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CbUnidades = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Un");
        defaultComboBoxModel2.addElement("Kg");
        defaultComboBoxModel2.addElement("g");
        defaultComboBoxModel2.addElement("L");
        defaultComboBoxModel2.addElement("Ml");
        CbUnidades.setModel(defaultComboBoxModel2);
        panel2.add(CbUnidades, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setVisible(true);
        panel1.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Cancelar = new JButton();
        Cancelar.setText("Cancelar");
        panel3.add(Cancelar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CadastrarButton = new JButton();
        CadastrarButton.setText("Cadastrar");
        panel3.add(CadastrarButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return panel1;
    }
}
