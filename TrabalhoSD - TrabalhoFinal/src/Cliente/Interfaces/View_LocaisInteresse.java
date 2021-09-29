package Cliente.Interfaces;

import Servidor.Localizacao;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class View_LocaisInteresse {
    /**
     * Varíavel de classe para suportar leitura
     */
    private static Scanner input = new Scanner(System.in);

    /**
     * Opção selecionada
     */
    private String selectedOption;

    /**
     * Constructor for objects of class Menu
     */
    public View_LocaisInteresse() {
        this.selectedOption = null;
    }

    /**
     * Método para apresentar o menu e ler uma opção.
     *
     */
    public void executa(Map<Localizacao,Boolean> d, int inicio, int salto) {
        do {
            showLocalizacoes(d, inicio, salto);
            this.selectedOption = readOption();
        } while (this.selectedOption == null);
    }

    /**
     * Método que apresenta o menu sob a forma de páginas.
     */
    private void showLocalizacoes(Map<Localizacao,Boolean> d, int inicio, int salto) {
        int i = 0;
        int fim = inicio + salto;

        for (Map.Entry<Localizacao, Boolean> m : d.entrySet()) {
            if (i >= inicio && i < fim) {
                String estado = (m.getValue()) ? "Vazia" : "Ocupada";
                System.out.println("Localizacao: " + m.getKey().toString() +
                        " | " +
                        "Estado: " + estado);
            }
            i++;
        }
        System.out.println("(0) Sair | (+) Avançar página | (-) Retroceder página");
    }

    /**
     * Método responsável por ler uma opção válida.
     *
     * @return Opção escolhida.
     */
    private String readOption() {

        String option = null;

        System.out.print("Opção: ");
        try {
            option = input.next().trim();

            if (option.compareToIgnoreCase("0") != 0 && !option.equals("+") && !option.equals("-")) {
                System.out.print("     -> Opção Inválida!!! \n");
                option = null;
            }
        } catch (InputMismatchException e) {
            option = null;
            System.out.println(e.toString());
        }
        return option;
    }

    /**
     * Método para obter a última opção lida
     */
    public String getLastOption() {
        return this.selectedOption;
    }
}
