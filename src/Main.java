import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {  
    public static void main(String[] args) throws Exception {
        File file = new File("src/glc.txt");
        String code = lerTXT(file.getAbsolutePath());
        String[] codeSplitado = code.split("\n");
        Map<String, String[]> grammarRules = gerarDicionario(codeSplitado);

        Map<String, Set<String>> first = gerarFirst(grammarRules);
        for (Map.Entry<String, Set<String>> entry : first.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("\n");
        Map<String, Set<String>> follow = gerarFollow(grammarRules, first);
        for (Map.Entry<String, Set<String>> entry : follow.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        
        String[][] tabela = gerarTabela(grammarRules, first, follow);

    }

    public static Map<String, Set<String>> gerarFirst(Map<String, String[]> grammarRules) {
        Map<String, Set<String>> first = new LinkedHashMap<>();
        do {
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();

                if (first.containsKey(key)) {
                    continue; // Pula se o elemento já foi processado
                }

                String[] values = entry.getValue();
                Set<String> aux = new HashSet<>();
                boolean flag = true;
                for (String value : values) {
                    if (grammarRules.containsKey(String.valueOf(value.charAt(0)))) {
                        String aux2 = String.valueOf(value.charAt(0));
                        if (value.length() > 1 && grammarRules.containsKey(aux2 + String.valueOf(value.charAt(1)))) {
                            aux2 = aux2 + "'";
                        }

                        if (first.containsKey(aux2)) {
                            aux.addAll(first.get(aux2));
                        } else {
                            flag = false;
                            break;
                        }
                    } else {
                        if (value.equals("id")) {
                            aux.add("id");
                        } else
                            aux.add(String.valueOf(value.charAt(0)));
                    }

                }
                if (flag) {
                    first.put(key, aux);
                }
            }
        } while (first.size() != grammarRules.size());
        return first;
    }

    public static Map<String, Set<String>> gerarFollow(Map<String, String[]> grammarRules,
            Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new LinkedHashMap<>();
        do {
            boolean toPutFinalCaracter = true;
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();
                if (follow.containsKey(key)) {
                    continue; // Pula se o elemento já foi processado
                }
                Set<String> aux = new HashSet<>();
                if (toPutFinalCaracter) {
                    aux.add("$");
                    toPutFinalCaracter = false;
                }
                boolean flag = true;

                for (Map.Entry<String, String[]> entry2 : grammarRules.entrySet()) {
                    String key2 = entry2.getKey();
                    String[] values2 = entry2.getValue();
                    boolean flag2 = true;
                    for (String value : values2) {
                        // se nao tem o key na produção pula
                        if (!value.contains(key)) {
                            continue;
                        }

                        Integer index = value.lastIndexOf(key);
                        if (key.length() == 1 && String.valueOf(value.charAt(index + 1)).equals("'") ) {
                            continue;
                        }
                        if (key.length() > 1){
                            index++;
                        }

                        if (value.length() > index + 1) {
                            String nextElement = String.valueOf(value.charAt(index + 1));
                            if (grammarRules.containsKey(nextElement)) {
                                if (value.length() > index + 2 && grammarRules.containsKey(nextElement + "'")) {
                                    nextElement += "'";
                                }
                                // Se houver uma produção 𝐴 → 𝛼𝐵𝛽 , então tudo em 𝐹𝐼𝑅𝑆𝑇(𝛽) exceto 𝜺 está em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                                aux.addAll(first.get(nextElement));

                                // Regra 3 : Se houver uma produção 𝐴 → 𝛼𝐵, ou 𝐴 → 𝛼𝐵𝛽, onde o 𝐹𝐼𝑅𝑆𝑇(𝛽) possui 𝜺, então inclua 𝐹𝑂𝐿𝐿𝑂𝑊(𝐴) em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                                if (first.get(nextElement).contains("#")) {
                                    if (follow.containsKey(key2)) {
                                        aux.addAll(follow.get(key2));
                                    } else {
                                        flag2 = false;
                                        break;
                                    }
                                }
                            } else {
                                aux.add(nextElement);
                            }
                        } else {
                            // Regra 3 : Se houver uma produção 𝐴 → 𝛼𝐵, ou 𝐴 → 𝛼𝐵𝛽, onde o 𝐹𝐼𝑅𝑆𝑇(𝛽) possui 𝜺, então inclua 𝐹𝑂𝐿𝐿𝑂𝑊(𝐴) em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                            if (!key.equals(key2)) {
                                if (follow.containsKey(key2)) {
                                    aux.addAll(follow.get(key2));
                                } else {
                                    flag2 = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (!flag2) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    if (aux.contains("#")) {
                        aux.remove("#");
                    }
                    follow.put(key, aux);
                }
            }
        } while (follow.size() != grammarRules.size());
        return follow;
    }

    public static String[][] gerarTabela(Map<String, String[]> grammarRules, Map<String, Set<String>> first, Map<String, Set<String>> follow) {
        
        Set<String> simbolosDeEntrada = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : first.entrySet()) 
            simbolosDeEntrada.addAll(entry.getValue());
        for (Map.Entry<String, Set<String>> entry : follow.entrySet()) 
            simbolosDeEntrada.addAll(entry.getValue());
        simbolosDeEntrada.remove("#");
        System.out.println(simbolosDeEntrada);

        String[][] tabela = new String[grammarRules.size()][simbolosDeEntrada.size()];

        return tabela;
    }   

    public static String lerTXT(String caminho) {
        try {
            return new String(Files.readAllBytes(Paths.get(caminho)));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return "";
        }
    }

    public static Map<String, String[]> gerarDicionario(String[] codeSplitado) {
        Map<String, String[]> grammarRules = new LinkedHashMap<>();

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

    public static int encontrarIndice(String[] array, String simbolo) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(simbolo)) {
                return i; 
            }
        }
        return -1; 
    }

}
