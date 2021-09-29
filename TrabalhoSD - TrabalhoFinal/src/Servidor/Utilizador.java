package Servidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utilizador {
    private String userName;
    private String Nome;
    private String password;

    private List<Map<Boolean,Utilizador>> pessoasContacto;

    private List<Localizacao> locaisInteresse;
    private List<Localizacao> locaisInteresseNotificar;

    private Localizacao localAtual;

    private boolean doente;
    private boolean especial;

    public Utilizador(String userName, String nome, String password, boolean especial) {
        this.userName = userName;
        Nome = nome;
        this.password = password;
        this.pessoasContacto = new ArrayList<>();
        this.locaisInteresse = new ArrayList<>();
        this.locaisInteresseNotificar = new ArrayList<>();
        this.doente = false;
        this.especial = especial;

        this.localAtual = new Localizacao(Double.MIN_VALUE,Double.MIN_VALUE);
    }

    /**
     * Metodo que retorna o username do utilizador
     * @return username do utilizador
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Metodo que define o username do utilizador
     * @param userName username do utilizador
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Metodo que informa o nome do utilizador
     * @return nome do utilizador
     */
    public String getNome() {
        return Nome;
    }

    /**
     * Metodo que define o nome do utilizador
     * @param nome nome do utilizador
     */
    public void setNome(String nome) {
        Nome = nome;
    }

    /**
     * Metodo que retorna a password do utilizador
     * @return password do utilizador
     */
    public String getPassword() {
        return password;
    }

    /**
     * Metodo que define a password do utilizador
     * @param password password do utilizador
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Metodo que informa as pessoas com quem o utilizador esteve em contacto
     * @return pessoas com quem o utilizador esteve em contacto
     */
    public List<Map<Boolean,Utilizador>> getPessoasContacto() {
        return pessoasContacto;
    }

    /**
     * Metodo que define as pessoas com quem o utilizador esteve em contacto
     * @param pessoasContacto pessoas com quem o utilizador esteve em contacto
     */
    public void setPessoasContacto(List<Map<Boolean,Utilizador>> pessoasContacto) {
        this.pessoasContacto = pessoasContacto;
    }

    /**
     * Metodo que informa os locais de interesse do utilizador
     * @return locais de interesse do utilizador
     */
    public List<Localizacao> getLocaisInteresse() {
        return locaisInteresse;
    }

    /**
     * Metodo que informa os locais de interesse do utilizador que tem de ser notificados
     * @return locais de interesse do utilizador que tem de ser notificados
     */
    public List<Localizacao> getLocaisInteresseNotificar() {
        return locaisInteresseNotificar;
    }

    /**
     * Metodo que informa se o utilizador se encontra doente
     * @return estado de saude do utilizador
     */
    public boolean isDoente() {
        return doente;
    }

    /**
     *  Metodo que define o utilizador como saudavel ou doente
     * @param doente estado de saude do utilizador (se esta doente - true - ou nao - false)
     */
    public void setDoente(boolean doente) {
        this.doente = doente;
    }

    /**
     * Metodo que informa se um utilizador é especial
     * @return indormação sobre o tipo de utilizador (especial ou nao)
     */
    public boolean isEspecial() {
        return especial;
    }

    /**
     * Metedo que define uma pessoa como especial ou normal
     * @param especial tipo de pessoa
     */
    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    /**
     * Metodo que devolve a localizacao da pessoa
     * @return localizacao do utilizador
     */
    public Localizacao getLocalAtual() {
        return localAtual;
    }

    /**
     * Metodo que define a localizacao do utilizador
     * @param localAtual nova localizacao
     */
    public void setLocalAtual(Localizacao localAtual) {
        this.localAtual = localAtual;
    }

    /**
     * Metodo responsavel por adicionar uma pessoa na lista de pessoas que o utilizador esteve em contacto.
     * Caso o utilizador ja tenha estado em contacto com essa pessoa, a mesma nao é adicionada
     * @param ut pessoa que contactou
     */
    public void addPessoaContacto(Utilizador ut) {
        if(this.pessoasContacto.stream().map(Map::values).noneMatch(l -> l.stream().anyMatch(u -> u.getUserName()
                .equals(ut.getUserName())))){
            HashMap<Boolean, Utilizador> add = new HashMap<>();
            add.put(false, ut);
            this.pessoasContacto.add(add);
        }
    }

    /**
     * Metodo que informa se o utilizador tem interesse na localizacao
     * @param l localizacao a confirmar
     * @return o valor da confirmação
     */
    public boolean temInteresse(Localizacao l) {
        return this.locaisInteresse.contains(l);
    }

    /**
     * Metodo que adiciona uma localizacao como local de interesse caso a mesma nao esteja já marcada
     * como tal.
     * @param l localizacao a adicionar
     */
    public void addLocalInteresse(Localizacao l) {
        if(this.locaisInteresse.stream().noneMatch(li->li.equals(l)))
            this.locaisInteresse.add(l);

        if(this.locaisInteresseNotificar.stream().noneMatch(li->li.equals(l)))
            this.locaisInteresseNotificar.add(l);
    }

    /**
     * Metodo que remove uma localizacao da lista de locais de interesse
     * @param l localizacao a remover
     */
    public void removelocalInteresse(Localizacao l) {
        this.locaisInteresse.remove(l);
    }

    /**
     * Metodo que remove uma localizacao da lista de locais de interesse a notificar
     * @param l localizacao a remover
     */
    public void removelocalInteresseNotificacao(Localizacao l) {
        this.locaisInteresseNotificar.remove(l);
    }

    /**
     * Metodo que altera o estado de notificação de contacto. Ao notificar anota a informação que notificou
     * @param index o index da lista a mudar
     */
    public void removePessoasContactoNotificar(int index) {
        Utilizador ut = this.pessoasContacto.get(index).remove(false);
        this.pessoasContacto.get(index).put(true,ut);
    }
}
