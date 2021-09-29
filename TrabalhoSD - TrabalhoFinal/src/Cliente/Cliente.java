package Cliente;

import Cliente.Excecoes.LoginInvalidoException;
import Servidor.Localizacao;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Cliente implements ICliente {

    private Coneccao con;
    private Coneccao conGetContacto;
    private Coneccao conGetLocais;

    private String userName;
    private boolean doente;
    private boolean definirLocalizacao;

    ReentrantLock l = new ReentrantLock();

    public Cliente() throws IOException {
        Socket s = new Socket("localhost", 12345);
        this.con = new Coneccao(s);
    }

    public Cliente(String userName, boolean doente, Coneccao c) throws IOException {
        this.userName = userName;
        this.doente = doente;
        this.definirLocalizacao = true;

        this.con = c;
        Socket s2 = new Socket("localhost", 12345);
        this.conGetContacto = new Coneccao(s2);
        Socket s3 = new Socket("localhost", 12345);
        this.conGetLocais = new Coneccao(s3);

        Runnable worker1 = () -> {

            try { Thread.sleep(100); } catch (InterruptedException ignored) { }

            try{
                while (true) {
                    List<byte[]> data = new ArrayList<>();
                    data.add(0, userName.getBytes());
                    this.conGetContacto.send(new Frame(10, data));

                    Frame f = this.conGetContacto.receive();
                    boolean contacto = Boolean.parseBoolean(new String(f.getData().get(0)));

                    if (contacto) {
                        try {
                            l.lock();
                            System.out.println("---");
                            System.out.println("*******************");
                            System.out.println("Esteve em contacto.");
                            System.out.println("*******************");
                            System.out.print("Opção: ");
                        } finally {
                            l.unlock();
                        }
                    }
                }
            }catch (IOException ignored){ }
        };

        Runnable worker2 = () -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) { }

            try {
                while (true) {

                    List<byte[]> data = new ArrayList<>();
                    data.add(0, userName.getBytes());
                    this.conGetLocais.send(new Frame(11, data));

                    Frame f = this.conGetLocais.receive();
                    data = f.getData();

                    try {
                        l.lock();
                        System.out.println("---");
                        System.out.println("************************************");

                        int tam = Integer.parseInt(new String(data.get(0)));
                        int num = 1;

                        for (int i = 0; i < tam; i++) {
                            double x = Double.parseDouble(new String(data.get(num)));
                            num++;
                            double y = Double.parseDouble(new String(data.get(num)));
                            num++;
                            System.out.println("Posição (" + x + "," + y + ") encontra-se livre.");
                        }
                        System.out.println("************************************");
                        System.out.print("Opção: ");

                    } finally {
                        l.unlock();
                    }
                }
            }catch (IOException ignored){ }
        };

        Thread a = new Thread(worker1);
        Thread b = new Thread(worker2);

        if(!doente){
            a.start();
            b.start();
        }
    }

    /**
     * Metodo responsavel por enviar ao servidor a informação da nova localizacao do utilizador
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o resulrado do cotacto com o servidor
     */
    public boolean setLocalizacao(Double x, Double y) throws IOException {

        String xS = String.valueOf(x);
        String yS = String.valueOf(y);

        List<byte[]> data = new ArrayList<>();
        data.add(0, userName.getBytes());
        data.add(1, xS.trim().getBytes());
        data.add(2, yS.trim().getBytes());
        con.send(new Frame(3, data));

        Frame frame = con.receive();
        data = frame.getData();
        boolean res = Boolean.parseBoolean(new String(data.get(0)));

        this.definirLocalizacao = false;

        return res;
    }

    /**
     * Metodo responsavel por pedir ao servidor o numero de pessoas num a localizacao
     * e informar o cliente
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o numero de pessoas numa localizacao (-1 em caso de erro ao contactar o servidor)
     */
    public int getPessoasLocalizacao(Double x, Double y) throws IOException {

        String xS = String.valueOf(x);
        String yS = String.valueOf(y);

        List<byte[]> data = new ArrayList<>();
        data.add(0, userName.getBytes());
        data.add(1, xS.trim().getBytes());
        data.add(2, yS.trim().getBytes());
        con.send(new Frame(4, data));

        Frame frame = con.receive();
        data = frame.getData();

        int res = Integer.parseInt(new String(data.get(0)));

        return res;
    }

    /**
     * Metodo responsavel por informar ao servidor de um novo local de interesse de um utilizador,
     * bem como informar o utilizador do estado do local no momento
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o numero de pessoas no local de interesse no momento
     */
    public int adicionarLocalizacaoInteresse(Double x, Double y) throws IOException {

        String xS = String.valueOf(x);
        String yS = String.valueOf(y);

        List<byte[]> data = new ArrayList<>();
        data.add(0, userName.getBytes());
        data.add(1, xS.trim().getBytes());
        data.add(2, yS.trim().getBytes());
        con.send(new Frame(7, data));

        Frame frame = con.receive();
        data = frame.getData();

        int res = Integer.parseInt(new String(data.get(0)));

        return res;
    }

    /**
     * Metodo responsavel por informar o servidor que o utilizador se encontra doente
     * Encerra o thread responsavel por verificar se esteve em contacto com alguem e o
     * thread responsavel por verificar se as localizações de interesse estão vazias
     */
    public void comunicarDoenca() throws IOException {

        List<byte[]> data = new ArrayList<>();
        data.add(0, userName.getBytes());
        con.send(new Frame(6, data));

        this.doente = true;

        this.conGetLocais.close();
        this.conGetContacto.close();
    }

    /**
     * Metodo responsavel por pedir ao servidor o mapa desenhado até ao momento
     * @return uma exceção dizendo que não está implementado para este tipo de cliente
     */
    public Map<Localizacao, List<Integer>> getMapa() throws IOException {
        throw new NullPointerException("Impossivel este cliente realizar esta operação");
    }

    /**
     * Metodo responsavel por veificar se o utilizador não esta doente
     * @return a informação sobre o estado se saude do cliente
     */
    public boolean naoEstaDoente() {
        return !this.doente;
    }

    /**
     * Metodo responsavel por pedir ao servidor o estado das localizacoes de interesse de um cliente
     * @return estado das localizacoes de interesse do cliente
     */
    public Map<Localizacao, Boolean> getDispLocaisInteresse() throws IOException {

        Map<Localizacao, Boolean> res = new HashMap<>();

        List<byte[]> data = new ArrayList<>();
        data.add(0, userName.getBytes());
        con.send(new Frame(8, data));

        Frame frame = con.receive();
        data = frame.getData();

        int tam = Integer.parseInt(new String(data.get(0)));
        int num = 1;

        for (int i=0; i<tam; i++){
            Double x = Double.parseDouble(new String(data.get(num)));
            num++;
            Double y = Double.parseDouble(new String(data.get(num)));
            num++;
            Localizacao l = new Localizacao(x, y);

            boolean disp = Boolean.parseBoolean(new String(data.get(num)));
            num++;

            res.put(l.clone(),disp);
        }

        return res;
    }

    /**
     * Metodo responsavel por partilhar o lock do cliente
     * @return o lock do cliente
     */
    public ReentrantLock getLock(){
        return this.l;
    }

    /**
     * Metodo responsavel por informar se é necessario definir a localizacao
     * @return se é preciso defenir a localizacao ou não.
     */
    public boolean necessarioDefinirLocalizacao(){
        return this.definirLocalizacao;
    }


    public Cliente realizarLogin(String userName, String passWord) throws IOException, LoginInvalidoException {

        Cliente res = null;

        List<byte[]> data = new ArrayList<>();
        data.add(0,userName.trim().getBytes());
        data.add(1,passWord.trim().getBytes());
        con.send(new Frame(1,data));

        Frame frame = con.receive();
        data = frame.getData();

        boolean sucesso = Boolean.parseBoolean(new String(data.get(0)));

        if(sucesso){
            boolean especial = Boolean.parseBoolean(new String(data.get(1)));
            boolean doente = Boolean.parseBoolean(new String(data.get(2)));

            if(especial){
                res = new ClientePlus(userName,doente,this.con);
            }else{
                res = new Cliente(userName,doente,this.con);
            }
        }else{
            String erro = new String(data.get(1));
            throw new LoginInvalidoException(erro);
        }

        return res;
    }

    /**
     * Metodo responsavel por criar uma conta. Comunica ao servidor os dados inseridos
     * de modo a que o servidor guarde o novo utilizador
     */
    public Cliente criarConta(String username, String nome, String pass, String especial) throws IOException, LoginInvalidoException {

        Cliente res = null;

        List<byte[]> data = new ArrayList<>();
        data.add(0, nome.trim().getBytes());
        data.add(1, username.trim().getBytes());
        data.add(2, pass.trim().getBytes());
        data.add(3, especial.trim().getBytes());
        con.send(new Frame(2, data));

        Frame frame = con.receive();
        data = frame.getData();

        boolean sucesso = Boolean.parseBoolean(new String(data.get(0)));

        if (sucesso) {
            System.out.println("Conta criada com sucesso");
            boolean esp = especial.compareToIgnoreCase("S") == 0;

            if(esp){
                res = new ClientePlus(username,false, this.con);
            }else{
                res = new Cliente(username,false, this.con);
            }
        } else {
            String erro = new String(data.get(1));
            throw new LoginInvalidoException("Erro ao criar a conta: " + erro);
        }

        return res;
    }

    /**
     * Metodo responsavel por devolver o username do utilizador
     * @return o username do utilizador
     */
    public String getUsername(){
        return this.userName;
    }

    /**
     * Metodo responsavel por devolver a conecao do utilizador com o servidor
     * @return a conecao com o servidor
     */
    public Coneccao getConeccao(){
        return this.con;
    }

    /**
     * Metodo responsavel por fechar as conecçoes responsaveis por obter dados como
     * se esteve em contacto ou se os locais escolhidos se encontram livres
     */
    public void close() throws Exception {
        this.con.close();
        if(!doente){
            this.conGetLocais.close();
            this.conGetContacto.close();
        }
    }
}
