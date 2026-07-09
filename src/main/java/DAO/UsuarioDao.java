package DAO;

import Model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    public boolean cadastrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario (numIdentificacao, nomeUsuario, email, cargo, estado, senha) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getNumIdentificacao());
            stmt.setString(2, usuario.getNomeUsuario());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getCargo());
            stmt.setInt(5, usuario.getEstado());
            stmt.setString(6, usuario.getSenha());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) System.out.println("Erro: Email já cadastrado");
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("numIdentificacao")) System.out.println("Erro: Crachá já cadastrado");
            }
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

    public Optional<Usuario> buscarPorCracha(int cracha) {
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
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
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
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorId(Long id) {
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
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
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
            }
        }

        return usuariosFiltrados;
    }

    public boolean checarSenha(String plainPassword, String storedHash) {
        return BCrypt.checkpw(plainPassword, storedHash);
    }

    public boolean forcarNovaSenha(String senhaHashNova, Long idAlvo, Long idAdminLogado) {
        String sql = "UPDATE Usuario SET senha = ?, updateID = ? WHERE idUsuario = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, senhaHashNova);
            stmt.setLong(2, idAdminLogado);
            stmt.setLong(3, idAlvo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao forçar redefinição de senha: " + e.getMessage());
            return false;
        }
    }

    public int alterarSenha(String senhaNova, String senhaAntiga, Long idUsuario) {
        String sqlBusca = "SELECT senha, numero_tentativas, ultima_tentativa FROM Usuario WHERE idUsuario = ?";
        String hashBanco = null;
        int tentativas = 0;
        java.time.LocalDateTime ultimaTentativa = null;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {
            stmt.setLong(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hashBanco = rs.getString("senha");
                    tentativas = rs.getInt("numero_tentativas");
                    java.sql.Timestamp ts = rs.getTimestamp("ultima_tentativa");
                    if (ts != null) {
                        ultimaTentativa = ts.toLocalDateTime();
                    }
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro na busca de segurança: " + e.getMessage());
            return -2;
        }

        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        boolean emTempoDeBloqueio = false;

        if (ultimaTentativa != null) {
            long minutosPassados = java.time.Duration.between(ultimaTentativa, agora).toMinutes();
            if (minutosPassados < 30) {
                emTempoDeBloqueio = true;
            } else {
                tentativas = 0;
            }
        }

        if (emTempoDeBloqueio && tentativas >= 5) {
            return -1;
        }

        boolean senhaCorreta = BCrypt.checkpw(senhaAntiga, hashBanco);

        if (senhaCorreta) {
            String senhaNovaHash = BCrypt.hashpw(senhaNova, BCrypt.gensalt());
            String sqlUpdate = "UPDATE Usuario SET senha = ?, numero_tentativas = 0, ultima_tentativa = NULL, updateID = ? WHERE idUsuario = ?";

            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, senhaNovaHash);
                stmt.setLong(2, idUsuario);
                stmt.setLong(3, idUsuario);
                stmt.executeUpdate();
                return 1;
            } catch (SQLException e) {
                System.out.println("Erro ao gravar nova senha: " + e.getMessage());
                return -2;
            }
        } else {
            tentativas++;
            String sqlFail = "UPDATE Usuario SET numero_tentativas = ?, ultima_tentativa = ? WHERE idUsuario = ?";

            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlFail)) {
                stmt.setInt(1, tentativas);
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(agora));
                stmt.setLong(3, idUsuario);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Erro ao registrar falha de segurança: " + e.getMessage());
            }
            return 0;
        }
    }


}