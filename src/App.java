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
    
        Map<String, String[]> grammarRules = gerarDicionario(codeSplitado);
        for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
            if (entry.getValue().length > 1) {
                System.out.print(entry.getKey() + " -> ");
                for (String aux : entry.getValue()) {
                    System.out.print(aux + " | ");
                }
                System.out.println();
            } else
            System.out.println(entry.getKey() + " -> " + entry.getValue()[0]);
        }
        gerarFirst(grammarRules);
    }

    
    
    public static void gerarFirst(Map<String, String[]> grammarRules) {
        Map<String, String[]> first = new HashMap<>();

        for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
            String[] aux = new String[entry.getValue().length];
            for (int i = 0; i < entry.getValue().length; i++) {
                System.out.println(entry.getKey());
                String[] parts = entry.getValue()[i].split(" ");
                if (parts[0].equals("epsilon")) {
                    aux[i] = "epsilon";
                } else {
                    aux[i] = parts[0];
                }
            }
            first.put(entry.getKey(), aux);
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

    public static Map<String, String[]> gerarDicionario(String[] codeSplitado) {
        Map<String, String[]> grammarRules = new HashMap<>();
        
        for (String line : codeSplitado) {
            String[] parts = line.split("->");
            if (parts[1].contains("|")) {
                String[] parts2 = parts[1].split("\\|");
                String[] aux = new String[parts2.length];
                for (int i = 0; i < parts2.length; i++) {
                    aux[i] = parts2[i].trim();
                }
                grammarRules.put(parts[0].trim(), aux);
            } else {
                String[] aux = new String[1];
                aux[0] = parts[1].trim();
                grammarRules.put(parts[0].trim(), aux);
            }
        }
        return grammarRules;
    }
    
}
