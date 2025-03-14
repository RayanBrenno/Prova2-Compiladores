import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class App {
    public static void main(String[] args) throws Exception {
        File file = new File("src/codigo.txt");
        String code = lerTXT(file.getAbsolutePath());
        String[] codeSplitado = code.split("\n");

    
        Map<String, String> grammarRules = gerarDicionario(codeSplitado);
        for (Map.Entry<String, String> entry : grammarRules.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static Map<String, String> gerarDicionario(String[] codeSplitado) {
        Map<String, String> grammarRules = new HashMap<>();
        
        for (String line : codeSplitado) {
            if (line.contains("->")) {
                String[] parts = line.split("->");
                String key = parts[0].trim();
                String value = parts[1].trim();
                grammarRules.put(key, value);
            }
        }
        
        return grammarRules;
    }
    
    public static void gerarFirst(String[] codeSplitado) {
        for (String aux : codeSplitado) {
            System.out.println(aux);
        }
    }
    
    public static String lerTXT(String caminho) {
        try {
            return Files.readString(Paths.get(caminho));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return "";
        }
    }
    
}
