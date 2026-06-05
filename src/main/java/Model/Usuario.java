package Model;

public class Usuario {
    private int id;
    private String nome;
    private String senha;
    int state;

   public Usuario() {
    }

   public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

   public Usuario(int id, String nome, String senha, int state) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.state = state;
    }

   public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

   public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}