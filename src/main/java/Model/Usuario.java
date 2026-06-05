package Model;

public class Usuario {
    private int idUsuario;
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
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

   public Usuario(int idUsuario, String nomeUsuario, String senha, int estado) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase().trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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