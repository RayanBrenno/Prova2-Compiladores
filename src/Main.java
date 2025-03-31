import Tokenizador.MainTokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
//
//    30/03
//    ALTERAÇÕES:
//
//    gerarFollow
//    pegarNextElement não precisa mais
//    linha 289 (gerarTabela)
//    MainTokenizer
//    entrada para a pilha foi alterada → números passados por cada dígito (134 → 1,3,4)
//    tratamento para entrada de char

//    31/03
//    ALTERAÇÕES:
//    agr aceita char = numero(0-10) sem aspas q eh o certo, mudou glc linha 17

    public static void main(String[] args) throws Exception {

        MainTokenizer.principal();

        File file = new File("src/glcFinal.txt");
        String code = lerTXT(file.getAbsolutePath());
        String[] codeSplitado = code.split("\n");

        Map<String, String[]> grammarRules = gerarDicionario(codeSplitado);
        Map<String, Set<String>> first = gerarFirst(grammarRules);

        System.out.println("FIRST:");
        for (Map.Entry<String, Set<String>> entry : first.entrySet()) {
            System.out.println(entry.getKey() + " -> " + new TreeSet<>(entry.getValue()));
        }

        System.out.println();

        System.out.println("FOLLOW:");
        Map<String, Set<String>> follow = gerarFollow(grammarRules, first);
        for (Map.Entry<String, Set<String>> entry : follow.entrySet()) {
            System.out.println(entry.getKey() + " -> " + new TreeSet<>(entry.getValue()));
        }

        System.out.println();

        System.out.println("Tabela Preditiva:");
        Map<String, Map<String, String>> tabela = gerarTabelaPreditiva(grammarRules, first, follow);
        imprimirTabelaPreditiva(tabela);
        // ↓
        String entrada = MainTokenizer.getEntrada();
        ArrayList<String> entradaSplit = new ArrayList<>();

        for(String item : MainTokenizer.getEntradaSplit()){
            try{
                // tratamento para número
                Integer.parseInt(item);
                String[] digitos = item.split("");
                entradaSplit.addAll(Arrays.asList(digitos));

            }catch(NumberFormatException e){
                // tratamento para char
                if(item.contains("'")){
                    String[] shar = item.split("");
                    entradaSplit.addAll(Arrays.asList(shar));
                }else{
                    entradaSplit.add(item);
                }
            }
        }
        System.out.println(entradaSplit);
        // ↑
        boolean resultado = analisarEntrada(entradaSplit, tabela, "S");
        System.out.println("\n Resultado da analise para '" + entrada + "': " + (resultado ? "ACEITA" : "REJEITADA "));

    }

    public static Map<String, String[]> gerarDicionario(String[] codeSplitado) {
        Map<String, String[]> grammarRules = new LinkedHashMap<>();
        for (String line : codeSplitado) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("->");
            if (parts.length < 2) {
                System.err.println("Erro: Linha inválida -> " + line);
                continue;
            }
            String naoTerminal = parts[0].trim();
            String[] producoes = parts[1].split("\\|");
            for (int i = 0; i < producoes.length; i++) {
                producoes[i] = producoes[i].trim();
            }
            grammarRules.put(naoTerminal, producoes);
        }
        return grammarRules;
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
                String[] productions = entry.getValue();
                Set<String> currentFirst = first.get(key);
                for (String production : productions) {
                    String firstSimbol = production.trim().split("\\s+")[0];
                    if (grammarRules.containsKey(firstSimbol)) {
                        Set<String> firstOfFirstSimbol = first.get(firstSimbol);
                        if (!firstOfFirstSimbol.isEmpty())
                            if (currentFirst.addAll(firstOfFirstSimbol)) 
                                changed = true;
                    } else {
                        if (currentFirst.add(firstSimbol)) 
                            changed = true; 
                    }
                }
            }
        } while (changed);
            return first;
    }

