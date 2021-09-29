package Cliente;

import java.util.List;

public class Frame {

    private final int tipo;
    private final List<byte[]> data;

    public Frame(int tipo, List<byte[]> data) {
        this.tipo = tipo;
        this.data = data;
    }

    public int getTipo() {
        return tipo;
    }

    public List<byte[]> getData() {
        return data;
    }
}
