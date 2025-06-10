package br.eng.dgjl.teatro.classes;

public class Ingresso {
    private String pecaNome;
    private String sessaoNome;
    private String areaNome;
    private int cadeiraPosicao;
    private int preco;
    private String CPF;

    public Ingresso(String pecaNome, String sessaoNome, String areaNome, int cadeiraPosicao, int preco, String CPF) {
        this.pecaNome = pecaNome;
        this.sessaoNome = sessaoNome;
        this.areaNome = areaNome;
        this.cadeiraPosicao = cadeiraPosicao;
        this.preco = preco;
        this.CPF = CPF;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getPecaNome() {
        return pecaNome;
    }

    public void setPecaNome(String pecaNome) {
        this.pecaNome = pecaNome;
    }

    public String getSessaoNome() {
        return sessaoNome;
    }

    public void setSessaoNome(String sessaoNome) {
        this.sessaoNome = sessaoNome;
    }

    public String getAreaNome() {
        return areaNome;
    }

    public void setAreaNome(String areaNome) {
        this.areaNome = areaNome;
    }

    public int getCadeiraPosicao() {
        return cadeiraPosicao;
    }

    public void setCadeiraPosicao(int cadeiraPosicao) {
        this.cadeiraPosicao = cadeiraPosicao;
    }

    public int getPreco() {
        return preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }
}
