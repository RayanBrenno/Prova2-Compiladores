import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {  
    public static void main(String[] args) throws Exception {
        File file = new File("src/glcExemploSlide.txt");
        String code = lerTXT(file.getAbsolutePath());
        String[] codeSplitado = code.split("\n");

        Map<String, String[]> grammarRules = gerarDicionario(codeSplitado);
        Map<String, Set<String>> first = gerarFirst(grammarRules);
        Map<String, Set<String>> follow = gerarFollow(grammarRules, first);

        for (Map.Entry<String, Set<String>> entry : first.entrySet()) {
            System.out.println(entry.getKey() + " -> " + new TreeSet<>(entry.getValue()));
        }

        System.out.println("\n");
        for (Map.Entry<String, Set<String>> entry : follow.entrySet()) {
            System.out.println(entry.getKey() + " -> " + new TreeSet<>(entry.getValue()));
        }

        Map<String, Set<String[]>> tabela = gerarTabela(first, follow);

    }

    public static Map<String, Set<String>> gerarFirst(Map<String, String[]> grammarRules) {
        Map<String, Set<String>> first = new LinkedHashMap<>();
        for (String key : grammarRules.keySet()) {
            first.put(key, new HashSet<>());
        }
        boolean changed;

        do {
            changed = false;
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                Set<String> aux = new HashSet<>();
                // se ta foi atulizado pula
                if (first.get(key).size() != 0)
                    continue;
                boolean flag = true;

                for (String value : values) {
                    int cont = 0;
                    String parar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ+-*/=(){}[];,";
                    String firstElement = String.valueOf(value.charAt(cont++));
                    while (cont < value.length() && !parar.contains(String.valueOf(value.charAt(cont)))) {
                        firstElement += String.valueOf(value.charAt(cont++));
                    }
                    if (grammarRules.containsKey(firstElement)) {
                        if (first.get(firstElement).size() == 0) {
                            flag = false;
                            break;
                        } 
                        aux.addAll(first.get(firstElement));
                    } else {
                        aux.add(firstElement);
                    }
                }
                if (flag) {
                    first.put(key, aux);
                    changed = true;
                }
            }
        } while (changed);
        return first;
    }

    public static Map<String, Set<String>> gerarFollow(Map<String, String[]> grammarRules, Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new LinkedHashMap<>();
        for (String key : grammarRules.keySet()) {
            follow.put(key, new HashSet<>());
        }
        boolean changed;

        do {
            changed = false;
            boolean toPutFinalCaracter = true;
            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
                String key = entry.getKey();
                Set<String> aux = new HashSet<>();
                if (toPutFinalCaracter) {
                    aux.add("$");
                    toPutFinalCaracter = false;
                }
                // se ta foi atulizado pula
                if (follow.get(key).size() != 0)
                    continue;
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
                        index += key.length();

                        if (value.length() > index) {
                            String parar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ+-*/=(){}[];,";
                            String nextElement = String.valueOf(value.charAt(index++));
                            while (index < value.length() && !parar.contains(String.valueOf(value.charAt(index)))) {
                                nextElement += String.valueOf(value.charAt(index++));
                            }

                            if (grammarRules.containsKey(nextElement)) {
                                // Se houver uma produção 𝐴 → 𝛼𝐵𝛽 , então tudo em 𝐹𝐼𝑅𝑆𝑇(𝛽) exceto 𝜺 está em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                                aux.addAll(first.get(nextElement));

                                // Regra 3 : Se houver uma produção 𝐴 → 𝛼𝐵, ou 𝐴 → 𝛼𝐵𝛽, onde o 𝐹𝐼𝑅𝑆𝑇(𝛽) possui 𝜺, então inclua 𝐹𝑂𝐿𝐿𝑂𝑊(𝐴) em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                                if (first.get(nextElement).contains("#")) {
                                    if (follow.get(key2).size() == 0) {
                                        flag = false;
                                        break;
                                    } 
                                    aux.addAll(follow.get(nextElement));
                                }
                            } else {
                                aux.add(nextElement);
                            }
                        } else {
                            // Regra 3 : Se houver uma produção 𝐴 → 𝛼𝐵, ou 𝐴 → 𝛼𝐵𝛽, onde o 𝐹𝐼𝑅𝑆𝑇(𝛽) possui 𝜺, então inclua 𝐹𝑂𝐿𝐿𝑂𝑊(𝐴) em 𝐹𝑂𝐿𝐿𝑂𝑊(𝐵);
                            if (!key.equals(key2)) {
                                if (follow.get(key2).size() == 0) {
                                    flag = false;
                                    break;
                                } 
                                aux.addAll(follow.get(key2));
                            }
                        }
                    }
                    if (!flag2) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    changed = true;
                    if (aux.contains("#")) {
                        aux.remove("#");
                    }
                    follow.put(key, aux);
                }
            }
        } while (changed);
        return follow;
    }

    public static Map<String, Set<String[]>> gerarTabela(Map<String, Set<String>> first, Map<String, Set<String>> follow) {
        Map<String, Set<String[]>> tabela = new LinkedHashMap<>();
        Set<String> terminais = new HashSet<>();
        for(Map.Entry<String, Set<String>> entry : first.entrySet()) {
            for(String value : entry.getValue()) {
                if(!value.equals("#")) {
                    terminais.add(value);
                }
            }
        }
        for(Map.Entry<String, Set<String>> entry : follow.entrySet()) {
            for(String value : entry.getValue()) {
                if(!value.equals("#")) {
                    terminais.add(value);
                }
            }
        }

        
        
        System.out.println(terminais);

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

}
