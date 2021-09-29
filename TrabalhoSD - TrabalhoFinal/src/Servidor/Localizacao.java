package Servidor;

import java.util.Objects;

public class Localizacao implements Comparable<Localizacao>{

    private Double x;
    private Double y;

    public Localizacao(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Localizacao(Localizacao l) {
        this.x = l.getX();
        this.y = l.getY();
    }

    /**
     * Metodo resposavel por devolver a posição em x
     * @return o valor da cooredenada em x
     */
    public Double getX() {
        return x;
    }

    /**
     * Metodo resposavel por defenir a posição em x
     * @param x o valor da cooredenada em x
     */
    public void setX(Double x) {
        this.x = x;
    }

    /**
     * Metodo resposavel por devolver a posição em y
     * @return o valor da cooredenada em y
     */
    public Double getY() {
        return y;
    }

    /**
     * Metodo resposavel por defenir a posição em y
     * @param y o valor da cooredenada em y
     */
    public void setY(Double y) {
        this.y = y;
    }

    /**
     * Metodo que calcula o valor hash para uma localizacao
     * @return o valor hash
     */
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Metodo que verifica se um objeto é igual ao atual
     * @param obj objeto a verificar a igualdade
     * @return o valor da igualdade
     */
    public boolean equals(Object obj) {
        if(this==obj)
            return true;
        if(obj==null||this.getClass()!=obj.getClass())
            return false;
        Localizacao l = (Localizacao) obj;

        return ((this.x.equals(l.getX()))&&(this.y.equals(l.getY())));
    }

    /**
     * Metdo resposnasel por defenir o o nivel de igualdade de uma localizacao
     * @param o localizacao a comparar
     * @return o valor da comparacao
     */
    public int compareTo(Localizacao o) {
        return this.x.compareTo(o.getX())==0 ? this.y.compareTo(o.getY()) : this.x.compareTo(o.getX());
    }

    /**
     * Metodo responsavel por realizar um clone do objeto atual
     * @return clone
     */
    public Localizacao clone(){
        return new Localizacao(this);
    }

    /**
     * Metodo que transforma uma localizacao em string
     * @return a localizacao em formato string
     */
    public String toString() {
        return "("+this.x+","+this.y+")";
    }
}
