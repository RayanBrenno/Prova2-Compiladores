import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        File file = new File("src/codigo.txt");
        String code = lerTXT(file.getAbsolutePath());
        String[] codeSplitado = code.split("\n");
        Map<String, String[]> grammarRules = gerarDicionario(codeSplitado);

        Map<String, Set<String>> first = gerarFirst(grammarRules);
        
    }
    
    public static Map<String, Set<String>> gerarFirst(Map<String, String[]> grammarRules) {
        Map<String, Set<String>> first = new HashMap<>();
        do{
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                Set<String> aux = new HashSet<>();
                
                for (String value : values) {
                    if (grammarRules.containsKey(value.charAt(0))){

                    }






                    if (value.length() > 1 && grammarRules.containsKey(value.charAt(0) + "" + value.charAt(1))) {
                        Set<String> aux2 = first.get(value);
                        aux.addAll(aux2);
                        if (aux2.contains("epsilon")) {
                            aux.remove("epsilon");
                            aux.addAll(aux2);
                        }
                    }else{

                    }

                }
                first.put(key, aux);
            }


        }while(first.size() != grammarRules.size());
        return first;
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
