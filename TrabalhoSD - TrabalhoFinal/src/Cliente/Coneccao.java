package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Coneccao implements AutoCloseable{
    private Socket s;
    private Lock wl;
    private Lock rl;
    private DataOutputStream out;
    private DataInputStream in;


    public Coneccao(Socket socket) throws IOException {
        this.s = socket;

        this.wl = new ReentrantLock();
        this.rl = new ReentrantLock();

        this.out = new DataOutputStream(this.s.getOutputStream());
        this.in = new DataInputStream(this.s.getInputStream());
    }

    /**
     * Metodo por enviar a informação ao servidor
     * @param dataSend dados a enviar
     */
    public void send(Frame dataSend) throws IOException {
        try {
            wl.lock();
            out.writeInt(dataSend.getTipo());

            List<byte[]> data = dataSend.getData();
            out.writeInt(data.size());

            for (byte[] b : data){
                int tam = b.length;
                out.writeInt(tam);
                out.write(b);
            }
            out.flush();

        }finally {
            wl.unlock();
        }
    }

    /**
     * Metodo responsavel por informar o cliente da resposta do servidor
     * @return dados recevidos
     */
    public Frame receive() throws IOException {
        try {
            rl.lock();
            List<byte[]> res = new ArrayList<>();

            int tipo = in.readInt();
            int tam = in.readInt();

            for (int i = 0; i<tam; i++){
                int tamArray = in.readInt();
                byte[] resArray = new byte[tamArray];
                in.readFully(resArray);
                res.add(resArray);
            }
            return new Frame(tipo,res);
        }finally {
            rl.unlock();
        }
    }

    /**
     * Fecha a conecção
     */
    public void close() throws IOException {
        this.s.close();
    }
}
