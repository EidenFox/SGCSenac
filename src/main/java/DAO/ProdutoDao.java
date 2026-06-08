package DAO;

import Model.Categoria;
import Model.Produto;
import Model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao {
    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.idProdutos, p.nomeProduto, p.descricaoProduto, p.precoProduto, p.quantidadeEstoque, p.estado AS estadoProduto, " +
                "c.idCategoria, c.nomeCategoria, c.descricaoCategoria, c.estado AS estadoCategoria " +
                "FROM Produtos p " +
                "INNER JOIN Categoria c ON p.Categoria_idCategoria = c.idCategoria " +
                "ORDER BY p.idProdutos ASC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getLong("idProdutos"));
                p.setNome(rs.getString("nomeProduto"));
                p.setDescricao(rs.getString("descricaoProduto"));
                p.setPreco(rs.getBigDecimal("precoProduto"));
                p.setQuantidade(rs.getInt("quantidadeEstoque"));
                p.setEstado(rs.getInt("estadoProduto"));

//                Funcionaria em pequena escala, mas com muitos produtos, fazer uma busca por categoria é um risco N+1 Query
//                CategoriaDao c = new CategoriaDao();
//                Long idCategoria = rs.getLong("Categoria_idCategoria");
//                p.setCategoria(c.buscarPorId(idCategoria));

                Categoria c = new Categoria();
                c.setIdCategoria(rs.getLong("idCategoria"));
                c.setNome(rs.getString("nomeCategoria"));
                c.setDescricao(rs.getString("descricaoCategoria"));
                c.setEstado(rs.getInt("estadoCategoria"));

                p.setCategoria(c);
                produtos.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    public boolean cadastrarProduto(Produto produto) {
        String sql = "INSERT INTO Produtos (nomeProduto, descricaoProduto, quantidadeEstoque, Categoria_idCategoria, precoProduto) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setLong(4, produto.getCategoria().getIdCategoria());
            stmt.setBigDecimal(5, produto.getPreco());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean editarProduto(Produto produto, Long idUsuarioLogado) {
        String sql = "UPDATE Produtos SET nomeProduto = ?, descricaoProduto = ?, quantidadeEstoque = ?, Categoria_idCategoria = ?, precoProduto = ?, updateID = ? WHERE idProdutos = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setLong(4, produto.getCategoria().getIdCategoria());
            stmt.setBigDecimal(5, produto.getPreco());
            stmt.setLong(6, idUsuarioLogado); // Necessário para o Log
            stmt.setLong(7, produto.getIdProduto());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeState(int state, Long idProdutoAlvo, Long idUsuarioLogado) {
        String sql = "UPDATE Produtos SET estado = ?, updateID = ? WHERE idProdutos = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state);
            stmt.setLong(2, idUsuarioLogado); // Necessário para o Log
            stmt.setLong(3, idProdutoAlvo);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   public Produto buscarPorId(Long id) {
        String sql = "SELECT p.idProdutos, p.nomeProduto, p.descricaoProduto, p.precoProduto, p.quantidadeEstoque, p.estado AS estadoProduto, " +
                "c.idCategoria, c.nomeCategoria, c.descricaoCategoria, c.estado AS estadoCategoria " +
                "FROM Produtos p " +
                "INNER JOIN Categoria c ON p.Categoria_idCategoria = c.idCategoria " +
                "WHERE p.idProdutos = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setIdProduto(rs.getLong("idProdutos"));
                    p.setNome(rs.getString("nomeProduto"));
                    p.setDescricao(rs.getString("descricaoProduto"));
                    p.setPreco(rs.getBigDecimal("precoProduto"));
                    p.setQuantidade(rs.getInt("quantidadeEstoque"));
                    p.setEstado(rs.getInt("estadoProduto"));

                    Categoria c = new Categoria();
                    c.setIdCategoria(rs.getLong("idCategoria"));
                    c.setNome(rs.getString("nomeCategoria"));
                    c.setDescricao(rs.getString("descricaoCategoria"));
                    c.setEstado(rs.getInt("estadoCategoria"));

                    p.setCategoria(c);

                    return p;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }



}
