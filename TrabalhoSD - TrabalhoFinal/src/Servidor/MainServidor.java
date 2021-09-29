package Servidor;

import Cliente.Coneccao;
import Cliente.Frame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainServidor {
    public static void main(String[] args) throws Exception {
        Servidor server = new Servidor();
        ServerSocket ss1 = new ServerSocket(12345);

        Runnable worker1 = () -> {
            while(true) {
                try {
                    Socket s = ss1.accept();
                    Coneccao c = new Coneccao(s);

                    Runnable worker = () -> {
                        try (c) {
                            for (;;) {
                                Frame f = c.receive();
                                switch (f.getTipo()){
                                    case 1 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        String pass = new String(data.get(1));
                                        c.send(server.realizaLogin(username,pass));
                                        break;
                                    }
                                    case 2 :{
                                        List<byte[]> data = f.getData();
                                        String nome = new String(data.get(0));
                                        String username = new String(data.get(1));
                                        String pass = new String(data.get(2));
                                        boolean especial = (new String(data.get(3))).compareToIgnoreCase("S")==0;
                                        c.send(server.criaConta(username,nome,pass,especial));
                                        break;
                                    }
                                    case 3 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        Double x = Double.parseDouble(new String(data.get(1)));
                                        Double y = Double.parseDouble(new String(data.get(2)));

                                        c.send(server.setLocalizacao(username,x,y));
                                        break;
                                    }
                                    case 4 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        Double x = Double.parseDouble(new String(data.get(1)));
                                        Double y = Double.parseDouble(new String(data.get(2)));

                                        c.send(server.getNumPessoasLocalizacao(username,x,y));
                                        break;
                                    }
                                    case 5 :{
                                        c.send(server.getMapa());
                                        break;
                                    }
                                    case 6 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        server.setDoente(username);
                                        break;
                                    }
                                    case 7 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        Double x = Double.parseDouble(new String(data.get(1)));
                                        Double y = Double.parseDouble(new String(data.get(2)));

                                        c.send(server.adicionaLocalInteresse(username,x,y));
                                        break;
                                    }
                                    case 8 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        c.send(server.getDispLocaisInteresse(username));
                                        break;
                                    }
                                    case 10 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        c.send(server.esteveContacto(username));
                                        break;
                                    }
                                    case 11 :{
                                        List<byte[]> data = f.getData();
                                        String username = new String(data.get(0));
                                        c.send(server.posicoesLivres(username));
                                        break;
                                    }
                                    default: break;
                                }
                            }
                        } catch (IOException ignored) { /*System.out.println("Fechou conecção");*/ }
                        catch (Exception e) { e.printStackTrace(); }
                    };

                    new Thread(worker).start();

                }catch (IOException ignored) { }
            }
        };

        Thread t1 = new Thread(worker1);
        t1.start();
    }
}
