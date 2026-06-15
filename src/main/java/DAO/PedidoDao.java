package DAO;

import Model.ItemPedido;
import Model.Pedido;
import Model.Produto;
import Model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDao {
    public boolean gerarSolicitacao(Pedido pedido) {
        String sqlPedido = "INSERT INTO Pedido (statusPedido, dataPedido, Usuario_idUsuario) VALUES (?, now(), ?)";
        String sqlItem = "INSERT INTO ItensPedido (Produtos_idProdutos, Pedido_idPedido, quantidade, precoUnitario) VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setInt(1, pedido.getStatusPedido());
                stmtPedido.setLong(2, pedido.getUsuario().getIdUsuario());
                stmtPedido.executeUpdate();

                try (ResultSet rs = stmtPedido.getGeneratedKeys()) {
                    if (rs.next()) {
                        long idPedidoGerado = rs.getLong(1);

                        try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                            for (ItemPedido item : pedido.getItens()) {
                                stmtItem.setLong(1, item.getProduto().getIdProduto());
                                stmtItem.setLong(2, idPedidoGerado);
                                stmtItem.setInt(3, item.getQuantidade());
                                stmtItem.setBigDecimal(4, item.getPrecoUnitario());
                                stmtItem.addBatch();
                            }
                            stmtItem.executeBatch();
                        }
                    } else {
                        throw new SQLException("Falha!! esta falhado!");
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro burro, aqui ó: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean processarPedido(Long idPedido, int novoStatus, Long idAdminLogado) {
        String sqlStatus = "UPDATE Pedido SET statusPedido = ? WHERE idPedido = ?";
        String sqlBuscaItens = "SELECT Produtos_idProdutos, quantidade FROM ItensPedido WHERE Pedido_idPedido = ?";
        String sqlBaixaEstoque = "UPDATE Produtos SET quantidadeEstoque = quantidadeEstoque - ?, updateID = ? WHERE idProdutos = ?";

        Connection conn = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtStatus = conn.prepareStatement(sqlStatus)) {
                stmtStatus.setInt(1, novoStatus); // 1 = Aprovado, 2 = Reprovado
                stmtStatus.setLong(2, idPedido);
                stmtStatus.executeUpdate();
            }

            if (novoStatus == 1) {
                try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscaItens);
                     PreparedStatement stmtBaixa = conn.prepareStatement(sqlBaixaEstoque)) {

                    stmtBusca.setLong(1, idPedido);

                    try (ResultSet rs = stmtBusca.executeQuery()) {
                        while (rs.next()) {
                            long idProduto = rs.getLong("Produtos_idProdutos");
                            int qtdComprada = rs.getInt("quantidade");

                            stmtBaixa.setInt(1, qtdComprada);
                            stmtBaixa.setLong(2, idAdminLogado);
                            stmtBaixa.setLong(3, idProduto);
                            stmtBaixa.addBatch();
                        }
                        stmtBaixa.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Deu ruim no: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Pedido> listarPedidosPorStatus(int status) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.idPedido, p.statusPedido, p.dataPedido, u.idUsuario, u.nomeUsuario " +
                "FROM Pedido p " +
                "INNER JOIN Usuario u ON p.Usuario_idUsuario = u.idUsuario " +
                "WHERE p.statusPedido = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
                    u.setNomeUsuario(rs.getString("nomeUsuario"));

                    Pedido p = new Pedido();
                    p.setIdPedido(rs.getLong("idPedido"));
                    p.setStatusPedido(rs.getInt("statusPedido"));

                    java.sql.Timestamp dataBanco = rs.getTimestamp("dataPedido");
                    if (dataBanco != null) {
                        p.setDataPedido(dataBanco.toLocalDateTime());
                    }

                    p.setUsuario(u);
                    pedidos.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Deu bosta aqui: " + e.getMessage());
        }
        return pedidos;
    }

    public List<ItemPedido> buscarItensPorPedido(Long idPedido) {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = "SELECT i.quantidade, i.precoUnitario, pr.idProdutos, pr.nomeProduto " +
                "FROM ItensPedido i " +
                "INNER JOIN Produtos pr ON i.Produtos_idProdutos = pr.idProdutos " +
                "WHERE i.Pedido_idPedido = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto prod = new Produto();
                    prod.setIdProduto(rs.getLong("idProdutos"));
                    prod.setNome(rs.getString("nomeProduto"));

                    ItemPedido item = new ItemPedido();
                    item.setProduto(prod);
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getBigDecimal("precoUnitario"));

                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Deu bosta aqui: " + e.getMessage());
        }
        return itens;
    }
}