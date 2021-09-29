package Cliente;

import Cliente.Excecoes.LoginInvalidoException;
import Servidor.Localizacao;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public interface ICliente {

    /**
     * Metodo responsavel por comunicar com o servidor para verificar se os
     * dados inseridos no login estao corretos
     * @param userName username inseridos
     * @param passWord password inserida
     */
    public Cliente realizarLogin(String userName, String passWord) throws IOException,LoginInvalidoException;

    /**
     * Metodo responsavel por criar uma conta. Comunica ao servidor os dados inseridos
     * de modo a que o servidor guarde o novo utilizador
     */
    Cliente criarConta(String username, String nome, String pass, String especial) throws IOException, LoginInvalidoException;

    /**
     * Metodo responsavel por enviar ao servidor a informação da nova localizacao do utilizador
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o resulrado do cotacto com o servidor
     */
    boolean setLocalizacao(Double x, Double y) throws IOException;

    /**
     * Metodo responsavel por pedir ao servidor o numero de pessoas num a localizacao
     * e informar o cliente
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o numero de pessoas numa localizacao (-1 em caso de erro ao contactar o servidor)
     */
    int getPessoasLocalizacao(Double x, Double y) throws IOException;

    /**
     * Metodo responsavel por informar ao servidor de um novo local de interesse de um utilizador,
     * bem como informar o utilizador do estado do local no momento
     * @param x cordenada em x da localizacao
     * @param y cordenada em y da localizacao
     * @return o numero de pessoas no local de interesse no momento
     */
    int adicionarLocalizacaoInteresse(Double x, Double y) throws IOException;

    /**
     * Metodo responsavel por informar o servidor que o utilizador se encontra doente
     * Encerra o thread responsavel por verificar se esteve em contacto com alguem e o
     * thread responsavel por verificar se as localizações de interesse estão vazias
     */
    void comunicarDoenca() throws IOException;

    /**
     * Metodo responsavel por pedir ao servidor o mapa desenhado até ao momento
     * @return uma exceção dizendo que não está implementado para este tipo de cliente
     */
    Map<Localizacao, List<Integer>> getMapa() throws IOException;

    /**
     * Metodo responsavel por veificar se o utilizador não esta doente
     * @return a informação sobre o estado se saude do cliente
     */
    boolean naoEstaDoente();

    /**
     * Metodo responsavel por pedir ao servidor o estado das localizacoes de interesse de um cliente
     * @return estado das localizacoes de interesse do cliente
     */
    Map<Localizacao, Boolean> getDispLocaisInteresse() throws IOException;

    /**
     * Metodo responsavel por partilhar o lock do cliente
     * @return o lock do cliente
     */
    ReentrantLock getLock();

    /**
     * Metodo responsavel por fechar as conecçoes responsaveis por obter dados como
     * se esteve em contacto ou se os locais escolhidos se encontram livres
     */
    void close() throws Exception;

    /**
     * Metodo responsavel por informar se é necessario definir a localizacao
     * @return se é preciso defenir a localizacao ou não.
     */
    boolean necessarioDefinirLocalizacao();

    /**
     * Metodo responsavel por devolver o username do utilizador
     * @return o username do utilizador
     */
    String getUsername();
}
