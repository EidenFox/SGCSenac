package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Long idPedido;
    private int statusPedido;
    private LocalDateTime dataPedido;
    private Usuario usuario;
    private List<ItemPedido> itens;

    public Pedido() {
        this.itens = new ArrayList<>();
    }

    public Pedido(int statusPedido, Usuario usuario) {
        this.statusPedido = statusPedido;
        this.usuario = usuario;
        this.itens = new ArrayList<>();
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public int getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(int statusPedido) {
        this.statusPedido = statusPedido;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }
}