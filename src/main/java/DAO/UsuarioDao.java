package DAO;

import Model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public boolean cadastrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario (numIdentificacao, nomeUsuario, email, cargo, senha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getNumIdentificacao());
            stmt.setString(2, usuario.getNomeUsuario());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getCargo());
            stmt.setString(5, usuario.getSenha());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean editarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuario SET nomeUsuario = ?, email = ?, telefone = ?, cargo = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setInt(4, usuario.getCargo());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeState(int state, int id) {
        String sql = "UPDATE Usuario SET estado = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM Usuario WHERE email = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            // email SEMPRE é considerado lower, salvar no banco já tratado

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setNumIdentificacao(rs.getInt("numIdentificacao"));
                    u.setNomeUsuario(rs.getString("nomeUsuario"));
                    u.setEmail(rs.getString("email"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setCargo(rs.getInt("cargo"));
                    u.setSenha(rs.getString("senha"));
                    u.setEstado(rs.getInt("estado"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setNumIdentificacao(rs.getInt("numIdentificacao"));
                    u.setNomeUsuario(rs.getString("nomeUsuario"));
                    u.setEmail(rs.getString("email"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setCargo(rs.getInt("cargo"));
                    u.setSenha(rs.getString("senha"));
                    u.setEstado(rs.getInt("estado"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT idUsuario, numIdentificacao, nomeUsuario, email, telefone, cargo, estado FROM Usuario ORDER BY id ASC"; //remover "senha", está aqui apenas para fins de teste

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNumIdentificacao(rs.getInt("numIdentificacao"));
                u.setNomeUsuario(rs.getString("nomeUsuario"));
                u.setEmail(rs.getString("email"));
                u.setTelefone(rs.getString("telefone"));
                u.setCargo(rs.getInt("cargo"));
                u.setEstado(rs.getInt("estado"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }


    // Lista usuarios ativos ou desativados
    // funciona, mas o trabalho é maior do que filtrar direto na função "listar todos" com um if a mais
    public List<Usuario> listarEspecial(int state){

        List<Usuario> usuarios = listarUsuarios();
        List<Usuario> usuariosFiltrados = new ArrayList<>();

        for (Usuario u : usuarios ){
            if (u.getEstado() == state){
                usuariosFiltrados.add(u);
                System.out.println(u.getEstado());
            }
        }

        return usuariosFiltrados;
    }

}