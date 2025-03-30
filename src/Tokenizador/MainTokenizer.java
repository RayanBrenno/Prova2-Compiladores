package Tokenizador;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainTokenizer {

    private static String entrada;
    private static ArrayList<String> entradaSplit;
    private static String[] codeTokenizado;

    public static String getEntrada() {
        return entrada;
    }

    public static void setEntrada(String code) {
        entrada = code;
    }

    public static ArrayList<String> getEntradaSplit() {
        return entradaSplit;
    }

    public static void setEntradaSplit(ArrayList<String> codeSeparado) { entradaSplit = codeSeparado; }

    public static String[] getCodeTokenizado() {
        return codeTokenizado;
    }

    public static void setCodeTokenizado(String[] codeT) {
        codeTokenizado = codeT;
    }

    public static void principal(){
        File file = new File("src/Tokenizador/codigo.txt");
        String code = lerTXT(file.getAbsolutePath());

        setEntrada(code);
        System.out.println("ENTRADA:");
        System.out.println(code+"\n");

        Map<String, Tokenizer> tabelaDeSimbolos = new LinkedHashMap<>();
        ArrayList<String> codeSeparado = Passos.separarTexto(code, tabelaDeSimbolos);
        setEntradaSplit(codeSeparado);

        // Exibir codeSeparado
        System.out.println("Code Separado:");
        System.out.println(codeSeparado);

//        System.out.println("\nTabela de Simbolos:");
//        for (String current : tabelaDeSimbolos.keySet()) {
//            System.out.println(current + " " + tabelaDeSimbolos.get(current).getLexema());
//        }

//        System.out.println("\nCode Tokenizado:");
        String[] codeTokenizado = Passos.tokenizarTexto(tabelaDeSimbolos, codeSeparado);
//        for (int i = 0; i < codeTokenizado.length; i++) {
//            System.out.println((i) + " " + codeSeparado.get(i) + " -> " + codeTokenizado[i]);
//        }

        setCodeTokenizado(codeTokenizado);
//        System.out.println("\n" + Arrays.toString(codeTokenizado));
    }

    public static String lerTXT(String caminho) {

        String texto = "";
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                texto += linha;
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return texto;
    }
}
