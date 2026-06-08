package DAO;

import Model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categoria ORDER BY idCategoria ASC";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getLong("idCategoria"));
                c.setNome(rs.getString("nomeCategoria"));
                c.setDescricao(rs.getString("descricaoCategoria"));
                c.setEstado(rs.getInt("estado"));
                categorias.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return categorias;
    }

    public boolean cadastrarCategoria(Categoria categoria) {
        String sql = "INSERT INTO Categoria (nomeCategoria, descricaoCategoria) VALUES (?, ?)";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean changeState(int state, Long idAlvo) {
        String sql = "UPDATE Categoria SET estado = ? WHERE idCategoria = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state);
            stmt.setLong(2, idAlvo);    // Para o WHERE
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public Categoria buscarPorId(Long id) {
        String sql = "SELECT * FROM Categoria WHERE idCategoria = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setIdCategoria(rs.getLong("idCategoria"));
                    c.setNome(rs.getString("nomeCategoria"));
                    c.setDescricao(rs.getString("descricaoCategoria"));
                    c.setEstado(rs.getInt("estado"));

                    return c;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean editarCategoria(Categoria categoria) {
        String sql = "UPDATE Categoria SET nomeCategoria = ?, descricaoCategoria = ? WHERE idCategoria = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setLong(3, categoria.getIdCategoria());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



}
