package DAO;

import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

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
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) System.out.println("Erro: Email já cadastado");
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("numIdentificacao")) System.out.println("Erro: Crachá já cadastado");
            return false;
        }
    }

    public boolean editarUsuario(Usuario usuario, Long usuarioLogado) {
        String sql = "UPDATE Usuario SET nomeUsuario = ?, email = ?, telefone = ?, cargo = ?, updateID = ? WHERE idUsuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setInt(4, usuario.getCargo());
            stmt.setLong(5, usuarioLogado);  // ID DO USUARIO LOGADO (quem está fazendo a edição)
            stmt.setLong(6, usuario.getIdUsuario());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeState(int state, Long idAlvo, Long idLogado) {
        String sql = "UPDATE Usuario SET estado = ?, updateID = ? WHERE idUsuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, state);
            stmt.setLong(2, idLogado);  // Para o LOG
            stmt.setLong(3, idAlvo);    // Para o WHERE
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorCracha(int cracha) {
        String sql = "SELECT * FROM Usuario WHERE numIdentificacao = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cracha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
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

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM Usuario WHERE email = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            // email SEMPRE é considerado lower, salvar no banco já tratado

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
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

    public Usuario buscarPorId(Long id) {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
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
        String sql = "SELECT idUsuario, numIdentificacao, nomeUsuario, email, telefone, cargo, senha, estado FROM Usuario ORDER BY idUsuario ASC"; //remover "senha", está aqui apenas para fins de teste

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getLong("idUsuario"));
                u.setNumIdentificacao(rs.getInt("numIdentificacao"));
                u.setNomeUsuario(rs.getString("nomeUsuario"));
                u.setEmail(rs.getString("email"));
                u.setTelefone(rs.getString("telefone"));
                u.setCargo(rs.getInt("cargo"));
                u.setSenha(rs.getString("senha"));
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


    public boolean checarSenha(String plainPassword, String storedHash) {
        return BCrypt.checkpw(plainPassword, storedHash);
    }

    public boolean mudarSenha(String senhaNova, String senhaAntiga, Long idUsuario) {
        Usuario usuarioEncontrado = buscarPorId(idUsuario);
        String senhaHash;
        
        if (usuarioEncontrado != null) {
            if (checarSenha(senhaAntiga, usuarioEncontrado.getSenha())) {
                senhaHash = BCrypt.hashpw(senhaNova, BCrypt.gensalt());
            }else return false;
        }else return false;

        String sql = "UPDATE Usuario SET senha = ?, updateID = ? WHERE idUsuario = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, senhaHash);
            stmt.setLong(2, idUsuario);  // Para o LOG
            stmt.setLong(3, idUsuario);  // Para o WHERE
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}