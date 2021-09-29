package Cliente.Excecoes;

import Cliente.Cliente;

public class LoginInvalidoException extends Exception {
    /**
     * Construtor por omissão para objetos da classe LoginInvalidoException.
     */
    public LoginInvalidoException() {
        super();
    }

    /**
     * Construtor parametrizado para objetos da classe LoginInvalidoException.
     *
     * @param erro Mensagem de erro.
     */
    public LoginInvalidoException(String erro) {
        super(erro);
    }
}