//    public static Map<String, Set<String>> gerarFollow(Map<String, String[]> grammarRules, Map<String, Set<String>> first) {
//        Map<String, Set<String>> follow = new LinkedHashMap<>();
//        for (String key : grammarRules.keySet()) {
//            follow.put(key, new HashSet<>());
//        }
//
//        String startSymbol = "S";
//        follow.get(startSymbol).add("$");
//
//        boolean changed;
//        do {
//            changed = false;
//            for (Map.Entry<String, String[]> entry : grammarRules.entrySet()) {
//                String key = entry.getKey();
//                for (Map.Entry<String, String[]> entry2 : grammarRules.entrySet()) {
//                    String key2 = entry2.getKey();
//                    String[] values2 = entry2.getValue();
//
//                    for (String value : values2) {
//                        String nextElement = pegarNextElement(value, key);
//
//                        if (nextElement.equals("-1"))
//                            continue;
//
//                        Set<String> toAdd = new HashSet<>();
//                        if (!nextElement.equals("")) {
//                            if (grammarRules.containsKey(nextElement)) {
//                                Set<String> firstNext = new HashSet<>(first.get(nextElement));
//                                if (firstNext.contains("#")) {
//                                    firstNext.remove("#");
//                                    toAdd.addAll(firstNext);
//                                    toAdd.addAll(follow.get(key2)); // Regra 3
//                                } else {
//                                    toAdd.addAll(firstNext); // Regra 2
//                                }
//                            } else {
//                                toAdd.add(nextElement); // terminal direto
//                            }
//                        } else {
//                            if (!key.equals(key2)) {
//                                toAdd.addAll(follow.get(key2)); // Regra 3 (fim da produção)
//                            }
//                        }
//                        if (follow.get(key).addAll(toAdd)) {
//                            changed = true;
//                        }
//                    }
//                }
//            }
//        } while (changed);
//        return follow;
//    }

    public static Map<String, Set<String>> gerarFollow(Map<String, String[]> grammarRules, Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new LinkedHashMap<>();

        for (String nt : grammarRules.keySet()) {
            follow.put(nt, new HashSet<>());
        }

        String simboloInicial = "S";
        follow.get(simboloInicial).add("$");

        boolean mudou;

        do {
            mudou = false;

            for (Map.Entry<String, String[]> regra : grammarRules.entrySet()) {
                String A = regra.getKey(); // Exemplo: Tipo_expr
                for (String producao : regra.getValue()) {
                    String[] simbolos = producao.trim().split("\\s+");

                    for (int i = 0; i < simbolos.length; i++) {
                        String B = simbolos[i];
                        if (!grammarRules.containsKey(B)) continue; // só não-terminais têm FOLLOW

                        boolean epsilonEmTudo = true;

                        for (int j = i + 1; j < simbolos.length; j++) {
                            String beta = simbolos[j];

                            Set<String> firstBeta = new HashSet<>();
                            if (grammarRules.containsKey(beta)) {
                                firstBeta.addAll(first.get(beta));
                            } else {
                                firstBeta.add(beta); // terminal direto
                            }

                            boolean adicionou = follow.get(B).addAll(
                                    firstBeta.stream().filter(s -> !s.equals("#")).toList()
                            );
                            if (adicionou) mudou = true;

                            if (!firstBeta.contains("#")) {
                                epsilonEmTudo = false;
                                break;
                            }
                        }

                        if (i == simbolos.length - 1 || epsilonEmTudo) {
                            if (follow.get(B).addAll(follow.get(A))) {
                                mudou = true;
                            }
                        }
                    }
                }
            }

        } while (mudou);

        return follow;
    }
//
//    public static String pegarNextElement(String value, String key) {
//        String[] tokens = value.trim().split("\\s+");
//        for (int i = 0; i < tokens.length; i++) {
//            if (tokens[i].equals(key)) {
//                if (i + 1 >= tokens.length) {
//                    return ""; // fim da produção
//                } else {
//                    return tokens[i + 1];
//                }
//            }
//        }
//        return "-1"; // não encontrado
//    }

    public static Map<String, Map<String, String>> gerarTabelaPreditiva(Map<String, String[]> grammarRules, Map<String, Set<String>> first, Map<String, Set<String>> follow) {

        Map<String, Map<String, String>> tabela = new LinkedHashMap<>();

        for (String naoTerminal : grammarRules.keySet()) {
            tabela.put(naoTerminal, new HashMap<>());

            for (String producao : grammarRules.get(naoTerminal)) {
                String[] simbolos = producao.trim().split("\\s+");
                Set<String> firstProducao = new HashSet<>();

                // Gerar FIRST(α) da produção
                for (String simbolo : simbolos) {
                    if (grammarRules.containsKey(simbolo)) {
                        firstProducao.addAll(first.get(simbolo));
                        if (!first.get(simbolo).contains("#"))
                            break;
                    } else {
                        firstProducao.add(simbolo);
                        break;
                    }
                }

                // Inserir na tabela para cada terminal de FIRST(α)
                for (String terminal : firstProducao) {
                    if (!terminal.equals("#")) {
                        tabela.get(naoTerminal).put(terminal, producao);
                    }
                }

                // Se FIRST(α) contém ε, usar FOLLOW(A)
                if (firstProducao.contains("#")) {
                    for (String terminal : follow.get(naoTerminal)) {
                        tabela.get(naoTerminal).put(terminal, producao);
                    }
                }
            }
        }

        return tabela;
    }

    public static boolean analisarEntrada(ArrayList<String> entrada, Map<String, Map<String, String>> tabela, String simboloInicial) {
        Stack<String> pilha = new Stack<>();
        pilha.push("$");
        pilha.push(simboloInicial);

        List<String> tokens = new ArrayList<>(entrada);
        tokens.add("$");

        int index = 0;
        while (!pilha.isEmpty()) {
            String topo = pilha.pop();
            String atual = tokens.get(index);

            if (topo.equals(atual)) {
                index++;
            } else if (!tabela.containsKey(topo)) {
                System.out.println("Erro: símbolo inesperado '" + atual + "'");
                return false;
            }else{

                String producao = tabela.get(topo).get(atual);

                if (producao == null) {
                    System.out.println("Erro: nenhuma produção para [" + topo + "][" + atual + "]");
                    return false;
                } else if (!producao.equals("#")) {
                    List<String> simbolos = new ArrayList<>(Arrays.asList(producao.trim().split("\\s+")));
                    Collections.reverse(simbolos);
                    for (String simbolo : simbolos) {
                        pilha.push(simbolo);
                    }
                }
            }
        }

        return index == tokens.size();
    }

    public static void imprimirTabelaPreditiva(Map<String, Map<String, String>> tabela) {
        System.out.println("TABELA LL(1)");
        System.out.println("-".repeat(40));
        System.out.printf("%-8s %-10s => %s\n", "NT", "Entrada", "Producao");
        System.out.println("-".repeat(40));
    
        for (String naoTerminal : tabela.keySet()) {
            Map<String, String> linha = tabela.get(naoTerminal);
            for (String terminal : linha.keySet()) {
                String producao = linha.get(terminal);
                System.out.printf("%-8s %-10s => %s\n", naoTerminal, terminal, producao);
            }
        }
    }

    public static String lerTXT(String caminho) {
        try {
            return new String(Files.readAllBytes(Paths.get(caminho)));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return "";
        }
    }

}