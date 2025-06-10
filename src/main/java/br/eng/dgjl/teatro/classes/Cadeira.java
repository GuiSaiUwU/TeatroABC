package br.eng.dgjl.teatro.classes;

public class Cadeira {
    private Area area;
    private int posicao;
    private String sessao;
    private boolean comprada = false;

    public Cadeira(Area area, int posicao, String sessao) {
        this.area = area;
        this.posicao = posicao;
        this.sessao = sessao;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public boolean isComprada() {
        return comprada;
    }

    public void setComprada(boolean comprada) {
        this.comprada = comprada;
    }
}
