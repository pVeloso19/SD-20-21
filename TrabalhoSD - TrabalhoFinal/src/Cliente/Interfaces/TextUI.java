package Cliente.Interfaces;

import Cliente.Cliente;
import Cliente.ClientePlus;
import Cliente.Excecoes.LoginInvalidoException;
import Cliente.ICliente;
import Servidor.Localizacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que implementa uma User Interface em modo texto.
 *
 * @author Carlos Preto (a89587)
 * @author Maria João Moreira (a89540)
 * @author Pedro Veloso (a89557)
 * @author Rui Fernandes (a89138)
 */
public class TextUI {
    /**
     * Model, que tem a 'lógica de negócio'.
     */
    private ICliente model;

    /**
     * Scanner para leitura.
     */
    private Scanner scin;

    /**
     * BufferedReader para leitura.
     */
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Lock do cliente
     */
    private ReentrantLock l;

    private boolean login;

    /**
     * Construtor para objetos da classe TextUI. Cria os menus e a camada de negócio.
     */
    public TextUI() {
        scin = new Scanner(System.in);
        login = false;
    }

    /**
     * Método que executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        System.out.println("Bem vindo ao Alarme Covid!");
        this.menuPrincipal();
        System.out.println("\nAté breve...");
    }

    // Métodos auxiliares - Estados da UI

    /**
     * Estado - Menu Principal
     */
    private void menuPrincipal() {
        try {
            this.model = new Cliente();

            Menu menu = new Menu(new String[]{
                    "Login",
                    "Criar Conta"
            }, "",new ReentrantLock());

            // Registar os handlers
            menu.setHandler(1, () -> login());
            menu.setHandler(2, () -> criarConta());

            menu.runUnique(()->login);

        } catch (IOException ignored) {
            System.out.println("\n********************************");
            System.out.println("Impossivel conectar ao servidor.");
            System.out.println("Tente mais tarde.");
            System.out.println("********************************\n");
        }
    }

    /**
     * Metodo que realiza o login.
     */
    private void login() {
        boolean entrou = false;
        try {
            while(!entrou){
                System.out.print("Insira o seu UserName: ");
                String username = scin.next();
                System.out.print("Insira a sua password: ");
                String pass = scin.next();

                this.model = this.model.realizarLogin(username, pass);
                if (this.model != null){
                    login = true;
                    this.defineViewCliente();
                    entrou = true;
                }
            }
        }catch (LoginInvalidoException erro) {
            System.out.println("\n*******************");
            System.out.println(erro.getMessage());
            System.out.println("*******************\n");
        } catch (IOException ignored) {
            System.out.println("\n********************************");
            System.out.println("Impossivel conectar ao servidor.");
            System.out.println("Tente mais tarde.");
            System.out.println("********************************\n");
        }
    }

    /**
     * Metodo responsavel por criar uma conta. Comunica ao servidor os dados inseridos
     * de modo a que o servidor guarde o novo utilizador
     */
    private void criarConta() {
        boolean entrou = false;
        while(!entrou){
            try {

                System.out.print("Insira o seu UserName: ");
                String username = scin.next();
                System.out.print("Insira o seu nome completo: ");
                String nome = in.readLine();
                System.out.print("Insira a sua password: ");
                String pass = scin.next();
                System.out.print("É um utilizador especial (S/N): ");
                String especial = scin.next();

                this.model = this.model.criarConta(username, nome, pass, especial);
                if (this.model != null){
                    login = true;
                    this.defineViewCliente();
                    entrou = true;
                }
            } catch (LoginInvalidoException erro) {
                System.out.println("\n*******************");
                System.out.println(erro.getMessage());
                System.out.println("*******************\n");
            } catch (IOException ignored) {
                System.out.println("\n********************************");
                System.out.println("Impossivel conectar ao servidor.");
                System.out.println("Tente mais tarde.");
                System.out.println("********************************\n");
                entrou = true;
            }
        }
    }

