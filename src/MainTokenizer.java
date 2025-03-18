import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainTokenizer {

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

    public static void main(String[] args) {

        File file = new File("src/codigo.txt");
        String code = lerTXT(file.getAbsolutePath());
        System.out.println(code);
        Map<String, Tokenizer> tabelaDeSimbolos = new LinkedHashMap<>();
        ArrayList<String> codeSeparado = Passos.separarTexto(code, tabelaDeSimbolos);

        // Exibir codeSeparado
        System.out.println("Code Separado:");
        System.out.println(codeSeparado);

        System.out.println("\nTabela de Simbolos:");
        for (String current : tabelaDeSimbolos.keySet()) {
            System.out.println(current + " " + tabelaDeSimbolos.get(current).getLexema());
        }

        System.out.println("\nCode Tokenizado:");
        String[] codeTokenizado = Passos.tokenizarTexto(tabelaDeSimbolos, codeSeparado);
        for (int i = 0; i < codeTokenizado.length; i++) {
            System.out.println((i) + " " + codeSeparado.get(i) + " -> " + codeTokenizado[i]);
        }

        System.out.println("\n" + Arrays.toString(codeTokenizado));

    }
}
