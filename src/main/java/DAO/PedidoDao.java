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
        String sqlItem = "INSERT INTO ItensPedido (Produtos_idProdutos, Pedido_idPedido, quantidade, precoUnitario, observacao) VALUES (?, ?, ?, ?, ?)";

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
                                stmtItem.setString(5, item.getObservacao());
                                stmtItem.addBatch();
                            }
                            stmtItem.executeBatch();
                        }
                    } else {
                        throw new SQLException("Falha ao recuperar o ID do pedido.");
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
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
        return processarPedido(idPedido, novoStatus, idAdminLogado, null);
    }

    public boolean processarPedido(Long idPedido, int novoStatus, Long idAdminLogado, String motivoRecusa) {
        String sqlStatus = "UPDATE Pedido SET statusPedido = ?, motivoRecusa = ? WHERE idPedido = ?";
        String sqlBuscaItens = "SELECT Produtos_idProdutos, quantidade FROM ItensPedido WHERE Pedido_idPedido = ?";
        String sqlBaixaEstoque = "UPDATE Produtos SET quantidadeEstoque = quantidadeEstoque - ?, updateID = ? WHERE idProdutos = ?";

        Connection conn = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtStatus = conn.prepareStatement(sqlStatus)) {
                stmtStatus.setInt(1, novoStatus);
                stmtStatus.setString(2, motivoRecusa); // Se for aprovação, recebe null
                stmtStatus.setLong(3, idPedido);
                stmtStatus.executeUpdate();
            }

            if (novoStatus == 1) {
                try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscaItens);
                     PreparedStatement stmtBaixa = conn.prepareStatement(sqlBaixaEstoque)) {

                    stmtBusca.setLong(1, idPedido);

                    try (ResultSet rs = stmtBusca.executeQuery()) {
                        while (rs.next()) {     // tratamento de produto negativo
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
            System.out.println("Erro: " + e.getMessage());
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

    public boolean atualizarSolicitacao(Pedido pedido) {
        String sqlDelete = "DELETE FROM ItensPedido WHERE Pedido_idPedido = ?";
        String sqlItem = "INSERT INTO ItensPedido (Produtos_idProdutos, Pedido_idPedido, quantidade, precoUnitario, observacao) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            try(PreparedStatement stmtDel = conn.prepareStatement(sqlDelete)) {
                stmtDel.setLong(1, pedido.getIdPedido());
                stmtDel.executeUpdate();
            }

            try(PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                for (ItemPedido item : pedido.getItens()) {
                    stmtItem.setLong(1, item.getProduto().getIdProduto());
                    stmtItem.setLong(2, pedido.getIdPedido());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setBigDecimal(4, item.getPrecoUnitario());
                    stmtItem.setString(5, item.getObservacao());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pedido: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<Pedido> listarPedidosAcompanhamento(Usuario usuarioLogado, String idFiltro) {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.idPedido, p.statusPedido, p.dataPedido, p.motivoRecusa, u.idUsuario, u.nomeUsuario, " +
                        "COALESCE(SUM(i.quantidade), 0) as totalItens, " +
                        "COALESCE(SUM(i.quantidade * i.precoUnitario), 0) as valorTotal " +
                        "FROM Pedido p " +
                        "INNER JOIN Usuario u ON p.Usuario_idUsuario = u.idUsuario " +
                        "LEFT JOIN ItensPedido i ON p.idPedido = i.Pedido_idPedido " +
                        "WHERE 1=1 "
        );

        if (usuarioLogado.getCargo() != 0) {
            sql.append("AND p.Usuario_idUsuario = ? ");
        }

        if (idFiltro != null && !idFiltro.trim().isEmpty()) {
            sql.append("AND p.idPedido = ? ");
        }

        sql.append("GROUP BY p.idPedido, p.statusPedido, p.dataPedido, p.motivoRecusa, u.idUsuario, u.nomeUsuario ");
        sql.append("ORDER BY p.dataPedido DESC");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (usuarioLogado.getCargo() != 0) {
                stmt.setLong(paramIndex++, usuarioLogado.getIdUsuario());
            }
            if (idFiltro != null && !idFiltro.trim().isEmpty()) {
                stmt.setLong(paramIndex++, Long.parseLong(idFiltro.trim()));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
                    u.setNomeUsuario(rs.getString("nomeUsuario"));

                    Pedido p = new Pedido();
                    p.setIdPedido(rs.getLong("idPedido"));
                    p.setStatusPedido(rs.getInt("statusPedido"));
                    p.setMotivoRecusa(rs.getString("motivoRecusa"));
                    p.setQuantidadeTotalItens(rs.getInt("totalItens"));
                    p.setValorTotal(rs.getBigDecimal("valorTotal"));

                    java.sql.Timestamp dataBanco = rs.getTimestamp("dataPedido");
                    if (dataBanco != null) {
                        p.setDataPedido(dataBanco.toLocalDateTime());
                    }
                    p.setUsuario(u);
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro SQL: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("ID inserido não é numérico.");
        }
        return lista;
    }

    public List<Pedido> listarPedidosParaAnalise(int status, String nomeFiltro) {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.idPedido, p.statusPedido, p.dataPedido, p.motivoRecusa, u.idUsuario, u.nomeUsuario, " +
                        "COALESCE(SUM(i.quantidade), 0) as totalItens, " +
                        "COALESCE(SUM(i.quantidade * i.precoUnitario), 0) as valorTotal " +
                        "FROM Pedido p " +
                        "INNER JOIN Usuario u ON p.Usuario_idUsuario = u.idUsuario " +
                        "LEFT JOIN ItensPedido i ON p.idPedido = i.Pedido_idPedido " +
                        "WHERE p.statusPedido = ? "
        );

        if (nomeFiltro != null && !nomeFiltro.trim().isEmpty()) {
            sql.append("AND u.nomeUsuario LIKE ? ");
        }

        sql.append("GROUP BY p.idPedido, p.statusPedido, p.dataPedido, p.motivoRecusa, u.idUsuario, u.nomeUsuario ");
        sql.append("ORDER BY p.dataPedido DESC");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setInt(1, status);

            if (nomeFiltro != null && !nomeFiltro.trim().isEmpty()) {
                // Adiciona os curingas % para permitir que a busca encontre o nome em qualquer parte do texto
                stmt.setString(2, "%" + nomeFiltro.trim() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
                    u.setNomeUsuario(rs.getString("nomeUsuario"));

                    Pedido p = new Pedido();
                    p.setIdPedido(rs.getLong("idPedido"));
                    p.setStatusPedido(rs.getInt("statusPedido"));
                    p.setMotivoRecusa(rs.getString("motivoRecusa"));
                    p.setQuantidadeTotalItens(rs.getInt("totalItens"));
                    p.setValorTotal(rs.getBigDecimal("valorTotal"));

                    java.sql.Timestamp dataBanco = rs.getTimestamp("dataPedido");
                    if (dataBanco != null) {
                        p.setDataPedido(dataBanco.toLocalDateTime());
                    }
                    p.setUsuario(u);
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro SQL: " + e.getMessage());
        }
        return lista;
    }

    public List<ItemPedido> buscarItensPorPedido(Long idPedido) {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = "SELECT i.quantidade, i.precoUnitario, i.observacao, pr.idProdutos, pr.nomeProduto " +
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
                    item.setObservacao(rs.getString("observacao"));

                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        return itens;
    }
}