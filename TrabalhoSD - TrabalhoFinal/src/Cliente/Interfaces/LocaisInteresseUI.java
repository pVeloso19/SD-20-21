package Cliente.Interfaces;

import Servidor.Localizacao;

import java.util.Map;
import java.util.Scanner;

public class LocaisInteresseUI {
    /**
     * Dados a mostrar.
     */
    private Map<Localizacao,Boolean> dadosMostrar;

    /**
     * Menu relativo a localizações.
     */
    private View_LocaisInteresse menuLocalizacoes;

    /**
     * Scanner que permite fazer a leitura.
     */
    private Scanner input;

    /**
     * Inicio.
     */
    private int inicio;

    /**
     * Salto
     */
    private int salto;

    /**
     * Construtor parametrizado para objetos da classe LocalizacoesUI.
     *
     * @param dado Dados a mostrar
     */
    public LocaisInteresseUI(Map<Localizacao,Boolean> dado) {
        this.menuLocalizacoes = new View_LocaisInteresse();
        this.dadosMostrar = dado;
        this.input = new Scanner(System.in);

        this.inicio = 0;
        this.salto = 1;
    }

    /**
     * Método responsável pela execução do menu.
     */
    public void run() {
        do {
            this.menuLocalizacoes.executa(dadosMostrar,inicio,salto);

            String opc = this.menuLocalizacoes.getLastOption();

            if(opc.equals("+")){
                if(this.inicio+this.salto < this.dadosMostrar.size()) this.inicio += this.salto;
            }

            if(opc.equals("-")){
                if(this.inicio-this.salto >= 0) this.inicio -= this.salto;
            }

        } while (this.menuLocalizacoes.getLastOption().compareToIgnoreCase("0")!=0); // A opção 0 é usada para sair do menu.
    }
}
