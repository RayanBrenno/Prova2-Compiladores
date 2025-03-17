import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class App {
    // teste
    public static void main(String[] args) throws Exception {
        File file = new File("src/codigo.txt");
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
    }

    public static Map<String, Set<String>> gerarFirst(Map<String, String[]> grammarRules) {
        Map<String, Set<String>> first = new HashMap<>();
        do {
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();
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
        Map<String, Set<String>> follow = new HashMap<>();
        do {
            boolean toPutFinalCaracter = true;
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                // se ja tem pula
                if (follow.containsKey(entry.getKey())) {
                    continue;
                }
                String key = entry.getKey();
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
                        if (value.length() > index + 1) {
                            String nextElement = String.valueOf(value.charAt(index + 1));

                            if (grammarRules.containsKey(nextElement)) {
                                if (value.length() > index + 2 && grammarRules.containsKey(nextElement + "'")) {
                                    nextElement += "'";
                                }
                                // Regra : Se existe A → αBβ, então First(β) vai para Follow(B)
                                aux.addAll(first.get(nextElement));

                                // Regra : Se First(nextElement) contém # (vazio) , adicionamos Follow(key2) em Follow(key)
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
                            // Regra : Se B está no final de uma produção, Follow(de onde ele ta) vai para Follow(B)
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
                    follow.put(key, aux);
                }
            }
        } while (follow.size() != grammarRules.size());
        return follow;
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

}
