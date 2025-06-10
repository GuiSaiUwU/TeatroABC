package br.eng.dgjl.teatro.classes;

import java.util.List;

public class Peca {
    private String nome;
    private String[] sessoes;
    private List<Cadeira> cadeiraList;

    public Peca(String nome, String[] sessoes, List<Cadeira> cadeiraList) {
        this.nome = nome;
        this.sessoes = sessoes;
        this.cadeiraList = cadeiraList;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String[] getSessoes() {
        return sessoes;
    }

    public void setSessoes(String[] sessoes) {
        this.sessoes = sessoes;
    }

    public List<Cadeira> getCadeiraList() {
        return cadeiraList;
    }

    public void setCadeiraList(List<Cadeira> cadeiraList) {
        this.cadeiraList = cadeiraList;
    }
}
