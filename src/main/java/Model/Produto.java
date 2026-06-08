package Model;

import java.math.BigDecimal;

public class Produto {
    private Long idProduto;
    private String nome;
    private String descricao;
    private int quantidade;
    private Categoria categoria;
    private BigDecimal preco;
    private int estado;

    public Produto(String nome, String descricao, int quantidade, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    public Produto() {
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            this.nome = null;
        } else {
            this.nome = nome.trim();
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            this.descricao = null;
        } else {
            this.descricao = descricao.trim();
        }
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
