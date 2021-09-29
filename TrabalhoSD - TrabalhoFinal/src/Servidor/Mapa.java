package Servidor;

import java.util.*;

public class Mapa {

    private Map<Localizacao, List<Utilizador>> mapa;

    public Mapa() {
        this.mapa = new HashMap<>();
    }

    /**
     * Metodo responsavel por adicionar a presença de um  utilizador numa dada localizacao
     * @param l localização do utilizador
     * @param u utilizador
     */
    public void adicionaPessoa(Localizacao l, Utilizador u) {
        List<Utilizador> lista = this.mapa.get(l);

        if(lista==null){
            lista = new ArrayList<>();
        }

        boolean contem = lista.stream().anyMatch(ut->ut.getUserName().equals(u.getUserName()));
        if(!contem)
            lista.add(u);

        this.mapa.put(l,lista);
    }

    /**
     * Metodo responsavel por verificar se uma determinada localizacao esta vazia no momento
     * @param l localizacao a verificar
     * @return um boolean com o facto da posicao estar vazia ou nao
     */
    public boolean posicaoVazia(Localizacao l) {
        List<Utilizador> lista = this.mapa.get(l);
        boolean res = true;
        if(lista!=null){
            Iterator<Utilizador> it = lista.iterator();
            while (it.hasNext() && res){
                Utilizador u = it.next();
                res = !u.getLocalAtual().equals(l);
            }
        }
        return res;
    }

    /**
     * Metodo responsavel por retornar o mapa numa lista de bytes
     * @return o estado do mapa atual em formato de bytes
     */
    public List<byte[]> getMapa() {
        int tam = this.mapa.size();
        List<byte[]> data = new ArrayList<>();
        data.add(0, String.valueOf(tam).getBytes());

        for (Map.Entry<Localizacao,List<Utilizador>> e : this.mapa.entrySet()){
            Localizacao l = e.getKey();
            data.add(String.valueOf(l.getX()).getBytes());
            data.add(String.valueOf(l.getY()).getBytes());

            List<Utilizador> lista = e.getValue();
            int ut = 0;
            int doente = 0;

            for(Utilizador u : lista){
                if(u.isDoente())
                    doente++;
                else
                    ut++;
            }

            data.add(String.valueOf(ut).getBytes());
            data.add(String.valueOf(doente).getBytes());
        }
        return data;
    }

    /**
     * Metodo responsavel por repesentar o mapa em formato string
     * @return o estado do mapa atual em formato string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Localizacao,List<Utilizador>> e : this.mapa.entrySet()){
            Localizacao l = e.getKey();
            sb.append("Localizacao: (").append(l.getX()).append(",").append(l.getY()).append(") -> [");
            List<Utilizador> lista = e.getValue();
            for(Utilizador u : lista){
                sb.append(u.getUserName());
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
