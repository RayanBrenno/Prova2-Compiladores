public class Tokenizer {
    String lexema;
    String token;
    String valor;

    public Tokenizer(String lexema, String token, String valor) {
        this.lexema = lexema;
        this.token = token;
        this.valor = valor;
    }

    public String getLexema() {
        return lexema;
    }

    public String getToken() {
        return token;
    }

    public String getValor() {
        return valor;
    }

}
