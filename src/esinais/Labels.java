package esinais;

import javax.swing.JLabel;

public class Labels {

    private JLabel nome;
    private String endereco;
    private int id;

    public Labels() {
    }

    public Labels(JLabel nome) {
        this.nome = nome;
    }

    public JLabel getNome() {
        return nome;
    }

    public void setNome(JLabel nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
