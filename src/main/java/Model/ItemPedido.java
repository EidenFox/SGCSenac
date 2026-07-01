package Model;

import java.math.BigDecimal;

public class ItemPedido {
    private Long idItensPedido;
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private String observacao;


    public ItemPedido() {
    }

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario, String observacao) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.observacao = observacao;
    }

    public Long getIdItensPedido() {
        return idItensPedido;
    }

    public void setIdItensPedido(Long idItensPedido) {
        this.idItensPedido = idItensPedido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}