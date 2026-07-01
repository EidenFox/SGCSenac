package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Long idPedido;
    private int statusPedido;
    private LocalDateTime dataPedido;
    private String motivoRecusa;
    private BigDecimal valorTotal;
    private int quantidadeTotalItens;
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

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeTotalItens() { return quantidadeTotalItens; }

    public void setQuantidadeTotalItens(int quantidadeTotalItens) {
        this.quantidadeTotalItens = quantidadeTotalItens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }

    @Override
    public String toString() {
        String statusStr = statusPedido == 0 ? "Pendente" : (statusPedido == 1 ? "Aprovado" : "Recusado");
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataStr = dataPedido != null ? dataPedido.format(formatter) : "Data Indisponível";

        return String.format("Pedido #%d | %s | Status: %s | Itens: %d | Total: R$ %s | Func: %s",
                idPedido, dataStr, statusStr, quantidadeTotalItens, valorTotal, usuario != null ? usuario.getNomeUsuario() : "");
    }
}