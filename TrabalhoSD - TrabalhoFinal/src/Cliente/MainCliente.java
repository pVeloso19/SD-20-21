package Cliente;

import Cliente.Interfaces.TextUI;

public class MainCliente {
    public static void main(String[] args) {
        try {
            new TextUI().run();
        }
        catch (Exception e) {
            System.out.println("Não foi possível arrancar: "+e.getMessage());
        }
        System.exit(0);
    }
}