    /**
     * Metodo por verificar se o cliente é especial ou nao. Cada tipo de utilizador tem um
     * menu diferente
     */
    private void defineViewCliente() {
        if(this.model instanceof ClientePlus){
            this.viewClientePlus();
        }else{
            this.viewCliente();
        }
    }

    /**
     * Metodo que cria o menu do cliente normal
     */
    private void viewCliente(){
        this.l = this.model.getLock();
        String nome = " - Username: "+this.model.getUsername();
        Menu menu = new Menu(new String[]{
                "Definir localização",
                "Número de pessoas numa localização",
                "Adicionar local de interesse",
                "Comunicar doença",
                "Meus locais"
        }, nome,this.l);

        //Pré-Condições
        menu.setPreCondition(1, () -> this.model.naoEstaDoente());
        menu.setPreCondition(2, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(3, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(4, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(5, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());

        // Registar os handlers
        menu.setHandler(1, () -> setLocalizacao());
        menu.setHandler(2, () -> getPessoasLocalizacao());
        menu.setHandler(3, () -> defenirLocalInteresse());
        menu.setHandler(4, () -> comunicarDoença());
        menu.setHandler(5, () -> showLocaisInteresse());

        menu.run();
        try {
            this.model.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que cria o menu do cliente especial
     */
    private void viewClientePlus(){
        this.l = this.model.getLock();
        String nome = " - Username: "+this.model.getUsername();
        Menu menu = new Menu(new String[]{
                "Definir localização",
                "Número de pessoas numa localização",
                "Adicionar local de interesse",
                "Comunicar doença",
                "Descarregar mapa",
                "Meus locais"
        }, nome,this.l);

        //Pré-Condições
        menu.setPreCondition(1, () -> this.model.naoEstaDoente());
        menu.setPreCondition(2, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(3, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(4, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(5, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());
        menu.setPreCondition(6, () -> this.model.naoEstaDoente() && !this.model.necessarioDefinirLocalizacao());

        // Registar os handlers
        menu.setHandler(1, () -> setLocalizacao());
        menu.setHandler(2, () -> getPessoasLocalizacao());
        menu.setHandler(3, () -> defenirLocalInteresse());
        menu.setHandler(4, () -> comunicarDoença());
        menu.setHandler(5, () -> descarregarMapa());
        menu.setHandler(6, () -> showLocaisInteresse());

        menu.run();
        try {
            this.model.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodos responsaveis por pedir ao model informaçoes a mostrar ao utilizador

    private void setLocalizacao(){
        try {
            l.lock();
            try {
                System.out.println("Insira a nova localização (x y)");
                String input = in.readLine();
                String[] local = input.split(" ");
                if (local.length == 2) {
                    try {
                        Double x = Double.parseDouble(local[0].trim());
                        Double y = Double.parseDouble(local[1].trim());

                        boolean sucesso = this.model.setLocalizacao(x, y);

                        System.out.println("\n**********************************");
                        if (sucesso)
                            System.out.println("Localização definida com sucesso");
                        else
                            System.out.println("Erro ao definir a sua localização.\nTente de novo mais tarde.");
                        System.out.println("**********************************");

                    } catch (NumberFormatException e) {
                        System.out.println("\n*************************************");
                        System.out.println("Formato numerico inserido é invalido.");
                        System.out.println("*************************************");
                    }
                } else {
                    System.out.println("\n*************************");
                    System.out.println("Dados inseridos invalidos");
                    System.out.println("*************************");
                }
            } catch (IOException e) {
                System.out.println("\n*****************************************************");
                System.out.println("Erro ao definir a nova localização: " + e.getMessage());
                System.out.println("*****************************************************");
            }
        }finally {
            l.unlock();
        }
    }

    private void getPessoasLocalizacao(){
        try {
            l.lock();
            try {
                System.out.println("Insira a localização (x y) a procurar.");
                String input = in.readLine();
                String[] local = input.split(" ");
                if (local.length == 2) {
                    try {
                        Double x = Double.parseDouble(local[0].trim());
                        Double y = Double.parseDouble(local[1].trim());

                        int numPessoas = this.model.getPessoasLocalizacao(x, y);

                        System.out.println("\n*****************************************");
                        System.out.println("Na localização (" + x + "," + y + ") tem " + numPessoas + " pessoas.");
                        System.out.println("*******************************************");

                    } catch (NumberFormatException e) {
                        System.out.println("\n*************************************");
                        System.out.println("Formato numerico inserido é invalido.");
                        System.out.println("*************************************");
                    }
                } else {
                    System.out.println("\n*************************");
                    System.out.println("Dados inseridos invalidos");
                    System.out.println("*************************");
                }
            } catch (IOException e) {
                System.out.println("\n*****************************************************");
                System.out.println("Erro na conecção com o servidor: " + e.getMessage());
                System.out.println("*****************************************************");
            }
        }finally {
            l.unlock();
        }
    }

    private void defenirLocalInteresse(){
        try {
            l.lock();
            try {
                System.out.println("Insira a localização (x y) do local de interesse.");
                String input = in.readLine();
                String[] local = input.split(" ");
                if (local.length == 2) {
                    try {
                        Double x = Double.parseDouble(local[0].trim());
                        Double y = Double.parseDouble(local[1].trim());

                        int total = this.model.adicionarLocalizacaoInteresse(x, y);

                        System.out.println("\n*************************************");
                        if (total == 0)
                            System.out.println("Localização está livre no momento");
                        else
                            System.out.println("Localização tem " + total + " pessoas.\nAguarde o local ficar vazio.");
                        System.out.println("*************************************");

                    } catch (NumberFormatException e) {
                        System.out.println("\n*************************************");
                        System.out.println("Formato numerico inserido é invalido.");
                        System.out.println("*************************************");
                    }
                } else {
                    System.out.println("\n*************************");
                    System.out.println("Dados inseridos invalidos");
                    System.out.println("*************************");
                }
            } catch (IOException e) {
                System.out.println("\n*****************************************************");
                System.out.println("Erro na conecção com o servidor: " + e.getMessage());
                System.out.println("*****************************************************");
            }
        }finally {
            l.unlock();
        }
    }

    private void comunicarDoença(){
        try {
            l.lock();
            try {
                this.model.comunicarDoenca();
            } catch (IOException e) {
                System.out.println("\n*****************************************************");
                System.out.println("Erro na conecção com o servidor: " + e.getMessage());
                System.out.println("*****************************************************");
            }
        }finally {
            l.unlock();
        }
    }

    private void descarregarMapa(){
        try {
            l.lock();
            Map<Localizacao, List<Integer>> mapa = null;
            try {
                mapa = this.model.getMapa();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mapa != null) {
                if (mapa.isEmpty()) {
                    System.out.println("\n*************************");
                    System.out.println("Sem localizações no mapa.");
                    System.out.println("*************************");
                } else {
                    System.out.println("\n************************ MAPA ************************\n");
                    for (Map.Entry<Localizacao, List<Integer>> e : mapa.entrySet()) {
                        Double x = e.getKey().getX();
                        Double y = e.getKey().getY();
                        int utilizadores = e.getValue().get(0);
                        int doentes = e.getValue().get(1);
                        System.out.println("Localização: (" + x + "," + y + ") -> Utilizadores: " + utilizadores + " | Doentes: " + doentes);
                    }
                    System.out.println("\n******************************************************");
                }
            }
        }finally {
            l.unlock();
        }
    }

    private void showLocaisInteresse(){
        try {
            l.lock();
            Map<Localizacao, Boolean> locais = null;
            try {
                locais = this.model.getDispLocaisInteresse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (locais != null && !locais.isEmpty()) {
                new LocaisInteresseUI(locais).run();
            } else {
                System.out.println("\n************************");
                System.out.println("Sem locais de interesse;");
                System.out.println("************************");
            }
        }finally {
            l.unlock();
        }
    }
}
