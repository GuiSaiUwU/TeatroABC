package br.eng.dgjl.teatro.classes;

public class Area {
    private String nome;
    private int qtdMaxCadeiras;
    private int preco;

    public Area(String nome, int qtdMaxCadeiras, int preco) {
        this.nome = nome;
        this.qtdMaxCadeiras = qtdMaxCadeiras;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQtdMaxCadeiras() {
        return qtdMaxCadeiras;
    }

    public void setQtdMaxCadeiras(int qtdMaxCadeiras) {
        this.qtdMaxCadeiras = qtdMaxCadeiras;
    }

    public int getPreco() {
        return preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }
}
