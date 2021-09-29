package Servidor;

import Cliente.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Servidor {

    ReentrantLock lock = new ReentrantLock();
    Condition condContacto = lock.newCondition();
    Condition condLocaisInteresse = lock.newCondition();

    private Map<String,Utilizador> utilizadores;
    private Mapa mapa;

    /**
     * Metodo construtor, sem parametros, de um servidor.
     */
    public Servidor() {
        this.utilizadores = new HashMap<>();
        this.mapa = new Mapa();
    }

    /**
     * Metodo responsavel por verificar se a password esta correta para o username
     * @param username username do cliente
     * @param pass passord inserida pelo cliente
     * @return um frame com a resposta a dar ao cliente, indicando se a password esta correta, se o utilizador esta doente e
     * o tipo de utilizador (especial ou nao), em caso de erro indica o motivo
     */
    public Frame realizaLogin(String username, String pass){
        try {
            lock.lock();
            boolean suc = false;
            String erro = null;
            boolean especial = false;
            boolean doente = false;

            if (!this.utilizadores.containsKey(username))
                erro = "Username não existe";
            else {
                Utilizador u = this.utilizadores.get(username);
                if (!pass.equals(u.getPassword()))
                    erro = "Password errada.";
                else {
                    suc = true;
                    especial = u.isEspecial();
                    doente = u.isDoente();
                }
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(suc).getBytes());
            if (!suc)
                data.add(1, erro.getBytes());
            else{
                data.add(1, String.valueOf(especial).getBytes());
                data.add(2, String.valueOf(doente).getBytes());
            }

            return new Frame(1, data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por criar um novo utilizador
     * @param username username para o novo utilizador
     * @param nome nome completo do utilizador
     * @param pass password a associar a esse utilizador
     * @param especial boolean que indica se é um utilizador especial ou nao
     * @return um frame com informação sobre o sucesso da criação da conta
     */
    public Frame criaConta(String username, String nome, String pass, boolean especial){
        try {
            lock.lock();
            boolean suc = false;
            String erro = null;

            if (this.utilizadores.containsKey(username))
                erro = "Username já existe";
            else {
                Utilizador u = new Utilizador(username, nome, pass, especial);
                this.utilizadores.put(username, u);
                suc = true;
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(suc).getBytes());
            if (erro != null)
                data.add(1, erro.getBytes());

            return new Frame(2, data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por defeniar uma nova localização para um determinado utilizador
     * Quando a localização muda é necessario verificar se o utilizador entrou em contacto com alguem e
     * se a antiga localização se encontra agora vazia
     * @param username nome do utilizador a mudar a localização
     * @param x cordenada em x da nova localizacao
     * @param y cordenada em y da nova localizacao
     * @return um frame com o sucesso da operação
     */
    public Frame setLocalizacao(String username, Double x, Double y){
        try {
            lock.lock();

            boolean suc = false;
            Utilizador u = this.utilizadores.get(username);

            if (u != null) {
                Localizacao antiga = u.getLocalAtual().clone();
                Localizacao l = new Localizacao(x, y);
                u.setLocalAtual(l);

                for (Utilizador ut : this.utilizadores.values()) {
                    if (!ut.getUserName().equals(username) && ut.getLocalAtual().equals(l)) {
                        ut.addPessoaContacto(u);
                        u.addPessoaContacto(ut);
                    }
                }

                this.mapa.adicionaPessoa(l.clone(), u);

                boolean posicaoFicouVazia = this.mapa.posicaoVazia(antiga);
                if (posicaoFicouVazia) {
                    condLocaisInteresse.signalAll();
                }

                suc = true;
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(suc).getBytes());
            return new Frame(3, data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por verificar quantas pessoas estão numa dada localizacao, em caso de a localizacao
     * pedida seja a mesma do utilizador o mesmo é contabilizado
     * @param username username do utilizador que pediu informação
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return frame com o numero de pessoas na localizacao
     */
    public Frame getNumPessoasLocalizacao(String username, Double x, Double y){
        try {
            lock.lock();
            Localizacao l = new Localizacao(x, y);
            int total = 0;
            for (Utilizador ut : this.utilizadores.values()) {
                //username.equals(ut.getUserName()) &&
                if (ut.getLocalAtual().equals(l)) {
                    total++;
                }
            }
            String totalString = String.valueOf(total);
            List<byte[]> data = new ArrayList<>();
            data.add(0, totalString.getBytes());
            return new Frame(4, data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por devolver o mapa representado no servidor
     * @return um frame com o mapa
     */
    public Frame getMapa(){
        try {
            lock.lock();
            return new Frame(5,this.mapa.getMapa());
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por defenir um utilizador como doente
     * @param username username do utilizador a definir
     */
    public void setDoente(String username) {
        try {
            lock.lock();
            Utilizador u = this.utilizadores.get(username);
            u.setDoente(true);
            u.setLocalAtual(new Localizacao(Double.MAX_VALUE,Double.MIN_VALUE));

            condContacto.signalAll();

        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por adicionar um local de interesse a um determinado utilizador, adiciona na queue de
     * locais de interesse como também na queue a notificar (caso o local esteja ocupado). Informa o numero de pessoas
     * para esse local
     * @param username nome do utilizador
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return um frame com o numero de utilizadores no local
     */
    public Frame adicionaLocalInteresse(String username, Double x, Double y) {
        try {
            lock.lock();
            Utilizador u = this.utilizadores.get(username);
            Localizacao l = new Localizacao(x,y);
            u.addLocalInteresse(l);

            int total = 0;
            for (Utilizador ut : this.utilizadores.values()){
                if(ut.getLocalAtual().equals(l))
                    total++;
            }

            if(total==0){
                u.removelocalInteresseNotificacao(l);
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(total).getBytes());

            return new Frame(7,data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsasavel por avisar um utilizador que esteve em contacto com alguem infetado. Retem o thread,
     * enquanto o utilizador continuar logado e sem estar em contacto com ninguem
     * @param username nome do utilizador a informar
     * @return frame com a informação que esteve em contacto.
     * @throws InterruptedException .
     */
    public Frame esteveContacto(String username) throws InterruptedException {
        try {
            lock.lock();
            int i=0;
            boolean res = false;
            Utilizador u = this.utilizadores.get(username);
            for (Map<Boolean,Utilizador> ls : u.getPessoasContacto()) {
                if (ls.containsKey(false)){
                    Utilizador ut = ls.get(false);
                    if (ut.isDoente()){
                        res = true;
                        u.removePessoasContactoNotificar(i);
                    }
                }
                i++;
            }

            while (!res){
                condContacto.await();
                i = 0;
                for (Map<Boolean,Utilizador> ls : u.getPessoasContacto()) {
                    if (ls.containsKey(false)){
                        Utilizador ut = ls.get(false);
                        if (ut.isDoente()){
                            res = true;
                            u.removePessoasContactoNotificar(i);
                        }
                    }
                    i++;
                }
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(res).getBytes());

            return new Frame(10,data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo por remover um local da lista de interesses.
     * @param username nome do utilizador a remover.
     * @param l localizacao a remover.
     */
    public void removeLocalInteresse(String username, Localizacao l) {
        try {
            lock.lock();
            Utilizador u = this.utilizadores.get(username);
            u.removelocalInteresse(l);
            u.removelocalInteresseNotificacao(l);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por indicar a disponibilidade de todos os locais de interesses de um utilizador
     * @param username username do utilizador a pedir a informação
     * @return um frame com a infromação
     */
    public Frame getDispLocaisInteresse(String username) {
        try {
            lock.lock();
            Utilizador u = this.utilizadores.get(username);
            List<Localizacao> lista = u.getLocaisInteresse();
            List<byte[]> data = new ArrayList<>();
            data.add(0, String.valueOf(lista.size()).getBytes());

            for (Localizacao l : lista) {
                data.add(String.valueOf(l.getX()).getBytes());
                data.add(String.valueOf(l.getY()).getBytes());
                data.add(String.valueOf(this.mapa.posicaoVazia(l)).getBytes());
            }
            return new Frame(8, data);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo responsavel por informar um utilizador que certos locais de interesse se encontram agora vazios.
     * Retem o thread enquanto não houver locais a informar, ou o utilizadores não tenha locais de interesses.
     * @param username username do utilizador
     * @return frame com os locais agora vazios.
     * @throws InterruptedException .
     */
    public Frame posicoesLivres(String username) throws InterruptedException {
        try {
            lock.lock();
            Utilizador u = this.utilizadores.get(username);
            List<Localizacao> locaisNotificar = new ArrayList<>();
            for (Localizacao ln : u.getLocaisInteresseNotificar()){
                if(this.mapa.posicaoVazia(ln))
                    locaisNotificar.add(ln.clone());
            }

            while (locaisNotificar.isEmpty()){
                this.condLocaisInteresse.await();

                locaisNotificar = new ArrayList<>();
                for (Localizacao ln : u.getLocaisInteresseNotificar()){
                    if(this.mapa.posicaoVazia(ln))
                        locaisNotificar.add(ln.clone());
                }
            }

            List<byte[]> data = new ArrayList<>();
            data.add(0,String.valueOf(locaisNotificar.size()).getBytes());

            for (Localizacao l : locaisNotificar){
                data.add(String.valueOf(l.getX()).getBytes());
                data.add(String.valueOf(l.getY()).getBytes());

                u.removelocalInteresseNotificacao(l);
            }

            return new Frame(11,data);

        }finally {
            lock.unlock();
        }
    }
}
