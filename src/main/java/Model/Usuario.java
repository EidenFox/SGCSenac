package Model;

public class Usuario {
    private Long idUsuario;
    private int numIdentificacao;
    private String nomeUsuario;
    private String email;
    private String telefone;
    private int cargo;
    private String senha;
    private int estado;

   public Usuario() {
    }

   public Usuario(String nomeUsuario, String senha) {
        this.nomeUsuario = nomeUsuario.trim();
        this.senha = senha;
    }

   public Usuario(Long idUsuario, String nomeUsuario, String senha, int estado) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario.trim();
        this.senha = senha;
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getNumIdentificacao() {
        return numIdentificacao;
    }

    public void setNumIdentificacao(int numIdentificacao) {
        this.numIdentificacao = numIdentificacao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            this.email = null;
        } else {
            this.email = email.trim().toLowerCase();
        }
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            this.telefone = null;
        } else {
            this.telefone = telefone;
        }
    }

    public int getCargo() {
        return cargo;
    }

    public void setCargo(int cargo) {
        this.cargo = cargo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}