package Cliente;

import Servidor.Localizacao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientePlus extends Cliente {

    public ClientePlus(String userName, boolean doente, Coneccao c) throws IOException {
        super(userName,doente, c);
    }

    /**
     * Metodo responsavel por pedir ao servidor o mapa desenhado até ao momento
     * @return o mapa desenhado até ao momento (null em caso de erro ao contactar o servidor)
     */
    public Map<Localizacao, List<Integer>> getMapa() throws IOException {
        Coneccao con = super.getConeccao();
        con.send(new Frame(5, new ArrayList<>()));

        Frame frame = con.receive();
        List<byte[]> data = frame.getData();

        Map<Localizacao, List<Integer>> res = new HashMap<>();
        int tam = Integer.parseInt(new String(data.get(0)));
        int num = 1;
        for (int i = 0; i < tam; i++) {
            Double x = Double.parseDouble(new String(data.get(num)));
            num++;
            Double y = Double.parseDouble(new String(data.get(num)));
            num++;
            Localizacao l = new Localizacao(x, y);

            int ut = Integer.parseInt(new String(data.get(num)));
            num++;
            int doente = Integer.parseInt(new String(data.get(num)));
            num++;

            List<Integer> temp = new ArrayList<>(2);
            temp.add(0, ut);
            temp.add(1, doente);

            res.put(l.clone(), new ArrayList<>(temp));
        }
        return res;
    }
}
