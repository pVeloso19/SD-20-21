package Cliente.Interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Esta classe implementa um menu em modo texto.
 * @version v2.3 (20201215)
 */
public class Menu {

    // Interfaces auxiliares

    ReentrantLock l ;

    /** Functional interface para handlers. */
    public interface MenuHandler {
        void execute();
    }

    /** Functional interface para pré-condições. */
    public interface MenuPreCondition {
        boolean validate();
    }

    // Varíável de classe para suportar leitura

    private static Scanner is = new Scanner(System.in);

    // Variáveis de instância

    private List<String> opcoes;                // Lista de opções
    private List<MenuPreCondition> disponivel;  // Lista de pré-condições
    private List<MenuHandler> handlers;         // Lista de handlers

    private String nome;
    // Construtor

    /**
     * Constructor for objects of class Menu
     */
    public Menu(String[] opcoes, String nome, ReentrantLock l) {
        this.opcoes = Arrays.asList(opcoes);
        this.disponivel = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.opcoes.forEach(s-> {
            this.disponivel.add(()->true);
            this.handlers.add(()->System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
        this.nome = nome;

        this.l = l;
    }

    // Métodos de instância

    /**
     * Correr o menu.
     * Termina com a opção 0 (zero).
     */
    public void run() {
        int op;
        do {

            try {
                l.lock();
                show();
            }finally {
                l.unlock();
            }

            op = readOption();
            // testar pré-condição
            if (op>0 && !this.disponivel.get(op-1).validate()) {
                System.out.println("\n************************************");
                System.out.println("Opção indisponível! Tente novamente.");
                System.out.println("************************************");
            } else if (op>0) {
                // executar handler
                this.handlers.get(op-1).execute();
            }
        } while (op != 0);
    }

    /**
     * Correr o menu ate ter uma opção valida.
     * Termina com a opção 0 (zero).
     */
    public void runUnique(MenuPreCondition p) {
        int op;
        boolean acertou = false;
        do {

            try {
                l.lock();
                show();
            }finally {
                l.unlock();
            }

            op = readOption();
            // testar pré-condição
            if (op>0 && !this.disponivel.get(op-1).validate()) {
                System.out.println("\n************************************");
                System.out.println("Opção indisponível! Tente novamente.");
                System.out.println("************************************");
            } else if (op>0) {
                // executar handler
                acertou = true;
                this.handlers.get(op-1).execute();
            }
        } while ((!acertou && op!=0) || !p.validate());
    }

    /**
     * Método que regista uma uma pré-condição numa opção do menu.
     *
     * @param i índice da opção (começa em 1)
     * @param b pré-condição a registar
     */
    public void setPreCondition(int i, MenuPreCondition b) {
        this.disponivel.set(i-1,b);
    }

    /**
     * Método para registar um handler numa opção do menu.
     *
     * @param i indice da opção  (começa em 1)
     * @param h handlers a registar
     */
    public void setHandler(int i, MenuHandler h) {
        this.handlers.set(i-1, h);
    }

    // Métodos auxiliares

    /** Apresentar o menu */
    private void show() {
        System.out.println("\n**** Menu"+nome+" *** ");
        for (int i=0; i<this.opcoes.size(); i++) {
            System.out.print(i+1);
            System.out.print(" - ");
            System.out.println(this.disponivel.get(i).validate()?this.opcoes.get(i):"---");
        }
        System.out.println("0 - Sair");
    }

    /** Ler uma opção válida */
    private int readOption() {
        int op;
        //Scanner is = new Scanner(System.in);

        System.out.print("Opção: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        if (op<0 || op>this.opcoes.size()) {
            System.out.println("\n*****************");
            System.out.println("Opção Inválida!!!");
            System.out.println("*****************");
            op = -1;
        }
        return op;
    }
}
